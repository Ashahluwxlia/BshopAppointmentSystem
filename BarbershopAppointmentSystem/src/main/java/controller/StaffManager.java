/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

/**
 *
 * @author anacarolina
 */


import model.Staff;
import database.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StaffManager {

    private static final Logger logger = Logger.getLogger(StaffManager.class.getName());

    public static void main(String[] args) {
        try {
            StaffManager manager = new StaffManager();

            // Example usage:
            // manager.addStaff(new Staff("Ash Ahluwalia", "1234567890"));
            // manager.addStaff(new Staff("Ana Carolina", "0987654321"));

            List<Staff> allStaff = manager.getAllStaff();
            System.out.println("All Staff Members:");
            allStaff.forEach(System.out::println);

            // Example removal:
            // manager.removeStaffByName("Ana Carolina");
            // manager.removeStaffByName("Ash Ahluwalia");

        } catch (Exception e) {
            logger.log(Level.SEVERE, "An error occurred", e);
        }
    }

    public void addStaff(Staff staff) {
        if (!isStaffExists(staff)) {
            staff.saveToDatabase();
            System.out.println("Staff '" + staff.getName() + "' added to the database.");
        } else {
            System.out.println("Staff '" + staff.getName() + "' already exists in the database.");
        }
    }

    public void removeStaffByName(String name) {
        String sql = "DELETE FROM Staff WHERE name = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, name);
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Staff '" + name + "' removed from the database.");
            } else {
                System.out.println("Staff '" + name + "' not found in the database.");
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error removing staff from the database", e);
        }
    }

    private boolean isStaffExists(Staff staff) {
        String sql = "SELECT COUNT(*) FROM Staff WHERE name = ? AND contactNumber = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, staff.getName());
            pstmt.setString(2, staff.getContactNumber());

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error checking if staff exists in the database", e);
        }
        return false;
    }

    public List<Staff> getAllStaff() {
        List<Staff> staffList = new ArrayList<>();
        String sql = "SELECT * FROM Staff";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                staffList.add(Staff.fromResultSet(rs));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error loading staff from the database", e);
        }
        return staffList;
    }
}
