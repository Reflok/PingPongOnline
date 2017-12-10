package org.suai.pposerver;

import java.net.InetAddress;
import java.util.logging.Logger;

//checks if connection is maintained
//if there were no packets from corresponding user for 1.5 seconds connection is considered inactive
public class UDPConnection {
    private static Logger logger = Logger.getLogger("");

    private long timer;
    private String address;
    private static final long timeoutTime = 1000;
    private GameSession session;
    private int playerNum;

    public UDPConnection(String addr) {
        address = addr;
        timer = System.currentTimeMillis();
        session = null;
    }

    public void handlePacket(String packet) {
        if (session != null) {
            session.addPacket(playerNum + packet);
        }
        update();
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
}
