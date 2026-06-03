import java.util.UUID;

public abstract class AbstractBookingTemplate {
    public final Ticket finalizeBooking(Reservation reservation, PaymentService paymentService,
                                        BookingPaymentRequest request, ReservationNotifier notifier) {
        if (reservation == null) {
            return null;
        }
        if (paymentService == null) {
            reservation.markFailed();
            return null;
        }
        if (request == null) {
            Payment payment = paymentService.createFailedPayment(getSafeAmount(reservation), "Payment request does not exist.");
            reservation.attachPayment(payment);
            reservation.markFailed();
            return null;
        }

        String validationError = validateBeforePayment(reservation, request);
        if (validationError != null) {
            Payment payment = paymentService.createFailedPayment(getSafeAmount(reservation), validationError);
            reservation.attachPayment(payment);
            reservation.markFailed();
            return null;
        }

        double originalAmount = calculateOriginalAmount(reservation);
        double finalAmount = calculateFinalAmount(reservation, request);
        double discountAmount = Math.max(0, originalAmount - finalAmount);
        request.setAmountSummary(originalAmount, finalAmount, discountAmount);

        Payment payment = paymentService.processPayment(finalAmount, request.getMethod(), request.getPaymentInfo());
        if (payment == null) {
            Payment failedPayment = paymentService.createFailedPayment(finalAmount, "Payment could not be processed.");
            reservation.attachPayment(failedPayment);
            reservation.markFailed();
            return null;
        }

        payment = payment.withDiscountSummary(originalAmount, request.getMileageToUse(), discountAmount);
        reservation.attachPayment(payment);

        if (!payment.isSuccess()) {
            reservation.markFailed();
            return null;
        }

        if (!afterPaymentSucceeded(reservation, request)) {
            Payment failedPayment = paymentService.createFailedPayment(finalAmount, "Mileage could not be deducted.");
            reservation.attachPayment(failedPayment);
            reservation.markFailed();
            return null;
        }

        reservation.confirm();
        Ticket ticket = issueTicket();
        reservation.issueTicket(ticket);

        if (notifier != null) {
            notifier.notifyReservationConfirmed(reservation, ticket, request);
        }
        return ticket;
    }

    protected abstract String validateBeforePayment(Reservation reservation, BookingPaymentRequest request);

    protected abstract double calculateOriginalAmount(Reservation reservation);

    protected abstract double calculateFinalAmount(Reservation reservation, BookingPaymentRequest request);

    protected abstract boolean afterPaymentSucceeded(Reservation reservation, BookingPaymentRequest request);

    protected Ticket issueTicket() {
        String ticketId = "T-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return new Ticket(ticketId, "ISSUED");
    }

    private double getSafeAmount(Reservation reservation) {
        return reservation == null ? 0 : reservation.getTotalFare();
    }
}
