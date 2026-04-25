import javax.swing.*;
import java.awt.*;

public class ReservationFrame extends JFrame {
    public ReservationFrame(AuthService authService, Customer currentUser, String selectedFlight) {
        setTitle("Reservation");
        setSize(450, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(25, 35, 25, 35));

        JLabel titleLabel = new JLabel("Reservation Step", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));

        JTextArea infoArea = new JTextArea();
        infoArea.setEditable(false);

        infoArea.setText(
                "Login completed.\n\n" +
                        "User Name: " + currentUser.getName() + "\n" +
                        "User Type: " + currentUser.getUserType() + "\n" +
                        "Selected Flight: " + selectedFlight + "\n\n" +
                        "Reservation module should continue from here."
        );

        JButton backButton = new JButton("Back to Search");

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(infoArea, BorderLayout.CENTER);
        panel.add(backButton, BorderLayout.SOUTH);

        add(panel);

        backButton.addActionListener(e -> {
            new SearchFlightFrame(authService, currentUser);
            dispose();
        });

        setVisible(true);
    }
}