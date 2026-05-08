public class Seat {
    private final int row;
    private final String column;
    private SeatState state;

    public Seat(int row, String column, SeatState state) {
        this.row = row;
        this.column = column;
        this.state = state;
    }

    public int getRow() {
        return row;
    }

    public String getColumn() {
        return column;
    }

    public String getSeatNumber() {
        return row + column;
    }

    public String getStatus() {
        return state.getName();
    }

    public boolean isAvailable() {
        return state.canBeSelected();
    }

    public boolean isSelected() {
        return state.isSelected();
    }

    public boolean isOccupied() {
        return state.isOccupied();
    }

    public boolean select() {
        if (!state.canBeSelected()) {
            return false;
        }
        state = new SelectedSeatState();
        return true;
    }

    public void release() {
        if (state.isSelected()) {
            state = new AvailableSeatState();
        }
    }
}
