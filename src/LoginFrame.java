import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public class LoginFrame extends JFrame {
    private AuthService authService;
    private Consumer<Customer> onLoginSuccess;

    public LoginFrame(AuthService authService, Consumer<Customer> onLoginSuccess) {
        this.authService = authService;
        this.onLoginSuccess = onLoginSuccess;

        setTitle("Login");
        setSize(460, 310);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = AppTheme.createPagePanel();

        JPanel headerPanel = new JPanel(new GridLayout(2, 1, 4, 4));
        headerPanel.setOpaque(false);
        headerPanel.add(AppTheme.createTitle("Login"));
        headerPanel.add(AppTheme.createSubtitle("Use your email and password to continue."));

        JPanel formPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        formPanel.setOpaque(false);

        JTextField emailField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        AppTheme.styleField(emailField);
        AppTheme.styleField(passwordField);

        formPanel.add(new JLabel("Email:"));
        formPanel.add(emailField);
        formPanel.add(new JLabel("Password:"));
        formPanel.add(passwordField);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        buttonPanel.setOpaque(false);
        JButton backButton = AppTheme.createSecondaryButton("Back");
        JButton loginButton = AppTheme.createPrimaryButton("Login");

        buttonPanel.add(backButton);
        buttonPanel.add(loginButton);

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(formPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        add(panel);

        backButton.addActionListener(e -> {
            new MainFrame(authService);
            dispose();
        });

        loginButton.addActionListener(e -> {
            String email = emailField.getText();
            String password = new String(passwordField.getPassword());

            Customer user = authService.login(email, password);

            if (user == null) {
                JOptionPane.showMessageDialog(this, "Login failed. Check your email or password.");
            } else {
                JOptionPane.showMessageDialog(this, "Login successful.");
                onLoginSuccess.accept(user);
                dispose();
            }
        });

        setVisible(true);
    }
}
