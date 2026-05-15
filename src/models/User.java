package models;

public class User {
    private int id;
    private String fullName;
    private String cardNumber;
    private String pin;
    private double balance;

    public User(int id, String fullName, String cardNumber, String pin, double balance) {
        this.id = id;
        this.fullName = fullName;
        this.cardNumber = cardNumber;
        this.pin = pin;
        this.balance = balance;
    }

    public int getId() { return id; }
    public String getCardNumber() { return cardNumber; }
    public String getPin() { return pin; }
    public double getBalance() { return balance; }

    public void setBalance(double balance) {
        this.balance = balance;
    }
}