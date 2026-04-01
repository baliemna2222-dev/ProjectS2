package JStream.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {

    public static final String URL =
    		"jdbc:mysql://localhost:3306/jstreamdb?useSSL=false&serverTimezone=UTC";
    public static final String USER = "root";
    public static final String PASSWORD = "";

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("MySQL Driver Loaded");
        } catch (ClassNotFoundException e) {
            System.out.println("MySQL Driver NOT FOUND");
            e.printStackTrace();
        } 
    }

    private static Connection connection;

    // Singleton connection
    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("Database connected!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }}
