package com.mf.mp63.notifications;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

@Component
@Slf4j
public class Notifications {

    public void notifyMessage(String message) {
        try {
            if (SystemTray.isSupported()) {
                displayTray(message);
            } else {
                log.error(message);
            }

        } catch (AWTException e) {
            throw new RuntimeException(e);
        }
    }
    public void displayTray(String message) throws AWTException {
        //Obtain only one instance of the SystemTray object
        SystemTray tray = SystemTray.getSystemTray();
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("static").getFile());
        //If the icon is a file
        //Image image = Toolkit.getDefaultToolkit().getImage(file.getPath()+"\\"+ "usb.JPG");
        //Alternative (if the icon is on the classpath):
        //Image image = Toolkit.getDefaultToolkit().createImage(getClass().getResource("static/usb.jpg"));
        InputStream inputStream= getClass().getResourceAsStream("/static/usb.jpg");
        BufferedImage image = null;
        try {
            image = ImageIO.read(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //Get the URL with method class.getResource("/path/to/image.png")
        //URL url = getClass().getResource("usb.jpg");

        //Use it to get the image
        //Image image = Toolkit.getDefaultToolkit().getImage(url);

        final PopupMenu popup = new PopupMenu();
        MenuItem defaultItem = new MenuItem("Default Action");
        popup.add(defaultItem);

        TrayIcon trayIcon = new TrayIcon(image, "Tray Demo");
        //Let the system resize the image if needed
        trayIcon.setImageAutoSize(true);
        //Set tooltip text for the tray icon
        trayIcon.setToolTip("System tray icon demo");
        tray.add(trayIcon);

        trayIcon.displayMessage(message, "notification demo", TrayIcon.MessageType.WARNING);
        //trayIcon.set
    }
}
