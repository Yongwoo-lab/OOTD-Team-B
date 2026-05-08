public interface SeatState {
    String getName();

    boolean canBeSelected();

    boolean isSelected();

    boolean isOccupied();
}
