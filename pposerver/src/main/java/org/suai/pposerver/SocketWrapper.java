package org.suai.pposerver;

import java.io.*;
import java.net.*;
import java.util.logging.*;

public class SocketWrapper implements Runnable {
    private static Logger logger = Logger.getLogger("");

    private InputHandler inputHandler;
    private static DatagramSocket socket;
    private boolean active = false;

    SocketWrapper(int portNumber, InputHandler inputHandler) {
        this.inputHandler = inputHandler;

        try {
            socket = new DatagramSocket(portNumber);
        } catch (SocketException e) {
            logger.log(Level.SEVERE, "Failed to open up a socket", e);
        }

        active = true;
    }

    public void run () {
        try {
            byte[] databuffer = new byte[1024];

            DatagramPacket recvPacket = new DatagramPacket(databuffer, databuffer.length);

            while (active) {
                socket.receive(recvPacket);

                inputHandler.handlePacket(new String(recvPacket.getData(), recvPacket.getOffset(),
                        recvPacket.getLength()) + ":" + recvPacket.getAddress().toString() + ":" + recvPacket.getPort());
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to send or receive packet", e);
        } catch (InterruptedException e) {
            logger.log(Level.SEVERE, "Interrupted", e);
            Thread.currentThread().interrupt();
        }
    }

    static synchronized void send(InetAddress addr, int port, String message) throws IOException {
        DatagramPacket sendPacket = new DatagramPacket(message.getBytes(), message.getBytes().length, addr, port);
        socket.send(sendPacket);
    }
}
