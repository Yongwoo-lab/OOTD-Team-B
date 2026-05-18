public interface PaymentStrategy {
    String getMethodName();

    Payment pay(double amount, String paymentInfo);
}
