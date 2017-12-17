package org.suai.ppoclient;

import org.suai.ppoview.PPOGameView;
import org.suai.ppoview.PPOMenuView;
import ppomodel.PPOModel;

public class ModelUpdater implements Runnable {
    private Connector connector;
    private PPOModel model;
    private PPOGameView view;
    private boolean active = true;

    public ModelUpdater(Connector connector, PPOModel model, PPOGameView view) {
        this.connector = connector;
        this.view = view;
        this.model = model;
    }

    @Override
    public void run() {

        while (isActive()) {
            String input = connector.receive();

            if (input.equals("END")) {
                view.dispose();
                return;
            }

            String[] data = input.split(":");

            model.getBall().setX(Integer.parseInt(data[0]));
            model.getBall().setY(Integer.parseInt(data[1]));

            model.getPlayer1().setX(Integer.parseInt(data[2]));
            model.getPlayer1().setY(Integer.parseInt(data[3]));

            model.getPlayer2().setX(Integer.parseInt(data[4]));
            model.getPlayer2().setY(Integer.parseInt(data[5]));

            model.setName1(data[6]);
            model.setName2(data[7]);

            model.setPlayer1Score(Integer.parseInt(data[8]));
            model.setPlayer2Score(Integer.parseInt(data[9]));
        }
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
