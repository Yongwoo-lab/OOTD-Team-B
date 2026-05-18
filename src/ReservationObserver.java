public interface ReservationObserver {
    void onReservationConfirmed(Reservation reservation, Ticket ticket, BookingPaymentRequest request);
}
