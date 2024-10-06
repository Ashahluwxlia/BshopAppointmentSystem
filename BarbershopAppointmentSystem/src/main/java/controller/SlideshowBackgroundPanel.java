/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

/**
 *
 * @author anacarolina
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class SlideshowBackgroundPanel extends JPanel {

    private List<ImageIcon> images;
    private int currentImageIndex = 0;
    private Timer timer;

    public SlideshowBackgroundPanel(String imagePathPrefix, int totalImages, int delay) {
        this.images = loadImages(imagePathPrefix, totalImages);

        // Create a timer to update the background image
        timer = new Timer(delay, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentImageIndex = (currentImageIndex + 1) % images.size();
                repaint();  // Repaint the panel with the new image
            }
        });
        timer.start();  // Start the slideshow timer
    }

    private List<ImageIcon> loadImages(String pathPrefix, int totalImages) {
        List<ImageIcon> loadedImages = new ArrayList<>();
        for (int i = 1; i <= totalImages; i++) {
            loadedImages.add(new ImageIcon(pathPrefix + i + ".jpeg"));  // Load the images
        }
        return loadedImages;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw the current slideshow image as the background
        if (!images.isEmpty()) {
            ImageIcon currentImage = images.get(currentImageIndex);
            g.drawImage(currentImage.getImage(), 0, 0, getWidth(), getHeight(), this);
        }

        // Apply the transparency effect to the child components
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f)); // 70% transparency

        // Draw the rest of the components with transparency
        super.paintChildren(g2d);
        g2d.dispose();
    }

    // Reusable method for creating transparent panels
    public JPanel createTransparentPanel(LayoutManager layout, float transparency, Color background) {
        JPanel transparentPanel = new JPanel(layout) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                ((Graphics2D) g).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, transparency));
            }
        };
        transparentPanel.setBackground(background);
        return transparentPanel;
    }

    public void stopSlideshow() {
        timer.stop();  // Stop the slideshow if needed
    }
}