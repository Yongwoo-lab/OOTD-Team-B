public class Reservation {
    private final String reservationId;
    private String status;
    private final double totalFare;
    private final Customer customer;
    private final Flight flight;
    private Payment payment;
    private Ticket ticket;

    public Reservation(String reservationId, Customer customer, Flight flight) {
        this.reservationId = reservationId;
        this.customer = customer;
        this.flight = flight;
        this.totalFare = flight.getPrice();
        this.status = "PENDING";
    }

    public String getReservationId() {
        return reservationId;
    }

    public String getStatus() {
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

    public void attachPayment(Payment payment) {
        this.payment = payment;
    }

    public void confirm() {
        this.status = "CONFIRMED";
    }

    public void markFailed() {
        this.status = "PENDING";
    }

    public void issueTicket(Ticket ticket) {
        this.ticket = ticket;
    }
}
