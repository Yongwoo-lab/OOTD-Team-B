import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ReservationHistoryFrame extends JFrame {
    private final AuthService authService;
    private final Customer currentUser;
    private final ReservationService reservationService;
    private final PaymentService paymentService;
    private final DefaultListModel<String> listModel;
    private final JList<String> reservationList;
    private List<Reservation> reservations;

    public ReservationHistoryFrame(AuthService authService, Customer currentUser) {
        this.authService = authService;
        this.currentUser = currentUser;
        this.reservationService = new ReservationService();
        this.paymentService = new PaymentService();
        this.listModel = new DefaultListModel<>();
        this.reservationList = new JList<>(listModel);

        setTitle("Reserved Tickets");
        setSize(920, 540);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = AppTheme.createPagePanel();

        JPanel headerPanel = new JPanel(new GridLayout(2, 1, 4, 4));
        headerPanel.setOpaque(false);
        headerPanel.add(AppTheme.createTitle("Reserved Tickets"));
        headerPanel.add(AppTheme.createSubtitle("Check issued tickets, reservation status, changes, and refunds."));

        reservationList.setFont(new Font("Monospaced", Font.PLAIN, 12));
        reservationList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(reservationList);
        scrollPane.setBorder(BorderFactory.createLineBorder(AppTheme.BORDER));

        JPanel buttonPanel = new JPanel(new GridLayout(1, 4, 10, 10));
        buttonPanel.setOpaque(false);

        JButton refreshButton = AppTheme.createSecondaryButton("Refresh");
        JButton changeButton = AppTheme.createSecondaryButton("Change Reservation");
        JButton cancelButton = AppTheme.createPrimaryButton("Cancel Reservation");
        JButton backButton = AppTheme.createSecondaryButton("Back to My Page");

        buttonPanel.add(refreshButton);
        buttonPanel.add(changeButton);
        buttonPanel.add(cancelButton);
        buttonPanel.add(backButton);

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        add(panel);

        refreshReservations();

        refreshButton.addActionListener(e -> refreshReservations());

        changeButton.addActionListener(e -> changeSelectedReservation());

        cancelButton.addActionListener(e -> cancelSelectedReservation());

        backButton.addActionListener(e -> {
            new UserInfoFrame(authService, currentUser);
            dispose();
        });

        setVisible(true);
    }

    private void refreshReservations() {
        listModel.clear();
        List<Reservation> allReservations = reservationService.getReservationsByCustomer(currentUser);
        reservations = new ArrayList<>();
        for (Reservation reservation : allReservations) {
            if (reservation.getStatus() != ReservationStatus.CANCELLED) {
                reservations.add(reservation);
            }
        }

        if (reservations.isEmpty()) {
            listModel.addElement("No active reservation history found.");
            reservationList.setEnabled(false);
            return;
        }

        reservationList.setEnabled(true);
        for (Reservation reservation : reservations) {
            listModel.addElement(formatReservation(reservation));
        }
    }

    private void cancelSelectedReservation() {
        int selectedIndex = reservationList.getSelectedIndex();
        if (selectedIndex < 0 || selectedIndex >= reservations.size()) {
            JOptionPane.showMessageDialog(this, "Please select a reservation to cancel.");
            return;
        }

        Reservation selectedReservation = reservations.get(selectedIndex);
        if (selectedReservation.getStatus() == ReservationStatus.CANCELLED) {
            JOptionPane.showMessageDialog(this, "This reservation is already cancelled.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Cancel reservation " + selectedReservation.getReservationId() + "?",
                "Cancel Confirmation",
                JOptionPane.YES_NO_OPTION
        );
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        boolean cancelled = reservationService.cancelReservation(currentUser, selectedReservation.getReservationId(), paymentService, authService);
        if (!cancelled) {
            JOptionPane.showMessageDialog(this, "Cancellation failed. Please try again.");
            return;
        }

        String message = "Reservation cancelled successfully.";
        Refund refund = selectedReservation.getRefund();
        if (refund != null) {
            message += "\nRefund ID: " + refund.getRefundId()
                    + "\nRefund Amount: " + String.format("%,.0f KRW", refund.getAmount())
                    + "\nRefund Status: " + refund.getStatus();
        }
        Payment payment = selectedReservation.getPayment();
        if (payment != null && payment.getMileageUsed() > 0) {
            message += "\nRestored Mileage: " + payment.getMileageUsed();
        }
        JOptionPane.showMessageDialog(this, message);
        refreshReservations();
    }

    private void changeSelectedReservation() {
        int selectedIndex = reservationList.getSelectedIndex();
        if (selectedIndex < 0 || selectedIndex >= reservations.size()) {
            JOptionPane.showMessageDialog(this, "Please select a reservation to change.");
            return;
        }

        Reservation selectedReservation = reservations.get(selectedIndex);
        if (!selectedReservation.canChange()) {
            JOptionPane.showMessageDialog(this, "This reservation cannot be changed.");
            return;
        }

        new ReservationChangeFrame(authService, currentUser, selectedReservation, reservationService);
        dispose();
    }

    private String formatReservation(Reservation reservation) {
        Flight flight = reservation.getFlight();
        String route = flight.getDeparture() + " -> " + flight.getArrival();
        String seat = reservation.hasSelectedSeat() ? reservation.getSelectedSeatNumber() : "-";
        String bus = reservation.hasBusTicket() ? " | Bus " + reservation.getBusTicket().getRouteText() : "";
        String ticket = reservation.getTicket() == null ? "Ticket -" : "Ticket " + reservation.getTicket().getTicketId();
        String busTicket = "";
        if (reservation.hasBusTicket()) {
            busTicket = " | BusTicket " + reservation.getBusTicket().getTicketId();
        }

        return String.format("%s | %s | %s | %s | Seat %s%s%s | %,.0f KRW",
                reservation.getReservationId(),
                reservation.getStatus(),
                ticket,
                route,
                seat,
                bus,
                busTicket,
                reservation.getTotalFare());
    }
}
