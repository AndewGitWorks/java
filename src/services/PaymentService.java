package services;

import dao.TransactionDAO;
import dao.UserDAO;
import models.User;

public class PaymentService {

    private final UserDAO userDAO = new UserDAO();
    private final TransactionDAO transactionDAO = new TransactionDAO();

    public boolean pay(User user, double amount, String type, String description) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Сумма должна быть положительной");
        }

        if (amount > user.getBalance()) {
            throw new IllegalArgumentException("Недостаточно средств");
        }

        double newBalance = user.getBalance() - amount;
        user.setBalance(newBalance);
        userDAO.updateBalance(user.getId(), newBalance);
        transactionDAO.save(user.getId(), type, amount, description);

        return true;
    }

    public boolean transfer(User sender, String recipientCard, double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Сумма должна быть положительной");
        }

        if (amount > sender.getBalance()) {
            throw new IllegalArgumentException("Недостаточно средств");
        }

        double newBalance = sender.getBalance() - amount;
        sender.setBalance(newBalance);
        userDAO.updateBalance(sender.getId(), newBalance);
        transactionDAO.save(sender.getId(), "TRANSFER", amount, 
                "Перевод на карту: " + maskCard(recipientCard));

        return true;
    }

    private String maskCard(String card) {
        if (card.length() < 4) return card;
        return "****" + card.substring(card.length() - 4);
    }
}
