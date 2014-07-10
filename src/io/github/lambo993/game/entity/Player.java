package io.github.lambo993.game.entity;

import io.github.lambo993.game.*;
import java.awt.*;

/**
 * Seperate the Main class and the Player
 * @author Lamboling Seans
 * @since version 1.7.6_Alpha
 */
public final class Player extends Entity {

	private boolean isAlive;
	private int lifePoint;
	private Main m;

	public Player(Main m) {
		this.m = m;
		setX(400);
		setY(300);
		setXVelocity(0);
		setYVelocity(0);
		setLife(3);
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
				setLife(0);
				Main.playSound("lost.wav");
			} else {
				setLife(3);
			}
		}
	}

	/**
	 * Gets the player current LifePoint
	 * @return The Players Life
	 */
	public int getLife() {
		if (lifePoint < 1) {
			setAlive(false);
		} else if (lifePoint > 3) {
			Main.LOGGER.warning("LifePoint can't be more than 3!");
			setLife(3);
		} else if (lifePoint < 0) {
			Main.LOGGER.warning("LifePoint can't be less than 0!");
			setLife(0);
		}
		return lifePoint;
	}

	public void setLife(final int lifePoint) {
		if (lifePoint < 0 || lifePoint > 3) {
			throw new IndexOutOfBoundsException("You can't set life more than 3 or less then 0");
		}
		this.lifePoint = lifePoint;
	}

	public void addLife(int healed) {
		if (getLife() < 3) {
			lifePoint += healed;
		}
		if (healed <= 0) {
			throw new IllegalArgumentException("You can't use negative");
		}
	}

	public void removeLife(int attacked) {
		if (getLife() > 0) {
			lifePoint -= attacked;
		}
		if (attacked < 0) {
			throw new IllegalArgumentException("You can't use negative");
		}
	}

	@Override
	public Rectangle getHitbox() {
		return new Rectangle(getX(), getY(), 69, 33);
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