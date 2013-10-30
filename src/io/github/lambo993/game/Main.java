package io.github.lambo993.game;

import java.awt.*;
import java.awt.event.*;
import javax.swing.JFrame;

public final class Main extends JFrame implements Runnable {

	private static final long serialVersionUID = 5832158247289767468L;
	private int x, y, xVelocity, yVelocity;

	public Main() {
		setSize(512, 384);
		setTitle("SpaceGame");
		setLocationRelativeTo(null);
		setVisible(true);
		setResizable(false);
		setBackground(Color.WHITE);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		x = 256;
		y = 192;
		xVelocity = 0;
		yVelocity = 0;
		addKeyListener(new KeyListenerEvent());
	}

	@Override
	public void run() {
		while (true) {
			move();
			try {
				Thread.sleep(5);
			} catch (InterruptedException ex) {
				System.err.println("Error: Thread Interrupted.");
			}
		}
	}

	public void move() {
		x += xVelocity;
		y += yVelocity;
		if (x < 0) {
			x = 0;
		}
		if (y < 20) {
			y = 20;
		}
		if (x > 462) {
			x = 462;
		}
		if (y > 344) {
			y = 344;
		}
	}

	@Override
	public void paint(Graphics g) {
		Image dbImg = createImage(getWidth(), getHeight());
		Graphics dbg = dbImg.getGraphics();
		draw(dbg);
		g.drawImage(dbImg, 0, 0, this);
	}

	public void draw(Graphics g) {
		g.setColor(Color.BLACK);
		g.fillRect(x, y, 69, 42);
		repaint();
	}

	public static void main(String[] args) {
		System.out.println("Starting SpaceGame version 0.0.1");
		Main m = new Main();
		new Thread(m).start();
	}

	private class KeyListenerEvent extends KeyAdapter {

		@Override
		public void keyPressed(KeyEvent e) {
			switch (e.getKeyCode()) {
				case KeyEvent.VK_UP:
				case KeyEvent.VK_W:
					yVelocity = -2;
					break;
				case KeyEvent.VK_DOWN:
				case KeyEvent.VK_S:
					yVelocity = 2;
					break;
				case KeyEvent.VK_LEFT:
				case KeyEvent.VK_A:
					xVelocity = -2;
					break;
				case KeyEvent.VK_RIGHT:
				case KeyEvent.VK_D:
					xVelocity = 2;
					break;
				default:
					break;
			}
		}

		@Override
		public void keyReleased(KeyEvent e) {
			switch (e.getKeyCode()) {
				case KeyEvent.VK_UP:
				case KeyEvent.VK_W:
				case KeyEvent.VK_DOWN:
				case KeyEvent.VK_S:
					yVelocity = 0;
					break;
				case KeyEvent.VK_LEFT:
				case KeyEvent.VK_A:
				case KeyEvent.VK_RIGHT:
				case KeyEvent.VK_D:
					xVelocity = 0;
					break;
				default:
					break;
			}
		}
	}
}