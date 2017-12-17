package org.suai.ppoclient;

import org.suai.ppoview.PPOGameView;
import org.suai.ppoview.PPOMenuView;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;

public class ViewUpdater implements Runnable, KeyListener {
    private PPOGameView view;
    private Connector connector;
    private boolean active;

    public ViewUpdater(PPOGameView view, Connector connector) {
        this.view = view;
        this.connector = connector;
        active = true;
    }

    @Override
    public void run() {
        long timer = System.currentTimeMillis();

        while (isActive()) {
            long wait = 1000/200 - (System.currentTimeMillis() - timer);

            if (wait > 0) {
                System.out.println("OK");
                try {
                    Thread.sleep(wait);


                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (NullPointerException e) {
                    return;
                }
            }
            try {
                if (isActive()) {
                    view.render();
                    view.draw();
                }
            } catch (NullPointerException e) {
                return;
            }

        }
    }

    public synchronized boolean isActive() {
        return active;
    }

    public synchronized void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_W) {
            connector.send("UP");
        } else if (e.getKeyCode() == KeyEvent.VK_S) {
            connector.send("DOWN");
        } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            setActive(false);
            view.dispose();

        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_W) {
            connector.send("STOP");
        } else if (e.getKeyCode() == KeyEvent.VK_S) {
            connector.send("STOP");
        }
    }
}
