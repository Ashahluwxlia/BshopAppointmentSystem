/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mainapp;

/**
 *
 * @author ash
 */
import view.MainPanel;
//import view.ViewBookingPanel;
import controller.AppointmentManager;
import database.DatabaseInitializer;
import model.Service;

import javax.swing.*;
import view.PersonalDetailsPanel;

public class MainApp {
    private static JMenu menu;
    private static JMenuItem newBookingItem;
    private static JMenuItem viewBookingItem;
    private static JMenuItem goBackItem;
    private static JMenuItem exitItem;

    public static void main(String[] args) {
        // Initialize the database tables
        DatabaseInitializer.initializeDatabase();
        
        // Initialize default services if needed
        Service.initializeDefaultServices();

        // Set up the main application window
        JFrame frame = new JFrame("Barbershop Appointment System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600); // Set window size

        AppointmentManager manager = new AppointmentManager();
        MainPanel mainPanel = new MainPanel(frame, manager);
        frame.setContentPane(mainPanel); // Set main panel as content

        // Set up the menu bar
        JMenuBar menuBar = new JMenuBar();
        menu = new JMenu("Options");

        newBookingItem = new JMenuItem("Make a New Booking");
        viewBookingItem = new JMenuItem("View Booking");
        goBackItem = new JMenuItem("Back to Main Menu");
        exitItem = new JMenuItem("Exit");

        // Add menu items to menu
        menu.add(newBookingItem);
        menu.add(viewBookingItem);
        menu.add(goBackItem);
        menu.add(exitItem);
        menuBar.add(menu);
        frame.setJMenuBar(menuBar); // Attach menu bar to frame

        // Set up action listeners for menu items
        newBookingItem.addActionListener(e -> {
            frame.setContentPane(new PersonalDetailsPanel(frame, manager)); // Load booking panel
            frame.revalidate(); // Refresh UI
            updateMenuItems(true, false, true); // Show "Go Back" button and hide others
        });

        viewBookingItem.addActionListener(e -> {
            frame.setContentPane(new ViewBookingPanel(frame, manager)); // Load view booking panel
            frame.revalidate(); // Refresh UI
            updateMenuItems(false, true, true); // Show "Go Back" button and hide others
        });

        goBackItem.addActionListener(e -> {
            frame.setContentPane(mainPanel); // Go back to main panel
            frame.revalidate(); // Refresh UI
            updateMenuItems(false, false, false); // Hide "Go Back" button and show others
        });

        exitItem.addActionListener(e -> System.exit(0)); // Exit the application

        // Set the initial state of the menu items
        updateMenuItems(false, false, false); // Hide "Go Back" button initially
        frame.setVisible(true); // Show the main window
    }

    // Method to control visibility of menu items
    public static void updateMenuItems(boolean newBookingHidden, boolean viewBookingHidden, boolean goBackShown) {
        newBookingItem.setVisible(!newBookingHidden); // Show/hide new booking item
        viewBookingItem.setVisible(!viewBookingHidden); // Show/hide view booking item
        goBackItem.setVisible(goBackShown); // Show/hide go back item
    }
}
