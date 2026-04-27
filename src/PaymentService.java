import java.util.UUID;

public class PaymentService {
    public Payment processPayment(double amount, String method, String accountNo) {
        String paymentId = "P-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        if (amount <= 0) {
            return new Payment(paymentId, amount, "FAILED", "Invalid payment amount.");
        }

        String digitsOnly = accountNo == null ? "" : accountNo.replaceAll("\\D", "");
        if (!isValidPaymentInfo(method, digitsOnly)) {
            return new Payment(paymentId, amount, "FAILED", "Invalid account/card format for selected method.");
        }

        // Deterministic demo failure rule: account ending with 0 is rejected.
        if (digitsOnly.endsWith("0")) {
            return new Payment(paymentId, amount, "FAILED", "Payment declined by issuer.");
        }

        return new Payment(paymentId, amount, "SUCCESS");
    }

    public boolean processRefund(String paymentId) {
        return paymentId != null && !paymentId.trim().isEmpty();
    }

    private boolean isValidPaymentInfo(String method, String digitsOnly) {
        if ("Credit Card".equals(method)) {
            return digitsOnly.length() == 16;
        }
        if ("Bank Transfer".equals(method)) {
            return digitsOnly.length() >= 10 && digitsOnly.length() <= 14;
        }
        if ("KakaoPay".equals(method)) {
            return digitsOnly.length() == 10 || digitsOnly.length() == 11;
        }
        return false;
    }
}
