import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private AuthService authService;

    public MainFrame(AuthService authService) {
        this.authService = authService;

        setTitle("Korean Air Reservation System");
        setSize(560, 520);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel pagePanel = AppTheme.createPagePanel();

        JPanel loginCard = AppTheme.createCardPanel();
        loginCard.setLayout(new BorderLayout(18, 18));

        JPanel accentBar = new JPanel(new GridLayout(1, 2));
        accentBar.setPreferredSize(new Dimension(10, 6));
        JPanel blueBar = new JPanel();
        JPanel redBar = new JPanel();
        blueBar.setBackground(AppTheme.BLUE);
        redBar.setBackground(AppTheme.RED);
        accentBar.add(blueBar);
        accentBar.add(redBar);

        JPanel headerPanel = new JPanel(new GridLayout(3, 1, 4, 4));
        headerPanel.setOpaque(false);

        JLabel brandLabel = AppTheme.createTitle("Korean Air");
        JLabel titleLabel = AppTheme.createTitle("Flight Booking Login");
        JLabel subtitleLabel = AppTheme.createSubtitle("Sign in to book flights, manage your profile, and check mileage.");

        headerPanel.add(brandLabel);
        headerPanel.add(titleLabel);
        headerPanel.add(subtitleLabel);

        JPanel formPanel = new JPanel(new GridLayout(2, 2, 12, 12));
        formPanel.setOpaque(false);

        JTextField emailField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        AppTheme.styleField(emailField);
        AppTheme.styleField(passwordField);

        formPanel.add(createFormLabel("Email"));
        formPanel.add(emailField);
        formPanel.add(createFormLabel("Password"));
        formPanel.add(passwordField);

        JPanel actionPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        actionPanel.setOpaque(false);

        JButton loginButton = AppTheme.createPrimaryButton("Login");
        JButton guestButton = AppTheme.createSecondaryButton("Continue as Guest");

        actionPanel.add(loginButton);
        actionPanel.add(guestButton);

        JPanel linkPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        linkPanel.setOpaque(false);

        JButton signupButton = AppTheme.createTextButton("Sign Up");
        JButton findEmailButton = AppTheme.createTextButton("Find ID");
        JButton findPasswordButton = AppTheme.createTextButton("Find Password");

        linkPanel.add(signupButton);
        linkPanel.add(new JLabel("|"));
        linkPanel.add(findEmailButton);
        linkPanel.add(new JLabel("|"));
        linkPanel.add(findPasswordButton);

        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        bottomPanel.setOpaque(false);
        bottomPanel.add(actionPanel, BorderLayout.CENTER);
        bottomPanel.add(linkPanel, BorderLayout.SOUTH);

        JPanel bodyPanel = new JPanel(new BorderLayout(18, 18));
        bodyPanel.setOpaque(false);
        bodyPanel.add(headerPanel, BorderLayout.NORTH);
        bodyPanel.add(formPanel, BorderLayout.CENTER);

        loginCard.add(accentBar, BorderLayout.NORTH);
        loginCard.add(bodyPanel, BorderLayout.CENTER);
        loginCard.add(bottomPanel, BorderLayout.SOUTH);

        pagePanel.add(loginCard, BorderLayout.CENTER);
        add(pagePanel);

        loginButton.addActionListener(e -> login(emailField.getText(), new String(passwordField.getPassword())));

        passwordField.addActionListener(e -> login(emailField.getText(), new String(passwordField.getPassword())));

        signupButton.addActionListener(e -> {
            new SignupFrame(authService);
            dispose();
        });

        findEmailButton.addActionListener(e -> showFindEmailDialog());
        findPasswordButton.addActionListener(e -> showFindPasswordDialog());

        guestButton.addActionListener(e -> {
            Customer guest = authService.continueAsGuest();
            new SearchFlightFrame(authService, guest);
            dispose();
        });

        setVisible(true);
    }

    private JLabel createFormLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(AppTheme.NAVY);
        label.setFont(new Font("Arial", Font.BOLD, 13));
        return label;
    }

    private void login(String email, String password) {
        Customer user = authService.login(email, password);

        if (user == null) {
            JOptionPane.showMessageDialog(this, "Login failed. Check your email or password.");
            return;
        }

        JOptionPane.showMessageDialog(this, "Login successful.");
        new SearchFlightFrame(authService, user);
        dispose();
    }

    private void showFindEmailDialog() {
        JTextField nameField = new JTextField();
        JTextField phoneField = new JTextField();
        AppTheme.styleField(nameField);
        AppTheme.styleField(phoneField);

        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));
        panel.add(new JLabel("Name"));
        panel.add(nameField);
        panel.add(new JLabel("Phone"));
        panel.add(phoneField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Find ID", JOptionPane.OK_CANCEL_OPTION);
        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        Customer customer = authService.findCustomerByNameAndPhone(nameField.getText(), phoneField.getText());
        if (customer == null) {
            JOptionPane.showMessageDialog(this, "No matching account found.");
            return;
        }

        JOptionPane.showMessageDialog(this, "Your login ID is:\n" + customer.getEmail());
    }

    private void showFindPasswordDialog() {
        JTextField emailField = new JTextField();
        JTextField phoneField = new JTextField();
        AppTheme.styleField(emailField);
        AppTheme.styleField(phoneField);

        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));
        panel.add(new JLabel("Email"));
        panel.add(emailField);
        panel.add(new JLabel("Phone"));
        panel.add(phoneField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Find Password", JOptionPane.OK_CANCEL_OPTION);
        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        Customer customer = authService.findCustomerByEmailAndPhone(emailField.getText(), phoneField.getText());
        if (customer == null) {
            JOptionPane.showMessageDialog(this, "No matching account found.");
            return;
        }

        JOptionPane.showMessageDialog(this, "Your password is:\n" + customer.getPassword());
    }
}
