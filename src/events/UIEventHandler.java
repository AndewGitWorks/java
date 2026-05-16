package events;

/**
 * ВЛОЖЕННЫЙ КЛАСС РЕАЛИЗАЦИЯ: Обработчик событий пользовательского интерфейса
 * Обновляет пользовательский интерфейс в ответ на события системы
 */
public class UIEventHandler implements ATMEventListener {
    private static final String LISTENER_NAME = "UIEventHandler";

    @Override
    public void onAuthenticationEvent(AuthenticationEvent event) {
        String message;
        if (event.isSuccess()) {
            message = String.format("✓ Добро пожаловать, пользователь %s", event.getUserId());
            System.out.println("[UI] Success: " + message);
            // TODO: Обновить UI для показа приветственного сообщения
        } else {
            message = String.format("✗ Ошибка входа: %s", event.getReason());
            System.out.println("[UI] Error: " + message);
            // TODO: Показать диалог с ошибкой
        }
    }

    @Override
    public void onTransactionEvent(TransactionEvent event) {
        String operationType = event.getTransactionType().equals(
                TransactionEvent.TransactionType.WITHDRAW) ? "Снятие" : "Пополнение";
        
        System.out.println(String.format(
                "[UI] %s: %.2f %s | Новый баланс: %.2f",
                operationType,
                event.getAmount(),
                event.getCurrency(),
                event.getNewBalance()
        ));
        // TODO: Обновить отображение баланса
        // TODO: Показать анимацию операции
    }

    @Override
    public void onPaymentEvent(PaymentEvent event) {
        System.out.println(String.format(
                "[UI] Платеж выполнен: %s на сумму %.2f ₽",
                event.getDescription(),
                event.getAmount()
        ));
        // TODO: Показать квитанцию платежа
        // TODO: Обновить историю операций
    }

    @Override
    public void onTransferEvent(TransferEvent event) {
        String statusMessage;
        if (event.getStatus() == ATMEvent.EventStatus.COMPLETED) {
            statusMessage = "Перевод успешно выполнен";
        } else if (event.getStatus() == ATMEvent.EventStatus.FAILED) {
            statusMessage = "Перевод отклонен";
        } else {
            statusMessage = "Перевод обрабатывается...";
        }
        
        System.out.println(String.format(
                "[UI] %s: %.2f ₽ на сумму %s",
                statusMessage,
                event.getAmount(),
                event.getTransferPurpose()
        ));
        // TODO: Показать статус перевода
    }

    @Override
    public void onErrorEvent(ErrorEvent event) {
        String errorDisplay = String.format(
                "Ошибка: %s\nКод: %s\nСообщение: %s",
                event.getErrorType(),
                event.getErrorCode(),
                event.getErrorMessage()
        );
        
        System.out.println("[UI] Error dialog: " + errorDisplay);
        // TODO: Показать диалог ошибки
        // TODO: Если ошибка восстанавливаемая, показать кнопку повтора
        // TODO: Логировать ошибку на сервер
    }

    @Override
    public String getListenerName() {
        return LISTENER_NAME;
    }
}
