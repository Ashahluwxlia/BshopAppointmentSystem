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
import controller.SlideshowBackgroundPanel;
import javax.swing.*;
import java.awt.*;
import mainapp.MainApp;

import controller.AppointmentManager;
import controller.SlideshowBackgroundPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class MainPanel extends JPanel {

    public MainPanel(JFrame frame, AppointmentManager manager) {
        frame.setSize(800, 600); // setting the frame size

        // set up the slideshow background
        SlideshowBackgroundPanel backgroundPanel = new SlideshowBackgroundPanel("", 6, 3000);  // 6 images, 3-second delay

        setLayout(new BorderLayout(20, 20)); // main layout
        backgroundPanel.setLayout(new BorderLayout(20, 20));  // layout for background
        add(backgroundPanel, BorderLayout.CENTER);  // add background to the frame

        // create content (label and buttons)
        JLabel welcomeLabel = createLabel("Welcome to The Ultimate Grooming Hub Booking System", 24, new Color(0, 102, 204)); // welcome label
        JPanel buttonPanel = createButtonPanel(frame, manager); // button panel

        // add content on top of the background
        backgroundPanel.add(welcomeLabel, BorderLayout.NORTH); // label at the top
        backgroundPanel.add(buttonPanel, BorderLayout.CENTER); // buttons in the center
    }

    private JLabel createLabel(String text, int fontSize, Color color) {
        JLabel label = new JLabel(text, JLabel.CENTER); // centered label
        label.setFont(new Font("Arial", Font.BOLD, fontSize)); // set font style
        label.setForeground(color); // set text color
        return label; // return label
    }

    private JPanel createButtonPanel(JFrame frame, AppointmentManager manager) {
        JPanel buttonPanel = new JPanel(new GridBagLayout());  // layout for buttons
        buttonPanel.setOpaque(false);  // keep background visible

        GridBagConstraints gbc = new GridBagConstraints(); // constraints for button layout
        gbc.gridx = 0; // column index
        gbc.gridy = GridBagConstraints.RELATIVE;  // stack buttons vertically
        gbc.insets = new Insets(10, 0, 10, 0);  // spacing between buttons

        // create buttons
        JButton newBookingButton = createStyledButton("New Booking", new Color(34, 139, 34));  // green for new booking
        newBookingButton.addActionListener(e -> switchPanel(frame, new PersonalDetailsPanel(frame, manager), true, false));

        JButton viewBookingButton = createStyledButton("View Booking", new Color(70, 130, 180));  // SteelBlue for viewing booking
        viewBookingButton.addActionListener(e -> switchPanel(frame, new ViewBookingPanel(frame, manager), false, true));

        JButton exitButton = createStyledButton("Exit", new Color(220, 20, 60));  // crimson for exit
        exitButton.addActionListener(e -> System.exit(0)); // exit app

        // add buttons to the panel
        buttonPanel.add(newBookingButton, gbc); // add new booking button
        buttonPanel.add(viewBookingButton, gbc); // add view booking button
        buttonPanel.add(exitButton, gbc); // add exit button

        return buttonPanel; // return button panel
    }

    private JButton createStyledButton(String text, Color backgroundColor) {
        JButton button = new JButton(text); // create button
        button.setFont(new Font("Arial", Font.BOLD, 14));  // set font size
        button.setForeground(Color.WHITE); // text color
        button.setBackground(backgroundColor); // background color
        button.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2)); // border
        button.setPreferredSize(new Dimension(180, 40));  // button size
        button.setOpaque(true); // make button opaque
        return button; // return styled button
    }

    private void switchPanel(JFrame frame, JPanel newPanel, boolean showNewBooking, boolean showViewBooking) {
        frame.setContentPane(newPanel); // switch to new panel
        frame.revalidate(); // refresh frame
        MainApp.updateMenuItems(showNewBooking, showViewBooking, true); // update menu
    }
}

