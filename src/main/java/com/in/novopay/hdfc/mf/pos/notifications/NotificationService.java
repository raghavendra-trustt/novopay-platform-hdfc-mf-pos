package com.in.novopay.hdfc.mf.pos.notifications;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

@Component
@Slf4j
public class NotificationService {

    // Reference to the currently active notification
    private JDialog currentNotification;

    public void showCustomNotification(String imagePath, String message) {
        //log.info("showCustomNotification : {}", currentNotification);
        // Close the existing notification if it exists
        if (currentNotification != null && currentNotification.isVisible()) {
            currentNotification.dispose();
        }

        // Create the notification window
        JDialog dialog = new JDialog();
        dialog.setUndecorated(true); // Remove title bar
        dialog.setAlwaysOnTop(true); // Ensure the notification stays on top
        dialog.setLayout(null); // Use absolute positioning for precise control
        dialog.setAutoRequestFocus(true); // Ensure the notification gets focus
        dialog.requestFocus(); // Request focus
        dialog.setFocusableWindowState(true); // Allow the notification to be focused
        dialog.getContentPane().setBackground(Color.WHITE); // Set background color
        dialog.getRootPane().setBorder(new LineBorder(Color.LIGHT_GRAY, 1)); // Add a gray border
        dialog.setModalityType(Dialog.ModalityType.MODELESS); // Allow interaction with other windows
        //dialog.setOpacity(0.9f); // Set transparency level
        //dialog.pack();




        // Add image on the left
        JLabel imageLabel = new JLabel();
        int imageHeight = 80; // Fixed image height
        if(imagePath==null || imagePath.isEmpty()){
            imagePath="bank.png";
        }
        /*if (imagePath != null && !imagePath.isEmpty()) {

        } else {
            imageLabel.setText("No Image"); // Fallback text
        }*/
        InputStream inputStream= getClass().getResourceAsStream("/static/"+imagePath);
        BufferedImage image = null;
        try {
            image = ImageIO.read(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ImageIcon icon = new ImageIcon(image);
        Image img = icon.getImage().getScaledInstance(75, 75, Image.SCALE_SMOOTH);
        imageLabel.setIcon(new ImageIcon(img));
        imageLabel.setBounds(10, 10, 80, imageHeight); // Explicit position and size
        dialog.add(imageLabel);

        //log.info("message : "+message);
        // Add message on the right
        JLabel messageLabel = new JLabel("<html>" + message.replace("\n", "<br>") + "</html>"); // Allow multi-line text
        messageLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        messageLabel.setHorizontalAlignment(SwingConstants.LEFT);
        messageLabel.setVerticalAlignment(SwingConstants.TOP); // Default to top alignment
        dialog.add(messageLabel);

        // Calculate dynamic height based on the larger of image height and message height
        int lineHeight = messageLabel.getFontMetrics(messageLabel.getFont()).getHeight();
        int estimatedLines = (int) Math.ceil((double) message.length() / 24); // Approximate characters per line
        int messageHeight = lineHeight * estimatedLines;
        int dialogHeight = Math.max(imageHeight + 20, messageHeight + 40); // Add padding

        // Adjust bounds to fit the final calculated height
        dialog.setSize(300, dialogHeight);

        // Set rounded corners using RoundRectangle2D
        int arcWidth = 25;  // Width of the arc at the corners
        int arcHeight = 25; // Height of the arc at the corners
        dialog.setShape(new RoundRectangle2D.Double(0, 0, dialog.getWidth(), dialog.getHeight(), arcWidth, arcHeight));

        // Center the message vertically
        int totalHeight = dialogHeight - 20; // Subtract padding
        int messageY = (totalHeight - Math.min(messageHeight, totalHeight)) / 2 + 10; // Center and add top padding
        messageLabel.setBounds(100, messageY, 180, Math.min(messageHeight, totalHeight)); // Set dynamic height

        // Add close button
        JButton closeButton = new JButton("X");
        closeButton.setFont(new Font("Microsoft Sans Serif", Font.BOLD, 12));
        closeButton.setForeground(Color.LIGHT_GRAY);
        closeButton.setBorder(BorderFactory.createEmptyBorder());
        closeButton.setContentAreaFilled(false);
        closeButton.setFocusPainted(false);
        closeButton.setBounds(270, 5, 20, 20); // Top-right position
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose(); // Close the notification
            }
        });
        dialog.add(closeButton);
        //log.info("after close button");
        // Determine position for the dialog (bottom-right corner)
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Insets screenInsets = Toolkit.getDefaultToolkit().getScreenInsets(GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration());

        int taskBarHeight = screenInsets.bottom; // Height of the taskbar
        int dialogWidth = dialog.getWidth();

        int x = screenSize.width - dialogWidth - 10; // Right margin
        int y = screenSize.height - dialogHeight - taskBarHeight - 10; // Bottom margin

        dialog.setLocation(x, y);
        // Add a WindowFocusListener to reasserts "always on top" behavior
        dialog.addWindowFocusListener(new WindowFocusListener() {
            @Override
            public void windowGainedFocus(WindowEvent e) {
                // this method is empty
            }

            @Override
            public void windowLostFocus(WindowEvent e) {
                dialog.setAlwaysOnTop(true); // Reasserts always-on-top
            }
        });

        //log.info("before visible");
        // Show the dialog
        dialog.setVisible(true);

        // Automatically close after 5 seconds
        Timer timer = new Timer(5000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });
        timer.setRepeats(false);
        timer.start();

        Timer alwaysOnTopTimer = new Timer(5000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.toFront(); // Bring the dialog to the front
                dialog.repaint(); // Ensure it's redrawn
            }
        });
        alwaysOnTopTimer.start();

        //log.info("last line");
        // Store the reference to the current notification
        currentNotification = dialog;
    }
}

