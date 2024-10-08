/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package test.controller;

/**
 *
 * @author ash
 */

import controller.AppointmentManager;
import database.DatabaseConnection;
import model.Appointment;
import model.Customer;
import model.Service;
import model.Staff;
import model.Address;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;

import static org.junit.Assert.assertFalse;

public class AppointmentManagerOverlapTest {

    private AppointmentManager manager;

    @Before
    public void setUp() {
        manager = new AppointmentManager();

        // clean up only the appointments table before each test
        try (var conn = DatabaseConnection.getConnection()) {
            try (var pstmt = conn.prepareStatement("DELETE FROM Appointments WHERE referenceNumber LIKE 'TEST_%'")) {
                pstmt.executeUpdate();  // delete test appointments
            }
        } catch (Exception e) {
            System.err.println("Error during test setup: " + e.getMessage());
            e.printStackTrace();  // print error if connection fails
        }
    }

    @Test
    public void testBookingOverlappingSlots() {
        // set up test data
        Address address = new Address("Rua", "Rio de Janeiro", "RJ", "22041-011");
        Customer customer = new Customer("Sofia", "Martins", LocalDate.of(1990, 5, 15), "9876543210", "Female", address);
        customer.saveToDatabase();  // save customer to database

        Staff staff = new Staff("Ana Carolina", "0987654321");
        staff.saveToDatabase();  // save staff to database

        Service service = new Service("Pedicure", 50.0, 50);  // create a service for the appointment

        LocalDateTime appointmentTime = LocalDateTime.of(2024, 9, 1, 9, 0);
        Appointment appointment1 = new Appointment(customer, staff, Arrays.asList(service), appointmentTime, "TEST_REF654321");
        manager.addAppointment(appointment1);  // add the first appointment

        // reload appointments from the database to check for overlaps
        manager.loadAppointmentsFromDatabase();

        // calculate the total duration for the services in minutes
        int totalDuration = service.getDuration();

        // check if the slot at the original appointment time is available
        boolean isSlotAvailable = manager.getAvailableSlots(staff, appointmentTime.toLocalDate(), totalDuration)
            .contains(appointmentTime.toLocalTime());

        assertFalse(isSlotAvailable);  // expect the slot to be unavailable due to overlap
    }
}

