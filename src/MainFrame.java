import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private AuthService authService;

    public MainFrame(AuthService authService) {
        this.authService = authService;

        setTitle("Korean Air Reservation System");
        setSize(420, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(25, 35, 25, 35));

        JLabel titleLabel = new JLabel("Korean Air Reservation System", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));

        JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 10, 10));

        JButton loginButton = new JButton("Login");
        JButton signupButton = new JButton("Sign Up");
        JButton guestButton = new JButton("Continue as Guest");

        buttonPanel.add(loginButton);
        buttonPanel.add(signupButton);
        buttonPanel.add(guestButton);

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(buttonPanel, BorderLayout.CENTER);

        add(panel);

        loginButton.addActionListener(e -> {
            new LoginFrame(authService, user -> {
                new SearchFlightFrame(authService, user);
            });
            dispose();
        });

        signupButton.addActionListener(e -> {
            new SignupFrame(authService);
            dispose();
        });

        guestButton.addActionListener(e -> {
            Customer guest = authService.continueAsGuest();
            new UserInfoFrame(authService, guest);
            dispose();
        });

        setVisible(true);
    }
}