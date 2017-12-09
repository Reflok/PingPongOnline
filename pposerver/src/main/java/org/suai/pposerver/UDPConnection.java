package org.suai.pposerver;

import java.net.InetAddress;
import java.util.logging.Logger;

//checks if connection is maintained
//if there were no packets from corresponding user for 1.5 seconds connection is considered inactive
public class UDPConnection {
    private static Logger logger = Logger.getLogger("");

    private long timer;
    private InetAddress address;
    private static final long timeoutTime = 1500;

    public UDPConnection(InetAddress addr) {
        address = addr;
        timer = System.currentTimeMillis();
    }

    public void update() {
        timer = System.currentTimeMillis();
    }

    //check if connection is active
    public boolean isActive() {
        return System.currentTimeMillis() - timer < timeoutTime;
    }
}
