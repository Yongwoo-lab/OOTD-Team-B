public class CancelledReservationState extends AbstractReservationState {
    @Override
    public ReservationStatus getStatus() {
        return ReservationStatus.CANCELLED;
    }
}
