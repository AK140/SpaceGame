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
		new Thread(new Main("SuperSpaceShooter Applet", true)).start();
	}
}