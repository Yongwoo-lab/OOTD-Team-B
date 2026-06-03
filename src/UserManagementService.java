import java.util.ArrayList;
import java.util.List;

public class UserManagementService {
    private final List<Customer> customers;
    private final CustomerDatabase customerDatabase;
    private final UserFactoryProvider userFactoryProvider;

    public UserManagementService(List<Customer> customers, CustomerDatabase customerDatabase,
                                 UserFactoryProvider userFactoryProvider) {
        this.customers = customers;
        this.customerDatabase = customerDatabase;
        this.userFactoryProvider = userFactoryProvider;
    }

    public List<Customer> getAllUsers(Customer adminUser) {
        if (!(adminUser instanceof Admin)) {
            return new ArrayList<>();
        }
        return new ArrayList<>(customers);
    }

    public boolean resetUserPassword(Customer adminUser, String customerId, String password, String confirmPassword) {
        if (!(adminUser instanceof Admin) || password == null || confirmPassword == null
                || password.isEmpty() || !password.equals(confirmPassword)) {
            return false;
        }

        Customer customer = findUserById(customerId);
        if (customer == null) {
            return false;
        }

        customer.updatePassword(password);
        saveUsers();
        return true;
    }

    public boolean deleteUser(Customer adminUser, String customerId) {
        if (!(adminUser instanceof Admin)) {
            return false;
        }

        Customer customer = findUserById(customerId);
        if (customer == null || customer instanceof Admin) {
            return false;
        }

        boolean removed = customers.remove(customer);
        if (removed) {
            saveUsers();
        }
        return removed;
    }

    public boolean changeUserTier(Customer adminUser, String customerId, MemberTier memberTier) {
        if (!(adminUser instanceof Admin) || memberTier == null) {
            return false;
        }

        Customer customer = findUserById(customerId);
        if (customer == null || customer instanceof Admin || customer instanceof Guest) {
            return false;
        }

        customer.updateMemberTier(memberTier);
        saveUsers();
        return true;
    }

    public void ensureDefaultAdminAccount(String adminId, String password) {
        if (findUserById(adminId) != null || isLoginDuplicated(adminId)) {
            return;
        }

        Customer admin = userFactoryProvider.getFactory(UserType.ADMIN)
                .createUser(adminId, "Administrator", adminId, "Not provided", password);
        customers.add(admin);
        saveUsers();
    }

    private Customer findUserById(String customerId) {
        if (customerId == null || customerId.trim().isEmpty()) {
            return null;
        }

        for (Customer customer : customers) {
            if (customer.getCustomerId().equalsIgnoreCase(customerId.trim())) {
                return customer;
            }
        }
        return null;
    }

    private boolean isLoginDuplicated(String loginId) {
        if (loginId == null) {
            return false;
        }

        for (Customer customer : customers) {
            if (customer.getEmail().equalsIgnoreCase(loginId.trim())) {
                return true;
            }
        }
        return false;
    }

    private void saveUsers() {
        customerDatabase.saveCustomers(customers);
    }
}
