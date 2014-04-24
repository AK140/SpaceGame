package io.github.lambo993.game;

import javax.swing.*;
import java.awt.BorderLayout;

/**
 * Applet Version of the game.
 * @author Lamboling Seans
 * @since version 1.3_Alpha
 * @serial -8906798179739115470L
 * @deprecated Repaint still bugged
 */
@Deprecated
public class MainApplet extends JApplet {

	private static final long serialVersionUID = -8906798179739115470L;

	public MainApplet() {
		AppletPanel panel = new AppletPanel();
		getContentPane().add(panel, BorderLayout.CENTER);
	}

	@Override
	public void init() {
		setSize(800, 600);
	}
}