package ppomodel;

public interface PPOGameObject {
    double getX();
    double getY();

    void setX(double newval);
    void setY(double newval);

    void setVspeed(double newval);
    void setHspeed(double newval);
}
