import java.util.ArrayList;
import java.util.List;

public abstract class AirplaneSeatLayoutStrategy implements SeatLayoutStrategy {
    private static final String[] COLUMNS = {"A", "B", "C", "D", "E", "F"};
    private static final int AISLE_INDEX = 3;
    private final ReservationRepository reservationRepository = ReservationRepository.getInstance();
    private final String aircraftType;
    private final int rowCount;

    protected AirplaneSeatLayoutStrategy(String aircraftType, int rowCount) {
        this.aircraftType = aircraftType;
        this.rowCount = rowCount;
    }

    @Override
    public List<Seat> createSeats(Flight flight, String selectedSeatNumber) {
        List<Seat> seats = new ArrayList<>();
        for (int row = 1; row <= rowCount; row++) {
            for (String column : COLUMNS) {
                String seatNumber = row + column;
                seats.add(new Seat(row, column, createSeatState(flight, seatNumber, selectedSeatNumber)));
            }
        }
        return seats;
    }

    @Override
    public int getRowCount() {
        return rowCount;
    }

    @Override
    public String[] getColumns() {
        return COLUMNS.clone();
    }

    @Override
    public int getAisleIndex() {
        return AISLE_INDEX;
    }

    @Override
    public String getAircraftType() {
        return aircraftType;
    }

    @Override
    public int getTotalSeatCount() {
        return rowCount * COLUMNS.length;
    }

    private SeatState createSeatState(Flight flight, String seatNumber, String selectedSeatNumber) {
        if (seatNumber.equals(selectedSeatNumber)) {
            return new SelectedSeatState();
        }
        if (isOccupied(flight, seatNumber)) {
            return new OccupiedSeatState();
        }
        return new AvailableSeatState();
    }

    private boolean isOccupied(Flight flight, String seatNumber) {
        return reservationRepository.isFlightSeatReserved(flight, seatNumber);
    }
}
