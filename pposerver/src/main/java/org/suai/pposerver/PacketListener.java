package org.suai.pposerver;



import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.*;

public class PacketListener implements Runnable {
    private static Logger logger = Logger.getLogger("");

    private static final String okMessage = "OK:";

    private InputHandler inputHandler;
    private int socketPort;
    private DatagramSocket socket;
    private TreeSet<InetAddress> ips = new TreeSet<InetAddress>();
    private boolean active = false;

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

                String received = new String(recvPacket.getData(), recvPacket.getOffset(), recvPacket.getLength());

                inputHandler.handlePacket(recvPacket.getAddress(), received);

                String response = okMessage;


            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to send packet", e);
        }
    }

    public synchronized void send(InetAddress addr, int port, String message) throws IOException {
        DatagramPacket sendPacket = new DatagramPacket(message.getBytes(), message.getBytes().length, addr, port);
        socket.send(sendPacket);
    }

    public static void main(String[] args) {


    }
}
