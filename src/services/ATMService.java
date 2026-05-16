package services;

import dao.TransactionDAO;
import dao.UserDAO;
import models.User;
import events.*;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Сервис банкомата с интеграцией системы событий
 * Генерирует события для всех операций и делегирует их обработку системе
 */
public class ATMService {

    private final CashService cashService = new CashService();
    private final PaymentService paymentService = new PaymentService();
    private final UserDAO userDAO = new UserDAO();
    private final TransactionDAO transactionDAO = new TransactionDAO();
    private final EventManager eventManager = EventManager.getInstance();
    private final ExecutorService computeExecutor = Executors.newFixedThreadPool(
            Math.max(2, Runtime.getRuntime().availableProcessors())
    );

    public ATMService() {
        // Инициализация слушателей событий при создании сервиса
        initializeEventListeners();
    }

    /**
     * Инициализация слушателей событий
     */
    private void initializeEventListeners() {
        eventManager.addListener(new AuditLogger());
        eventManager.addListener(new UIEventHandler());
        eventManager.addListener(new NotificationHandler());
    }

    public CompletableFuture<Void> withdrawAsync(User user, double amount) {
        return CompletableFuture.runAsync(() -> withdrawTask(user, amount), computeExecutor);
    }

    public CompletableFuture<Void> depositAsync(User user, double amount) {
        return CompletableFuture.runAsync(() -> depositTask(user, amount), computeExecutor);
    }

    public CompletableFuture<Void> payPhoneAsync(User user, String phone, double amount) {
        return CompletableFuture.runAsync(() -> payPhoneTask(user, phone, amount), computeExecutor);
    }

    public CompletableFuture<Void> payUtilitiesAsync(User user, String account, double amount) {
        return CompletableFuture.runAsync(() -> payUtilitiesTask(user, account, amount), computeExecutor);
    }

    public CompletableFuture<Void> transferToCardAsync(User user, String cardNumber, double amount) {
        return CompletableFuture.runAsync(() -> transferToCardTask(user, cardNumber, amount), computeExecutor);
    }

    public CompletableFuture<Void> transferToUserAsync(User user, String userCard, double amount) {
        return CompletableFuture.runAsync(() -> transferToUserTask(user, userCard, amount), computeExecutor);
    }

    public CompletableFuture<Void> payPenaltyAsync(User user, String penaltyNumber, double amount) {
        return CompletableFuture.runAsync(() -> payPenaltyTask(user, penaltyNumber, amount), computeExecutor);
    }

    private void withdrawTask(User user, double amount) {
        try {
            synchronized (user) {
                double previousBalance = user.getBalance();
                double newBalance = cashService.withdraw(amount, previousBalance);
                user.setBalance(newBalance);
                userDAO.updateBalance(user.getId(), newBalance);
                transactionDAO.save(user.getId(), "WITHDRAW", amount, "Снятие наличных");
                publishTransactionEvent(user.getId(), TransactionEvent.TransactionType.WITHDRAW,
                        amount, previousBalance, newBalance);
            }
        } catch (Exception e) {
            publishErrorEvent(e, user.getId(), "WITHDRAW_ERROR", "Ошибка при снятии наличных: ");
            throw new RuntimeException("Ошибка при снятии наличных", e);
        }
    }

    private void depositTask(User user, double amount) {
        try {
            synchronized (user) {
                double previousBalance = user.getBalance();
                double newBalance = cashService.deposit(amount, previousBalance);
                user.setBalance(newBalance);
                userDAO.updateBalance(user.getId(), newBalance);
                transactionDAO.save(user.getId(), "DEPOSIT", amount, "Пополнение счета");
                publishTransactionEvent(user.getId(), TransactionEvent.TransactionType.DEPOSIT,
                        amount, previousBalance, newBalance);
            }
        } catch (Exception e) {
            publishErrorEvent(e, user.getId(), "DEPOSIT_ERROR", "Ошибка при пополнении счета: ");
            throw new RuntimeException("Ошибка при пополнении счета", e);
        }
    }

    private void payPhoneTask(User user, String phone, double amount) {
        try {
            synchronized (user) {
                double currentBalance = user.getBalance();
                paymentService.pay(user, amount, "PHONE_PAYMENT",
                        "Оплата телефона " + maskPhone(phone));
                publishPaymentEvent(user.getId(), PaymentEvent.PaymentType.PHONE_PAYMENT,
                        amount, maskPhone(phone), "Оплата телефона", currentBalance - amount);
            }
        } catch (Exception e) {
            publishErrorEvent(e, user.getId(), "PHONE_PAYMENT_ERROR", "Ошибка при оплате телефона: ");
            throw new RuntimeException("Ошибка при оплате телефона", e);
        }
    }

    private void payUtilitiesTask(User user, String account, double amount) {
        try {
            synchronized (user) {
                double currentBalance = user.getBalance();
                paymentService.pay(user, amount, "UTILITIES_PAYMENT",
                        "Оплата ЖКХ счет: " + maskAccount(account));
                publishPaymentEvent(user.getId(), PaymentEvent.PaymentType.UTILITIES_PAYMENT,
                        amount, maskAccount(account), "Оплата ЖКХ", currentBalance - amount);
            }
        } catch (Exception e) {
            publishErrorEvent(e, user.getId(), "UTILITIES_PAYMENT_ERROR", "Ошибка при оплате ЖКХ: ");
            throw new RuntimeException("Ошибка при оплате ЖКХ", e);
        }
    }

    private void transferToCardTask(User user, String cardNumber, double amount) {
        try {
            synchronized (user) {
                double previousBalance = user.getBalance();
                paymentService.transfer(user, cardNumber, amount);
                double newBalance = user.getBalance();
                publishTransferEvent(user.getId(), user.getCardNumber(), cardNumber,
                        TransferEvent.TransferType.TO_CARD, amount, "Перевод на карту",
                        previousBalance, newBalance);
            }
        } catch (Exception e) {
            publishErrorEvent(e, user.getId(), "TRANSFER_ERROR", "Ошибка при переводе на карту: ");
            throw new RuntimeException("Ошибка при переводе на карту", e);
        }
    }

    private void transferToUserTask(User user, String userCard, double amount) {
        try {
            synchronized (user) {
                double previousBalance = user.getBalance();
                paymentService.transfer(user, userCard, amount);
                double newBalance = user.getBalance();
                publishTransferEvent(user.getId(), user.getCardNumber(), userCard,
                        TransferEvent.TransferType.TO_USER, amount, "Перевод пользователю",
                        previousBalance, newBalance);
            }
        } catch (Exception e) {
            publishErrorEvent(e, user.getId(), "USER_TRANSFER_ERROR", "Ошибка при переводе пользователю: ");
            throw new RuntimeException("Ошибка при переводе пользователю", e);
        }
    }

    private void payPenaltyTask(User user, String penaltyNumber, double amount) {
        try {
            synchronized (user) {
                double currentBalance = user.getBalance();
                paymentService.pay(user, amount, "PENALTY_PAYMENT",
                        "Оплата штрафа: " + penaltyNumber);
                publishPaymentEvent(user.getId(), PaymentEvent.PaymentType.PENALTY_PAYMENT,
                        amount, penaltyNumber, "Оплата штрафа", currentBalance - amount);
            }
        } catch (Exception e) {
            publishErrorEvent(e, user.getId(), "PENALTY_PAYMENT_ERROR", "Ошибка при оплате штрафа: ");
            throw new RuntimeException("Ошибка при оплате штрафа", e);
        }
    }

    private void publishTransactionEvent(int userId, TransactionEvent.TransactionType type,
                                         double amount, double previousBalance, double newBalance) {
        TransactionEvent event = new TransactionEvent(
                userId,
                type,
                amount,
                previousBalance,
                newBalance
        );
        event.setStatus(ATMEvent.EventStatus.COMPLETED);
        eventManager.publishEvent(event);
    }

    private void publishPaymentEvent(int userId, PaymentEvent.PaymentType type,
                                     double amount, String recipientId,
                                     String description, double currentBalance) {
        PaymentEvent event = new PaymentEvent(
                userId,
                type,
                amount,
                recipientId,
                description,
                currentBalance
        );
        event.setStatus(ATMEvent.EventStatus.COMPLETED);
        eventManager.publishEvent(event);
    }

    private void publishTransferEvent(int userId, String senderCard, String recipientCard,
                                      TransferEvent.TransferType type, double amount,
                                      String purpose, double previousBalance, double newBalance) {
        TransferEvent event = new TransferEvent(
                userId,
                senderCard,
                recipientCard,
                type,
                amount,
                purpose,
                previousBalance,
                newBalance
        );
        event.setStatus(ATMEvent.EventStatus.COMPLETED);
        eventManager.publishEvent(event);
    }

    private void publishErrorEvent(Exception e, int userId, String errorCode, String prefix) {
        ErrorEvent errorEvent = new ErrorEvent(
                ErrorEvent.ErrorType.INVALID_AMOUNT,
                errorCode,
                prefix + e.getMessage(),
                e.getStackTrace().toString(),
                userId
        );
        eventManager.publishEvent(errorEvent);
    }

    public void shutdown() {
        computeExecutor.shutdownNow();
    }

    /**
     * Снять наличные и сгенерировать событие
     */
    public void withdraw(User user, double amount) {
        withdrawTask(user, amount);
    }

    /**
     * Пополнить счет и сгенерировать событие
     */
    public void deposit(User user, double amount) {
        depositTask(user, amount);
    }

    /**
     * Оплата телефона и генерация события
     */
    public void payPhone(User user, String phone, double amount) {
        payPhoneTask(user, phone, amount);
    }

    /**
     * Оплата ЖКХ и генерация события
     */
    public void payUtilities(User user, String account, double amount) {
        payUtilitiesTask(user, account, amount);
    }

    /**
     * Перевод на карту и генерация события
     */
    public void transferToCard(User user, String cardNumber, double amount) {
        transferToCardTask(user, cardNumber, amount);
    }

    /**
     * Перевод пользователю и генерация события
     */
    public void transferToUser(User user, String userCard, double amount) {
        transferToUserTask(user, userCard, amount);
    }

    /**
     * Оплата штрафов и генерация события
     */
    public void payPenalty(User user, String penaltyNumber, double amount) {
        payPenaltyTask(user, penaltyNumber, amount);
    }

    private String maskPhone(String phone) {
        if (phone.length() < 5) return phone;
        return "****" + phone.substring(Math.max(0, phone.length() - 4));
    }

    private String maskAccount(String account) {
        if (account.length() < 4) return account;
        return "****" + account.substring(account.length() - 4);
    }
}