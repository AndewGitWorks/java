package models;

public class Payment {
    private String type;
    private String description;
    private double amount;

    public Payment(String type, String description, double amount) {
        this.type = type;
        this.description = description;
        this.amount = amount;
    }

    public String getType() { return type; }
    public String getDescription() { return description; }
    public double getAmount() { return amount; }
}
