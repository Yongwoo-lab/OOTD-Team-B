public abstract class AbstractReservationState implements ReservationState {
    @Override
    public boolean handle(Reservation reservation, ReservationAction action, Object payload) {
        return false;
    }

    @Override
    public boolean canHandle(ReservationAction action) {
        return false;
    }

    protected boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }

    protected String getTextPayload(Object payload) {
        return payload instanceof String ? ((String) payload).trim() : null;
    }
}
