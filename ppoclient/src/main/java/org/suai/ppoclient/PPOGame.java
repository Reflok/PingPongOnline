package org.suai.ppoclient;

import org.suai.ppoview.PPOGameView;
import org.suai.ppoview.PPOMenuView;
import ppomodel.PPOModel;

public class PPOGame implements Runnable {
    PPOMenuView menu;
    Connector connector;

    public PPOGame(PPOMenuView menu, Connector connector) {
        this.menu = menu;
        this.connector = connector;
    }

    @Override
    public void run() {
        PPOModel model = new PPOModel(5, 5, 3, "placeholder");
        PPOGameView view = new PPOGameView(model, PPOModel.WIDTH, PPOModel.HEIGHT);
        ViewUpdater updater = new ViewUpdater(view, connector);
        ModelUpdater modelUpdater = new ModelUpdater(connector, model);

        view.addKeyListener(updater);
        new Thread(updater).start();
        new Thread(modelUpdater).start();
        view.requestFocus();
    }
}
