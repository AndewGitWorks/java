package events;

/**
 * Событие ошибки и системных проблем
 */
public class ErrorEvent extends ATMEvent {
    public enum ErrorType {
        AUTHENTICATION_ERROR,   // Ошибка аутентификации
        INSUFFICIENT_BALANCE,   // Недостаточно средств
        INVALID_AMOUNT,         // Неверная сумма
        CARD_BLOCKED,           // Карта заблокирована
        NETWORK_ERROR,          // Ошибка сети
        DATABASE_ERROR,         // Ошибка БД
        ATM_MALFUNCTION,        // Неисправность банкомата
        INVALID_INPUT,          // Неверный ввод
        OPERATION_CANCELLED,    // Операция отменена
        SYSTEM_MAINTENANCE      // Техническое обслуживание
    }

    private ErrorType errorType;
    private String errorCode;
    private String errorMessage;
    private String stackTrace;
    private int userId;
    private boolean isRecoverable;

    public ErrorEvent(ErrorType type, String errorCode, String errorMessage) {
        super("ERROR", "System error - " + type);
        this.errorType = type;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.isRecoverable = !type.equals(ErrorType.ATM_MALFUNCTION);
    }

    public ErrorEvent(ErrorType type, String errorCode, String errorMessage,
                     String stackTrace, int userId) {
        this(type, errorCode, errorMessage);
        this.stackTrace = stackTrace;
        this.userId = userId;
    }

    public ErrorType getErrorType() { return errorType; }
    public String getErrorCode() { return errorCode; }
    public String getErrorMessage() { return errorMessage; }
    public String getStackTrace() { return stackTrace; }
    public int getUserId() { return userId; }
    public boolean isRecoverable() { return isRecoverable; }

    /**
     * Вложенный класс для информации об ошибке и её решении
     */
    public static class ErrorContext {
        private final String operationType;
        private final String operationId;
        private final long errorTime;
        private final String suggestedAction;
        private final int retryCount;

        public ErrorContext(String operationType, String operationId,
                          String suggestedAction, int retryCount) {
            this.operationType = operationType;
            this.operationId = operationId;
            this.errorTime = System.currentTimeMillis();
            this.suggestedAction = suggestedAction;
            this.retryCount = retryCount;
        }

        public String getOperationType() { return operationType; }
        public String getOperationId() { return operationId; }
        public long getErrorTime() { return errorTime; }
        public String getSuggestedAction() { return suggestedAction; }
        public int getRetryCount() { return retryCount; }

        @Override
        public String toString() {
            return String.format("Operation: %s (#%s) | Action: %s | Retries: %d",
                    operationType, operationId, suggestedAction, retryCount);
        }
    }

    /**
     * Вложенный класс для логирования и мониторинга ошибок
     */
    public static class ErrorLog {
        private final String logId;
        private final String severity;
        private final String[] affectedSystems;
        private final boolean requiresNotification;

        public ErrorLog(String logId, String severity, String[] affectedSystems,
                       boolean requiresNotification) {
            this.logId = logId;
            this.severity = severity;
            this.affectedSystems = affectedSystems;
            this.requiresNotification = requiresNotification;
        }

        public String getLogId() { return logId; }
        public String getSeverity() { return severity; }
        public String[] getAffectedSystems() { return affectedSystems; }
        public boolean isRequiresNotification() { return requiresNotification; }

        @Override
        public String toString() {
            return String.format("Log#%s | Severity: %s | Systems: %s | Notify: %s",
                    logId, severity, String.join(", ", affectedSystems), requiresNotification);
        }
    }

    @Override
    public String toString() {
        return super.toString() + String.format(" | Error: %s (%s) | Message: %s | Recoverable: %s",
                errorType, errorCode, errorMessage, isRecoverable);
    }
}
