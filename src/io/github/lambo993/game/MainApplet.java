package io.github.lambo993.game;

import javax.swing.*;

/**
 * Applet Version of the game.
 * @author Lamboling Seans
 * @since version 1.3_Alpha
 * @serial -8906798179739115470L
 */
public class MainApplet extends JApplet {

	private static final long serialVersionUID = -8906798179739115470L;

	@Override
	public void start() {
		Main m = new Main("Space Catastrophe Applet", true); //TODO: Improve Applet
		new Thread(m).start();
		do {
			m.spawnEnemy(15);
			m.spawnPowers(1);
			try {
				Thread.sleep(3 * 1000);
			} catch (InterruptedException ex) {
				System.err.println("Error: Thread Interrupted.");
			}
		} while (m.isEnabled());
	}
}