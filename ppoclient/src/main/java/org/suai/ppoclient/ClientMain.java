package org.suai.ppoclient;

import javax.swing.*;
import java.io.IOException;
import java.net.*;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class ClientMain {
    private static Logger logger = Logger.getLogger("");
    private static final boolean APPEND = false;

    public static void main(String[] args) {
        try {
            logger.setUseParentHandlers(false);

            FileHandler fl1 = new FileHandler("clientExceptions.log");
            logger.addHandler(fl1);
            fl1.setFormatter(new SimpleFormatter());
            fl1.setFilter(logRecord -> logRecord.getLevel() == Level.SEVERE);

        } catch (IOException e) {
            logger.log(Level.SEVERE, "Can't set up logger", e);
            return;
        }

        DatagramSocket socket = null;
        DatagramPacket packet;

        try {
            socket = new DatagramSocket();
            Connector connector = new Connector(InetAddress.getByName("localhost"), 5000);

            JFrame frame = new JFrame("Enter name");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(200, 50);
            frame.setLocationRelativeTo(null);
            JTextField textField = new JTextField();
            JButton button = new JButton("Submit");

        } catch (IOException e) {
            logger.log(Level.SEVERE, "Can't receive packet", e);
        } finally {
            if (socket != null) {
                socket.close();
            }
        }
    }
}
