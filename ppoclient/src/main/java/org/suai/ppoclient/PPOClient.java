package org.suai.ppoclient;

import java.io.IOException;
import java.net.*;
import java.util.logging.*;

public class PPOClient {
    private static Logger logger = Logger.getLogger("");
    private static final boolean APPEND = false;

    public static void main (String[] args) {
        //logger set up
        Logger.getLogger("").setLevel(Level.FINEST);

        try {
            logger.setUseParentHandlers(false);

            FileHandler fl1 = new FileHandler("clientExceptions.log", APPEND);
            logger.addHandler(fl1);
            fl1.setFormatter(new SimpleFormatter());

        } catch (IOException e) {
            logger.log(Level.SEVERE, "Can't set up logger", e);
            return;
        }
        DatagramSocket socket = null;

        try  {
            socket = new DatagramSocket();
            Connector connector = new Connector(socket, InetAddress.getByName("localhost"), 5555);
            new Thread(connector).start();
            int i = 0;
            byte[] databuffer = new byte[1024];

            DatagramPacket packet = new DatagramPacket(databuffer, databuffer.length);

            while (i < 10) {
                i++;
                socket.receive(packet);

                System.out.println(new String(packet.getData(), packet.getOffset(), packet.getLength()));

            }
            connector.closeSocket();
        } catch (SocketException e) {
            logger.log(Level.SEVERE, "Failed to open socket", e);
        } catch (UnknownHostException e) {
            logger.log(Level.SEVERE, "Host error", e);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Socket receive problem", e);
        } finally {
            socket.close();
        }
    }
}
