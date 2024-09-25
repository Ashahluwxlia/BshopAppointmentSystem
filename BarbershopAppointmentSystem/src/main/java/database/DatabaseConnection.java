/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package database;

/**
 *
 * @author ash
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String URL = "jdbc:derby:mydatabase;create=true";
    private static final String USER = ""; // Set these if necessary
    private static final String PASSWORD = "";

    // Method to get a new connection for each operation
    public static synchronized Connection getConnection() throws SQLException {
        try {
            Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
            connection.setAutoCommit(true); // Ensure auto-commit is enabled
            return connection;
        } catch (SQLException e) {
            System.err.println("Error establishing connection to the database: " + e.getMessage());
            throw e; // rethrow to handle it in your calling method
        }
    }
}
