package io.github.lambo993.game.entity;

import io.github.lambo993.game.Main;
import java.awt.*;
import java.util.Random;

/**
 * The <code>Enemy</code> Entity
 * @author Lamboling Seans
 * @since version 0.7_Alpha
 */
public class Enemy extends Entity {

	private int x, y;
	private boolean isIgnoring = false;

	public Enemy() {
		setX(400);
		setY(50);
		Random rng = new Random();
		setXVelocity(-1 + rng.nextInt(3));
		setYVelocity(-1 + rng.nextInt(3));
		if (getXVelocity() == 0 && getYVelocity() == 0) {
			setXVelocity(1);
			setYVelocity(1);
		}
	}

	@Override
	public void run() {
		while (true) {
			move(0, 5, 790, 590);
			try {
				Main.sleep();
			} catch (InterruptedException ex) {
				System.err.println("Error: Thread Interrupted.");
			}
		}
	}

	@Override
	public void draw(Graphics2D g) {
		g.setColor(Color.ORANGE);
		g.fillOval(getX(), getY(), 11, 11);
	}

	public boolean isSmart() {
		return this instanceof SmartEnemy;
	}

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
	public int getX() {
		return x;
	}

	@Override
	public int getY() {
		return y;
	}

	@Override
	public void setX(int x) {
		this.x = x;
	}

	@Override
	public void setY(int y) {
		this.y = y;
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
		return "Enemy";
	}
}