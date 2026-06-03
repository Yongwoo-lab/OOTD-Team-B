public class MemberFactory extends UserFactory {
    @Override
    protected Customer createCustomer(String customerId, String name, String email, String phoneNumber,
                                      String password, int mileage, boolean skyPassMember) {
        if (skyPassMember) {
            return new SkyPassMember(customerId, name, email, phoneNumber, password, mileage);
        }
        return new Customer(customerId, name, email, phoneNumber, password);
    }
}
