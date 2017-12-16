package org.suai.pposerver;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

//handles received packets
public class InputHandler implements Runnable {
    private static Logger logger = Logger.getLogger("");
    private static final boolean DEBUG = true;

    private HashMap<String, UDPConnection> connections = new HashMap<>();
    private LinkedBlockingQueue<String> packets = new LinkedBlockingQueue<>();
    //private ArrayList<GameSession> sessions = new ArrayList<>();
    private HashMap<Integer, GameSession> sessions = new HashMap<>();
    private boolean active = true;

        public synchronized void handlePacket(String str) throws InterruptedException {
        packets.put(str);
    }

    public void run() {
        //try {
            int sessionId = 0;

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
                            connection = newConnection(packetData, addr, port);

                            if (connection == null) {
                                new UDPConnection(packetAddr, Integer.parseInt(port), null).send("FAIL");
                                continue;
                            }

                            connection.send("OK");
                            connections.put(addr, connection);
                        } else if (packetData.equals("SESSIONSDATA")) {
                            connection.send(getSessionsInfo());
                        } else if (packetData.startsWith("NEWSESSION")) {
                            sessions.put(sessionId, new GameSession(connection));
                            connection.send("OK:" + sessionId);
                            sessionId++;

                        } else if (packetData.startsWith("CONNECTTO=")) {
                            GameSession session = sessions.get(Integer.parseInt(packetData.split("=")[1]));

                            if (session != null && session.getNumOfPlayers() < 2) {
                                session.addConnection(connection);
                                connection.send("OK");
                            } else {
                                connection.send("FAIL");
                            }
                        } else {
                            connection.handlePacket(packetData);
                        }


                        String logEntry = String.format("Message from %s%n%s", addr, packetData);

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

    private String getSessionsInfo() {
        StringBuilder msg = new StringBuilder();

        for (int i = 0; i < sessions.size(); i++) {
            if (sessions.get(i).getNumOfPlayers() < 2) {
                msg.append(i).append(":");
            }
        }
        return msg.toString();
    }

    private UDPConnection newConnection(String packetData, String addr, String port) {
        UDPConnection connection;

        for (UDPConnection c :connections.values()) {
            if (c.getName().equals(packetData)) {
                return null;
            }
        }

        connection = new UDPConnection(addr, Integer.parseInt(port), packetData);
        connections.put(addr, connection);

        return connection;
    }

    public synchronized void setActive(boolean b) {active = b;}
    public synchronized boolean getActive() {return active;}
}
