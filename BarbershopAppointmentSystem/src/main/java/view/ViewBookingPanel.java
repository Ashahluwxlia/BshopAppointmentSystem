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
import mainapp.MainApp;
import model.Appointment;

import javax.swing.*;
import java.awt.*;
import java.util.stream.Collectors;

public class ViewBookingPanel extends JPanel {
    private JTextField referenceNumberField;  // input field for reference number
    private JTextArea resultArea;  // area to display appointment results
    private JButton searchButton;  // button to search for an appointment
    private JButton goBackButton;  // button to go back to the main panel
    private JButton exitButton;  // button to exit the application

    public ViewBookingPanel(JFrame frame, AppointmentManager manager) {
        // reload appointments from the database
        manager.loadAppointmentsFromDatabase();

        // create the slideshow background panel
        SlideshowBackgroundPanel backgroundPanel = new SlideshowBackgroundPanel("", 6, 3000); // adjust image path as needed

        setLayout(new BorderLayout(10, 10));
        add(backgroundPanel, BorderLayout.CENTER);  // add the slideshow background

        // create the main container for content
        JPanel mainContainer = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 10, 10, 10);
        mainContainer.setOpaque(false);  // make the container transparent

        // create content panels with transparency
        JPanel inputPanel = backgroundPanel.createTransparentPanel(new GridBagLayout(), 0.7f, new Color(255, 255, 255, 150));  // input panel
        JPanel resultPanel = backgroundPanel.createTransparentPanel(new BorderLayout(10, 10), 0.7f, new Color(255, 255, 255, 150));  // results panel
        JPanel buttonPanel = backgroundPanel.createTransparentPanel(new FlowLayout(FlowLayout.CENTER, 20, 10), 0.7f, new Color(255, 255, 255, 150));  // button panel

        // header label
        JLabel headerLabel = new JLabel("View Booking", JLabel.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        headerLabel.setForeground(new Color(0, 102, 204)); // set header text color to dark blue
        gbc.gridwidth = 2;  // span across 2 columns
        inputPanel.add(headerLabel, gbc);

        // reference number input
        gbc.gridwidth = 1;  // switch back to single column layout
        gbc.gridy = 1;
        gbc.gridx = 0;
        JLabel referenceNumberLabel = new JLabel("Reference Number:");  // label for reference number
        referenceNumberLabel.setFont(new Font("Arial", Font.BOLD, 16));
        inputPanel.add(referenceNumberLabel, gbc);

        gbc.gridx = 1;  // set position for the text field
        referenceNumberField = new JTextField(20);  // input field for the reference number
        inputPanel.add(referenceNumberField, gbc);

        // search button
        gbc.gridy = 2;  // move down to next row
        gbc.gridx = 1;  // set column for the button
        searchButton = new JButton("Search");
        searchButton.setFont(new Font("Arial", Font.BOLD, 16));
        searchButton.setBackground(new Color(0, 102, 204));  // set button background to dark blue
        searchButton.setForeground(Color.WHITE);
        searchButton.setOpaque(true);
        searchButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        inputPanel.add(searchButton, gbc);

        // result area (make it transparent)
        resultArea = new JTextArea(10, 30);  // area to display results
        resultArea.setEditable(false);  // make it non-editable
        resultArea.setFont(new Font("Arial", Font.PLAIN, 16));
        resultArea.setOpaque(false);  // make the text area transparent
        resultArea.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));  // add border for visibility

        JScrollPane scrollPane = new JScrollPane(resultArea);  // add scroll pane for results
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);  // make the scroll pane transparent
        resultPanel.add(scrollPane, BorderLayout.CENTER);  // add scroll pane to result panel

        // buttons (Go Back and Exit)
        goBackButton = new JButton("Go Back");
        goBackButton.setFont(new Font("Arial", Font.BOLD, 16));
        goBackButton.setBackground(new Color(0, 102, 204));  // set button background to dark blue
        goBackButton.setForeground(Color.WHITE);
        goBackButton.setOpaque(true);
        goBackButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        goBackButton.addActionListener(e -> {
            // switch back to main panel
            frame.setContentPane(new MainPanel(frame, manager));
            frame.revalidate();
            MainApp.updateMenuItems(false, false, false); // show all menu items
        });

        exitButton = new JButton("Exit");
        exitButton.setFont(new Font("Arial", Font.BOLD, 16));
        exitButton.setBackground(new Color(255, 69, 58)); // set exit button background to red
        exitButton.setForeground(Color.WHITE);
        exitButton.setOpaque(true);
        exitButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        exitButton.addActionListener(e -> System.exit(0));  // exit the application

        buttonPanel.add(goBackButton);  // add buttons to panel
        buttonPanel.add(exitButton);

        // add everything to the main container
        gbc.gridy = 0;  // position for input panel
        mainContainer.add(inputPanel, gbc);
        gbc.gridy = 1;  // position for results panel
        mainContainer.add(resultPanel, gbc);
        gbc.gridy = 2;  // position for button panel
        mainContainer.add(buttonPanel, gbc);

        // add mainContainer to the background panel
        backgroundPanel.setLayout(new GridBagLayout());
        backgroundPanel.add(mainContainer);

        // search button logic
        searchButton.addActionListener(e -> {
            String referenceNumber = referenceNumberField.getText().trim();  // get the reference number from input
            if (referenceNumber.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please enter a reference number.");  // show warning if empty
                return;
            }

            Appointment appointment = null;  // initialize appointment

            try {
                appointment = manager.getAppointmentByReference(referenceNumber);  // search for the appointment
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "An error occurred while searching for the appointment.");  // show error message
                ex.printStackTrace();
            }

            if (appointment == null) {
                JOptionPane.showMessageDialog(frame, "No appointment found with reference number: " + referenceNumber);  // no appointment found
                resultArea.setText("");  // clear result area
            } else {
                resultArea.setText(formatAppointmentDetails(appointment));  // display appointment details
            }
        });
    }

    // format appointment details for better readability and show total duration and amount
    private String formatAppointmentDetails(Appointment appointment) {
        if (appointment == null) {
            return "No appointment details available.";  // return message if appointment is null
        }

        String customerName = (appointment.getCustomer() != null) ?
            appointment.getCustomer().getFirstName() + " " + appointment.getCustomer().getLastName() : "N/A";  // get customer name

        String staffName = (appointment.getStaff() != null) ?
            appointment.getStaff().getName() : "N/A";  // get staff name

        String services = (appointment.getServices() != null && !appointment.getServices().isEmpty()) ?
            appointment.getServices().stream()
                .map(service -> service.getServiceName() + " ($" + service.getPrice() + ")")  // format services list
                .collect(Collectors.joining(", ")) : "N/A";  // join services with a comma

        int totalDuration = (appointment.getTotalDuration() > 0) ? appointment.getTotalDuration() : 0;  // get total duration

        double totalAmount = (appointment.getTotalCost() > 0) ? appointment.getTotalCost() : 0.0;  // get total cost

        String formattedDateTime = (appointment.getAppointmentTime() != null) ?
            appointment.getAppointmentTime().format(java.time.format.DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a")) : "N/A";  // format appointment date and time

        // return formatted appointment details
        return String.format(
            "Appointment Details:\n\n" +
            "Reference Number: %s\n" +
            "Customer: %s\n" +
            "Staff: %s\n" +
            "Services: %s\n" +
            "Total Duration: %d mins\n" +
            "Total Amount: $%.2f\n" +
            "Date & Time: %s\n",
            appointment.getReferenceNumber(),
            customerName,
            staffName,
            services,
            totalDuration,
            totalAmount,
            formattedDateTime
        );
    }
}
