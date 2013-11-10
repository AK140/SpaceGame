package io.github.lambo993.game;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.sound.sampled.*;
import javax.swing.*;

/**
 * The Main class
 * Handles the entities thread, the frames and the input
 * @author Lamboling Seans
 * @version 1.7.7_Alpha
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
	private boolean screenShowed;
	private static boolean isSoundMuted = false;
	private static final int PRESS_PERIOD = 0x177;
	private long lastPressMs = 0;

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
				if (collidesWith(player, enemies.get(i))) {
					playSound("/io/github/lambo993/game/sound/enemy.wav");
					enemies.remove(i);
					player.removeLife(1);
				}
			}
			for (int i = enemies.size() - 1; i >= 0; i--) {
				for (int j = bullets.size() - 1; j >= 0 && i < enemies.size(); j--) {
					if (collidesWith(bullets.get(j), enemies.get(i))) {
						playSound("/io/github/lambo993/game/sound/hit.wav");
						enemies.remove(i);
						bullets.remove(j);
						addScore(1);
					}
				}
			}
			for (int i = 0; i < powers.size(); i++) {
				if (collidesWith(player, powers.get(i))) {
					powers.remove(i);
					playSound("/io/github/lambo993/game/sound/powerup.wav");
					player.addLife(1);
					addScore(1);
				}
			}
			try {
				Thread.sleep(5);
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

	public void draw(Graphics g) {
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
			g.drawString(Integer.toString(player.getX()), 760, 45);
			g.drawString(Integer.toString(player.getY()), 760, 60);
		}
		repaint();
	}

	/**
	 * Loads an Image
	 * @param path Path to the Image File
	 * @return the loaded <code>Image</code> object
	 * @since version 1.4_Alpha
	 */
	public static Image loadImage(String path) {
		if (path == null) {
			throw new IllegalArgumentException("path cannot be null!");
		}
		ImageIcon sid = new ImageIcon(Main.class.getResource(path));
		return sid.getImage();
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
			clip.open(audioIn);
			if (isSoundMuted) {
				clip.stop();
				clip.flush();
				clip.close();
			} else {
				clip.start();
				clip.loop(loop);
			}
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
			isSoundMuted = true;
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

	/**
	 * Checks if an <code>Entity</code> Collided with another <code>Entity</code>
	 * @param collider The <code>Entity</code> Collider
	 * @param collidee The <code>Entity</code> Collidee
	 * @return true If the Collider collides with the Collidee,
	 * false if the collider/collidee is player and died
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
		lastPressMs = System.currentTimeMillis();
		shootBullet();
	}

	private void shootBullet() {
		if (bullets.size() < 10 && player.isAlive()) {
			playSound("/io/github/lambo993/game/sound/bullet.wav");
			Bullet b = new Bullet(player.getX() + 30, player.getY() - 12);
			bullets.add(b);
			Thread t = new Thread(b);
			t.start();
		}
	}

	private void spawnEnemy(int spawnLimit) {
		if (enemies.size() < spawnLimit && player.isAlive()) {
			Enemy e = new Enemy();
			enemies.add(e);
			new Thread(e).start();
		}
	}

	private void spawnPowers(int spawnLimit) {
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
				onDisable();
			}
		}
	}

	@Override
	public boolean isEnabled() {
		return isEnabled;
	}

	private void onEnable() {
		System.out.println("Starting game...");
		setIconImage(loadImage("/io/github/lambo993/game/images/Ship.png"));
		playSound("/io/github/lambo993/game/sound/music.wav", Clip.LOOP_CONTINUOUSLY);
		System.out.println("You are now running SuperSpaceShooter version 1.7.7_Alpha Developed by Lamboling Seans");
	}

	private void onDisable() {
		System.err.println("Closing game...");
		isSoundMuted = false;
		screenShowed = false;
		setScore(0);
		player.setX(0);
		player.setY(0);
		player.xVelocity = 0;
		player.yVelocity = 0;
		enemies.removeAll(enemies);
		bullets.removeAll(bullets);
		powers.removeAll(powers);
		Cursor cursor = new Cursor(Cursor.DEFAULT_CURSOR);
		setCursor(cursor);
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
		setScore(0);
		player.setX(400);
		player.setY(300);
		player.xVelocity = 0;
		player.yVelocity = 0;
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
	 * The Main method.
	 * @param args the JVM Arguments
	 * @since version 0.1_Alpha
	 */
	public static final void main(final String[] args) {
		Main m = new Main("Space Catastrophe", true);
		new Thread(m).start();
		while (m.isEnabled()) {
			m.spawnEnemy(15);
			m.spawnPowers(1);
			try {
				Thread.sleep(3 * 1000);
			} catch (InterruptedException ex) {
				System.err.println("Error: Thread Interrupted.");
			}
		}
	}

	@Override
	public String toString() {
		return "Game";
	}

	/**
	 * The Keyboard key input.
	 * @author Lamboling Seans
	 * @since version 0.3_Alpha
	 */
	private class KeyListenerEvent extends KeyAdapter {
		@Override
		public void keyPressed(KeyEvent event) {
			switch (event.getKeyCode()) {
			case KeyEvent.VK_UP:
			case KeyEvent.VK_W:
				player.yVelocity = -2;
				break;
			case KeyEvent.VK_DOWN:
			case KeyEvent.VK_S:
				player.yVelocity = 2;
				break;
			case KeyEvent.VK_LEFT:
			case KeyEvent.VK_A:
				player.xVelocity = -2;
				break;
			case KeyEvent.VK_RIGHT:
			case KeyEvent.VK_D:
				player.xVelocity = 2;
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
					isSoundMuted = false;
					System.out.println("Unmuted");
				} else {
					isSoundMuted = true;
					System.out.println("Mutted");
				}
				break;
			case KeyEvent.VK_ESCAPE:
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
				player.yVelocity = 0;
				break;
			case KeyEvent.VK_LEFT:
			case KeyEvent.VK_A:
			case KeyEvent.VK_RIGHT:
			case KeyEvent.VK_D:
				player.xVelocity = 0;
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
	private class MouseListenerEvent extends MouseAdapter {

		@Override
		public void mousePressed(MouseEvent event) {
			switch (event.getButton()) {
			case MouseEvent.BUTTON1:
				fireBullet();
				break;
			default:
				break;
			}
		}
	}

	public final class WindowsListener extends WindowAdapter {

		@Override
		public void windowClosing(WindowEvent event) {
			setEnabled(false);
		}
	}
}