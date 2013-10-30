package io.github.lambo993.game;

import java.awt.*;
import javax.swing.JFrame;

public final class Main extends JFrame implements Runnable {

	private static final long serialVersionUID = 5832158247289767468L;

	public Main() {
		setSize(512, 384);
		setTitle("SpaceGame");
		setLocationRelativeTo(null);
		setVisible(true);
		setResizable(false);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
	}

	@Override
	public void paint(Graphics g) {
		g.drawImage(null, 0, 0, this);
	}

	public static void main(String[] args) {
		System.out.println("Starting SpaceGame version 0.0.0");
		Main m = new Main();
		new Thread(m).start();
	}
}
