public class Payment {
    private final String paymentId;
    private final double amount;
    private final String status;
    private final String failureReason;

    public Payment(String paymentId, double amount, String status) {
        this(paymentId, amount, status, null);
    }

    public Payment(String paymentId, double amount, String status, String failureReason) {
        this.paymentId = paymentId;
        this.amount = amount;
        this.status = status;
        this.failureReason = failureReason;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public double getAmount() {
        return amount;
    }

    public String getStatus() {
        return status;
    }

    public boolean isSuccess() {
        return "SUCCESS".equals(status);
    }

    public String getFailureReason() {
        return failureReason;
    }
}
