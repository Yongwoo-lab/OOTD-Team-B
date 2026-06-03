public class MemberFareDiscountService {
    public FlightFare createFare(Customer customer, double flightFare) {
        FlightFare fare = new BasicFlightFare(flightFare);
        MemberTier tier = getEligibleTier(customer);

        switch (tier) {
            case SILVER:
                return new SilverFlightFareDecorator(fare);
            case GOLD:
                return new GoldFlightFareDecorator(fare);
            case PLATINUM:
                return new PlatinumFlightFareDecorator(fare);
            case DIAMOND:
                return new DiamondFlightFareDecorator(fare);
            case BASIC:
            default:
                return fare;
        }
    }

    public double calculateDiscountedFlightFare(Customer customer, double flightFare) {
        return createFare(customer, flightFare).getAmount();
    }

    public double calculateMemberDiscount(Customer customer, double flightFare) {
        return Math.max(0, flightFare - calculateDiscountedFlightFare(customer, flightFare));
    }

    public MemberTier getEligibleTier(Customer customer) {
        if (customer == null || customer instanceof Guest || customer instanceof Admin) {
            return MemberTier.BASIC;
        }
        return customer.getMemberTier();
    }

    public String getDiscountMessage(Customer customer, double flightFare) {
        MemberTier tier = getEligibleTier(customer);
        double discount = calculateMemberDiscount(customer, flightFare);
        return tier.getDisplayName() + " / " + tier.getDiscountRateText()
                + " / " + String.format("%,.0f KRW", discount);
    }
}
