public interface ReservationState {
    ReservationStatus getStatus();

    boolean handle(Reservation reservation, ReservationAction action, Object payload);

    boolean canHandle(ReservationAction action);
}

enum ReservationAction {
    SELECT_SEAT,
    CONFIRM,
    MARK_PAYMENT_FAILED,
    CANCEL,
    REQUEST_CHANGE,
    COMPLETE_CHANGE,
    REQUEST_REFUND,
    COMPLETE_REFUND
}
