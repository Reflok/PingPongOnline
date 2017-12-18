package org.suai.pposerver;

import java.io.IOException;
import java.net.InetAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UDPConnection {
    private static Logger logger = Logger.getLogger("");

    private String address;
    private int port;
    private GameSession session;
    private int playerNum;
    private String name = null;

    UDPConnection(String addr, int port, String name) {
        address = addr;
        this.port = port;
        this.name = name;
        session = null;
    }

    void handlePacket(String packet) {
        if (session != null) {
            session.addPacket(playerNum + ":" + packet);
        }
    }

    void send(String message) {
        try {
            SocketWrapper.send(InetAddress.getByName(address), port, message);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Can't send", e);
        }
    }

    GameSession getSession() {
        return session;
    }

    void setSession(GameSession session) {
        this.session = session;
    }

    void setPlayerNum(int playerNum) {
        this.playerNum = playerNum;
    }

    String getName() {
        return name;
    }

    String getAddress() { return address;}

    int getPort() {return port;}

    public String toString() {
        return address + ":" + port + ":" + name;
    }
}
