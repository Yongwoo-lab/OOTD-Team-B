public class RefundRequestedReservationState extends AbstractReservationState {
    @Override
    public ReservationStatus getStatus() {
        return ReservationStatus.REFUND_REQUESTED;
    }

    @Override
    public boolean completeRefund(Reservation reservation) {
        reservation.setState(new RefundedReservationState());
        return true;
    }
}
