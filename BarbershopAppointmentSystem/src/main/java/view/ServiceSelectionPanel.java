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
import model.Customer;
import model.Service;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ServiceSelectionPanel extends JPanel {
    private final Map<String, Service> servicesMap = new HashMap<>(); // map for services
    private final Map<String, JCheckBox> serviceCheckboxes = new HashMap<>(); // checkboxes for services

    public ServiceSelectionPanel(JFrame frame, AppointmentManager manager, Customer customer) {
        setLayout(new BorderLayout(10, 10)); // set layout for the panel
        setBackground(new Color(240, 248, 255)); // background color for the panel

        // title label
        JLabel titleLabel = new JLabel("Select Services", JLabel.CENTER); // title label
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24)); // font style for title
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0)); // border around title
        add(titleLabel, BorderLayout.NORTH); // add title to the top

        // services panel
        JPanel servicesPanel = createServicesPanel(frame); // create services panel
        add(new JScrollPane(servicesPanel), BorderLayout.CENTER);  // add scroll in case there are many services

        // button panel
        JPanel buttonPanel = createButtonPanel(frame, manager, customer); // create button panel
        add(buttonPanel, BorderLayout.SOUTH); // add button panel to the bottom
    }

    // method to create the services panel with checkboxes
    private JPanel createServicesPanel(JFrame frame) {
        JPanel servicesPanel = new JPanel(new GridLayout(0, 2, 10, 10)); // grid layout for services
        servicesPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // border around services panel

        String[] serviceNames = {"Haircut", "Shave", "Manicure", "Pedicure", "Hair Coloring", "Hairstyling"}; // available services

        // create checkboxes for each service
        Stream.of(serviceNames).forEach(serviceName -> {
            Service service = Service.getServiceByName(serviceName); // get service by name
            if (service != null) {
                JCheckBox checkBox = createServiceCheckBox(service); // create checkbox for service
                serviceCheckboxes.put(serviceName, checkBox); // store checkbox in map
                servicesMap.put(serviceName, service); // store service in map
                servicesPanel.add(checkBox); // add checkbox to services panel
            } else {
                showServiceLoadError(frame, serviceName); // show error if service not found
            }
        });
        return servicesPanel; // return the services panel
    }

    // method to create a styled checkbox for each service
    private JCheckBox createServiceCheckBox(Service service) {
        String label = String.format("%s - $%.2f - %d mins", service.getServiceName(), service.getPrice(), service.getDuration()); // label format
        JCheckBox checkBox = new JCheckBox(label); // create checkbox with label
        checkBox.setFont(new Font("Arial", Font.PLAIN, 16)); // font style for checkbox
        checkBox.setBackground(new Color(240, 248, 255)); // background color for checkbox
        return checkBox; // return created checkbox
    }

    // method to create the button panel
    private JPanel createButtonPanel(JFrame frame, AppointmentManager manager, Customer customer) {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10)); // flow layout for buttons
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0)); // border around button panel

        JButton nextButton = new JButton("Next"); // create next button
        nextButton.setFont(new Font("Arial", Font.BOLD, 16)); // font style for button
        nextButton.setBackground(new Color(0, 153, 76)); // background color for button
        nextButton.setForeground(Color.WHITE); // text color for button
        nextButton.setOpaque(true); // make button opaque
        nextButton.setBorderPainted(false); // remove border from button

        // add action listener for the "Next" button
        nextButton.addActionListener((ActionEvent e) -> {
            List<Service> selectedServices = serviceCheckboxes.entrySet().stream() // get selected services
                    .filter(entry -> entry.getValue().isSelected()) // filter selected checkboxes
                    .map(entry -> servicesMap.get(entry.getKey())) // get service from map
                    .collect(Collectors.toList()); // collect selected services into a list

            if (selectedServices.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please select at least one service."); // show warning if none selected
            } else {
                frame.setContentPane(new StaffSelectionPanel(frame, manager, customer, selectedServices)); // switch to next panel
                frame.revalidate(); // revalidate frame to update
            }
        });

        buttonPanel.add(nextButton); // add button to button panel
        return buttonPanel; // return button panel
    }

    // method to show a service loading error message
    private void showServiceLoadError(JFrame frame, String serviceName) {
        JOptionPane.showMessageDialog(frame, "Service " + serviceName + " not found in the database.", "Service Load Error", JOptionPane.WARNING_MESSAGE); // show error message
    }
}
