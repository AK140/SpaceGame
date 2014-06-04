package io.github.lambo993.game;

import javax.swing.*;
import java.awt.BorderLayout;

/**
 * Applet Version of the game.
 * @author Lamboling Seans
 * @since version 1.3_Alpha
 * @serial -8906798179739115470L
 * @deprecated Keyboard input broken
 */
@Deprecated
public class MainApplet extends JApplet implements Runnable {

	private static final long serialVersionUID = -8906798179739115470L;
	private AppletPanel panel;

	public MainApplet() {
		panel = new AppletPanel();
		new Thread(panel, "Spawn").start();
		getContentPane().add(panel, BorderLayout.CENTER);
	}

	@Override
	public void init() {
		setSize(800, 600);
		new Thread(this).start();
	}

	@Override
	public void run() {
		while (panel.isEnabled()) {
			if (!AppletPanel.isPaused()) {
				panel.spawnEnemy(15);
				panel.spawnPowers(1);
			}
			try {
				Thread.sleep(3000);
			} catch (InterruptedException ex) {
				System.err.println("Error: Thread interrupted.");
			}
		}
	}
}