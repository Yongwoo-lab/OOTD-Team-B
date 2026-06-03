public class SeatSelectedReservationState extends AbstractReservationState {
    @Override
    public ReservationStatus getStatus() {
        return ReservationStatus.SEAT_SELECTED;
    }

    @Override
    public boolean handle(Reservation reservation, ReservationAction action, Object payload) {
        if (action == ReservationAction.SELECT_SEAT) {
            String selectedSeatNumber = getTextPayload(payload);
            if (!hasText(selectedSeatNumber)) {
                return false;
            }
            reservation.setSelectedSeatNumber(selectedSeatNumber);
            return true;
        }
        if (action == ReservationAction.CONFIRM) {
            reservation.setState(new ConfirmedReservationState());
            return true;
        }
        if (action == ReservationAction.MARK_PAYMENT_FAILED) {
            reservation.setState(new SeatSelectedReservationState());
            return true;
        }
        if (action == ReservationAction.CANCEL) {
            reservation.setState(new CancelledReservationState());
            return true;
        }
        if (action == ReservationAction.REQUEST_CHANGE) {
            reservation.setState(new ChangeRequestedReservationState());
            return true;
        }
        return false;
    }

    @Override
    public boolean canHandle(ReservationAction action) {
        return action == ReservationAction.SELECT_SEAT
                || action == ReservationAction.CONFIRM
                || action == ReservationAction.MARK_PAYMENT_FAILED
                || action == ReservationAction.CANCEL
                || action == ReservationAction.REQUEST_CHANGE;
    }
}
