import javax.swing.*;
import java.awt.*;

public class SignupFrame extends JFrame {
    private AuthService authService;

    public SignupFrame(AuthService authService) {
        this.authService = authService;

        setTitle("Sign Up");
        setSize(450, 360);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(25, 35, 25, 35));

        JLabel titleLabel = new JLabel("Sign Up", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));

        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));

        JTextField nameField = new JTextField();
        JTextField emailField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JPasswordField confirmPasswordField = new JPasswordField();
        JCheckBox skyPassCheckBox = new JCheckBox("Join as SkyPass Member");

        formPanel.add(new JLabel("Name:"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("Email:"));
        formPanel.add(emailField);
        formPanel.add(new JLabel("Password:"));
        formPanel.add(passwordField);
        formPanel.add(new JLabel("Confirm Password:"));
        formPanel.add(confirmPasswordField);
        formPanel.add(new JLabel("Membership Type:"));
        formPanel.add(skyPassCheckBox);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        JButton backButton = new JButton("Back");
        JButton signupButton = new JButton("Sign Up");

        buttonPanel.add(backButton);
        buttonPanel.add(signupButton);

        panel.add(titleLabel, BorderLayout.NORTH);
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
            String password = new String(passwordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());
            boolean isSkyPass = skyPassCheckBox.isSelected();

            if (authService.isEmailDuplicated(email)) {
                JOptionPane.showMessageDialog(this, "This email is already registered.");
                return;
            }

            Customer user = authService.signup(name, email, password, confirmPassword, isSkyPass);

            if (user == null) {
                JOptionPane.showMessageDialog(this, "Signup failed. Please check your input.");
            } else {
                JOptionPane.showMessageDialog(this, "Signup successful.");
                new UserInfoFrame(authService, user);
                dispose();
            }
        });

        setVisible(true);
    }
}