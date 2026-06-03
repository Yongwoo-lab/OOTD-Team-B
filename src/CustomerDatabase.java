import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class CustomerDatabase {
    private static final String DATA_DIR = "data";
    private static final String CUSTOMER_FILE = DATA_DIR + File.separator + "customers.txt";
    private final UserFactoryProvider userFactoryProvider = new UserFactoryProvider();

    public List<Customer> loadCustomers() {
        ensureDatabaseExists();

        List<Customer> customers = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(CUSTOMER_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Customer customer = parseCustomer(line);
                if (customer != null) {
                    customers.add(customer);
                }
            }
        } catch (IOException e) {
            System.out.println("Failed to load customer database: " + e.getMessage());
        }

        return customers;
    }

    public void saveCustomers(List<Customer> customers) {
        ensureDataDirectoryExists();

        try (PrintWriter writer = new PrintWriter(new FileWriter(CUSTOMER_FILE))) {
            for (Customer customer : customers) {
                if (!(customer instanceof Guest)) {
                    writer.println(formatCustomer(customer));
                }
            }
        } catch (IOException e) {
            System.out.println("Failed to save customer database: " + e.getMessage());
        }
    }

    private Customer parseCustomer(String line) {
        if (line == null || line.trim().isEmpty()) {
            return null;
        }

        String[] parts = line.split("\\|", -1);
        if (parts.length < 7) {
            return null;
        }

        String customerId = parts[0];
        UserType userType = UserType.fromDatabaseValue(parts[1]);
        String name = parts[2];
        String email = parts[3];
        String phoneNumber = parts[4];
        String password = parts[5];
        int mileage = parseMileage(parts[6]);
        MemberTier memberTier = parts.length >= 8 ? MemberTier.fromStorageValue(parts[7]) : MemberTier.BASIC;
        boolean skyPassMember = UserType.isSkyPassDatabaseValue(parts[1]);

        Customer customer = userFactoryProvider.getFactory(userType)
                .createUser(customerId, name, email, phoneNumber, password, mileage, skyPassMember);
        customer.updateMemberTier(memberTier);
        return customer;
    }

    private String formatCustomer(Customer customer) {
        String userType = getStorageUserType(customer);
        int mileage = customer instanceof SkyPassMember
                ? ((SkyPassMember) customer).getMileage()
                : 0;

        return String.join("|",
                customer.getCustomerId(),
                userType,
                customer.getName(),
                customer.getEmail(),
                customer.getPhoneNumber(),
                customer.getPassword(),
                String.valueOf(mileage),
                customer.getMemberTier().getStorageName()
        );
    }

    private int parseMileage(String mileageText) {
        try {
            return Integer.parseInt(mileageText.trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private void ensureDatabaseExists() {
        ensureDataDirectoryExists();

        File file = new File(CUSTOMER_FILE);
        if (file.exists()) {
            return;
        }

        List<Customer> defaultCustomers = new ArrayList<>();
        defaultCustomers.add(userFactoryProvider.getFactory(UserType.ADMIN)
                .createUser("admin", "Administrator", "admin", "Not provided", "admin1234"));
        defaultCustomers.add(userFactoryProvider.getFactory(UserType.MEMBER)
                .createUser("C001", "Kim", "kim@test.com", "010-1111-2222", "1234"));
        defaultCustomers.add(userFactoryProvider.getFactory(UserType.MEMBER)
                .createUser("S001", "Lee", "lee@test.com", "010-3333-4444", "1234", 5000, true));
        saveCustomers(defaultCustomers);
    }

    private void ensureDataDirectoryExists() {
        File dataDirectory = new File(DATA_DIR);
        if (!dataDirectory.exists()) {
            dataDirectory.mkdirs();
        }
    }

    private String getStorageUserType(Customer customer) {
        if (customer instanceof Admin) {
            return UserType.ADMIN.getStorageName();
        }
        if (customer instanceof SkyPassMember) {
            return "SkyPass";
        }
        return UserType.MEMBER.getStorageName();
    }
}
