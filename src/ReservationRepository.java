import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ReservationRepository {
    private static final ReservationRepository INSTANCE = new ReservationRepository();
    private final List<Reservation> reservations = new ArrayList<>();

    private ReservationRepository() {
    }

    public static ReservationRepository getInstance() {
        return INSTANCE;
    }

    public void save(Reservation reservation) {
        if (reservation != null && findById(reservation.getReservationId()) == null) {
            reservations.add(reservation);
        }
    }

    public List<Reservation> findByCustomer(Customer customer) {
        if (customer == null) {
            return Collections.emptyList();
        }

        List<Reservation> result = new ArrayList<>();
        for (Reservation reservation : reservations) {
            if (reservation.getCustomer() != null
                    && customer.getCustomerId().equals(reservation.getCustomer().getCustomerId())) {
                result.add(reservation);
            }
        }
        return result;
    }

    public Reservation findById(String reservationId) {
        if (reservationId == null || reservationId.trim().isEmpty()) {
            return null;
        }

        for (Reservation reservation : reservations) {
            if (reservationId.equals(reservation.getReservationId())) {
                return reservation;
            }
        }
        return null;
    }
}
