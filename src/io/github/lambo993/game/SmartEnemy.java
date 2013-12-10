package io.github.lambo993.game;

/**
 * Same as enemy but follow the player movements
 * @author Lamboling Seans
 * @since version 1.7.9_Alpha
 */
public class SmartEnemy extends Enemy {

	private Player player;

	public SmartEnemy(Player player) {
		setX(400);
		setY(50);
		this.player = player;
	}

	@Override
	public void run() {
		while (true) {
			int x = player.getX() + 30;
			int y = player.getY() - 3;
			if (getX() == x && player.isAlive()) {
				setXVelocity(0);
			}
			if (getY() == y && player.isAlive()) {
				setYVelocity(0);
			}
			if (player.isAlive()) {
				move(x, y, x, y);
			} else {
				move(0, 5, 790, 590);
			}
			try {
				Thread.sleep(5);
			} catch (InterruptedException ex) {
				System.err.println("Error: Thread Interrupted.");
			}
		}
	}
}