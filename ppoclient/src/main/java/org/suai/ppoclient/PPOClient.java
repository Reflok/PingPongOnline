package org.suai.ppoclient;

import java.io.IOException;
import java.net.*;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class PPOClient implements Runnable {
    private static Logger logger = Logger.getLogger(PPOClient.class.getName());
    private static FileHandler fl;

    private DatagramSocket socket;
    private Integer port;


    public void run() {

    }

    public static void main(String[] args) {

        try {
            logger.setUseParentHandlers(false);
            fl = new FileHandler("clientLogger.log");
            logger.addHandler(fl);
            fl.setFormatter(new SimpleFormatter());
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Can't set up logger", e);
            return;
        }
    }
}
