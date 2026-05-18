public class BookingPaymentRequest {
    private final String method;
    private final String paymentInfo;
    private final int mileageToUse;
    private final AuthService authService;
    private double originalAmount;
    private double finalAmount;
    private double discountAmount;

    public BookingPaymentRequest(String method, String paymentInfo, int mileageToUse, AuthService authService) {
        this.method = method;
        this.paymentInfo = paymentInfo;
        this.mileageToUse = Math.max(0, mileageToUse);
        this.authService = authService;
    }

    public String getMethod() {
        return method;
    }

    public String getPaymentInfo() {
        return paymentInfo;
    }

    public int getMileageToUse() {
        return mileageToUse;
    }

    public AuthService getAuthService() {
        return authService;
    }

    public double getOriginalAmount() {
        return originalAmount;
    }

    public double getFinalAmount() {
        return finalAmount;
    }

    public double getDiscountAmount() {
        return discountAmount;
    }

    public void setAmountSummary(double originalAmount, double finalAmount, double discountAmount) {
        this.originalAmount = originalAmount;
        this.finalAmount = finalAmount;
        this.discountAmount = discountAmount;
    }
}
