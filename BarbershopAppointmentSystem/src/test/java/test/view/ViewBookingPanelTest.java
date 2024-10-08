/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package test.view;

/**
 *
 * @author anacarolina
 */
import controller.AppointmentManager;
import model.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import view.ViewBookingPanel;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class ViewBookingPanelTest {

    private JFrame frame;
    private AppointmentManager manager;
    private String testReferenceNumber; // store the generated test reference number

    @Before
    public void setUp() {
        frame = new JFrame(); // create a new JFrame for testing
        manager = new AppointmentManager(); // create a new appointment manager

        // add a test appointment to the manager for searching
        Address address = new Address("Rua das Flores", "Rio de Janeiro", "RJ", "22041-011");
        Customer customer = new Customer("Sofia", "Martins", LocalDate.of(1990, 5, 15), "9876543210", "Female", address);
        Staff staff = new Staff("Ana Carolina", "0987654321");
        Service service = new Service("Pedicure", 50.0, 50);
        LocalDateTime appointmentTime = LocalDateTime.of(2024, 9, 1, 9, 0);

        // use the test prefix for the reference number
        testReferenceNumber = AppointmentManager.generateTestReference();
        Appointment appointment = new Appointment(customer, staff, Arrays.asList(service), appointmentTime, testReferenceNumber);
        manager.addAppointment(appointment); // add the test appointment to the manager
    }

    @Test
    public void testSearchAppointment() {
        SwingUtilities.invokeLater(() -> {
            ViewBookingPanel viewBookingPanel = new ViewBookingPanel(frame, manager); // create the view booking panel
            frame.setContentPane(viewBookingPanel); // set the panel as the content of the frame
            frame.pack(); // pack the frame to fit components

            // simulate entering the generated test reference number
            JTextField referenceNumberField = getComponent(viewBookingPanel, JTextField.class);
            referenceNumberField.setText(testReferenceNumber); // set the reference number in the text field

            // simulate clicking the search button
            JButton searchButton = getComponent(viewBookingPanel, JButton.class, "Search");
            searchButton.doClick(); // simulate button click

            // verify that the appointment details are correctly displayed
            JTextArea resultArea = getComponent(viewBookingPanel, JTextArea.class);
            String expectedText = String.format(
                "Booking Reference: %s\n" +
                "Customer Name: Sofia Martins\n" +
                "Customer Contact: 9876543210\n" +
                "Customer Address: Rua das Flores, Rio de Janeiro, RJ, 22041-011\n" +
                "Staff: Ana Carolina\n" +
                "Services: Pedicure (Price: $50.0, Duration: 50 mins)\n" +
                "Appointment Time: 01 Sep 2024, 09:00 AM\n",
                testReferenceNumber
            );
            assertEquals(expectedText.trim(), resultArea.getText().trim()); // check if displayed text matches expected
        });
    }

    @After
    public void tearDown() {
        // clear only test-related data
        manager.clearTestAppointments(); // clean up test appointments from the manager
    }

    // helper method to get components by type and name (if specified)
    private <T extends JComponent> T getComponent(Container container, Class<T> componentClass, String... componentName) {
        for (Component comp : container.getComponents()) {
            if (componentClass.isInstance(comp) && (componentName.length == 0 || comp.getName().equals(componentName[0]))) {
                return componentClass.cast(comp); // return the component if found
            }
            if (comp instanceof Container) {
                T result = getComponent((Container) comp, componentClass, componentName); // search recursively in containers
                if (result != null) {
                    return result; // return the found component
                }
            }
        }
        return null; // return null if not found
    }
}
