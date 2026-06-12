public class MileageFlightFareDecorator extends FlightFareDecorator {
    private final int mileageToUse;

    public MileageFlightFareDecorator(FlightFare flightFare, int mileageToUse) {
        super(flightFare);
        this.mileageToUse = Math.max(0, mileageToUse);
    }

    @Override
    public double getAmount() {
        return Math.max(0, flightFare.getAmount() - mileageToUse);
    }

    @Override
    public String getDescription() {
        return flightFare.getDescription() + " + mileage discount";
    }

    public int getMileageToUse() {
        return mileageToUse;
    }
}
