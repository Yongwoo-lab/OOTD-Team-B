public class ChangeRequestedReservationState extends AbstractReservationState {
    @Override
    public ReservationStatus getStatus() {
        return ReservationStatus.CHANGE_REQUESTED;
    }

    @Override
    public boolean selectSeat(Reservation reservation, String selectedSeatNumber) {
        if (!hasText(selectedSeatNumber)) {
            return false;
        }
        reservation.setSelectedSeatNumber(selectedSeatNumber.trim());
        reservation.setState(new SeatSelectedReservationState());
        return true;
    }

    @Override
    public boolean completeChange(Reservation reservation) {
        if (reservation.hasSelectedSeat()) {
            reservation.setState(new SeatSelectedReservationState());
        } else {
            reservation.setState(new PendingReservationState());
        }
        return true;
    }

    @Override
    public boolean cancel(Reservation reservation) {
        reservation.setState(new CancelledReservationState());
        return true;
    }

    @Override
    public boolean canCancel() {
        return true;
    }

    @Override
    public boolean canChange() {
        return true;
    }
}
