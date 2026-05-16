package database;

import java.sql.Connection;
import java.sql.Statement;

public class DatabaseInitializer {

    public static void init() {
        try (Connection conn = DatabaseManager.connect();
             Statement stmt = conn.createStatement()) {

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS users(
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    full_name TEXT,
                    card_number TEXT UNIQUE,
                    pin TEXT,
                    balance REAL
                )
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS admins(
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    username TEXT UNIQUE,
                    password TEXT
                )
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS transactions(
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    user_id INTEGER,
                    type TEXT,
                    amount REAL,
                    description TEXT,
                    created_at TEXT
                )
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS system_logs(
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    action TEXT,
                    created_at TEXT
                )
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS atm_balance(
                    id INTEGER PRIMARY KEY CHECK (id = 1),
                    balance REAL DEFAULT 10000000
                )
            """);

            // Инициализировать баланс банкомата если его нет
            stmt.execute("INSERT OR IGNORE INTO atm_balance (id, balance) VALUES (1, 10000000)");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}