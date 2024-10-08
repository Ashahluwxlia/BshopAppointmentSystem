/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view;

/**
 *
 * @author anacarolina
 */


import controller.SlideshowBackgroundPanel;
import model.Feedback;
import controller.AppointmentManager;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class FeedbackPanel extends JPanel {

    private final AppointmentManager manager;  // keeping a reference to the AppointmentManager

    public FeedbackPanel(JFrame frame, String bookingReference, String customerName, AppointmentManager manager) {
        this.manager = manager;  // storing the reference to AppointmentManager
        frame.setSize(800, 600);

        // create the slideshow background panel
        SlideshowBackgroundPanel backgroundPanel = new SlideshowBackgroundPanel("", 6, 3000); // images are in the root directory (e.g., 1.jpeg)
        setLayout(new BorderLayout(10, 10));
        add(backgroundPanel, BorderLayout.CENTER);  // adding background panel to main panel

        // create the main container for feedback form and buttons
        JPanel mainContainer = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 10, 10, 10);

        mainContainer.setOpaque(false); // transparent background for the main container

        // create content panels with transparency using the reusable method from SlideshowBackgroundPanel
        JPanel feedbackContentPanel = backgroundPanel.createTransparentPanel(new BorderLayout(10, 10), 0.7f, new Color(255, 255, 255, 150));
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10)); // button panel is not transparent

        // populate feedback content panel
        JTextArea feedbackArea = createTextArea(); // defining the feedback area
        JScrollPane scrollPane = new JScrollPane(feedbackArea);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false); // making JScrollPane and its viewport transparent
        feedbackContentPanel.add(scrollPane, BorderLayout.CENTER); // adding feedback area to content panel

        // populate button panel (no transparency for buttons)
        buttonPanel.add(createButton("Submit Feedback", e -> submitFeedback(frame, feedbackArea, bookingReference, customerName)));
        buttonPanel.add(createButton("Exit", e -> System.exit(0), new Color(255, 69, 58)));

        // add feedback content and button panels to the main container
        gbc.gridy = 0;
        mainContainer.add(feedbackContentPanel, gbc);
        gbc.gridy = 1;
        mainContainer.add(buttonPanel, gbc);

        // center the mainContainer within the background
        backgroundPanel.setLayout(new GridBagLayout());
        backgroundPanel.add(mainContainer);
    }

    private JTextArea createTextArea() {
        JTextArea textArea = new JTextArea(10, 30);
        textArea.setFont(new Font("Arial", Font.PLAIN, 14));
        textArea.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        textArea.setOpaque(false); // ensure transparency works for the text area
        textArea.setBackground(new Color(255, 255, 255, 150)); // semi-transparent background for the text area
        return textArea;
    }

    private JButton createButton(String text, ActionListener actionListener) {
        return createButton(text, actionListener, new Color(0, 102, 204));
    }

    private JButton createButton(String text, ActionListener actionListener, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setOpaque(true);
        button.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        button.addActionListener(actionListener);
        return button;
    }

    private void submitFeedback(JFrame frame, JTextArea feedbackArea, String bookingReference, String customerName) {
        String feedbackText = feedbackArea.getText().trim();
        if (feedbackText.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "feedback cannot be empty.");
            return;
        }

        Feedback feedback = new Feedback(bookingReference, customerName, feedbackText);
        feedback.saveToDatabase();
        JOptionPane.showMessageDialog(frame, "thanks for your feedback!");

        // switch back to the MainPanel with the correct AppointmentManager instance
        frame.setContentPane(new MainPanel(frame, manager));  // passing the AppointmentManager instance
        frame.revalidate();
    }
}
