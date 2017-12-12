package org.suai.pposerver;

import ppomodel.PPOModel;
import ppomodel.PlayerModel;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GameSession implements Runnable {
    private static final Logger logger = Logger.getLogger("");

    private static final int STATE_WAITING = 0;
    private static final int STATE_PLAY = 1;
    private static final int STATE_BEGIN1 = 2;
    private static final int STATE_BEGIN2 = 3;

    private UDPConnection connection1;
    private UDPConnection connection2;

    private LinkedBlockingQueue<String> recvPackets = new LinkedBlockingQueue<>();

    private PPOModel gameModel;
    private int state;

    private int numOfPlayers;

    public GameSession(UDPConnection connection1) {
        this.connection1 = connection1;
        connection1.setPlayerNum(1);
        connection1.setSession(this);
        numOfPlayers = 1;
        gameModel = new PPOModel(5, 5, 3);
        state = STATE_WAITING;
    }

    public void addConnection(UDPConnection connection2) {
        numOfPlayers = 2;
        this.connection2 = connection2;
        connection2.setSession(this);
        connection2.setPlayerNum(2);
        state = STATE_BEGIN1;
    }


    public void addPacket(String packet) {
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
            while (10 < System.currentTimeMillis()) {
                str = recvPackets.poll(1000/100, TimeUnit.MILLISECONDS);

                if (str != null) {

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

        wait = (1000 / 60 - (System.nanoTime() - packetSendTimer) / 1000000);

        if (wait <= 0) {
            String message = Integer.toString(gameModel.getBall().getX()) + ":" +
                    Integer.toString(gameModel.getBall().getY()) + ":" +
                    Integer.toString(gameModel.getPlayer1().getX()) + ":" +
                    Integer.toString(gameModel.getPlayer1().getY()) + ":" +
                    Integer.toString(gameModel.getPlayer2().getX()) + ":" +
                    Integer.toString(gameModel.getPlayer2().getY()) + ":";


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

        if (tokens[1].equals("DOWN")) {
            gameModel.getPlayer(playerNum).setDirection(PlayerModel.DOWN);
        } else if (tokens[1].equals("UP")) {
            gameModel.getPlayer(playerNum).setDirection(PlayerModel.UP);
        } else if (tokens[1].equals("STOP")) {
            gameModel.getPlayer(playerNum).setDirection(PlayerModel.STOP);
        }
    }

    public int getState() {
        return state;
    }

    public PPOModel getGameModel() {
        return gameModel;
    }

    public int getNumOfPlayers() {
        return numOfPlayers;
    }

    public UDPConnection getPlayerConnection(int playerNum) {
        if (playerNum == 1) {
            return connection1;
        } else if (playerNum == 2) {
            return connection2;
        }

        return null;
    }
}
