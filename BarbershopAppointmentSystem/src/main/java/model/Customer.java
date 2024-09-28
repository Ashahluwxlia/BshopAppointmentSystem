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
    private int id; // unique ID for the customer
    private String firstName; // customer's first name
    private String lastName; // customer's last name
    private LocalDate birthDate; // customer's birth date
    private String contactNumber; // customer's contact number
    private String gender; // customer's gender
    private Address address; // customer's address

    // Constructor to create a Customer object with an ID
    public Customer(int id, String firstName, String lastName, LocalDate birthDate, String contactNumber, String gender, Address address) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.contactNumber = contactNumber;
        this.gender = gender;
        this.address = address;
    }

    // Constructor to create a Customer object without an ID
    public Customer(String firstName, String lastName, LocalDate birthDate, String contactNumber, String gender, Address address) {
        this(0, firstName, lastName, birthDate, contactNumber, gender, address); // call the other constructor
    }

    // Getters and setters
    public int getId() { return id; } // return customer ID
    public void setId(int id) { this.id = id; } // set customer ID

    public String getFirstName() { return firstName; } // return first name
    public void setFirstName(String firstName) { this.firstName = firstName; } // set first name

    public String getLastName() { return lastName; } // return last name
    public void setLastName(String lastName) { this.lastName = lastName; } // set last name

    public LocalDate getBirthDate() { return birthDate; } // return birth date
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; } // set birth date

    public String getContactNumber() { return contactNumber; } // return contact number
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; } // set contact number

    public String getGender() { return gender; } // return gender
    public void setGender(String gender) { this.gender = gender; } // set gender

    public Address getAddress() { return address; } // return address
    public void setAddress(Address address) { this.address = address; } // set address

    // Convert the Customer details to a string for display
    @Override
    public String toString() {
        return String.format("%s %s%nCustomer Contact: %s%nCustomer Address: %s", 
                firstName, lastName, contactNumber, address); // format output
    }

    // Save customer details to the database
    public void saveToDatabase() {
        String sql = "INSERT INTO Customers (firstName, lastName, birthDate, contactNumber, gender, street, city, state, zipCode) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)"; // SQL insert statement
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, firstName); // set first name
            pstmt.setString(2, lastName); // set last name
            pstmt.setDate(3, Date.valueOf(birthDate)); // set birth date
            pstmt.setString(4, contactNumber); // set contact number
            pstmt.setString(5, gender); // set gender
            pstmt.setString(6, address.getStreet()); // set street from address
            pstmt.setString(7, address.getCity()); // set city from address
            pstmt.setString(8, address.getState()); // set state from address
            pstmt.setString(9, address.getZipCode()); // set zip code from address

            pstmt.executeUpdate(); // execute the insert
            ResultSet rs = pstmt.getGeneratedKeys(); // get generated keys
            if (rs.next()) {
                this.id = rs.getInt(1); // set the customer ID
            }

        } catch (SQLException e) {
            System.err.println("Error saving customer to database: " + e.getMessage()); // print error message
        }
    }

    // Get Customer ID from the database based on details
    public int getCustomerId() throws SQLException {
        String sql = "SELECT id FROM Customers WHERE firstName = ? AND lastName = ? AND contactNumber = ?"; // SQL query
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, firstName); // set first name
            pstmt.setString(2, lastName); // set last name
            pstmt.setString(3, contactNumber); // set contact number

            ResultSet rs = pstmt.executeQuery(); // execute query
            if (rs.next()) {
                return rs.getInt("id"); // return customer ID
            } else {
                throw new SQLException("Customer not found in the database."); // throw error if not found
            }
        }
    }

    // Get Customer by ID from the database
    public static Customer getCustomerById(int id) throws SQLException {
        String sql = "SELECT * FROM Customers WHERE id = ?"; // SQL query
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id); // set customer ID
            ResultSet rs = pstmt.executeQuery(); // execute query

            if (rs.next()) {
                return fromResultSet(rs); // create Customer object from ResultSet
            } else {
                throw new SQLException("Customer with ID " + id + " not found."); // throw error if not found
            }
        }
    }

    // Create a Customer object from a ResultSet
    public static Customer fromResultSet(ResultSet rs) throws SQLException {
        return new Customer(
                rs.getInt("id"), // get customer ID
                rs.getString("firstName"), // get first name
                rs.getString("lastName"), // get last name
                rs.getDate("birthDate").toLocalDate(), // get birth date
                rs.getString("contactNumber"), // get contact number
                rs.getString("gender"), // get gender
                new Address(
                        rs.getString("street"), // get street
                        rs.getString("city"), // get city
                        rs.getString("state"), // get state
                        rs.getString("zipCode") // get zip code
                )
        );
    }

    // Create a Customer from a string (for legacy purposes)
    public static Customer fromString(String str) {
        String[] parts = str.split("\n"); // split the input string into parts
        if (parts.length < 3) throw new IllegalArgumentException("Invalid customer string: " + str); // check for valid input

        String[] nameParts = parts[0].split(" "); // split the name
        String contactNumber = parts[1].split(":")[1].trim(); // get contact number
        Address address = Address.fromString(parts[2].split(":")[1].trim()); // get address

        return new Customer(nameParts[0], nameParts[1], null, contactNumber, null, address); // create Customer object
    }
}