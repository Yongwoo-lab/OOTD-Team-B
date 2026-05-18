public class PaymentController {
    private final ReservationService reservationService;
    private final PaymentService paymentService;

    public PaymentController(ReservationService reservationService) {
        this.reservationService = reservationService;
        this.paymentService = new PaymentService();
    }

    public Ticket processPayment(Reservation reservation, String method, String accountNo) {
        return reservationService.finalizeBooking(reservation, paymentService, method, accountNo);
    }

    public Ticket processPayment(Reservation reservation, String method, String accountNo, int mileageToUse, AuthService authService) {
        return reservationService.finalizeBooking(reservation, paymentService, method, accountNo, mileageToUse, authService);
    }

    public Payment getLastPayment(Reservation reservation) {
        return reservation.getPayment();
    }
}
