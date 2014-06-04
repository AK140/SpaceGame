package io.github.lambo993.game;

import io.github.lambo993.game.entity.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.*;
import javax.sound.sampled.*;
import javax.swing.*;

/**
 * A panel for the applet
 * @deprecated Keyboard input broken
 */
@Deprecated
public final class AppletPanel extends JPanel implements Runnable {

	private static final long serialVersionUID = 5832158247289767468L;
	private final Player player;
	private final ArrayList<Bullet> bullets;
	private final ArrayList<Enemy> enemies;
	private final ArrayList<PowerUp> powers;
	private Main m = new Main();
	private boolean isEnabled = false;
	private int killedEnemy = 0;
	private boolean debugEnabled;
	private static boolean isPaused = false;

	public AppletPanel() throws HeadlessException {
		setEnabled(true);
		setSize(800, 600);
		setBackground(Color.BLACK);
		setVisible(true);
		setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
		addKeyListener(new KeyListenerEvent());
		addMouseListener(new MouseListenerEvent());
		player = m.getPlayer();
		bullets = new ArrayList<Bullet>();
		enemies = new ArrayList<Enemy>();
		powers = new ArrayList<PowerUp>();
		new Thread(player).start();
	}

	@Override
	public void run() {
		while (isEnabled()) {
			for (int i = 0; i < bullets.size(); i++) {
				if (bullets.get(i).isOffScreen())
					bullets.remove(i);
			}
			for (int i = 0; i < enemies.size(); i++) {
				Enemy e = enemies.get(i);
				if (Main.collidesWith(player, e)) {
					Main.playSound("explosion.wav");
					enemies.remove(i);
					player.removeLife(1);
					if (e.isSmart()) {
						if (e.getY() > player.getY()) {
							Main.unlock(Main.ACHIEVE1);
						}
					} else {
						m.reduceScore(1);
					}
				}
			}
			for (int i = enemies.size() - 1; i >= 0; i--) {
				for (int j = bullets.size() - 1; j >= 0 && i < enemies.size(); j--) {
					Enemy e = enemies.get(i);
					if (Main.collidesWith(bullets.get(j), e)) {
						Main.playSound("hit.wav");
						enemies.remove(i);
						bullets.remove(j);
						if (e.isSmart()) {
							m.addScore(2);
						} else {
							m.addScore(1);
						}
						killedEnemy++;
						if (killedEnemy == 100) {
							Main.unlock(Main.ACHIEVE4);
						}
					}
				}
			}
			for (int i = 0; i < powers.size(); i++) {
				if (Main.collidesWith(player, powers.get(i))) {
					powers.remove(i);
					Main.playSound("powerup.wav");
					if (player.getLife() == 3) {
						m.addScore(1);
					}
					player.addLife(1);
				}
			}
			for (int i = 0; i < powers.size(); i++) {
				for (int j = 0; j < bullets.size(); j++) {
					if (Main.collidesWith(bullets.get(j), powers.get(i))) {
						Main.unlock(Main.ACHIEVE2);
					}
				}
			}
			try {
				Main.sleep();
			} catch (InterruptedException ex) {
				System.err.println("Error: Thread Interrupted.");
			}
		}
	}

	@Override
	public void paint(Graphics g) {
		BufferedImage dbImg = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics2D dbg = dbImg.createGraphics();
		draw(dbg);
		g.drawImage(dbImg, 0, 0, this);
	}

	public void draw(final Graphics2D g) {
		g.drawImage(Main.loadImage("BackGround.png"), 0, 0, this);
		player.draw(g);
		if (debugEnabled && player.isAlive()) g.draw(player.getHitbox());
		for (int i = 0; i < bullets.size(); i++) {
			Bullet b = bullets.get(i);
			if (debugEnabled) g.draw(b.getHitbox());
			b.draw(g);
		}
		for (int i = 0; i < enemies.size(); i++) {
			Enemy e = enemies.get(i);
			if (debugEnabled) g.draw(e.getHitbox());
			e.draw(g);
		}
		for (int i = 0; i < powers.size(); i++) {
			PowerUp p = powers.get(i);
			if (debugEnabled) g.draw(p.getHitbox());
			p.draw(g);
		}

		g.setColor(Color.BLACK);
		g.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
		g.drawString(Integer.toString(m.getScore()), 85, 45);
		g.drawString(Integer.toString(player.getLife()), 85, 60);
		g.drawString("Score:", 40, 45);
		g.drawString("HP:", 40, 60);
		if (debugEnabled) {
			g.drawString("x:", 735, 45);
			g.drawString("y:", 735, 60);
			g.drawString("b:", 735, 75);
			g.drawString("e:", 735, 90);
			g.drawString("gcmax:", 707, 105);
			g.drawString("gctotal:", 693, 120);
			g.drawString("gcfree:", 700, 135);
			g.drawString(Integer.toString(player.getX()), 760, 45);
			g.drawString(Integer.toString(player.getY()), 760, 60);
			g.drawString(Integer.toString(bullets.size()), 760, 75);
			g.drawString(Integer.toString(enemies.size()), 760, 90);
			long max = Runtime.getRuntime().maxMemory() / 1024 / 1024;
			long total = Runtime.getRuntime().totalMemory() / 1024 / 1024;
			long free = Runtime.getRuntime().freeMemory() / 1024 / 1024;
			g.drawString(Long.toString(max), 760, 105);
			g.drawString(Long.toString(total), 760, 120);
			g.drawString(Long.toString(free), 760, 135);
		}
		repaint(5);
	}

	public void fireBullet() {
		if (!Main.delayButton(375)) {
			return;
		}
		shootBullet(10);
	}

	private void shootBullet(int spawnLimit) {
		if (bullets.size() < spawnLimit && player.isAlive() && !isPaused()) {
			Main.playSound("bullet.wav");
			Bullet b = new Bullet(player);
			bullets.add(b);
			Thread t = new Thread(b);
			t.start();
			for (int i = 0; i < enemies.size(); i++) {
				Enemy e = enemies.get(i);
				if (e.isSmart() && b.getX() == e.getX() && b.getY() > e.getY() && !e.isIgnoring()) {
					e.setIgnoring(true);
					int chance = new Random().nextInt();
					if ((chance % 2) == 0) {
						e.setXVelocity(1);
					} else {
						e.setXVelocity(-1);
					}
				}
			}
		}
	}

	protected void spawnEnemy(int spawnLimit) {
		if (enemies.size() < spawnLimit && player.isAlive()) {
			int chance = new Random().nextInt();
			if ((chance % 2) == 0) {
				SmartEnemy se = new SmartEnemy(player);
				enemies.add(se);
				new Thread(se).start();
			} else {
				Enemy e = new Enemy();
				enemies.add(e);
				new Thread(e).start();
			}
		}
	}

	protected void spawnPowers(int spawnLimit) {
		if (powers.size() < spawnLimit && player.isAlive()) {
			PowerUp p = new PowerUp();
			powers.add(p);
			new Thread(p).start();
		}
	}

	@Override
	public void setEnabled(final boolean enabled) {
		if (isEnabled() != enabled) {
			isEnabled = enabled;

			if (isEnabled) {
				onEnable();
			} else {
				try {
					onDisable();
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(null, "Error on disabling forcing close", "Disabling Error", JOptionPane.ERROR_MESSAGE);
					System.exit(0);
				}
			}
		}
	}

	@Override
	public boolean isEnabled() {
		return isEnabled;
	}

	private void onEnable() {
		System.out.println("Starting game...");
		System.setProperty("spacecatastrophe.name", m.toString());
		System.setProperty("spacecatastrophe.version", "1.8.5_Alpha");
		System.setProperty("spacecatastrophe.author", "Lambo993");
		Main.playSound("/io/github/lambo993/game/sound/music.wav", Clip.LOOP_CONTINUOUSLY);
		System.out.println("You are now running " + m.toString() + " version " + System.getProperty("spacecatastrophe.version") + " Developed by Lamboling Seans");
	}

	private void onDisable() throws Exception {
		Main.setMuted(true);
		debugEnabled = false;
		System.out.println("Closing game...");
		m.setScore(0);
		player.setX(0);
		player.setY(0);
		player.setXVelocity(0);
		player.setYVelocity(0);
		enemies.removeAll(enemies);
		bullets.removeAll(bullets);
		powers.removeAll(powers);
		setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		setBackground(Color.WHITE);
		setVisible(false);
		System.exit(1);
	}

	private void onReset() {
		if (!Main.delayButton(500)) {
			return;
		}
		System.out.println("Reseting...");
		powers.removeAll(powers);
		enemies.removeAll(enemies);
		bullets.removeAll(bullets);
		player.setLife(3);
		player.setAlive(true);
		killedEnemy = 0;
		m.setScore(0);
		player.setX(400);
		player.setY(300);
		player.setXVelocity(0);
		player.setYVelocity(0);
	}

	private void setPaused(boolean paused) {
		if (isPaused() != paused) {
			isPaused = paused;
			player.setPaused(paused);
			if (player.isPaused()) {
				player.setXVelocity(0);
				player.setYVelocity(0);
			}
			for (int i = 0; i < enemies.size(); i++) {
				Enemy e = enemies.get(i);
				e.setPaused(paused);
			}
			for (int i = 0; i < bullets.size(); i++) {
				Bullet b = bullets.get(i);
				b.setPaused(paused);
			}
			for (int i = 0; i < powers.size(); i++) {
				PowerUp p = powers.get(i);
				p.setPaused(paused);
			}
		}
	}

	public static boolean isPaused() {
		return isPaused;
	}

	protected class KeyListenerEvent extends KeyAdapter {

		@Override
		public void keyPressed(KeyEvent event) {
			switch (event.getKeyCode()) {
			case KeyEvent.VK_UP:
			case KeyEvent.VK_W:
				player.setYVelocity(-2);
				break;
			case KeyEvent.VK_DOWN:
			case KeyEvent.VK_S:
				player.setYVelocity(2);
				break;
			case KeyEvent.VK_LEFT:
			case KeyEvent.VK_A:
				player.setXVelocity(-2);
				break;
			case KeyEvent.VK_RIGHT:
			case KeyEvent.VK_D:
				player.setXVelocity(2);
				break;
			case KeyEvent.VK_R:
				onReset();
				break;
			case KeyEvent.VK_T:
				break;
			case KeyEvent.VK_Z:
				fireBullet();
				break;
			case KeyEvent.VK_F3:
				if (!debugEnabled) {
					debugEnabled = true;
				} else {
					debugEnabled = false;
				}
				break;
			case KeyEvent.VK_F8:
				if (Main.isMuted()) {
					Main.setMuted(false);
					System.out.println("Unmuted");
				} else {
					Main.setMuted(true);
					System.out.println("Mutted");
				}
				break;
			case KeyEvent.VK_ESCAPE:
				setPaused(true);
				int i = JOptionPane.showConfirmDialog(AppletPanel.this, "Paused", m.toString(), JOptionPane.YES_NO_OPTION);
				if (i == 1 || i == -1) {
					setPaused(false);
					return;
				}
				break;
			default:
				break;
			}
		}

		@Override
		public void keyReleased(KeyEvent event) {
			switch (event.getKeyCode()) {
			case KeyEvent.VK_UP:
			case KeyEvent.VK_W:
			case KeyEvent.VK_DOWN:
			case KeyEvent.VK_S:
				player.setYVelocity(0);
				break;
			case KeyEvent.VK_LEFT:
			case KeyEvent.VK_A:
			case KeyEvent.VK_RIGHT:
			case KeyEvent.VK_D:
				player.setXVelocity(0);
				break;
			default:
				break;
			}
		}
	}

	protected class MouseListenerEvent extends MouseAdapter {

		@Override
		public void mousePressed(MouseEvent event) {
			switch (event.getButton()) {
			case MouseEvent.BUTTON1:
				fireBullet();
				break;
			case MouseEvent.BUTTON3:
				Main.unlock(Main.ACHIEVE3);
				break;
			default:
				break;
			}
		}
	}
}