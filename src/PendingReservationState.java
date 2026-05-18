public class PendingReservationState extends AbstractReservationState {
    @Override
    public ReservationStatus getStatus() {
        return ReservationStatus.PENDING;
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
    public boolean cancel(Reservation reservation) {
        reservation.setState(new CancelledReservationState());
        return true;
    }

    @Override
    public boolean requestChange(Reservation reservation) {
        reservation.setState(new ChangeRequestedReservationState());
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
