import java.util.UUID;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ReservationService {
    private static final List<Reservation> RESERVATION_STORE = new ArrayList<>();

    public Reservation bookFlight(Customer customer, Flight flight) {
        String reservationId = "R-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Reservation reservation = new Reservation(reservationId, customer, flight);
        RESERVATION_STORE.add(reservation);
        return reservation;
    }

    public Ticket finalizeBooking(Reservation reservation, PaymentService paymentService, String method, String accountNo) {
        if (!reservation.hasSelectedSeat()) {
            Payment payment = paymentService.createFailedPayment(reservation.getTotalFare(), "Seat must be selected before payment.");
            reservation.attachPayment(payment);
            reservation.markFailed();
            return null;
        }

        Payment payment = paymentService.processPayment(reservation.getTotalFare(), method, accountNo);
        reservation.attachPayment(payment);

        if (!payment.isSuccess()) {
            reservation.markFailed();
            return null;
        }

        reservation.confirm();
        Ticket ticket = issueTicket();
        reservation.issueTicket(ticket);
        return ticket;
    }

    private Ticket issueTicket() {
        String ticketId = "T-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return new Ticket(ticketId, "ISSUED");
    }

    public List<Reservation> getReservationsByCustomer(Customer customer) {
        if (customer == null) {
            return Collections.emptyList();
        }

        List<Reservation> reservations = new ArrayList<>();
        for (Reservation reservation : RESERVATION_STORE) {
            if (reservation.getCustomer() != null
                    && customer.getCustomerId().equals(reservation.getCustomer().getCustomerId())
                    && reservation.getStatus() != ReservationStatus.CANCELLED) {
                reservations.add(reservation);
            }
        }
        return reservations;
    }

    public boolean cancelReservation(String reservationId, PaymentService paymentService) {
        Reservation reservation = findById(reservationId);
        if (reservation == null || !reservation.isCancellable()) {
            return false;
        }

        Payment payment = reservation.getPayment();
        if (payment != null && payment.isSuccess() && paymentService != null) {
            paymentService.processRefund(payment.getPaymentId());
        }

        reservation.cancel();
        return true;
    }

    public Reservation findById(String reservationId) {
        if (reservationId == null || reservationId.trim().isEmpty()) {
            return null;
        }

        for (Reservation reservation : RESERVATION_STORE) {
            if (reservationId.equals(reservation.getReservationId())) {
                return reservation;
            }
        }
        return null;
    }
}
