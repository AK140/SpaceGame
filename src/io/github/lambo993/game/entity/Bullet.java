package io.github.lambo993.game.entity;

import io.github.lambo993.game.Main;
import java.awt.*;

/**
 * The <code>Bullet</code> Entity
 * @author Lamboling Seans
 * @since version 0.5_Alpha
 */
public class Bullet extends Entity {

	public Bullet(Player player) {
		this(player.getX() + 30, player.getY() - 12);
	}

	public Bullet(int xCoord, int yCoord) {
		setX(xCoord);
		setY(yCoord);
		setYVelocity(-4);
	}

	@Override
	public void run() {
		while (true) {
			if (!isPaused()) move(0, 0, 0, 0);
			try {
				Main.sleep();
			} catch (InterruptedException ex) {
				Main.LOGGER.warning("Error: Thread Interrupted.");
			}
		}
	}

	@Override
	public void move(int xMin, int yMin, int xMax, int yMax) {
		setY(getY() + getYVelocity());
	}

	/**
	 * Removes the bullet when it goes off the screen limit.
	 * @author Lamboling Seans
	 * @return screen top limit y 0
	 * @since version 0.5_Alpha
	 */
	public boolean isOffScreen() {
		return getY() < 0 || getY() > 560;
	}

	@Override
	public void draw(Graphics2D g) {
		g.drawImage(Main.loadImage("Bullet.gif"), getX(), getY(), new Main());
	}

	@Override
	public Rectangle getHitbox() {
		return new Rectangle(getX(), getY() + 3, 10, 21);
	}

	@Override
	public EntityType getType() {
		return EntityType.BULLET;
	}

	@Override
	public String toString() {
		return getType().getName();
	}
}