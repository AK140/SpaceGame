package io.github.lambo993.game;

import java.awt.*;
import java.util.Random;

/**
 * The <code>Enemy</code> Entity
 * @author Lamboling Seans
 * @since version 0.7_Alpha
 */
public class Enemy implements Entity {

	private int x, y, xVelocity, yVelocity;

	public Enemy() {
		setX(400);
		setY(50);
		Random rng = new Random();
		xVelocity = -1 + rng.nextInt(3);
		yVelocity = -1 + rng.nextInt(3);
		if (xVelocity == 0 && yVelocity == 0) {
			xVelocity = 1;
			yVelocity = 1;
		}
	}

	@Override
	public void run() {
		while (true) {
			move(0, 5, 790, 590);
			try {
				Thread.sleep(5);
			} catch (InterruptedException ex) {
				System.err.println("Error: Thread Interrupted.");
			}
		}
	}

	@Override
	public void move(int xMin, int yMin, int xMax, int yMax) {
		x += xVelocity;
		y += yVelocity;
		if (getX() < xMin) {
			xVelocity = 1;
		}
		if (getX() > xMax) {
			xVelocity = -1;
		}
		if (getY() < yMin) {
			yVelocity = 1;
		}
		if (getY() > yMax) {
			yVelocity = -1;
		}
	}

	@Override
	public void draw(Graphics g) {
		g.drawImage(Main.loadImage("/io/github/lambo993/game/images/Enemy.png"), getX(), getY(), new Main());
	}

	@Override
	public int getX() {
		return x;
	}

	public int getXVelocity() {
		return xVelocity;
	}

	@Override
	public int getY() {
		return y;
	}

	public int getYVelocity() {
		return yVelocity;
	}

	@Override
	public void setX(int x) {
		this.x = x;
	}

	public void setXVelocity(int xVelocity) {
		this.xVelocity = xVelocity;
	}

	@Override
	public void setY(int y) {
		this.y = y;
	}

	public void setYVelocity(int yVelocity) {
		this.yVelocity = yVelocity;
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