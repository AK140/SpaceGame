package io.github.lambo993.game;

import java.awt.*;

/**
 * Seperate the Main class and the Player
 * @author Lamboling Seans
 * @since version 1.7.6_Alpha
 */
public final class Player implements Entity {

	private int x, y;
	protected int xVelocity, yVelocity;
	private boolean isAlive;
	private int lifePoint;
	private Main m;

	public Player(Main m) {
		this.m = m;
		setX(400);
		setY(300);
		xVelocity = 0;
		yVelocity = 0;
		lifePoint = 3;
		setAlive(true);
	}

	@Override
	public void run() {
		while (m.isEnabled()) {
			move(0, 20, 740, 560);
			try {
				Thread.sleep(5);
			} catch (InterruptedException ex) {
				System.err.println("Error: Thread Interrupted.");
			}
		}
	}

	@Override
	public void move(int xMin, int yMin, int xMax, int yMax) {
		if (isAlive()) {
			x += xVelocity;
			y += yVelocity;
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
	public void draw(Graphics g) {
		if (isAlive()) {
			g.drawImage(Main.loadImage("/io/github/lambo993/game/images/Ship.png"), getX(), getY(), m);
		}
		if (!isAlive()) {
        	g.drawString("You Died!", 400, 300);
        	g.drawString("Press \"R\" To Try Again!", 350, 325);
        }
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

	/**
	 * Checks if the player is alive or died
	 * @return true if alive false if died
	 * @since version 1.5_Alpha
	 */
	public boolean isAlive() {
		return isAlive;
	}

	public void setAlive(final boolean alive) {
		if (isAlive() != alive) {
			isAlive = alive;
			if (!alive) {
				setLife(0);
				Main.playSound("/io/github/lambo993/game/sound/lost.wav");
			}
		}
	}

	/**
	 * Gets the player current life
	 * @return The Players Life
	 */
	public int getLife() {
		if (lifePoint < 1) {
			setAlive(false);
		} else if (lifePoint > 3) {
			System.err.println("LifePoint can't be more than 3!");
			setLife(3);
		} else if (lifePoint < 0) {
			System.err.println("LifePoint can't be less than 0!");
			setLife(0);
		}
		return lifePoint;
	}

	public void setLife(final int lifePoint) throws IndexOutOfBoundsException {
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
		return new Rectangle(getX(), getY(), 69, 42);
	}

	@Override
	public EntityType getType() {
		return EntityType.PLAYER;
	}

	@Override
	public String toString() {
		return "Player";
	}
}