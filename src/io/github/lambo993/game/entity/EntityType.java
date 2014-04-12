package io.github.lambo993.game.entity;

/**
 * Currently unusued hopefully to be used soon
 * @author Lamboling Seans
 */
public enum EntityType {

	UNKNOWN(null, null, -1),
	PLAYER("Player", Player.class, 0),
	BULLET("Bullet", Bullet.class, 1),
	POWERUP("PowerUp", PowerUp.class, 2),
	ENEMY("Enemy", Enemy.class, 3);

	private String name;
	private Class<? extends Entity> clazz;
	private short typeId;

	private EntityType(String name, Class<? extends Entity> clazz, int typeId) {
		this.name = name;
		this.clazz = clazz;
		this.typeId = (short) typeId;
	}

	public String getName() {
		return name;
	}

	public Class<? extends Entity> getEntityClass() {
		return clazz;
	}

	public short getTypeId() {
		return typeId;
	}
}