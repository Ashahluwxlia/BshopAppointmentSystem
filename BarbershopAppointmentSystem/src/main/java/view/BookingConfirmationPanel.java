/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view;

/**
 *
 * @author anacarolina
 */

import controller.AppointmentManager;
import controller.SlideshowBackgroundPanel;
import model.Appointment;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

public class BookingConfirmationPanel extends JPanel {

    public BookingConfirmationPanel(JFrame frame, AppointmentManager manager, Appointment appointment) {
        frame.setSize(800, 600); // Set frame size

        // Create the slideshow background panel
        SlideshowBackgroundPanel backgroundPanel = new SlideshowBackgroundPanel("", 6, 3000); // Set up slideshow
        setLayout(new BorderLayout(10, 10));
        add(backgroundPanel, BorderLayout.CENTER);  // Add background to the main panel

        // Main container for confirmation and buttons
        JPanel mainContainer = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 10, 10, 10); // Add some space between components

        mainContainer.setOpaque(false); // Transparent background for the main container

        // Create content panels with transparency
        JPanel confirmationPanel = backgroundPanel.createTransparentPanel(new BorderLayout(10, 10), 0.7f, new Color(255, 255, 255, 150)); // Semi-transparent confirmation panel
        JPanel textAreaPanel = backgroundPanel.createTransparentPanel(new BorderLayout(), 0.7f, new Color(255, 255, 255, 150)); // Semi-transparent text area panel
        JPanel buttonPanel = backgroundPanel.createTransparentPanel(new FlowLayout(FlowLayout.CENTER, 20, 10), 0.7f, new Color(255, 255, 255, 150)); // Semi-transparent button panel

        // Populate confirmation panel
        JLabel confirmationLabel = createTransparentLabel("Booking Confirmed!", 28, new Color(0, 76, 153)); // Confirmation label
        confirmationPanel.add(confirmationLabel, BorderLayout.NORTH);

        // Create the JTextArea for appointment details
        JTextArea appointmentDetails = createTransparentTextArea(formatAppointmentDetails(appointment));
        JScrollPane scrollPane = new JScrollPane(appointmentDetails);
        scrollPane.setOpaque(false); // Make JScrollPane transparent
        scrollPane.getViewport().setOpaque(false); // Make viewport transparent
        textAreaPanel.add(scrollPane, BorderLayout.CENTER);
        confirmationPanel.add(textAreaPanel, BorderLayout.CENTER); // Add text area to confirmation panel

        // Populate button panel with actions
        buttonPanel.add(createButton("Back to Main Menu", e -> switchToMainPanel(frame, manager))); // Back button
        buttonPanel.add(createButton("Feedback for Booking", e -> switchToFeedbackPanel(frame, appointment, manager), new Color(0, 153, 76))); // Feedback button in green
        buttonPanel.add(createButton("Exit", e -> System.exit(0), new Color(255, 69, 58))); // Exit button in red

        // Add confirmation and button panels to the main container
        gbc.gridy = 0;
        mainContainer.add(confirmationPanel, gbc);
        gbc.gridy = 1;
        mainContainer.add(buttonPanel, gbc);

        // Center the mainContainer within the background
        backgroundPanel.setLayout(new GridBagLayout());
        backgroundPanel.add(mainContainer);
    }

    private JLabel createTransparentLabel(String text, int fontSize, Color color) {
        JLabel label = new JLabel(text, JLabel.CENTER); // Create a label
        label.setFont(new Font("Verdana", Font.BOLD, fontSize)); // Set font
        label.setForeground(color); // Set text color
        label.setOpaque(false); // Make label transparent
        return label;
    }

    private JTextArea createTransparentTextArea(String text) {
        JTextArea textArea = new JTextArea(text); // Create a text area
        textArea.setEditable(false); // Make it non-editable
        textArea.setFont(new Font("Serif", Font.PLAIN, 18)); // Set font
        textArea.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 2)); // Set border
        textArea.setOpaque(false); // Ensure transparency
        textArea.setBackground(new Color(255, 255, 255, 0)); // Fully transparent background
        return textArea;
    }

    private JButton createButton(String text, ActionListener actionListener) {
        return createButton(text, actionListener, new Color(0, 102, 204)); // Default button color is blue
    }

    private JButton createButton(String text, ActionListener actionListener, Color bgColor) {
        JButton button = new JButton(text); // Create button
        button.setFont(new Font("Verdana", Font.BOLD, 16)); // Set font
        button.setBackground(bgColor); // Set background color
        button.setForeground(Color.WHITE); // Set text color to white
        button.setOpaque(true); // Make button opaque
        button.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1)); // Set border
        button.addActionListener(actionListener); // Add action listener
        return button;
    }

    private void switchToMainPanel(JFrame frame, AppointmentManager manager) {
        frame.setContentPane(new MainPanel(frame, manager)); // Switch to main panel
        frame.revalidate(); // Refresh the frame
    }

    private void switchToFeedbackPanel(JFrame frame, Appointment appointment, AppointmentManager manager) {
        String customerName = appointment.getCustomer().getFirstName() + " " + appointment.getCustomer().getLastName(); // Get customer name
        frame.setContentPane(new FeedbackPanel(frame, appointment.getReferenceNumber(), customerName, manager)); // Switch to feedback panel
        frame.revalidate(); // Refresh the frame
    }

    // Format appointment details into a string
    private String formatAppointmentDetails(Appointment appointment) {
        if (appointment == null) return "No appointment details available."; // Handle null appointment

        String customerName = appointment.getCustomer() != null ? appointment.getCustomer().getFirstName() + " " + appointment.getCustomer().getLastName() : "N/A"; // Get customer name
        String staffName = appointment.getStaff() != null ? appointment.getStaff().getName() : "N/A"; // Get staff name
        String services = appointment.getServices() != null ? appointment.getServices().stream() // Get services list
                .map(service -> service.getServiceName() + " ($" + service.getPrice() + ")") // Format each service
                .collect(Collectors.joining(", ")) : "N/A"; // Join services with comma
        String formattedDateTime = appointment.getAppointmentTime() != null ? appointment.getAppointmentTime().format(DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a")) : "N/A"; // Format date and time

        return String.format(
                "Appointment Details:\n\n" +
                        "Reference Number: %s\n" +
                        "Customer: %s\n" +
                        "Staff: %s\n" +
                        "Services: %s\n" +
                        "Total Duration: %d minutes\n" +
                        "Total Amount Payable: $%.2f\n" +
                        "Date & Time: %s\n",
                appointment.getReferenceNumber(),
                customerName,
                staffName,
                services,
                appointment.getTotalDuration(),
                appointment.getTotalCost(),
                formattedDateTime
        ); // Format appointment details into a string
    }
}

