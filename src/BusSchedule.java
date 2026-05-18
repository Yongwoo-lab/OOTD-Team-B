import java.util.UUID;

public class BusSchedule {
    private final String scheduleId;
    private final String departureCity;
    private final String arrivalCity;
    private final String date;
    private final String departureTime;
    private final String arrivalTime;
    private final String grade;
    private final double fare;

    public BusSchedule(String scheduleId, String departureCity, String arrivalCity, String date,
                       String departureTime, String arrivalTime, String grade, double fare) {
        this.scheduleId = scheduleId;
        this.departureCity = departureCity;
        this.arrivalCity = arrivalCity;
        this.date = date;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.grade = grade;
        this.fare = fare;
    }

    public String getScheduleId() {
        return scheduleId;
    }

    public String getDepartureCity() {
        return departureCity;
    }

    public String getArrivalCity() {
        return arrivalCity;
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

    public String getGrade() {
        return grade;
    }

    public double getFare() {
        return fare;
    }

    public BusTicket createTicket() {
        String ticketId = "B-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return new BusTicket(ticketId, this, null);
    }

    public BusTicket createTicket(String seatNumber) {
        String ticketId = "B-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return new BusTicket(ticketId, this, seatNumber);
    }
}
