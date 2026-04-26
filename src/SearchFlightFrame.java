import javax.swing.*;
import java.awt.*;

public class SearchFlightFrame extends JFrame {
    private AuthService authService;
    private Customer currentUser;

    // {항공편번호, 목적지, 날짜, 출발시간, 도착시간, 가격}
    private static final String[][] FLIGHTS = {
        {"KE701", "Tokyo", "2026-04-26", "07:30", "09:50", "280,000"},
        {"KE702", "Tokyo", "2026-04-27", "13:00", "15:20", "310,000"},
        {"KE703", "Tokyo", "2026-04-28", "19:45", "22:05", "295,000"},
        {"KE704", "Tokyo", "2026-04-29", "22:10", "00:30", "285,000"},

        {"KE081", "New York", "2026-04-26", "09:00", "10:50", "1,300,000"},
        {"KE082", "New York", "2026-04-28", "15:30", "17:20", "1,250,000"},
        {"KE083", "New York", "2026-04-30", "20:00", "21:50", "1,320,000"},
        {"KE084", "New York", "2026-05-02", "23:40", "01:30", "1,280,000"},

        {"KE901", "Paris", "2026-04-27", "06:30", "13:20", "1,100,000"},
        {"KE902", "Paris", "2026-04-29", "11:30", "18:20", "1,050,000"},
        {"KE903", "Paris", "2026-05-01", "17:15", "00:05", "1,120,000"},
        {"KE904", "Paris", "2026-05-03", "23:00", "05:50", "1,080,000"},
    };

    public SearchFlightFrame(AuthService authService, Customer currentUser) {
        this.authService = authService;
        this.currentUser = currentUser;

        setTitle("Search Flights");
        setSize(560, 420);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(25, 35, 25, 35));

        JLabel titleLabel = new JLabel("Search Flights", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));

        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));

        // 1. 나라 선택 (중복 제거)
        JComboBox<String> countryBox = new JComboBox<>();
        countryBox.addItem("-- All Destinations --");
        java.util.LinkedHashSet<String> destinations = new java.util.LinkedHashSet<>();
        for (String[] f : FLIGHTS) destinations.add(f[1]);
        for (String d : destinations) countryBox.addItem(d);

        // 2. 날짜 선택 (중복 제거)
        JComboBox<String> dateBox = new JComboBox<>();
        dateBox.addItem("-- All Dates --");
        java.util.LinkedHashSet<String> dates = new java.util.LinkedHashSet<>();
        for (String[] f : FLIGHTS) dates.add(f[2]);
        for (String d : dates) dateBox.addItem(d);

        // Available Flights
        JComboBox<String> flightBox = new JComboBox<>();
        populateFlights(flightBox, null, null);

        formPanel.add(new JLabel("Destination:"));
        formPanel.add(countryBox);
        formPanel.add(new JLabel("Date:"));
        formPanel.add(dateBox);
        formPanel.add(new JLabel("Available Flights:"));
        formPanel.add(flightBox);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        JButton searchButton = new JButton("Search");
        JButton bookButton = new JButton("Book Selected Flight");
        buttonPanel.add(searchButton);
        buttonPanel.add(bookButton);

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(formPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        add(panel);

        searchButton.addActionListener(e -> {
            String selectedCountry = (String) countryBox.getSelectedItem();
            String selectedDate = (String) dateBox.getSelectedItem();

            if ("-- All Destinations --".equals(selectedCountry)) {
                selectedCountry = null;
            }
            if ("-- All Dates --".equals(selectedDate)) {
                selectedDate = null;
            }

            populateFlights(flightBox, selectedCountry, selectedDate);

            if (flightBox.getItemCount() == 0) {
                JOptionPane.showMessageDialog(this, "No matching flights found.\nTry a different date or destination.");
            }
        });

        bookButton.addActionListener(e -> {
            String selectedFlight = (String) flightBox.getSelectedItem();
            if (selectedFlight == null) {
                JOptionPane.showMessageDialog(this, "Please search and select a flight first.");
                return;
            }
            if (currentUser == null || currentUser instanceof Guest) {
                JOptionPane.showMessageDialog(this, "Please login before booking.");
                new LoginFrame(authService, user -> {
                    new ReservationFrame(authService, user, selectedFlight);
                });
                dispose();
            } else {
                new ReservationFrame(authService, currentUser, selectedFlight);
                dispose();
            }
        });

        setVisible(true);
    }

    private void populateFlights(JComboBox<String> flightBox, String country, String date) {
        flightBox.removeAllItems();
        for (String[] f : FLIGHTS) {
            // f = {항공편번호, 목적지, 날짜, 출발시간, 도착시간, 가격}
            boolean matchCountry = (country == null) || f[1].equals(country);
            boolean matchDate    = (date == null)    || f[2].equals(date);
            if (matchCountry && matchDate) {
                flightBox.addItem(String.format("%s | Seoul -> %-10s | %s %s -> %s | %s KRW",
                        f[0], f[1], f[2], f[3], f[4], f[5]));
            }
        }
    }
}
