/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package test.model;

/**
 *
 * @author anacarolina
 */


import model.Appointment;
import model.Customer;
import model.Service;
import model.Staff;
import model.Address;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;

public class TotalCostTest {

    private static final String TEST_REF = "TEST_REF789012";
    private static final double SERVICE1_PRICE = 80.0;
    private static final double SERVICE2_PRICE = 50.0;
    private static final double EXPECTED_TOTAL_COST = 130.0;

    @Test
    public void testTotalCostCalculation() {
        // arrange: set up the necessary data
        Address address = new Address("Avenida Paulista", "Sao Paulo", "SP", "01310-000");
        Customer customer = new Customer("Carlos", "Silva", LocalDate.of(1995, 7, 10), "0567890123", "Male", address);
        Staff staff = new Staff("Test Staff", "0123456789");

        Service service1 = new Service("Hair Coloring", SERVICE1_PRICE, 60);
        Service service2 = new Service("Hairstyling", SERVICE2_PRICE, 45);

        LocalDateTime appointmentTime = LocalDateTime.of(2024, 9, 2, 10, 0);

        // act: create an appointment with the services
        Appointment appointment = new Appointment(customer, staff, Arrays.asList(service1, service2), appointmentTime, TEST_REF);

        // assert: verify that the total cost is correctly calculated
        assertEquals(EXPECTED_TOTAL_COST, appointment.getTotalCost(), 0.01); // check if total cost matches expected value
    }
}