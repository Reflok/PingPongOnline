package org.suai.pposerver;

import ppomodel.PPOModel;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GameSession implements Runnable {
    Logger logger = Logger.getLogger("");

    private UDPConnection connection1;
    private UDPConnection connection2;

    private LinkedBlockingQueue<String> recvPackets;

    private PPOModel gameModel;
    private PPOState state;

    private int numOfPlayers;

    public GameSession(UDPConnection connection1) {
        this.connection1 = connection1;
        connection1.setPlayerNum(1);
        numOfPlayers = 1;
        gameModel = new PPOModel(5, 5, 3);
    }

    public void addConnection(UDPConnection connection2) {
        numOfPlayers = 2;
        this.connection2 = connection2;
        connection2.setPlayerNum(2);
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

        try {
            while (10 < System.currentTimeMillis()) {
                str = recvPackets.take();

                int entityNum = Character.getNumericValue(str.charAt(0));

                gameModel.getEntity(entityNum).setHspeed(Character.getNumericValue(str.charAt(1)));
                gameModel.getEntity(entityNum).setVspeed(Character.getNumericValue(str.charAt(2)));


            }
        } catch (InterruptedException e) {
            logger.log(Level.SEVERE, "Interrupted", e);
            Thread.currentThread().interrupt();
        }

    }

    public PPOState getState() {
        return state;
    }

    public PPOModel getGameModel() {
        return gameModel;
    }

    public int getNumOfPlayers() {
        return numOfPlayers;
    }
}
