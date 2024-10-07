/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view;

/**
 *
 * @author ash
 */
import mainapp.MainApp;
import controller.AppointmentManager;
import controller.SlideshowBackgroundPanel;
import model.Address;
import model.Customer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class PersonalDetailsPanel extends JPanel {
    private JTextField firstNameField, lastNameField, dobField, contactNumberField, streetField, cityField, stateField, zipCodeField;
    private JComboBox<String> genderComboBox;

    public PersonalDetailsPanel(JFrame frame, AppointmentManager manager) {
        frame.setSize(800, 600); // set frame size
        setLayout(new BorderLayout(10, 10)); // layout for the panel

        // create the slideshow background panel
        SlideshowBackgroundPanel backgroundPanel = new SlideshowBackgroundPanel("", 6, 3000); // images are in the root directory
        add(backgroundPanel, BorderLayout.CENTER);

        // create form and button panels with transparency using a reusable method
        JPanel formPanel = backgroundPanel.createTransparentPanel(new GridBagLayout(), 0.7f, new Color(240, 248, 255, 150));
        JPanel buttonPanel = backgroundPanel.createTransparentPanel(new FlowLayout(FlowLayout.CENTER, 20, 10), 0.7f, new Color(240, 248, 255, 150));

        // setup GridBagLayout for form panel
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // spacing for components
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // add form fields to formPanel with alignment
        addLabelAndField(formPanel, "first name:", firstNameField = new JTextField(20), gbc, 0);
        addLabelAndField(formPanel, "last name:", lastNameField = new JTextField(20), gbc, 1);
        addLabelAndField(formPanel, "date of birth (dd/MM/yyyy):", dobField = new JTextField(20), gbc, 2);
        addLabelAndField(formPanel, "contact number:", contactNumberField = new JTextField(20), gbc, 3);
        addLabelAndField(formPanel, "street:", streetField = new JTextField(20), gbc, 4);
        addLabelAndField(formPanel, "city:", cityField = new JTextField(20), gbc, 5);
        addLabelAndField(formPanel, "state:", stateField = new JTextField(20), gbc, 6);
        addLabelAndField(formPanel, "zip code:", zipCodeField = new JTextField(20), gbc, 7);

        // add gender combo box
        genderComboBox = new JComboBox<>(new String[]{"male", "female"});
        addLabelAndField(formPanel, "gender:", genderComboBox, gbc, 8);

        // populate button panel with buttons
        buttonPanel.add(createButton("next", e -> handleNextButtonClick(frame, manager)));
        buttonPanel.add(createButton("go back", e -> goBack(frame, manager)));
        buttonPanel.add(createButton("exit", e -> exitApplication(frame)));

        // add the form and button panels on top of the background
        backgroundPanel.add(formPanel, BorderLayout.CENTER);
        backgroundPanel.add(buttonPanel, BorderLayout.SOUTH);

        firstNameField.requestFocusInWindow(); // focus on first name field
    }

    private void addLabelAndField(JPanel panel, String labelText, JComponent field, GridBagConstraints gbc, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.1;
        panel.add(new JLabel(labelText), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.9;
        panel.add(field, gbc);
    }

    private JButton createButton(String text, ActionListener actionListener) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 16)); // button font style
        button.setBackground(new Color(0, 102, 204)); // button background color
        button.setForeground(Color.WHITE); // button text color
        button.setOpaque(true); // make button opaque
        button.addActionListener(actionListener); // add action listener
        return button;
    }

    private void handleNextButtonClick(JFrame frame, AppointmentManager manager) {
        // get input values from fields
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String dob = dobField.getText().trim();
        String contactNumber = contactNumberField.getText().trim();
        String street = streetField.getText().trim();
        String city = cityField.getText().trim();
        String state = stateField.getText().trim();
        String zipCode = zipCodeField.getText().trim();
        String gender = (String) genderComboBox.getSelectedItem();

        // validate inputs before proceeding
        if (validateInputs(frame, firstName, lastName, dob, contactNumber, street, city, state, zipCode, gender)) {
            try {
                // create address and customer objects
                Address address = new Address(street, city, state, zipCode);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                LocalDate birthDate = LocalDate.parse(dob, formatter);
                Customer customer = new Customer(firstName, lastName, birthDate, contactNumber, gender, address);
                manager.addCustomer(customer); // add customer to manager

                // switch to the service selection panel
                frame.setContentPane(new ServiceSelectionPanel(frame, manager, customer));
                frame.revalidate();
                MainApp.updateMenuItems(true, false, true); // update menu items

            } catch (Exception ex) {
                showError(frame, "an unexpected error occurred: " + ex.getMessage());
            }
        }
    }

    private void goBack(JFrame frame, AppointmentManager manager) {
        // switch back to the main panel
        frame.setContentPane(new MainPanel(frame, manager));
        frame.revalidate();
        MainApp.updateMenuItems(false, false, false); // update menu items
    }

    private void exitApplication(JFrame frame) {
        // confirm exit from application
        int confirm = JOptionPane.showConfirmDialog(frame, "are you sure you want to exit?", "exit confirmation", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            System.exit(0); // exit application
        }
    }

    private boolean validateInputs(JFrame frame, String firstName, String lastName, String dob, String contactNumber,
                                   String street, String city, String state, String zipCode, String gender) {
        // check if any fields are empty
        if (isAnyFieldEmpty(firstName, lastName, dob, contactNumber, street, city, state, zipCode, gender)) {
            showError(frame, "all fields must be filled out.");
            return false;
        }

        // validate name fields
        if (!firstName.matches("[a-zA-Z]+") || !lastName.matches("[a-zA-Z]+")) {
            showError(frame, "name fields can only contain letters.");
            return false;
        }

        // validate date of birth
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate birthDate = LocalDate.parse(dob, formatter);
            if (LocalDate.now().minusYears(16).isBefore(birthDate)) {
                showError(frame, "customer must be at least 16 years old.");
                return false;
            }
            if (LocalDate.now().minusYears(120).isAfter(birthDate)) {
                showError(frame, "age cannot exceed 120 years.");
                return false;
            }
        } catch (DateTimeParseException ex) {
            showError(frame, "invalid date format. please use dd/MM/yyyy.");
            return false;
        }

        // validate contact number
        if (!contactNumber.matches("\\d{10,15}")) {
            showError(frame, "contact number must be between 10 to 15 digits.");
            return false;
        }
        
        // validate street address
        if (!street.matches(".*\\d.*") || !street.matches(".*[a-zA-Z].*")) {
            showError(frame, "street address must contain both letters and numbers.");
            return false;
        }

        // validate city and state
        if (!city.matches("[a-zA-Z ]+") || !state.matches("[a-zA-Z ]+")) {
            showError(frame, "city and state fields must only contain letters.");
            return false;
        }

        // validate zip code
        if (!zipCode.matches("\\d{4,10}")) {
            showError(frame, "zip code must be between 4 to 10 digits.");
            return false;
        }

        return true; // all validations passed
    }

    private boolean isAnyFieldEmpty(String... fields) {
        for (String field : fields) {
            if (field.isEmpty()) return true; // return true if any field is empty
        }
        return false; // all fields are filled
    }

    private void showError(JFrame frame, String message) {
        JOptionPane.showMessageDialog(frame, message, "input error", JOptionPane.WARNING_MESSAGE); // show error message
    }
}

