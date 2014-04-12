package io.github.lambo993.game;

import io.github.lambo993.game.entity.*;
import java.awt.*;
import javax.swing.*;

/**
 * Applet Version of the game.
 * @author Lamboling Seans
 * @since version 1.3_Alpha
 * @serial -8906798179739115470L
 */
public class MainApplet extends JApplet implements Runnable {

	private static final long serialVersionUID = -8906798179739115470L;
	Main m = new Main();
	Player p = m.getPlayer();

	@Override
	public void run() {
		while (true) {
			setVisible(true);
			try {
				Thread.sleep(5);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	@Override
	public void init() {
		setSize(800, 600);
		new Thread(new MainApplet()).start();
	}

	@Override
	public void paint(Graphics g) {
		Image dbImg = createImage(getWidth(), getHeight());
		Graphics dbg = dbImg.getGraphics();
		draw(dbg);
		g.drawImage(dbImg, 0, 0, this);
	}

	public void draw(Graphics g) {
		m.draw(g);
	}

	@Override
	public void start() {
		new Thread(p).start();
		p.setXVelocity(1);
	}
}