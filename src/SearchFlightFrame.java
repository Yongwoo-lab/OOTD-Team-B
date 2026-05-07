import javax.swing.*;
import java.awt.*;
import java.util.List;

public class SearchFlightFrame extends JFrame {
    private static final String FLIGHT_PLACEHOLDER = "-- Select destination or date first --";
    private static final String NO_MATCHING_FLIGHTS = "-- No matching flights --";

    private AuthService authService;
    private Customer currentUser;
    private MileageService mileageService = new MileageService();
    private List<Flight> flights;

    public SearchFlightFrame(AuthService authService, Customer currentUser) {
        this.authService = authService;
        this.currentUser = currentUser;
        this.flights = new FlightDatabase().loadFlights();

        setTitle("Korean Air Booking");
        setSize(860, 560);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = AppTheme.createPagePanel();
        panel.add(createTopBar(), BorderLayout.NORTH);
        panel.add(createBookingPanel(), BorderLayout.CENTER);

        add(panel);
        setVisible(true);
    }

    private JPanel createTopBar() {
        JPanel topBar = new JPanel(new BorderLayout(16, 16));
        topBar.setOpaque(false);

        JPanel titlePanel = new JPanel(new GridLayout(2, 1, 2, 2));
        titlePanel.setOpaque(false);

        JLabel brandLabel = new JLabel("Korean Air Booking");
        brandLabel.setForeground(AppTheme.NAVY);
        brandLabel.setFont(new Font("Arial", Font.BOLD, 24));

        JLabel subtitleLabel = new JLabel("Depart from Incheon and find a flight that fits your schedule.");
        subtitleLabel.setForeground(AppTheme.MUTED);
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 13));

        titlePanel.add(brandLabel);
        titlePanel.add(subtitleLabel);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionPanel.setOpaque(false);

        JLabel mileageLabel = AppTheme.createBadge(createMileageText());
        JButton myPageButton = AppTheme.createSecondaryButton("My Page");
        myPageButton.setEnabled(!(currentUser instanceof Guest));

        actionPanel.add(mileageLabel);
        actionPanel.add(myPageButton);

        topBar.add(titlePanel, BorderLayout.CENTER);
        topBar.add(actionPanel, BorderLayout.EAST);

        myPageButton.addActionListener(e -> {
            openMyPageAfterPasswordCheck();
        });

        return topBar;
    }

    private void openMyPageAfterPasswordCheck() {
        JPasswordField passwordField = new JPasswordField();
        AppTheme.styleField(passwordField);

        JPanel panel = new JPanel(new GridLayout(1, 2, 10, 10));
        panel.add(new JLabel("Password"));
        panel.add(passwordField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Confirm Password", JOptionPane.OK_CANCEL_OPTION);
        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        String password = new String(passwordField.getPassword());
        if (!currentUser.checkPassword(password)) {
            JOptionPane.showMessageDialog(this, "Password is incorrect.");
            return;
        }

        new UserInfoFrame(authService, currentUser);
        dispose();
    }

    private JPanel createBookingPanel() {
        JPanel bookingPanel = AppTheme.createCardPanel();
        bookingPanel.setLayout(new BorderLayout(18, 18));

        JPanel accentBar = new JPanel(new GridLayout(1, 2));
        accentBar.setPreferredSize(new Dimension(10, 6));
        JPanel blueBar = new JPanel();
        JPanel redBar = new JPanel();
        blueBar.setBackground(AppTheme.BLUE);
        redBar.setBackground(AppTheme.RED);
        accentBar.add(blueBar);
        accentBar.add(redBar);

        JPanel formPanel = new JPanel(new GridLayout(4, 2, 12, 12));
        formPanel.setOpaque(false);

        JComboBox<String> departureBox = new JComboBox<>();
        departureBox.addItem("Incheon International Airport (ICN)");
        AppTheme.styleComboBox(departureBox);

        JComboBox<String> destinationBox = new JComboBox<>();
        destinationBox.addItem("-- All Destinations --");
        java.util.LinkedHashSet<String> destinations = new java.util.LinkedHashSet<>();
        for (Flight flight : flights) {
            destinations.add(flight.getArrival());
        }
        for (String destination : destinations) {
            destinationBox.addItem(destination);
        }

        JComboBox<String> dateBox = new JComboBox<>();
        dateBox.addItem("-- All Dates --");
        java.util.LinkedHashSet<String> dates = new java.util.LinkedHashSet<>();
        for (Flight flight : flights) {
            dates.add(flight.getDate());
        }
        for (String date : dates) {
            dateBox.addItem(date);
        }

        JComboBox<String> flightBox = new JComboBox<>();
        showFlightPlaceholder(flightBox);

        AppTheme.styleComboBox(destinationBox);
        AppTheme.styleComboBox(dateBox);
        AppTheme.styleComboBox(flightBox);

        formPanel.add(createFormLabel("Departure Airport"));
        formPanel.add(departureBox);
        formPanel.add(createFormLabel("Destination"));
        formPanel.add(destinationBox);
        formPanel.add(createFormLabel("Date"));
        formPanel.add(dateBox);
        formPanel.add(createFormLabel("Available Flights"));
        formPanel.add(flightBox);

        JTextArea summaryArea = createSummaryArea();
        showEmptySummary(summaryArea);

        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 18, 18));
        centerPanel.setOpaque(false);
        centerPanel.add(formPanel);
        centerPanel.add(summaryArea);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);

        JButton searchButton = AppTheme.createSecondaryButton("Search");
        JButton bookButton = AppTheme.createPrimaryButton("Book Selected Flight");

        buttonPanel.add(searchButton);
        buttonPanel.add(bookButton);

        bookingPanel.add(accentBar, BorderLayout.NORTH);
        bookingPanel.add(centerPanel, BorderLayout.CENTER);
        bookingPanel.add(buttonPanel, BorderLayout.SOUTH);

        flightBox.addActionListener(e -> updateFlightSummary(summaryArea, (String) flightBox.getSelectedItem()));

        destinationBox.addActionListener(e -> applyFlightFilter(destinationBox, dateBox, flightBox, summaryArea));
        dateBox.addActionListener(e -> applyFlightFilter(destinationBox, dateBox, flightBox, summaryArea));

        searchButton.addActionListener(e -> {
            applyFlightFilter(destinationBox, dateBox, flightBox, summaryArea);
            showNoFlightMessageIfNeeded(flightBox);
        });

        bookButton.addActionListener(e -> {
            String selectedFlight = (String) flightBox.getSelectedItem();
            if (!isSelectableFlight(selectedFlight)) {
                JOptionPane.showMessageDialog(this, "Please search and select a flight first.");
                return;
            }

            if (currentUser == null || currentUser instanceof Guest) {
                JOptionPane.showMessageDialog(this, "Please login before booking.");
                new MainFrame(authService);
                dispose();
                return;
            }

            new ReservationFrame(authService, currentUser, selectedFlight);
            dispose();
        });

        return bookingPanel;
    }

    private void applyFlightFilter(JComboBox<String> destinationBox, JComboBox<String> dateBox,
                                   JComboBox<String> flightBox, JTextArea summaryArea) {
        String selectedDestination = normalizeDestination((String) destinationBox.getSelectedItem());
        String selectedDate = normalizeDate((String) dateBox.getSelectedItem());

        if (selectedDestination == null && selectedDate == null) {
            showFlightPlaceholder(flightBox);
            showEmptySummary(summaryArea);
            return;
        }

        populateFlights(flightBox, selectedDestination, selectedDate);
        updateFlightSummary(summaryArea, (String) flightBox.getSelectedItem());
    }

    private void showNoFlightMessageIfNeeded(JComboBox<String> flightBox) {
        if (flightBox.getItemCount() == 1 && NO_MATCHING_FLIGHTS.equals(flightBox.getItemAt(0))) {
            JOptionPane.showMessageDialog(this, "No matching flights found.\nTry a different date or destination.");
        }
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
                BorderFactory.createEmptyBorder(14, 14, 14, 14)
        ));
        return summaryArea;
    }

    private JLabel createFormLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(AppTheme.NAVY);
        label.setFont(new Font("Arial", Font.BOLD, 13));
        return label;
    }

    private String createMileageText() {
        if (currentUser instanceof SkyPassMember) {
            return "Mileage  " + mileageService.getMileageMessage(currentUser);
        }
        if (currentUser instanceof Guest) {
            return "Guest Mode";
        }
        return "Mileage  SkyPass only";
    }

    private void populateFlights(JComboBox<String> flightBox, String destination, String date) {
        flightBox.removeAllItems();
        for (Flight flight : flights) {
            boolean matchDestination = (destination == null) || flight.getArrival().equals(destination);
            boolean matchDate = (date == null) || flight.getDate().equals(date);
            if (matchDestination && matchDate) {
                flightBox.addItem(formatFlight(flight));
            }
        }

        if (flightBox.getItemCount() == 0) {
            flightBox.addItem(NO_MATCHING_FLIGHTS);
        }
    }

    private void showFlightPlaceholder(JComboBox<String> flightBox) {
        flightBox.removeAllItems();
        flightBox.addItem(FLIGHT_PLACEHOLDER);
    }

    private String normalizeDestination(String destination) {
        if (destination == null || "-- All Destinations --".equals(destination)) {
            return null;
        }
        return destination;
    }

    private String normalizeDate(String date) {
        if (date == null || "-- All Dates --".equals(date)) {
            return null;
        }
        return date;
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

    private void updateFlightSummary(JTextArea summaryArea, String selectedFlight) {
        if (!isSelectableFlight(selectedFlight)) {
            showEmptySummary(summaryArea);
            return;
        }

        summaryArea.setText(
                "Selected Flight\n\n" +
                        selectedFlight + "\n\n" +
                        "Passenger: " + currentUser.getName() + "\n" +
                        "Member Type: " + currentUser.getUserType() + "\n" +
                        createMileageText()
        );
    }

    private void showEmptySummary(JTextArea summaryArea) {
        summaryArea.setText("No flight selected.\n\nChoose a destination or date to see available flights.");
    }

    private boolean isSelectableFlight(String selectedFlight) {
        return selectedFlight != null
                && !FLIGHT_PLACEHOLDER.equals(selectedFlight)
                && !NO_MATCHING_FLIGHTS.equals(selectedFlight);
    }
}
