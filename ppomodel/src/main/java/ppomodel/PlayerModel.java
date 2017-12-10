package ppomodel;

public class PlayerModel implements PPOGameObject {
    public static final int WIDTH = 10;
    public static final int HEIGHT = 50;

    public static final int UP = -1;
    public static final int STOP = 0;
    public static final int DOWN = 1;

    private int x;
    private int y;
    private int speed;

    private int direction;// >0 - down, =0 stop, <0 up

    public PlayerModel(int x, int speed) {
        this.x = x;
        y = PPOModel.HEIGHT / 2;
        direction = STOP;
        this.speed = speed;
    }

    public void update() {
        if (direction != STOP) {
            y += speed * direction/Math.abs(direction);
        }
    }

    public synchronized int getDirection() {
        return direction;
    }

    public synchronized void setDirection(int dir) {
        this.direction = dir;
    }

    public synchronized int getX() {
        return x;
    }

    public synchronized void setX(int x) {
        this.x = x;
    }

    public synchronized int getY() {
        return y;
    }

    @Override
    public void setVspeed(int newval) {
        //vspeed = newval;
    }

    @Override
    public void setHspeed(int newval) {
        speed = newval;
    }

    public synchronized void setY(int y) {
        this.y = y;
    }
}
