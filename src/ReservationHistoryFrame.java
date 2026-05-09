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

        setTitle("Reservation History");
        setSize(740, 520);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = AppTheme.createPagePanel();

        JPanel headerPanel = new JPanel(new GridLayout(2, 1, 4, 4));
        headerPanel.setOpaque(false);
        headerPanel.add(AppTheme.createTitle("Reservation History"));
        headerPanel.add(AppTheme.createSubtitle("Check your reservation status and cancel when needed."));

        reservationList.setFont(new Font("Monospaced", Font.PLAIN, 12));
        reservationList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(reservationList);
        scrollPane.setBorder(BorderFactory.createLineBorder(AppTheme.BORDER));

        JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        buttonPanel.setOpaque(false);

        JButton refreshButton = AppTheme.createSecondaryButton("Refresh");
        JButton cancelButton = AppTheme.createPrimaryButton("Cancel Reservation");
        JButton backButton = AppTheme.createSecondaryButton("Back to My Page");

        buttonPanel.add(refreshButton);
        buttonPanel.add(cancelButton);
        buttonPanel.add(backButton);

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        add(panel);

        refreshReservations();

        refreshButton.addActionListener(e -> refreshReservations());

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

        boolean cancelled = reservationService.cancelReservation(selectedReservation.getReservationId(), paymentService);
        if (!cancelled) {
            JOptionPane.showMessageDialog(this, "Cancellation failed. Please try again.");
            return;
        }

        JOptionPane.showMessageDialog(this, "Reservation cancelled successfully.");
        refreshReservations();
    }

    private String formatReservation(Reservation reservation) {
        Flight flight = reservation.getFlight();
        String route = flight.getDeparture() + " -> " + flight.getArrival();
        String seat = reservation.hasSelectedSeat() ? reservation.getSelectedSeatNumber() : "-";

        return String.format("%s | %s | %s | Seat %s | %,.0f KRW",
                reservation.getReservationId(),
                reservation.getStatus(),
                route,
                seat,
                reservation.getTotalFare());
    }
}
