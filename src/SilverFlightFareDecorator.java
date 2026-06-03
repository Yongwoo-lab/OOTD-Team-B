public class SilverFlightFareDecorator extends MemberTierFareDecorator {
    public SilverFlightFareDecorator(FlightFare flightFare) {
        super(flightFare, MemberTier.SILVER);
    }
}
