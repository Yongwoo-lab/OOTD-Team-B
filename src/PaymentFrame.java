import javax.swing.*;
import java.awt.*;

public class PaymentFrame extends JFrame {
    public PaymentFrame(AuthService authService, Customer currentUser, String selectedFlight) {
        setTitle("Payment");
        setSize(500, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(25, 35, 25, 35));

        JLabel titleLabel = new JLabel("Payment Step", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));

        JTextArea infoArea = new JTextArea();
        infoArea.setEditable(false);
        infoArea.setLineWrap(true);
        infoArea.setWrapStyleWord(true);

        String flightInfo = selectedFlight;
        if (flightInfo == null || flightInfo.trim().isEmpty()) {
            flightInfo = "No flight selected.";
        }

        infoArea.setText(
                "Payment Information\n\n" +
                        "Selected Flight:\n" + flightInfo + "\n\n" +
                        "User Name: " + currentUser.getName() + "\n" +
                        "User Type: " + currentUser.getUserType() + "\n"
        );

        JPanel formPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        JComboBox<String> paymentMethodBox = new JComboBox<>();
        paymentMethodBox.addItem("Credit Card");
        paymentMethodBox.addItem("Bank Transfer");
        paymentMethodBox.addItem("KakaoPay");

        JTextField cardNumberField = new JTextField();

        formPanel.add(new JLabel("Payment Method:"));
        formPanel.add(paymentMethodBox);
        formPanel.add(new JLabel("Card / Account Number:"));
        formPanel.add(cardNumberField);

        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.add(infoArea, BorderLayout.CENTER);
        centerPanel.add(formPanel, BorderLayout.SOUTH);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        JButton backButton = new JButton("Back to Reservation");
        JButton payButton = new JButton("Pay");
        buttonPanel.add(backButton);
        buttonPanel.add(payButton);

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(centerPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        add(panel);

        backButton.addActionListener(e -> {
            new ReservationFrame(authService, currentUser, selectedFlight);
            dispose();
        });

        payButton.addActionListener(e -> {
            if (cardNumberField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter payment information.");
                return;
            }
            JOptionPane.showMessageDialog(this, "Payment completed.");
        });

        setVisible(true);
    }
}
