public abstract class UserFactory {
    public final Customer createUser() {
        return createUser("", "", "", "", "", 0, false);
    }

    public final Customer createUser(String customerId, String name, String email, String phoneNumber, String password) {
        return createUser(customerId, name, email, phoneNumber, password, 0, false);
    }

    public final Customer createUser(String customerId, String name, String email, String phoneNumber,
                                     String password, int mileage) {
        return createUser(customerId, name, email, phoneNumber, password, mileage, false);
    }

    public final Customer createUser(String customerId, String name, String email, String phoneNumber,
                                     String password, int mileage, boolean skyPassMember) {
        return createCustomer(customerId, name, email, phoneNumber, password, mileage, skyPassMember);
    }

    protected abstract Customer createCustomer(String customerId, String name, String email, String phoneNumber,
                                               String password, int mileage, boolean skyPassMember);
}
