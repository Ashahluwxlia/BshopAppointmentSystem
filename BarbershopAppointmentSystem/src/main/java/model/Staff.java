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
    private int id;
    private final String name;
    private final String contactNumber;

    public Staff(int id, String name, String contactNumber) {
        this.id = id;
        this.name = name;
        this.contactNumber = contactNumber;
    }

    public Staff(String name, String contactNumber) {
        this(0, name, contactNumber); // Delegate to the main constructor
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void saveToDatabase() {
        String sql = "INSERT INTO Staff (name, contactNumber) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, name);
            pstmt.setString(2, contactNumber);
            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    this.id = rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error saving staff to database: " + e.getMessage());
        }
    }

    public int getStaffId() throws SQLException {
        String sql = "SELECT id FROM Staff WHERE name = ? AND contactNumber = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, name);
            pstmt.setString(2, contactNumber);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                } else {
                    throw new SQLException("Staff not found in the database.");
                }
            }
        }
    }

    public static Staff getStaffById(int id) throws SQLException {
        String sql = "SELECT * FROM Staff WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return fromResultSet(rs);
                } else {
                    throw new SQLException("Staff with ID " + id + " not found.");
                }
            }
        }
    }

    public static Staff fromResultSet(ResultSet rs) throws SQLException {
        return new Staff(rs.getInt("id"), rs.getString("name"), rs.getString("contactNumber"));
    }

    @Override
    public String toString() {
        return String.format("Staff Name: %s\nContact Number: %s\n", name, contactNumber);
    }
}
