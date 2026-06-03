public class Reservation {
    private final String reservationId;
    private final Customer customer;
    private Flight flight;
    private ReservationState state;
    private Payment payment;
    private Refund refund;
    private Ticket ticket;
    private BusTicket busTicket;
    private String selectedSeatNumber;
    private String lastNotificationMessage;

    public Reservation(String reservationId, Customer customer, Flight flight) {
        this.reservationId = reservationId;
        this.customer = customer;
        this.flight = flight;
        this.state = new PendingReservationState();
    }

    public String getReservationId() {
        return reservationId;
    }

    public ReservationStatus getStatus() {
        return state.getStatus();
    }

    public double getTotalFare() {
        return getFlightFare() + getBusFare();
    }

    public double getFlightFare() {
        return flight == null ? 0 : flight.getPrice();
    }

    public double getBusFare() {
        return busTicket == null ? 0 : busTicket.getFare();
    }

    public Customer getCustomer() {
        return customer;
    }

    public Flight getFlight() {
        return flight;
    }

    public Payment getPayment() {
        return payment;
    }

    public Refund getRefund() {
        return refund;
    }

    public Ticket getTicket() {
        return ticket;
    }

    public BusTicket getBusTicket() {
        return busTicket;
    }

    public boolean hasBusTicket() {
        return busTicket != null;
    }

    public void addBusTicket(BusTicket busTicket) {
        this.busTicket = busTicket;
    }

    public void removeBusTicket() {
        this.busTicket = null;
    }

    public String getSelectedSeatNumber() {
        return selectedSeatNumber;
    }

    public boolean hasSelectedSeat() {
        return selectedSeatNumber != null && !selectedSeatNumber.trim().isEmpty();
    }

    public boolean request(ReservationAction action) {
        return request(action, null);
    }

    public boolean request(ReservationAction action, Object payload) {
        if (action == null) {
            return false;
        }
        return state.handle(this, action, payload);
    }

    public boolean selectSeat(String selectedSeatNumber) {
        return request(ReservationAction.SELECT_SEAT, selectedSeatNumber);
    }

    public void attachPayment(Payment payment) {
        this.payment = payment;
    }

    public boolean confirm() {
        return request(ReservationAction.CONFIRM);
    }

    public boolean markFailed() {
        if (request(ReservationAction.MARK_PAYMENT_FAILED)) {
            return true;
        }
        state = hasSelectedSeat() ? new SeatSelectedReservationState() : new PendingReservationState();
        return true;
    }

    public boolean cancel() {
        return request(ReservationAction.CANCEL);
    }

    public boolean isCancellable() {
        return state.canHandle(ReservationAction.CANCEL);
    }

    public boolean canPay() {
        return state.canHandle(ReservationAction.CONFIRM);
    }

    public boolean canChange() {
        return state.canHandle(ReservationAction.REQUEST_CHANGE);
    }

    public boolean canRefund() {
        return state.canHandle(ReservationAction.REQUEST_REFUND);
    }

    public boolean requestChange() {
        return request(ReservationAction.REQUEST_CHANGE);
    }

    public boolean completeChange() {
        return request(ReservationAction.COMPLETE_CHANGE);
    }

    public boolean requestRefund() {
        return request(ReservationAction.REQUEST_REFUND);
    }

    public boolean completeRefund(Refund refund) {
        this.refund = refund;
        return request(ReservationAction.COMPLETE_REFUND, refund);
    }

    public void issueTicket(Ticket ticket) {
        this.ticket = ticket;
        if (busTicket != null) {
            busTicket.issue();
        }
    }

    public void addNotification(String message) {
        this.lastNotificationMessage = message;
    }

    public String getLastNotificationMessage() {
        return lastNotificationMessage;
    }

    void setState(ReservationState state) {
        if (state != null) {
            this.state = state;
        }
    }

    void setSelectedSeatNumber(String selectedSeatNumber) {
        this.selectedSeatNumber = selectedSeatNumber;
    }

    void clearSelectedSeat() {
        this.selectedSeatNumber = null;
    }
}
