package org.suai.pposerver;



import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.*;

//receives packets and hands them to handler
public class PacketListener implements Runnable {
    private static Logger logger = Logger.getLogger("");

    private static final String OK = "OK:";

    private InputHandler inputHandler;
    private int socketPort;
    private DatagramSocket socket;
    private boolean active = false;
    private ExecutorService executor = Executors.newFixedThreadPool(3);

    public PacketListener(int portNumber, InputHandler inputHandler) {
        this.inputHandler = inputHandler;

        socketPort = portNumber;


        try {
            socket = new DatagramSocket(socketPort);
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
                        recvPacket.getLength()));

                //tell sender that package is received
                send(recvPacket.getAddress(), recvPacket.getPort(), OK);
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to send packet", e);
        } catch (InterruptedException e) {
            logger.log(Level.SEVERE, "Interrupted", e);
            Thread.currentThread().interrupt();
        }
    }

    public synchronized void send(InetAddress addr, int port, String message) throws IOException {
        DatagramPacket sendPacket = new DatagramPacket(message.getBytes(), message.getBytes().length, addr, port);
        socket.send(sendPacket);
    }

    public static void main(String[] args) {


    }
}
