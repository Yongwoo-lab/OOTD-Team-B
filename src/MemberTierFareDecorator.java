public abstract class MemberTierFareDecorator extends FlightFareDecorator {
    private final MemberTier memberTier;

    protected MemberTierFareDecorator(FlightFare flightFare, MemberTier memberTier) {
        super(flightFare);
        this.memberTier = memberTier == null ? MemberTier.BASIC : memberTier;
    }

    @Override
    public double getAmount() {
        return flightFare.getAmount() * (1 - memberTier.getFlightDiscountRate());
    }

    @Override
    public String getDescription() {
        return flightFare.getDescription() + " + " + memberTier.getDisplayName() + " member discount";
    }

    public MemberTier getMemberTier() {
        return memberTier;
    }
}
