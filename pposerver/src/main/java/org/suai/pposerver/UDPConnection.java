package org.suai.pposerver;

import java.io.IOException;
import java.net.InetAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

//checks if connection is maintained
//if there were no packets from corresponding user for 1.5 seconds connection is considered inactive
public class UDPConnection {
    private static Logger logger = Logger.getLogger("");

    private long timer;
    private String address;
    private int port;
    private static final long timeoutTime = 1000;
    private GameSession session;
    private int playerNum;
    private String name = null;
    private boolean inGame = false;


    public UDPConnection(String addr, int port, String name) {
        address = addr;
        this.port = port;
        this.name = name;
        timer = System.currentTimeMillis();
        session = null;
    }

    public void handlePacket(String packet) {
        if (session != null) {
            session.addPacket(playerNum + ":" + packet);
        }
        update();
    }

    public void send(String message) {
        try {
            SocketWrapper.send(InetAddress.getByName(address), port, message);
            //System.out.println("Sending " + message + " to " + address + ":" + port);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Can't send", e);
        }
    }

    private void update() {
        timer = System.currentTimeMillis();
    }

    //check if connection is active
    public boolean isActive() {
        return System.currentTimeMillis() - timer < timeoutTime;
    }

    public GameSession getSession() {
        return session;
    }

    public void setSession(GameSession session) {
        this.session = session;
    }

    public void setPlayerNum(int playerNum) {
        this.playerNum = playerNum;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() { return address;}

    public boolean isInGame() {
        return inGame;
    }

    public void setInGame(boolean inGame) {
        this.inGame = inGame;
    }
    public int getPort() {return port;}

    public String toString() {
        return address + ":" + port + ":" + name;
    }
}
