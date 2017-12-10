package ppoview;

import ppomodel.BallModel;
import ppomodel.PPOModel;
import ppomodel.PlayerModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;

public class PPOView extends JPanel implements KeyListener {
    private int frameWidth;
    private int frameHeight;
    private JFrame frame;
    private BufferedImage mainImage;
    private Graphics2D imageGraphics;

    private PPOModel gameModel;
    PlayerModel player1;
    PlayerModel player2;
    BallModel ball;


    public PPOView(PPOModel model, int w, int h) {
        gameModel = model;
        player1 = gameModel.getPlayer1();
        player2 = gameModel.getPlayer2();
        ball = gameModel.getBall();

        frameWidth = w;
        frameHeight = h;

        mainImage = new BufferedImage(frameWidth, frameHeight, BufferedImage.TYPE_INT_RGB);
        imageGraphics = (Graphics2D) mainImage.getGraphics();

        imageGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        imageGraphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        frame = new JFrame("Ping Pong Online");
        System.out.println("Hey");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.setContentPane(this);
        frame.pack();
        frame.setResizable(false);
        frame.setSize(frameWidth, frameHeight);
        frame.setVisible(true);
        requestFocus();
        addKeyListener(this);
    }

    public void draw() {
        Graphics2D panel = (Graphics2D) this.getGraphics();
        panel.drawImage(mainImage, 0, 0, null);
        panel.dispose();
    }

    public void render() {
        renderPlayground();
        renderPlayers();
        renderBall();
    }

    private void renderPlayground() {
        imageGraphics.setColor(Color.BLACK);
        imageGraphics.fillRect(0, 0,frameWidth, frameHeight);
    }

    private void renderPlayers() {
        imageGraphics.setColor(Color.WHITE);

        imageGraphics.fillRect(player1.getX() - PlayerModel.WIDTH / 2, player1.getY() - PlayerModel.HEIGHT / 2,
                PlayerModel.WIDTH, PlayerModel.HEIGHT);

        imageGraphics.fillRect(player2.getX() - PlayerModel.WIDTH / 2, player2.getY() - PlayerModel.HEIGHT / 2,
                PlayerModel.WIDTH, PlayerModel.HEIGHT);
    }

    private void renderBall() {
        imageGraphics.setColor(Color.WHITE);

        imageGraphics.fillOval(ball.getX() - BallModel.RADIUS, ball.getY() - BallModel.RADIUS,
                BallModel.RADIUS * 2, BallModel.RADIUS * 2);
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();

        if (code == KeyEvent.VK_W) {
            player1.setDirection(PlayerModel.UP);
        }

        if (code == KeyEvent.VK_S) {
            player1.setDirection(PlayerModel.DOWN);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();

        if (code == KeyEvent.VK_S || code == KeyEvent.VK_W) {
            player1.setDirection(PlayerModel.STOP);
        }
    }
}
