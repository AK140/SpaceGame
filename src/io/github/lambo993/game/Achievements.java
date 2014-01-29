package io.github.lambo993.game;

/**
 * @author Lamboling Seans
 * @deprecated Still in testing and doesn't have an idea for achievements yet
 */
@Deprecated
public enum Achievements {

	UNKNOWN(-1, null);

	private int id;
	private String name;
	private boolean isUnlocked = false;

	private Achievements(int id, String name) {
		this.id = id;
		this.name = name;
	}

	public void unlock() {
		if (!isUnlocked()) {
			System.out.println("Achievements unlocked! " + getName());
			setUnlocked(true);
		}
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public boolean isUnlocked() {
		return isUnlocked;
	}

	public void setUnlocked(boolean unlocked) {
		if (isUnlocked() != unlocked) {
			isUnlocked = unlocked;
		}
	}
}
