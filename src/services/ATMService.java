package services;

import dao.TransactionDAO;
import dao.UserDAO;
import models.User;

public class ATMService {

    private final CashService cashService = new CashService();
    private final UserDAO userDAO = new UserDAO();
    private final TransactionDAO transactionDAO = new TransactionDAO();

    public void withdraw(User user, double amount) {
        double newBalance = cashService.withdraw(amount, user.getBalance());

        user.setBalance(newBalance);
        userDAO.updateBalance(user.getId(), newBalance);
        transactionDAO.save(user.getId(), "WITHDRAW", amount);
    }
}