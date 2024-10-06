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

        } catch (Exception e) {  // catches and logs any exceptions that occur
            logger.log(Level.SEVERE, "An error occurred", e);
        }
    }

    public void addStaff(Staff staff) { // method to add a staff member to the database
        // checks if the staff member already exists in the database
        if (!isStaffExists(staff)) {
            staff.saveToDatabase(); // if not, the staff is saved to the database
            System.out.println("Staff '" + staff.getName() + "' added to the database."); // Confirmation message
        } else {
            // if staff already exists, a message is displayed
            System.out.println("Staff '" + staff.getName() + "' already exists in the database.");
        }
    }

public void removeStaffByName(String name) { // method to remove a staff member by name
        // SQL query to delete staff with a specific name
        String sql = "DELETE FROM Staff WHERE name = ?";
        try (Connection conn = DatabaseConnection.getConnection(); //opens a connection to the database
             PreparedStatement pstmt = conn.prepareStatement(sql)) { //prepares the SQL statement

            pstmt.setString(1, name); //sets the name parameter in the query
            int rowsAffected = pstmt.executeUpdate(); //executes the SQL update (deletion)

            //checks if any rows were affected (i.e., staff was found and deleted)
            if (rowsAffected > 0) {
                System.out.println("Staff '" + name + "' removed from the database."); //confirmation message
            } else {
                System.out.println("Staff '" + name + "' not found in the database."); //message if staff was not found
            }
        } catch (SQLException e) { //catches and logs any SQL exceptions
            logger.log(Level.SEVERE, "Error removing staff from the database", e); //logs the error
        }
    }

    private boolean isStaffExists(Staff staff) { //method to check if a staff member already exists
        //SQL query to count how many staff members match the name and contact number
        String sql = "SELECT COUNT(*) FROM Staff WHERE name = ? AND contactNumber = ?";
        try (Connection conn = DatabaseConnection.getConnection(); //opens a connection to the database
             PreparedStatement pstmt = conn.prepareStatement(sql)) { //prepares the SQL query

            pstmt.setString(1, staff.getName()); //sets the name parameter in the query
            pstmt.setString(2, staff.getContactNumber()); //sets the contact number parameter in the query

            try (ResultSet rs = pstmt.executeQuery()) { //executes the query and gets the result set
                if (rs.next()) { //moves to the first row in the result set
                    return rs.getInt(1) > 0; //returns true if the count is greater than 0 (staff exists)
                }
            }
        } catch (SQLException e) { //catches and logs any SQL exceptions
            logger.log(Level.SEVERE, "Error checking if staff exists in the database", e); //logs the error
        }
        return false; //returns false if staff doesnt exist or if an error occurred
    }

    public List<Staff> getAllStaff() { //method to retrieve all staff members from the database
        List<Staff> staffList = new ArrayList<>(); //creates an empty list to store staff
        String sql = "SELECT * FROM Staff"; //SQL query to select all staff members

        try (Connection conn = DatabaseConnection.getConnection(); //opens a connection to the database
             Statement stmt = conn.createStatement(); //creates a statement object
             ResultSet rs = stmt.executeQuery(sql)) { //executes the SQL query and gets the result set

            while (rs.next()) { //iterates through each row in the result set
                staffList.add(Staff.fromResultSet(rs)); //adds each staff to the list using the 'fromResultSet' method
            }
        } catch (SQLException e) { //catches and logs any SQL exceptions
            logger.log(Level.SEVERE, "Error loading staff from the database", e); //logs the error
        }
        return staffList; //returns the list of staff
    }
}
