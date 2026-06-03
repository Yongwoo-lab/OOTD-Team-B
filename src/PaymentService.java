public class PaymentService {
    private final PaymentStrategyFactory strategyFactory = new PaymentStrategyFactory();

    public Payment processPayment(double amount, String method, String accountNo) {
        PaymentStrategy strategy = strategyFactory.createStrategy(method);
        if (strategy == null) {
            return createFailedPayment(amount, "Unsupported payment method.");
        }
        return strategy.pay(amount, accountNo);
    }

    public Payment createFailedPayment(double amount, String failureReason) {
        String paymentId = "P-" + java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return new Payment(paymentId, amount, "FAILED", failureReason);
    }

    public Refund processRefund(Payment payment) {
        if (payment == null || !payment.isSuccess()) {
            return new Refund(null, 0, RefundStatus.FAILED, "Only successful payments can be refunded.");
        }
        if (payment.getAmount() < 0) {
            return new Refund(payment.getPaymentId(), payment.getAmount(), RefundStatus.FAILED, "Invalid refund amount.");
        }
        return new Refund(payment.getPaymentId(), payment.getAmount(), RefundStatus.COMPLETED, "Refund completed.");
    }
}
