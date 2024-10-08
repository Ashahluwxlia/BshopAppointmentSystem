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

    // Main method to initialize the database and create tables
    public static void initializeDatabase() {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            createCustomersTable(conn, stmt); // create customers table if not exists
            createStaffTable(conn, stmt);     // create staff table if not exists
            createServicesTable(conn, stmt);  // create services table if not exists
            createAppointmentsTable(conn, stmt); // create appointments table if not exists
            createFeedbackTable(conn, stmt);  // create feedback table if not exists

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error initializing the database", e); // log any errors
        }
    }

    // Method to create Customers table
    private static void createCustomersTable(Connection conn, Statement stmt) throws SQLException {
        if (!tableExists(conn, "CUSTOMERS")) { // check if table already exists
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
                    "zipCode VARCHAR(10))"; // SQL to create the table
            stmt.executeUpdate(sql); // execute the SQL statement
            logger.info("Customers table created successfully."); // log success
        }
    }

    // Method to create Staff table
    private static void createStaffTable(Connection conn, Statement stmt) throws SQLException {
        if (!tableExists(conn, "STAFF")) { // check if table already exists
            String sql = "CREATE TABLE STAFF (" +
                    "id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), " +
                    "name VARCHAR(100), " +
                    "contactNumber VARCHAR(15))"; // SQL to create the table
            stmt.executeUpdate(sql); // execute the SQL statement
            logger.info("Staff table created successfully."); // log success
        }
    }

    // Method to create Services table
    private static void createServicesTable(Connection conn, Statement stmt) throws SQLException {
        if (!tableExists(conn, "SERVICES")) { // check if table already exists
            String sql = "CREATE TABLE SERVICES (" +
                    "id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), " +
                    "serviceName VARCHAR(100), " +
                    "price DOUBLE, " +
                    "duration INT)"; // SQL to create the table
            stmt.executeUpdate(sql); // execute the SQL statement
            logger.info("Services table created successfully."); // log success
        }
    }

    // Method to create Appointments table
    private static void createAppointmentsTable(Connection conn, Statement stmt) throws SQLException {
        if (!tableExists(conn, "APPOINTMENTS")) { // check if table already exists
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
                    "FOREIGN KEY (staffId) REFERENCES STAFF(id))"; // SQL to create the table with foreign keys
            stmt.executeUpdate(sql); // execute the SQL statement
            logger.info("Appointments table created successfully."); // log success
        }
    }

    // Method to create Feedback table
    private static void createFeedbackTable(Connection conn, Statement stmt) throws SQLException {
        if (!tableExists(conn, "FEEDBACK")) { // check if table already exists
            String sql = "CREATE TABLE FEEDBACK (" +
                    "id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), " +
                    "bookingReference VARCHAR(50), " +
                    "customerName VARCHAR(100), " +
                    "feedbackText VARCHAR(500), " +
                    "feedbackTime TIMESTAMP)"; // SQL to create the table
            stmt.executeUpdate(sql); // execute the SQL statement
            logger.info("Feedback table created successfully."); // log success
        }
    }

    // Method to check if a table already exists
    private static boolean tableExists(Connection conn, String tableName) throws SQLException {
        try (ResultSet rs = conn.getMetaData().getTables(null, null, tableName.toUpperCase(), null)) {
            return rs.next(); // return true if table exists, false otherwise
        }
    }
}