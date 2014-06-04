package io.github.lambo993.game.entity;

import io.github.lambo993.game.Main;
import java.awt.*;
import java.util.Random;

public class PowerUp extends Entity {

	public PowerUp() {
		Random rng = new Random();
		int x = rng.nextInt(770);
		int y = rng.nextInt(570);
		if (x < 1) {
			x = 2;
		} else if (y < 15) {
			y = 16;
		}
		setX(x);
		setY(y);
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
			move(0, 15, 790, 590);
			try {
				Main.sleep();
			} catch (InterruptedException ex) {
				Main.LOGGER.warning("Error: Thread Interrupted.");
			}
		}
	}

	@Override
	public void draw(Graphics2D g) {
		g.setColor(Color.GREEN);
		g.fillOval(getX(), getY(), 7, 7);
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
		return getType().getName();
	}
}