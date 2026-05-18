public class RefundedReservationState extends AbstractReservationState {
    @Override
    public ReservationStatus getStatus() {
        return ReservationStatus.REFUNDED;
    }

    @Override
    public boolean cancel(Reservation reservation) {
        reservation.setState(new CancelledReservationState());
        return true;
    }
}
