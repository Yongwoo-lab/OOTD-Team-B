import java.util.UUID;

public class ReservationService {
    public Reservation bookFlight(Customer customer, Flight flight) {
        String reservationId = "R-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return new Reservation(reservationId, customer, flight);
    }

    public Ticket finalizeBooking(Reservation reservation, PaymentService paymentService, String method, String accountNo) {
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
}
