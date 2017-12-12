package ppomodel;

import java.util.logging.Logger;

public class PPOModel {
    private static Logger logger = Logger.getLogger("");

    public static final int WIDTH = 600;
    public static final int HEIGHT = 500;
    public static final int FPS = 60;
    public static final int timePerFrame = 1000/FPS;

    public static final int PLAYER_1 = 1;
    public static final int PLAYER_2 = 2;
    public static final int GAME_BALL = 0;

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
        int ballX = ball.getX();
        int ballY = ball.getY();

        if (ballX < BallModel.RADIUS + PlayerModel.WIDTH) {
            if (ballY - BallModel.RADIUS <= player1.getY() + PlayerModel.HEIGHT / 2 &&
                    ballY + BallModel.RADIUS >= player1.getY() - PlayerModel.HEIGHT / 2) {
                ball.setX(BallModel.RADIUS + PlayerModel.WIDTH);
                ball.setHspeed(-ball.getHspeed());

            } else {
                ball.reset();
            }
        }

        if (ballX > PPOModel.WIDTH - BallModel.RADIUS - PlayerModel.WIDTH) {
            if (ballY - BallModel.RADIUS <= player2.getY() + PlayerModel.HEIGHT / 2 &&
                    ballY + BallModel.RADIUS >= player2.getY() - PlayerModel.HEIGHT / 2) {
                ball.setX(PPOModel.WIDTH - BallModel.RADIUS - PlayerModel.WIDTH);
                ball.setHspeed(-ball.getHspeed());

            } else {
                ball.reset();
                ball.setHspeed(-ball.getHspeed());
            }
            /*if (ballX > PPOModel.WIDTH - RADIUS) {
                ball.reset();
            } else {
                ball.setX(PPOModel.WIDTH - RADIUS - PlayerModel.WIDTH);
                ball.setVspeed(-ball.getVspeed());
            }*/
        }

        if (ballY < BallModel.RADIUS) {
            ball.setY(BallModel.RADIUS);
            ball.setVspeed(-ball.getVspeed());
        }

        if (ballY > PPOModel.HEIGHT - BallModel.RADIUS) {
            ball.setY(PPOModel.HEIGHT - BallModel.RADIUS);
            ball.setVspeed(-ball.getVspeed());
        }
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

    public synchronized PlayerModel getPlayer1() {return player1;}
    public synchronized PlayerModel getPlayer2() {return player2;}
    public synchronized BallModel getBall() {return ball;}

    public synchronized PPOGameObject getEntity(int num) {
        if (num == PLAYER_1) {
            return player1;
        } else if (num == PLAYER_2) {
            return player2;
        } else if(num == GAME_BALL) {
            return ball;
        }

        return null;
    }

    public synchronized PlayerModel getPlayer(int num) {
        if (num > 0 && num < 3) {
            return (PlayerModel) getEntity(num);
        }

        return null;
    }
}
