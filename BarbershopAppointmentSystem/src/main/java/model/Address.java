/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author ash
 */
import database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Address {

    private String street;
    private String city;
    private String state;
    private String zipCode;

    public Address(String street, String city, String state, String zipCode) {
        this.street = street;
        this.city = city;
        this.state = state;
        this.zipCode = zipCode;
    }

    // Getters and Setters
    public String getStreet() { return street; }
    public void setStreet(String street) { this.street = street; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getZipCode() { return zipCode; }
    public void setZipCode(String zipCode) { this.zipCode = zipCode; }

    @Override
    public String toString() {
        return String.join(", ", street, city, state, zipCode);
    }

    // Create an Address from a string
    public static Address fromString(String str) {
        String[] parts = str.split(", ");
        if (parts.length != 4) throw new IllegalArgumentException("Invalid address format: " + str);
        return new Address(parts[0].trim(), parts[1].trim(), parts[2].trim(), parts[3].trim());
    }

    // Save the address to the database
    public void saveToDatabase(int customerId) {
        String sql = "INSERT INTO Addresses (customerId, street, city, state, zipCode) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, customerId);
            pstmt.setString(2, street);
            pstmt.setString(3, city);
            pstmt.setString(4, state);
            pstmt.setString(5, zipCode);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            // Handle the error quietly without cluttering the console
        }
    }

    // Create an Address from a ResultSet
    public static Address fromResultSet(ResultSet rs) throws SQLException {
        return new Address(
            rs.getString("street"),
            rs.getString("city"),
            rs.getString("state"),
            rs.getString("zipCode")
        );
    }
}