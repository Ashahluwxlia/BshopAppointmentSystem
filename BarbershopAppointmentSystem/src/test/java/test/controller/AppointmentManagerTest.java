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
import model.Appointment;
import model.Customer;
import model.Service;
import model.Staff;
import model.Address;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class AppointmentManagerTest {

    private AppointmentManager manager;
    private String testReference;

    @Before
    public void setUp() {
        manager = new AppointmentManager();

        // generate a unique test reference number for this test
        testReference = AppointmentManager.generateTestReference();

        // create a test appointment
        Address address = new Address("Rua das ", "Rio de Janeiro", "RJ", "22041-011");
        Customer customer = new Customer("Sofia", "Martins", LocalDate.of(1990, 5, 15), "9876543210", "Female", address);
        customer.saveToDatabase();  // save the customer to the database

        Staff staff = new Staff("Ana Carolina", "0987654321");
        staff.saveToDatabase();  // save the staff to the database

        Service service = new Service("Pedicure", 50.0, 50);
        LocalDateTime appointmentTime = LocalDateTime.of(2024, 9, 1, 9, 0);

        // create the appointment and add it to the manager
        Appointment appointment = new Appointment(customer, staff, Arrays.asList(service), appointmentTime, testReference);
        manager.addAppointment(appointment);
        manager.loadAppointmentsFromDatabase(); // load appointments after adding to ensure they’re there
    }

    @After
    public void tearDown() {
        // clear only test appointments after each test to avoid conflicts
        manager.clearTestAppointments();
        manager.clearAllData();  // clear all data for a clean state
    }

    @Test
    public void testRetrieveExistingAppointment() {
        // check if the appointment can be retrieved with the test reference
        Appointment appointment = manager.getAppointmentByReference(testReference);
        assertNotNull(appointment);  // expect to find the appointment

        // check for a non-existent appointment reference
        Appointment nonExistentAppointment = manager.getAppointmentByReference("NON_EXISTENT_REF");
        assertNull(nonExistentAppointment);  // expect this to be null
    }
}

