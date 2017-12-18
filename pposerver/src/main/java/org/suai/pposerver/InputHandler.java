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

                        if (packetData.equals("SESSIONSDATA")) {
                            new UDPConnection(addr, Integer.parseInt(port), null).send(getSessionsInfo());
                            continue;
                        }

                        UDPConnection connection = connections.get(addr+port);


                        if (connection == null) {
                            connection = newConnection(packetData, addr, port);

                            if (connection == null) {
                                new UDPConnection(addr, Integer.parseInt(port), null).send("FAIL");
                                continue;
                            }

                            connection.send("OK");
                            connections.put(addr+port, connection);
                        } else if (packetData.startsWith("NEWSESSION=")) {
                            sessions.put(sessionId, new GameSession(connection, sessionId,
                                    Integer.parseInt(packetData.split("=")[1])));
                            //connection.send("OK:" + sessionId);
                            sessionId++;

                        } else if (packetData.startsWith("CONNECTTO=")) {
                            GameSession session = sessions.get(Integer.parseInt(packetData.split("=")[1]));

                            if (session != null && session.getNumOfPlayers() < 2) {
                                session.addConnection(connection);
                            } else {
                                connection.send("FAIL");
                            }
                        } else if (packetData.startsWith("TONAME=")) {
                            GameSession sess = null;
                            String name = packetData.split("=")[1];

                            for (Map.Entry<String, UDPConnection> c : connections.entrySet()) {
                                UDPConnection con = c.getValue();

                                if (con.getName().equals(name)) {
                                    if (con.getSession().getNumOfPlayers() < 2) {
                                        sess = con.getSession();
                                        break;
                                    }

                                    break;
                                }
                            }

                            if (sess == null) {
                                connection.send("FAIL");
                            } else {
                                connection.send("OK");
                                sess.addConnection(connection);
                            }

                        } else if (packetData.equals("END")) {
                            UDPConnection connection1 = connection.getSession().getPlayerConnection(1);
                            UDPConnection connection2 = connection.getSession().getPlayerConnection(2);

                            if (connection1 != null) {
                                connection1.send("END");
                                connections.remove(connection1.getAddress() + connection1.getPort());
                            }

                            if (connection2 != null) {
                                connection2.send("END");
                                connections.remove(connection2.getAddress() + connection2.getPort());
                            }

                            sessions.remove(connection.getSession().getSessnum());
                            connection.getSession().addPacket("END");

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
        msg.append("DATA:");

        for (Map.Entry<Integer, GameSession> entry : sessions.entrySet()) {
            if (entry.getValue().getNumOfPlayers() < 2) {
                msg.append(entry.getKey()).append(":");
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

        return connection;
    }

    public synchronized void setActive(boolean b) {active = b;}
    public synchronized boolean getActive() {return active;}
}
