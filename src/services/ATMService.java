package services;

import dao.TransactionDAO;
import dao.UserDAO;
import models.User;

public class ATMService {

    private final CashService cashService = new CashService();
    private final PaymentService paymentService = new PaymentService();
    private final UserDAO userDAO = new UserDAO();
    private final TransactionDAO transactionDAO = new TransactionDAO();

    public void withdraw(User user, double amount) {
        double newBalance = cashService.withdraw(amount, user.getBalance());
        user.setBalance(newBalance);
        userDAO.updateBalance(user.getId(), newBalance);
        transactionDAO.save(user.getId(), "WITHDRAW", amount, "Снятие наличных");
    }

    public void deposit(User user, double amount) {
        double newBalance = cashService.deposit(amount, user.getBalance());
        user.setBalance(newBalance);
        userDAO.updateBalance(user.getId(), newBalance);
        transactionDAO.save(user.getId(), "DEPOSIT", amount, "Пополнение счета");
    }

    public void payPhone(User user, String phone, double amount) {
        paymentService.pay(user, amount, "PHONE_PAYMENT", 
                "Оплата телефона " + maskPhone(phone));
    }

    public void payUtilities(User user, String account, double amount) {
        paymentService.pay(user, amount, "UTILITIES_PAYMENT", 
                "Оплата ЖКХ счет: " + maskAccount(account));
    }

    public void transferToCard(User user, String cardNumber, double amount) {
        paymentService.transfer(user, cardNumber, amount);
    }

    public void transferToUser(User user, String userCard, double amount) {
        paymentService.transfer(user, userCard, amount);
    }

    public void payPenalty(User user, String penaltyNumber, double amount) {
        paymentService.pay(user, amount, "PENALTY_PAYMENT", 
                "Оплата штрафа: " + penaltyNumber);
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