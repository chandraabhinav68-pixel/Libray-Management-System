package com.library.db;

import com.library.exceptions.DatabaseException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Singleton Database Manager handling SQLite JDBC Connection and Schema Initialization.
 * Demonstrates Database Connectivity (JDBC) requirement.
 */
public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:library.db";
    private static DatabaseManager instance;

    private DatabaseManager() throws DatabaseException {
        try {
            // Register JDBC driver
            Class.forName("org.sqlite.JDBC");
            initializeSchema();
        } catch (ClassNotFoundException e) {
            throw new DatabaseException("SQLite JDBC Driver not found in classpath.", e);
        }
    }

    public static synchronized DatabaseManager getInstance() throws DatabaseException {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    private void initializeSchema() throws DatabaseException {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            
            // Create Items table
            stmt.execute("CREATE TABLE IF NOT EXISTS items (" +
                    "item_id TEXT PRIMARY KEY, " +
                    "title TEXT NOT NULL, " +
                    "author TEXT NOT NULL, " +
                    "category TEXT, " +
                    "total_copies INTEGER, " +
                    "available_copies INTEGER, " +
                    "item_type TEXT, " +
                    "extra_info TEXT" +
                    ");");

            // Create Users table
            stmt.execute("CREATE TABLE IF NOT EXISTS users (" +
                    "user_id TEXT PRIMARY KEY, " +
                    "name TEXT NOT NULL, " +
                    "email TEXT, " +
                    "phone TEXT, " +
                    "borrowed_count INTEGER DEFAULT 0, " +
                    "user_role TEXT NOT NULL, " +
                    "extra_info TEXT" +
                    ");");

            // Create Transactions table
            stmt.execute("CREATE TABLE IF NOT EXISTS transactions (" +
                    "transaction_id TEXT PRIMARY KEY, " +
                    "user_id TEXT, " +
                    "item_id TEXT, " +
                    "issue_date TEXT, " +
                    "due_date TEXT, " +
                    "return_date TEXT, " +
                    "fine_amount REAL, " +
                    "status TEXT, " +
                    "FOREIGN KEY(user_id) REFERENCES users(user_id), " +
                    "FOREIGN KEY(item_id) REFERENCES items(item_id)" +
                    ");");

            // Seed initial data if empty
            seedInitialData(conn, stmt);

        } catch (SQLException e) {
            throw new DatabaseException("Failed to initialize SQLite Database Schema.", e);
        }
    }

    private void seedInitialData(Connection conn, Statement stmt) throws SQLException {
        var rs = stmt.executeQuery("SELECT COUNT(*) FROM items");
        if (rs.next() && rs.getInt(1) == 0) {
            stmt.executeUpdate("INSERT INTO items VALUES ('B101', 'Clean Code', 'Robert C. Martin', 'Computer Science', 5, 5, 'PHYSICAL_BOOK', 'ISBN: 978-0132350884|Pages: 464')");
            stmt.executeUpdate("INSERT INTO items VALUES ('B102', 'Effective Java', 'Joshua Bloch', 'Computer Science', 3, 3, 'PHYSICAL_BOOK', 'ISBN: 978-0134685991|Pages: 412')");
            stmt.executeUpdate("INSERT INTO items VALUES ('E201', 'Design Patterns (Gang of Four)', 'Erich Gamma et al.', 'Software Architecture', 10, 10, 'EBOOK', 'URL: https://lib.org/dp.pdf|Size: 12.5MB')");
            stmt.executeUpdate("INSERT INTO items VALUES ('B103', 'Introduction to Algorithms', 'Thomas H. Cormen', 'Algorithms', 4, 4, 'PHYSICAL_BOOK', 'ISBN: 978-0262033848|Pages: 1312')");

            stmt.executeUpdate("INSERT INTO users VALUES ('U1001', 'Alice Johnson', 'alice@university.edu', '555-0192', 0, 'STUDENT', 'STU-2024-88')");
            stmt.executeUpdate("INSERT INTO users VALUES ('U1002', 'Dr. Bob Smith', 'bob.smith@university.edu', '555-0482', 0, 'FACULTY', 'Computer Science')");
            stmt.executeUpdate("INSERT INTO users VALUES ('U1003', 'Charlie Brown', 'charlie@university.edu', '555-0723', 0, 'STUDENT', 'STU-2024-99')");
        }
    }
}
