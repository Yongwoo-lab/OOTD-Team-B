import java.util.Collections;
import java.util.List;

public class SeatService {
    private final SeatLayoutStrategy layoutStrategy;
    private final List<Seat> seats;

    public SeatService(Flight flight, String selectedSeatNumber) {
        this(new SeatLayoutStrategyFactory().createStrategy(flight), flight, selectedSeatNumber);
    }

    public SeatService(SeatLayoutStrategy layoutStrategy, Flight flight, String selectedSeatNumber) {
        this.layoutStrategy = layoutStrategy;
        this.seats = layoutStrategy.createSeats(flight, selectedSeatNumber);
    }

    public List<Seat> getSeats() {
        return Collections.unmodifiableList(seats);
    }

    public Seat findSeat(String seatNumber) {
        if (seatNumber == null) {
            return null;
        }
        for (Seat seat : seats) {
            if (seat.getSeatNumber().equals(seatNumber)) {
                return seat;
            }
        }
        return null;
    }

    public boolean selectSeat(String seatNumber) {
        Seat targetSeat = findSeat(seatNumber);
        if (targetSeat == null || targetSeat.isOccupied()) {
            return false;
        }
        if (targetSeat.isSelected()) {
            return true;
        }
        if (!targetSeat.isAvailable()) {
            return false;
        }

        Seat selectedSeat = getSelectedSeat();
        if (selectedSeat != null) {
            selectedSeat.release();
        }
        return targetSeat.select();
    }

    public Seat getSelectedSeat() {
        for (Seat seat : seats) {
            if (seat.isSelected()) {
                return seat;
            }
        }
        return null;
    }

    public String getSelectedSeatNumber() {
        Seat selectedSeat = getSelectedSeat();
        return selectedSeat == null ? null : selectedSeat.getSeatNumber();
    }

    public int getTotalSeatCount() {
        return layoutStrategy.getTotalSeatCount();
    }

    public int getAvailableSeatCount() {
        int count = 0;
        for (Seat seat : seats) {
            if (seat.isAvailable()) {
                count++;
            }
        }
        return count;
    }

    public int getOccupiedSeatCount() {
        int count = 0;
        for (Seat seat : seats) {
            if (seat.isOccupied()) {
                count++;
            }
        }
        return count;
    }

    public int getRowCount() {
        return layoutStrategy.getRowCount();
    }

    public String[] getColumns() {
        return layoutStrategy.getColumns();
    }

    public int getAisleIndex() {
        return layoutStrategy.getAisleIndex();
    }

    public String getAircraftType() {
        return layoutStrategy.getAircraftType();
    }
}
