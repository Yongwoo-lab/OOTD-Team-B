public class CreditCardPaymentStrategy extends AbstractPaymentStrategy {
    @Override
    public String getMethodName() {
        return "Credit Card";
    }

    @Override
    protected boolean isValid(String digitsOnly) {
        return digitsOnly.length() == 16;
    }

    @Override
    protected String getInvalidMessage() {
        return "Credit card number must contain 16 digits.";
    }
}
