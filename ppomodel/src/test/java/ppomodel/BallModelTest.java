package ppomodel;

import org.junit.Test;

import static org.junit.Assert.*;

public class BallModelTest {



    @Test
    public void start() throws Exception {
        BallModel ball = new BallModel(5);
        ball.start();
        assertTrue(ball.getHspeed() < 0);
    }

    @Test
    public void update() throws Exception {
        BallModel ball = new BallModel(5);
        ball.start();
        ball.update();
        assertTrue(ball.getX() != PPOModel.WIDTH / 2);
    }

    @Test
    public void reset() throws Exception {
        BallModel ball = new BallModel(5);
        ball.start();
        ball.update();
        ball.reset();
        assertTrue(ball.getX() == PPOModel.WIDTH / 2);
    }


}