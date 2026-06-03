public class FlightBookingTemplate extends AbstractBookingTemplate {
    private final MileageService mileageService;

    public FlightBookingTemplate(MileageService mileageService) {
        this.mileageService = mileageService;
    }

    @Override
    protected String validateBeforePayment(Reservation reservation, BookingPaymentRequest request) {
        if (reservation == null) {
            return "Reservation does not exist.";
        }
        if (request == null) {
            return "Payment request does not exist.";
        }
        if (!reservation.hasSelectedSeat()) {
            return "Seat must be selected before payment.";
        }
        if (!reservation.canPay()) {
            return "Reservation status does not allow payment: " + reservation.getStatus();
        }
        if (!mileageService.canUseMileage(reservation.getCustomer(), request.getMileageToUse(), reservation.getFlightFare())) {
            return "Mileage amount is invalid or exceeds the flight fare.";
        }
        return null;
    }

    @Override
    protected double calculateOriginalAmount(Reservation reservation) {
        return reservation.getTotalFare();
    }

    @Override
    protected double calculateFinalAmount(Reservation reservation, BookingPaymentRequest request) {
        return mileageService.calculateFinalAmount(reservation, request.getMileageToUse());
    }

    @Override
    protected boolean afterPaymentSucceeded(Reservation reservation, BookingPaymentRequest request) {
        return mileageService.useMileage(
                reservation.getCustomer(),
                request.getMileageToUse(),
                request.getAuthService()
        );
    }
}
