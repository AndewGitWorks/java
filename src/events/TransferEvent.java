package events;

/**
 * Событие перевода (между картами, пользователям)
 */
public class TransferEvent extends ATMEvent {
    public enum TransferType {
        TO_CARD,        // Перевод на карту
        TO_USER,        // Перевод пользователю
        BETWEEN_ACCOUNTS // Перевод между счетами
    }

    private int senderId;
    private String senderCard;
    private String recipientCard;
    private int recipientId;
    private TransferType transferType;
    private double amount;
    private String transferPurpose;
    private double senderPreviousBalance;
    private double senderNewBalance;

    public TransferEvent(int senderId, String senderCard, String recipientCard,
                        TransferType type, double amount, String purpose,
                        double previousBalance, double newBalance) {
        super("TRANSFER", "Fund transfer - " + type);
        this.senderId = senderId;
        this.senderCard = senderCard;
        this.recipientCard = recipientCard;
        this.transferType = type;
        this.amount = amount;
        this.transferPurpose = purpose;
        this.senderPreviousBalance = previousBalance;
        this.senderNewBalance = newBalance;
    }

    public int getSenderId() { return senderId; }
    public String getSenderCard() { return senderCard; }
    public String getRecipientCard() { return recipientCard; }
    public TransferType getTransferType() { return transferType; }
    public double getAmount() { return amount; }
    public String getTransferPurpose() { return transferPurpose; }
    public double getSenderPreviousBalance() { return senderPreviousBalance; }
    public double getSenderNewBalance() { return senderNewBalance; }

    public void setRecipientId(int recipientId) {
        this.recipientId = recipientId;
    }
    public int getRecipientId() { return recipientId; }

    /**
     * Вложенный класс для отслеживания статуса перевода
     */
    public static class TransferStatus {
        public enum Status {
            INITIATED,      // Инициирован
            PROCESSING,     // Обработка
            CONFIRMED,      // Подтвержден
            COMPLETED,      // Завершен
            FAILED,         // Ошибка
            CANCELLED       // Отменен
        }

        private final String transferId;
        private Status status;
        private final long createdAt;
        private long completedAt;
        private String failureReason;

        public TransferStatus(String transferId) {
            this.transferId = transferId;
            this.status = Status.INITIATED;
            this.createdAt = System.currentTimeMillis();
        }

        public String getTransferId() { return transferId; }
        public Status getStatus() { return status; }
        public void setStatus(Status status) { this.status = status; }
        public long getCreatedAt() { return createdAt; }
        public long getCompletedAt() { return completedAt; }
        public void setCompletedAt(long time) { this.completedAt = time; }
        public String getFailureReason() { return failureReason; }
        public void setFailureReason(String reason) { this.failureReason = reason; }

        @Override
        public String toString() {
            return String.format("Transfer#%s | Status: %s | Created: %d | Completed: %d",
                    transferId, status, createdAt, completedAt);
        }
    }

    /**
     * Вложенный класс для информации об отправителе и получателе
     */
    public static class TransferParticipants {
        private final String senderName;
        private final String senderBank;
        private final String recipientName;
        private final String recipientBank;

        public TransferParticipants(String senderName, String senderBank,
                                   String recipientName, String recipientBank) {
            this.senderName = senderName;
            this.senderBank = senderBank;
            this.recipientName = recipientName;
            this.recipientBank = recipientBank;
        }

        public String getSenderName() { return senderName; }
        public String getSenderBank() { return senderBank; }
        public String getRecipientName() { return recipientName; }
        public String getRecipientBank() { return recipientBank; }

        @Override
        public String toString() {
            return String.format("Sender: %s (%s) → Recipient: %s (%s)",
                    senderName, senderBank, recipientName, recipientBank);
        }
    }

    @Override
    public String toString() {
        return super.toString() + String.format(" | Type: %s | From: %s → To: %s | Amount: %.2f | Purpose: %s",
                transferType, senderCard, recipientCard, amount, transferPurpose);
    }
}
