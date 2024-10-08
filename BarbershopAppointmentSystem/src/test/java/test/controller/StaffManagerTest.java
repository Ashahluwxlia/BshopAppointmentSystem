/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package test.controller;

/**
 *
 * @author ash
 */

import controller.StaffManager;
import database.DatabaseConnection;
import model.Staff;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import static org.junit.Assert.*;

public class StaffManagerTest {

    @Before
    public void setup() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // clean up related tables before each test
            clearTable(conn, "Appointments");

            // clear the Staff table
            clearTable(conn, "Staff");

            // re-add "Ash Ahluwalia" and "Ana Carolina" to the database
            addDefaultStaffMembers();
        } catch (SQLException e) {
            e.printStackTrace(); // print any SQL error
        }
    }

    @After
    public void cleanup() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // clean up related tables after each test
            clearTable(conn, "Appointments");

            // clear the Staff table
            clearTable(conn, "Staff");

            // re-add "Ash Ahluwalia" and "Ana Carolina" to the database
            addDefaultStaffMembers();
        } catch (SQLException e) {
            e.printStackTrace(); // print any SQL error
        }
    }

    // helper method to clear a table by name
    private void clearTable(Connection conn, String tableName) throws SQLException {
        String deleteQuery = "DELETE FROM " + tableName;
        try (PreparedStatement pstmt = conn.prepareStatement(deleteQuery)) {
            pstmt.executeUpdate(); // execute delete query
        }
    }

    // helper method to add default staff members to the database
    private void addDefaultStaffMembers() throws SQLException {
        StaffManager manager = new StaffManager();
        Staff staff1 = new Staff("Ash Ahluwalia", "1234567890");
        Staff staff2 = new Staff("Ana Carolina", "0987654321");

        manager.addStaff(staff1); // add Ash
        manager.addStaff(staff2); // add Ana
    }

    @Test
    public void testAddAndRetrieveStaff() throws SQLException {
        StaffManager manager = new StaffManager();

        // add a new staff member (apart from Ash and Ana)
        Staff staff3 = new Staff("Fatima Al-Zahra", "0567890123");
        manager.addStaff(staff3);

        // retrieve all staff members from the database
        List<Staff> allStaff = manager.getAllStaff();

        // verify that all staff members were added
        assertEquals(3, allStaff.size()); // expect 3 staff members
        assertTrue(allStaff.stream().anyMatch(staff -> staff.getName().equals("Ash Ahluwalia"))); // check for Ash
        assertTrue(allStaff.stream().anyMatch(staff -> staff.getName().equals("Ana Carolina"))); // check for Ana
        assertTrue(allStaff.stream().anyMatch(staff -> staff.getName().equals("Fatima Al-Zahra"))); // check for Fatima
    }

    @Test
    public void testRemoveStaff() throws SQLException {
        StaffManager manager = new StaffManager();

        // add and then remove a staff member
        Staff staff = new Staff("Fatima Al-Zahra", "0567890123");
        manager.addStaff(staff);
        manager.removeStaffByName("Fatima Al-Zahra");

        // verify that the staff member was removed
        List<Staff> allStaff = manager.getAllStaff();
        assertEquals(2, allStaff.size()); // only Ash and Ana should remain
        assertTrue(allStaff.stream().noneMatch(s -> s.getName().equals("Fatima Al-Zahra"))); // ensure Fatima is gone
    }

    @Test
    public void testDuplicateStaffAddition() throws SQLException {
        StaffManager manager = new StaffManager();

        // add a staff member that already exists
        Staff staff = new Staff("Ana Carolina", "0987654321");
        manager.addStaff(staff); // Ana is already in the database

        // try to add the same staff member again
        manager.addStaff(staff);

        // verify that the staff member was not added twice
        List<Staff> allStaff = manager.getAllStaff();
        assertEquals(2, allStaff.size()); // only Ash and Ana should be present
    }

    @Test
    public void testRetrieveStaffById() throws SQLException {
        // retrieve "Ash Ahluwalia" by ID
        Staff staff = new Staff("Ash Ahluwalia", "1234567890");
        int staffId = staff.getStaffId();
        Staff retrievedStaff = Staff.getStaffById(staffId);

        // verify the retrieved staff details
        assertNotNull(retrievedStaff); // make sure we found the staff
        assertEquals("Ash Ahluwalia", retrievedStaff.getName()); // check the name
        assertEquals("1234567890", retrievedStaff.getContactNumber()); // check the contact number
    }
}
