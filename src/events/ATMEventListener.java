package events;

/**
 * Слушатель событий банкомата
 * Использует паттерн Observer для обработки различных типов событий
 */
public interface ATMEventListener {
    /**
     * Вызывается при возникновении события аутентификации
     */
    void onAuthenticationEvent(AuthenticationEvent event);

    /**
     * Вызывается при возникновении события транзакции
     */
    void onTransactionEvent(TransactionEvent event);

    /**
     * Вызывается при возникновении события платежа
     */
    void onPaymentEvent(PaymentEvent event);

    /**
     * Вызывается при возникновении события перевода
     */
    void onTransferEvent(TransferEvent event);

    /**
     * Вызывается при возникновении ошибки
     */
    void onErrorEvent(ErrorEvent event);

    /**
     * Получить имя слушателя
     */
    String getListenerName();
}
