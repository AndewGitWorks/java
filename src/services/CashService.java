package services;

public class CashService {

    public <T extends Number> double withdraw(T amount, double balance) {
        double value = amount.doubleValue();

        if (value > balance) {
            throw new IllegalArgumentException("Недостаточно средств");
        }

        return balance - value;
    }

    public <T extends Number> double deposit(T amount, double balance) {
        return balance + amount.doubleValue();
    }
}