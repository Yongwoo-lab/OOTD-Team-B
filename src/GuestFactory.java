public class GuestFactory extends UserFactory {
    @Override
    protected Customer createCustomer(String customerId, String name, String email, String phoneNumber,
                                      String password, int mileage, boolean skyPassMember) {
        return new Guest();
    }
}
