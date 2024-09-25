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
    private String serviceName;
    private double price;
    private int duration; // duration in minutes

    public Service(String serviceName, double price, int duration) {
        this.serviceName = serviceName;
        this.price = price;
        this.duration = duration;
    }

    // Getters
    public String getServiceName() { return serviceName; }
    public double getPrice() { return price; }
    public int getDuration() { return duration; }

    @Override
    public String toString() {
        return String.join(",", serviceName, String.valueOf(price), String.valueOf(duration));
    }

    public static Service fromString(String str) {
        String[] parts = str.split(",");
        if (parts.length != 3) throw new IllegalArgumentException("Invalid service string: " + str);
        return new Service(parts[0], Double.parseDouble(parts[1]), Integer.parseInt(parts[2]));
    }

    public void saveToDatabase() {
        String sql = "INSERT INTO Services (serviceName, price, duration) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, serviceName);
            pstmt.setDouble(2, price);
            pstmt.setInt(3, duration);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error saving service to database: " + e.getMessage());
        }
    }

    public static Service getServiceByName(String serviceName) {
        String sql = "SELECT * FROM Services WHERE serviceName = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, serviceName);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Service(serviceName, rs.getDouble("price"), rs.getInt("duration"));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving service from database: " + e.getMessage());
        }
        return null;
    }

    public static Service fromResultSet(ResultSet rs) throws SQLException {
        return new Service(rs.getString("serviceName"), rs.getDouble("price"), rs.getInt("duration"));
    }

    public static void initializeDefaultServices() {
        String[] serviceNames = {"Haircut", "Shave", "Manicure", "Pedicure", "Hair Coloring", "Hairstyling"};
        double[] prices = {30.0, 20.0, 25.0, 35.0, 50.0, 40.0};
        int[] durations = {30, 15, 40, 50, 90, 60};

        for (int i = 0; i < serviceNames.length; i++) {
            if (getServiceByName(serviceNames[i]) == null) {
                new Service(serviceNames[i], prices[i], durations[i]).saveToDatabase();
            }
        }
    }
}
