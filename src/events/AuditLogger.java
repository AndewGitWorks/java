package events;

/**
 * ВЛОЖЕННЫЙ КЛАСС РЕАЛИЗАЦИЯ: Аудитор системы
 * Логирует все события для целей аудита и безопасности
 */
public class AuditLogger implements ATMEventListener {
    private static final String LISTENER_NAME = "AuditLogger";

    @Override
    public void onAuthenticationEvent(AuthenticationEvent event) {
        String action = event.getAuthType().toString();
        String result = event.isSuccess() ? "SUCCESS" : "FAILED";
        System.out.println(String.format(
                "[AUDIT] Authentication | User: %s | Action: %s | Result: %s | Time: %s",
                event.getUserId(), action, result, event.getTimestamp()
        ));
    }

    @Override
    public void onTransactionEvent(TransactionEvent event) {
        System.out.println(String.format(
                "[AUDIT] Transaction | User: %d | Type: %s | Amount: %.2f | Balance: %.2f → %.2f | Time: %s",
                event.getUserId(),
                event.getTransactionType(),
                event.getAmount(),
                event.getPreviousBalance(),
                event.getNewBalance(),
                event.getTimestamp()
        ));
    }

    @Override
    public void onPaymentEvent(PaymentEvent event) {
        System.out.println(String.format(
                "[AUDIT] Payment | User: %d | Type: %s | Amount: %.2f | Recipient: %s | Time: %s",
                event.getUserId(),
                event.getPaymentType(),
                event.getAmount(),
                event.getRecipientId(),
                event.getTimestamp()
        ));
    }

    @Override
    public void onTransferEvent(TransferEvent event) {
        System.out.println(String.format(
                "[AUDIT] Transfer | Sender: %d | From: %s | To: %s | Amount: %.2f | Time: %s",
                event.getSenderId(),
                event.getSenderCard(),
                event.getRecipientCard(),
                event.getAmount(),
                event.getTimestamp()
        ));
    }

    @Override
    public void onErrorEvent(ErrorEvent event) {
        System.out.println(String.format(
                "[AUDIT] Error | Type: %s | Code: %s | Message: %s | Recoverable: %s | Time: %s",
                event.getErrorType(),
                event.getErrorCode(),
                event.getErrorMessage(),
                event.isRecoverable(),
                event.getTimestamp()
        ));
    }

    @Override
    public String getListenerName() {
        return LISTENER_NAME;
    }
}
