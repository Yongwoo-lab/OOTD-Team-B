import javax.swing.*;
import java.awt.*;

public class UserInfoFrame extends JFrame {
    private AuthService authService;
    private Customer currentUser;

    public UserInfoFrame(AuthService authService, Customer currentUser) {
        this.authService = authService;
        this.currentUser = currentUser;

        setTitle("User Information");
        setSize(430, 320);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(25, 35, 25, 35));

        JLabel titleLabel = new JLabel("Authentication Completed", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));

        JTextArea infoArea = new JTextArea();
        infoArea.setEditable(false);
        infoArea.setFont(new Font("Arial", Font.PLAIN, 14));

        String info = "";
        info += "ID: " + currentUser.getCustomerId() + "\n";
        info += "Name: " + currentUser.getName() + "\n";
        info += "Email: " + currentUser.getEmail() + "\n";
        info += "User Type: " + currentUser.getUserType() + "\n";

        if (currentUser instanceof SkyPassMember) {
            SkyPassMember member = (SkyPassMember) currentUser;
            info += "Mileage: " + member.getMileage() + "\n";
        }

        info += "\nThis currentUser object can be passed to the reservation module.";

        infoArea.setText(info);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 10));

        JButton logoutButton = new JButton("Logout");
        JButton nextButton = new JButton("Proceed");

        buttonPanel.add(logoutButton);
        buttonPanel.add(nextButton);

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(infoArea, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        add(panel);

        logoutButton.addActionListener(e -> {
            new MainFrame(authService);
            dispose();
        });

        nextButton.addActionListener(e -> {
            new SearchFlightFrame(authService, currentUser);
            dispose();
        });

        setVisible(true);
    }
}