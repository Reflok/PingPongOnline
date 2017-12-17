package org.suai.ppoview;

import org.suai.ppoclient.Connector;
import org.suai.ppoclient.ModelUpdater;
import org.suai.ppoclient.PPOGame;
import org.suai.ppoclient.ViewUpdater;
import ppomodel.PPOModel;

import javax.swing.*;
import java.awt.event.*;
import java.net.*;

public class PPOMenuView extends JFrame implements KeyListener, Runnable {
    private Connector connector;
    private JPanel contentPane;
    private JTextField nameField;
    private DatagramSocket socket;
    private JList jList;
    private DefaultListModel<String> listModel;

    public PPOMenuView(Connector connector, DatagramSocket socket) {
        super("PPOMenu");
        this.connector = connector;
        this.socket = socket;


    }

    public void playGame() {
        setVisible(false);

        new Thread(new PPOGame(this, connector)).start();


    }

    @Override
    public void run() {
        contentPane = (JPanel) getContentPane();
        setSize(400, 300);
        JLabel nameLabel = new JLabel("Your name:");
        nameLabel.setBounds(20, 20, 100, 30);
        nameField = new JTextField();
        nameField.setBounds(20, 55, 100, 30);

        JButton submitButton = new JButton("Play");
        submitButton.addActionListener(e -> connect());
        submitButton.setBounds(130, 55, 100, 30);

        listModel = new DefaultListModel<>();
        requestSessionsInfo();

        jList = new JList(listModel);
        jList.setBounds(290, 20, 100, 200);

        JButton updateButton = new JButton("Update");
        updateButton.setBounds(290, 225, 100, 30);
        updateButton.addActionListener(e -> requestSessionsInfo());

        JButton createNewButton = new JButton("New session");
        createNewButton.setBounds(20, 90, 100, 30);
        createNewButton.addActionListener(e -> requestNewSession());

        contentPane.add(jList);
        contentPane.add(updateButton);
        contentPane.add(submitButton);
        contentPane.add(createNewButton);
        contentPane.add(nameField);
        contentPane.add(nameLabel);

        setLocationRelativeTo(null);
        setLayout(null);
        setResizable(false);
        setSize(500, 300);
        setVisible(true);
        requestFocus();
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    public class Lisnt implements KeyListener {
        @Override
        public void keyTyped(KeyEvent e) {
        }

        @Override
        public void keyPressed(KeyEvent e) {
            System.out.println(e.getKeyChar());

        }

        @Override
        public void keyReleased(KeyEvent e) {
            System.out.println(e.getKeyChar());

        }
    }


    public void connect() {
        boolean ok = validateInput();

        if (!ok) {
            return;
        }

        String session = (String) jList.getSelectedValue();

        connector.send("CONNECTTO=" + session.split(" ")[1]);

        playGame();
    }

    public boolean validateInput() {
        String name = nameField.getText();

        if (name.length() < 3) {
            JOptionPane.showMessageDialog(null, "Name should be at least 3 characters long");
            return false;
        }

        if (jList.getSelectedIndex() == -1) {
            JOptionPane.showMessageDialog(null, "Please choose session to connect to");
            return false;
        }

        //connector.send(name);
        //String response = connector.receive();

        boolean ok = initConnection(name);

        if (!ok) {
            JOptionPane.showMessageDialog(null, "Please choose different name");
            return false;
        }

        return true;
    }

    public void requestSessionsInfo() {
        connector.send("SESSIONSDATA");
        String[] sessions = connector.receive().split(":");
        listModel.clear();

        for (int i = 0; i < sessions.length; i++) {
            //list.add(Integer.parseInt(sessions[i]));
            if (sessions[i].equals("")) {
                continue;
            }
            listModel.addElement("Session " + sessions[i]);
        }
    }

    private void requestNewSession() {
        if (nameField.getText().length() < 3) {
            JOptionPane.showMessageDialog(null, "Name should be at least 3 characters long");
            return;
        }

        boolean ok = initConnection(nameField.getText());
        if (!ok) {
            JOptionPane.showMessageDialog(null, "Please choose different name");
            return;
        }

        connector.send("NEWSESSION");

        playGame();


    }

    private boolean initConnection(String name) {
        connector.send(name);
        String str = connector.receive();

        if (str.equals("OK")) {
            return true;
        } else {
            return false;
        }
    }

    public static void main(String[] args) {

        DatagramSocket socket = null;
        try {


            Connector connector = new Connector(InetAddress.getByName("localhost"), 5000);
            new Thread(new PPOMenuView(connector, socket)).run();
            long i = System.currentTimeMillis();

        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        System.out.println(">");
        if (e.getKeyCode() == KeyEvent.VK_W) {
            connector.send("UP");
        } else if (e.getKeyCode() == KeyEvent.VK_S) {
            connector.send("DOWN");
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_W) {
            connector.send("STOP");
        } else if (e.getKeyCode() == KeyEvent.VK_S) {
            connector.send("STOP");
        }
    }
}
