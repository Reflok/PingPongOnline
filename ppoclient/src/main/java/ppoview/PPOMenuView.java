package ppoview;

import javax.swing.*;
import java.net.DatagramSocket;

public class PPOMenuView extends JPanel {
    private DatagramSocket socket;
    private JFrame frame;

    public PPOMenuView(DatagramSocket socket) {
        this.socket = socket;
        frame = new JFrame("PPO Client");
        frame.setContentPane(this);
        frame.pack();
        frame.setResizable(false);
        frame.setSize(500, 300);
        frame.setVisible(true);
        requestFocus();
    }

    public JFrame getFrame() {
        return frame;
    }

    public void setFrame(JFrame frame) {
        this.frame = frame;
    }
}
