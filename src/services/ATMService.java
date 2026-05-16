package services;

import dao.TransactionDAO;
import dao.UserDAO;
import models.User;
import events.*;

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

    /**
     * Снять наличные и сгенерировать событие
     */
    public void withdraw(User user, double amount) {
        try {
            double previousBalance = user.getBalance();
            double newBalance = cashService.withdraw(amount, previousBalance);
            
            user.setBalance(newBalance);
            userDAO.updateBalance(user.getId(), newBalance);
            transactionDAO.save(user.getId(), "WITHDRAW", amount, "Снятие наличных");
            
            // Генерация события транзакции
            TransactionEvent event = new TransactionEvent(
                user.getId(),
                TransactionEvent.TransactionType.WITHDRAW,
                amount,
                previousBalance,
                newBalance
            );
            event.setStatus(ATMEvent.EventStatus.COMPLETED);
            eventManager.publishEvent(event);
            
        } catch (Exception e) {
            // Генерация события ошибки
            ErrorEvent errorEvent = new ErrorEvent(
                ErrorEvent.ErrorType.INVALID_AMOUNT,
                "WITHDRAW_ERROR",
                "Ошибка при снятии наличных: " + e.getMessage(),
                e.getStackTrace().toString(),
                user.getId()
            );
            eventManager.publishEvent(errorEvent);
            throw new RuntimeException("Ошибка при снятии наличных", e);
        }
    }

    /**
     * Пополнить счет и сгенерировать событие
     */
    public void deposit(User user, double amount) {
        try {
            double previousBalance = user.getBalance();
            double newBalance = cashService.deposit(amount, previousBalance);
            
            user.setBalance(newBalance);
            userDAO.updateBalance(user.getId(), newBalance);
            transactionDAO.save(user.getId(), "DEPOSIT", amount, "Пополнение счета");
            
            // Генерация события транзакции
            TransactionEvent event = new TransactionEvent(
                user.getId(),
                TransactionEvent.TransactionType.DEPOSIT,
                amount,
                previousBalance,
                newBalance
            );
            event.setStatus(ATMEvent.EventStatus.COMPLETED);
            eventManager.publishEvent(event);
            
        } catch (Exception e) {
            ErrorEvent errorEvent = new ErrorEvent(
                ErrorEvent.ErrorType.INVALID_AMOUNT,
                "DEPOSIT_ERROR",
                "Ошибка при пополнении счета: " + e.getMessage(),
                e.getStackTrace().toString(),
                user.getId()
            );
            eventManager.publishEvent(errorEvent);
            throw new RuntimeException("Ошибка при пополнении счета", e);
        }
    }

    /**
     * Оплата телефона и генерация события
     */
    public void payPhone(User user, String phone, double amount) {
        try {
            double currentBalance = user.getBalance();
            paymentService.pay(user, amount, "PHONE_PAYMENT", 
                    "Оплата телефона " + maskPhone(phone));
            
            // Генерация события платежа
            PaymentEvent event = new PaymentEvent(
                user.getId(),
                PaymentEvent.PaymentType.PHONE_PAYMENT,
                amount,
                maskPhone(phone),
                "Оплата телефона",
                currentBalance - amount
            );
            event.setStatus(ATMEvent.EventStatus.COMPLETED);
            eventManager.publishEvent(event);
            
        } catch (Exception e) {
            ErrorEvent errorEvent = new ErrorEvent(
                ErrorEvent.ErrorType.INVALID_AMOUNT,
                "PHONE_PAYMENT_ERROR",
                "Ошибка при оплате телефона: " + e.getMessage(),
                e.getStackTrace().toString(),
                user.getId()
            );
            eventManager.publishEvent(errorEvent);
            throw new RuntimeException("Ошибка при оплате телефона", e);
        }
    }

    /**
     * Оплата ЖКХ и генерация события
     */
    public void payUtilities(User user, String account, double amount) {
        try {
            double currentBalance = user.getBalance();
            paymentService.pay(user, amount, "UTILITIES_PAYMENT", 
                    "Оплата ЖКХ счет: " + maskAccount(account));
            
            // Генерация события платежа
            PaymentEvent event = new PaymentEvent(
                user.getId(),
                PaymentEvent.PaymentType.UTILITIES_PAYMENT,
                amount,
                maskAccount(account),
                "Оплата ЖКХ",
                currentBalance - amount
            );
            event.setStatus(ATMEvent.EventStatus.COMPLETED);
            eventManager.publishEvent(event);
            
        } catch (Exception e) {
            ErrorEvent errorEvent = new ErrorEvent(
                ErrorEvent.ErrorType.INVALID_AMOUNT,
                "UTILITIES_PAYMENT_ERROR",
                "Ошибка при оплате ЖКХ: " + e.getMessage(),
                e.getStackTrace().toString(),
                user.getId()
            );
            eventManager.publishEvent(errorEvent);
            throw new RuntimeException("Ошибка при оплате ЖКХ", e);
        }
    }

    /**
     * Перевод на карту и генерация события
     */
    public void transferToCard(User user, String cardNumber, double amount) {
        try {
            double previousBalance = user.getBalance();
            paymentService.transfer(user, cardNumber, amount);
            double newBalance = user.getBalance();
            
            // Генерация события перевода
            TransferEvent event = new TransferEvent(
                user.getId(),
                user.getCardNumber(),
                cardNumber,
                TransferEvent.TransferType.TO_CARD,
                amount,
                "Перевод на карту",
                previousBalance,
                newBalance
            );
            event.setStatus(ATMEvent.EventStatus.COMPLETED);
            eventManager.publishEvent(event);
            
        } catch (Exception e) {
            ErrorEvent errorEvent = new ErrorEvent(
                ErrorEvent.ErrorType.INVALID_AMOUNT,
                "TRANSFER_ERROR",
                "Ошибка при переводе на карту: " + e.getMessage(),
                e.getStackTrace().toString(),
                user.getId()
            );
            eventManager.publishEvent(errorEvent);
            throw new RuntimeException("Ошибка при переводе на карту", e);
        }
    }

    /**
     * Перевод пользователю и генерация события
     */
    public void transferToUser(User user, String userCard, double amount) {
        try {
            double previousBalance = user.getBalance();
            paymentService.transfer(user, userCard, amount);
            double newBalance = user.getBalance();
            
            // Генерация события перевода
            TransferEvent event = new TransferEvent(
                user.getId(),
                user.getCardNumber(),
                userCard,
                TransferEvent.TransferType.TO_USER,
                amount,
                "Перевод пользователю",
                previousBalance,
                newBalance
            );
            event.setStatus(ATMEvent.EventStatus.COMPLETED);
            eventManager.publishEvent(event);
            
        } catch (Exception e) {
            ErrorEvent errorEvent = new ErrorEvent(
                ErrorEvent.ErrorType.INVALID_AMOUNT,
                "USER_TRANSFER_ERROR",
                "Ошибка при переводе пользователю: " + e.getMessage(),
                e.getStackTrace().toString(),
                user.getId()
            );
            eventManager.publishEvent(errorEvent);
            throw new RuntimeException("Ошибка при переводе пользователю", e);
        }
    }

    /**
     * Оплата штрафов и генерация события
     */
    public void payPenalty(User user, String penaltyNumber, double amount) {
        try {
            double currentBalance = user.getBalance();
            paymentService.pay(user, amount, "PENALTY_PAYMENT", 
                    "Оплата штрафа: " + penaltyNumber);
            
            // Генерация события платежа
            PaymentEvent event = new PaymentEvent(
                user.getId(),
                PaymentEvent.PaymentType.PENALTY_PAYMENT,
                amount,
                penaltyNumber,
                "Оплата штрафа",
                currentBalance - amount
            );
            event.setStatus(ATMEvent.EventStatus.COMPLETED);
            eventManager.publishEvent(event);
            
        } catch (Exception e) {
            ErrorEvent errorEvent = new ErrorEvent(
                ErrorEvent.ErrorType.INVALID_AMOUNT,
                "PENALTY_PAYMENT_ERROR",
                "Ошибка при оплате штрафа: " + e.getMessage(),
                e.getStackTrace().toString(),
                user.getId()
            );
            eventManager.publishEvent(errorEvent);
            throw new RuntimeException("Ошибка при оплате штрафа", e);
        }
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