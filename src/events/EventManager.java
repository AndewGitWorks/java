package events;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * Менеджер событий банкомата
 * Реализует паттерн Delegation для распределения событий между слушателями
 * и использует вложенные классы для специализированной обработки
 */
public class EventManager {
    private static final EventManager instance = new EventManager();
    private final List<ATMEventListener> listeners = new CopyOnWriteArrayList<>();
    private final ExecutorService eventExecutor = Executors.newFixedThreadPool(5);
    private final Queue<ATMEvent> eventQueue = new ConcurrentLinkedQueue<>();
    private final Map<String, EventProcessor> eventProcessors = new ConcurrentHashMap<>();
    private final EventLogger eventLogger = new EventLogger();
    private volatile boolean isRunning = true;

    private EventManager() {
        initializeProcessors();
        startEventProcessor();
    }

    public static EventManager getInstance() {
        return instance;
    }

    /**
     * Инициализация специализированных обработчиков событий
     */
    private void initializeProcessors() {
        eventProcessors.put("AUTHENTICATION", new AuthenticationProcessor());
        eventProcessors.put("TRANSACTION", new TransactionProcessor());
        eventProcessors.put("PAYMENT", new PaymentProcessor());
        eventProcessors.put("TRANSFER", new TransferProcessor());
        eventProcessors.put("ERROR", new ErrorProcessor());
    }

    /**
     * Регистрация слушателя событий
     */
    public void addListener(ATMEventListener listener) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
            eventLogger.log("Listener registered: " + listener.getListenerName());
        }
    }

    /**
     * Удаление слушателя событий
     */
    public void removeListener(ATMEventListener listener) {
        if (listeners.remove(listener)) {
            eventLogger.log("Listener removed: " + listener.getListenerName());
        }
    }

    /**
     * Издание события (делегирование обработки)
     */
    public void publishEvent(ATMEvent event) {
        if (event != null) {
            eventQueue.offer(event);
            eventLogger.logEvent(event);
        }
    }

    /**
     * Обработка события синхронно
     */
    public void fireEvent(ATMEvent event) {
        if (event == null) return;

        EventProcessor processor = eventProcessors.get(event.getEventType());
        if (processor != null) {
            processor.process(event);
        }

        delegateEventToListeners(event);
    }

    /**
     * Делегирование события всем зарегистрированным слушателям
     */
    private void delegateEventToListeners(ATMEvent event) {
        if (event instanceof AuthenticationEvent) {
            AuthenticationEvent authEvent = (AuthenticationEvent) event;
            listeners.forEach(listener -> listener.onAuthenticationEvent(authEvent));
        } else if (event instanceof TransactionEvent) {
            TransactionEvent transEvent = (TransactionEvent) event;
            listeners.forEach(listener -> listener.onTransactionEvent(transEvent));
        } else if (event instanceof PaymentEvent) {
            PaymentEvent payEvent = (PaymentEvent) event;
            listeners.forEach(listener -> listener.onPaymentEvent(payEvent));
        } else if (event instanceof TransferEvent) {
            TransferEvent transferEvent = (TransferEvent) event;
            listeners.forEach(listener -> listener.onTransferEvent(transferEvent));
        } else if (event instanceof ErrorEvent) {
            ErrorEvent errorEvent = (ErrorEvent) event;
            listeners.forEach(listener -> listener.onErrorEvent(errorEvent));
        }
    }

    /**
     * Получить все события определенного типа
     */
    public List<ATMEvent> getEventsByType(String eventType) {
        return eventQueue.stream()
                .filter(e -> e.getEventType().equals(eventType))
                .collect(Collectors.toList());
    }

    /**
     * Получить все события с определенным статусом
     */
    public List<ATMEvent> getEventsByStatus(ATMEvent.EventStatus status) {
        return eventQueue.stream()
                .filter(e -> e.getStatus().equals(status))
                .collect(Collectors.toList());
    }

    /**
     * Запуск фонового процессора событий
     */
    private void startEventProcessor() {
        Thread processorThread = new Thread(() -> {
            while (isRunning) {
                try {
                    ATMEvent event = eventQueue.poll();
                    if (event != null) {
                        fireEvent(event);
                    } else {
                        Thread.sleep(100);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
        processorThread.setDaemon(true);
        processorThread.setName("ATM-EventProcessor");
        processorThread.start();
    }

    /**
     * Остановка менеджера событий
     */
    public void shutdown() {
        isRunning = false;
        eventExecutor.shutdown();
        eventLogger.flush();
    }

    public EventLogger getEventLogger() {
        return eventLogger;
    }

    /**
     * ВЛОЖЕННЫЙ КЛАСС: Логгер событий
     */
    public static class EventLogger {
        private final List<String> logs = new ArrayList<>();
        private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        private synchronized void log(String message) {
            String logMessage = "[" + dateFormat.format(new Date()) + "] " + message;
            logs.add(logMessage);
            System.out.println(logMessage);
        }

        private synchronized void logEvent(ATMEvent event) {
            log(event.toString());
        }

        public synchronized List<String> getLogs() {
            return new ArrayList<>(logs);
        }

        public synchronized void flush() {
            logs.clear();
        }

        public synchronized void clear() {
            logs.clear();
        }
    }

    /**
     * ВЛОЖЕННЫЙ КЛАСС: Абстрактный обработчик событий
     */
    private abstract static class EventProcessor {
        abstract void process(ATMEvent event);
    }

    /**
     * ВЛОЖЕННЫЙ КЛАСС: Обработчик событий аутентификации
     */
    private static class AuthenticationProcessor extends EventProcessor {
        @Override
        void process(ATMEvent event) {
            if (event instanceof AuthenticationEvent) {
                AuthenticationEvent authEvent = (AuthenticationEvent) event;
                if (authEvent.isSuccess()) {
                    authEvent.setStatus(ATMEvent.EventStatus.COMPLETED);
                    System.out.println("[AUTH-PROCESSOR] Authentication successful for: " +
                            authEvent.getUserId());
                } else {
                    authEvent.setStatus(ATMEvent.EventStatus.FAILED);
                    System.out.println("[AUTH-PROCESSOR] Authentication failed: " +
                            authEvent.getReason());
                }
            }
        }
    }

    /**
     * ВЛОЖЕННЫЙ КЛАСС: Обработчик событий транзакций
     */
    private static class TransactionProcessor extends EventProcessor {
        @Override
        void process(ATMEvent event) {
            if (event instanceof TransactionEvent) {
                TransactionEvent transEvent = (TransactionEvent) event;
                double difference = transEvent.getNewBalance() - transEvent.getPreviousBalance();
                System.out.println("[TRANSACTION-PROCESSOR] Processing " +
                        transEvent.getTransactionType() + ": " + difference +
                        " " + transEvent.getCurrency());
                transEvent.setStatus(ATMEvent.EventStatus.IN_PROGRESS);
            }
        }
    }

    /**
     * ВЛОЖЕННЫЙ КЛАСС: Обработчик событий платежей
     */
    private static class PaymentProcessor extends EventProcessor {
        @Override
        void process(ATMEvent event) {
            if (event instanceof PaymentEvent) {
                PaymentEvent payEvent = (PaymentEvent) event;
                System.out.println("[PAYMENT-PROCESSOR] Processing payment: " +
                        payEvent.getPaymentType() + " to " + payEvent.getRecipientId() +
                        " amount: " + payEvent.getAmount());
                payEvent.setStatus(ATMEvent.EventStatus.IN_PROGRESS);
            }
        }
    }

    /**
     * ВЛОЖЕННЫЙ КЛАСС: Обработчик событий переводов
     */
    private static class TransferProcessor extends EventProcessor {
        @Override
        void process(ATMEvent event) {
            if (event instanceof TransferEvent) {
                TransferEvent transferEvent = (TransferEvent) event;
                System.out.println("[TRANSFER-PROCESSOR] Processing transfer: " +
                        transferEvent.getSenderCard() + " → " +
                        transferEvent.getRecipientCard() + " amount: " +
                        transferEvent.getAmount());
                transferEvent.setStatus(ATMEvent.EventStatus.IN_PROGRESS);
            }
        }
    }

    /**
     * ВЛОЖЕННЫЙ КЛАСС: Обработчик событий ошибок
     */
    private static class ErrorProcessor extends EventProcessor {
        @Override
        void process(ATMEvent event) {
            if (event instanceof ErrorEvent) {
                ErrorEvent errorEvent = (ErrorEvent) event;
                System.out.println("[ERROR-PROCESSOR] Error: " +
                        errorEvent.getErrorType() + " - " + errorEvent.getErrorMessage() +
                        " (Code: " + errorEvent.getErrorCode() + ")");
                errorEvent.setStatus(ATMEvent.EventStatus.FAILED);
            }
        }
    }

    /**
     * Вспомогательный класс для форматирования времени
     */
    private static class SimpleDateFormat {
        private final java.text.SimpleDateFormat formatter;

        SimpleDateFormat(String pattern) {
            this.formatter = new java.text.SimpleDateFormat(pattern);
        }

        synchronized String format(Date date) {
            return formatter.format(date);
        }
    }
}
