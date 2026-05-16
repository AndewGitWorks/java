package events;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * ПРИМЕР РАСШИРЕНИЯ: Пользовательский обработчик для сохранения событий в файл
 * Демонстрирует как создать новый слушатель для расширения функциональности
 */
public class FileEventLogger implements ATMEventListener {
    private static final String LISTENER_NAME = "FileEventLogger";
    private static final String LOG_FILE_PATH = "atm_events.log";
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    @Override
    public void onAuthenticationEvent(AuthenticationEvent event) {
        logToFile(String.format(
                "[AUTH] User: %s | Type: %s | Success: %s | Time: %s",
                event.getUserId(),
                event.getAuthType(),
                event.isSuccess(),
                formatTime(event.getTimestamp())
        ));
    }

    @Override
    public void onTransactionEvent(TransactionEvent event) {
        logToFile(String.format(
                "[TRANS] User: %d | Type: %s | Amount: %.2f | Balance: %.2f → %.2f | Time: %s",
                event.getUserId(),
                event.getTransactionType(),
                event.getAmount(),
                event.getPreviousBalance(),
                event.getNewBalance(),
                formatTime(event.getTimestamp())
        ));
    }

    @Override
    public void onPaymentEvent(PaymentEvent event) {
        logToFile(String.format(
                "[PAYMENT] User: %d | Type: %s | Amount: %.2f | Recipient: %s | Time: %s",
                event.getUserId(),
                event.getPaymentType(),
                event.getAmount(),
                event.getRecipientId(),
                formatTime(event.getTimestamp())
        ));
    }

    @Override
    public void onTransferEvent(TransferEvent event) {
        logToFile(String.format(
                "[TRANSFER] Sender: %d | From: %s | To: %s | Amount: %.2f | Status: %s | Time: %s",
                event.getSenderId(),
                maskCard(event.getSenderCard()),
                maskCard(event.getRecipientCard()),
                event.getAmount(),
                event.getStatus(),
                formatTime(event.getTimestamp())
        ));
    }

    @Override
    public void onErrorEvent(ErrorEvent event) {
        logToFile(String.format(
                "[ERROR] Type: %s | Code: %s | Message: %s | Recoverable: %s | Time: %s",
                event.getErrorType(),
                event.getErrorCode(),
                event.getErrorMessage(),
                event.isRecoverable(),
                formatTime(event.getTimestamp())
        ));
    }

    @Override
    public String getListenerName() {
        return LISTENER_NAME;
    }

    /**
     * Форматирование времени события
     */
    private String formatTime(LocalDateTime dateTime) {
        return dateTime.format(formatter);
    }

    /**
     * Маскирование номера карты
     */
    private String maskCard(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 4) {
            return "****";
        }
        return "****" + cardNumber.substring(cardNumber.length() - 4);
    }

    /**
     * Запись события в лог-файл
     */
    private synchronized void logToFile(String message) {
        try (FileWriter writer = new FileWriter(LOG_FILE_PATH, true)) {
            writer.write(message + "\n");
            writer.flush();
        } catch (IOException e) {
            System.err.println("Ошибка при записи в лог-файл: " + e.getMessage());
        }
    }
}

/**
 * ПРИМЕР РАСШИРЕНИЯ: Аналитический обработчик событий
 * Собирает статистику по операциям для аналитики
 */
class AnalyticsEventHandler implements ATMEventListener {
    private static final String LISTENER_NAME = "AnalyticsHandler";

    // Вложенный класс для статистики
    public static class OperationStatistics {
        public long totalWithdrawals = 0;
        public long totalDeposits = 0;
        public long totalPayments = 0;
        public long totalTransfers = 0;
        public long totalErrors = 0;
        public double totalWithdrawnAmount = 0;
        public double totalDepositedAmount = 0;
        public double totalPaymentAmount = 0;
        public double totalTransferAmount = 0;

        @Override
        public String toString() {
            return String.format(
                    "Statistics: Withdrawals=%d (%.2f), Deposits=%d (%.2f), " +
                    "Payments=%d (%.2f), Transfers=%d (%.2f), Errors=%d",
                    totalWithdrawals, totalWithdrawnAmount,
                    totalDeposits, totalDepositedAmount,
                    totalPayments, totalPaymentAmount,
                    totalTransfers, totalTransferAmount,
                    totalErrors
            );
        }
    }

    private final OperationStatistics stats = new OperationStatistics();

    @Override
    public void onAuthenticationEvent(AuthenticationEvent event) {
        // Сбор статистики входов/выходов
        System.out.println("[ANALYTICS] Auth attempt: " + event.getAuthType());
    }

    @Override
    public void onTransactionEvent(TransactionEvent event) {
        switch (event.getTransactionType()) {
            case WITHDRAW:
                stats.totalWithdrawals++;
                stats.totalWithdrawnAmount += event.getAmount();
                break;
            case DEPOSIT:
                stats.totalDeposits++;
                stats.totalDepositedAmount += event.getAmount();
                break;
            default:
                break;
        }
        printStatistics();
    }

    @Override
    public void onPaymentEvent(PaymentEvent event) {
        stats.totalPayments++;
        stats.totalPaymentAmount += event.getAmount();
        printStatistics();
    }

    @Override
    public void onTransferEvent(TransferEvent event) {
        stats.totalTransfers++;
        stats.totalTransferAmount += event.getAmount();
        printStatistics();
    }

    @Override
    public void onErrorEvent(ErrorEvent event) {
        stats.totalErrors++;
        printStatistics();
    }

    @Override
    public String getListenerName() {
        return LISTENER_NAME;
    }

    private void printStatistics() {
        System.out.println("[ANALYTICS] " + stats);
    }

    public OperationStatistics getStatistics() {
        return stats;
    }
}

/**
 * ПРИМЕР РАСШИРЕНИЯ: Обработчик для отправки данных на удаленный сервер
 * Демонстрирует асинхронную обработку событий
 */
class RemoteServerEventHandler implements ATMEventListener {
    private static final String LISTENER_NAME = "RemoteServerHandler";

    /**
     * Вложенный класс для представления пакета данных для отправки
     */
    private static class EventDataPacket {
        private final String eventType;
        private final String eventData;
        private final long timestamp;
        private final String sourceATM;

        EventDataPacket(String eventType, String eventData, String sourceATM) {
            this.eventType = eventType;
            this.eventData = eventData;
            this.timestamp = System.currentTimeMillis();
            this.sourceATM = sourceATM;
        }

        @Override
        public String toString() {
            return String.format(
                    "Packet{type='%s', source='%s', time=%d, data=%s}",
                    eventType, sourceATM, timestamp, eventData
            );
        }

        // Отправить пакет на удаленный сервер (имитация)
        void sendToServer() {
            System.out.println("[SERVER] Sending: " + this);
            // Здесь была бы реальная отправка HTTP POST/REST API вызова
        }
    }

    @Override
    public void onAuthenticationEvent(AuthenticationEvent event) {
        EventDataPacket packet = new EventDataPacket(
                "AUTH",
                "user=" + event.getUserId() + "&type=" + event.getAuthType(),
                "ATM-001"
        );
        sendAsynchronously(packet);
    }

    @Override
    public void onTransactionEvent(TransactionEvent event) {
        EventDataPacket packet = new EventDataPacket(
                "TRANSACTION",
                "userId=" + event.getUserId() + "&amount=" + event.getAmount(),
                "ATM-001"
        );
        sendAsynchronously(packet);
    }

    @Override
    public void onPaymentEvent(PaymentEvent event) {
        EventDataPacket packet = new EventDataPacket(
                "PAYMENT",
                "userId=" + event.getUserId() + "&recipient=" + event.getRecipientId(),
                "ATM-001"
        );
        sendAsynchronously(packet);
    }

    @Override
    public void onTransferEvent(TransferEvent event) {
        EventDataPacket packet = new EventDataPacket(
                "TRANSFER",
                "sender=" + event.getSenderId() + "&amount=" + event.getAmount(),
                "ATM-001"
        );
        sendAsynchronously(packet);
    }

    @Override
    public void onErrorEvent(ErrorEvent event) {
        EventDataPacket packet = new EventDataPacket(
                "ERROR",
                "type=" + event.getErrorType() + "&code=" + event.getErrorCode(),
                "ATM-001"
        );
        sendAsynchronously(packet);
    }

    @Override
    public String getListenerName() {
        return LISTENER_NAME;
    }

    /**
     * Асинхронная отправка пакета (в отдельном потоке)
     */
    private void sendAsynchronously(EventDataPacket packet) {
        Thread thread = new Thread(() -> {
            try {
                Thread.sleep(100); // Имитация задержки сети
                packet.sendToServer();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        thread.setDaemon(true);
        thread.setName("RemoteServer-Sender");
        thread.start();
    }
}
