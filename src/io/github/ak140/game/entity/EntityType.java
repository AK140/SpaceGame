package io.github.ak140.game.entity;

/**
 * Currently unusued hopefully to be used soon
 * @author AK140
 * @Since version 1.6_Alpha
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