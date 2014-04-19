package io.github.lambo993.game.entity;

import io.github.lambo993.game.Main;
import java.awt.*;
import java.util.Random;

public class PowerUp extends Entity {

	private int x, y;

	public PowerUp() {
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
			if (!isPaused()) move(0, 0, 790, 590);
			try {
				Main.sleep();
			} catch (InterruptedException ex) {
				System.err.println("Error: Thread Interrupted.");
			}
		}
	}

	@Override
	public void draw(Graphics2D g) {
		g.setColor(Color.GREEN);
		g.fillOval(getX(), getY(), 7, 7);
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
		return new Rectangle(getX(), getY(), 7, 7);
	}

	@Override
	public EntityType getType() {
		return EntityType.POWERUP;
	}

	@Override
	public String toString() {
		return "PowerUp";
	}
}