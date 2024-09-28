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

    private String street; // street address
    private String city; // city name
    private String state; // state name
    private String zipCode; // postal code

    // Constructor to create an Address object
    public Address(String street, String city, String state, String zipCode) {
        this.street = street;
        this.city = city;
        this.state = state;
        this.zipCode = zipCode;
    }

    // Getters and Setters
    public String getStreet() { return street; } // return street
    public void setStreet(String street) { this.street = street; } // set street

    public String getCity() { return city; } // return city
    public void setCity(String city) { this.city = city; } // set city

    public String getState() { return state; } // return state
    public void setState(String state) { this.state = state; } // set state

    public String getZipCode() { return zipCode; } // return zip code
    public void setZipCode(String zipCode) { this.zipCode = zipCode; } // set zip code

    // Convert address details to a string
    @Override
    public String toString() {
        return String.join(", ", street, city, state, zipCode); // format as "street, city, state, zip"
    }

    // Create an Address object from a string
    public static Address fromString(String str) {
        String[] parts = str.split(", "); // split the string into parts
        if (parts.length != 4) throw new IllegalArgumentException("Invalid address format: " + str); // check format
        return new Address(parts[0].trim(), parts[1].trim(), parts[2].trim(), parts[3].trim()); // create Address object
    }

    // Save the address to the database
    public void saveToDatabase(int customerId) {
        String sql = "INSERT INTO Addresses (customerId, street, city, state, zipCode) VALUES (?, ?, ?, ?, ?)"; // SQL insert statement
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, customerId); // set customer ID
            pstmt.setString(2, street); // set street
            pstmt.setString(3, city); // set city
            pstmt.setString(4, state); // set state
            pstmt.setString(5, zipCode); // set zip code
            pstmt.executeUpdate(); // execute the insert
        } catch (SQLException e) {
            // Handle the error quietly without cluttering the console
        }
    }

    // Create an Address from a ResultSet
    public static Address fromResultSet(ResultSet rs) throws SQLException {
        return new Address(
            rs.getString("street"), // get street from ResultSet
            rs.getString("city"), // get city from ResultSet
            rs.getString("state"), // get state from ResultSet
            rs.getString("zipCode") // get zip code from ResultSet
        );
    }
}