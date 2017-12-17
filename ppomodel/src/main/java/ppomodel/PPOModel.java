package ppomodel;

import java.util.logging.Logger;

public class PPOModel {
    private static Logger logger = Logger.getLogger("");

    public static final int WIDTH = 700;
    public static final int HEIGHT = 500;
    public static final int FPS = 60;
    public static final int timePerFrame = 1000/FPS;

    public static final int PLAYER_1 = 1;
    public static final int PLAYER_2 = 2;
    public static final int GAME_BALL = 0;

    public static final int STATE_WAIT = 0;
    public static final int STATE_PLAY = 1;
    public static final int STATE_START1 = 2;
    public static final int STATE_START2 = 3;
    public static final int STATE_WIN1 = 4;
    public static final int STATE_WIN2 = 5;


    private PlayerModel player1;
    private PlayerModel player2;
    private BallModel ball;

    private int maxScore;
    private int player1Score = 0;
    private int player2Score = 0;

    private String name1;
    private String name2;

    private boolean active;
    private long timer;

    private int state = STATE_WAIT;


    public PPOModel(int maxScore, int totalSpeed, int playerSpeed, String name) {
        this.maxScore = maxScore;

        player1 = new PlayerModel(0 + PlayerModel.WIDTH / 2, playerSpeed);
        player2 = new PlayerModel(WIDTH - PlayerModel.WIDTH / 2, playerSpeed);
        ball = new BallModel(totalSpeed);
        name1 = name;
        name2 = "Awaiting connection";

    }

    public void update() {
        if (!(state == STATE_PLAY)) {
            return;
        }

        player1.update();
        player2.update();
        ball.update();
        int ballX = (int) ball.getX();
        int ballY = (int) ball.getY();

        if (ballX < BallModel.RADIUS + PlayerModel.WIDTH) {
            if (ballY - BallModel.RADIUS <= player1.getY() + (double)PlayerModel.HEIGHT / 2 &&
                    ballY + BallModel.RADIUS >= player1.getY() - (double)PlayerModel.HEIGHT / 2) {
                ball.setX(BallModel.RADIUS + (double)PlayerModel.WIDTH);
                double diff = (ballY - player1.getY()) / ((double)PlayerModel.HEIGHT / 2);

                ball.setAngle(diff * 60);

            } else {
                score(PLAYER_2);
            }
        }

        if (ballX > PPOModel.WIDTH - BallModel.RADIUS - PlayerModel.WIDTH) {
            if (ballY - BallModel.RADIUS <= player2.getY() + (double)PlayerModel.HEIGHT / 2 &&
                    ballY + BallModel.RADIUS >= player2.getY() - (double)PlayerModel.HEIGHT / 2) {
                ball.setX(PPOModel.WIDTH - BallModel.RADIUS - PlayerModel.WIDTH);
                double diff = (ballY - player2.getY()) / ((double)PlayerModel.HEIGHT / 2);

                //ball.setAngle((int)(ball.getAngle() + 15*diff/Math.abs(90 - ball.getAngle())), true );
                ball.setAngle(diff * 60);
                ball.setHspeed(-ball.getHspeed());

            } else {
                score(PLAYER_1);
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

    public void start() {
        state = STATE_PLAY;
        ball.start();
    }

    public void score(int playerNum) {
        if (playerNum == PLAYER_1) {
            player1Score++;
            state = STATE_START1;
            ball.reset();

            if (player1Score > maxScore) {
                state = STATE_WIN1;
            }
        } else if(playerNum == PLAYER_2) {
            player2Score++;
            state = STATE_START2;
            ball.reset();

            if (player1Score > maxScore) {
                state = STATE_WIN2;
            }
        }

        timer = System.currentTimeMillis();
    }



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

    public int getPlayer1Score() {
        return player1Score;
    }

    public void setPlayer1Score(int player1Score) {
        this.player1Score = player1Score;
    }

    public int getPlayer2Score() {
        return player2Score;
    }

    public void setPlayer2Score(int player2Score) {
        this.player2Score = player2Score;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}
