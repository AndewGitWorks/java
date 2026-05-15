package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import config.DbConfig;

public class DatabaseManager {
    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(DbConfig.URL);
    }
}