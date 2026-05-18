import java.util.ArrayList;
import java.util.List;

public class ReservationNotifier {
    private final List<ReservationObserver> observers = new ArrayList<>();

    public void addObserver(ReservationObserver observer) {
        if (observer != null) {
            observers.add(observer);
        }
    }

    public void notifyReservationConfirmed(Reservation reservation, Ticket ticket, BookingPaymentRequest request) {
        for (ReservationObserver observer : observers) {
            observer.onReservationConfirmed(reservation, ticket, request);
        }
    }
}
