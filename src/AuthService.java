import java.util.List;

public class AuthService {
    private static final String DEFAULT_ADMIN_ID = "admin";
    private static final String DEFAULT_ADMIN_PASSWORD = "admin1234";
    private List<Customer> customers;
    private CustomerDatabase customerDatabase;
    private UserFactoryProvider userFactoryProvider;
    private UserManagementService userManagementService;
    private int nextCustomerNumber;
    private int nextSkyPassNumber;

    public AuthService() {
        customerDatabase = new CustomerDatabase();
        userFactoryProvider = new UserFactoryProvider();
        customers = customerDatabase.loadCustomers();
        userManagementService = new UserManagementService(customers, customerDatabase, userFactoryProvider);
        userManagementService.ensureDefaultAdminAccount(DEFAULT_ADMIN_ID, DEFAULT_ADMIN_PASSWORD);
        updateNextCustomerNumbers();
    }

    public Customer login(String loginId, String password) {
        loginId = normalize(loginId);
        for (Customer customer : customers) {
            if ((customer.getEmail().equalsIgnoreCase(loginId)
                    || customer.getCustomerId().equalsIgnoreCase(loginId))
                    && customer.checkPassword(password)) {
                return customer;
            }
        }
        return null;
    }

    public Customer signup(String name, String email, String password, String confirmPassword, boolean isSkyPass) {
        return signup(name, email, "Not provided", password, confirmPassword, isSkyPass);
    }

    public Customer signup(String name, String email, String phoneNumber, String password, String confirmPassword, boolean isSkyPass) {
        name = normalize(name);
        email = normalize(email);
        phoneNumber = normalize(phoneNumber);
        password = normalize(password);
        confirmPassword = normalize(confirmPassword);

        if (name.isEmpty() || email.isEmpty() || phoneNumber.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            return null;
        }

        if (!password.equals(confirmPassword)) {
            return null;
        }

        if (isEmailDuplicated(email)) {
            return null;
        }

        UserType userType = UserType.fromSignup();
        String id = createMemberId(isSkyPass);
        UserFactory userFactory = userFactoryProvider.getFactory(userType);
        Customer newUser = userFactory.createUser(id, name, email, phoneNumber, password, 0, isSkyPass);

        customers.add(newUser);
        saveCustomerData();
        return newUser;
    }

    public Guest continueAsGuest() {
        return (Guest) userFactoryProvider.getFactory(UserType.GUEST).createUser();
    }

    public UserManagementService getUserManagementService() {
        return userManagementService;
    }

    public Customer findCustomerByNameAndPhone(String name, String phoneNumber) {
        name = normalize(name);
        phoneNumber = normalize(phoneNumber);

        for (Customer customer : customers) {
            if (customer.getName().equalsIgnoreCase(name)
                    && customer.getPhoneNumber().equals(phoneNumber)) {
                return customer;
            }
        }
        return null;
    }

    public Customer findCustomerByEmailAndPhone(String email, String phoneNumber) {
        email = normalize(email);
        phoneNumber = normalize(phoneNumber);

        for (Customer customer : customers) {
            if (customer.getEmail().equalsIgnoreCase(email)
                    && customer.getPhoneNumber().equals(phoneNumber)) {
                return customer;
            }
        }
        return null;
    }

    public boolean resetPassword(String email, String phoneNumber, String password, String confirmPassword) {
        password = normalize(password);
        confirmPassword = normalize(confirmPassword);
        if (password.isEmpty() || !password.equals(confirmPassword)) {
            return false;
        }

        Customer customer = findCustomerByEmailAndPhone(email, phoneNumber);
        if (customer == null) {
            return false;
        }

        customer.updatePassword(password);
        saveCustomerData();
        return true;
    }

    public boolean isEmailDuplicated(String email) {
        email = normalize(email);
        for (Customer customer : customers) {
            if (customer.getEmail().equalsIgnoreCase(email)) {
                return true;
            }
        }
        return false;
    }

    public boolean isEmailDuplicatedByAnotherUser(String email, Customer currentUser) {
        email = normalize(email);
        for (Customer customer : customers) {
            if (customer != currentUser && customer.getEmail().equalsIgnoreCase(email)) {
                return true;
            }
        }
        return false;
    }

    public void saveCustomerData() {
        customerDatabase.saveCustomers(customers);
    }

    private void updateNextCustomerNumbers() {
        nextCustomerNumber = 1;
        nextSkyPassNumber = 1;

        for (Customer customer : customers) {
            String customerId = customer.getCustomerId();
            if (customerId.length() < 2) {
                continue;
            }

            int number = parseCustomerNumber(customerId.substring(1));
            if (customerId.startsWith("C")) {
                nextCustomerNumber = Math.max(nextCustomerNumber, number + 1);
            } else if (customerId.startsWith("S")) {
                nextSkyPassNumber = Math.max(nextSkyPassNumber, number + 1);
            }
        }
    }

    private int parseCustomerNumber(String numberText) {
        try {
            return Integer.parseInt(numberText);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private String createMemberId(boolean skyPassMember) {
        if (skyPassMember) {
            return String.format("S%03d", nextSkyPassNumber++);
        }
        return String.format("%s%03d", UserType.MEMBER.getIdPrefix(), nextCustomerNumber++);
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim();
    }
}
