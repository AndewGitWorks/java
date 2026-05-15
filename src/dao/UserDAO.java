package dao;

import database.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class UserDAO {

    public void updateBalance(int userId, double balance) {
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement stmt = conn.prepareStatement(
                     "UPDATE users SET balance=? WHERE id=?")) {

            stmt.setDouble(1, balance);
            stmt.setInt(2, userId);
            stmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}