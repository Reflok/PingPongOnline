package org.suai.pposerver;

import java.io.IOException;
import java.util.logging.*;

public class PPOServer {
    private static Logger logger = Logger.getLogger("");
    private static final boolean APPEND = false;

    public static void main (String[] args) {

        Logger.getLogger("").setLevel(Level.FINEST);

        try {
            logger.setUseParentHandlers(false);

            for (Handler h : logger.getHandlers()) {
                logger.removeHandler(h);
            }

            FileHandler fl1 = new FileHandler("exceptionLogs.log", APPEND);
            logger.addHandler(fl1);
            fl1.setFormatter(new SimpleFormatter());
            fl1.setFilter(logRecord -> logRecord.getLevel() == Level.SEVERE);

            FileHandler fl2 = new FileHandler("netLogs.log", APPEND);
            logger.addHandler(fl2);
            fl2.setFormatter(new SimpleFormatter());
            fl2.setFilter(logRecord -> logRecord.getLevel() == Level.INFO);

            ConsoleHandler ch = new ConsoleHandler();
            logger.addHandler(ch);
            ch.setFormatter(new SimpleFormatter());
            ch.setFilter(logRecord -> logRecord.getLevel() == Level.FINEST);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Can't set up logger", e);
            return;
        }

        InputHandler inputHandler = new InputHandler();
        new Thread(inputHandler).start();

        SocketWrapper listener = new SocketWrapper(5000, inputHandler);
        new Thread(listener).start();
    }
}
