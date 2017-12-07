package org.suai.pposerver;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class InputHandler {
    private static Logger exceptionLogger = Logger.getLogger("");
    private HashMap<InetAddress, UDPConnection> connections = new HashMap<InetAddress, UDPConnection>();

    public InputHandler() {

    }

    public void handlePacket(InetAddress address, String recvStr) {

    }

}
