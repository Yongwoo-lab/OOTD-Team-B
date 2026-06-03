public class ConfirmedReservationState extends AbstractReservationState {
    @Override
    public ReservationStatus getStatus() {
        return ReservationStatus.CONFIRMED;
    }

    @Override
    public boolean handle(Reservation reservation, ReservationAction action, Object payload) {
        if (action == ReservationAction.REQUEST_CHANGE) {
            reservation.setState(new ChangeRequestedReservationState());
            return true;
        }
        if (action == ReservationAction.REQUEST_REFUND) {
            reservation.setState(new RefundRequestedReservationState());
            return true;
        }
        if (action == ReservationAction.CANCEL) {
            reservation.setState(new CancelledReservationState());
            return true;
        }
        return false;
    }

    @Override
    public boolean canHandle(ReservationAction action) {
        return action == ReservationAction.CANCEL
                || action == ReservationAction.REQUEST_CHANGE
                || action == ReservationAction.REQUEST_REFUND;
    }
}
