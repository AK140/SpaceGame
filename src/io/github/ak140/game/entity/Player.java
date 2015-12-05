package io.github.ak140.game.entity;

import io.github.ak140.game.*;
import java.awt.*;

/**
 * Seperate the Main class and the Player
 * @author AK140
 * @since version 1.7.6_Alpha
 */
public final class Player extends Entity {

	private boolean isAlive;
	private int lifePoint, maxHealth;
	private Main m;

	public Player(Main m, int maxHealth) {
		this.m = m;
		setX(400);
		setY(300);
		setMaxHealth(maxHealth);
		setAlive(true);
	}

	@Override
	public void run() {
		while (m.isEnabled()) {
			if (!isPaused()) move(0, 20, 740, 565);
			Main.sleep();
		}
	}

	@Override
	public void move(int xMin, int yMin, int xMax, int yMax) {
		if (isAlive()) {
			setX(getX() + getXVelocity());
			setY(getY() + getYVelocity());
			if (getX() < xMin) {
				setX(xMin);
			}
			if (getY() < yMin) {
				setY(yMin);
			}
			if (getX() > xMax) {
				setX(xMax);
			}
			if (getY() > yMax) {
				setY(yMax);
			}
		}
	}

	@Override
	public void draw(Graphics2D g) {
		if (isAlive()) {
			String p;
			if (isMoving()) { 
				p = "Ship.png";
			} else {
				p = "ShipOff.png";
			}
			g.drawImage(Main.loadImage(p), getX(), getY(), m);
		} else {
			g.setColor(Color.BLACK);
			g.drawString("You Died!", 400, 300);
			g.drawString("Press \"R\" to try again!", 350, 325);
		}
	}
	/**
	 * Checks if the player is alive or died
	 * @return true if alive false if died
	 * @since version 1.5_Alpha
	 */
	public boolean isAlive() {
		return isAlive;
	}

	/**
	 * Sets the player alive or not
	 * @param alive Set to true to make the player alive, false for died
	 * @since version 1.5_Alpha
	 */
	public void setAlive(final boolean alive) {
		if (isAlive() != alive) {
			isAlive = alive;
			if (!alive) {
				setHealth(0);
				Main.playSound("lost.wav");
			} else {
				setHealth(getMaxHealth());
			}
		}
	}

	/**
	 * Gets the player current LifePoint
	 * @return The Players Life
	 */
	public int getHealth() {
		if (lifePoint < 1) {
			setAlive(false);
		} else if (lifePoint < 0) {
			Main.LOGGER.warning("LifePoint can't be less than 0!");
			setHealth(0);
		}
		return lifePoint;
	}

	public int getMaxHealth() {
		return maxHealth;
	}

	public void setHealth(final int lifePoint) {
		if (lifePoint < 0 || lifePoint > getMaxHealth()) {
			throw new IndexOutOfBoundsException("You can't set health more than Max health or less than 0");
		}
		this.lifePoint = lifePoint;
	}

	public void setMaxHealth(int maxHealth) {
		if (maxHealth < 0) {
			throw new IndexOutOfBoundsException("You can't use negative");
		}
		this.maxHealth = maxHealth;
	}

	public void addHealth(int value) {
		if (getHealth() < getMaxHealth()) {
			lifePoint += value;
		}
		if (value <= 0) {
			throw new IllegalArgumentException("You can't use negative");
		}
	}

	public void reduceHealth(int value) {
		if (getHealth() > 0) {
			lifePoint -= value;
		}
		if (value < 0) {
			throw new IllegalArgumentException("You can't use negative");
		}
	}

	@Override
	public Rectangle getHitbox() {
		return new Rectangle(getX(), getY(), 66, 36);
	}

	@Override
	public EntityType getType() {
		return EntityType.PLAYER;
	}

	@Override
	public String toString() {
		return getType().getName();
	}
}