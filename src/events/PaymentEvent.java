package events;

/**
 * Событие платежа (телефон, ЖКХ, штрафы)
 */
public class PaymentEvent extends ATMEvent {
    public enum PaymentType {
        PHONE_PAYMENT,      // Оплата телефона
        UTILITIES_PAYMENT,  // Оплата ЖКХ
        PENALTY_PAYMENT,    // Оплата штрафов
        INSURANCE_PAYMENT   // Оплата страховки
    }

    private int userId;
    private PaymentType paymentType;
    private double amount;
    private String recipientId;
    private String description;
    private double currentBalance;

    public PaymentEvent(int userId, PaymentType type, double amount,
                       String recipientId, String description, double currentBalance) {
        super("PAYMENT", "Payment operation - " + type);
        this.userId = userId;
        this.paymentType = type;
        this.amount = amount;
        this.recipientId = recipientId;
        this.description = description;
        this.currentBalance = currentBalance;
    }

    public int getUserId() { return userId; }
    public PaymentType getPaymentType() { return paymentType; }
    public double getAmount() { return amount; }
    public String getRecipientId() { return recipientId; }
    public String getDescription() { return description; }
    public double getCurrentBalance() { return currentBalance; }

    /**
     * Вложенный класс для информации о получателе
     */
    public static class RecipientInfo {
        private final String recipientName;
        private final String recipientAccount;
        private final String bank;
        private final String bic;

        public RecipientInfo(String recipientName, String recipientAccount,
                           String bank, String bic) {
            this.recipientName = recipientName;
            this.recipientAccount = recipientAccount;
            this.bank = bank;
            this.bic = bic;
        }

        public String getRecipientName() { return recipientName; }
        public String getRecipientAccount() { return recipientAccount; }
        public String getBank() { return bank; }
        public String getBic() { return bic; }

        @Override
        public String toString() {
            return String.format("Recipient: %s, Account: %s, Bank: %s, BIC: %s",
                    recipientName, recipientAccount, bank, bic);
        }
    }

    /**
     * Вложенный класс для квитанции платежа
     */
    public static class PaymentReceipt {
        private final String receiptNumber;
        private final long receiptDate;
        private final String paymentStatus;
        private final String confirmationCode;

        public PaymentReceipt(String receiptNumber, long receiptDate,
                            String paymentStatus, String confirmationCode) {
            this.receiptNumber = receiptNumber;
            this.receiptDate = receiptDate;
            this.paymentStatus = paymentStatus;
            this.confirmationCode = confirmationCode;
        }

        public String getReceiptNumber() { return receiptNumber; }
        public long getReceiptDate() { return receiptDate; }
        public String getPaymentStatus() { return paymentStatus; }
        public String getConfirmationCode() { return confirmationCode; }

        @Override
        public String toString() {
            return String.format("Receipt#%s | Status: %s | Code: %s",
                    receiptNumber, paymentStatus, confirmationCode);
        }
    }

    @Override
    public String toString() {
        return super.toString() + String.format(" | Type: %s | Amount: %.2f | Recipient: %s | Description: %s",
                paymentType, amount, recipientId, description);
    }
}
