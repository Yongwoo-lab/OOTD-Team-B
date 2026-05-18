public class MileageAccrualObserver implements ReservationObserver {
    private final MileageService mileageService;

    public MileageAccrualObserver(MileageService mileageService) {
        this.mileageService = mileageService;
    }

    @Override
    public void onReservationConfirmed(Reservation reservation, Ticket ticket, BookingPaymentRequest request) {
        if (reservation == null || request == null || mileageService == null) {
            return;
        }
        double paidFlightAmount = Math.max(0, reservation.getFlightFare() - request.getDiscountAmount());
        mileageService.earnFlightMileage(
                reservation.getCustomer(),
                paidFlightAmount,
                request.getAuthService()
        );
    }
}
