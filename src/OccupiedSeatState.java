public class OccupiedSeatState implements SeatState {
    @Override
    public String getName() {
        return "OCCUPIED";
    }

    @Override
    public boolean canBeSelected() {
        return false;
    }

    @Override
    public boolean isSelected() {
        return false;
    }

    @Override
    public boolean isOccupied() {
        return true;
    }
}
