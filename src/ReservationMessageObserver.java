public class ReservationMessageObserver implements ReservationObserver {
    @Override
    public void onReservationConfirmed(Reservation reservation, Ticket ticket, BookingPaymentRequest request) {
        if (reservation == null || ticket == null) {
            return;
        }
        reservation.addNotification(
                "Reservation " + reservation.getReservationId() +
                        " confirmed. Ticket " + ticket.getTicketId() + " has been issued."
        );
    }
}
