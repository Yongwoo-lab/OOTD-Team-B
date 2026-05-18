import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class Refund {
    private final String refundId;
    private final String paymentId;
    private final double amount;
    private final RefundStatus status;
    private final String message;
    private final String processedAt;

    public Refund(String paymentId, double amount, RefundStatus status, String message) {
        this.refundId = "RF-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.paymentId = paymentId;
        this.amount = amount;
        this.status = status;
        this.message = message;
        this.processedAt = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    public String getRefundId() {
        return refundId;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public double getAmount() {
        return amount;
    }

    public RefundStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public String getProcessedAt() {
        return processedAt;
    }

    public boolean isCompleted() {
        return status == RefundStatus.COMPLETED;
    }
}
