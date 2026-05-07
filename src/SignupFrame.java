import javax.swing.*;
import java.awt.*;

public class SignupFrame extends JFrame {
    private AuthService authService;

    public SignupFrame(AuthService authService) {
        this.authService = authService;

        setTitle("Sign Up");
        setSize(500, 430);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = AppTheme.createPagePanel();

        JPanel headerPanel = new JPanel(new GridLayout(2, 1, 4, 4));
        headerPanel.setOpaque(false);
        headerPanel.add(AppTheme.createTitle("Sign Up"));
        headerPanel.add(AppTheme.createSubtitle("Create a customer account or join as a SkyPass member."));

        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        formPanel.setOpaque(false);

        JTextField nameField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField phoneField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JPasswordField confirmPasswordField = new JPasswordField();
        JCheckBox skyPassCheckBox = new JCheckBox("Join as SkyPass Member");
        skyPassCheckBox.setOpaque(false);

        AppTheme.styleField(nameField);
        AppTheme.styleField(emailField);
        AppTheme.styleField(phoneField);
        AppTheme.styleField(passwordField);
        AppTheme.styleField(confirmPasswordField);

        formPanel.add(new JLabel("Name:"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("Email:"));
        formPanel.add(emailField);
        formPanel.add(new JLabel("Phone:"));
        formPanel.add(phoneField);
        formPanel.add(new JLabel("Password:"));
        formPanel.add(passwordField);
        formPanel.add(new JLabel("Confirm Password:"));
        formPanel.add(confirmPasswordField);
        formPanel.add(new JLabel("Membership Type:"));
        formPanel.add(skyPassCheckBox);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        buttonPanel.setOpaque(false);
        JButton backButton = AppTheme.createSecondaryButton("Back");
        JButton signupButton = AppTheme.createPrimaryButton("Sign Up");

        buttonPanel.add(backButton);
        buttonPanel.add(signupButton);

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(formPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        add(panel);

        backButton.addActionListener(e -> {
            new MainFrame(authService);
            dispose();
        });

        signupButton.addActionListener(e -> {
            String name = nameField.getText();
            String email = emailField.getText();
            String phoneNumber = phoneField.getText();
            String password = new String(passwordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());
            boolean isSkyPass = skyPassCheckBox.isSelected();

            if (authService.isEmailDuplicated(email)) {
                JOptionPane.showMessageDialog(this, "This email is already registered.");
                return;
            }

            Customer user = authService.signup(name, email, phoneNumber, password, confirmPassword, isSkyPass);

            if (user == null) {
                JOptionPane.showMessageDialog(this, "Signup failed. Please check your input.");
            } else {
                JOptionPane.showMessageDialog(this, "Signup successful.");
                new SearchFlightFrame(authService, user);
                dispose();
            }
        });

        setVisible(true);
    }
}
