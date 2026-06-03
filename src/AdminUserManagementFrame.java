import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class AdminUserManagementFrame extends JFrame {
    private final AuthService authService;
    private final UserManagementService userManagementService;
    private final Customer adminUser;
    private final AdminCommandInvoker commandInvoker;
    private final DefaultListModel<String> listModel;
    private final JList<String> userList;
    private List<Customer> users;

    public AdminUserManagementFrame(AuthService authService, Customer adminUser) {
        this.authService = authService;
        this.userManagementService = authService.getUserManagementService();
        this.adminUser = adminUser;
        this.commandInvoker = new AdminCommandInvoker();
        this.listModel = new DefaultListModel<>();
        this.userList = new JList<>(listModel);
        this.users = new ArrayList<>();

        setTitle("Admin User Management");
        setSize(980, 520);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = AppTheme.createPagePanel();

        JPanel headerPanel = new JPanel(new GridLayout(2, 1, 4, 4));
        headerPanel.setOpaque(false);
        headerPanel.add(AppTheme.createTitle("Admin User Management"));
        headerPanel.add(AppTheme.createSubtitle("View users, reset passwords, manage tiers, and remove customer accounts."));

        userList.setFont(new Font("Monospaced", Font.PLAIN, 12));
        userList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(userList);
        scrollPane.setBorder(BorderFactory.createLineBorder(AppTheme.BORDER));

        JPanel buttonPanel = new JPanel(new GridLayout(1, 5, 10, 10));
        buttonPanel.setOpaque(false);
        JButton logoutButton = AppTheme.createSecondaryButton("Logout");
        JButton refreshButton = AppTheme.createSecondaryButton("Refresh");
        JButton changeTierButton = AppTheme.createPrimaryButton("Change Tier");
        JButton resetPasswordButton = AppTheme.createPrimaryButton("Reset Password");
        JButton deleteButton = AppTheme.createSecondaryButton("Delete User");

        buttonPanel.add(logoutButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(changeTierButton);
        buttonPanel.add(resetPasswordButton);
        buttonPanel.add(deleteButton);

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        add(panel);

        logoutButton.addActionListener(e -> {
            new MainFrame(authService);
            dispose();
        });

        refreshButton.addActionListener(e -> refreshUsers());
        changeTierButton.addActionListener(e -> changeSelectedUserTier());
        resetPasswordButton.addActionListener(e -> resetSelectedUserPassword());
        deleteButton.addActionListener(e -> deleteSelectedUser());

        refreshUsers();
        setVisible(true);
    }

    private void refreshUsers() {
        users = userManagementService.getAllUsers(adminUser);
        listModel.clear();

        if (users.isEmpty()) {
            listModel.addElement("No users found or admin permission is missing.");
            userList.setEnabled(false);
            return;
        }

        userList.setEnabled(true);
        listModel.addElement(String.format("%-10s | %-16s | %-18s | %-24s | %-15s | %-10s | %s",
                "ID", "Type", "Name", "Login", "Phone", "Tier", "Mileage"));
        listModel.addElement("-----------+------------------+--------------------+--------------------------+-----------------+------------+---------");
        for (Customer user : users) {
            listModel.addElement(formatUser(user));
        }
    }

    private void changeSelectedUserTier() {
        Customer selectedUser = getSelectedUser();
        if (selectedUser == null) {
            JOptionPane.showMessageDialog(this, "Please select a user.");
            return;
        }
        if (selectedUser instanceof Admin) {
            JOptionPane.showMessageDialog(this, "Admin accounts do not use member tiers.");
            return;
        }

        JComboBox<MemberTier> tierBox = new JComboBox<>(MemberTier.values());
        tierBox.setSelectedItem(selectedUser.getMemberTier());
        AppTheme.styleComboBox(tierBox);

        JPanel inputPanel = new JPanel(new GridLayout(2, 1, 8, 8));
        inputPanel.add(new JLabel("Select member tier for " + selectedUser.getCustomerId()));
        inputPanel.add(tierBox);

        int result = JOptionPane.showConfirmDialog(this, inputPanel, "Change Member Tier", JOptionPane.OK_CANCEL_OPTION);
        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        MemberTier selectedTier = (MemberTier) tierBox.getSelectedItem();
        UserManagementCommand command = new ChangeUserTierCommand(
                userManagementService,
                adminUser,
                selectedUser.getCustomerId(),
                selectedTier
        );
        boolean changed = commandInvoker.execute(command);
        if (!changed) {
            JOptionPane.showMessageDialog(this, "Tier change failed. Check admin permission and selected user.");
            return;
        }

        JOptionPane.showMessageDialog(this, "Member tier has been changed.");
        refreshUsers();
    }

    private void resetSelectedUserPassword() {
        Customer selectedUser = getSelectedUser();
        if (selectedUser == null) {
            JOptionPane.showMessageDialog(this, "Please select a user.");
            return;
        }

        JPasswordField passwordField = new JPasswordField();
        JPasswordField confirmPasswordField = new JPasswordField();
        AppTheme.styleField(passwordField);
        AppTheme.styleField(confirmPasswordField);

        JPanel inputPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        inputPanel.add(new JLabel("New Password"));
        inputPanel.add(passwordField);
        inputPanel.add(new JLabel("Confirm Password"));
        inputPanel.add(confirmPasswordField);

        int result = JOptionPane.showConfirmDialog(this, inputPanel, "Reset Password", JOptionPane.OK_CANCEL_OPTION);
        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        UserManagementCommand command = new ResetUserPasswordCommand(
                userManagementService,
                adminUser,
                selectedUser.getCustomerId(),
                new String(passwordField.getPassword()),
                new String(confirmPasswordField.getPassword())
        );
        boolean reset = commandInvoker.execute(command);

        if (!reset) {
            JOptionPane.showMessageDialog(this, "Password reset failed. Check password confirmation or admin permission.");
            return;
        }

        JOptionPane.showMessageDialog(this, "Password has been reset.");
        refreshUsers();
    }

    private void deleteSelectedUser() {
        Customer selectedUser = getSelectedUser();
        if (selectedUser == null) {
            JOptionPane.showMessageDialog(this, "Please select a user.");
            return;
        }

        int result = JOptionPane.showConfirmDialog(
                this,
                "Delete user " + selectedUser.getCustomerId() + "?\nThis cannot delete admin accounts.",
                "Delete User",
                JOptionPane.YES_NO_OPTION
        );
        if (result != JOptionPane.YES_OPTION) {
            return;
        }

        UserManagementCommand command = new DeleteUserCommand(userManagementService, adminUser, selectedUser.getCustomerId());
        boolean deleted = commandInvoker.execute(command);
        if (!deleted) {
            JOptionPane.showMessageDialog(this, "Delete failed. Admin accounts cannot be deleted.");
            return;
        }

        JOptionPane.showMessageDialog(this, "User has been deleted.");
        refreshUsers();
    }

    private Customer getSelectedUser() {
        int selectedIndex = userList.getSelectedIndex();
        int userIndex = selectedIndex - 2;
        if (selectedIndex < 2 || userIndex < 0 || userIndex >= users.size()) {
            return null;
        }
        return users.get(userIndex);
    }

    private String formatUser(Customer user) {
        String mileage = user instanceof SkyPassMember
                ? String.format("%,d", ((SkyPassMember) user).getMileage())
                : "-";
        return String.format("%-10s | %-16s | %-18s | %-24s | %-15s | %-10s | %s",
                user.getCustomerId(),
                user.getUserType(),
                user.getName(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getMemberTier().getDisplayName(),
                mileage);
    }
}
