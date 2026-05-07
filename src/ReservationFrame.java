import javax.swing.*;
import java.awt.*;

public class ReservationFrame extends JFrame {
    private final ReservationController reservationController = new ReservationController();

    public ReservationFrame(AuthService authService, Customer currentUser, String selectedFlight) {
        this(authService, currentUser, selectedFlight, null);
    }

    public ReservationFrame(AuthService authService, Customer currentUser, String selectedFlight, Reservation existingReservation) {
        setTitle("Reservation");
        setSize(560, 410);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = AppTheme.createPagePanel();

        JPanel headerPanel = new JPanel(new GridLayout(2, 1, 4, 4));
        headerPanel.setOpaque(false);
        headerPanel.add(AppTheme.createTitle("Reservation"));
        headerPanel.add(AppTheme.createSubtitle("Review your selected flight before payment."));

        JTextArea infoArea = new JTextArea();
        infoArea.setEditable(false);
        infoArea.setLineWrap(true);
        infoArea.setWrapStyleWord(true);
        infoArea.setFont(new Font("Arial", Font.PLAIN, 13));
        infoArea.setBackground(Color.WHITE);
        infoArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.BORDER),
                BorderFactory.createEmptyBorder(14, 14, 14, 14)
        ));

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
        buttonPanel.setOpaque(false);
        JButton backButton = AppTheme.createSecondaryButton("Back to Search");
        JButton nextButton = AppTheme.createPrimaryButton("Next");
        buttonPanel.add(backButton);
        buttonPanel.add(nextButton);

        panel.add(headerPanel, BorderLayout.NORTH);
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
