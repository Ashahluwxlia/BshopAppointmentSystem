/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package test.model;

/**
 *
 * @author anacarolina
 */

import database.DatabaseConnection;
import model.Appointment;
import model.Customer;
import model.Service;
import model.Staff;
import model.Address;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class AppointmentTest {

    @Before
    public void setup() {
        cleanUpTestData(); // clean up test data before each test
    }

    @After
    public void cleanup() {
        cleanUpTestData(); // clean up test data after each test
    }

    private void cleanUpTestData() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String deleteAppointments = "DELETE FROM Appointments WHERE referenceNumber LIKE 'TEST_%'"; // remove test appointments
            String deleteStaff = "DELETE FROM Staff WHERE name IN ('Gagandeep Ahluwalia')"; // remove test staff
            String deleteCustomers = "DELETE FROM Customers WHERE firstName = 'Ahmed' AND lastName = 'Al-Mansoori'"; // remove test customer

            try (PreparedStatement pstmt1 = conn.prepareStatement(deleteAppointments);
                 PreparedStatement pstmt2 = conn.prepareStatement(deleteStaff);
                 PreparedStatement pstmt3 = conn.prepareStatement(deleteCustomers)) {
                pstmt1.executeUpdate(); // execute delete for appointments
                pstmt2.executeUpdate(); // execute delete for staff
                pstmt3.executeUpdate(); // execute delete for customers
            }
        } catch (SQLException e) {
            e.printStackTrace(); // log any SQL exceptions
        }
    }

    @Test
    public void testCreateNewAppointment() throws SQLException {
        // set up test data
        Address address = new Address("Palm Jumeirah", "Dubai", "DXB", "12345");
        Customer customer = new Customer("Ahmed", "Al-Mansoori", LocalDate.of(1985, 4, 25), "0501234567", "Male", address);
        customer.saveToDatabase(); // save test customer to the database

        Staff staff = new Staff("Gagandeep Ahluwalia", "0123456789");
        staff.saveToDatabase(); // save test staff to the database

        Service service1 = new Service("Haircut", 30.0, 30);
        Service service2 = new Service("Shave", 20.0, 15);

        LocalDateTime appointmentTime = LocalDateTime.of(2024, 9, 1, 10, 0);
        Appointment appointment = new Appointment(customer, staff, Arrays.asList(service1, service2), appointmentTime, "TEST_REF123456");

        // save the appointment to the database
        appointment.saveToDatabase();

        // retrieve the appointment back from the database
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM Appointments WHERE referenceNumber = ?")) {

            pstmt.setString(1, "TEST_REF123456"); // set reference number for query

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Appointment retrievedAppointment = Appointment.fromResultSet(rs); // create appointment from result set

                    // verify that the retrieved appointment matches the saved one
                    assertEquals("TEST_REF123456", retrievedAppointment.getReferenceNumber());
                    assertEquals(2, retrievedAppointment.getServices().size());
                    assertEquals("Ahmed Al-Mansoori", retrievedAppointment.getCustomer().getFirstName() + " " + retrievedAppointment.getCustomer().getLastName());
                    assertEquals("Gagandeep Ahluwalia", retrievedAppointment.getStaff().getName());
                    assertEquals(appointmentTime, retrievedAppointment.getAppointmentTime());
                }
            }
        }
    }
}