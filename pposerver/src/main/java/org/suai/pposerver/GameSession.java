package org.suai.pposerver;

import ppomodel.PPOModel;
import ppomodel.PlayerModel;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GameSession implements Runnable {
    private static final Logger logger = Logger.getLogger("");

    private boolean active = true;

    private UDPConnection connection1;
    private UDPConnection connection2;

    private LinkedBlockingQueue<String> recvPackets = new LinkedBlockingQueue<>();

    private PPOModel gameModel;

    private int numOfPlayers;

    private int sessnum;

    private int maxScore;

    GameSession(UDPConnection connection1, int sessnum, int maxScore) {
        this.maxScore = maxScore;
        this.connection1 = connection1;
        this.sessnum = sessnum;
        connection1.setPlayerNum(1);
        connection1.setSession(this);
        numOfPlayers = 1;
        gameModel = new PPOModel(maxScore, 6, 4, connection1.getName());
        new Thread(this).start();
    }

    void addConnection(UDPConnection connection2) {
        numOfPlayers = 2;
        this.connection2 = connection2;
        connection2.setSession(this);
        connection2.setPlayerNum(2);
        gameModel.setName2(connection2.getName());
        gameModel.setState(PPOModel.STATE_START1);
    }

    void addPacket(String packet) {
        try {
            recvPackets.put(packet);
        } catch (InterruptedException e) {
            logger.log(Level.SEVERE, "Interrupted", e);
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void run() {
        String str;
        long updateTimer = System.nanoTime();
        long packetSendTimer = System.nanoTime();
        long wait;

        try {
            while (isActive()) {
                str = recvPackets.poll(1000/100, TimeUnit.MILLISECONDS);

                if (str != null) {

                    if (str.equals("END")) {
                        return;
                    }

                    handleInput(str);
                }

                wait = (1000 / 100 - (System.nanoTime() - updateTimer) / 1000000);

                if (wait <= 0) {
                    packetSendTimer = updateModel(packetSendTimer);

                    updateTimer = System.nanoTime();
                }
            }
        } catch (InterruptedException e) {
            logger.log(Level.SEVERE, "Interrupted", e);
            Thread.currentThread().interrupt();
        }
    }

    private long updateModel(long packetSendTimer) {
        long wait;

        gameModel.update();

        if (gameModel.getState() == PPOModel.STATE_WIN1 || gameModel.getState() == PPOModel.STATE_WIN2) {
            gameModel = new PPOModel(maxScore, 6, 4, connection1.getName());
            gameModel.setName2(connection2.getName());
            gameModel.setState(PPOModel.STATE_START1);
        }

        wait = (1000 / 40 - (System.nanoTime() - packetSendTimer) / 1000000);

        if (wait <= 0) {
            String message =
                    Integer.toString((int) gameModel.getBall().getX()) + ":" +
                            Integer.toString((int) gameModel.getBall().getY()) + ":" +
                            Integer.toString((int) gameModel.getPlayer1().getX()) + ":" +
                            Integer.toString((int) gameModel.getPlayer1().getY()) + ":" +
                            Integer.toString((int) gameModel.getPlayer2().getX()) + ":" +
                            Integer.toString((int) gameModel.getPlayer2().getY()) + ":" +
                            gameModel.getName1() + ":" + gameModel.getName2() + ":"
                            + gameModel.getPlayer1Score() + ":" + gameModel.getPlayer2Score();


            connection1.send(message);

            if (connection2 != null) {
                connection2.send(message);
            }
            packetSendTimer = System.nanoTime();
        }
        return packetSendTimer;
    }

    private void handleInput(String str) {
        String[] tokens = str.split(":");

        int playerNum = Integer.parseInt(tokens[0]);

        if (gameModel.getState() == PPOModel.STATE_PLAY) {
            switch (tokens[1]) {
                case "DOWN":
                    gameModel.getPlayer(playerNum).setDirection(PlayerModel.DOWN);
                    break;
                case "UP":
                    gameModel.getPlayer(playerNum).setDirection(PlayerModel.UP);
                    break;
                case "STOP":
                    gameModel.getPlayer(playerNum).setDirection(PlayerModel.STOP);
                    break;
            }
        } else if (gameModel.getState() == PPOModel.STATE_START1 && playerNum == 1 && tokens[1].equals("SPACE")) {
            gameModel.start();
            gameModel.getBall().setHspeed(-gameModel.getBall().getHspeed());
        } else if (gameModel.getState() == PPOModel.STATE_START2 && playerNum == 2 && tokens[1].equals("SPACE")) {
            gameModel.start();
        }
    }


    int getNumOfPlayers() {
        return numOfPlayers;
    }

    UDPConnection getPlayerConnection(int playerNum) {
        if (playerNum == 1) {
            return connection1;
        } else if (playerNum == 2) {
            return connection2;
        }

        return null;
    }

    int getSessnum() {
        return sessnum;
    }

    private boolean isActive() {
        return active;
    }

}