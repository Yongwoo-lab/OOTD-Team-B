public class ReservationController {
    private final ReservationService reservationService;

    public ReservationController() {
        this.reservationService = new ReservationService();
    }

    public Reservation bookFlight(Customer customer, Flight flight) {
        return reservationService.bookFlight(customer, flight);
    }

    public ReservationService getReservationService() {
        return reservationService;
    }
}
