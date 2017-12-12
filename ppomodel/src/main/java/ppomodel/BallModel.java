package ppomodel;

public class BallModel implements PPOGameObject {
    public static final int RADIUS = 10;

    private int x;
    private int y;

    private int vspeed;
    private int hspeed;
    private int totalSpeed;

    public BallModel(int totalSpeed){
        reset();

        this.totalSpeed = totalSpeed;
    }

    public void update() {
        y += vspeed;
        x += hspeed;


    }

    public void reset() {
        x = PPOModel.WIDTH / 2;
        y = PPOModel.HEIGHT / 2;

        vspeed = 4;
        hspeed = -3;
    }

    public synchronized int getX() {
        return x;
    }

    public synchronized void setX(int xPos) {
        this.x = xPos;
    }

    public synchronized int getY() {
        return y;
    }

    public synchronized void setY(int yPos) {
        this.y = yPos;
    }

    public void generateDirection(boolean leftOrRight) {
        if (leftOrRight) {

        }
    }

    public void setVspeed(int newval) {vspeed = newval;}
    public void setHspeed(int newval) {hspeed = newval;}

    public int getVspeed() {return vspeed;}
    public int getHspeed() {return hspeed;}
}
