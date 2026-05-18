public interface ReservationState {
    ReservationStatus getStatus();

    boolean selectSeat(Reservation reservation, String selectedSeatNumber);

    boolean confirm(Reservation reservation);

    boolean markPaymentFailed(Reservation reservation);

    boolean cancel(Reservation reservation);

    boolean requestChange(Reservation reservation);

    boolean completeChange(Reservation reservation);

    boolean requestRefund(Reservation reservation);

    boolean completeRefund(Reservation reservation);

    boolean canPay();

    boolean canCancel();

    boolean canChange();

    boolean canRefund();
}
