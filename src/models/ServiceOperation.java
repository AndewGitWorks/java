package models;

public class ServiceOperation {
    private int id;
    private String serviceName;
    private double price;

    public ServiceOperation(int id, String serviceName, double price) {
        this.id = id;
        this.serviceName = serviceName;
        this.price = price;
    }
}