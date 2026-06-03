public class DiamondFlightFareDecorator extends MemberTierFareDecorator {
    public DiamondFlightFareDecorator(FlightFare flightFare) {
        super(flightFare, MemberTier.DIAMOND);
    }
}
