package ppomodel;

import org.junit.Test;

import static org.junit.Assert.*;

public class PPOModelTest {
    @Test
    public void start() throws Exception {
        PPOModel model = new PPOModel(5, 6, 4,"Test");
        model.start();
        assertEquals(model.getState(), PPOModel.STATE_PLAY);
    }

    @Test
    public void update() throws Exception {
        PPOModel model = new PPOModel(5, 6, 4,"Test");
        model.start();
        model.update();
        assertNotEquals(PPOModel.WIDTH / 2, model.getBall().getX());
    }

    @Test
    public void getPlayer() throws Exception {
        PPOModel model = new PPOModel(5, 6, 4,"Test");
        assertEquals((long) PPOModel.HEIGHT / 2, (long) model.getPlayer2().getY());
    }

}