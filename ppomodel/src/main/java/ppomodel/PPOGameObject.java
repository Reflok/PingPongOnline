package ppomodel;

public interface PPOGameObject {
    public int getX();
    public int getY();

    public void setX(int newval);
    public void setY(int newval);

    public void setVspeed(int newval);
    public void setHspeed(int newval);
}
