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
            StaffManager manager = new StaffManager(); // create a StaffManager instance

            // Example usage to add staff members:
            // manager.addStaff(new Staff("Ash Ahluwalia", "1234567890"));
            // manager.addStaff(new Staff("Ana Carolina", "0987654321"));

            List<Staff> allStaff = manager.getAllStaff(); // get list of all staff
            System.out.println("All Staff Members:");
            allStaff.forEach(System.out::println); // print all staff members

            // Example removal of staff by name:
            // manager.removeStaffByName("Ana Carolina");
            // manager.removeStaffByName("Ash Ahluwalia");

        } catch (Exception e) {
            logger.log(Level.SEVERE, "An error occurred", e); // log any errors that occur
        }
    }

    public void addStaff(Staff staff) {
        // check if staff already exists before adding
        if (!isStaffExists(staff)) {
            staff.saveToDatabase(); // save staff to the database
            System.out.println("Staff '" + staff.getName() + "' added to the database.");
        } else {
            System.out.println("Staff '" + staff.getName() + "' already exists in the database.");
        }
    }

    public void removeStaffByName(String name) {
        String sql = "DELETE FROM Staff WHERE name = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, name); // set the name parameter for the SQL query
            int rowsAffected = pstmt.executeUpdate(); // execute deletion

            // check if any rows were affected by the deletion
            if (rowsAffected > 0) {
                System.out.println("Staff '" + name + "' removed from the database.");
            } else {
                System.out.println("Staff '" + name + "' not found in the database.");
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error removing staff from the database", e); // log errors if any
        }
    }

    private boolean isStaffExists(Staff staff) {
        String sql = "SELECT COUNT(*) FROM Staff WHERE name = ? AND contactNumber = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, staff.getName()); // set the name parameter
            pstmt.setString(2, staff.getContactNumber()); // set the contact number parameter

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0; // check if count is greater than 0
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error checking if staff exists in the database", e); // log errors
        }
        return false; // return false if staff doesn't exist
    }

    public List<Staff> getAllStaff() {
        List<Staff> staffList = new ArrayList<>(); // create an empty list to store staff
        String sql = "SELECT * FROM Staff"; // query to get all staff

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                staffList.add(Staff.fromResultSet(rs)); // add each staff member to the list
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error loading staff from the database", e); // log errors
        }
        return staffList; // return the list of staff
    }
}