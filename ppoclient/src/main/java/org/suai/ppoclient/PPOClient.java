package org.suai.ppoclient;

import ppomodel.PPOModel;
import ppoview.PPOView;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.net.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.*;

public class PPOClient implements Runnable, KeyListener {
    private static Logger logger = Logger.getLogger("");
    private static final boolean APPEND = false;

    private static PPOModel game;
    private static PPOView view;
    private static Connector connector;
    private static String state = "STOP";


    public static void main (String[] args) {
        //logger set up
        //Logger.getLogger("").setLevel(Level.FINEST);


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
        DatagramSocket socket = null;
        DatagramPacket packet = null;

        /*try  {
            socket = new DatagramSocket();
            Connector connector = new Connector(socket, InetAddress.getByName("localhost"), 5555);
            new Thread(connector).start();
            int i = 0;
            byte[] databuffer = new byte[1024];

            packet = new DatagramPacket(databuffer, databuffer.length);

            while (i < 10) {
                i++;
                socket.receive(packet);

                System.out.println(new String(packet.getData(), packet.getOffset(), packet.getLength()));

            }
            connector.closeSocket();
        } catch (SocketException e) {
            logger.log(Level.SEVERE, "Failed to open socket", e);
        } catch (UnknownHostException e) {
            logger.log(Level.SEVERE, "Host error", e);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Socket receive problem", e);
        } finally {
            assert socket != null;
            socket.close();
        }*/



        try {
            socket = new DatagramSocket();

            connector = new Connector(socket, InetAddress.getByName("localhost"), 5555);
            //new Thread(connector).start();

            //LinkedBlockingQueue<String> q = new LinkedBlockingQueue<>();
            game  = new PPOModel(5, 5, 3);
            view = new PPOView(game, PPOModel.WIDTH, PPOModel.HEIGHT);
            new Thread(new PPOClient()).start();
            connector.send("NEW");

            while (10 < System.currentTimeMillis()) {
                System.out.println("!");
                byte[] databuffer = new byte[1024];
                packet = new DatagramPacket(databuffer, databuffer.length);

                socket.receive(packet);
                String str = new String(packet.getData(), packet.getOffset(), packet.getLength());

                if (!str.equals("OK")) {
                    String[] data = str.split(":");

                    game.getBall().setX(Integer.parseInt(data[0]));
                    game.getBall().setY(Integer.parseInt(data[1]));

                    game.getPlayer1().setX(Integer.parseInt(data[2]));
                    game.getPlayer1().setY(Integer.parseInt(data[3]));

                    game.getPlayer2().setX(Integer.parseInt(data[4]));
                    game.getPlayer2().setY(Integer.parseInt(data[5]));

                    //game.getPlayer2().setX(Integer.parseInt(data[4]));
                    //game.getPlayer2().setY(Integer.parseInt(data[5]));

                }
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Can't receive packet", e);
        } finally {
            socket.close();
        }
    }

    public void run() {
        //PPOModel game = new PPOModel(5, 5, 3);
        //PPOView view = new PPOView(game, PPOModel.WIDTH, PPOModel.HEIGHT);
        view.addKeyListener(this);

        boolean active = true;
        long timer;
        long wait;
        int FPS = 60;
        int timePerFrame = 1000 / FPS;
        view.render();
        view.draw();

        while (10 < System.currentTimeMillis()) {
            timer = System.nanoTime();

            //game.update();
            view.render();
            view.draw();
            connector.send(state);

            wait = (timePerFrame - (System.nanoTime() - timer)/1000000);

            if (wait > 0) {
                try {
                    Thread.sleep(wait);
                } catch (InterruptedException e) {
                    logger.log(Level.SEVERE, "Interrupted", e);
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_W) {
            state = "UP";
        } else if (e.getKeyCode() == KeyEvent.VK_S) {
            state = "DOWN";

        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_W || e.getKeyCode() == KeyEvent.VK_S) {
            state = "STOP";
        }

    }
}
