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
    private JTextField nameField;
    private JTextField maxScoreField;
    private JTextField connectToField;
    private JList<String> jList;
    private DefaultListModel<String> listModel;

    public PPOMenuView(Connector connector) {
        super("PPOMenu");
        this.connector = connector;
    }

    @Override
    public void run() {
        JPanel contentPane = (JPanel) getContentPane();
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

        jList = new JList<>(listModel);
        jList.setBounds(290, 20, 100, 200);

        JButton updateButton = new JButton("Update");
        updateButton.setBounds(290, 225, 100, 30);
        updateButton.addActionListener(e -> requestSessionsInfo());

        JButton createNewButton = new JButton("New session");
        createNewButton.setBounds(20, 90, 150, 30);
        createNewButton.addActionListener(e -> requestNewSession());

        maxScoreField = new JTextField();
        maxScoreField.setBounds(175, 90, 30, 30);

        JLabel connectToLable = new JLabel("Or connect using name:");
        connectToLable.setBounds(20, 140, 200, 30);

        connectToField = new JTextField();
        connectToField.setBounds(20, 175, 100, 30);

        JButton connectToButton = new JButton("Connect");
        connectToButton.setBounds(125, 175, 150, 30);
        connectToButton.addActionListener(e -> connectByName());

        contentPane.add(maxScoreField);
        contentPane.add(jList);
        contentPane.add(updateButton);
        contentPane.add(submitButton);
        contentPane.add(createNewButton);
        contentPane.add(nameField);
        contentPane.add(nameLabel);
        contentPane.add(connectToLable);
        contentPane.add(connectToField);
        contentPane.add(connectToButton);

        setLocationRelativeTo(null);
        setLayout(null);
        setResizable(false);
        setSize(500, 300);
        setVisible(true);
        requestFocus();
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    private void playGame() {
        setVisible(false);

        new Thread(new PPOGame(this, connector)).start();
    }

    private void connect() {
        boolean ok = validateInput();

        if (!ok) {
            return;
        }

        String session = jList.getSelectedValue();

        connector.send("CONNECTTO=" + session.split(" ")[1]);

        playGame();
    }

    private void connectByName() {
        String name = nameField.getText();

        if (name.length() < 3) {
            showNameLengthError();
            return;
        }

        name = connectToField.getText();

        if (name.length() < 3) {
            showNameLengthError();
            return;
        }

        if (!initConnection(nameField.getText())) {
            showNameDifferent();
            return;
        }

        connector.send("TONAME=" + name);
        String response = connector.receive();

        if (!response.equals("OK")) {

            return;
        }

        playGame();
    }

    private boolean validateInput() {
        String name = nameField.getText();

        if (name.length() < 3) {
            showNameLengthError();
            return false;
        }

        if (jList.getSelectedIndex() == -1) {
            JOptionPane.showMessageDialog(null, "Please choose session to connect to");
            return false;
        }

        boolean ok = initConnection(name);

        if (!ok) {
            showNameDifferent();
            return false;
        }

        return true;
    }

    private void requestSessionsInfo() {
        connector.send("SESSIONSDATA");
        String[] sessions;

        while (true) {
            sessions = connector.receive().split(":");
            if (sessions[0].equals("DATA")) {
                break;
            }
        }

        listModel.clear();

        for (int i = 1; i < sessions.length; i++) {
            if (sessions[i].equals("")) {
                continue;
            }

            listModel.addElement("Session " + sessions[i]);
        }
    }

    private void requestNewSession() {
        if (nameField.getText().length() < 3) {
            showNameLengthError();
            return;
        }

        int maxScore;

        try {
            maxScore = Integer.parseInt(maxScoreField.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Max score must be numerical");
            return;
        }

        boolean ok = initConnection(nameField.getText());
        if (!ok) {
            showNameDifferent();
            return;
        }

        connector.send("NEWSESSION=" + maxScore);

        playGame();
    }

    private boolean initConnection(String name) {
        connector.send(name);
        String str = connector.receive();

        while (!(str.equals("OK") || str.equals("FAIL"))) {
            str = connector.receive();
        }

        return str.equals("OK");
    }

    @Override
    public void keyTyped(KeyEvent e) {
        //unused
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_W) {
            connector.send("UP");
        } else if (e.getKeyCode() == KeyEvent.VK_S) {
            connector.send("DOWN");
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_W || e.getKeyCode() == KeyEvent.VK_S) {
            connector.send("STOP");
        }
    }

    private void showNameLengthError() {
        JOptionPane.showMessageDialog(null, "Name not found or game is full already");
    }

    private void showNameDifferent() {
        JOptionPane.showMessageDialog(null, "Please choose different name");
    }

}
