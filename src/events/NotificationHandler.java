package events;

/**
 * ВЛОЖЕННЫЙ КЛАСС РЕАЛИЗАЦИЯ: Обработчик уведомлений системы
 * Отправляет уведомления пользователям и администраторам о важных событиях
 */
public class NotificationHandler implements ATMEventListener {
    private static final String LISTENER_NAME = "NotificationHandler";

    @Override
    public void onAuthenticationEvent(AuthenticationEvent event) {
        if (!event.isSuccess()) {
            // Отправить уведомление об ошибке входа
            sendNotification(
                    "SECURITY_ALERT",
                    "Попытка входа не удалась",
                    event.getUserId(),
                    "Причина: " + event.getReason()
            );
        }
    }

    @Override
    public void onTransactionEvent(TransactionEvent event) {
        // Отправить уведомление о крупной транзакции
        if (event.getAmount() > 50000) {
            sendNotification(
                    "LARGE_TRANSACTION",
                    "Крупная операция выполнена",
                    String.valueOf(event.getUserId()),
                    "Сумма: " + event.getAmount() + " " + event.getCurrency()
            );
        }
    }

    @Override
    public void onPaymentEvent(PaymentEvent event) {
        // Отправить SMS/Email подтверждение платежа
        sendNotification(
                "PAYMENT_CONFIRMATION",
                "Платеж выполнен успешно",
                String.valueOf(event.getUserId()),
                "Сумма: " + event.getAmount() + " ₽, Получатель: " + event.getRecipientId()
        );
    }

    @Override
    public void onTransferEvent(TransferEvent event) {
        if (event.getStatus() == ATMEvent.EventStatus.COMPLETED) {
            // Отправить уведомление об успешном переводе
            sendNotification(
                    "TRANSFER_COMPLETED",
                    "Перевод денег выполнен",
                    String.valueOf(event.getSenderId()),
                    "Получатель: " + event.getRecipientCard() + ", Сумма: " + event.getAmount() + " ₽"
            );
        } else if (event.getStatus() == ATMEvent.EventStatus.FAILED) {
            // Отправить уведомление об ошибке перевода
            sendNotification(
                    "TRANSFER_FAILED",
                    "Перевод не выполнен",
                    String.valueOf(event.getSenderId()),
                    "Попробуйте позже или свяжитесь со службой поддержки"
            );
        }
    }

    @Override
    public void onErrorEvent(ErrorEvent event) {
        if (event.getErrorType() == ErrorEvent.ErrorType.CARD_BLOCKED) {
            // Критическое уведомление - карта заблокирована
            sendNotification(
                    "CRITICAL",
                    "Карта заблокирована",
                    String.valueOf(event.getUserId()),
                    "Свяжитесь с банком для получения дополнительной информации"
            );
        } else if (event.getErrorType() == ErrorEvent.ErrorType.ATM_MALFUNCTION) {
            // Уведомление администратору о неисправности
            sendNotification(
                    "ADMIN_ALERT",
                    "Неисправность банкомата",
                    "ADMIN",
                    "Требуется техническое обслуживание: " + event.getErrorMessage()
            );
        }
    }

    /**
     * Вложенный класс для отправки уведомлений
     */
    private static class NotificationSender {
        private final String channelType;

        NotificationSender(String channelType) {
            this.channelType = channelType;
        }

        void send(String recipient, String title, String message) {
            System.out.println(String.format(
                    "[NOTIFICATION-%s] To: %s | Title: %s | Message: %s",
                    channelType, recipient, title, message
            ));
        }
    }

    /**
     * Отправить уведомление через различные каналы
     */
    private void sendNotification(String priority, String title, String recipient, String message) {
        // Отправить через SMS
        new NotificationSender("SMS").send(recipient, title, message);
        
        // Отправить через Email
        new NotificationSender("EMAIL").send(recipient, title, message);
        
        // Отправить через push-уведомление
        new NotificationSender("PUSH").send(recipient, title, message);
        
        System.out.println(String.format("[NOTIFICATION] Priority: %s | Recipient: %s",
                priority, recipient));
    }

    @Override
    public String getListenerName() {
        return LISTENER_NAME;
    }
}
