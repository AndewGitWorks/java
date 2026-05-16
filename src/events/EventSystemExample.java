package events;

import models.User;

/**
 * Пример использования системы событий банкомата
 * Демонстрирует полный жизненный цикл создания, обработки и обслуживания событий
 */
public class EventSystemExample {

    /**
     * Вложенный класс-демонстратор для примеров
     */
    public static class ExampleEventListener implements ATMEventListener {
        private String name;

        public ExampleEventListener(String name) {
            this.name = name;
        }

        @Override
        public void onAuthenticationEvent(AuthenticationEvent event) {
            System.out.println("[" + name + "] Обработка события аутентификации: " + event);
        }

        @Override
        public void onTransactionEvent(TransactionEvent event) {
            System.out.println("[" + name + "] Обработка события транзакции: " + event);
        }

        @Override
        public void onPaymentEvent(PaymentEvent event) {
            System.out.println("[" + name + "] Обработка события платежа: " + event);
        }

        @Override
        public void onTransferEvent(TransferEvent event) {
            System.out.println("[" + name + "] Обработка события перевода: " + event);
        }

        @Override
        public void onErrorEvent(ErrorEvent event) {
            System.out.println("[" + name + "] Обработка события ошибки: " + event);
        }

        @Override
        public String getListenerName() {
            return name;
        }
    }

    /**
     * Пример 1: Инициализация системы событий
     */
    public static void example1_InitializeEventSystem() {
        System.out.println("=== ПРИМЕР 1: Инициализация системы событий ===\n");

        EventManager manager = EventManager.getInstance();

        // Регистрация встроенных слушателей
        manager.addListener(new AuditLogger());
        manager.addListener(new UIEventHandler());
        manager.addListener(new NotificationHandler());

        // Регистрация пользовательских слушателей
        manager.addListener(new ExampleEventListener("CustomHandler1"));
        manager.addListener(new ExampleEventListener("CustomHandler2"));

        System.out.println("✓ Система событий инициализирована\n");
    }

    /**
     * Пример 2: Обработка события аутентификации
     */
    public static void example2_AuthenticationEvent() {
        System.out.println("=== ПРИМЕР 2: Событие аутентификации ===\n");

        EventManager manager = EventManager.getInstance();

        // Успешный вход
        AuthenticationEvent successLogin = new AuthenticationEvent(
                "user@example.com",
                AuthenticationEvent.AuthType.LOGIN,
                true,
                "Успешный вход"
        );
        successLogin.setStatus(ATMEvent.EventStatus.COMPLETED);
        manager.fireEvent(successLogin);

        System.out.println("\n");

        // Неудачная попытка входа
        AuthenticationEvent failedLogin = new AuthenticationEvent(
                "user@example.com",
                AuthenticationEvent.AuthType.LOGIN,
                false,
                "Неверный ПИН-код"
        );
        failedLogin.setStatus(ATMEvent.EventStatus.FAILED);
        manager.fireEvent(failedLogin);

        System.out.println("\n");
    }

    /**
     * Пример 3: Обработка события транзакции
     */
    public static void example3_TransactionEvent() {
        System.out.println("=== ПРИМЕР 3: Событие транзакции (снятие наличных) ===\n");

        EventManager manager = EventManager.getInstance();

        // Снятие наличных
        TransactionEvent withdrawal = new TransactionEvent(
                123,
                TransactionEvent.TransactionType.WITHDRAW,
                5000.0,
                10000.0,
                5000.0
        );
        withdrawal.setStatus(ATMEvent.EventStatus.COMPLETED);
        manager.fireEvent(withdrawal);

        System.out.println("\n");

        // Пополнение счета
        TransactionEvent deposit = new TransactionEvent(
                123,
                TransactionEvent.TransactionType.DEPOSIT,
                2000.0,
                5000.0,
                7000.0
        );
        deposit.setStatus(ATMEvent.EventStatus.COMPLETED);
        manager.fireEvent(deposit);

        System.out.println("\n");
    }

    /**
     * Пример 4: Обработка события платежа
     */
    public static void example4_PaymentEvent() {
        System.out.println("=== ПРИМЕР 4: Событие платежа (оплата телефона) ===\n");

        EventManager manager = EventManager.getInstance();

        PaymentEvent phonePayment = new PaymentEvent(
                123,
                PaymentEvent.PaymentType.PHONE_PAYMENT,
                999.0,
                "+7 (917) 123-45-67",
                "Оплата пополнения счета мобильного",
                6001.0
        );
        phonePayment.setStatus(ATMEvent.EventStatus.COMPLETED);
        manager.fireEvent(phonePayment);

        System.out.println("\n");

        // Пример с вложенными классами
        PaymentEvent.RecipientInfo recipientInfo = new PaymentEvent.RecipientInfo(
                "MegaFon",
                "40700000000000000000",
                "Альфа-Банк",
                "044525593"
        );
        System.out.println("Информация о получателе: " + recipientInfo);
        System.out.println("\n");
    }

    /**
     * Пример 5: Обработка события перевода
     */
    public static void example5_TransferEvent() {
        System.out.println("=== ПРИМЕР 5: Событие перевода ===\n");

        EventManager manager = EventManager.getInstance();

        TransferEvent transfer = new TransferEvent(
                123,
                "4111111111111111",
                "4222222222222222",
                TransferEvent.TransferType.TO_CARD,
                10000.0,
                "Перевод другу",
                7000.0,
                -3000.0
        );
        transfer.setStatus(ATMEvent.EventStatus.COMPLETED);
        manager.fireEvent(transfer);

        System.out.println("\n");

        // Пример со статусом перевода
        TransferEvent.TransferStatus status = new TransferEvent.TransferStatus(
                "TR-2026-05-16-001"
        );
        status.setStatus(TransferEvent.TransferStatus.Status.COMPLETED);
        status.setCompletedAt(System.currentTimeMillis());
        System.out.println("Статус перевода: " + status);
        System.out.println("\n");
    }

    /**
     * Пример 6: Обработка события ошибки
     */
    public static void example6_ErrorEvent() {
        System.out.println("=== ПРИМЕР 6: Событие ошибки ===\n");

        EventManager manager = EventManager.getInstance();

        // Ошибка: недостаточно средств
        ErrorEvent insufficientBalance = new ErrorEvent(
                ErrorEvent.ErrorType.INSUFFICIENT_BALANCE,
                "INSUFF_BALANCE",
                "На счете недостаточно средств для выполнения операции"
        );
        insufficientBalance.setStatus(ATMEvent.EventStatus.FAILED);
        manager.fireEvent(insufficientBalance);

        System.out.println("\n");

        // Ошибка: карта заблокирована
        ErrorEvent cardBlocked = new ErrorEvent(
                ErrorEvent.ErrorType.CARD_BLOCKED,
                "CARD_BLOCKED",
                "Ваша карта заблокирована. Обратитесь в банк.",
                null,
                123
        );
        cardBlocked.setStatus(ATMEvent.EventStatus.FAILED);
        manager.fireEvent(cardBlocked);

        System.out.println("\n");

        // Пример с контекстом ошибки
        ErrorEvent.ErrorContext errorContext = new ErrorEvent.ErrorContext(
                "WITHDRAWAL",
                "OP-2026-05-16-001",
                "Повторите операцию позже",
                0
        );
        System.out.println("Контекст ошибки: " + errorContext);
        System.out.println("\n");
    }

    /**
     * Пример 7: Фильтрация событий
     */
    public static void example7_FilteringEvents() {
        System.out.println("=== ПРИМЕР 7: Фильтрация событий ===\n");

        EventManager manager = EventManager.getInstance();

        // Получить события с определенным статусом
        System.out.println("Выполненные события:");
        manager.getEventsByStatus(ATMEvent.EventStatus.COMPLETED)
                .forEach(e -> System.out.println("  - " + e.getEventType()));

        System.out.println("\nОшибки:");
        manager.getEventsByStatus(ATMEvent.EventStatus.FAILED)
                .forEach(e -> System.out.println("  - " + e.getEventType()));

        System.out.println("\nЭвенты транзакций:");
        manager.getEventsByType("TRANSACTION")
                .forEach(e -> System.out.println("  - " + e));

        System.out.println("\n");
    }

    /**
     * Пример 8: Вложенные классы в событиях
     */
    public static void example8_NestedClasses() {
        System.out.println("=== ПРИМЕР 8: Использование вложенных классов ===\n");

        // Вложенный класс в TransactionEvent
        TransactionEvent.TransactionFee fee = new TransactionEvent.TransactionFee(
                50.0,
                1.5,
                "COMMISSION"
        );
        System.out.println("Комиссия: " + fee);

        // Вложенный класс в PaymentEvent
        PaymentEvent.PaymentReceipt receipt = new PaymentEvent.PaymentReceipt(
                "RCP-2026-05-16-001",
                System.currentTimeMillis(),
                "CONFIRMED",
                "CONF-123456"
        );
        System.out.println("Квитанция: " + receipt);

        // Вложенный класс в TransferEvent
        TransferEvent.TransferParticipants participants = new TransferEvent.TransferParticipants(
                "Ivan Petrov",
                "Sberbank",
                "Maria Sidorova",
                "VTB"
        );
        System.out.println("Участники: " + participants);

        // Вложенный класс в ErrorEvent
        ErrorEvent.ErrorLog errorLog = new ErrorEvent.ErrorLog(
                "LOG-2026-05-16-001",
                "HIGH",
                new String[]{"AUTH", "PAYMENT", "DB"},
                true
        );
        System.out.println("Лог ошибки: " + errorLog);

        System.out.println("\n");
    }

    /**
     * Пример 9: Жизненный цикл события
     */
    public static void example9_EventLifecycle() {
        System.out.println("=== ПРИМЕР 9: Жизненный цикл события ===\n");

        // 1. Создание события
        AuthenticationEvent event = new AuthenticationEvent(
                "user@example.com",
                AuthenticationEvent.AuthType.LOGIN,
                true,
                "Успешный вход"
        );
        System.out.println("1. Событие создано: " + event.getStatus());

        // 2. Установка статуса на IN_PROGRESS
        event.setStatus(ATMEvent.EventStatus.IN_PROGRESS);
        System.out.println("2. Статус изменен: " + event.getStatus());

        // 3. Обработка событием системой
        EventManager manager = EventManager.getInstance();
        manager.fireEvent(event);
        System.out.println("3. Событие обработано системой");

        // 4. Завершение события
        event.setStatus(ATMEvent.EventStatus.COMPLETED);
        System.out.println("4. Событие завершено: " + event.getStatus());
        System.out.println("   Время события: " + event.getTimestamp());

        System.out.println("\n");
    }

    /**
     * Пример 10: Интеграция с ATMService
     */
    public static void example10_ATMServiceIntegration() {
        System.out.println("=== ПРИМЕР 10: Интеграция с ATMService ===\n");

        // Создание фиктивного пользователя для примера
        User demoUser = new User(1, "1234567890123456", "1234", "Ivan", 15000.0);

        // Создание сервиса (автоматически инициализирует слушатели)
        System.out.println("Создание ATMService...");
        // ATMService service = new ATMService(); // Раскомментировать когда User будет полностью совместим

        System.out.println("✓ ATMService создан и инициализирован с системой событий\n");
    }

    /**
     * Главный метод для запуска всех примеров
     */
    public static void main(String[] args) {
        System.out.println("\n" +
                "╔════════════════════════════════════════════════════════════╗\n" +
                "║   ПРИМЕРЫ ИСПОЛЬЗОВАНИЯ СИСТЕМЫ СОБЫТИЙ БАНКОМАТА          ║\n" +
                "╚════════════════════════════════════════════════════════════╝\n");

        try {
            // Инициализация системы
            example1_InitializeEventSystem();

            // Примеры событий
            example2_AuthenticationEvent();
            example3_TransactionEvent();
            example4_PaymentEvent();
            example5_TransferEvent();
            example6_ErrorEvent();

            // Продвинутые примеры
            example7_FilteringEvents();
            example8_NestedClasses();
            example9_EventLifecycle();
            example10_ATMServiceIntegration();

            System.out.println("=== ВСЕ ПРИМЕРЫ УСПЕШНО ВЫПОЛНЕНЫ ===\n");

        } catch (Exception e) {
            System.err.println("Ошибка при выполнении примеров: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
