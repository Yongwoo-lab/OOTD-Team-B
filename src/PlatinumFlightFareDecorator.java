public class PlatinumFlightFareDecorator extends MemberTierFareDecorator {
    public PlatinumFlightFareDecorator(FlightFare flightFare) {
        super(flightFare, MemberTier.PLATINUM);
    }
}
