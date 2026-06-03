public class BasicFlightFare implements FlightFare {
    private final double amount;

    public BasicFlightFare(double amount) {
        this.amount = Math.max(0, amount);
    }

    @Override
    public double getAmount() {
        return amount;
    }

    @Override
    public String getDescription() {
        return "Basic flight fare";
    }
}
