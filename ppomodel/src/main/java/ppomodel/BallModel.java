package ppomodel;

import java.util.Random;

public class BallModel implements PPOGameObject {
    public static final int RADIUS = 10;

    private double x;
    private double y;

    private double vspeed;
    private double hspeed;
    private int totalSpeed;
    private double angle;
    private Random gen = new Random(System.currentTimeMillis());

    public BallModel(int totalSpeed){
        this.totalSpeed = totalSpeed;
        reset();

    }

    public void update() {
        y += vspeed;
        x += hspeed;


    }

    public void reset() {
        x = PPOModel.WIDTH / 2;
        y = PPOModel.HEIGHT / 2;
        angle = -45 + gen.nextInt(90);

        vspeed = totalSpeed * Math.sin(Math.toRadians(angle));
        hspeed = -totalSpeed * Math.cos(Math.toRadians(angle));
    }

    public synchronized double getX() {
        return x;
    }

    public synchronized void setX(double xPos) {
        this.x = xPos;
    }

    public synchronized double getY() {
        return y;
    }

    public synchronized void setY(double yPos) {
        this.y = yPos;
    }

    public void generateDirection(boolean leftOrRight) {
        if (leftOrRight) {

        }
    }

    public void setVspeed(double newval) {vspeed = newval;}
    public void setHspeed(double newval) {hspeed = newval;}

    public double getVspeed() {return vspeed;}
    public double getHspeed() {return hspeed;}

    public double getAngle() {return angle;}
    public void setAngle(double newval) {
        angle = newval;

        vspeed = totalSpeed * Math.sin(Math.toRadians(angle));
        hspeed = totalSpeed * Math.cos(Math.toRadians(angle));
    }
}
