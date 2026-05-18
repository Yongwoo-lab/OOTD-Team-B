import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class BusSeatSelectionFrame extends JFrame {
    private final AuthService authService;
    private final Customer currentUser;
    private final String selectedFlight;
    private final Reservation reservation;
    private final ReservationService reservationService;
    private final BusSchedule selectedSchedule;
    private final Map<Integer, JButton> busSeatButtons = new HashMap<>();
    private final JPanel busSeatPanel = new JPanel(new GridBagLayout());
    private final JTextArea summaryArea;
    private Integer selectedBusSeatNumber;

    public BusSeatSelectionFrame(AuthService authService, Customer currentUser, String selectedFlight,
                                 Reservation reservation, ReservationService reservationService,
                                 BusSchedule selectedSchedule) {
        this.authService = authService;
        this.currentUser = currentUser;
        this.selectedFlight = selectedFlight;
        this.reservation = reservation;
        this.reservationService = reservationService;
        this.selectedSchedule = selectedSchedule;

        setTitle("Premium Bus Seat Selection");
        setSize(620, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = AppTheme.createPagePanel();

        JPanel headerPanel = new JPanel(new GridLayout(2, 1, 4, 4));
        headerPanel.setOpaque(false);
        headerPanel.add(AppTheme.createTitle("Premium Bus Seat Selection"));
        headerPanel.add(AppTheme.createSubtitle("Choose one of 28 premium express bus seats."));

        summaryArea = createSummaryArea();
        updateSummary();

        JPanel centerPanel = AppTheme.createCardPanel();
        centerPanel.setLayout(new BorderLayout(8, 8));

        JLabel titleLabel = new JLabel("Premium Bus Seat Map", JLabel.CENTER);
        titleLabel.setForeground(AppTheme.NAVY);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));

        busSeatPanel.setOpaque(false);
        createBusSeatButtons();
        refreshBusSeatButtons();

        centerPanel.add(titleLabel, BorderLayout.NORTH);
        centerPanel.add(busSeatPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        buttonPanel.setOpaque(false);
        JButton addButton = AppTheme.createPrimaryButton("Add Bus Ticket");
        JButton backButton = AppTheme.createSecondaryButton("Back to Bus Tickets");
        buttonPanel.add(addButton);
        buttonPanel.add(backButton);

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(centerPanel, BorderLayout.CENTER);
        panel.add(summaryArea, BorderLayout.WEST);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        add(panel);

        addButton.addActionListener(e -> {
            if (selectedBusSeatNumber == null) {
                JOptionPane.showMessageDialog(this, "Please select a bus seat.");
                return;
            }

            BusTicket busTicket = selectedSchedule.createTicket(String.valueOf(selectedBusSeatNumber));
            reservation.addBusTicket(busTicket);
            JOptionPane.showMessageDialog(this, "Bus ticket has been added to this flight reservation.");
            new ReservationFrame(authService, currentUser, selectedFlight, reservation);
            dispose();
        });

        backButton.addActionListener(e -> {
            new BusTicketFrame(authService, currentUser, selectedFlight, reservation, reservationService);
            dispose();
        });

        setVisible(true);
    }

    private void createBusSeatButtons() {
        busSeatPanel.removeAll();
        busSeatButtons.clear();

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.NONE;

        JLabel frontLabel = new JLabel("Front");
        frontLabel.setForeground(AppTheme.MUTED);
        frontLabel.setFont(new Font("Arial", Font.BOLD, 12));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 4;
        busSeatPanel.add(frontLabel, gbc);
        gbc.gridwidth = 1;

        for (int row = 0; row < 8; row++) {
            int leftSeat = row * 3 + 1;
            addBusSeatButton(leftSeat, 0, row + 1, gbc);
            addBusSeatButton(leftSeat + 1, 1, row + 1, gbc);
            addAisleSpacer(2, row + 1, gbc);
            addBusSeatButton(leftSeat + 2, 3, row + 1, gbc);
        }

        int lastRow = 9;
        addBusSeatButton(25, 0, lastRow, gbc);
        addBusSeatButton(26, 1, lastRow, gbc);
        addBusSeatButton(27, 2, lastRow, gbc);
        addBusSeatButton(28, 3, lastRow, gbc);
    }

    private void addBusSeatButton(int seatNumber, int gridX, int gridY, GridBagConstraints gbc) {
        JButton button = new JButton(String.valueOf(seatNumber));
        button.setPreferredSize(new Dimension(58, 42));
        button.setMinimumSize(new Dimension(58, 42));
        button.setMaximumSize(new Dimension(58, 42));
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.addActionListener(e -> selectBusSeat(seatNumber));

        busSeatButtons.put(seatNumber, button);
        gbc.gridx = gridX;
        gbc.gridy = gridY;
        busSeatPanel.add(button, gbc);
    }

    private void addAisleSpacer(int gridX, int gridY, GridBagConstraints gbc) {
        JLabel aisle = new JLabel(" ");
        aisle.setPreferredSize(new Dimension(34, 42));
        gbc.gridx = gridX;
        gbc.gridy = gridY;
        busSeatPanel.add(aisle, gbc);
    }

    private void selectBusSeat(int seatNumber) {
        if (isBusSeatOccupied(seatNumber)) {
            JOptionPane.showMessageDialog(this, "This bus seat is not available.");
            return;
        }
        selectedBusSeatNumber = seatNumber;
        refreshBusSeatButtons();
        updateSummary();
    }

    private void refreshBusSeatButtons() {
        for (int seatNumber = 1; seatNumber <= 28; seatNumber++) {
            JButton button = busSeatButtons.get(seatNumber);
            if (button == null) {
                continue;
            }

            boolean occupied = isBusSeatOccupied(seatNumber);
            boolean selected = selectedBusSeatNumber != null && selectedBusSeatNumber == seatNumber;
            button.setOpaque(true);
            button.setContentAreaFilled(true);
            button.setEnabled(!occupied);

            if (selected) {
                button.setBackground(AppTheme.BLUE);
                button.setForeground(Color.WHITE);
            } else if (occupied) {
                button.setBackground(new Color(226, 232, 240));
                button.setForeground(AppTheme.MUTED);
            } else {
                button.setBackground(Color.WHITE);
                button.setForeground(AppTheme.BLUE);
            }
            button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(selected ? AppTheme.BLUE : AppTheme.BORDER),
                    BorderFactory.createEmptyBorder(4, 6, 4, 6)
            ));
        }
    }

    private boolean isBusSeatOccupied(int seatNumber) {
        String currentSeat = reservation.hasBusTicket() ? reservation.getBusTicket().getSeatNumber() : null;
        String currentSchedule = reservation.hasBusTicket() ? reservation.getBusTicket().getSchedule().getScheduleId() : null;
        if (String.valueOf(seatNumber).equals(currentSeat)
                && selectedSchedule.getScheduleId().equals(currentSchedule)) {
            return false;
        }
        int seatHash = Math.floorMod((selectedSchedule.getScheduleId() + "-" + seatNumber).hashCode(), 100);
        return seatHash < 8;
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
        area.setPreferredSize(new Dimension(180, 120));
        return area;
    }

    private void updateSummary() {
        summaryArea.setText(
                "Route\n" +
                        selectedSchedule.getDepartureCity() + " -> " + selectedSchedule.getArrivalCity() + "\n\n" +
                        "Date: " + selectedSchedule.getDate() + "\n" +
                        "Time: " + selectedSchedule.getDepartureTime() + " -> " + selectedSchedule.getArrivalTime() + "\n" +
                        "Fare: " + String.format("%,.0f KRW", selectedSchedule.getFare()) + "\n" +
                        "Seat: " + (selectedBusSeatNumber == null ? "Not selected" : selectedBusSeatNumber)
        );
    }
}
