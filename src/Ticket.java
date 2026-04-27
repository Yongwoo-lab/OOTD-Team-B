import java.time.LocalDate;

public class Ticket {
    private final String ticketId;
    private final String status;
    private final String issueDate;

    public Ticket(String ticketId, String status) {
        this.ticketId = ticketId;
        this.status = status;
        this.issueDate = LocalDate.now().toString();
    }

    public String getTicketId() {
        return ticketId;
    }

    public String getStatus() {
        return status;
    }

    public String getIssueDate() {
        return issueDate;
    }
}
