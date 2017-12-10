package org.suai.pposerver;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

//handles received packets
public class InputHandler implements Runnable {
    private static Logger logger = Logger.getLogger("");
    private static final boolean DEBUG = true;

    private HashMap<String, UDPConnection> connections = new HashMap<>();
    private LinkedBlockingQueue<String> packets = new LinkedBlockingQueue<>();
    private ArrayList<GameSession> sessions = new ArrayList<>();
    private boolean active = true;

        public synchronized void handlePacket(String str) throws InterruptedException {
        packets.put(str);
    }

    public void run() {
        //try {

            while (getActive()) {

                    try {
                        String packetAddr = packets.take();
                        String[] tokens = packetAddr.split("<ADDR>");
                        String addr = tokens[1];
                        String packetData = tokens[0];

                        UDPConnection connection = connections.get(addr);

                        if (connection == null) {
                            connection = new UDPConnection(addr);
                            connections.put(addr, new UDPConnection(addr));

                            if (sessions.isEmpty() || sessions.get(sessions.size() - 1).getNumOfPlayers() == 2) {
                                GameSession session = new GameSession(connection);
                            } else {
                                sessions.get(sessions.size() - 1).addConnection(connection);
                            }
                        }
                        connection.handlePacket(packetData);

                        String logEntry = String.format("Message from%s%n%s", addr, packetData);

                        logger.log(Level.INFO, logEntry);
                    } catch (InterruptedException e) {
                        logger.log(Level.SEVERE, "Interrupted");
                        Thread.currentThread().interrupt();
                    }
                    //} else {
                    //connection.update();
                //}

                    //logger.log(Level.INFO, String.format("Received packet from %s:%n%s%n", packet.getAddress().toString(),
                        //new String(packet.getData(), packet.getOffset(), packet.getLength())))

            }
        /*} catch (InterruptedExcedption e) {
            //logger.log(Level.SEVERE, "Interrupted", e);
            Thread.currentThread().interrupt();
        }*/
    }

    public synchronized void setActive(boolean b) {active = b;}
    public synchronized boolean getActive() {return active;}
}
