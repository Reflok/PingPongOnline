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

    private boolean p1ready = false;
    private boolean p2ready = false;

    private boolean active = true;

    private UDPConnection connection1;
    private UDPConnection connection2;

    private LinkedBlockingQueue<String> recvPackets = new LinkedBlockingQueue<>();

    private PPOModel gameModel;
    private int state;

    private int numOfPlayers;

    private int sessnum;

    public GameSession(UDPConnection connection1, int sessnum) {
        this.connection1 = connection1;
        this.sessnum = sessnum;
        connection1.setPlayerNum(1);
        connection1.setSession(this);
        numOfPlayers = 1;
        gameModel = new PPOModel(5, 6, 4, connection1.getName());
        state = STATE_WAITING;
        new Thread(this).start();
    }

    public void addConnection(UDPConnection connection2) {
        numOfPlayers = 2;
        this.connection2 = connection2;
        connection2.setSession(this);
        connection2.setPlayerNum(2);
        gameModel.setName2(connection2.getName());
        gameModel.setState(PPOModel.STATE_START1);

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
            while (isActive()) {
                /*if (getState() == STATE_WAITING) {
                    Thread.sleep(100);
                    continue;
                } else if (getState() == STATE_BEGIN1) {
                    gameModel = new PPOModel(5, 15, 9);
                }*/



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
        //if (state == STATE_PLAY) {
            gameModel.update();
        //}

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
            if (tokens[1].equals("DOWN")) {
                gameModel.getPlayer(playerNum).setDirection(PlayerModel.DOWN);
            } else if (tokens[1].equals("UP")) {
                gameModel.getPlayer(playerNum).setDirection(PlayerModel.UP);
            } else if (tokens[1].equals("STOP")) {
                gameModel.getPlayer(playerNum).setDirection(PlayerModel.STOP);
            } else if (tokens[1].equals("READY")) {
                if (playerNum == 1) {
                    p1ready = true;
                } else if (playerNum == 2) {
                    p2ready = true;
                }
            }
        } else if (gameModel.getState() == PPOModel.STATE_START1 && playerNum == 1 && tokens[1].equals("SPACE")) {
            gameModel.start();
            gameModel.getBall().setHspeed(-gameModel.getBall().getHspeed());
        } else if (gameModel.getState() == PPOModel.STATE_START2 && playerNum == 2 && tokens[1].equals("SPACE")) {
            gameModel.start();
        }
        System.out.println(gameModel.getState() + " " + playerNum + " " + tokens[1]);
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

    public int getSessnum() {
        return sessnum;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
