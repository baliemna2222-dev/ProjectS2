package JStream.utils;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class Database {

    private static HikariDataSource dataSource;

    // Static block initializes the connection pool
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // Load MySQL driver
            System.out.println("MySQL Driver Loaded");

            HikariConfig config = new HikariConfig();
            config.setJdbcUrl("jdbc:mysql://localhost:3306/jstreamdb?useSSL=false&serverTimezone=UTC");
            config.setUsername("root"); // use a secure user for production
            config.setPassword("");     // use a secure password for production
            config.setMaximumPoolSize(10); // max simultaneous connections
            config.setMinimumIdle(2);
            config.setIdleTimeout(30000); // 30 seconds
            config.setMaxLifetime(600000); // 10 minutes
            config.setConnectionTimeout(10000); // 10 seconds
            config.setLeakDetectionThreshold(2000); // warn if connection not closed in 2s

            dataSource = new HikariDataSource(config);
            System.out.println("HikariCP Pool Initialized");

        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL Driver not found", e);
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize HikariCP", e);
        }
    }

    // Private constructor to prevent instantiation
    private Database() {}

    // Get a connection from the pool
    public static Connection getConnection() {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get database connection", e);
        }
    }

    // Close the pool when application exits
    public static void closePool() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            System.out.println("Database pool closed");
        }
    }
}