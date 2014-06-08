package io.github.lambo993.game.entity;

import io.github.lambo993.engine.*;
import java.awt.*;

/**
 * An interface for Entity methods
 * @author Lambo993
 * @since version 1.4_Alpha
 */
public abstract class Entity extends EntityBase implements Runnable {

	/**
	 * Moves the <code>Entity</code> and limit the <code>Entity</code> movement on the screen.
	 * @param xMin Minimum screen limit from the x coord
	 * @param yMin Minimum screen limit from the y coord
	 * @param xMax Maximum screen limit from the x coord
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