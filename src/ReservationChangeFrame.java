import javax.swing.*;
import java.awt.*;

public class ReservationChangeFrame extends JFrame {
    private final AuthService authService;
    private final Customer currentUser;
    private final Reservation reservation;
    private final ReservationService reservationService;

    public ReservationChangeFrame(AuthService authService, Customer currentUser,
                                  Reservation reservation, ReservationService reservationService) {
        this.authService = authService;
        this.currentUser = currentUser;
        this.reservation = reservation;
        this.reservationService = reservationService;

        setTitle("Change Reservation");
        setSize(620, 420);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = AppTheme.createPagePanel();

        JPanel headerPanel = new JPanel(new GridLayout(2, 1, 4, 4));
        headerPanel.setOpaque(false);
        headerPanel.add(AppTheme.createTitle("Change Reservation"));
        headerPanel.add(AppTheme.createSubtitle("Only seat changes are available within the same flight."));

        JTextArea summaryArea = createSummaryArea();
        updateSummary(summaryArea);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        buttonPanel.setOpaque(false);
        JButton seatButton = AppTheme.createPrimaryButton("Change Seat");
        JButton backButton = AppTheme.createSecondaryButton("Back to Tickets");
        buttonPanel.add(seatButton);
        buttonPanel.add(backButton);

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(summaryArea, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        add(panel);

        seatButton.addActionListener(e -> {
            new SeatChangeFrame(authService, currentUser, reservation, reservationService);
            dispose();
        });

        backButton.addActionListener(e -> {
            new ReservationHistoryFrame(authService, currentUser);
            dispose();
        });

        setVisible(true);
    }

    private JTextArea createSummaryArea() {
        JTextArea summaryArea = new JTextArea();
        summaryArea.setEditable(false);
        summaryArea.setLineWrap(true);
        summaryArea.setWrapStyleWord(true);
        summaryArea.setFont(new Font("Arial", Font.PLAIN, 13));
        summaryArea.setForeground(AppTheme.TEXT);
        summaryArea.setBackground(Color.WHITE);
        summaryArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.BORDER),
                BorderFactory.createEmptyBorder(14, 14, 14, 14)
        ));
        return summaryArea;
    }

    private void updateSummary(JTextArea summaryArea) {
        Flight flight = reservation.getFlight();
        String seat = reservation.hasSelectedSeat() ? reservation.getSelectedSeatNumber() : "Not selected";
        summaryArea.setText(
                "Reservation ID: " + reservation.getReservationId() + "\n" +
                        "Status: " + reservation.getStatus() + "\n" +
                        "Flight: " + flight.getFlightId() + " / " + flight.getDeparture() + " -> " + flight.getArrival() + "\n" +
                        "Date: " + flight.getDate() + " " + flight.getDepartureTime() + " -> " + flight.getArrivalTime() + "\n" +
                        "Current Seat: " + seat + "\n\n" +
                        "Reservation changes are limited to seat changes on this same flight."
        );
    }
}
