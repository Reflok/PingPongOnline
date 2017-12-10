package ppomodel;

public class BallModel {
    public static final int RADIUS = 10;

    private int x;
    private int y;

    private int vspeed;
    private int hspeed;
    private int totalSpeed;

    public BallModel(int totalSpeed) {
        x = PPOModel.WIDTH / 2;
        y = PPOModel.HEIGHT / 2;

        vspeed = 5;
        hspeed = 0;

        this.totalSpeed = totalSpeed;
    }

    public void update() {
        x += vspeed;
        y += hspeed;

        if (x < RADIUS) {
            x = RADIUS;
            vspeed = -vspeed;
        }

        if (x > PPOModel.WIDTH - RADIUS) {
            x = PPOModel.WIDTH - RADIUS;
            vspeed = -vspeed;
        }

        if (y < RADIUS) {
            y = RADIUS;
            hspeed = -hspeed;
        }

        if (y > PPOModel.HEIGHT - RADIUS) {
            y = PPOModel.HEIGHT - RADIUS;
            hspeed = -hspeed;
        }
    }

    public int getX() {
        return x;
    }

    public void setX(int xPos) {
        this.x = xPos;
    }

    public int getY() {
        return y;
    }

    public void setY(int yPos) {
        this.y = yPos;
    }

    public void generateDirection(boolean leftOrRight) {
        if (leftOrRight) {

        }
    }
}
