public class Reservation {
    private final String reservationId;
    private ReservationStatus status;
    private final double totalFare;
    private final Customer customer;
    private final Flight flight;
    private Payment payment;
    private Ticket ticket;
    private String selectedSeatNumber;

    public Reservation(String reservationId, Customer customer, Flight flight) {
        this.reservationId = reservationId;
        this.customer = customer;
        this.flight = flight;
        this.totalFare = flight.getPrice();
        this.status = ReservationStatus.PENDING;
    }

    public String getReservationId() {
        return reservationId;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public double getTotalFare() {
        return totalFare;
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

    public Ticket getTicket() {
        return ticket;
    }

    public String getSelectedSeatNumber() {
        return selectedSeatNumber;
    }

    public boolean hasSelectedSeat() {
        return selectedSeatNumber != null && !selectedSeatNumber.trim().isEmpty();
    }

    public void selectSeat(String selectedSeatNumber) {
        if (selectedSeatNumber == null || selectedSeatNumber.trim().isEmpty()) {
            return;
        }
        this.selectedSeatNumber = selectedSeatNumber.trim();
        this.status = ReservationStatus.SEAT_SELECTED;
    }

    public void attachPayment(Payment payment) {
        this.payment = payment;
    }

    public void confirm() {
        this.status = ReservationStatus.CONFIRMED;
    }

    public void markFailed() {
        this.status = hasSelectedSeat() ? ReservationStatus.SEAT_SELECTED : ReservationStatus.PENDING;
    }

    public void cancel() {
        this.status = ReservationStatus.CANCELLED;
    }

    public boolean isCancellable() {
        return status != ReservationStatus.CANCELLED;
    }

    public void issueTicket(Ticket ticket) {
        this.ticket = ticket;
    }
}
