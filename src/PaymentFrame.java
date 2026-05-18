import javax.swing.*;
import java.awt.*;

public class PaymentFrame extends JFrame {
    private final PaymentController paymentController;
    private final MileageService mileageService = new MileageService();
    private final AuthorizationService authorizationService = new AuthorizationService();

    public PaymentFrame(AuthService authService, Customer currentUser, String selectedFlight, Reservation reservation, ReservationService reservationService) {
        this.paymentController = new PaymentController(reservationService);
        setTitle("Payment");
        setSize(680, 560);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = AppTheme.createPagePanel();

        JPanel headerPanel = new JPanel(new GridLayout(2, 1, 4, 4));
        headerPanel.setOpaque(false);
        headerPanel.add(AppTheme.createTitle("Payment"));
        headerPanel.add(AppTheme.createSubtitle("Confirm payment details and issue your ticket."));

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

        String flightInfo = selectedFlight;
        if (flightInfo == null || flightInfo.trim().isEmpty()) {
            flightInfo = "No flight selected.";
        }

        infoArea.setText(
                "Payment Information\n\n" +
                        "Selected Flight:\n" + flightInfo + "\n\n" +
                        "Optional Bus Ticket: " + createBusTicketText(reservation) + "\n" +
                        "Selected Seat: " + (reservation.hasSelectedSeat() ? reservation.getSelectedSeatNumber() : "Not selected") + "\n" +
                        "User Name: " + currentUser.getName() + "\n" +
                        "User Type: " + currentUser.getUserType() + "\n" +
                        "Flight Fare: " + String.format("%,.0f KRW", reservation.getFlightFare()) + "\n" +
                        "Bus Fare: " + String.format("%,.0f KRW", reservation.getBusFare()) + "\n" +
                        "Mileage discount applies to flight fare only."
        );

        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        formPanel.setOpaque(false);
        JComboBox<String> paymentMethodBox = new JComboBox<>();
        paymentMethodBox.addItem("KakaoPay");
        paymentMethodBox.addItem("NaverPay");
        paymentMethodBox.addItem("General Card");
        paymentMethodBox.addItem("TossPay");

        JTextField cardNumberField = new JTextField();
        JTextField mileageField = new JTextField("0");
        JLabel mileageBalanceLabel = new JLabel(mileageService.getMileageMessage(currentUser));
        JLabel finalAmountLabel = new JLabel();
        AppTheme.styleComboBox(paymentMethodBox);
        AppTheme.styleField(cardNumberField);
        AppTheme.styleField(mileageField);
        mileageField.setEnabled(authorizationService.canUseMileage(currentUser));
        refreshFinalAmountLabel(reservation, mileageField, finalAmountLabel);

        formPanel.add(new JLabel("Payment Method:"));
        formPanel.add(paymentMethodBox);
        formPanel.add(new JLabel("Payment Number:"));
        formPanel.add(cardNumberField);
        formPanel.add(new JLabel("Available Mileage:"));
        formPanel.add(mileageBalanceLabel);
        formPanel.add(new JLabel("Use Mileage:"));
        formPanel.add(mileageField);
        formPanel.add(new JLabel("Final Payment Amount:"));
        formPanel.add(finalAmountLabel);

        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setOpaque(false);
        centerPanel.add(infoArea, BorderLayout.CENTER);
        centerPanel.add(formPanel, BorderLayout.SOUTH);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        buttonPanel.setOpaque(false);
        JButton backButton = AppTheme.createSecondaryButton("Back to Seat Selection");
        JButton applyMileageButton = AppTheme.createSecondaryButton("Apply Mileage");
        JButton payButton = AppTheme.createPrimaryButton("Pay");
        buttonPanel.add(backButton);
        buttonPanel.add(applyMileageButton);
        buttonPanel.add(payButton);

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(centerPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        add(panel);

        backButton.addActionListener(e -> {
            new SeatSelectionFrame(authService, currentUser, selectedFlight, reservation, reservationService);
            dispose();
        });

        applyMileageButton.addActionListener(e -> refreshFinalAmountLabel(reservation, mileageField, finalAmountLabel));

        payButton.addActionListener(e -> {
            if (!reservation.hasSelectedSeat()) {
                JOptionPane.showMessageDialog(this, "Please select a seat before payment.");
                new SeatSelectionFrame(authService, currentUser, selectedFlight, reservation, reservationService);
                dispose();
                return;
            }

            int mileageToUse = parseMileage(mileageField.getText());
            if (!mileageService.canUseMileage(currentUser, mileageToUse, reservation.getFlightFare())) {
                JOptionPane.showMessageDialog(this, "Mileage amount is invalid. It cannot exceed your balance or the flight fare.");
                return;
            }

            double finalAmount = mileageService.calculateFinalAmount(reservation, mileageToUse);
            if (finalAmount > 0 && cardNumberField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter payment information.");
                return;
            }

            String selectedMethod = (String) paymentMethodBox.getSelectedItem();
            String paymentInfo = cardNumberField.getText().trim();
            Ticket ticket = paymentController.processPayment(reservation, selectedMethod, paymentInfo, mileageToUse, authService);
            if (ticket == null) {
                Payment failedPayment = paymentController.getLastPayment(reservation);
                String reason = failedPayment != null && failedPayment.getFailureReason() != null
                        ? failedPayment.getFailureReason()
                        : "Unknown reason";
                JOptionPane.showMessageDialog(this,
                        "Payment failed.\n" +
                                "Reason: " + reason + "\n" +
                                "Reservation Status: " + reservation.getStatus());
                return;
            }

            Payment payment = paymentController.getLastPayment(reservation);
            JOptionPane.showMessageDialog(this,
                    "Payment completed successfully.\n" +
                            "Reservation ID: " + reservation.getReservationId() + "\n" +
                            "Reservation Status: " + reservation.getStatus() + "\n" +
                            "Seat Number: " + reservation.getSelectedSeatNumber() + "\n" +
                            "Flight Fare: " + String.format("%,.0f KRW", reservation.getFlightFare()) + "\n" +
                            "Bus Fare: " + String.format("%,.0f KRW", reservation.getBusFare()) + "\n" +
                            "Mileage Used: " + (payment == null ? 0 : payment.getMileageUsed()) + "\n" +
                            "Paid Amount: " + String.format("%,.0f KRW", payment == null ? finalAmount : payment.getAmount()) + "\n" +
                            "Ticket ID: " + ticket.getTicketId() + "\n" +
                            "Issue Date: " + ticket.getIssueDate() + "\n" +
                            "Notification: " + reservation.getLastNotificationMessage());
            new ReservationHistoryFrame(authService, currentUser);
            dispose();
        });

        setVisible(true);
    }

    private String createBusTicketText(Reservation reservation) {
        if (reservation == null || !reservation.hasBusTicket()) {
            return "Not selected";
        }
        BusTicket busTicket = reservation.getBusTicket();
        BusSchedule schedule = busTicket.getSchedule();
        return schedule.getDepartureCity() + " -> " + schedule.getArrivalCity()
                + " / " + schedule.getDate() + " " + schedule.getDepartureTime()
                + " / Seat " + busTicket.getSeatNumber()
                + " / " + String.format("%,.0f KRW", busTicket.getFare());
    }

    private void refreshFinalAmountLabel(Reservation reservation, JTextField mileageField, JLabel finalAmountLabel) {
        int mileageToUse = parseMileage(mileageField.getText());
        double finalAmount = mileageService.calculateFinalAmount(reservation, mileageToUse);
        finalAmountLabel.setText(String.format("%,.0f KRW", finalAmount));
        finalAmountLabel.setForeground(AppTheme.BLUE);
        finalAmountLabel.setFont(new Font("Arial", Font.BOLD, 13));
    }

    private int parseMileage(String text) {
        try {
            if (text == null || text.trim().isEmpty()) {
                return 0;
            }
            return Math.max(0, Integer.parseInt(text.trim().replace(",", "")));
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
