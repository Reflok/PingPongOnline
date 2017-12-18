package org.suai.ppoclient;

import org.suai.ppoview.PPOGameView;
import org.suai.ppoview.PPOMenuView;
import ppomodel.PPOModel;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class PPOGame implements Runnable, WindowListener {
    private PPOMenuView menu;
    private Connector connector;
    private ViewUpdater viewUpdater;
    private ModelUpdater modelUpdater;


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
        //unused
    }

    @Override
    public void windowClosing(WindowEvent e) {
        //unused
    }

    @Override
    public void windowClosed(WindowEvent e) {
        modelUpdater.setActive(false);
        viewUpdater.setActive(false);
        menu.setVisible(true);
    }

    @Override
    public void windowIconified(WindowEvent e) {
        //unused
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
        //unused
    }

    @Override
    public void windowActivated(WindowEvent e) {
        //unused
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
        //unused
    }
}
