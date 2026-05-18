public class Payment {
    private final String paymentId;
    private final double amount;
    private final String status;
    private final String failureReason;
    private final String method;
    private final double originalAmount;
    private final int mileageUsed;
    private final double discountAmount;

    public Payment(String paymentId, double amount, String status) {
        this(paymentId, amount, status, null, null);
    }

    public Payment(String paymentId, double amount, String status, String failureReason) {
        this(paymentId, amount, status, failureReason, null);
    }

    public Payment(String paymentId, double amount, String status, String failureReason, String method) {
        this(paymentId, amount, status, failureReason, method, amount, 0, 0);
    }

    public Payment(String paymentId, double amount, String status, String failureReason, String method,
                   double originalAmount, int mileageUsed, double discountAmount) {
        this.paymentId = paymentId;
        this.amount = amount;
        this.status = status;
        this.failureReason = failureReason;
        this.method = method;
        this.originalAmount = originalAmount;
        this.mileageUsed = mileageUsed;
        this.discountAmount = discountAmount;
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

    public String getMethod() {
        return method;
    }

    public double getOriginalAmount() {
        return originalAmount;
    }

    public int getMileageUsed() {
        return mileageUsed;
    }

    public double getDiscountAmount() {
        return discountAmount;
    }

    public boolean isSuccess() {
        return "SUCCESS".equals(status);
    }

    public String getFailureReason() {
        return failureReason;
    }

    public Payment withMileageDiscount(double originalAmount, int mileageUsed, double discountAmount) {
        return new Payment(paymentId, amount, status, failureReason, method, originalAmount, mileageUsed, discountAmount);
    }
}
