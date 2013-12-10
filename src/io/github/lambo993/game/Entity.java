package io.github.lambo993.game;

import java.awt.*;

/**
 * An interface for Entity methods
 * @author Lamboling Seans
 * @since version 1.4_Alpha
 */
public interface Entity extends Runnable {

	/**
	 * Moves the <code>Entity</code> and limit the <code>Entity</code> movement on the screen.
	 * @param xMin Minimum screen limit from the x coord
	 * @param yMin Minimum screen limit from the y coord
	 * @param xMax Maximum screen limit from the x coord
	 * @param yMax Maximum screen limit from the y cord
	 * @since version 0.3_Alpha
	 */
	public void move(int xMin, int yMin, int xMax, int yMax);

	public void draw(Graphics g);

	/**
	 * Gets the <code>Entity</code> X Location of the screen
	 * @return the X Location of the screen
	 */
	public int getX();

	/**
	 * Gets the <code>Entity</code> Y Location of the screen
	 * @return the Y Location of the screen
	 */
	public int getY();

	/**
	 * Sets the <code>Entity</code> X coordinate of the screen
	 * @param x the new X Location of the screen
	 */
	public void setX(int x);

	/**
	 * Sets the <code>Entity</code> Y coordinate of the screen
	 * @param y the new Y Location of the screen
	 */
	public void setY(int y);

	/**
	 * Get's the size of the hit box
	 * @return the Rectangle Size
	 * @since version 0.7_Alpha
	 */
	public Rectangle getHitbox();

	public EntityType getType();
}