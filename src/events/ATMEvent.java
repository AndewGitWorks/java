package events;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Base class for all ATM events
 * Defines common structure for event hierarchy
 */
public abstract class ATMEvent {
    protected int eventId;
    protected String eventType;
    protected LocalDateTime timestamp;
    protected String description;
    protected EventStatus status;

    public enum EventStatus {
        INITIATED,      // Событие инициировано
        IN_PROGRESS,    // Обработка
        COMPLETED,      // Завершено успешно
        FAILED,         // Ошибка
        CANCELLED       // Отменено
    }

    public ATMEvent(String eventType, String description) {
        this.eventId = generateEventId();
        this.eventType = eventType;
        this.timestamp = LocalDateTime.now();
        this.description = description;
        this.status = EventStatus.INITIATED;
    }

    private static int generateEventId() {
        return (int) (System.nanoTime() % Integer.MAX_VALUE);
    }

    public int getEventId() {
        return eventId;
    }

    public String getEventType() {
        return eventType;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getDescription() {
        return description;
    }

    public EventStatus getStatus() {
        return status;
    }

    public void setStatus(EventStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return String.format("[%s] Event#%d: %s - %s (%s)",
                timestamp.format(formatter),
                eventId,
                eventType,
                description,
                status);
    }

    /**
     * Вложенный класс для деталей события
     */
    public static class EventDetails {
        private final String userId;
        private final double amount;
        private final String metadata;

        public EventDetails(String userId, double amount, String metadata) {
            this.userId = userId;
            this.amount = amount;
            this.metadata = metadata;
        }

        public String getUserId() { return userId; }
        public double getAmount() { return amount; }
        public String getMetadata() { return metadata; }

        @Override
        public String toString() {
            return String.format("User: %s, Amount: %.2f, Metadata: %s",
                    userId, amount, metadata);
        }
    }
}
