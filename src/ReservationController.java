public class ReservationController {
    private final ReservationService reservationService;

    public ReservationController() {
        this.reservationService = new ReservationService();
    }

    public Reservation bookFlight(Customer customer, String selectedFlight) {
        Flight flight = parseFlight(selectedFlight);
        return reservationService.bookFlight(customer, flight);
    }

    public ReservationService getReservationService() {
        return reservationService;
    }

    private Flight parseFlight(String selectedFlight) {
        String[] parts = selectedFlight.split("\\|");

        String flightId = parts[0].trim();

        String routePart = parts[1].trim();
        String[] routeTokens = routePart.split("->");
        String departure = routeTokens[0].trim();
        String arrival = routeTokens[1].trim();

        String timePart = parts[2].trim();
        String[] dateAndTimes = timePart.split(" ");
        String date = dateAndTimes[0].trim();
        String departureTime = dateAndTimes[1].trim();
        String arrivalTime = dateAndTimes[3].trim();

        String pricePart = parts[3].replace("KRW", "").replace(",", "").trim();
        double price = Double.parseDouble(pricePart);

        return new Flight(flightId, departure, arrival, date, departureTime, arrivalTime, price);
    }
}
