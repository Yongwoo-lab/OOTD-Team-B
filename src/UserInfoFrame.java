import javax.swing.*;
import java.awt.*;

public class UserInfoFrame extends JFrame {
    private AuthService authService;
    private UserService userService;
    private MileageService mileageService;
    private Customer currentUser;

    private JTextField emailField;
    private JTextField phoneField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JLabel mileageValueLabel;

    public UserInfoFrame(AuthService authService, Customer currentUser) {
        this.authService = authService;
        this.userService = new UserService(authService);
        this.mileageService = new MileageService();
        this.currentUser = currentUser;

        setTitle("My Page");
        setSize(620, 520);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = AppTheme.createPagePanel();

        JPanel headerPanel = new JPanel(new GridLayout(2, 1, 4, 4));
        headerPanel.setOpaque(false);
        headerPanel.add(AppTheme.createTitle("My Page"));
        headerPanel.add(AppTheme.createSubtitle("View your account, update contact details, and check mileage."));

        JPanel contentPanel = new JPanel(new GridLayout(1, 2, 16, 16));
        contentPanel.setOpaque(false);
        contentPanel.add(createProfilePanel());
        contentPanel.add(createEditPanel());

        JPanel buttonPanel = new JPanel(new GridLayout(1, 4, 10, 10));
        buttonPanel.setOpaque(false);

        JButton logoutButton = AppTheme.createSecondaryButton("Logout");
        JButton saveButton = AppTheme.createPrimaryButton("Save Changes");
        JButton historyButton = AppTheme.createSecondaryButton("Reservation History");
        JButton proceedButton = AppTheme.createPrimaryButton("Search Flights");
        saveButton.setEnabled(!(currentUser instanceof Guest));
        historyButton.setEnabled(!(currentUser instanceof Guest));

        buttonPanel.add(logoutButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(historyButton);
        buttonPanel.add(proceedButton);

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(contentPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        add(panel);

        logoutButton.addActionListener(e -> {
            new MainFrame(authService);
            dispose();
        });

        saveButton.addActionListener(e -> saveUserInfo());

        historyButton.addActionListener(e -> {
            new ReservationHistoryFrame(authService, currentUser);
            dispose();
        });

        proceedButton.addActionListener(e -> {
            new SearchFlightFrame(authService, currentUser);
            dispose();
        });

        setVisible(true);
    }

    private JPanel createProfilePanel() {
        JPanel profilePanel = AppTheme.createCardPanel();
        profilePanel.setLayout(new GridLayout(7, 1, 8, 8));

        JLabel sectionTitle = new JLabel("Account Summary");
        sectionTitle.setForeground(AppTheme.NAVY);
        sectionTitle.setFont(new Font("Arial", Font.BOLD, 16));

        mileageValueLabel = new JLabel("Mileage: " + mileageService.getMileageMessage(currentUser));
        mileageValueLabel.setForeground(AppTheme.BLUE);
        mileageValueLabel.setFont(new Font("Arial", Font.BOLD, 18));

        profilePanel.add(sectionTitle);
        profilePanel.add(createInfoLabel("ID", currentUser.getCustomerId()));
        profilePanel.add(createInfoLabel("Name", currentUser.getName()));
        profilePanel.add(createInfoLabel("Email", currentUser.getEmail()));
        profilePanel.add(createInfoLabel("Phone", currentUser.getPhoneNumber()));
        profilePanel.add(createInfoLabel("User Type", currentUser.getUserType()));
        profilePanel.add(mileageValueLabel);

        return profilePanel;
    }

    private JPanel createEditPanel() {
        JPanel editPanel = AppTheme.createCardPanel();
        editPanel.setLayout(new GridLayout(9, 1, 8, 8));

        JLabel sectionTitle = new JLabel("Edit Information");
        sectionTitle.setForeground(AppTheme.NAVY);
        sectionTitle.setFont(new Font("Arial", Font.BOLD, 16));

        emailField = new JTextField(currentUser.getEmail());
        phoneField = new JTextField(currentUser.getPhoneNumber());
        passwordField = new JPasswordField();
        confirmPasswordField = new JPasswordField();

        AppTheme.styleField(emailField);
        AppTheme.styleField(phoneField);
        AppTheme.styleField(passwordField);
        AppTheme.styleField(confirmPasswordField);

        editPanel.add(sectionTitle);
        editPanel.add(new JLabel("Email"));
        editPanel.add(emailField);
        editPanel.add(new JLabel("Phone"));
        editPanel.add(phoneField);
        editPanel.add(new JLabel("New Password"));
        editPanel.add(passwordField);
        editPanel.add(new JLabel("Confirm New Password"));
        editPanel.add(confirmPasswordField);

        boolean isEditableUser = !(currentUser instanceof Guest);
        emailField.setEnabled(isEditableUser);
        phoneField.setEnabled(isEditableUser);
        passwordField.setEnabled(isEditableUser);
        confirmPasswordField.setEnabled(isEditableUser);

        return editPanel;
    }

    private JLabel createInfoLabel(String label, String value) {
        JLabel infoLabel = new JLabel(label + ": " + value);
        infoLabel.setForeground(AppTheme.TEXT);
        infoLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        return infoLabel;
    }

    private void saveUserInfo() {
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());

        boolean updated = userService.updateUserInfo(
                currentUser,
                emailField.getText(),
                phoneField.getText(),
                password,
                confirmPassword
        );

        if (!updated) {
            JOptionPane.showMessageDialog(this, "Update failed. Check required fields, duplicated email, or password confirmation.");
            return;
        }

        JOptionPane.showMessageDialog(this, "Your information has been updated.");
        new UserInfoFrame(authService, currentUser);
        dispose();
    }
}
