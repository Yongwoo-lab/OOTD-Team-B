public class ChangeUserTierCommand implements UserManagementCommand {
    private final UserManagementService userManagementService;
    private final Customer adminUser;
    private final String customerId;
    private final MemberTier memberTier;

    public ChangeUserTierCommand(UserManagementService userManagementService, Customer adminUser,
                                 String customerId, MemberTier memberTier) {
        this.userManagementService = userManagementService;
        this.adminUser = adminUser;
        this.customerId = customerId;
        this.memberTier = memberTier;
    }

    @Override
    public boolean execute() {
        return userManagementService.changeUserTier(adminUser, customerId, memberTier);
    }

    @Override
    public String getName() {
        return "Change user tier: " + customerId + " -> " + memberTier;
    }
}
