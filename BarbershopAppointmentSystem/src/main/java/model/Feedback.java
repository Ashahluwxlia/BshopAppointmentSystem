/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author anacarolina
 */

import database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Feedback {
    private final String bookingReference; // booking reference for the feedback
    private final String customerName; // name of the customer providing feedback
    private final String feedbackText; // the feedback text
    private final LocalDateTime feedbackTime; // time the feedback was given

    // Constructor to create a Feedback object
    public Feedback(String bookingReference, String customerName, String feedbackText) {
        this.bookingReference = bookingReference; // set booking reference
        this.customerName = customerName; // set customer name
        this.feedbackText = feedbackText; // set feedback text
        this.feedbackTime = LocalDateTime.now(); // set feedback time to now
    }

    // Getters for feedback attributes
    public String getBookingReference() { return bookingReference; } // return booking reference
    public String getCustomerName() { return customerName; } // return customer name
    public String getFeedbackText() { return feedbackText; } // return feedback text
    public LocalDateTime getFeedbackTime() { return feedbackTime; } // return feedback time

    // Save feedback details to the database
    public void saveToDatabase() {
        String sql = "INSERT INTO Feedback (bookingReference, customerName, feedbackText, feedbackTime) VALUES (?, ?, ?, ?)"; // SQL insert statement
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, bookingReference); // set booking reference
            pstmt.setString(2, customerName); // set customer name
            pstmt.setString(3, feedbackText); // set feedback text
            pstmt.setTimestamp(4, java.sql.Timestamp.valueOf(feedbackTime)); // set feedback time

            pstmt.executeUpdate(); // execute the insert

        } catch (SQLException e) {
            System.err.println("Error saving feedback to database: " + e.getMessage()); // print error message
        }
    }

    // Create a Feedback object from a ResultSet
    public static Feedback fromResultSet(ResultSet rs) throws SQLException {
        return new Feedback(
            rs.getString("bookingReference"), // get booking reference
            rs.getString("customerName"), // get customer name
            rs.getString("feedbackText") // get feedback text
        );
    }

    // Convert Feedback details to a string for display
    @Override
    public String toString() {
        return String.format(
            "Feedback Time: %s\nBooking Reference: %s\nCustomer Name: %s\nFeedback: %s\n",
            feedbackTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), // format feedback time
            bookingReference, customerName, feedbackText // include booking reference, customer name, and feedback text
        );
    }
}