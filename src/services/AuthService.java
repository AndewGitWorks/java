package services;

import database.DatabaseManager;
import models.User;

import java.sql.*;

public class AuthService {

    public User login(String card, String pin) {
        String sql = "SELECT * FROM users WHERE card_number=? AND pin=?";

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, card);
            stmt.setString(2, pin);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new User(
                        rs.getInt("id"),
                        rs.getString("full_name"),
                        rs.getString("card_number"),
                        rs.getString("pin"),
                        rs.getDouble("balance")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}