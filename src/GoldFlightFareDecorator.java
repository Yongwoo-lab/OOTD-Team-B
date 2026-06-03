public class GoldFlightFareDecorator extends MemberTierFareDecorator {
    public GoldFlightFareDecorator(FlightFare flightFare) {
        super(flightFare, MemberTier.GOLD);
    }
}
