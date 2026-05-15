package models;

public class Transaction {
    private int id;
    private int userId;
    private String type;
    private double amount;
    private String createdAt;

    public Transaction(int id, int userId, String type,
                       double amount, String createdAt) {
        this.id = id;
        this.userId = userId;
        this.type = type;
        this.amount = amount;
        this.createdAt = createdAt;
    }
}