/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view;

/**
 *
 * @author ash
 */
import controller.AppointmentManager;
import controller.DateLabelFormatter;
import controller.SlideshowBackgroundPanel;
import model.Customer;
import model.Service;
import model.Staff;
import model.Appointment;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Properties;

public class StaffSelectionPanel extends JPanel {
    private final JComboBox<Staff> staffComboBox = new JComboBox<>();
    private final JComboBox<LocalTime> timeSlotComboBox = new JComboBox<>();
    private final JDatePickerImpl datePicker;
    private final LocalDate minSelectableDate = LocalDate.now();

    public StaffSelectionPanel(JFrame frame, AppointmentManager manager, Customer customer, List<Service> selectedServices) {
        // create a background panel for slideshow effect
        SlideshowBackgroundPanel backgroundPanel = new SlideshowBackgroundPanel("", 6, 3000);
        setLayout(new BorderLayout());
        add(backgroundPanel, BorderLayout.CENTER);

        // make a transparent panel for the form inputs
        JPanel formPanel = backgroundPanel.createTransparentPanel(new GridBagLayout(), 0.7f, new Color(240, 248, 255, 150));

        // set constraints for layout
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.CENTER;

        // add labels and input fields to the form
        addLabelAndComponent(formPanel, "Select Staff:", staffComboBox, gbc, 0);
        addLabelAndComponent(formPanel, "Select Date:", datePicker = createDatePicker(frame, manager, selectedServices), gbc, 1);
        addLabelAndComponent(formPanel, "Select Time Slot:", timeSlotComboBox, gbc, 2);

        // create a panel for the action buttons
        JPanel buttonPanel = createButtonPanel(frame, manager, customer, selectedServices);

        // center the form and button panels in the background
        backgroundPanel.setLayout(new GridBagLayout());
        GridBagConstraints panelConstraints = new GridBagConstraints();
        panelConstraints.anchor = GridBagConstraints.CENTER;
        panelConstraints.insets = new Insets(20, 0, 20, 0);
        backgroundPanel.add(formPanel, panelConstraints);

        // add the button panel below the form
        panelConstraints.gridy = 1;
        backgroundPanel.add(buttonPanel, panelConstraints);

        updateStaffList(manager); // fill the staff list
        staffComboBox.addActionListener(e -> updateAvailableSlots(manager, selectedServices)); // update time slots when staff is selected
    }

    private void addLabelAndComponent(JPanel panel, String labelText, JComponent component, GridBagConstraints gbc, int gridY) {
        gbc.gridx = 0;
        gbc.gridy = gridY;
        panel.add(new JLabel(labelText), gbc); // add the label
        gbc.gridx = 1;
        panel.add(component, gbc); // add the input component
    }

    private JDatePickerImpl createDatePicker(JFrame frame, AppointmentManager manager, List<Service> selectedServices) {
        UtilDateModel model = new UtilDateModel();
        model.setValue(Date.from(minSelectableDate.atStartOfDay(ZoneId.systemDefault()).toInstant())); // set minimum date
        model.setSelected(true);

        JDatePanelImpl datePanel = new JDatePanelImpl(model, createDatePickerProperties());
        JDatePickerImpl datePicker = new JDatePickerImpl(datePanel, new DateLabelFormatter());

        datePicker.addActionListener(e -> validateAndSetDate(frame, manager, selectedServices, model)); // check the date selection
        return datePicker;
    }

    private Properties createDatePickerProperties() {
        Properties p = new Properties();
        p.put("text.today", "Today");
        p.put("text.month", "Month");
        p.put("text.year", "Year");
        return p;
    }

    private void validateAndSetDate(JFrame frame, AppointmentManager manager, List<Service> selectedServices, UtilDateModel model) {
        Date selectedDate = (Date) datePicker.getModel().getValue();
        if (selectedDate != null) {
            LocalDate localDate = convertToLocalDate(selectedDate);
            // make sure the selected date isn't before the minimum date
            if (localDate.isBefore(minSelectableDate)) {
                JOptionPane.showMessageDialog(frame, "Please select a date on or after " + minSelectableDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                model.setValue(Date.from(minSelectableDate.atStartOfDay(ZoneId.systemDefault()).toInstant())); // reset to the minimum date
            } else {
                updateAvailableSlots(manager, selectedServices); // update available time slots
            }
        }
    }

    private JPanel createButtonPanel(JFrame frame, AppointmentManager manager, Customer customer, List<Service> selectedServices) {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));

        JButton confirmButton = createButton("Confirm Booking", e -> confirmBooking(frame, manager, customer, selectedServices));
        JButton exitButton = createButton("Exit", e -> System.exit(0), new Color(255, 69, 58)); // make exit button stand out

        buttonPanel.add(confirmButton);
        buttonPanel.add(exitButton);
        return buttonPanel;
    }

    private JButton createButton(String text, ActionListener listener) {
        return createButton(text, listener, new Color(0, 153, 76)); // default button color
    }

    private JButton createButton(String text, ActionListener listener, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 16)); // set font style for button
        button.setBackground(bgColor); // set background color
        button.setForeground(Color.WHITE); // set text color
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.addActionListener(listener); // add the action listener
        return button;
    }

    private void confirmBooking(JFrame frame, AppointmentManager manager, Customer customer, List<Service> selectedServices) {
        Staff selectedStaff = (Staff) staffComboBox.getSelectedItem();
        LocalDate selectedDate = convertToLocalDate((Date) datePicker.getModel().getValue());
        LocalTime selectedTime = (LocalTime) timeSlotComboBox.getSelectedItem();

        // check if all selections are made
        if (selectedStaff == null || selectedDate == null || selectedTime == null) {
            JOptionPane.showMessageDialog(frame, "Please select a staff, date, and time slot.");
            return;
        }

        // see if the time slot is already booked
        if (manager.getBookedSlotsForStaff(selectedStaff, selectedDate).contains(selectedTime)) {
            JOptionPane.showMessageDialog(frame, "Time slot already booked for this staff! Please select another slot.");
        } else {
            LocalDateTime appointmentTime = LocalDateTime.of(selectedDate, selectedTime);
            String referenceNumber = "REF" + System.currentTimeMillis(); // generate a reference number
            Appointment appointment = new Appointment(customer, selectedStaff, selectedServices, appointmentTime, referenceNumber);
            
            // add the appointment and reload the data
            manager.addAppointment(appointment);
            manager.loadAppointmentsFromDatabase();  // refresh appointments from DB

            JOptionPane.showMessageDialog(frame, "Booking Confirmed! Reference Number: " + referenceNumber);
            frame.setContentPane(new BookingConfirmationPanel(frame, manager, appointment)); // show the confirmation panel
            frame.revalidate();
        }
    }

    private void updateStaffList(AppointmentManager manager) {
        staffComboBox.removeAllItems(); // clear the staff list
        manager.getStaffList().forEach(staffComboBox::addItem); // fill the combo box with staff
    }

    private void updateAvailableSlots(AppointmentManager manager, List<Service> selectedServices) {
        Staff selectedStaff = (Staff) staffComboBox.getSelectedItem();
        LocalDate selectedDate = convertToLocalDate((Date) datePicker.getModel().getValue());

        // update available time slots based on the selected staff and date
        if (selectedStaff != null && selectedDate != null) {
            List<LocalTime> availableSlots = manager.getAvailableSlots(selectedStaff, selectedDate, selectedServices.stream().mapToInt(Service::getDuration).sum());
            timeSlotComboBox.removeAllItems();
            availableSlots.forEach(timeSlotComboBox::addItem); // fill the time slots
        } else {
            timeSlotComboBox.removeAllItems(); // clear time slots if nothing is selected
        }
    }

    private LocalDate convertToLocalDate(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(); // convert Date to LocalDate
    }
}
