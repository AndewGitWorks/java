package events;

/**
 * Событие транзакции (снятие/пополнение наличных)
 */
public class TransactionEvent extends ATMEvent {
    public enum TransactionType {
        WITHDRAW,   // Снятие наличных
        DEPOSIT,    // Пополнение счета
        BALANCE_INQUIRY // Запрос баланса
    }

    private int userId;
    private TransactionType transactionType;
    private double amount;
    private double previousBalance;
    private double newBalance;
    private String currency;

    public TransactionEvent(int userId, TransactionType type, double amount,
                           double previousBalance, double newBalance) {
        super("TRANSACTION", "Cash transaction - " + type);
        this.userId = userId;
        this.transactionType = type;
        this.amount = amount;
        this.previousBalance = previousBalance;
        this.newBalance = newBalance;
        this.currency = "RUB";
    }

    public int getUserId() { return userId; }
    public TransactionType getTransactionType() { return transactionType; }
    public double getAmount() { return amount; }
    public double getPreviousBalance() { return previousBalance; }
    public double getNewBalance() { return newBalance; }
    public String getCurrency() { return currency; }

    /**
     * Вложенный класс для расчета комиссии и налогов
     */
    public static class TransactionFee {
        private final double feeAmount;
        private final double feePercentage;
        private final String feeType;

        public TransactionFee(double feeAmount, double feePercentage, String feeType) {
            this.feeAmount = feeAmount;
            this.feePercentage = feePercentage;
            this.feeType = feeType;
        }

        public double getFeeAmount() { return feeAmount; }
        public double getFeePercentage() { return feePercentage; }
        public String getFeeType() { return feeType; }

        @Override
        public String toString() {
            return String.format("Fee: %.2f (%s, %.2f%%)", 
                    feeAmount, feeType, feePercentage);
        }
    }

    /**
     * Вложенный класс для деталей операции
     */
    public static class OperationDetails {
        private final String operationId;
        private final String atmLocation;
        private final String serialNumber;

        public OperationDetails(String operationId, String atmLocation, String serialNumber) {
            this.operationId = operationId;
            this.atmLocation = atmLocation;
            this.serialNumber = serialNumber;
        }

        public String getOperationId() { return operationId; }
        public String getAtmLocation() { return atmLocation; }
        public String getSerialNumber() { return serialNumber; }
    }

    @Override
    public String toString() {
        return super.toString() + String.format(" | Type: %s | Amount: %.2f %s | Balance: %.2f → %.2f",
                transactionType, amount, currency, previousBalance, newBalance);
    }
}
