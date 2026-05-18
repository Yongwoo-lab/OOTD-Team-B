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
    private final JTextArea summaryArea;

    public BusTicketFrame(AuthService authService, Customer currentUser, String selectedFlight,
                          Reservation reservation, ReservationService reservationService) {
        this.authService = authService;
        this.currentUser = currentUser;
        this.selectedFlight = selectedFlight;
        this.reservation = reservation;
        this.reservationService = reservationService;
        this.schedules = new BusDatabase().loadSchedules(reservation.getFlight());

        setTitle("Premium Express Bus Ticket");
        setSize(760, 520);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = AppTheme.createPagePanel();

        JPanel headerPanel = new JPanel(new GridLayout(2, 1, 4, 4));
        headerPanel.setOpaque(false);
        headerPanel.add(AppTheme.createTitle("Premium Express Bus Ticket"));
        headerPanel.add(AppTheme.createSubtitle("Bus arrival is set about two hours before your flight departure."));

        scheduleList.setFont(new Font("Monospaced", Font.PLAIN, 12));
        scheduleList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(scheduleList);
        scrollPane.setBorder(BorderFactory.createLineBorder(AppTheme.BORDER));

        summaryArea = createSummaryArea();
        updateSummary();

        JPanel centerPanel = new JPanel(new BorderLayout(12, 12));
        centerPanel.setOpaque(false);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        centerPanel.add(summaryArea, BorderLayout.SOUTH);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        buttonPanel.setOpaque(false);
        JButton nextButton = AppTheme.createPrimaryButton("Next: Choose Seat");
        JButton removeButton = AppTheme.createSecondaryButton("Remove Bus Ticket");
        JButton backButton = AppTheme.createSecondaryButton("Back to Reservation");
        buttonPanel.add(nextButton);
        buttonPanel.add(removeButton);
        buttonPanel.add(backButton);

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(centerPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        add(panel);

        populateSchedules();
        if (!schedules.isEmpty()) {
            scheduleList.setSelectedIndex(0);
        }

        scheduleList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateSummary();
            }
        });

        nextButton.addActionListener(e -> {
            BusSchedule selectedSchedule = getSelectedSchedule();
            if (selectedSchedule == null) {
                JOptionPane.showMessageDialog(this, "Please select a bus ticket.");
                return;
            }

            new BusSeatSelectionFrame(authService, currentUser, selectedFlight, reservation, reservationService, selectedSchedule);
            dispose();
        });

        removeButton.addActionListener(e -> {
            reservation.removeBusTicket();
            JOptionPane.showMessageDialog(this, "Bus ticket has been removed.");
            updateSummary();
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
        return String.format("%s | %-8s -> %-7s | %s %s -> %s | %s | %,8.0f KRW",
                schedule.getScheduleId(),
                schedule.getDepartureCity(),
                schedule.getArrivalCity(),
                schedule.getDate(),
                schedule.getDepartureTime(),
                schedule.getArrivalTime(),
                schedule.getGrade(),
                schedule.getFare());
    }

    private BusSchedule getSelectedSchedule() {
        int selectedIndex = scheduleList.getSelectedIndex();
        if (selectedIndex < 0 || selectedIndex >= schedules.size()) {
            return null;
        }
        return schedules.get(selectedIndex);
    }

    private JTextArea createSummaryArea() {
        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setFont(new Font("Arial", Font.PLAIN, 13));
        area.setForeground(AppTheme.TEXT);
        area.setBackground(AppTheme.LIGHT_BLUE);
        area.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(190, 211, 238)),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));
        return area;
    }

    private void updateSummary() {
        String currentBusText = "No bus ticket selected.";
        if (reservation.hasBusTicket()) {
            BusTicket busTicket = reservation.getBusTicket();
            BusSchedule schedule = busTicket.getSchedule();
            currentBusText = schedule.getDepartureCity() + " -> " + schedule.getArrivalCity()
                    + " / " + schedule.getDate() + " " + schedule.getDepartureTime()
                    + " / Seat " + busTicket.getSeatNumber()
                    + " / " + String.format("%,.0f KRW", schedule.getFare());
        }

        BusSchedule selectedSchedule = getSelectedSchedule();
        String selectedText = selectedSchedule == null
                ? "Not selected"
                : selectedSchedule.getDepartureCity() + " -> " + selectedSchedule.getArrivalCity()
                + " / " + selectedSchedule.getDate() + " " + selectedSchedule.getDepartureTime()
                + " / " + String.format("%,.0f KRW", selectedSchedule.getFare());

        summaryArea.setText(
                "Reservation ID: " + reservation.getReservationId() + "\n" +
                        "Selected Route: " + selectedText + "\n" +
                        "Current Bus Ticket: " + currentBusText + "\n" +
                        "Flight Date/Departure: " + reservation.getFlight().getDate() + " " + reservation.getFlight().getDepartureTime() + "\n" +
                        "Flight Fare: " + String.format("%,.0f KRW", reservation.getFlightFare()) + "\n" +
                        "Bus Fare: " + String.format("%,.0f KRW", reservation.getBusFare()) + "\n" +
                        "Total Before Mileage: " + String.format("%,.0f KRW", reservation.getTotalFare())
        );
    }
}
