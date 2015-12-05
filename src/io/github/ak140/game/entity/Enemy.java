package io.github.ak140.game.entity;

import io.github.ak140.game.*;
import java.awt.*;

/**
 * The <code>Enemy</code> Entity
 * @author Lamboling Seans
 * @since version 0.7_Alpha
 */
public class Enemy extends Entity {

	private boolean isIgnoring = false;

	public Enemy() {
		setX(r.nextInt(580));
		setY(50);
		setXVelocity(-1 + r.nextInt(3));
		setYVelocity(-1 + r.nextInt(3));
		if (getXVelocity() == 0 && getYVelocity() == 0) {
			setXVelocity(1);
			setYVelocity(1);
		}
	}

	@Override
	public void run() {
		while (true) {
			move(0, 5, 790, 590);
			Main.sleep();
		}
	}

	@Override
	public void draw(Graphics2D g) {
		g.setColor(Color.ORANGE);
		g.fillOval(getX(), getY(), 11, 11);
	}

	/**
	 * Check if the enemy is smart
	 * @return True if smart False if other types
	 */
	public boolean isSmart() {
		return this instanceof SmartEnemy;
	}

	/**
	 * Checking if a smart enemy is ignoring player movements
	 * @return True if ignoring player movements False if following
	 */
	public boolean isIgnoring() {
		return isIgnoring;
	}

	/**
	 * Set smart enemy to ignore the player or not
	 * @param ignoring True to ignore the player movements for a while, False for stop ignoring
	 */
	public void setIgnoring(boolean ignoring) {
		isIgnoring = ignoring;
	}

	@Override
	public Rectangle getHitbox() {
		return new Rectangle(getX(), getY(), 11, 11);
	}

	@Override
	public EntityType getType() {
		return EntityType.ENEMY;
	}

	@Override
	public String toString() {
		return getType().getName();
	}
}