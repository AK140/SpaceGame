package io.github.lambo993.game;

import io.github.lambo993.engine.*;
import io.github.lambo993.game.entity.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.sound.sampled.*;
import javax.swing.*;

/**
 * The Main class
 * Handles the entities thread, the frames and the input
 * @author Lamboling Seans
 * @version 1.8.5_Alpha
 * @since 7/14/2013
 * @serial 5832158247289767468L
 */
public final class Main extends Engine {

	private static final long serialVersionUID = 5832158247289767468L;
	private final Player player;
	private final ArrayList<Bullet> bullets;
	private final ArrayList<Enemy> enemies;
	private final ArrayList<PowerUp> powers;
	private int killedEnemy = 0;
	private int powersCollected = 0;
	private int bulletsShooted = 0;
	private boolean debugEnabled;
	private static boolean isPaused = false;
	public static final Achievements ACHIEVE1 = Achievements.BACK_WAY;
	public static final Achievements ACHIEVE2 = Achievements.MY_LOVE;
	public static final Achievements ACHIEVE3 = Achievements.RIGHT_CLICK;
	public static final Achievements ACHIEVE4 = Achievements.KILLER;

	/**
	 * Construct a Windowless <code>Main</code>.
	 * To instance this class
	 */
	public Main() {
		this(false);
	}

	/**
	 * Construct The Game
	 * @param createWindows true if create Window false if not
	 * @throws HeadlessException
	 */
	protected Main(final boolean createWindows) throws HeadlessException {
		super("Space Catastrophe", 800, 600, createWindows);
		if (createWindows) {
			setBackground(Color.BLACK);
			setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
			addKeyListener(new KeyListenerEvent());
			addMouseListener(new MouseListenerEvent());
			addWindowListener(new WindowsListener());
		}
		player = new Player(this);
		bullets = new ArrayList<Bullet>();
		enemies = new ArrayList<Enemy>();
		powers = new ArrayList<PowerUp>();
	}

	@Override
	public void run() {
		new Thread(player, "Player").start();
		while (isEnabled()) {
			for (int i = 0; i < bullets.size(); i++) {
				if (bullets.get(i).isOffScreen())
					bullets.remove(i);
			}
			for (int i = 0; i < enemies.size(); i++) {
				Enemy e = enemies.get(i);
				if (collidesWith(player, e)) {
					playSound("explosion.wav");
					enemies.remove(i);
					player.removeLife(1);
					if (e.isSmart()) {
						if (e.getY() > player.getY() + 10) {
							unlock(ACHIEVE1);
						}
					} else {
						reduceScore(1);
					}
				}
			}
			for (int i = enemies.size() - 1; i >= 0; i--) {
				for (int j = bullets.size() - 1; j >= 0 && i < enemies.size(); j--) {
					Enemy e = enemies.get(i);
					if (collidesWith(e, bullets.get(j))) {
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
							unlock(ACHIEVE4);
						}
					}
				}
			}
			for (int i = 0; i < powers.size(); i++) {
				if (collidesWith(player, powers.get(i))) {
					powers.remove(i);
					playSound("powerup.wav");
					if (player.getLife() == 3) {
						addScore(1);
					}
					player.addLife(1);
					powersCollected++;
				}
			}
			for (int i = 0; i < powers.size(); i++) {
				for (int j = 0; j < bullets.size(); j++) {
					if (collidesWith(bullets.get(j), powers.get(i))) {
						unlock(ACHIEVE2);
					}
				}
			}
			try {
				sleep();
			} catch (InterruptedException ex) {
				LOGGER.severe("Error: Thread Interrupted.");
			}
		}
	}

	@Override
	public void draw(final Graphics2D g) {
		g.drawImage(loadImage("BackGround.png"), 0, 0, this);
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
		g.drawString("Score: " + getScore(), 40, 45);
		g.drawString("HP:    " + player.getLife(), 40, 60);
		if (debugEnabled) {
			g.drawString("x: " + player.getX(), 735, 45);
			g.drawString("y: " + player.getY(), 735, 60);
			g.drawString("b: " + bullets.size(), 735, 75);
			g.drawString("e: " + enemies.size(), 735, 90);
			long max = Runtime.getRuntime().maxMemory() / 1024 / 1024;
			long total = Runtime.getRuntime().totalMemory() / 1024 / 1024;
			long free = Runtime.getRuntime().freeMemory() / 1024 / 1024;
			g.drawString("gcmax: " + max, 707, 105);
			g.drawString("gctotal: " + total, 693, 120);
			g.drawString("gcfree: " + free, 700, 135);
		}
		repaint(5);
	}

	/**
	 * Loads an Image
	 * @param path Path to the Image File
	 * @return the loaded <code>Image</code> object
	 * @since version 1.4_Alpha
	 */
	public static Image loadImage(String path) {
		return loadImage("/io/github/lambo993/game/images/" + path, false);
	}

	/**
	 * Plays The sound
	 * @param path Path to the Sound File
	 * @since version 1.7_Alpha
	 */
	public static void playSound(final String path) {
		playSound("/io/github/lambo993/game/sound/" + path, 0);
	}

	public static void sleep() throws InterruptedException {
		Thread.sleep(5); //TODO: Make a better game loop
	}

	/**
	 * Checks if an <code>Entity</code> Collided with another <code>Entity</code>
	 * @param collider The <code>Entity</code> Collider
	 * @param collidee The <code>Entity</code> Collidee
	 * @return true If the Collider collides with the Collidee,
	 * false if the collider/collidee is player and died
	 * @since version 1.7.2_Alpha
	 */
	public static boolean collidesWith(Entity collider, Entity collidee) {
		Rectangle hitBox1 = collider.getHitbox();
		Rectangle hitBox2 = collidee.getHitbox();
		if (collider instanceof Player) {
			Player p = (Player)collider;
			if (!p.isAlive()) return false;
		} else if (collidee instanceof Player) {
			Player p = (Player)collidee;
			if (!p.isAlive()) return false;
		}
		if (collider == collidee || collider == null || collidee == null || collider.isPaused() || collidee.isPaused()) {
			return false;
		}

		return hitBox1.intersects(hitBox2) || hitBox2.intersects(hitBox1);
	}

	public static void unlock(Achievements a) {
		if (!a.isUnlocked() && !isPaused()) {
			LOGGER.info("Achievements unlocked! " + a.getName());
			a.setUnlocked(true);
			playSound("achievement.wav");
		}
	}

	public Player getPlayer() {
		return player;
	}

	/**
	 * Delays the bullet shooting
	 * @since version 1.7.5_Alpha
	 */
	public void fireBullet() {
		if (!delayButton(375)) { //FIXME: Pausing doesn't pause the bullet shooting time limit
			return;
		}
		shootBullet(10);
	}

	private void shootBullet(int spawnLimit) {
		if (bullets.size() < spawnLimit && player.isAlive() && !isPaused()) {
			playSound("bullet.wav");
			Bullet b = new Bullet(player);
			bullets.add(b);
			new Thread(b, "Bullet-" + bulletsShooted).start();
			bulletsShooted++;
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
		if (enemies.size() < spawnLimit && player.isAlive() && !isPaused()) {
			int chance = new Random().nextInt();
			Enemy e;
			if ((chance % 2) == 0) {
				e = new SmartEnemy(player);
			} else {
				e = new Enemy();
			}
			enemies.add(e);
			new Thread(e).start();
		}
	}

	protected void spawnPowers(int spawnLimit) {
		if (powers.size() < spawnLimit && player.isAlive() && !isPaused()) {
			PowerUp p = new PowerUp();
			powers.add(p);
			new Thread(p).start();
		}
	}

	@Override
	protected void onEnable() {
		LOGGER.info("Starting game...");
		System.setProperty("spacecatastrophe.name", toString());
		System.setProperty("spacecatastrophe.version", "1.8.5_Alpha");
		System.setProperty("spacecatastrophe.author", "Lambo993");
		/*
		Object obj = JOptionPane.showInputDialog(this, "Menu", toString(), JOptionPane.PLAIN_MESSAGE,
				new ImageIcon(Main.class.getResource("/io/github/lambo993/game/images/Ship.png")),
				new Object[] { "Play", "MultiPlayer", "Settings", "Exit" }, "Play");
		if (obj.equals("Exit")) {
			LOGGER.info("Closed game");
			System.exit(1);
			return;
		} else if (obj.equals("MultiPlayer")) {
			multiPlayer();
			return;
		} else if (obj.equals("Settings")) {
			LOGGER.info("Not supported yet");
		} //*/
		setIconImage(loadImage("Ship.png"));
		playSound("/io/github/lambo993/game/sound/music.wav", Clip.LOOP_CONTINUOUSLY);
		LOGGER.info("You are now running " + toString() + " version " + System.getProperty("spacecatastrophe.version") + " Developed by Lamboling Seans");
	}

	@Override
	protected void onDisable() throws Exception {
		setMuted(true);
		debugEnabled = false;
		LOGGER.info("Closing game...");
		saveStats();
		setScore(0);
		player.setX(0);
		player.setY(0);
		player.setXVelocity(0);
		player.setYVelocity(0);
		enemies.removeAll(enemies);
		bullets.removeAll(bullets);
		powers.removeAll(powers);
		setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		setTitle("");
		setBackground(Color.WHITE);
		setIconImage(null);
		setLocationRelativeTo(null);
		setVisible(false);
		System.exit(1);
	}

	private void onReset() {
		if (!delayButton(500)) {
			return;
		}
		LOGGER.info("Reseting...");
		powers.removeAll(powers);
		enemies.removeAll(enemies);
		bullets.removeAll(bullets);
		player.setLife(3);
		player.setAlive(true);
		bulletsShooted = 0;
		killedEnemy = 0;
		powersCollected = 0;
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
	 * <p>Save the last stats to a file</p>
	 * (Credit goes to Wilee999 for the method example)
	 * @author Wilee999
	 * @since version 1.7.8_Alpha
	 */
	public boolean saveStats() {
		try {
			int i = JOptionPane.showConfirmDialog(null, "Save the stats?", toString(),
					JOptionPane.YES_NO_OPTION);
			if (i == 1 || i == -1) {
				return false;
			}
			File dir = new File("Space Catastrophe");
			if (!dir.exists()) {
				dir.mkdir();
			}
			File file = new File(dir, "Stats.txt");
			if (!file.exists()) {
				file.createNewFile();
			}
			PrintStream out = new PrintStream(file);
			out.println("Here's the previous stats");
			out.println("Score: " + getScore());
			out.println("Life: " + player.getLife());
			out.println("Bullets Shooted: " + bulletsShooted);
			out.println("Killed Enemy: " + killedEnemy);
			out.println("PowerUps Collected: " + powersCollected);
			out.println("X: " + player.getX());
			out.println("Y: " + player.getY());
			out.println("Achievements Unlocked:");
			if (ACHIEVE1.isUnlocked()) {
				out.println(ACHIEVE1.getName());
			} if (ACHIEVE2.isUnlocked()) {
				out.println(ACHIEVE2.getName());
			} if (ACHIEVE3.isUnlocked()) {
				out.println(ACHIEVE3.getName());
			} if (ACHIEVE4.isUnlocked()) {
				out.println(ACHIEVE4.getName());
			} else if (!ACHIEVE1.isUnlocked() && !ACHIEVE2.isUnlocked() && !ACHIEVE3.isUnlocked() && !ACHIEVE4.isUnlocked()) {
				out.println("None");
			}
			LOGGER.info("Stats saved!");
			out.println("Log history:");
			for (String s: LOGGER.getHistory()) {
				out.print(s);
			}
			out.close();
			return true;
		} catch (IOException ex) {
			LOGGER.warning("Error: " + ex.getMessage());
			ex.printStackTrace();
			return false;
		}
	}

	/**
	 * Writes a crash report to a file if the game got an exception
	 * @param t The error that's be checked
	 * @deprecated Adding time and date to report and more error checking
	 */
	public static void crashReport(Throwable t) {
		try {
			File dir = new File("Space Catastrophe");
			if (!dir.exists()) {
				dir.mkdir();
			}
			File file = new File(dir, "crash-" + new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss").format(new Date()) + "-client.txt");
			PrintStream err = new PrintStream(file);
			err.println("Please go to https://github.com/Lambo993/SpaceGame/issues and report this");
			err.println("Stack Trace:");
			t.printStackTrace(err);
			err.println("System details:");
			Runtime r = Runtime.getRuntime();
			long m = 1024;
			long mm = r.maxMemory();
			long tm = r.totalMemory();
			long fm = r.freeMemory();
			err.println("Operating System: " + System.getProperty("os.name") + "(" + System.getProperty("os.arch") + ") version "
					+ System.getProperty("os.version"));
			err.println("Java Version: " + System.getProperty("java.version") + ", " + System.getProperty("java.vendor"));
			err.println("Java VM Version: " + System.getProperty("java.vm.name") +
					" (" + System.getProperty("java.vm.info") + "), " + System.getProperty("java.vm.vendor"));
			err.println("Memory: " + fm + " bytes (" + fm / m / m + " MB) / " + tm + " bytes (" + tm / m / m + " MB) up to " + mm + " bytes (" + mm /m /m + " MB)");
		} catch (IOException ex) {
			LOGGER.severe("Couldn't save crash report");
			System.exit(0);
		}
	}

	public void multiPlayer() {
		if (isDisplayable()) {
			return; //Make sure display haven't been created yet
		}
		try {
			String fullIp = JOptionPane.showInputDialog("Enter ip address and port");
			String[] partialIp = fullIp.split(":");
			int port;
			if (partialIp.length == 1 || partialIp.length < 3) {
				port = 25575;
			} else {
				try {
					port = Integer.parseInt(partialIp[1]);
				} catch (NumberFormatException ex) {
					port = 25575;
				}
			}
			Socket server = new Socket(partialIp[0], port);
			if (server.isConnected()) {
				LOGGER.info("Connected to " + fullIp);
			}
			server.close();
		} catch (UnknownHostException ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(null, "Unknown host", "java.net.UnkownHostException", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		} catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}
	}

	/**
	 * The Main method.
	 * @param args the JVM Arguments
	 * @since version 0.1_Alpha
	 */
	public static final void main(final String[] args) {
		Main m = new Main(true);
		new Thread(m, "MainLoop").start();
		do {
			m.spawnEnemy(15);
			m.spawnPowers(1);
			try {
				Thread.sleep(3 * 1000); //FIXME: Pausing or restart doesn't restart or pause the timings
			} catch (InterruptedException ex) {
				LOGGER.warning("Error: Thread Interrupted.");
			}
		} while (m.isEnabled());
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
				if (isMuted()) {
					setMuted(false);
					LOGGER.info("Unmuted");
				} else {
					setMuted(true);
					LOGGER.info("Mutted");
				}
				break;
			case KeyEvent.VK_ESCAPE:
				setPaused(true);
				int i = JOptionPane.showConfirmDialog(Main.this, "Paused\nClose Game?", Main.this.toString(), JOptionPane.YES_NO_OPTION);
				if (i == 1 || i == -1) {
					setPaused(false);
					return;
				}
				setEnabled(false);
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
				unlock(ACHIEVE3);
				break;
			default:
				break;
			}
		}
	}

	private final class WindowsListener extends WindowAdapter {

		@Override
		public void windowClosing(WindowEvent event) {
			setPaused(true);
			setEnabled(false);
		}

		@Override
		public void windowLostFocus(WindowEvent event) {
			player.setXVelocity(0);
			player.setYVelocity(0);
		}
	}
}