package org.suai.ppoclient;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Connector implements Runnable {
    private static Logger logger = Logger.getLogger("");
    private static final int packetsPerSecond = 2;


    private boolean active = true;
    private InetAddress addr;
    private int port;
    private DatagramSocket socket;

    public Connector(InetAddress addr, int port) throws SocketException {
        this.socket = new DatagramSocket();
        this.addr = addr;
        this.port = port;

    }

    public void run() {
        try {
            while (getActive()) {
                String str = "ACTIVE";
                send(str);
                Thread.sleep(1000 / packetsPerSecond);
            }
        } catch (InterruptedException e) {
            logger.log(Level.SEVERE, "Interrupted", e);
            Thread.currentThread().interrupt();
        }

    }

    public void send(String str) {
        try {
            socket.send(new DatagramPacket(str.getBytes(), str.getBytes().length, addr, port ));
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to send packet", e);
        }

    }

    public String receive() {
        byte[] databuffer = new byte[1024];
        DatagramPacket packet = new DatagramPacket(databuffer, databuffer.length);

        try {
            socket.receive(packet);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to receive packet", e);
        }
        return new String(packet.getData(), packet.getOffset(), packet.getLength());

    }
    public synchronized void setActive(boolean b) { active = b;}
    public synchronized  boolean getActive() { return active; }
}
