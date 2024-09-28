/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author ash
 */

import database.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class Appointment {
    private Customer customer; // customer who made the appointment
    private Staff staff; // staff assigned to the appointment
    private List<Service> services; // list of services for the appointment
    private LocalDateTime appointmentTime; // date and time of the appointment
    private String referenceNumber; // unique reference number for the booking

    // Constructor to create an Appointment object
    public Appointment(Customer customer, Staff staff, List<Service> services, LocalDateTime appointmentTime, String referenceNumber) {
        this.customer = customer;
        this.staff = staff;
        this.services = services;
        this.appointmentTime = appointmentTime;
        this.referenceNumber = referenceNumber;
    }

    // Getters and setters
    public Customer getCustomer() { return customer; } // return customer
    public void setCustomer(Customer customer) { this.customer = customer; } // set customer

    public Staff getStaff() { return staff; } // return staff
    public void setStaff(Staff staff) { this.staff = staff; } // set staff

    public List<Service> getServices() { return services; } // return list of services
    public void setServices(List<Service> services) { this.services = services; } // set list of services

    public LocalDateTime getAppointmentTime() { return appointmentTime; } // return appointment time
    public void setAppointmentTime(LocalDateTime appointmentTime) { this.appointmentTime = appointmentTime; } // set appointment time

    public String getReferenceNumber() { return referenceNumber; } // return reference number
    public void setReferenceNumber(String referenceNumber) { this.referenceNumber = referenceNumber; } // set reference number

    // Calculate the total cost of the appointment
    public double getTotalCost() {
        return services.stream().mapToDouble(Service::getPrice).sum(); // sum up the prices of all services
    }

    // Calculate the total duration of the appointment
    public int getTotalDuration() {
        return services.stream().mapToInt(Service::getDuration).sum(); // sum up the durations of all services
    }

    // Save the appointment details to the database
    public void saveToDatabase() {
        String sql = "INSERT INTO Appointments (referenceNumber, customerId, staffId, services, appointmentTime, totalCost, totalDuration) VALUES (?, ?, ?, ?, ?, ?, ?)"; // SQL insert statement
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            int customerId = customer.getCustomerId(); // get customer ID
            int staffId = staff.getStaffId(); // get staff ID
            if (customerId <= 0 || staffId <= 0) throw new SQLException("Invalid customer or staff ID."); // check for valid IDs

            pstmt.setString(1, referenceNumber); // set reference number
            pstmt.setInt(2, customerId); // set customer ID
            pstmt.setInt(3, staffId); // set staff ID
            pstmt.setString(4, getServicesAsString()); // set services as string
            pstmt.setTimestamp(5, Timestamp.valueOf(appointmentTime)); // set appointment time
            pstmt.setDouble(6, getTotalCost()); // set total cost
            pstmt.setInt(7, getTotalDuration()); // set total duration

            pstmt.executeUpdate(); // execute the insert
        } catch (SQLException e) {
            System.err.println("Error saving appointment to database: " + e.getMessage()); // print error message
            e.printStackTrace(); // print stack trace for debugging
        }
    }

    // Create an Appointment object from a ResultSet
    public static Appointment fromResultSet(ResultSet rs) throws SQLException {
        Customer customer = Customer.getCustomerById(rs.getInt("customerId")); // get customer from ID
        Staff staff = Staff.getStaffById(rs.getInt("staffId")); // get staff from ID
        List<Service> services = parseServices(rs.getString("services")); // parse services from string

        return new Appointment(customer, staff, services, 
                               rs.getTimestamp("appointmentTime").toLocalDateTime(), // get appointment time
                               rs.getString("referenceNumber")); // get reference number
    }

    // Parse a string of services into a list of Service objects
    private static List<Service> parseServices(String servicesStr) {
        return List.of(servicesStr.split(", ")) // split the string into individual services
                   .stream()
                   .map(Service::fromString) // convert each string to a Service object
                   .collect(Collectors.toList()); // collect into a list
    }

    // Convert services list to a formatted string for database storage
    private String getServicesAsString() {
        return services.stream()
                       .map(service -> String.join(",", service.getServiceName(), // format each service
                                                       String.valueOf(service.getPrice()), 
                                                       String.valueOf(service.getDuration())))
                       .collect(Collectors.joining(", ")); // join all services into a single string
    }

    // Convert the Appointment details to a string for display
    @Override
    public String toString() {
        String servicesString = services.stream()
                .map(service -> String.format("%s (Price: $%.2f, Duration: %d mins)", // format each service for display
                                               service.getServiceName(), 
                                               service.getPrice(), 
                                               service.getDuration()))
                .collect(Collectors.joining(", ")); // join services into a single string
        return String.format("Booking Reference: %s%nCustomer Name: %s %s%nCustomer Contact: %s%nCustomer Address: %s%nStaff: %s%nServices: %s%nAppointment Time: %s%n",
                referenceNumber, // format the output
                customer.getFirstName(), customer.getLastName(),
                customer.getContactNumber(),
                customer.getAddress(),
                staff.getName(),
                servicesString,
                appointmentTime.format(DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a"))); // format appointment time
    }
}