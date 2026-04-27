public class Flight {
    private final String flightId;
    private final String departure;
    private final String arrival;
    private final String date;
    private final String departureTime;
    private final String arrivalTime;
    private final double price;

    public Flight(String flightId, String departure, String arrival, String date, String departureTime, String arrivalTime, double price) {
        this.flightId = flightId;
        this.departure = departure;
        this.arrival = arrival;
        this.date = date;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.price = price;
    }

    public String getFlightId() {
        return flightId;
    }

    public String getDeparture() {
        return departure;
    }

    public String getArrival() {
        return arrival;
    }

    public String getDate() {
        return date;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public String getArrivalTime() {
        return arrivalTime;
    }

    public double getPrice() {
        return price;
    }
}
