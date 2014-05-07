package io.github.lambo993.game;

/**
 * The achievements class
 * @author Lamboling Seans
 * @since version 1.8_Alpha
 */
public enum Achievements {

	UNKNOWN(-1, null),
	BACK_WAY(1, "Back Way: Got ambused by an enemy from back of the ship"),
	MY_LOVE(2, "My Love: Shoots a power up"),
	RIGHT_CLICK(3, "Right Clicked: It wont do anything anyway"),
	KILLER(4, "Killed 100 enemies: Wow that's a lot of it");

	private int id;
	private String name;
	private boolean isUnlocked = false;

	private Achievements(int id, String name) {
		this.id = id;
		this.name = name;
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