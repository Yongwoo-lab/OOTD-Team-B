public abstract class FlightFareDecorator implements FlightFare {
    protected final FlightFare flightFare;

    protected FlightFareDecorator(FlightFare flightFare) {
        this.flightFare = flightFare;
    }

    @Override
    public String getDescription() {
        return flightFare.getDescription();
    }
}
