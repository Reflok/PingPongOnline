package org.suai.pposerver;


import java.io.IOException;
import java.util.logging.*;

public class PPOServer {
    private static Logger logger = Logger.getLogger("");

    public static void main (String[] args) {
        Logger.getLogger("").setLevel(Level.FINEST);

        try {
            logger.setUseParentHandlers(false);

            FileHandler fl1 = new FileHandler("exceptionLogs.log", true);
            logger.addHandler(fl1);
            fl1.setFormatter(new SimpleFormatter());
            fl1.setFilter(new Filter() {
                public boolean isLoggable(LogRecord logRecord) { return logRecord.getLevel() == Level.SEVERE; }});

            FileHandler fl2 = new FileHandler("netLogs.log", true);
            logger.addHandler(fl2);
            fl2.setFormatter(new SimpleFormatter());
            fl2.setFilter(new Filter() {
                public boolean isLoggable(LogRecord logRecord) { return logRecord.getLevel() == Level.INFO; }});

        } catch (IOException e) {
            logger.log(Level.SEVERE, "Can't set up logger", e);
            return;
        }

        try {
            throw new Exception("test");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "TEST", e);
        }

        logger.info("LOL");
        logger.log(Level.INFO, "Message");

        InputHandler inputHandler = new InputHandler();

        PacketListener listener = new PacketListener(5555, inputHandler);
        new Thread(listener).run();


    }
}
