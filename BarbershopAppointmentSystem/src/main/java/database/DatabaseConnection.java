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

    private static final String URL = "jdbc:derby:mydatabase;create=true"; // database URL
    private static final String USER = ""; // optional user
    private static final String PASSWORD = ""; // optional password

    // Method to get a new connection for each operation
    public static synchronized Connection getConnection() throws SQLException {
        try {
            Connection connection = DriverManager.getConnection(URL, USER, PASSWORD); // establish connection
            connection.setAutoCommit(true); // make sure auto-commit is enabled
            return connection; // return the connection
        } catch (SQLException e) {
            System.err.println("Error establishing connection to the database: " + e.getMessage()); // print error
            throw e; // rethrow the exception to handle it elsewhere
        }
    }
}