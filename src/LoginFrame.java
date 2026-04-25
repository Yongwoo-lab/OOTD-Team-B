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
        setSize(420, 260);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(25, 35, 25, 35));

        JLabel titleLabel = new JLabel("Login", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));

        JPanel formPanel = new JPanel(new GridLayout(2, 2, 10, 10));

        JTextField emailField = new JTextField();
        JPasswordField passwordField = new JPasswordField();

        formPanel.add(new JLabel("Email:"));
        formPanel.add(emailField);
        formPanel.add(new JLabel("Password:"));
        formPanel.add(passwordField);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        JButton backButton = new JButton("Back");
        JButton loginButton = new JButton("Login");

        buttonPanel.add(backButton);
        buttonPanel.add(loginButton);

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(formPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        add(panel);

        backButton.addActionListener(e -> {
            new SearchFlightFrame(authService, null);
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