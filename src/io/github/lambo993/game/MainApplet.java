package io.github.lambo993.game;

import io.github.lambo993.game.entity.*;

import java.awt.*;
import java.awt.image.BufferedImage;

import javax.swing.*;

/**
 * Applet Version of the game.
 * @author Lamboling Seans
 * @since version 1.3_Alpha
 * @serial -8906798179739115470L
 * @deprecated Repaint still bugged
 */
@Deprecated
public class MainApplet extends JApplet implements Runnable {

	private static final long serialVersionUID = -8906798179739115470L;
	Main m = new Main();
	Player p = m.getPlayer();
	private boolean isEnabled = false;

	@Override
	public void run() {
		while (isEnabled()) {
			repaint();
			try {
				Thread.sleep(5);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	@Override
	public void setEnabled(final boolean enabled) {
		if (isEnabled() != enabled) {
			isEnabled = enabled;
		}
	}

	@Override
	public boolean isEnabled() {
		return isEnabled;
	}

	@Override
	public void init() {
		setSize(800, 600);
		setEnabled(true);
		new Thread(new MainApplet()).start();
		new Thread(m).start();
	}

	@Override
	public void paint(Graphics g) {
		BufferedImage dbImg = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics2D dbg = (Graphics2D)dbImg.getGraphics();
		m.draw(dbg);
		g.drawImage(dbImg, 0, 0, this);
	}

	@Override
	public void start() {
		new Thread(p).start();
		p.setXVelocity(1);
	}
}