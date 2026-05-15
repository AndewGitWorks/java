package dao;

import database.DatabaseManager;
import utils.DateUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class LogDAO {

    public void log(String action) {
        String sql = """
            INSERT INTO system_logs(action,created_at)
            VALUES(?,?)
        """;

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, action);
            stmt.setString(2, DateUtil.now());

            stmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}