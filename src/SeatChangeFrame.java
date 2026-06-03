import javax.swing.*;
import java.awt.*;

public class SeatChangeFrame extends JFrame {
    private final AuthService authService;
    private final Customer currentUser;
    private final Reservation reservation;
    private final ReservationService reservationService;
    private final SeatService seatService;
    private final JLabel selectedSeatLabel = new JLabel();

    public SeatChangeFrame(AuthService authService, Customer currentUser,
                           Reservation reservation, ReservationService reservationService) {
        this.authService = authService;
        this.currentUser = currentUser;
        this.reservation = reservation;
        this.reservationService = reservationService;
        this.seatService = new SeatService(reservation.getFlight(), reservation.getSelectedSeatNumber());

        setTitle("Change Seat");
        setSize(920, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = AppTheme.createPagePanel();

        JPanel headerPanel = new JPanel(new GridLayout(2, 1, 4, 4));
        headerPanel.setOpaque(false);
        headerPanel.add(AppTheme.createTitle("Change Seat"));
        headerPanel.add(AppTheme.createSubtitle("Choose another seat on the same flight."));

        JPanel contentPanel = new JPanel(new BorderLayout(14, 14));
        contentPanel.setOpaque(false);
        contentPanel.add(createSummaryPanel(), BorderLayout.NORTH);
        contentPanel.add(createSeatMapScrollPane(), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        buttonPanel.setOpaque(false);
        JButton confirmButton = AppTheme.createPrimaryButton("Save Seat Change");
        JButton backButton = AppTheme.createSecondaryButton("Back");
        buttonPanel.add(confirmButton);
        buttonPanel.add(backButton);

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(contentPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        add(panel);

        confirmButton.addActionListener(e -> saveSeatChange());

        backButton.addActionListener(e -> {
            new ReservationChangeFrame(authService, currentUser, reservation, reservationService);
            dispose();
        });

        updateSelectedSeatLabel();
        setVisible(true);
    }

    private JPanel createSummaryPanel() {
        JPanel summaryPanel = AppTheme.createCardPanel();
        summaryPanel.setLayout(new GridLayout(3, 1, 6, 6));
        Flight flight = reservation.getFlight();

        JLabel flightLabel = new JLabel("Flight: " + flight.getFlightId() + " / " + flight.getDeparture() + " -> " + flight.getArrival());
        flightLabel.setForeground(AppTheme.TEXT);
        flightLabel.setFont(new Font("Arial", Font.PLAIN, 13));

        JLabel currentSeatLabel = new JLabel("Current Seat: " + (reservation.hasSelectedSeat() ? reservation.getSelectedSeatNumber() : "Not selected"));
        currentSeatLabel.setForeground(AppTheme.MUTED);
        currentSeatLabel.setFont(new Font("Arial", Font.PLAIN, 13));

        selectedSeatLabel.setForeground(AppTheme.NAVY);
        selectedSeatLabel.setFont(new Font("Arial", Font.BOLD, 13));

        summaryPanel.add(flightLabel);
        summaryPanel.add(currentSeatLabel);
        summaryPanel.add(selectedSeatLabel);
        return summaryPanel;
    }

    private JScrollPane createSeatMapScrollPane() {
        SeatMapPanel seatMapPanel = new SeatMapPanel(seatService, seatNumber -> updateSelectedSeatLabel());
        JScrollPane scrollPane = new JScrollPane(seatMapPanel);
        scrollPane.setBorder(BorderFactory.createLineBorder(AppTheme.BORDER));
        scrollPane.getVerticalScrollBar().setUnitIncrement(18);
        return scrollPane;
    }

    private void updateSelectedSeatLabel() {
        String selectedSeatNumber = seatService.getSelectedSeatNumber();
        selectedSeatLabel.setText("New Seat: " + (selectedSeatNumber == null ? "Not selected" : selectedSeatNumber));
    }

    private void saveSeatChange() {
        String selectedSeatNumber = seatService.getSelectedSeatNumber();
        if (selectedSeatNumber == null) {
            JOptionPane.showMessageDialog(this, "Please select a seat.");
            return;
        }

        boolean changed = reservationService.changeReservationSeat(
                currentUser,
                reservation.getReservationId(),
                selectedSeatNumber
        );
        if (!changed) {
            JOptionPane.showMessageDialog(this, "Seat change failed.");
            return;
        }

        JOptionPane.showMessageDialog(this, "Seat changed successfully.");
        new ReservationHistoryFrame(authService, currentUser);
        dispose();
    }
}
