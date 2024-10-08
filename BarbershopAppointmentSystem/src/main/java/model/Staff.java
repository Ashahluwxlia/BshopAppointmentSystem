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

import java.sql.*;

public class Staff {
    private int id; // staff ID
    private final String name; // staff name
    private final String contactNumber; // staff contact number

    // Constructor with ID
    public Staff(int id, String name, String contactNumber) {
        this.id = id; // set staff ID
        this.name = name; // set staff name
        this.contactNumber = contactNumber; // set contact number
    }

    // Constructor without ID, defaults to 0
    public Staff(String name, String contactNumber) {
        this(0, name, contactNumber); // delegate to the main constructor
    }

    // Getters for staff attributes
    public int getId() { return id; } // return staff ID
    public String getName() { return name; } // return staff name
    public String getContactNumber() { return contactNumber; } // return contact number

    // Save staff details to the database
    public void saveToDatabase() {
        String sql = "INSERT INTO Staff (name, contactNumber) VALUES (?, ?)"; // SQL insert statement
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, name); // set staff name
            pstmt.setString(2, contactNumber); // set contact number
            pstmt.executeUpdate(); // execute the insert

            // Get generated ID and set it
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    this.id = rs.getInt(1); // set staff ID
                }
            }
        } catch (SQLException e) {
            System.err.println("Error saving staff to database: " + e.getMessage()); // print error message
        }
    }

    // Get staff ID by name and contact number
    public int getStaffId() throws SQLException {
        String sql = "SELECT id FROM Staff WHERE name = ? AND contactNumber = ?"; // SQL select statement
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, name); // set staff name in query
            pstmt.setString(2, contactNumber); // set contact number in query

            // Execute query and get ID
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id"); // return staff ID
                } else {
                    throw new SQLException("Staff not found in the database."); // throw error if not found
                }
            }
        }
    }

    // Get a Staff object by ID from the database
    public static Staff getStaffById(int id) throws SQLException {
        String sql = "SELECT * FROM Staff WHERE id = ?"; // SQL select statement
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id); // set ID in query
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return fromResultSet(rs); // return Staff object
                } else {
                    throw new SQLException("Staff with ID " + id + " not found."); // throw error if not found
                }
            }
        }
    }

    // Create a Staff object from a ResultSet
    public static Staff fromResultSet(ResultSet rs) throws SQLException {
        return new Staff(rs.getInt("id"), rs.getString("name"), rs.getString("contactNumber")); // create Staff from ResultSet
    }

    // Convert staff details to a string
    @Override
    public String toString() {
        return String.format("Staff Name: %s\nContact Number: %s\n", name, contactNumber); // return staff info
    }
}