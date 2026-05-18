public class BusTicket {
    private final String ticketId;
    private final BusSchedule schedule;
    private String status;

    public BusTicket(String ticketId, BusSchedule schedule) {
        this.ticketId = ticketId;
        this.schedule = schedule;
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
        return schedule.getDepartureCity() + " -> " + schedule.getArrivalCity();
    }
}
