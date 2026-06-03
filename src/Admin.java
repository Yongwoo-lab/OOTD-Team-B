public class Admin extends Customer {
    public Admin(String adminId, String name, String email, String phoneNumber, String password) {
        super(adminId, name, email, phoneNumber, password);
    }

    @Override
    public String getUserType() {
        return "Admin";
    }
}
