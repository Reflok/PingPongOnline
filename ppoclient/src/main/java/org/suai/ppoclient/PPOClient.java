package org.suai.ppoclient;

import ppomodel.PPOModel;
import ppoview.PPOView;

import java.io.IOException;
import java.net.*;
import java.util.logging.*;

public class PPOClient implements Runnable {
    private static Logger logger = Logger.getLogger("");
    private static final boolean APPEND = false;

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
        /*DatagramSocket socket = null;

        try  {
            socket = new DatagramSocket();
            Connector connector = new Connector(socket, InetAddress.getByName("localhost"), 5555);
            new Thread(connector).start();
            int i = 0;
            byte[] databuffer = new byte[1024];

            DatagramPacket packet = new DatagramPacket(databuffer, databuffer.length);

            /*while (i < 10) {
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
        new Thread(new PPOClient()).start();
    }

    public void run() {
        PPOModel game = new PPOModel(5, 5, 3);
        PPOView view = new PPOView(game, PPOModel.WIDTH, PPOModel.HEIGHT);

        boolean active = true;
        long timer;
        long wait;
        int FPS = 60;
        int timePerFrame = 1000/ FPS;

        while (10 < System.currentTimeMillis()) {
            timer = System.nanoTime();

            game.update();
            view.render();
            view.draw();

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
}
