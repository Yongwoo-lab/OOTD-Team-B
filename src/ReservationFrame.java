import javax.swing.*;
import java.awt.*;

public class ReservationFrame extends JFrame {
    private final ReservationController reservationController = new ReservationController();

    public ReservationFrame(AuthService authService, Customer currentUser, String selectedFlight) {
        this(authService, currentUser, selectedFlight, null);
    }

    public ReservationFrame(AuthService authService, Customer currentUser, String selectedFlight, Reservation existingReservation) {
        setTitle("Reservation");
        setSize(500, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(25, 35, 25, 35));

        JLabel titleLabel = new JLabel("Reservation", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));

        JTextArea infoArea = new JTextArea();
        infoArea.setEditable(false);
        infoArea.setLineWrap(true);
        infoArea.setWrapStyleWord(true);

        String flightInfo = selectedFlight;
        if (flightInfo == null || flightInfo.trim().isEmpty()) {
            flightInfo = "No flight selected.";
        }

        Reservation reservation = existingReservation != null
                ? existingReservation
                : reservationController.bookFlight(currentUser, selectedFlight);

        infoArea.setText(
                "Basic reservation ready.\n\n" +
                        "Reservation ID: " + reservation.getReservationId() + "\n" +
                        "Reservation Status: " + reservation.getStatus() + "\n" +
                        "Selected Flight:\n" + flightInfo + "\n\n" +
                        "User Name: " + currentUser.getName() + "\n" +
                        "User Type: " + currentUser.getUserType() + "\n" +
                        "Total Fare: " + String.format("%,.0f KRW", reservation.getTotalFare()) + "\n"
        );

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        JButton backButton = new JButton("Back to Search");
        JButton nextButton = new JButton("Next");
        buttonPanel.add(backButton);
        buttonPanel.add(nextButton);

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(infoArea, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        add(panel);

        backButton.addActionListener(e -> {
            new SearchFlightFrame(authService, currentUser);
            dispose();
        });

        nextButton.addActionListener(e -> {
            new PaymentFrame(authService, currentUser, selectedFlight, reservation, reservationController.getReservationService());
            dispose();
        });

        setVisible(true);
    }
}
