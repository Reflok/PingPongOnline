package org.suai.ppoclient;

import org.suai.ppoview.PPOMenuView;

import java.io.IOException;
import java.net.*;
import java.util.logging.*;

public class PPOClient {
    private static Logger logger = Logger.getLogger("");
    private static final boolean APPEND = false;

    public static void main (String[] args) {
        //logger set up

        try {
            logger.setUseParentHandlers(false);

            FileHandler fl1 = new FileHandler("clientExceptions.log", APPEND);
            logger.addHandler(fl1);
            fl1.setFormatter(new SimpleFormatter());
            fl1.setFilter(logRecord -> logRecord.getLevel() == Level.SEVERE);

        } catch (IOException e) {
            logger.log(Level.SEVERE, "Can't set up logger", e);
            return;
        }

        try {
            Connector connector = new Connector(InetAddress.getByName("localhost"), 5000);
            new Thread(new PPOMenuView(connector)).start();
        } catch (SocketException e) {
            logger.log(Level.SEVERE, "Can't open socket", e);
        } catch (UnknownHostException e) {
            logger.log(Level.SEVERE, "Can't resolve server address", e);
        }
    }
}
