public class SelectedSeatState implements SeatState {
    @Override
    public String getName() {
        return "SELECTED";
    }

    @Override
    public boolean canBeSelected() {
        return false;
    }

    @Override
    public boolean isSelected() {
        return true;
    }

    @Override
    public boolean isOccupied() {
        return false;
    }
}
