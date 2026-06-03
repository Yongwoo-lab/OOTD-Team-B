import javax.swing.*;
import java.awt.*;

public class SeatSelectionFrame extends JFrame {
    private final SeatService seatService;
    private final Reservation reservation;
    private final JLabel selectedSeatLabel = new JLabel();
    private final JLabel availableSeatLabel = new JLabel();

    public SeatSelectionFrame(AuthService authService, Customer currentUser, Flight selectedFlight,
                              Reservation reservation, ReservationService reservationService) {
        this.reservation = reservation;
        this.seatService = new SeatService(reservation.getFlight(), reservation.getSelectedSeatNumber());

        setTitle("Seat Selection");
        setSize(920, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = AppTheme.createPagePanel();

        JPanel headerPanel = new JPanel(new GridLayout(2, 1, 4, 4));
        headerPanel.setOpaque(false);
        headerPanel.add(AppTheme.createTitle("Seat Selection"));
        headerPanel.add(AppTheme.createSubtitle("Choose one seat before moving to payment."));

        JPanel contentPanel = new JPanel(new BorderLayout(14, 14));
        contentPanel.setOpaque(false);
        contentPanel.add(createReservationSummary(currentUser, selectedFlight), BorderLayout.NORTH);
        contentPanel.add(createSeatMapScrollPane(), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        buttonPanel.setOpaque(false);
        JButton backButton = AppTheme.createSecondaryButton("Back to Reservation");
        JButton nextButton = AppTheme.createPrimaryButton("Continue to Payment");
        buttonPanel.add(backButton);
        buttonPanel.add(nextButton);

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(contentPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        add(panel);

        backButton.addActionListener(e -> {
            new ReservationFrame(authService, currentUser, selectedFlight, reservation);
            dispose();
        });

        nextButton.addActionListener(e -> {
            String selectedSeatNumber = seatService.getSelectedSeatNumber();
            if (selectedSeatNumber == null) {
                JOptionPane.showMessageDialog(this, "Please select a seat before payment.");
                return;
            }

            reservation.selectSeat(selectedSeatNumber);
            new PaymentFrame(authService, currentUser, selectedFlight, reservation, reservationService);
            dispose();
        });

        updateSummaryLabels();
        setVisible(true);
    }

    private JPanel createReservationSummary(Customer currentUser, Flight selectedFlight) {
        JPanel summaryPanel = AppTheme.createCardPanel();
        summaryPanel.setLayout(new BorderLayout(12, 12));

        JTextArea flightArea = new JTextArea();
        flightArea.setEditable(false);
        flightArea.setLineWrap(true);
        flightArea.setWrapStyleWord(true);
        flightArea.setFont(new Font("Arial", Font.PLAIN, 13));
        flightArea.setForeground(AppTheme.TEXT);
        flightArea.setBackground(Color.WHITE);
        flightArea.setBorder(BorderFactory.createEmptyBorder());
        flightArea.setText(
                "Reservation ID: " + reservation.getReservationId() + "\n" +
                        "Passenger: " + currentUser.getName() + " (" + currentUser.getUserType() + ")\n" +
                        "Aircraft: " + seatService.getAircraftType() + " (" + seatService.getTotalSeatCount() + " seats)\n" +
                        "Flight: " + formatFlight(selectedFlight)
        );

        JPanel statusPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        statusPanel.setOpaque(false);
        selectedSeatLabel.setFont(new Font("Arial", Font.BOLD, 13));
        selectedSeatLabel.setForeground(AppTheme.NAVY);
        availableSeatLabel.setFont(new Font("Arial", Font.BOLD, 13));
        availableSeatLabel.setForeground(AppTheme.MUTED);
        statusPanel.add(selectedSeatLabel);
        statusPanel.add(availableSeatLabel);

        summaryPanel.add(flightArea, BorderLayout.CENTER);
        summaryPanel.add(statusPanel, BorderLayout.SOUTH);
        return summaryPanel;
    }

    private JScrollPane createSeatMapScrollPane() {
        SeatMapPanel seatMapPanel = new SeatMapPanel(seatService, seatNumber -> {
            reservation.selectSeat(seatNumber);
            updateSummaryLabels();
        });
        JScrollPane scrollPane = new JScrollPane(seatMapPanel);
        scrollPane.setBorder(BorderFactory.createLineBorder(AppTheme.BORDER));
        scrollPane.getVerticalScrollBar().setUnitIncrement(18);
        return scrollPane;
    }

    private void updateSummaryLabels() {
        String selectedSeatNumber = seatService.getSelectedSeatNumber();
        selectedSeatLabel.setText("Selected Seat: " + (selectedSeatNumber == null ? "Not selected" : selectedSeatNumber));
        availableSeatLabel.setText(
                "Available: " + seatService.getAvailableSeatCount() +
                        " / " + seatService.getTotalSeatCount() +
                        "  Occupied: " + seatService.getOccupiedSeatCount()
        );
    }

    private String formatFlight(Flight flight) {
        if (flight == null) {
            return "No flight selected.";
        }
        return flight.getFlightId() + " / " + flight.getDeparture() + " -> " + flight.getArrival()
                + " / " + flight.getDate() + " " + flight.getDepartureTime() + " -> " + flight.getArrivalTime()
                + " / " + String.format("%,.0f KRW", flight.getPrice());
    }
}
