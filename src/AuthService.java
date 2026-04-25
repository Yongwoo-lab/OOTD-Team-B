import java.util.ArrayList;
import java.util.List;

public class AuthService {
    private List<Customer> customers;
    private int nextCustomerNumber;
    private int nextSkyPassNumber;

    public AuthService() {
        customers = new ArrayList<>();

        customers.add(new Customer("C001", "Kim", "kim@test.com", "1234"));
        customers.add(new SkyPassMember("S001", "Lee", "lee@test.com", "1234", 5000));

        nextCustomerNumber = 2;
        nextSkyPassNumber = 2;
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
        name = name.trim();
        email = email.trim();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
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
            newUser = new SkyPassMember(id, name, email, password, 0);
        } else {
            String id = String.format("C%03d", nextCustomerNumber++);
            newUser = new Customer(id, name, email, password);
        }

        customers.add(newUser);
        return newUser;
    }

    public Guest continueAsGuest() {
        return new Guest();
    }

    public boolean isEmailDuplicated(String email) {
        for (Customer customer : customers) {
            if (customer.getEmail().equalsIgnoreCase(email.trim())) {
                return true;
            }
        }
        return false;
    }
}