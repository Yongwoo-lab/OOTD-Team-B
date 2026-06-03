public class UserFactoryProvider {
    private final UserFactory adminFactory = new AdminFactory();
    private final UserFactory memberFactory = new MemberFactory();
    private final UserFactory guestFactory = new GuestFactory();

    public UserFactory getFactory(UserType userType) {
        if (userType == UserType.ADMIN) {
            return adminFactory;
        }
        if (userType == UserType.GUEST) {
            return guestFactory;
        }
        return memberFactory;
    }
}
