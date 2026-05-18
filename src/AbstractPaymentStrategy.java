import java.util.UUID;

public abstract class AbstractPaymentStrategy implements PaymentStrategy {
    @Override
    public Payment pay(double amount, String paymentInfo) {
        String paymentId = createPaymentId();
        if (amount < 0) {
            return new Payment(paymentId, amount, "FAILED", "Invalid payment amount.", getMethodName());
        }
        if (amount == 0) {
            return new Payment(paymentId, amount, "SUCCESS", null, getMethodName());
        }

        String digitsOnly = paymentInfo == null ? "" : paymentInfo.replaceAll("\\D", "");
        if (!isValid(digitsOnly)) {
            return new Payment(paymentId, amount, "FAILED", getInvalidMessage(), getMethodName());
        }

        // Deterministic demo failure rule: account ending with 0 is rejected.
        if (digitsOnly.endsWith("0")) {
            return new Payment(paymentId, amount, "FAILED", "Payment declined by issuer.", getMethodName());
        }

        return new Payment(paymentId, amount, "SUCCESS", null, getMethodName());
    }

    protected abstract boolean isValid(String digitsOnly);

    protected abstract String getInvalidMessage();

    private String createPaymentId() {
        return "P-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
