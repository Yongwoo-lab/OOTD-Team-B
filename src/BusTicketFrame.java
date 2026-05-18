import javax.swing.*;
import java.awt.*;
import java.util.List;

public class BusTicketFrame extends JFrame {
    private final AuthService authService;
    private final Customer currentUser;
    private final String selectedFlight;
    private final Reservation reservation;
    private final ReservationService reservationService;
    private final List<BusSchedule> schedules;
    private final DefaultListModel<String> listModel = new DefaultListModel<>();
    private final JList<String> scheduleList = new JList<>(listModel);

    public BusTicketFrame(AuthService authService, Customer currentUser, String selectedFlight,
                          Reservation reservation, ReservationService reservationService) {
        this.authService = authService;
        this.currentUser = currentUser;
        this.selectedFlight = selectedFlight;
        this.reservation = reservation;
        this.reservationService = reservationService;
        this.schedules = new BusDatabase().loadSchedules();

        setTitle("Premium Express Bus Ticket");
        setSize(760, 520);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = AppTheme.createPagePanel();

        JPanel headerPanel = new JPanel(new GridLayout(2, 1, 4, 4));
        headerPanel.setOpaque(false);
        headerPanel.add(AppTheme.createTitle("Premium Express Bus Ticket"));
        headerPanel.add(AppTheme.createSubtitle("Optional add-on service for six major cities."));

        scheduleList.setFont(new Font("Monospaced", Font.PLAIN, 12));
        scheduleList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(scheduleList);
        scrollPane.setBorder(BorderFactory.createLineBorder(AppTheme.BORDER));

        JTextArea summaryArea = createSummaryArea();
        updateSummary(summaryArea);

        JPanel centerPanel = new JPanel(new BorderLayout(12, 12));
        centerPanel.setOpaque(false);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        centerPanel.add(summaryArea, BorderLayout.SOUTH);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        buttonPanel.setOpaque(false);
        JButton addButton = AppTheme.createPrimaryButton("Add Bus Ticket");
        JButton removeButton = AppTheme.createSecondaryButton("Remove Bus Ticket");
        JButton backButton = AppTheme.createSecondaryButton("Back to Reservation");
        buttonPanel.add(addButton);
        buttonPanel.add(removeButton);
        buttonPanel.add(backButton);

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(centerPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        add(panel);

        populateSchedules();

        addButton.addActionListener(e -> {
            int selectedIndex = scheduleList.getSelectedIndex();
            if (selectedIndex < 0 || selectedIndex >= schedules.size()) {
                JOptionPane.showMessageDialog(this, "Please select a bus ticket.");
                return;
            }

            BusTicket busTicket = schedules.get(selectedIndex).createTicket();
            reservation.addBusTicket(busTicket);
            JOptionPane.showMessageDialog(this, "Bus ticket has been added to this flight reservation.");
            updateSummary(summaryArea);
        });

        removeButton.addActionListener(e -> {
            reservation.removeBusTicket();
            JOptionPane.showMessageDialog(this, "Bus ticket has been removed.");
            updateSummary(summaryArea);
        });

        backButton.addActionListener(e -> {
            new ReservationFrame(authService, currentUser, selectedFlight, reservation);
            dispose();
        });

        setVisible(true);
    }

    private void populateSchedules() {
        listModel.clear();
        for (BusSchedule schedule : schedules) {
            listModel.addElement(formatSchedule(schedule));
        }
    }

    private String formatSchedule(BusSchedule schedule) {
        return String.format("%s | %-7s -> %-7s | %s %s -> %s | %s | %,8.0f KRW",
                schedule.getScheduleId(),
                schedule.getDepartureCity(),
                schedule.getArrivalCity(),
                schedule.getDate(),
                schedule.getDepartureTime(),
                schedule.getArrivalTime(),
                schedule.getGrade(),
                schedule.getFare());
    }

    private JTextArea createSummaryArea() {
        JTextArea summaryArea = new JTextArea();
        summaryArea.setEditable(false);
        summaryArea.setLineWrap(true);
        summaryArea.setWrapStyleWord(true);
        summaryArea.setFont(new Font("Arial", Font.PLAIN, 13));
        summaryArea.setForeground(AppTheme.TEXT);
        summaryArea.setBackground(AppTheme.LIGHT_BLUE);
        summaryArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(190, 211, 238)),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));
        return summaryArea;
    }

    private void updateSummary(JTextArea summaryArea) {
        String busText = "No bus ticket selected.";
        if (reservation.hasBusTicket()) {
            BusTicket busTicket = reservation.getBusTicket();
            BusSchedule schedule = busTicket.getSchedule();
            busText = schedule.getDepartureCity() + " -> " + schedule.getArrivalCity()
                    + " / " + schedule.getDate() + " " + schedule.getDepartureTime()
                    + " / " + String.format("%,.0f KRW", schedule.getFare());
        }

        summaryArea.setText(
                "Reservation ID: " + reservation.getReservationId() + "\n" +
                        "Current Bus Ticket: " + busText + "\n" +
                        "Flight Fare: " + String.format("%,.0f KRW", reservation.getFlightFare()) + "\n" +
                        "Bus Fare: " + String.format("%,.0f KRW", reservation.getBusFare()) + "\n" +
                        "Total Before Mileage: " + String.format("%,.0f KRW", reservation.getTotalFare())
        );
    }
}
