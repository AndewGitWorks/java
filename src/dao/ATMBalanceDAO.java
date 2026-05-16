package dao;

import database.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ATMBalanceDAO {

    public double getBalance() {
        String sql = "SELECT balance FROM atm_balance WHERE id = 1";

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getDouble("balance");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public void updateBalance(double newBalance) {
        String sql = "UPDATE atm_balance SET balance = ? WHERE id = 1";

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDouble(1, newBalance);
            stmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void withdraw(double amount) {
        double currentBalance = getBalance();
        double newBalance = currentBalance - amount;
        updateBalance(newBalance);
    }
}
