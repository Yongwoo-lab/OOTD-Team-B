public class KakaoPayPaymentStrategy extends AbstractPaymentStrategy {
    @Override
    public String getMethodName() {
        return "KakaoPay";
    }

    @Override
    protected boolean isValid(String digitsOnly) {
        return digitsOnly.length() == 10 || digitsOnly.length() == 11;
    }

    @Override
    protected String getInvalidMessage() {
        return "KakaoPay phone number must contain 10 or 11 digits.";
    }
}
