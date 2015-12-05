package io.github.ak140.game.entity;

import io.github.ak140.engine.*;
import io.github.ak140.game.*;
import java.awt.*;
import java.util.Random;

/**
 * An interface for Entity methods
 * @author Lambo993
 * @since version 1.4_Alpha
 */
public abstract class Entity extends EntityBase implements Runnable {

	protected Random r = new Random();

	/**
	 * Moves the <code>Entity</code> and limit the <code>Entity</code> movement on the screen.
	 * @param xMin Minimum screen limit from the x cord
	 * @param yMin Minimum screen limit from the y cord
	 * @param xMax Maximum screen limit from the x cord
	 * @param yMax Maximum screen limit from the y cord
	 * @since version 0.3_Alpha
	 */
	public void move(int xMin, int yMin, int xMax, int yMax) {
		if (!isPaused()) {
			setX(getX() + getXVelocity());
			setY(getY() + getYVelocity());
			if (getX() < xMin) {
				setXVelocity(1);
			}
			if (getX() > xMax) {
				setXVelocity(-1);
			}
			if (getY() < yMin) {
				setYVelocity(1);
			}
			if (getY() > yMax) {
				setYVelocity(-1);
			}
		}
	}

	public boolean isPaused() {
		return Main.isPaused();
	}

	public abstract void draw(Graphics2D g);

	/**
	 * Get's the size of the hit box
	 * @return the Rectangle Size
	 * @since version 0.7_Alpha
	 */
	public abstract Rectangle getHitbox();

	public EntityType getType() {
		return EntityType.UNKNOWN;
	}
}