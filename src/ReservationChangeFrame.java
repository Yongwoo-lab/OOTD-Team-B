import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ReservationChangeFrame extends JFrame {
    private final AuthService authService;
    private final Customer currentUser;
    private final Reservation reservation;
    private final ReservationService reservationService;
    private final List<Flight> flights;
    private final DefaultListModel<String> listModel = new DefaultListModel<>();
    private final JList<String> flightList = new JList<>(listModel);

    public ReservationChangeFrame(AuthService authService, Customer currentUser,
                                  Reservation reservation, ReservationService reservationService) {
        this.authService = authService;
        this.currentUser = currentUser;
        this.reservation = reservation;
        this.reservationService = reservationService;
        this.flights = new FlightDatabase().loadFlights();

        setTitle("Change Reservation");
        setSize(820, 560);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = AppTheme.createPagePanel();

        JPanel headerPanel = new JPanel(new GridLayout(2, 1, 4, 4));
        headerPanel.setOpaque(false);
        headerPanel.add(AppTheme.createTitle("Change Reservation"));
        headerPanel.add(AppTheme.createSubtitle("Select a new flight or continue to seat selection."));

        JTextArea summaryArea = createSummaryArea();
        updateSummary(summaryArea);

        flightList.setFont(new Font("Monospaced", Font.PLAIN, 12));
        flightList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(flightList);
        scrollPane.setBorder(BorderFactory.createLineBorder(AppTheme.BORDER));

        JPanel centerPanel = new JPanel(new BorderLayout(12, 12));
        centerPanel.setOpaque(false);
        centerPanel.add(summaryArea, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 4, 10, 10));
        buttonPanel.setOpaque(false);
        JButton applyFlightButton = AppTheme.createPrimaryButton("Apply Flight Change");
        JButton seatButton = AppTheme.createSecondaryButton("Change Seat");
        JButton paymentButton = AppTheme.createPrimaryButton("Continue Payment");
        JButton backButton = AppTheme.createSecondaryButton("Back to History");
        buttonPanel.add(applyFlightButton);
        buttonPanel.add(seatButton);
        buttonPanel.add(paymentButton);
        buttonPanel.add(backButton);

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(centerPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        add(panel);

        populateFlights();

        applyFlightButton.addActionListener(e -> {
            int selectedIndex = flightList.getSelectedIndex();
            if (selectedIndex < 0 || selectedIndex >= flights.size()) {
                JOptionPane.showMessageDialog(this, "Please select a new flight.");
                return;
            }

            Flight newFlight = flights.get(selectedIndex);
            boolean changed = reservationService.changeReservationFlight(currentUser, reservation.getReservationId(), newFlight);
            if (!changed) {
                JOptionPane.showMessageDialog(this, "Reservation cannot be changed.");
                return;
            }

            JOptionPane.showMessageDialog(this, "Flight changed. Please select a seat again before payment.");
            updateSummary(summaryArea);
        });

        seatButton.addActionListener(e -> {
            reservation.requestChange();
            new SeatSelectionFrame(authService, currentUser, formatFlight(reservation.getFlight()), reservation, reservationService);
            dispose();
        });

        paymentButton.addActionListener(e -> {
            if (!reservation.hasSelectedSeat()) {
                JOptionPane.showMessageDialog(this, "Please select a seat before payment.");
                return;
            }
            new PaymentFrame(authService, currentUser, formatFlight(reservation.getFlight()), reservation, reservationService);
            dispose();
        });

        backButton.addActionListener(e -> {
            new ReservationHistoryFrame(authService, currentUser);
            dispose();
        });

        setVisible(true);
    }

    private void populateFlights() {
        listModel.clear();
        for (Flight flight : flights) {
            listModel.addElement(formatFlight(flight));
        }
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
                BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));
        return summaryArea;
    }

    private void updateSummary(JTextArea summaryArea) {
        Flight flight = reservation.getFlight();
        String seat = reservation.hasSelectedSeat() ? reservation.getSelectedSeatNumber() : "Not selected";
        summaryArea.setText(
                "Reservation ID: " + reservation.getReservationId() + "\n" +
                        "Status: " + reservation.getStatus() + "\n" +
                        "Current Flight: " + flight.getFlightId() + " / " + flight.getDeparture() + " -> " + flight.getArrival() + "\n" +
                        "Date: " + flight.getDate() + " " + flight.getDepartureTime() + " -> " + flight.getArrivalTime() + "\n" +
                        "Selected Seat: " + seat + "\n" +
                        "Total Before Mileage: " + String.format("%,.0f KRW", reservation.getTotalFare())
        );
    }

    private String formatFlight(Flight flight) {
        return String.format("%s | %s -> %-28s | %s %s -> %s | %,.0f KRW",
                flight.getFlightId(),
                flight.getDeparture(),
                flight.getArrival(),
                flight.getDate(),
                flight.getDepartureTime(),
                flight.getArrivalTime(),
                flight.getPrice());
    }
}
