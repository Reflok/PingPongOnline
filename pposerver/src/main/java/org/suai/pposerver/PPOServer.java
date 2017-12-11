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

        //LinkedBlockingQueue<DatagramPacket> q = new LinkedBlockingQueue<>();
        //ConsSTR c = new ConsSTR();
        /*ConsPACKET c = new ConsPACKET();
        new Thread(c).start();
        int i = 0;
        byte[] databuf = new byte[1024];
        try (DatagramSocket socket = new DatagramSocket(5555)){
            while (i < 10) {
                DatagramPacket packet = new DatagramPacket(databuf, databuf.length);
                i++;
                socket.receive(packet);
                String str = new String(packet.getData(), packet.getOffset(), packet.getLength());
                System.out.println("Adding" + str);
                //c.add(str);
                c.add(packet);
                c.show();
                socket.send(new DatagramPacket("OK".getBytes(), "OK".getBytes().length, packet.getAddress(),
                        packet.getPort()));

            }
            Thread.sleep(5000);
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        //new Thread(c).start();

        if (System.currentTimeMillis() > 100) {
            return;
        }*/


        InputHandler inputHandler = new InputHandler();
        new Thread(inputHandler).start();

        SocketWrapper listener = new SocketWrapper(5555, inputHandler);
        new Thread(listener).start();
        logger.log(Level.FINEST, "PPOServer is set and ready");

        /*PPOModel gameModel = new PPOModel(5, 10, 5);

        long timer = System.nanoTime();
        long wait;

        try (DatagramSocket socket = new DatagramSocket(5555)) {
            byte[] databuf = new byte[1024];

            DatagramPacket packet = new DatagramPacket(databuf, databuf.length);
            while (active) {

                if (System.currentTimeMillis() < 10) {
                    break;
                }
                socket.receive(packet);

                String str = new String(packet.getData(), packet.getOffset(), packet.getLength());

                if (str.equals("DOWN")) {
                    gameModel.getPlayer1().setDirection(PlayerModel.DOWN);
                } else if (str.equals("UP")) {
                    gameModel.getPlayer1().setDirection(PlayerModel.UP);
                } else if (str.equals("STOP")) {
                    gameModel.getPlayer1().setDirection(PlayerModel.STOP);
                }

                wait = (1000 / 60 - (System.nanoTime() - timer) / 1000000);

                if (wait <= 0) {
                    gameModel.update();
                    timer = System.nanoTime();
                    String message = 1 + ":" + Integer.toString(gameModel.getBall().getX()) + ":" +
                            Integer.toString(gameModel.getBall().getY()) + ":" +
                            Integer.toString(gameModel.getPlayer1().getX()) + ":" +
                            Integer.toString(gameModel.getPlayer1().getY()) + ":";

                    socket.send(new DatagramPacket(message.getBytes(), message.getBytes().length, packet.getAddress(),
                            packet.getPort()));
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }*/


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
