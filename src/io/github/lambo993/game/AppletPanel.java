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
 * @deprecated A bug in the loop
 */
@Deprecated
public final class AppletPanel extends JPanel implements Runnable {

	private static final long serialVersionUID = 5832158247289767468L;
	private final Player player;
	private final ArrayList<Bullet> bullets;
	private final ArrayList<Enemy> enemies;
	private final ArrayList<PowerUp> powers;
	private boolean isEnabled = false;
	private int score = 0;
	private int killedEnemy = 0;
	private boolean debugEnabled;
	private static boolean isPaused = false;
	private static boolean isSoundMuted = false;
	private static final int PRESS_PERIOD = 0x177;
	private long lastPressMs = 0;

	public AppletPanel() throws HeadlessException {
		setEnabled(true);
		setSize(800, 600);
		setBackground(Color.BLACK);
		setVisible(true);
		Cursor cursor = new Cursor(Cursor.CROSSHAIR_CURSOR);
		setCursor(cursor);
		addKeyListener(new KeyListenerEvent());
		addMouseListener(new MouseListenerEvent());
		player = new Player(this);
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
					playSound("explosion.wav");
					enemies.remove(i);
					player.removeLife(1);
					if (e.isSmart()) {
						if (e.getY() > player.getY()) {
							Main.unlock(Main.ACHIEVE1);
						}
					} else {
						reduceScore(1);
					}
				}
			}
			for (int i = enemies.size() - 1; i >= 0; i--) {
				for (int j = bullets.size() - 1; j >= 0 && i < enemies.size(); j--) {
					Enemy e = enemies.get(i);
					if (Main.collidesWith(bullets.get(j), e)) {
						playSound("hit.wav");
						enemies.remove(i);
						bullets.remove(j);
						if (e.isSmart()) {
							addScore(2);
						} else {
							addScore(1);
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
					playSound("powerup.wav");
					if (player.getLife() == 3) {
						addScore(1);
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
		g.drawString(Integer.toString(getScore()), 85, 45);
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

	public static void setMuted(boolean muted) {
		isSoundMuted = muted;
	}

	/**
	 * Plays The sound
	 * @param path Path to the Sound File
	 * @param loop How Many Times The Sound Loop
	 * @since version 1.7.4_Alpha
	 */
	public static void playSound(final String path, final int loop) {
		try {
			AudioInputStream audioIn = AudioSystem.getAudioInputStream(AppletPanel.class.getResource(path));
			Clip clip = AudioSystem.getClip();
			if (isSoundMuted) {
				clip.stop();
				clip.flush();
				clip.close();
			} else {
				clip.open(audioIn);
				if (loop != 0) {
					clip.loop(loop);
				} else {
					clip.setFramePosition(0);
					clip.start();
				}
			}
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
			setMuted(true);
		}
	}

	/**
	 * Plays The sound
	 * @param path Path to the Sound File
	 * @since version 1.7_Alpha
	 */
	public static void playSound(final String path) {
		playSound("/io/github/lambo993/game/sound/" + path, 0);
	}

	/**
	 * Delays the bullet shooting
	 * @since version 1.7.5_Alpha
	 */
	public void fireBullet() {
		if (System.currentTimeMillis() - lastPressMs < PRESS_PERIOD) {
			return;
		}
		lastPressMs = System.currentTimeMillis(); //FIXME: Pausing doesn't pause the bullet shooting time limit
		shootBullet(10);
	}

	private void shootBullet(int spawnLimit) {
		if (bullets.size() < spawnLimit && player.isAlive() && !isPaused()) {
			playSound("bullet.wav");
			Bullet b = new Bullet(player.getX() + 30, player.getY() - 12);
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
					//crashReport(ex);
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
		System.setProperty("spacecatastrophe.name", toString());
		System.setProperty("spacecatastrophe.version", "1.8.3_Alpha");
		System.setProperty("spacecatastrophe.author", "Lambo993");
		playSound("/io/github/lambo993/game/sound/music.wav", Clip.LOOP_CONTINUOUSLY);
		System.out.println("You are now running " + toString() + " version " + System.getProperty("spacecatastrophe.version") + " Developed by Lamboling Seans");
	}

	private void onDisable() throws Exception {
		setMuted(true);
		debugEnabled = false;
		System.out.println("Closing game...");
		setScore(0);
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
		if (System.currentTimeMillis() - lastPressMs < PRESS_PERIOD) {
			return;
		}
		lastPressMs = System.currentTimeMillis();
		System.out.println("Reseting...");
		powers.removeAll(powers);
		enemies.removeAll(enemies);
		bullets.removeAll(bullets);
		player.setLife(3);
		player.setAlive(true);
		killedEnemy = 0;
		setScore(0);
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

	/**
	 * Gets the score
	 * @return The Value of the Score
	 * @since version 1.4_Alpha
	 */
	public int getScore() {
		if (score < 0) {
			score = 0;
		}
		return score;
	}

	/**
	 * Sets the score
	 * @param score sets the score
	 * @since version 1.4_Alpha
	 */
	public void setScore(int newScore) {
		score = newScore;
	}

	/**
	 * For adding scores
	 * @param addScore Adds the score
	 * @throws IllegalArgumentException When using negatives to <code>addScore</code>
	 * @since version 1.5_Alpha
	 */
	public void addScore(int addScore) {
		score += addScore;
		if (addScore < 0) {
			throw new IllegalArgumentException("You can't use negative");
		}
	}

	/**
	 * Removes the score if the score is more than 0
	 * @param reduceScore Removes the score
	 * @throws IllegalArgumentException When using negatives to <code>removeScore</code>
	 * @since version 1.5_Alpha
	 */
	public void reduceScore(int reduceScore) {
		if (score > 0) {
			score -= reduceScore;
		}
		if (reduceScore < 0) {
			throw new IllegalArgumentException("You can't use negative");
		}
	}

	@Override
	public String toString() {
		return "The Amazing Space Catastrophe";
	}

	/**
	 * The Keyboard key input.
	 * @author Lamboling Seans
	 * @since version 0.3_Alpha
	 */
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
				if (isSoundMuted) {
					setMuted(false);
					System.out.println("Unmuted");
				} else {
					setMuted(true);
					System.out.println("Mutted");
				}
				break;
			case KeyEvent.VK_ESCAPE:
				setPaused(true);
				int i = JOptionPane.showConfirmDialog(AppletPanel.this, "Paused", AppletPanel.this.toString(), JOptionPane.YES_NO_OPTION);
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

	/**
	 * The Mouse Key input
	 * @author Lamboling Seans
	 * @since version 0.7_Alpha
	 */
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