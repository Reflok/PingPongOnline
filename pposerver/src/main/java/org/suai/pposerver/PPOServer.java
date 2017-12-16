package org.suai.pposerver;



import ppomodel.PPOModel;
import ppomodel.PlayerModel;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.*;

public class PPOServer {
    private static Logger logger = Logger.getLogger("");
    private static final boolean APPEND = false;
    public static boolean active = true;

    public static void main (String[] args) {

        //logger set up
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
        logger.log(Level.FINEST, "PPOServer is set and ready");
    }

    private static class ConsSTR implements Runnable {
        LinkedBlockingQueue<String> q = new LinkedBlockingQueue<>();

        @Override
        public void run() {
            ArrayList<String> l = new ArrayList<>();
            String str;
            while (10 < System.currentTimeMillis()) {

                try {
                    //DatagramPacket packet = q.take();
                    str = q.take();
                        System.out.println("Logging " + str);
                        logger.log(Level.INFO, str);
                        Thread.sleep(500);
                        //logger.log(Level.INFO, "Message:" + q.take()
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        public void add(String p) throws InterruptedException {
            q.put(p);
        }

        public void show() {
            for (String p : q) {
                System.out.print(p + " ");

            }
            System.out.println();
        }
    }

    private static class ConsPACKET implements Runnable {
        LinkedBlockingQueue<DatagramPacket> q = new LinkedBlockingQueue<>();

        @Override
        public void run() {
            ArrayList<DatagramPacket> l = new ArrayList<>();
            String str;
            while (10 < System.currentTimeMillis()) {

                try {
                    DatagramPacket packet = q.take();
                    str = new String(packet.getData(), packet.getOffset(), packet.getLength());
                    System.out.println("Logging " + str);
                    logger.log(Level.INFO, str);
                    //Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        public void add(DatagramPacket p) throws InterruptedException {
            q.put(p);
        }

        public void show() {
            for (DatagramPacket packet : q) {
                System.out.print(new String(packet.getData(), packet.getOffset(), packet.getLength()) + " ");

            }
            System.out.println();
        }
    }
}
