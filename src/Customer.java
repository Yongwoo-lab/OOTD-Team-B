public class Customer {
    protected String customerId;
    protected String name;
    protected String email;
    protected String phoneNumber;
    protected String password;

    public Customer(String customerId, String name, String email, String password) {
        this(customerId, name, email, "", password);
    }

    public Customer(String customerId, String name, String email, String phoneNumber, String password) {
        this.customerId = customerId;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.password = password;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getPassword() {
        return password;
    }

    public boolean checkPassword(String password) {
        return this.password.equals(password);
    }

    public void updateEmail(String email) {
        this.email = email;
    }

    public void updatePhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void updatePassword(String password) {
        this.password = password;
    }

    public String getUserType() {
        return "Customer";
    }
}
