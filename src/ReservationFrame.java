import javax.swing.*;
import java.awt.*;

public class ReservationFrame extends JFrame {
    private final ReservationController reservationController = new ReservationController();

    public ReservationFrame(AuthService authService, Customer currentUser, Flight selectedFlight) {
        this(authService, currentUser, selectedFlight, null);
    }

    public ReservationFrame(AuthService authService, Customer currentUser, Flight selectedFlight, Reservation existingReservation) {
        setTitle("Reservation");
        setSize(560, 410);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = AppTheme.createPagePanel();

        JPanel headerPanel = new JPanel(new GridLayout(2, 1, 4, 4));
        headerPanel.setOpaque(false);
        headerPanel.add(AppTheme.createTitle("Reservation"));
        headerPanel.add(AppTheme.createSubtitle("Review your selected flight before choosing a seat."));

        JTextArea infoArea = new JTextArea();
        infoArea.setEditable(false);
        infoArea.setLineWrap(true);
        infoArea.setWrapStyleWord(true);
        infoArea.setFont(new Font("Arial", Font.PLAIN, 13));
        infoArea.setBackground(Color.WHITE);
        infoArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.BORDER),
                BorderFactory.createEmptyBorder(14, 14, 14, 14)
        ));

        Reservation reservation = existingReservation != null
                ? existingReservation
                : reservationController.bookFlight(currentUser, selectedFlight);
        Flight flight = reservation == null ? selectedFlight : reservation.getFlight();
        String flightInfo = formatFlight(flight);

        if (reservation == null) {
            JOptionPane.showMessageDialog(this, "Reservation requires member login.");
            new MainFrame(authService);
            dispose();
            return;
        }

        String busTicketText = "Not selected";
        if (reservation.hasBusTicket()) {
            BusTicket busTicket = reservation.getBusTicket();
            busTicketText = busTicket.getRouteText() + " / " + String.format("%,.0f KRW", busTicket.getFare());
        }

        infoArea.setText(
                "Basic reservation ready.\n\n" +
                        "Reservation ID: " + reservation.getReservationId() + "\n" +
                        "Reservation Status: " + reservation.getStatus() + "\n" +
                        "Selected Seat: " + (reservation.hasSelectedSeat() ? reservation.getSelectedSeatNumber() : "Not selected") + "\n" +
                        "Selected Flight:\n" + flightInfo + "\n\n" +
                        "Optional Bus Ticket: " + busTicketText + "\n" +
                        "User Name: " + currentUser.getName() + "\n" +
                        "User Type: " + currentUser.getUserType() + "\n" +
                        "Flight Fare: " + String.format("%,.0f KRW", reservation.getFlightFare()) + "\n" +
                        "Bus Fare: " + String.format("%,.0f KRW", reservation.getBusFare()) + "\n" +
                        "Total Before Mileage: " + String.format("%,.0f KRW", reservation.getTotalFare()) + "\n"
        );

        JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        buttonPanel.setOpaque(false);
        JButton backButton = AppTheme.createSecondaryButton("Back to Search");
        JButton busButton = AppTheme.createSecondaryButton(reservation.hasBusTicket() ? "Change Bus Ticket" : "Add Bus Ticket");
        JButton nextButton = AppTheme.createPrimaryButton("Choose Seat");
        buttonPanel.add(backButton);
        buttonPanel.add(busButton);
        buttonPanel.add(nextButton);

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(infoArea, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        add(panel);

        backButton.addActionListener(e -> {
            new SearchFlightFrame(authService, currentUser);
            dispose();
        });

        busButton.addActionListener(e -> {
            new BusTicketFrame(authService, currentUser, flight, reservation, reservationController.getReservationService());
            dispose();
        });

        nextButton.addActionListener(e -> {
            new SeatSelectionFrame(authService, currentUser, flight, reservation, reservationController.getReservationService());
            dispose();
        });

        setVisible(true);
    }

    private String formatFlight(Flight flight) {
        if (flight == null) {
            return "No flight selected.";
        }
        return String.format("%s | %s -> %s | %s %s -> %s | %,.0f KRW",
                flight.getFlightId(),
                flight.getDeparture(),
                flight.getArrival(),
                flight.getDate(),
                flight.getDepartureTime(),
                flight.getArrivalTime(),
                flight.getPrice());
    }
}
