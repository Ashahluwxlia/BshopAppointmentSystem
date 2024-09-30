/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

/**
 *
 * @author ash
 */

import model.Appointment;
import model.Customer;
import model.Service;
import model.Staff;
import database.DatabaseConnection;
import java.util.stream.Collectors;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class AppointmentManager {
    private List<Appointment> appointments;
    private List<Staff> staffList;
    private static final String TEST_PREFIX = "TEST_"; // prefix for test appointments

    private static final LocalTime WORKING_HOURS_START = LocalTime.of(9, 0);
    private static final LocalTime WORKING_HOURS_END = LocalTime.of(17, 0);
    private static final int SLOT_INTERVAL_MINUTES = 30; // each slot is 30 minutes

    public AppointmentManager() {
        this.appointments = new ArrayList<>();
        this.staffList = new ArrayList<>();
        initialize(); // load initial staff and appointments from the database
    }

    // load staff and appointments from the database
    private void initialize() {
        loadStaffFromDatabase();
        loadAppointmentsFromDatabase();
    }

    // add a new appointment and save it to the database
    public void addAppointment(Appointment appointment) {
        appointments.add(appointment);
        appointment.saveToDatabase(); // save immediately so it's persistent
    }

    // add a new customer to the database
    public void addCustomer(Customer customer) {
        String sql = "INSERT INTO Customers (firstName, lastName, birthDate, contactNumber, gender, street, city, state, zipCode) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        executeUpdate(sql, pstmt -> {
            pstmt.setString(1, customer.getFirstName());
            pstmt.setString(2, customer.getLastName());
            pstmt.setDate(3, Date.valueOf(customer.getBirthDate()));
            pstmt.setString(4, customer.getContactNumber());
            pstmt.setString(5, customer.getGender());
            pstmt.setString(6, customer.getAddress().getStreet());
            pstmt.setString(7, customer.getAddress().getCity());
            pstmt.setString(8, customer.getAddress().getState());
            pstmt.setString(9, customer.getAddress().getZipCode());
        });
    }

    // find an appointment by its reference number
    public Appointment getAppointmentByReference(String referenceNumber) {
        String sql = "SELECT * FROM Appointments WHERE referenceNumber = ?";
        return executeQuery(sql, pstmt -> pstmt.setString(1, referenceNumber), Appointment::fromResultSet);
    }

    // load appointments from the database into the list
    public void loadAppointmentsFromDatabase() {
        appointments.clear(); // clear old data before loading
        String sql = "SELECT * FROM Appointments";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Appointment appointment = Appointment.fromResultSet(rs);
                appointments.add(appointment); // add each appointment to the list
            }

        } catch (SQLException e) {
            System.err.println("Error loading appointments from database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // return all the appointments in a new list
    public List<Appointment> getAllAppointments() {
        return new ArrayList<>(appointments);
    }

    // remove an appointment from the database and the list
    public boolean removeAppointmentByReference(String referenceNumber) {
        String sql = "DELETE FROM Appointments WHERE referenceNumber = ?";
        int affectedRows = 0;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, referenceNumber);
            affectedRows = pstmt.executeUpdate(); // try to remove from the database

        } catch (SQLException e) {
            System.err.println("Error removing appointment from database: " + e.getMessage());
            e.printStackTrace();
        }

        boolean removed = appointments.removeIf(appointment -> appointment.getReferenceNumber().equals(referenceNumber)); // also remove from the list
        return affectedRows > 0 && removed; // return true if both database and list were updated
    }

    // generate a list of available time slots for a given staff and date
    public List<LocalTime> getAvailableSlots(Staff staff, LocalDate date, int totalDuration) {
        List<LocalTime> allSlots = generateAllSlots();
        List<LocalTime> bookedSlots = getBookedSlotsForStaff(staff, date);

        return allSlots.stream()
            .filter(slot -> isSlotAvailable(slot, totalDuration, bookedSlots))
            .collect(Collectors.toList());
    }

    // get all booked slots for a given staff member on a specific date
    public List<LocalTime> getBookedSlotsForStaff(Staff staff, LocalDate date) {
        List<LocalTime> bookedSlots = new ArrayList<>();

        for (Appointment appointment : appointments) {
            if (appointment.getStaff().getName().equals(staff.getName()) && appointment.getAppointmentTime().toLocalDate().equals(date)) {

                LocalTime startTime = appointment.getAppointmentTime().toLocalTime();
                int duration = appointment.getTotalDuration();
                LocalTime endTime = startTime.plusMinutes(duration);

                // add all slots within the appointment duration
                LocalTime slotTime = startTime;
                while (slotTime.isBefore(endTime)) {
                    bookedSlots.add(slotTime);
                    slotTime = slotTime.plusMinutes(SLOT_INTERVAL_MINUTES);
                }
            }
        }

        return bookedSlots;
    }

    // check if a time slot is available (no overlap with booked slots)
    private boolean isSlotAvailable(LocalTime slot, int totalDuration, List<LocalTime> bookedSlots) {
        LocalTime endSlotTime = slot.plusMinutes(totalDuration);
        if (endSlotTime.isAfter(WORKING_HOURS_END)) return false; // slot is outside of working hours

        LocalTime checkTime = slot;
        while (checkTime.isBefore(endSlotTime)) {
            if (bookedSlots.contains(checkTime)) return false; // there's a conflict
            checkTime = checkTime.plusMinutes(SLOT_INTERVAL_MINUTES);
        }
        return true; // no conflict, slot is free
    }

    // generate all possible time slots in a day
    private List<LocalTime> generateAllSlots() {
        List<LocalTime> slots = new ArrayList<>();
        LocalTime startTime = WORKING_HOURS_START;
        while (startTime.isBefore(WORKING_HOURS_END)) {
            slots.add(startTime); // add each 30-minute slot
            startTime = startTime.plusMinutes(SLOT_INTERVAL_MINUTES);
        }
        return slots;
    }

    // return the list of all staff
    public List<Staff> getStaffList() {
        return new ArrayList<>(staffList);
    }

    // load staff from the database into the list
    private void loadStaffFromDatabase() {
        staffList.clear(); // clear old data before loading
        String sql = "SELECT * FROM Staff";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Staff staff = Staff.fromResultSet(rs);
                staffList.add(staff); // add each staff member to the list
            }

        } catch (SQLException e) {
            System.err.println("Error loading staff from database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // clear all data (appointments, customers, feedback)
    public void clearAllData() {
        clearAppointments();
        clearCustomers();
        clearFeedback();
    }
    
    // clear all test appointments (those with reference numbers starting with TEST_)
    public void clearTestAppointments() {
        String sql = "DELETE FROM Appointments WHERE referenceNumber LIKE ?";
        executeUpdate(sql, pstmt -> pstmt.setString(1, TEST_PREFIX + "%"));
        appointments.removeIf(appointment -> appointment.getReferenceNumber().startsWith(TEST_PREFIX));
    }
        
    // clear all appointments from both the database and the list
    private void clearAppointments() {
        String sql = "DELETE FROM Appointments";
        executeUpdate(sql, pstmt -> {});
        appointments.clear(); // also clear the list
    }

    // clear all customers from the database
    private void clearCustomers() {
        String sql = "DELETE FROM Customers";
        executeUpdate(sql, pstmt -> {});
    }

    // clear all feedback from the database
    private void clearFeedback() {
        String sql = "DELETE FROM Feedback";
        executeUpdate(sql, pstmt -> {});
    }

    // generate a reference number for test appointments
    public static String generateTestReference() {
        return TEST_PREFIX + System.currentTimeMillis(); // use the current time as part of the reference
    }

    // execute an update SQL statement
    private void executeUpdate(String sql, SqlConsumer<PreparedStatement> consumer) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            consumer.accept(pstmt); // apply parameters
            pstmt.executeUpdate(); // run the update
        } catch (SQLException e) {
            System.err.println("Error executing update: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // execute a query SQL statement
    private <T> T executeQuery(String sql, SqlConsumer<PreparedStatement> consumer, SqlFunction<ResultSet, T> extractor) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            consumer.accept(pstmt); // apply parameters
            ResultSet rs = pstmt.executeQuery(); // execute the query
            if (rs.next()) {
                return extractor.apply(rs); // extract the result
            }
        } catch (SQLException e) {
            System.err.println("Error executing query: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @FunctionalInterface
    private interface SqlConsumer<T> {
        void accept(T t) throws SQLException;
    }

    @FunctionalInterface
    private interface SqlFunction<T, R> {
        R apply(T t) throws SQLException;
    }
    
public static void main(String[] args) {
    AppointmentManager manager = new AppointmentManager(); // create an instance of AppointmentManager
    List<Appointment> appointments = manager.getAllAppointments(); // get all appointments

    // check if the appointments list is empty
    if (appointments.isEmpty()) {
        System.out.println("No appointments found."); // show message if no appointments
    } else {
        System.out.println("All Appointments:");
        for (Appointment appointment : appointments) {
            System.out.println(appointment); // print each appointment
        }
    }
    }
}

