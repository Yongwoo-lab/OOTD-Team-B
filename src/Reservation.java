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

    public void changeFlight(Flight flight) {
        if (flight == null) {
            return;
        }
        requestChange();
        this.flight = flight;
        clearSelectedSeat();
        this.ticket = null;
        completeChange();
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

    public boolean selectSeat(String selectedSeatNumber) {
        return state.selectSeat(this, selectedSeatNumber);
    }

    public void attachPayment(Payment payment) {
        this.payment = payment;
    }

    public boolean confirm() {
        return state.confirm(this);
    }

    public boolean markFailed() {
        if (state.markPaymentFailed(this)) {
            return true;
        }
        state = hasSelectedSeat() ? new SeatSelectedReservationState() : new PendingReservationState();
        return true;
    }

    public boolean cancel() {
        return state.cancel(this);
    }

    public boolean isCancellable() {
        return state.canCancel();
    }

    public boolean canPay() {
        return state.canPay();
    }

    public boolean canChange() {
        return state.canChange();
    }

    public boolean canRefund() {
        return state.canRefund();
    }

    public boolean requestChange() {
        return state.requestChange(this);
    }

    public boolean completeChange() {
        return state.completeChange(this);
    }

    public boolean requestRefund() {
        return state.requestRefund(this);
    }

    public boolean completeRefund(Refund refund) {
        this.refund = refund;
        return state.completeRefund(this);
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
