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
                                System.out.println("inv name");
                                continue;
                            }

                            connection.send("OK");
                            connections.put(addr+port, connection);
                            System.out.println(connections);
                        } else if (packetData.startsWith("NEWSESSION")) {
                            sessions.put(sessionId, new GameSession(connection, sessionId));
                            //connection.send("OK:" + sessionId);
                            sessionId++;

                        } else if (packetData.startsWith("CONNECTTO=")) {
                            GameSession session = sessions.get(Integer.parseInt(packetData.split("=")[1]));

                            if (session != null && session.getNumOfPlayers() < 2) {
                                session.addConnection(connection);
                                //connection.send("OK");
                            } else {
                                connection.send("FAIL");
                            }
                        } else if (packetData.equals("END")) {
                            UDPConnection connection1 = connection.getSession().getPlayerConnection(1);
                            UDPConnection connection2 = connection.getSession().getPlayerConnection(2);

                            if (connection1 != null) {
                                connection1.send("END");
                                connections.remove(connection1.getAddress() + connection1.getPort());
                                System.out.println(connections);
                            }

                            if (connection2 != null) {
                                connection2.send("END");
                                connections.remove(connection2.getAddress() + connection2.getPort());
                            }

                            sessions.remove(connection.getSession().getSessnum());
                            connection.getSession().addPacket("END");
                            System.out.println(connections);

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
                System.out.println(c.getName() + " " + packetData);
                return null;
            }
        }

        connection = new UDPConnection(addr, Integer.parseInt(port), packetData);

        return connection;
    }

    public synchronized void setActive(boolean b) {active = b;}
    public synchronized boolean getActive() {return active;}
}
