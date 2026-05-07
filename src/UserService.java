public class UserService {
    private AuthService authService;

    public UserService(AuthService authService) {
        this.authService = authService;
    }

    public boolean updateUserInfo(Customer user, String email, String phoneNumber, String password, String confirmPassword) {
        if (user == null || user instanceof Guest) {
            return false;
        }

        email = email.trim();
        phoneNumber = phoneNumber.trim();

        if (email.isEmpty() || phoneNumber.isEmpty()) {
            return false;
        }

        if (authService.isEmailDuplicatedByAnotherUser(email, user)) {
            return false;
        }

        if (!password.isEmpty() || !confirmPassword.isEmpty()) {
            if (password.isEmpty() || !password.equals(confirmPassword)) {
                return false;
            }
            user.updatePassword(password);
        }

        user.updateEmail(email);
        user.updatePhoneNumber(phoneNumber);
        authService.saveCustomerData();
        return true;
    }
}
