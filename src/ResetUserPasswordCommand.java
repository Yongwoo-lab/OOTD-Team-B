public class ResetUserPasswordCommand implements UserManagementCommand {
    private final UserManagementService userManagementService;
    private final Customer adminUser;
    private final String customerId;
    private final String password;
    private final String confirmPassword;

    public ResetUserPasswordCommand(UserManagementService userManagementService, Customer adminUser, String customerId,
                                    String password, String confirmPassword) {
        this.userManagementService = userManagementService;
        this.adminUser = adminUser;
        this.customerId = customerId;
        this.password = password;
        this.confirmPassword = confirmPassword;
    }

    @Override
    public boolean execute() {
        return userManagementService.resetUserPassword(adminUser, customerId, password, confirmPassword);
    }

    @Override
    public String getName() {
        return "Reset password: " + customerId;
    }
}
