import java.util.List;

public interface SeatLayoutStrategy {
    List<Seat> createSeats(Flight flight, String selectedSeatNumber);

    int getRowCount();

    String[] getColumns();

    int getAisleIndex();

    String getAircraftType();

    int getTotalSeatCount();
}
