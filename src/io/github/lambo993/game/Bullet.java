package io.github.lambo993.game;

import java.awt.Graphics;
import java.awt.Rectangle;

/**
 * 
 * @author Lamboling Seans
 * @since version 0.5_Alpha
 */
public class Bullet implements Entity {

	private int x, y, yVelocity;

	public Bullet(int xCoord, int yCoord) {
		setX(xCoord);
		setY(yCoord);
		yVelocity = -4;
	}

	@Override
	public void run() {
		while (true) {
			move(0, 0, 0, 0);
			try {
				Thread.sleep(5);
			} catch (InterruptedException ex) {
				System.err.println("Error: Thread Interrupted.");
			}
		}
	}

	@Override
	public void move(int xMin, int yMin, int xMax, int yMax) {
		y += yVelocity;
	}

	/**
	 * Removes the bullet when it goes off the screen limit.
	 * @author Lamboling Seans
	 * @return screen top limit y 0
	 * @since version 0.5_Alpha
	 */
	public boolean isOffScreen() {
		return getY() < 0 || getY() > 560;
	}

	@Override
	public void draw(Graphics g) {
		g.drawImage(Main.loadImage("/io/github/lambo993/game/images/Bullet.gif"), getX(), getY(), new Main());
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
		return new Rectangle(getX(), getY() + 3, 10, 21);
	}

	@Override
	public EntityType getType() {
		return EntityType.BULLET;
	}

	@Override
	public String toString() {
		return "Bullet";
	}
}