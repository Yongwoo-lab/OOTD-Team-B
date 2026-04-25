import javax.swing.*;
import java.awt.*;

public class SearchFlightFrame extends JFrame {
    private AuthService authService;
    private Customer currentUser;

    public SearchFlightFrame(AuthService authService, Customer currentUser) {
        this.authService = authService;
        this.currentUser = currentUser;

        setTitle("Search Flights");
        setSize(500, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(25, 35, 25, 35));

        JLabel titleLabel = new JLabel("Search Flights", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));

        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));

        JTextField departureField = new JTextField();
        JTextField arrivalField = new JTextField();

        JComboBox<String> flightBox = new JComboBox<>();
        flightBox.addItem("KE101 | Seoul -> Tokyo | 300,000 KRW");
        flightBox.addItem("KE202 | Seoul -> Paris | 1,200,000 KRW");
        flightBox.addItem("KE303 | Seoul -> London | 1,100,000 KRW");

        formPanel.add(new JLabel("Departure:"));
        formPanel.add(departureField);
        formPanel.add(new JLabel("Arrival:"));
        formPanel.add(arrivalField);
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
            JOptionPane.showMessageDialog(this, "Flight search completed.");
        });

        bookButton.addActionListener(e -> {
            String selectedFlight = (String) flightBox.getSelectedItem();

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
}