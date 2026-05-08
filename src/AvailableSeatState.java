public class AvailableSeatState implements SeatState {
    @Override
    public String getName() {
        return "AVAILABLE";
    }

    @Override
    public boolean canBeSelected() {
        return true;
    }

    @Override
    public boolean isSelected() {
        return false;
    }

    @Override
    public boolean isOccupied() {
        return false;
    }
}
