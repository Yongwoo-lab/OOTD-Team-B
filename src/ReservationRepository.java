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

    public boolean isFlightSeatReserved(Flight flight, String seatNumber) {
        if (flight == null || seatNumber == null || seatNumber.trim().isEmpty()) {
            return false;
        }

        for (Reservation reservation : reservations) {
            if (!isActiveReservation(reservation)) {
                continue;
            }
            Flight reservedFlight = reservation.getFlight();
            if (reservedFlight != null
                    && flight.getFlightId().equals(reservedFlight.getFlightId())
                    && seatNumber.equals(reservation.getSelectedSeatNumber())) {
                return true;
            }
        }
        return false;
    }

    public boolean isBusSeatReserved(BusSchedule schedule, String seatNumber) {
        if (schedule == null || seatNumber == null || seatNumber.trim().isEmpty()) {
            return false;
        }

        for (Reservation reservation : reservations) {
            if (!isActiveReservation(reservation) || !reservation.hasBusTicket()) {
                continue;
            }
            BusTicket ticket = reservation.getBusTicket();
            BusSchedule reservedSchedule = ticket.getSchedule();
            if (reservedSchedule != null
                    && schedule.getScheduleId().equals(reservedSchedule.getScheduleId())
                    && seatNumber.equals(ticket.getSeatNumber())) {
                return true;
            }
        }
        return false;
    }

    private boolean isActiveReservation(Reservation reservation) {
        if (reservation == null) {
            return false;
        }
        ReservationStatus status = reservation.getStatus();
        return status != ReservationStatus.CANCELLED
                && status != ReservationStatus.REFUNDED;
    }
}
