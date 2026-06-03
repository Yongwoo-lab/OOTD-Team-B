public enum MemberTier {
    BASIC("BASIC", "Basic", 0.00),
    SILVER("SILVER", "Silver", 0.05),
    GOLD("GOLD", "Gold", 0.10),
    PLATINUM("PLATINUM", "Platinum", 0.15),
    DIAMOND("DIAMOND", "Diamond", 0.20);

    private final String storageName;
    private final String displayName;
    private final double flightDiscountRate;

    MemberTier(String storageName, String displayName, double flightDiscountRate) {
        this.storageName = storageName;
        this.displayName = displayName;
        this.flightDiscountRate = flightDiscountRate;
    }

    public String getStorageName() {
        return storageName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public double getFlightDiscountRate() {
        return flightDiscountRate;
    }

    public String getDiscountRateText() {
        return String.format("%.0f%%", flightDiscountRate * 100);
    }

    public static MemberTier fromStorageValue(String value) {
        if (value == null || value.trim().isEmpty()) {
            return BASIC;
        }

        String normalizedValue = value.trim().toUpperCase();
        for (MemberTier tier : values()) {
            if (tier.storageName.equals(normalizedValue) || tier.name().equals(normalizedValue)) {
                return tier;
            }
        }
        return BASIC;
    }

    @Override
    public String toString() {
        return displayName + " (" + getDiscountRateText() + ")";
    }
}
