package org.suai.ppoclient;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Connector implements Runnable {
    private static Logger logger = Logger.getLogger("");

    private boolean active = true;
    private InetAddress addr;
    private int port;
    private DatagramSocket socket;

    public Connector(DatagramSocket socket, InetAddress addr, int port) {
        this.socket = socket;
        this.addr = addr;
        this.port = port;
    }

    public void run() {
        int i = 0;
        try {
            while (getActive()) {
                String str = "ACTIVE" + i;
                i++;
                socket.send(new DatagramPacket(str.getBytes(), str.getBytes().length,addr, port ));
                Thread.sleep(200);
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to send packet", e);
        } catch (InterruptedException e) {
            logger.log(Level.SEVERE, "Interrupted", e);
            Thread.currentThread().interrupt();
        }

    }

    public void closeSocket() {
        setActive(false);
        socket.close();
    }

    public synchronized void setActive(boolean b) { active = b;}
    public synchronized  boolean getActive() { return active; }
}
