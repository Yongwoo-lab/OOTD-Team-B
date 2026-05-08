import javax.swing.*;
import java.awt.*;

public class PaymentFrame extends JFrame {
    private final PaymentController paymentController;

    public PaymentFrame(AuthService authService, Customer currentUser, String selectedFlight, Reservation reservation, ReservationService reservationService) {
        this.paymentController = new PaymentController(reservationService);
        setTitle("Payment");
        setSize(580, 440);
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
                        "Selected Seat: " + (reservation.hasSelectedSeat() ? reservation.getSelectedSeatNumber() : "Not selected") + "\n" +
                        "User Name: " + currentUser.getName() + "\n" +
                        "User Type: " + currentUser.getUserType() + "\n"
        );

        JPanel formPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        formPanel.setOpaque(false);
        JComboBox<String> paymentMethodBox = new JComboBox<>();
        paymentMethodBox.addItem("Credit Card");
        paymentMethodBox.addItem("Bank Transfer");
        paymentMethodBox.addItem("KakaoPay");

        JTextField cardNumberField = new JTextField();
        AppTheme.styleComboBox(paymentMethodBox);
        AppTheme.styleField(cardNumberField);

        formPanel.add(new JLabel("Payment Method:"));
        formPanel.add(paymentMethodBox);
        formPanel.add(new JLabel("Card / Account Number:"));
        formPanel.add(cardNumberField);

        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setOpaque(false);
        centerPanel.add(infoArea, BorderLayout.CENTER);
        centerPanel.add(formPanel, BorderLayout.SOUTH);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        buttonPanel.setOpaque(false);
        JButton backButton = AppTheme.createSecondaryButton("Back to Seat Selection");
        JButton payButton = AppTheme.createPrimaryButton("Pay");
        buttonPanel.add(backButton);
        buttonPanel.add(payButton);

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(centerPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        add(panel);

        backButton.addActionListener(e -> {
            new SeatSelectionFrame(authService, currentUser, selectedFlight, reservation, reservationService);
            dispose();
        });

        payButton.addActionListener(e -> {
            if (!reservation.hasSelectedSeat()) {
                JOptionPane.showMessageDialog(this, "Please select a seat before payment.");
                new SeatSelectionFrame(authService, currentUser, selectedFlight, reservation, reservationService);
                dispose();
                return;
            }

            if (cardNumberField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter payment information.");
                return;
            }

            String selectedMethod = (String) paymentMethodBox.getSelectedItem();
            String paymentInfo = cardNumberField.getText().trim();
            Ticket ticket = paymentController.processPayment(reservation, selectedMethod, paymentInfo);
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

            JOptionPane.showMessageDialog(this,
                    "Payment completed successfully.\n" +
                            "Reservation ID: " + reservation.getReservationId() + "\n" +
                            "Reservation Status: " + reservation.getStatus() + "\n" +
                            "Seat Number: " + reservation.getSelectedSeatNumber() + "\n" +
                            "Ticket ID: " + ticket.getTicketId() + "\n" +
                            "Issue Date: " + ticket.getIssueDate());
            new MainFrame(authService);
            dispose();
        });

        setVisible(true);
    }
}
