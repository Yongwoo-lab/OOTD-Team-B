public class DeleteUserCommand implements UserManagementCommand {
    private final UserManagementService userManagementService;
    private final Customer adminUser;
    private final String customerId;

    public DeleteUserCommand(UserManagementService userManagementService, Customer adminUser, String customerId) {
        this.userManagementService = userManagementService;
        this.adminUser = adminUser;
        this.customerId = customerId;
    }

    @Override
    public boolean execute() {
        return userManagementService.deleteUser(adminUser, customerId);
    }

    @Override
    public String getName() {
        return "Delete user: " + customerId;
    }
}
