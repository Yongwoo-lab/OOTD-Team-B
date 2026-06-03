public class ChangeRequestedReservationState extends AbstractReservationState {
    @Override
    public ReservationStatus getStatus() {
        return ReservationStatus.CHANGE_REQUESTED;
    }

    @Override
    public boolean handle(Reservation reservation, ReservationAction action, Object payload) {
        if (action == ReservationAction.SELECT_SEAT) {
            String selectedSeatNumber = getTextPayload(payload);
            if (!hasText(selectedSeatNumber)) {
                return false;
            }
            reservation.setSelectedSeatNumber(selectedSeatNumber);
            reservation.setState(new SeatSelectedReservationState());
            return true;
        }
        if (action == ReservationAction.COMPLETE_CHANGE) {
            if (reservation.hasSelectedSeat()) {
                reservation.setState(new SeatSelectedReservationState());
            } else {
                reservation.setState(new PendingReservationState());
            }
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
                || action == ReservationAction.SELECT_SEAT
                || action == ReservationAction.COMPLETE_CHANGE;
    }
}
