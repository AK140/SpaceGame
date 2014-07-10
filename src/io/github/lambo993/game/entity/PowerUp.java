package io.github.lambo993.game.entity;

import io.github.lambo993.game.Main;
import java.awt.*;

public class PowerUp extends Entity {

	public PowerUp() {
		int y = r.nextInt(580);
		if (y <= 15) {
			y = 16;
		}
		setX(r.nextInt(780));
		setY(y);
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
			move(0, 15, 790, 590);
			Main.sleep();
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