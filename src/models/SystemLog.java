package models;

public class SystemLog {
    private int id;
    private String action;
    private String createdAt;

    public SystemLog(int id, String action, String createdAt) {
        this.id = id;
        this.action = action;
        this.createdAt = createdAt;
    }
}