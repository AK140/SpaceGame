package io.github.lambo993.game.entity;

import java.awt.*;

/**
 * An interface for Entity methods
 * @author Lamboling Seans
 * @since version 1.4_Alpha
 */
public abstract class Entity implements Runnable {

	private boolean isPaused = false;
	private int xVelocity , yVelocity;

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
	 * Gets the <code>Entity</code> X Location of the screen
	 * @return the X Location of the screen
	 */
	public abstract int getX();

	public int getXVelocity() {
		return xVelocity;
	}

	/**
	 * Gets the <code>Entity</code> Y Location of the screen
	 * @return the Y Location of the screen
	 */
	public abstract int getY();

	public int getYVelocity() {
		return yVelocity;
	}

	/**
	 * Sets the <code>Entity</code> X coordinate of the screen
	 * @param x the new X Location of the screen
	 */
	public abstract void setX(int x);

	public void setXVelocity(int xVelocity) {
		this.xVelocity = xVelocity;
	}

	/**
	 * Sets the <code>Entity</code> Y coordinate of the screen
	 * @param y the new Y Location of the screen
	 */
	public abstract void setY(int y);

	public void setYVelocity(int yVelocity) {
		this.yVelocity = yVelocity;
	}

	public boolean isPaused() {
		return isPaused;
	}

	public void setPaused(boolean paused) {
		isPaused = paused;
	}

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