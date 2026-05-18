import java.util.UUID;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ReservationService {
    private static final List<Reservation> RESERVATION_STORE = new ArrayList<>();
    private final AuthorizationService authorizationService = new AuthorizationService();
    private final MileageService mileageService = new MileageService();
    private final ReservationNotifier reservationNotifier;
    private final AbstractBookingTemplate bookingTemplate;

    public ReservationService() {
        this.reservationNotifier = new ReservationNotifier();
        this.reservationNotifier.addObserver(new ReservationMessageObserver());
        this.reservationNotifier.addObserver(new MileageAccrualObserver(mileageService));
        this.bookingTemplate = new FlightBookingTemplate(mileageService);
    }

    public Reservation bookFlight(Customer customer, Flight flight) {
        if (!authorizationService.canBookFlight(customer)) {
            return null;
        }
        String reservationId = "R-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Reservation reservation = new Reservation(reservationId, customer, flight);
        RESERVATION_STORE.add(reservation);
        return reservation;
    }

    public Ticket finalizeBooking(Reservation reservation, PaymentService paymentService, String method, String accountNo) {
        return finalizeBooking(reservation, paymentService, method, accountNo, 0, null);
    }

    public Ticket finalizeBooking(Reservation reservation, PaymentService paymentService,
                                  String method, String accountNo, int mileageToUse, AuthService authService) {
        BookingPaymentRequest request = new BookingPaymentRequest(method, accountNo, mileageToUse, authService);
        return bookingTemplate.finalizeBooking(reservation, paymentService, request, reservationNotifier);
    }

    public List<Reservation> getReservationsByCustomer(Customer customer) {
        if (customer == null) {
            return Collections.emptyList();
        }

        List<Reservation> reservations = new ArrayList<>();
        for (Reservation reservation : RESERVATION_STORE) {
            if (reservation.getCustomer() != null
                    && customer.getCustomerId().equals(reservation.getCustomer().getCustomerId())
                    && reservation.getStatus() != ReservationStatus.CANCELLED) {
                reservations.add(reservation);
            }
        }
        return reservations;
    }

    public boolean cancelReservation(String reservationId, PaymentService paymentService) {
        return cancelReservation(null, reservationId, paymentService, null);
    }

    public boolean cancelReservation(Customer user, String reservationId, PaymentService paymentService, AuthService authService) {
        Reservation reservation = findById(reservationId);
        if (reservation == null || !reservation.isCancellable()) {
            return false;
        }
        if (user != null && !authorizationService.ownsReservation(user, reservation)) {
            return false;
        }

        Payment payment = reservation.getPayment();
        if (payment != null && payment.isSuccess() && paymentService != null) {
            if (reservation.canRefund()) {
                reservation.requestRefund();
            }
            Refund refund = paymentService.processRefund(payment);
            if (!refund.isCompleted()) {
                return false;
            }
            reservation.completeRefund(refund);
            mileageService.restoreMileage(reservation.getCustomer(), payment.getMileageUsed(), authService);
            double paidFlightFare = Math.max(0, reservation.getFlightFare() - payment.getDiscountAmount());
            mileageService.revokeEarnedMileage(reservation.getCustomer(), paidFlightFare, authService);
        }

        reservation.cancel();
        return true;
    }

    public boolean changeReservationSeat(Customer user, String reservationId, String selectedSeatNumber) {
        Reservation reservation = findById(reservationId);
        if (reservation == null) {
            return false;
        }
        if (!authorizationService.ownsReservation(user, reservation) || !reservation.canChange()) {
            return false;
        }

        boolean wasConfirmed = reservation.getStatus() == ReservationStatus.CONFIRMED
                || reservation.getTicket() != null
                || (reservation.getPayment() != null && reservation.getPayment().isSuccess());
        reservation.requestChange();
        boolean changed = reservation.selectSeat(selectedSeatNumber);
        if (!changed) {
            return false;
        }
        if (wasConfirmed) {
            reservation.confirm();
        } else {
            reservation.completeChange();
        }
        return true;
    }

    public Reservation findById(String reservationId) {
        if (reservationId == null || reservationId.trim().isEmpty()) {
            return null;
        }

        for (Reservation reservation : RESERVATION_STORE) {
            if (reservationId.equals(reservation.getReservationId())) {
                return reservation;
            }
        }
        return null;
    }
}
