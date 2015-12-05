package io.github.ak140.game.entity;

import io.github.ak140.game.*;
import java.awt.*;

/**
 * The <code>Bullet</code> Entity
 * @author AK140
 * @since version 0.5_Alpha
 */
public class Bullet extends Entity {

	/**
	 * Initialize a new bullet from a <code>Player</code>
	 * @param player The player the bullet will be shot
	 */
	public Bullet(Player player) {
		this(player.getX() + 31, player.getY() - 12);
	}

	/**
	 * Initialize a new bullet
	 * @param xCoord The X coord the bullet will spawn
	 * @param yCoord The Y coord the bullet will spawn
	 */
	public Bullet(int xCoord, int yCoord) {
		if (!isPaused()) {
			setX(xCoord);
			setY(yCoord);
			setYVelocity(-4);
			Main.playSound("bullet.wav");
		}
	}

	@Override
	public void run() {
		while (true) {
			if (!isPaused()) move(0, -9, 790, 565);
			Main.sleep();
		}
	}

	/**
	 * Removes the bullet when it goes off the screen limit.
	 * @return true if at offscreen bottom and above
	 * @since version 0.5_Alpha
	 */
	public boolean isOffScreen() {
		return getY() < 0 || getY() > 565;
	}

	@Override
	public void draw(Graphics2D g) {
		g.drawImage(Main.loadImage("Bullet.png"), getX(), getY(), new Main());
	}

	@Override
	public Rectangle getHitbox() {
		return new Rectangle(getX() - 1, getY(), 9, 17);
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