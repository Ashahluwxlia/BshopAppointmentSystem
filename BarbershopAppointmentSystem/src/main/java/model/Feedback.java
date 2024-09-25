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
    private final String bookingReference;
    private final String customerName;
    private final String feedbackText;
    private final LocalDateTime feedbackTime;

    public Feedback(String bookingReference, String customerName, String feedbackText) {
        this.bookingReference = bookingReference;
        this.customerName = customerName;
        this.feedbackText = feedbackText;
        this.feedbackTime = LocalDateTime.now();
    }

    // Getters
    public String getBookingReference() { return bookingReference; }
    public String getCustomerName() { return customerName; }
    public String getFeedbackText() { return feedbackText; }
    public LocalDateTime getFeedbackTime() { return feedbackTime; }

    // Save feedback to the database
    public void saveToDatabase() {
        String sql = "INSERT INTO Feedback (bookingReference, customerName, feedbackText, feedbackTime) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, bookingReference);
            pstmt.setString(2, customerName);
            pstmt.setString(3, feedbackText);
            pstmt.setTimestamp(4, java.sql.Timestamp.valueOf(feedbackTime));

            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error saving feedback to database: " + e.getMessage());
        }
    }

    // Create a Feedback object from a ResultSet
    public static Feedback fromResultSet(ResultSet rs) throws SQLException {
        return new Feedback(
            rs.getString("bookingReference"),
            rs.getString("customerName"),
            rs.getString("feedbackText")
        );
    }

    @Override
    public String toString() {
        return String.format(
            "Feedback Time: %s\nBooking Reference: %s\nCustomer Name: %s\nFeedback: %s\n",
            feedbackTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
            bookingReference, customerName, feedbackText
        );
    }
}
