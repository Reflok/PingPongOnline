package ppomodel;

import java.util.logging.Level;
import java.util.logging.Logger;

public class PPOModel {
    private static Logger logger = Logger.getLogger("");

    public static final int WIDTH = 600;
    public static final int HEIGHT = 500;
    public static final int FPS = 60;
    public static final int timePerFrame = 1000/FPS;

    private PlayerModel player1;
    private PlayerModel player2;
    private BallModel ball;

    private int maxScore;
    private int player1Score;
    private int player2Score;

    private boolean active;

    public PPOModel(int maxScore, int totalSpeed, int playerSpeed) {
        this.maxScore = maxScore;

        player1 = new PlayerModel(0 + PlayerModel.WIDTH / 2, playerSpeed);
        player2 = new PlayerModel(WIDTH - PlayerModel.WIDTH / 2, playerSpeed);
        ball = new BallModel(totalSpeed);
    }

    public void update() {
        player1.update();
        player2.update();
        ball.update();
    }

   /* @Override
    public void run() {
        active = true;
        long timer;
        long wait;

        while (isActive()) {
            timer = System.nanoTime();

            player1.update();
            player2.update();
            ball.update();

            wait = (timePerFrame - (System.nanoTime() - timer)/1000000);

            if (wait > 0) {
                try {
                    Thread.sleep(wait);
                } catch (InterruptedException e) {
                    logger.log(Level.SEVERE, "Interrupted", e);
                    Thread.currentThread().interrupt();
                }
            }
        }

    }*/

    public synchronized boolean isActive() {
        return active;
    }

    public synchronized void setActive(boolean active) {
        this.active = active;
    }

    public PlayerModel getPlayer1() {return player1;}
    public PlayerModel getPlayer2() {return player2;}

    public BallModel getBall() {return ball;}
}
