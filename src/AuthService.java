import java.util.List;

public class AuthService {
    private List<Customer> customers;
    private CustomerDatabase customerDatabase;
    private int nextCustomerNumber;
    private int nextSkyPassNumber;

    public AuthService() {
        customerDatabase = new CustomerDatabase();
        customers = customerDatabase.loadCustomers();
        updateNextCustomerNumbers();
    }

    public Customer login(String email, String password) {
        for (Customer customer : customers) {
            if (customer.getEmail().equalsIgnoreCase(email)
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
        name = name.trim();
        email = email.trim();
        phoneNumber = phoneNumber.trim();

        if (name.isEmpty() || email.isEmpty() || phoneNumber.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            return null;
        }

        if (!password.equals(confirmPassword)) {
            return null;
        }

        if (isEmailDuplicated(email)) {
            return null;
        }

        Customer newUser;

        if (isSkyPass) {
            String id = String.format("S%03d", nextSkyPassNumber++);
            newUser = new SkyPassMember(id, name, email, phoneNumber, password, 0);
        } else {
            String id = String.format("C%03d", nextCustomerNumber++);
            newUser = new Customer(id, name, email, phoneNumber, password);
        }

        customers.add(newUser);
        saveCustomerData();
        return newUser;
    }

    public Guest continueAsGuest() {
        return new Guest();
    }

    public Customer findCustomerByNameAndPhone(String name, String phoneNumber) {
        name = name.trim();
        phoneNumber = phoneNumber.trim();

        for (Customer customer : customers) {
            if (customer.getName().equalsIgnoreCase(name)
                    && customer.getPhoneNumber().equals(phoneNumber)) {
                return customer;
            }
        }
        return null;
    }

    public Customer findCustomerByEmailAndPhone(String email, String phoneNumber) {
        email = email.trim();
        phoneNumber = phoneNumber.trim();

        for (Customer customer : customers) {
            if (customer.getEmail().equalsIgnoreCase(email)
                    && customer.getPhoneNumber().equals(phoneNumber)) {
                return customer;
            }
        }
        return null;
    }

    public boolean isEmailDuplicated(String email) {
        for (Customer customer : customers) {
            if (customer.getEmail().equalsIgnoreCase(email.trim())) {
                return true;
            }
        }
        return false;
    }

    public boolean isEmailDuplicatedByAnotherUser(String email, Customer currentUser) {
        for (Customer customer : customers) {
            if (customer != currentUser && customer.getEmail().equalsIgnoreCase(email.trim())) {
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
}
