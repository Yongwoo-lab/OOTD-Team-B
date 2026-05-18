public class ConfirmedReservationState extends AbstractReservationState {
    @Override
    public ReservationStatus getStatus() {
        return ReservationStatus.CONFIRMED;
    }

    @Override
    public boolean requestChange(Reservation reservation) {
        reservation.setState(new ChangeRequestedReservationState());
        return true;
    }

    @Override
    public boolean requestRefund(Reservation reservation) {
        reservation.setState(new RefundRequestedReservationState());
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

    @Override
    public boolean canRefund() {
        return true;
    }
}
