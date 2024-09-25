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
import java.time.LocalDate;

public class Customer {
    private int id;
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private String contactNumber;
    private String gender;
    private Address address;

    public Customer(int id, String firstName, String lastName, LocalDate birthDate, String contactNumber, String gender, Address address) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.contactNumber = contactNumber;
        this.gender = gender;
        this.address = address;
    }

    public Customer(String firstName, String lastName, LocalDate birthDate, String contactNumber, String gender, Address address) {
        this(0, firstName, lastName, birthDate, contactNumber, gender, address);
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }

    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public Address getAddress() { return address; }
    public void setAddress(Address address) { this.address = address; }

    @Override
    public String toString() {
        return String.format("%s %s%nCustomer Contact: %s%nCustomer Address: %s", 
                firstName, lastName, contactNumber, address);
    }

    // Save customer to the database
    public void saveToDatabase() {
        String sql = "INSERT INTO Customers (firstName, lastName, birthDate, contactNumber, gender, street, city, state, zipCode) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, firstName);
            pstmt.setString(2, lastName);
            pstmt.setDate(3, Date.valueOf(birthDate));
            pstmt.setString(4, contactNumber);
            pstmt.setString(5, gender);
            pstmt.setString(6, address.getStreet());
            pstmt.setString(7, address.getCity());
            pstmt.setString(8, address.getState());
            pstmt.setString(9, address.getZipCode());

            pstmt.executeUpdate();
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                this.id = rs.getInt(1);
            }

        } catch (SQLException e) {
            System.err.println("Error saving customer to database: " + e.getMessage());
        }
    }

    // Get Customer ID from the database
    public int getCustomerId() throws SQLException {
        String sql = "SELECT id FROM Customers WHERE firstName = ? AND lastName = ? AND contactNumber = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, firstName);
            pstmt.setString(2, lastName);
            pstmt.setString(3, contactNumber);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            } else {
                throw new SQLException("Customer not found in the database.");
            }
        }
    }

    // Get Customer by ID from the database
    public static Customer getCustomerById(int id) throws SQLException {
        String sql = "SELECT * FROM Customers WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return fromResultSet(rs);
            } else {
                throw new SQLException("Customer with ID " + id + " not found.");
            }
        }
    }

    // Create a Customer from a ResultSet
    public static Customer fromResultSet(ResultSet rs) throws SQLException {
        return new Customer(
                rs.getInt("id"),
                rs.getString("firstName"),
                rs.getString("lastName"),
                rs.getDate("birthDate").toLocalDate(),
                rs.getString("contactNumber"),
                rs.getString("gender"),
                new Address(
                        rs.getString("street"),
                        rs.getString("city"),
                        rs.getString("state"),
                        rs.getString("zipCode")
                )
        );
    }

    // Create a Customer from a string (legacy purposes)
    public static Customer fromString(String str) {
        String[] parts = str.split("\n");
        if (parts.length < 3) throw new IllegalArgumentException("Invalid customer string: " + str);

        String[] nameParts = parts[0].split(" ");
        String contactNumber = parts[1].split(":")[1].trim();
        Address address = Address.fromString(parts[2].split(":")[1].trim());

        return new Customer(nameParts[0], nameParts[1], null, contactNumber, null, address);
    }
}