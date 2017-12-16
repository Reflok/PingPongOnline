package ppomodel;

public interface PPOGameObject {
    public double getX();
    public double getY();

    public void setX(double newval);
    public void setY(double newval);

    public void setVspeed(double newval);
    public void setHspeed(double newval);
}
