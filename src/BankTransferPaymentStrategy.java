public class BankTransferPaymentStrategy extends AbstractPaymentStrategy {
    @Override
    public String getMethodName() {
        return "Bank Transfer";
    }

    @Override
    protected boolean isValid(String digitsOnly) {
        return digitsOnly.length() >= 10 && digitsOnly.length() <= 14;
    }

    @Override
    protected String getInvalidMessage() {
        return "Bank account number must contain 10 to 14 digits.";
    }
}
