public class BusTicket {
    private final String ticketId;
    private final BusSchedule schedule;
    private final String seatNumber;
    private String status;

    public BusTicket(String ticketId, BusSchedule schedule) {
        this(ticketId, schedule, null);
    }

    public BusTicket(String ticketId, BusSchedule schedule, String seatNumber) {
        this.ticketId = ticketId;
        this.schedule = schedule;
        this.seatNumber = seatNumber;
        this.status = "SELECTED";
    }

    public String getTicketId() {
        return ticketId;
    }

    public BusSchedule getSchedule() {
        return schedule;
    }

    public String getStatus() {
        return status;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public double getFare() {
        return schedule == null ? 0 : schedule.getFare();
    }

    public void issue() {
        status = "ISSUED";
    }

    public String getRouteText() {
        if (schedule == null) {
            return "-";
        }
        String seat = seatNumber == null ? "" : " / Seat " + seatNumber;
        return schedule.getDepartureCity() + " -> " + schedule.getArrivalCity() + seat;
    }
}
