public enum ReservationStatus {
    PENDING("PENDING"),
    SEAT_SELECTED("SEAT_SELECTED"),
    CONFIRMED("CONFIRMED"),
    CANCELLED("CANCELLED");

    private final String label;

    ReservationStatus(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }
}
