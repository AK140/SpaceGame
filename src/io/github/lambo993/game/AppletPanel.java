package io.github.lambo993.game;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.*;

public class AppletPanel extends JPanel {

	private static final long serialVersionUID = -8808146090462409157L;
	private final Main m;

	public AppletPanel() {
		m = new Main();
		setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
		addKeyListener(m.new KeyListenerEvent());
		addMouseListener(m.new MouseListenerEvent());
	}

	@Override
	public void paint(Graphics g) {
		BufferedImage img = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics2D dbg = img.createGraphics();
		draw(dbg);
		g.drawImage(img, 0, 0, this);
	}

	public void draw(final Graphics2D g) {
		m.draw(g);
		repaint();
	}
}