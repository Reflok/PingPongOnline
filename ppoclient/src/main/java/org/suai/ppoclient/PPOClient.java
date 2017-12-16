package org.suai.ppoclient;

import ppomodel.PPOModel;
import ppoview.PPOGameView;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.net.*;
import java.util.logging.*;

public class PPOClient implements Runnable, KeyListener {
    private static Logger logger = Logger.getLogger("");
    private static final boolean APPEND = false;

    private static PPOModel game;
    private static PPOGameView view;
    private static Connector connector;
    private static String state = "STOP";
    private boolean upPressed = false;
    private boolean downPressed = false;
    public boolean active = false;


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
        DatagramPacket packet;

        try {
            socket = new DatagramSocket();

            connector = new Connector(socket, InetAddress.getByName("localhost"), 5000);

            boolean f = false;
            int i = 1;

            while (!f) {
                f = initConnection(socket,"Name" + i);
                i++;
            }

            int sess = requestNewSession(socket);



            //new Thread(connector).start();

            //LinkedBlockingQueue<String> q = new LinkedBlockingQueue<>();
            game  = new PPOModel(5, 5, 3, "Name" + (i - 1));
            view = new PPOGameView(game, PPOModel.WIDTH, PPOModel.HEIGHT);
            new Thread(new PPOClient()).start();
            Thread.sleep(2000);
            connector.send("READY");

            while (10 < System.currentTimeMillis()) {
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

                    game.setName1(data[6]);
                    game.setName2(data[7]);

                    game.setPlayer1Score(Integer.parseInt(data[8]));
                    game.setPlayer2Score(Integer.parseInt(data[9]));

                }
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Can't receive packet", e);
        } catch (InterruptedException e) {
            logger.log(Level.SEVERE, "Interrupted", e);
            Thread.currentThread().interrupt();
        } finally {
            socket.close();
        }
    }

    private static boolean initConnection(DatagramSocket socket, String name) throws IOException {
        DatagramPacket packet;
        connector.send(name);
        byte[] buf = new byte[1024];
        packet = new DatagramPacket(buf, buf.length);

        socket.receive(packet);

        String str = new String(packet.getData(), packet.getOffset(), packet.getLength());

        if (str.equals("OK")) {
            return true;
        } else {
            return false;
        }
    }

    private static int requestNewSession(DatagramSocket socket) throws IOException {
        DatagramPacket packet;
        connector.send("NEWSESSION");
        byte[] buf = new byte[1024];
        packet = new DatagramPacket(buf, buf.length);

        socket.receive(packet);

        String str = new String(packet.getData(), packet.getOffset(), packet.getLength());

        if (str.startsWith("OK:")) {
            return Integer.parseInt(str.split(":")[1]);
        } else {
            return -1;
        }
    }

    public void run() {
        //PPOModel game = new PPOModel(5, 5, 3);
        //PPOGameView view = new PPOGameView(game, PPOModel.WIDTH, PPOModel.HEIGHT);
        view.addKeyListener(this);

        boolean active = true;
        long timer;
        long wait;
        int FPS = 200;
        int timePerFrame = 1000 / FPS;
        view.render();
        view.draw();

        while (active) {
            timer = System.nanoTime();

            //game.update();
            view.render();
            view.draw();

            if (upPressed && downPressed || !downPressed && !upPressed) {
                state = "STOP";
            } else if (upPressed) {
                state = "UP";
            } else if (downPressed) {
                state = "DOWN";
            }

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
            upPressed = true;
        } else if (e.getKeyCode() == KeyEvent.VK_S) {
            state = "DOWN";
            downPressed = true;

        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_W || e.getKeyCode() == KeyEvent.VK_S) {
            state = "STOP";
        }

        if (e.getKeyCode() == KeyEvent.VK_W) {
            upPressed = false;
        } else if (e.getKeyCode() == KeyEvent.VK_S) {
            downPressed = false;
        } else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            connector.send("READY");
        }
    }
}
