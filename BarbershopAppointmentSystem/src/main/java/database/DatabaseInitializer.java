/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package database;

/**
 *
 * @author anacarolina
 */ 


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseInitializer {

    private static final Logger logger = Logger.getLogger(DatabaseInitializer.class.getName());

    public static void initializeDatabase() {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            createCustomersTable(conn, stmt);
            createStaffTable(conn, stmt);
            createServicesTable(conn, stmt);
            createAppointmentsTable(conn, stmt);
            createFeedbackTable(conn, stmt);

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error initializing the database", e);
        }
    }

    private static void createCustomersTable(Connection conn, Statement stmt) throws SQLException {
        if (!tableExists(conn, "CUSTOMERS")) {
            String sql = "CREATE TABLE CUSTOMERS (" +
                    "id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), " +
                    "firstName VARCHAR(50), " +
                    "lastName VARCHAR(50), " +
                    "birthDate DATE, " +
                    "contactNumber VARCHAR(15), " +
                    "gender VARCHAR(10), " +
                    "street VARCHAR(100), " +
                    "city VARCHAR(50), " +
                    "state VARCHAR(50), " +
                    "zipCode VARCHAR(10))";
            stmt.executeUpdate(sql);
            logger.info("Customers table created successfully.");
        }
    }

    private static void createStaffTable(Connection conn, Statement stmt) throws SQLException {
        if (!tableExists(conn, "STAFF")) {
            String sql = "CREATE TABLE STAFF (" +
                    "id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), " +
                    "name VARCHAR(100), " +
                    "contactNumber VARCHAR(15))";
            stmt.executeUpdate(sql);
            logger.info("Staff table created successfully.");
        }
    }

    private static void createServicesTable(Connection conn, Statement stmt) throws SQLException {
        if (!tableExists(conn, "SERVICES")) {
            String sql = "CREATE TABLE SERVICES (" +
                    "id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), " +
                    "serviceName VARCHAR(100), " +
                    "price DOUBLE, " +
                    "duration INT)";
            stmt.executeUpdate(sql);
            logger.info("Services table created successfully.");
        }
    }

    private static void createAppointmentsTable(Connection conn, Statement stmt) throws SQLException {
        if (!tableExists(conn, "APPOINTMENTS")) {
            String sql = "CREATE TABLE APPOINTMENTS (" +
                    "id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), " +
                    "referenceNumber VARCHAR(50), " +
                    "customerId INT, " +
                    "staffId INT, " +
                    "appointmentTime TIMESTAMP, " +
                    "services VARCHAR(255), " +
                    "totalCost DOUBLE, " +
                    "totalDuration INT, " +
                    "FOREIGN KEY (customerId) REFERENCES CUSTOMERS(id), " +
                    "FOREIGN KEY (staffId) REFERENCES STAFF(id))";
            stmt.executeUpdate(sql);
            logger.info("Appointments table created successfully.");
        }
    }

    private static void createFeedbackTable(Connection conn, Statement stmt) throws SQLException {
        if (!tableExists(conn, "FEEDBACK")) {
            String sql = "CREATE TABLE FEEDBACK (" +
                    "id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), " +
                    "bookingReference VARCHAR(50), " +
                    "customerName VARCHAR(100), " +
                    "feedbackText VARCHAR(500), " +
                    "feedbackTime TIMESTAMP)";
            stmt.executeUpdate(sql);
            logger.info("Feedback table created successfully.");
        }
    }

    private static boolean tableExists(Connection conn, String tableName) throws SQLException {
        try (ResultSet rs = conn.getMetaData().getTables(null, null, tableName.toUpperCase(), null)) {
            return rs.next();
        }
    }
}
