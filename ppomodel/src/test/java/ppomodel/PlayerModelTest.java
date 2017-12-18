package ppomodel;

import org.junit.Test;

import static org.junit.Assert.*;

public class PlayerModelTest {
    @Test
    public void update() throws Exception {
        PlayerModel player = new PlayerModel(10, 10);
        player.update();
        assertEquals(PPOModel.HEIGHT / 2, (long) player.getY());
    }

    @Test
    public void setDirection() throws Exception {
        PlayerModel player = new PlayerModel(10, 10);
        player.setDirection(PlayerModel.DOWN);
        player.update();
        assertTrue(player.getY() > PPOModel.HEIGHT / 2);

    }

    @Test
    public void reset() throws Exception {
        PlayerModel player = new PlayerModel(10, 10);
        player.setDirection(PlayerModel.DOWN);
        player.update();
        player.reset();
        assertEquals(PPOModel.HEIGHT / 2, (long) player.getY());
    }
}