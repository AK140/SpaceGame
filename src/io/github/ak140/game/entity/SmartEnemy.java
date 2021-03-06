package io.github.ak140.game.entity;

import io.github.ak140.game.*;
import java.awt.*;

/**
 * Same as enemy but follow the player movements
 * @author Lamboling Seans
 * @since version 1.7.9_Alpha
 */
public class SmartEnemy extends Enemy {

	private Player player;

	public SmartEnemy(Player player) {
		setX(400);
		setY(50);
		this.player = player;
	}

	@Override
	public void run() {
		while (true) {
			int x = player.getX() + 31;
			int y = player.getY() + 8;
			int x1 = x + 50;
			int x2 = x - 50;
			if (getX() == x && player.isAlive() && !isIgnoring()) {
				setXVelocity(0);
			}
			if (getY() == y && player.isAlive() && !isIgnoring()) {
				setYVelocity(0);
			}
			if (player.isAlive() && !isIgnoring()) {
				move(x, y, x, y);
			} else {
				move(0, 5, 790, 590);
			}
			if (isIgnoring()) {
				if (getX() > x1) {
					setIgnoring(false);
				} else if (getX() < x2) {
					setIgnoring(false);
				}
			}
			Main.sleep();
		}
	}

	@Override
	public void draw(Graphics2D g) {
		g.setColor(Color.RED);
		g.fillOval(getX(), getY(), 11, 11);
	}
}