import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ReservationHistoryFrame extends JFrame {
    private final AuthService authService;
    private final Customer currentUser;
    private final ReservationService reservationService;
    private final PaymentService paymentService;
    private final DefaultListModel<Reservation> listModel;
    private final JList<Reservation> reservationList;
    private final CardLayout centerLayout;
    private final JPanel centerPanel;
    private List<Reservation> reservations;

    public ReservationHistoryFrame(AuthService authService, Customer currentUser) {
        this.authService = authService;
        this.currentUser = currentUser;
        this.reservationService = new ReservationService();
        this.paymentService = new PaymentService();
        this.listModel = new DefaultListModel<>();
        this.reservationList = new JList<>(listModel);
        this.centerLayout = new CardLayout();
        this.centerPanel = new JPanel(centerLayout);

        setTitle("Reserved Tickets");
        setSize(980, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = AppTheme.createPagePanel();

        JPanel headerPanel = new JPanel(new GridLayout(2, 1, 4, 4));
        headerPanel.setOpaque(false);
        headerPanel.add(AppTheme.createTitle("Reserved Tickets"));
        headerPanel.add(AppTheme.createSubtitle("Check issued tickets, reservation status, changes, and refunds."));

        reservationList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        reservationList.setCellRenderer(new ReservationTicketRenderer());
        reservationList.setFixedCellHeight(-1);
        reservationList.setBackground(AppTheme.BACKGROUND);
        reservationList.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        JScrollPane scrollPane = new JScrollPane(reservationList);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(AppTheme.BACKGROUND);

        JPanel emptyPanel = AppTheme.createCardPanel();
        emptyPanel.setLayout(new GridBagLayout());
        JLabel emptyLabel = new JLabel("No active reservation history found.");
        emptyLabel.setForeground(AppTheme.MUTED);
        emptyLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        emptyPanel.add(emptyLabel);

        centerPanel.setOpaque(false);
        centerPanel.add(scrollPane, "tickets");
        centerPanel.add(emptyPanel, "empty");

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
        panel.add(centerPanel, BorderLayout.CENTER);
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
            reservationList.setEnabled(false);
            centerLayout.show(centerPanel, "empty");
            return;
        }

        reservationList.setEnabled(true);
        for (Reservation reservation : reservations) {
            listModel.addElement(reservation);
        }
        reservationList.setSelectedIndex(0);
        centerLayout.show(centerPanel, "tickets");
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

        if (!confirmAccountPassword()) {
            JOptionPane.showMessageDialog(this, "Password verification failed. Cancellation was not processed.");
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
        int revokedMileage = new MileageService().calculateEarnedMileageForPayment(selectedReservation, payment);
        if (revokedMileage > 0) {
            message += "\nRevoked Earned Mileage: " + revokedMileage;
        }
        JOptionPane.showMessageDialog(this, message);
        refreshReservations();
    }

    private boolean confirmAccountPassword() {
        JPasswordField passwordField = new JPasswordField();
        AppTheme.styleField(passwordField);

        JPanel panel = new JPanel(new GridLayout(1, 2, 10, 10));
        panel.add(new JLabel("Account Password"));
        panel.add(passwordField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Verify Password for Cancellation", JOptionPane.OK_CANCEL_OPTION);
        if (result != JOptionPane.OK_OPTION) {
            return false;
        }

        String password = new String(passwordField.getPassword());
        return currentUser != null && currentUser.checkPassword(password);
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

    private static class ReservationTicketRenderer extends JPanel implements ListCellRenderer<Reservation> {
        private static final Font TITLE_FONT = new Font("Dialog", Font.BOLD, 17);
        private static final Font SECTION_FONT = new Font("Dialog", Font.BOLD, 14);
        private static final Font LABEL_FONT = new Font("Dialog", Font.BOLD, 12);
        private static final Font VALUE_FONT = new Font("Dialog", Font.BOLD, 18);
        private static final Font BODY_FONT = new Font("Dialog", Font.PLAIN, 13);
        private static final Font SMALL_FONT = new Font("Dialog", Font.PLAIN, 12);

        @Override
        public Component getListCellRendererComponent(JList<? extends Reservation> list, Reservation reservation,
                                                      int index, boolean isSelected, boolean cellHasFocus) {
            removeAll();
            setLayout(new BorderLayout(0, 12));
            setOpaque(true);
            setBackground(AppTheme.BACKGROUND);
            setBorder(BorderFactory.createEmptyBorder(0, 0, 14, 0));

            JPanel cardPanel = AppTheme.createCardPanel();
            cardPanel.setLayout(new BoxLayout(cardPanel, BoxLayout.Y_AXIS));
            cardPanel.setBorder(createCardBorder(isSelected));

            cardPanel.add(createTicketHeader(reservation));
            cardPanel.add(Box.createVerticalStrut(14));
            cardPanel.add(createSectionTitle("항공 여정", "Flight Itinerary"));
            cardPanel.add(createFlightPanel(reservation));

            if (reservation != null && reservation.hasBusTicket()) {
                cardPanel.add(Box.createVerticalStrut(14));
                cardPanel.add(createSectionTitle("버스 여정", "Bus Itinerary"));
                cardPanel.add(createBusPanel(reservation));
            }

            add(cardPanel, BorderLayout.CENTER);
            return this;
        }

        private Border createCardBorder(boolean isSelected) {
            Color borderColor = isSelected ? AppTheme.BLUE : AppTheme.BORDER;
            return BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(borderColor, isSelected ? 2 : 1),
                    BorderFactory.createEmptyBorder(18, 20, 18, 20)
            );
        }

        private JPanel createTicketHeader(Reservation reservation) {
            JPanel panel = new JPanel(new BorderLayout(12, 8));
            panel.setOpaque(false);

            JLabel titleLabel = new JLabel("e-티켓 확인증");
            titleLabel.setForeground(AppTheme.NAVY);
            titleLabel.setFont(TITLE_FONT);

            JPanel metaPanel = new JPanel(new GridLayout(2, 3, 12, 6));
            metaPanel.setOpaque(false);
            metaPanel.add(createMetaLabel("승객명 Passenger", reservation == null || reservation.getCustomer() == null ? "-" : reservation.getCustomer().getName()));
            metaPanel.add(createMetaLabel("항공권번호 Ticket", reservation == null || reservation.getTicket() == null ? "-" : reservation.getTicket().getTicketId()));
            metaPanel.add(createMetaLabel("예약번호 Booking", reservation == null ? "-" : reservation.getReservationId()));
            metaPanel.add(createMetaLabel("예약상태 Status", reservation == null ? "-" : String.valueOf(reservation.getStatus())));
            metaPanel.add(createMetaLabel("총 결제금액 Total", reservation == null ? "-" : String.format("%,.0f KRW", reservation.getTotalFare())));
            metaPanel.add(createMetaLabel("버스 포함 Bus", reservation != null && reservation.hasBusTicket() ? "Yes" : "No"));

            panel.add(titleLabel, BorderLayout.NORTH);
            panel.add(metaPanel, BorderLayout.CENTER);
            return panel;
        }

        private JPanel createSectionTitle(String koreanTitle, String englishTitle) {
            JPanel panel = new JPanel(new BorderLayout());
            panel.setOpaque(false);
            panel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, AppTheme.BORDER));

            JLabel label = new JLabel(koreanTitle + "  " + englishTitle);
            label.setForeground(AppTheme.BLUE);
            label.setFont(SECTION_FONT);
            label.setBorder(BorderFactory.createEmptyBorder(12, 0, 8, 0));
            panel.add(label, BorderLayout.CENTER);
            return panel;
        }

        private JPanel createFlightPanel(Reservation reservation) {
            Flight flight = reservation == null ? null : reservation.getFlight();
            JPanel panel = createItineraryGrid();

            addItineraryCell(panel, createInfoBlock(
                    "출발 From",
                    flight == null ? "-" : flight.getDeparture(),
                    flight == null ? "-" : flight.getDate() + " " + flight.getDepartureTime()
            ), 0, 28, false);
            addItineraryCell(panel, createArrowLabel("항공"), 1, 4, false);
            addItineraryCell(panel, createInfoBlock(
                    "도착 To",
                    flight == null ? "-" : flight.getArrival(),
                    flight == null ? "-" : flight.getDate() + " " + flight.getArrivalTime()
            ), 2, 28, false);
            addItineraryCell(panel, createInfoBlock(
                    "비행기 정보 Flight",
                    flight == null ? "-" : flight.getFlightId(),
                    flight == null ? "-" : String.format("%,.0f KRW", flight.getPrice())
            ), 3, 23, false);
            addItineraryCell(panel, createInfoBlock(
                    "예약 좌석 Seat",
                    reservation != null && reservation.hasSelectedSeat() ? reservation.getSelectedSeatNumber() : "-",
                    "Aircraft seat"
            ), 4, 17, true);

            return panel;
        }

        private JPanel createBusPanel(Reservation reservation) {
            BusTicket busTicket = reservation == null ? null : reservation.getBusTicket();
            BusSchedule schedule = busTicket == null ? null : busTicket.getSchedule();
            JPanel panel = createItineraryGrid();

            addItineraryCell(panel, createInfoBlock(
                    "출발 From",
                    schedule == null ? "-" : schedule.getDepartureCity(),
                    schedule == null ? "-" : schedule.getDate() + " " + schedule.getDepartureTime()
            ), 0, 28, false);
            addItineraryCell(panel, createArrowLabel("버스"), 1, 4, false);
            addItineraryCell(panel, createInfoBlock(
                    "도착 To",
                    schedule == null ? "-" : schedule.getArrivalCity(),
                    schedule == null ? "-" : schedule.getDate() + " " + schedule.getArrivalTime()
            ), 2, 28, false);
            addItineraryCell(panel, createInfoBlock(
                    "버스 정보 Bus",
                    schedule == null ? "-" : schedule.getScheduleId(),
                    schedule == null ? "-" : schedule.getGrade() + " / " + String.format("%,.0f KRW", schedule.getFare())
            ), 3, 23, false);
            addItineraryCell(panel, createInfoBlock(
                    "예약 좌석 Seat",
                    busTicket == null || busTicket.getSeatNumber() == null ? "-" : busTicket.getSeatNumber(),
                    busTicket == null ? "-" : "Ticket " + busTicket.getTicketId()
            ), 4, 17, true);

            return panel;
        }

        private JPanel createItineraryGrid() {
            JPanel panel = new JPanel(new GridBagLayout());
            panel.setOpaque(false);
            return panel;
        }

        private void addItineraryCell(JPanel panel, Component component, int gridX, double weightX, boolean isLastCell) {
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = gridX;
            gbc.gridy = 0;
            gbc.fill = GridBagConstraints.BOTH;
            gbc.weighty = 1;
            gbc.weightx = weightX;
            gbc.insets = new Insets(0, 0, 0, isLastCell ? 0 : 10);
            panel.add(component, gbc);
        }

        private JLabel createArrowLabel(String text) {
            JLabel label = new JLabel("→", JLabel.CENTER);
            label.setForeground(AppTheme.BLUE);
            label.setFont(new Font("Dialog", Font.BOLD, 22));
            label.setToolTipText(text);
            return label;
        }

        private JPanel createInfoBlock(String label, String value, String detail) {
            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            panel.setOpaque(false);
            panel.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));

            JLabel labelView = new JLabel(label);
            labelView.setForeground(AppTheme.MUTED);
            labelView.setFont(LABEL_FONT);

            JLabel valueView = new JLabel(value);
            valueView.setForeground(AppTheme.BLUE);
            valueView.setFont(VALUE_FONT);

            JLabel detailView = new JLabel(detail);
            detailView.setForeground(AppTheme.TEXT);
            detailView.setFont(BODY_FONT);

            panel.add(labelView);
            panel.add(Box.createVerticalStrut(8));
            panel.add(valueView);
            panel.add(Box.createVerticalStrut(6));
            panel.add(detailView);
            return panel;
        }

        private JLabel createMetaLabel(String label, String value) {
            JLabel metaLabel = new JLabel("<html><span style='color:#64748b;'>" + label + "</span><br><b>"
                    + escapeHtml(value) + "</b></html>");
            metaLabel.setForeground(AppTheme.TEXT);
            metaLabel.setFont(SMALL_FONT);
            return metaLabel;
        }

        private String escapeHtml(String value) {
            if (value == null) {
                return "-";
            }
            return value
                    .replace("&", "&amp;")
                    .replace("<", "&lt;")
                    .replace(">", "&gt;")
                    .replace("\"", "&quot;")
                    .replace("'", "&#39;");
        }
    }
}
