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
                        String[] tokens = packetAddr.split(":");
                        String packetData = tokens[0];
                        String addr = tokens[1];
                        if (addr.startsWith("/")) {
                            addr = addr.substring(1, addr.length());
                        }
                        String port = tokens[2];

                        UDPConnection connection = connections.get(addr);

                        if (connection == null) {
                            connection = new UDPConnection(addr, Integer.parseInt(port));
                            connections.put(addr, connection);

                            if (sessions.isEmpty() || sessions.get(sessions.size() - 1).getNumOfPlayers() == 2) {
                                sessions.add(new GameSession(connection));
                                new Thread(sessions.get(sessions.size() - 1)).start();
                            } else {
                                sessions.get(sessions.size() - 1).addConnection(connection);
                            }

                        } else {
                            connection.handlePacket(packetData);
                        }


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
