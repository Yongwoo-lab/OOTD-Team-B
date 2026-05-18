import java.util.ArrayList;
import java.util.List;

public class PaymentStrategyFactory {
    private final List<PaymentStrategy> strategies;

    public PaymentStrategyFactory() {
        strategies = new ArrayList<>();
        strategies.add(new KakaoPayPaymentStrategy());
        strategies.add(new NaverPayPaymentStrategy());
        strategies.add(new CreditCardPaymentStrategy());
        strategies.add(new TossPayPaymentStrategy());
    }

    public PaymentStrategy createStrategy(String method) {
        for (PaymentStrategy strategy : strategies) {
            if (strategy.getMethodName().equals(method)) {
                return strategy;
            }
        }
        return null;
    }
}
