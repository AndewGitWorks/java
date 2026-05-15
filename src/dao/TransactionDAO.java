package dao;

import database.DatabaseManager;
import utils.DateUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class TransactionDAO {

    public void save(int userId, String type, double amount) {
        String sql = """
            INSERT INTO transactions(user_id,type,amount,created_at)
            VALUES(?,?,?,?)
        """;

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setString(2, type);
            stmt.setDouble(3, amount);
            stmt.setString(4, DateUtil.now());

            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}