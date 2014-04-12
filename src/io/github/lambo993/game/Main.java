package io.github.lambo993.game;

import io.github.lambo993.game.entity.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.sound.sampled.*;
import javax.swing.*;

/**
 * The Main class
 * Handles the entities thread, the frames and the input
 * @author Lamboling Seans
 * @version 1.8.2_Alpha
 * @since 7/14/2013
 * @serial 5832158247289767468L
 */
public final class Main extends JFrame implements Runnable {

	private static final long serialVersionUID = 5832158247289767468L;
	private final Player player;
	private final ArrayList<Bullet> bullets;
	private final ArrayList<Enemy> enemies;
	private final ArrayList<PowerUp> powers;
	private boolean isEnabled = false;
	private int score = 0;
	private int killedEnemy = 0;
	private int powersCollected = 0;
	private int bulletsShooted = 0;
	private boolean screenShowed;
	private static boolean isPaused = false;
	private static boolean isSoundMuted = false;
	private static final int PRESS_PERIOD = 0x177;
	private long lastPressMs = 0;
	private static final Achievements ACHIEVE1 = Achievements.BACK_WAY;
	private static final Achievements ACHIEVE2 = Achievements.MY_LOVE;
	private static final Achievements ACHIEVE3 = Achievements.RIGHT_CLICK;

	/**
	 * Construct a Windowless <code>Main</code>.
	 * To instance this class
	 */
	public Main() {
		this(null, false);
	}

	/**
	 * Construct The Game
	 * @param title Title for the Window
	 * @param createWindows true if create Window false if not
	 * @throws HeadlessException
	 */
	protected Main(final String title, final boolean createWindows) throws HeadlessException {
		if (createWindows) {
			setEnabled(true);
			setSize(800, 600);
			setResizable(false);
			setLocationRelativeTo(null);
			setDefaultCloseOperation(EXIT_ON_CLOSE);
			setBackground(Color.BLACK);
			setVisible(true);
			setTitle(title);
			Cursor cursor = new Cursor(Cursor.CROSSHAIR_CURSOR);
			setCursor(cursor);
			addKeyListener(new KeyListenerEvent());
			addMouseListener(new MouseListenerEvent());
			addWindowListener(new WindowsListener());
		}
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
				if (collidesWith(player, e)) {
					playSound("/io/github/lambo993/game/sound/explosion.wav");
					enemies.remove(i);
					player.removeLife(1);
					if (e.isSmart()) {
						if (e.getY() > player.getY()) {
							ACHIEVE1.unlock();
						}
					} else {
						removeScore(1);
					}
				}
			}
			for (int i = enemies.size() - 1; i >= 0; i--) {
				for (int j = bullets.size() - 1; j >= 0 && i < enemies.size(); j--) {
					Enemy e = enemies.get(i);
					if (collidesWith(bullets.get(j), e)) {
						playSound("/io/github/lambo993/game/sound/hit.wav");
						enemies.remove(i);
						bullets.remove(j);
						if (e.isSmart()) {
							addScore(2);
						} else {
							addScore(1);
						}
						killedEnemy++;
					}
				}
			}
			for (int i = 0; i < powers.size(); i++) {
				if (collidesWith(player, powers.get(i))) {
					powers.remove(i);
					playSound("/io/github/lambo993/game/sound/powerup.wav");
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
						ACHIEVE2.unlock();
					}
				}
			}
			try {
				sleep();
			} catch (InterruptedException ex) {
				System.err.println("Error: Thread Interrupted.");
			}
		}
	}

	@Override
	public void paint(Graphics g) {
		Image dbImg = createImage(getWidth(), getHeight());
		Graphics dbg = dbImg.getGraphics();
		draw(dbg);
		g.drawImage(dbImg, 0, 0, this);
	}

	public void draw(final Graphics g) {
		//TODO: Make better space-like background and moving it
		g.drawImage(loadImage("/io/github/lambo993/game/images/BackGround.png"), 0, 0, this);
		player.draw(g);
		for (int i = 0; i < bullets.size(); i++) {
			Bullet b = bullets.get(i);
			b.draw(g);
		}
		for (int i = 0; i < enemies.size(); i++) {
			Enemy e = enemies.get(i);
			e.draw(g);
		}
		for (int i = 0; i < powers.size(); i++) {
			PowerUp p = powers.get(i);
			p.draw(g);
		}

		g.setColor(Color.BLACK);
		Font font = new Font(Font.MONOSPACED, 0, 12);
		g.setFont(font);
		g.drawString(Integer.toString(getScore()), 85, 45);
		g.drawString(Integer.toString(player.getLife()), 85, 60);
		g.drawString("Score:", 40, 45);
		g.drawString("HP:", 40, 60);
		if (screenShowed) {
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

	/**
	 * Loads an Image
	 * @param path Path to the Image File
	 * @param useDirectory true for load image in jar false for comp directory
	 * @return the loaded <code>Image</code> object
	 * @since version 1.7.8_Alpha
	 */
	public static Image loadImage(String path, boolean useDirectory) {
		if (path == null) {
			throw new IllegalArgumentException("path cannot be null!");
		}
		ImageIcon img;
		if (!useDirectory) {
			img = new ImageIcon(Main.class.getResource(path));
			return img.getImage();
		} else {
			img = new ImageIcon(path);
			return img.getImage();
		}
	}

	/**
	 * Loads an Image
	 * @param path Path to the Image File
	 * @return the loaded <code>Image</code> object
	 * @since version 1.4_Alpha
	 */
	public static Image loadImage(String path) {
		return loadImage(path, false);
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
			AudioInputStream audioIn = AudioSystem.getAudioInputStream(Main.class.getResource(path));
			Clip clip = AudioSystem.getClip();
			if (isSoundMuted) {
				clip.stop();
				clip.flush();
				clip.close();
			} else {
				clip.open(audioIn);
				clip.loop(loop);
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
		playSound(path, 0);
	}

	public static void sleep() throws InterruptedException {
		Thread.sleep(5);
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
		if (collider == collidee || collider == null || collidee == null) {
			return false;
		}

		return hitBox1.intersects(hitBox2) || hitBox2.intersects(hitBox1);
	}

	public Player getPlayer() {
		return player;
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
		if (bullets.size() < spawnLimit && player.isAlive()) {
			playSound("/io/github/lambo993/game/sound/bullet.wav");
			Bullet b = new Bullet(player.getX() + 30, player.getY() - 12);
			bullets.add(b);
			Thread t = new Thread(b);
			t.start();
			bulletsShooted++;
			for (int i = 0; i < enemies.size(); i++) {
				Enemy e = enemies.get(i);
				if (e.isSmart() && b.getX() == e.getX() && b.getY() > e.getY() && !e.isIgnoring()) {
					e.setIgnoring(true);
					Random rng = new Random();
					int chance = rng.nextInt();
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
			Random r = new Random();
			int chance = r.nextInt();
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
				} catch (Exception e) {
					System.err.println("Error on disabling forcing close");
					e.printStackTrace();
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
		System.setProperty("spacecatastrophe.version", "1.8.2_Alpha");
		System.setProperty("spacecatastrophe.author", "Lambo993");
		int i = JOptionPane.showConfirmDialog(null, "Start Game", toString(),
				JOptionPane.DEFAULT_OPTION);
		if (i == -1) {
			System.exit(1);
			return;
		}
		setIconImage(loadImage("/io/github/lambo993/game/images/Ship.png"));
		playSound("/io/github/lambo993/game/sound/music.wav", Clip.LOOP_CONTINUOUSLY);
		System.out.println("You are now running " + toString() + " version " + System.getProperty("spacecatastrophe.version") + " Developed by Lamboling Seans");
	}

	private void onDisable() throws Exception {
		setMuted(true);
		screenShowed = false;
		saveStats();
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
		setTitle("");
		setBackground(Color.WHITE);
		setIconImage(null);
		setLocationRelativeTo(null);
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
			for (int i = 0; i < enemies.size(); i++) {
				Enemy e = enemies.get(i);
				e.setPaused(paused);
				if (e.isSmart()) {
					new Thread(e).start();
				}
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
	 * @param removeScore Removes the score
	 * @throws IllegalArgumentException When using negatives to <code>removeScore</code>
	 * @since version 1.5_Alpha
	 */
	public void removeScore(int removeScore) {
		if (score > 0) {
			score -= removeScore;
		}
		if (removeScore < 0) {
			throw new IllegalArgumentException("You can't use negative");
		}
	}

	/**
	 * <p>Save the last stats to a file</p>
	 * (Credit goes to Wilee999 for the method example)
	 * @author Wilee999
	 * @since version 1.7.8_Alpha
	 */
	public void saveStats() {
		try {
			int i = JOptionPane.showConfirmDialog(null, "Save the stats?", toString(),
					JOptionPane.YES_NO_OPTION);
			if (i == 1 || i == -1) {
				return;
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
			} else if (!ACHIEVE1.isUnlocked() && !ACHIEVE2.isUnlocked() && !ACHIEVE3.isUnlocked()) {
				out.println("None");
			}
			out.close();
			System.out.println("Stats saved!");
		} catch (IOException ex) {
			System.err.println("Error: " + ex.getMessage());
			ex.printStackTrace();
		}
	}

	/**
	 * Writes a crash report to a file if the game got an exception
	 * @param t The error that's be checked
	 * @deprecated Still finding a way where to check the error
	 */
	@Deprecated
	public static void crashReport(Throwable t) {
		try {
			File dir = new File("Space Catastrophe");
			if (!dir.exists()) {
				dir.mkdir();
			}
			File file = new File(dir, "crash-report.txt");
			PrintStream err = new PrintStream(file);
			err.println(t.getMessage());
			t.printStackTrace(err);
		} catch (IOException e) {
			System.exit(0);
		}
	}

	/**
	 * The Main method.
	 * @param args the JVM Arguments
	 * @since version 0.1_Alpha
	 */
	public static final void main(final String[] args) {
		if (args.length == 1 && args[0].equalsIgnoreCase("-server")) {
			JOptionPane.showMessageDialog(null, "The server feature supposed to be hidden!");
			try {
				InetAddress address = InetAddress.getByName(JOptionPane.showInputDialog("Enter ip address"));
				int port = Integer.parseInt(JOptionPane.showInputDialog("Enter port"));
				Socket server = new Socket(address, port);
				if (server.isConnected()) {
					System.out.println("Connected to " + server.getInetAddress().getHostName() + ":" + port);
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
		Main m = new Main("Space Catastrophe", true);
		new Thread(m).start();
		do {
			if (!isPaused()) {
				m.spawnEnemy(15);
				m.spawnPowers(1);
			}
			try {
				Thread.sleep(3 * 1000); //FIXME: Pausing or restart doesn't restart or pause the timings
			} catch (InterruptedException ex) {
				System.err.println("Error: Thread Interrupted.");
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
				if (!screenShowed) {
					screenShowed = true;
				} else {
					screenShowed = false;
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
				ACHIEVE3.unlock();
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
	}
}