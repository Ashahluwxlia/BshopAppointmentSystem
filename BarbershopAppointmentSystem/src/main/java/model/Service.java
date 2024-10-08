/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author anacarolina
 */

import database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Service {
    private String serviceName; // name of the service
    private double price; // price of the service
    private int duration; // duration in minutes

    // Constructor to create a Service object
    public Service(String serviceName, double price, int duration) {
        this.serviceName = serviceName; // set service name
        this.price = price; // set service price
        this.duration = duration; // set service duration
    }

    // Getters for service attributes
    public String getServiceName() { return serviceName; } // return service name
    public double getPrice() { return price; } // return service price
    public int getDuration() { return duration; } // return service duration

    // Convert service details to a string
    @Override
    public String toString() {
        return String.join(",", serviceName, String.valueOf(price), String.valueOf(duration)); // return service info as CSV
    }

    // Create a Service object from a string
    public static Service fromString(String str) {
        String[] parts = str.split(","); // split input string by commas
        if (parts.length != 3) throw new IllegalArgumentException("Invalid service string: " + str); // check format
        return new Service(parts[0], Double.parseDouble(parts[1]), Integer.parseInt(parts[2])); // create Service object
    }

    // Save service details to the database
    public void saveToDatabase() {
        String sql = "INSERT INTO Services (serviceName, price, duration) VALUES (?, ?, ?)"; // SQL insert statement
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, serviceName); // set service name
            pstmt.setDouble(2, price); // set service price
            pstmt.setInt(3, duration); // set service duration
            pstmt.executeUpdate(); // execute the insert
        } catch (SQLException e) {
            System.err.println("Error saving service to database: " + e.getMessage()); // print error message
        }
    }

    // Get a Service object by name from the database
    public static Service getServiceByName(String serviceName) {
        String sql = "SELECT * FROM Services WHERE serviceName = ?"; // SQL select statement
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, serviceName); // set service name in query
            ResultSet rs = pstmt.executeQuery(); // execute query
            if (rs.next()) {
                return new Service(serviceName, rs.getDouble("price"), rs.getInt("duration")); // return Service object
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving service from database: " + e.getMessage()); // print error message
        }
        return null; // return null if service not found
    }

    // Create a Service object from a ResultSet
    public static Service fromResultSet(ResultSet rs) throws SQLException {
        return new Service(rs.getString("serviceName"), rs.getDouble("price"), rs.getInt("duration")); // create Service from ResultSet
    }

    // Initialize default services in the database
    public static void initializeDefaultServices() {
        String[] serviceNames = {"Haircut", "Shave", "Manicure", "Pedicure", "Hair Coloring", "Hairstyling"}; // default service names
        double[] prices = {30.0, 20.0, 25.0, 35.0, 50.0, 40.0}; // corresponding prices
        int[] durations = {30, 15, 40, 50, 90, 60}; // corresponding durations

        for (int i = 0; i < serviceNames.length; i++) {
            if (getServiceByName(serviceNames[i]) == null) { // check if service exists
                new Service(serviceNames[i], prices[i], durations[i]).saveToDatabase(); // save new service to database
            }
        }
    }
}