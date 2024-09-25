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
    private Customer customer;
    private Staff staff;
    private List<Service> services;
    private LocalDateTime appointmentTime;
    private String referenceNumber;

    public Appointment(Customer customer, Staff staff, List<Service> services, LocalDateTime appointmentTime, String referenceNumber) {
        this.customer = customer;
        this.staff = staff;
        this.services = services;
        this.appointmentTime = appointmentTime;
        this.referenceNumber = referenceNumber;
    }

    // Getters and setters
    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }

    public Staff getStaff() { return staff; }
    public void setStaff(Staff staff) { this.staff = staff; }

    public List<Service> getServices() { return services; }
    public void setServices(List<Service> services) { this.services = services; }

    public LocalDateTime getAppointmentTime() { return appointmentTime; }
    public void setAppointmentTime(LocalDateTime appointmentTime) { this.appointmentTime = appointmentTime; }

    public String getReferenceNumber() { return referenceNumber; }
    public void setReferenceNumber(String referenceNumber) { this.referenceNumber = referenceNumber; }

    public double getTotalCost() {
        return services.stream().mapToDouble(Service::getPrice).sum();
    }

    public int getTotalDuration() {
        return services.stream().mapToInt(Service::getDuration).sum();
    }

    public void saveToDatabase() {
        String sql = "INSERT INTO Appointments (referenceNumber, customerId, staffId, services, appointmentTime, totalCost, totalDuration) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            int customerId = customer.getCustomerId();
            int staffId = staff.getStaffId();
            if (customerId <= 0 || staffId <= 0) throw new SQLException("Invalid customer or staff ID.");

            pstmt.setString(1, referenceNumber);
            pstmt.setInt(2, customerId);
            pstmt.setInt(3, staffId);
            pstmt.setString(4, getServicesAsString());
            pstmt.setTimestamp(5, Timestamp.valueOf(appointmentTime));
            pstmt.setDouble(6, getTotalCost());
            pstmt.setInt(7, getTotalDuration());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error saving appointment to database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static Appointment fromResultSet(ResultSet rs) throws SQLException {
        Customer customer = Customer.getCustomerById(rs.getInt("customerId"));
        Staff staff = Staff.getStaffById(rs.getInt("staffId"));
        List<Service> services = parseServices(rs.getString("services"));

        return new Appointment(customer, staff, services, 
                               rs.getTimestamp("appointmentTime").toLocalDateTime(), 
                               rs.getString("referenceNumber"));
    }

    private static List<Service> parseServices(String servicesStr) {
        return List.of(servicesStr.split(", "))
                   .stream()
                   .map(Service::fromString)
                   .collect(Collectors.toList());
    }

    private String getServicesAsString() {
        return services.stream()
                       .map(service -> String.join(",", service.getServiceName(), 
                                                       String.valueOf(service.getPrice()), 
                                                       String.valueOf(service.getDuration())))
                       .collect(Collectors.joining(", "));
    }

    @Override
    public String toString() {
        String servicesString = services.stream()
                .map(service -> String.format("%s (Price: $%.2f, Duration: %d mins)", 
                                               service.getServiceName(), 
                                               service.getPrice(), 
                                               service.getDuration()))
                .collect(Collectors.joining(", "));
        return String.format("Booking Reference: %s%nCustomer Name: %s %s%nCustomer Contact: %s%nCustomer Address: %s%nStaff: %s%nServices: %s%nAppointment Time: %s%n",
                referenceNumber,
                customer.getFirstName(), customer.getLastName(),
                customer.getContactNumber(),
                customer.getAddress(),
                staff.getName(),
                servicesString,
                appointmentTime.format(DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a")));
    }
}