package io.github.lambo993.game;

import java.awt.*;
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
		//TODO: Make the ship moving
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
		g.fillRect(x, y, 20, 20);
		repaint();
	}

	public static void main(String[] args) {
		System.out.println("Starting SpaceGame version 0.0.0");
		Main m = new Main();
		new Thread(m).start();
	}
}