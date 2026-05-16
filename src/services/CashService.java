package services;

import dao.ATMBalanceDAO;

public class CashService {
    private ATMBalanceDAO atmBalanceDAO = new ATMBalanceDAO();

    public <T extends Number> double withdraw(T amount, double balance) {
        double value = amount.doubleValue();

        if (value > balance) {
            throw new IllegalArgumentException("Недостаточно средств на вашем счете");
        }

        double atmBalance = atmBalanceDAO.getBalance();
        if (value > atmBalance) {
            throw new IllegalArgumentException("❌ Банкомат не может выдать такую сумму.\nНедостаточно наличных в банкомате.");
        }

        // Уменьшаем баланс банкомата
        atmBalanceDAO.withdraw(value);

        return balance - value;
    }

    public <T extends Number> double deposit(T amount, double balance) {
        return balance + amount.doubleValue();
    }
}