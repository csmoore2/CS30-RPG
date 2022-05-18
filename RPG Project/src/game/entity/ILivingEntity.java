package game.entity;

import java.awt.image.BufferedImage;

/**
 * This interface specifies methods common to the player and
 * enemies. This also provides a handy way for the battle logic
 * to be handled.
 */
public interface ILivingEntity {
	/**
	 * This method sets the value of the given primary attribute
	 * to the given value.
	 * 
	 * @param attr     the attribute whose value we are setting
	 * @param newValue the new value for the given attribute
	 */
	void setPrimaryAttributeValue(Attribute attr, int newValue);
	
	/**
	 * This method gets the current value of the given primary
	 * attribute.
	 * 
	 * @param attr the primary attribute whose value we are retrieving
	 * 
	 * @return the current value of the given primary attribute
	 */
	int getPrimaryAttributeValue(Attribute attr);
	
	/**
	 * This method calculates the value of the given secondary
	 * attribute and returns the result.
	 * 
	 * @param attr the attribute whose value we are retrieving and scaling
	 * 
	 * @return the current scaled value of the given attribute
	 */
	double getSecondaryAttributeValue(Attribute attr);

	/**
	 * This method is called at the start of this entity's turn
	 * during a battle. It is responsible for updating the entity's
	 * status effects.
	 */
	void onBattleTurn();

	/**
	 * This method returns the amount of health this entity
	 * currently has.
	 * 
	 * @return the amount of health this entity currently has
	 */
	int getCurrentHealth();

	/**
	 * This methods gives the entity the specified amount of health,
	 * ensuring that the entity does not have more than their maximum
	 * amount of health.
	 * 
	 * @param amount the amount of health to give the entity
	 */
	void addHealth(int amount);

	/**
	 * This method inflicts the specified amount of damage on
	 * this entity.
	 * 
	 * @param damage the amount of damage to inflict on this entity
	 */
	void inflictDamage(int damage);

	/**
	 * This method applys a poison effect to this entity that deals the
	 * specified amount of damage each turn for the specified number of
	 * turns.
	 * 
	 * @param damagePerTurn the amount of damage to do each turn
	 * @param numTurns      the number of turns this effect lasts
	 */
	void inflictPoison(int damagePerTurn, int numTurns);

	/**
	 * This method returns whether or not this entity has an active poison
	 * effect applied to them.
	 * 
	 * @return whether or not this entity has an active poison effect applied
	 *         to them
	 */
	boolean hasPoisonEffect();

	/**
	 * This method returns whether or not this entity is dead.
	 * 
	 * @return if this entity is dead
	 */
	boolean isDead();

	/**
	 * This method returns the amount of experience the player should
	 * gain by killing this entity. By default this is zero.
	 * 
	 * @return the amount of experience the player should gain by killing
	 *         this entity
	 */
	default int getExperienceGainOnDeath() { return 0; }

	/**
	 * This method returns the image that represents this entity.
	 * 
	 * @return the image that represents this entity
	 */
	BufferedImage getImage();
}
