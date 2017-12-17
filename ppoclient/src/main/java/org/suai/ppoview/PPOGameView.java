package org.suai.ppoview;

import ppomodel.BallModel;
import ppomodel.PPOModel;
import ppomodel.PlayerModel;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class PPOGameView extends JFrame {
    private int frameWidth;
    private int frameHeight;
    private JFrame frame;
    private JPanel contentPane;
    private BufferedImage mainImage;
    private Graphics2D imageGraphics;

    private PPOModel gameModel;
    private PlayerModel player1;
    private PlayerModel player2;
    private BallModel ball;

    private String name1;
    private String name2;


    public PPOGameView(PPOModel model, int w, int h) {
        super("Ping Pong Online");
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

        //frame = new JFrame("Ping Pong Online");
        contentPane = (JPanel) getContentPane();
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setFocusable(true);
        setContentPane(contentPane);
        pack();
        setResizable(false);
        setSize(frameWidth, frameHeight);
        setVisible(true);
        requestFocus();
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

        int len1 = (int) imageGraphics.getFontMetrics().getStringBounds(gameModel.getName2(), imageGraphics).getWidth();
        int len2 = (int) imageGraphics.getFontMetrics().getStringBounds(Integer.toString(gameModel.getPlayer2Score()),
                imageGraphics).getWidth();

        imageGraphics.setColor(Color.WHITE);

        imageGraphics.drawString(gameModel.getName1(), 20, PPOModel.HEIGHT - 20);
        imageGraphics.drawString(gameModel.getName2(), PPOModel.WIDTH - len1 - 20, PPOModel.HEIGHT - 20);

        imageGraphics.drawString(Integer.toString(gameModel.getPlayer1Score()), 20, PPOModel.HEIGHT - 50);
        imageGraphics.drawString(Integer.toString(gameModel.getPlayer2Score()), PPOModel.WIDTH - len2 - 20,
                PPOModel.HEIGHT - 50);
    }

    private void renderPlayers() {
        imageGraphics.setColor(Color.WHITE);

        imageGraphics.fillRect((int) player1.getX() - PlayerModel.WIDTH / 2, (int) player1.getY() - PlayerModel.HEIGHT / 2,
                PlayerModel.WIDTH, PlayerModel.HEIGHT);

        imageGraphics.fillRect((int) player2.getX() - PlayerModel.WIDTH / 2, (int) player2.getY() - PlayerModel.HEIGHT / 2,
                PlayerModel.WIDTH, PlayerModel.HEIGHT);
    }

    private void renderBall() {
        imageGraphics.setColor(Color.WHITE);

        imageGraphics.fillOval((int) ball.getX() - BallModel.RADIUS, (int) ball.getY() - BallModel.RADIUS,
                BallModel.RADIUS * 2, BallModel.RADIUS * 2);
    }


    public String getName1() {
        return name1;
    }

    public void setName1(String name1) {
        this.name1 = name1;
    }

    public String getName2() {
        return name2;
    }

    public void setName2(String name2) {
        this.name2 = name2;
    }
}
