public class RefundedReservationState extends AbstractReservationState {
    @Override
    public ReservationStatus getStatus() {
        return ReservationStatus.REFUNDED;
    }

    @Override
    public boolean handle(Reservation reservation, ReservationAction action, Object payload) {
        if (action == ReservationAction.CANCEL) {
            reservation.setState(new CancelledReservationState());
            return true;
        }
        return false;
    }

    @Override
    public boolean canHandle(ReservationAction action) {
        return action == ReservationAction.CANCEL;
    }
}
