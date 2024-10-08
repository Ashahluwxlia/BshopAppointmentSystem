/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package test.view;

/**
 *
 * @author anacarolina
 */
import model.Customer;
import model.Address;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

import java.time.LocalDate;

public class CustomerTest {

    private Customer customer; // declare the customer object
    private Address address; // declare the address object

    @Before
    public void setUp() {
        // set up test data before each test
        address = new Address("Marine Drive", "Mumbai", "MH", "400005"); // create a new address
        customer = new Customer("Ravi", "Kumar", LocalDate.of(1992, 11, 12), "0123456789", "Male", address); // create a new customer
    }

    @Test
    public void testCustomerNames() {
        // check if the customer's first name is correct
        assertEquals("Ravi", customer.getFirstName());
        // check if the customer's last name is correct
        assertEquals("Kumar", customer.getLastName());
        // check if the full name is correctly concatenated
        assertEquals("Ravi Kumar", customer.getFirstName() + " " + customer.getLastName());
    }

    @Test
    public void testCustomerAddress() {
        // check if the customer's address matches the expected address
        assertEquals(address, customer.getAddress());
    }
}
