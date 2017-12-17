package org.suai.ppoclient;

import org.suai.ppoview.PPOGameView;
import org.suai.ppoview.PPOMenuView;
import ppomodel.PPOModel;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class PPOGame implements Runnable, WindowListener {
    PPOMenuView menu;
    Connector connector;
    ViewUpdater viewUpdater;
    ModelUpdater modelUpdater;


    public PPOGame(PPOMenuView menu, Connector connector) {
        this.menu = menu;
        this.connector = connector;
    }

    @Override
    public void run() {
        PPOModel model = new PPOModel(5, 5, 3, "placeholder");
        PPOGameView view = new PPOGameView(model, PPOModel.WIDTH, PPOModel.HEIGHT);
        viewUpdater = new ViewUpdater(view, connector);
        modelUpdater = new ModelUpdater(connector, model, view);

        view.addKeyListener(viewUpdater);
        view.addWindowListener(this);

        new Thread(viewUpdater).start();
        new Thread(modelUpdater).start();
        view.requestFocus();
    }

    @Override
    public void windowOpened(WindowEvent e) {

    }

    @Override
    public void windowClosing(WindowEvent e) {

    }

    @Override
    public void windowClosed(WindowEvent e) {
        modelUpdater.setActive(false);
        viewUpdater.setActive(false);
        menu.setVisible(true);
        //connector.send("END");
    }

    @Override
    public void windowIconified(WindowEvent e) {

    }

    @Override
    public void windowDeiconified(WindowEvent e) {

    }

    @Override
    public void windowActivated(WindowEvent e) {

    }

    @Override
    public void windowDeactivated(WindowEvent e) {

    }
}
