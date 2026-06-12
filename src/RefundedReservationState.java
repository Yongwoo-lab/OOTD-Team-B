public class RefundedReservationState extends AbstractReservationState {
    @Override
    public ReservationStatus getStatus() {
        return ReservationStatus.REFUNDED;
    }
}
