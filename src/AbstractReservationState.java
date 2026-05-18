public abstract class AbstractReservationState implements ReservationState {
    @Override
    public boolean selectSeat(Reservation reservation, String selectedSeatNumber) {
        return false;
    }

    @Override
    public boolean confirm(Reservation reservation) {
        return false;
    }

    @Override
    public boolean markPaymentFailed(Reservation reservation) {
        return false;
    }

    @Override
    public boolean cancel(Reservation reservation) {
        return false;
    }

    @Override
    public boolean requestChange(Reservation reservation) {
        return false;
    }

    @Override
    public boolean completeChange(Reservation reservation) {
        return false;
    }

    @Override
    public boolean requestRefund(Reservation reservation) {
        return false;
    }

    @Override
    public boolean completeRefund(Reservation reservation) {
        return false;
    }

    @Override
    public boolean canPay() {
        return false;
    }

    @Override
    public boolean canCancel() {
        return false;
    }

    @Override
    public boolean canChange() {
        return false;
    }

    @Override
    public boolean canRefund() {
        return false;
    }

    protected boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }
}
