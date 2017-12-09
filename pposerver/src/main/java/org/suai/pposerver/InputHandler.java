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

    private HashMap<InetAddress, UDPConnection> connections = new HashMap<>();
    private LinkedBlockingQueue<String> packets = new LinkedBlockingQueue<>();
    private boolean active = true;

    public synchronized void handlePacket(String str) throws InterruptedException {
        packets.put(str);
        //System.out.println("+" + new String(recvPacket.getData()));
    }

    public void run() {
        //try {

            while (getActive()) {

                //UDPConnection connection = connections.get(addr);

                //if (connection == null) {
                    //connections.put(addr, new UDPConnection(addr));
                    try {
                        String str = packets.take();
                        logger.log(Level.INFO, str);
                        Thread.sleep(300);
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
