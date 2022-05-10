package game.entities;

import java.awt.image.BufferedImage;

import game.Attribute;

/**
 * This interface specifies methods common to the player and
 * enemies. This also provides a handy way for the battle logic
 * to be handled.
 */
public interface ILivingEntity {
	/**
	 * This method gets the current value of the given attribute.
	 * 
	 * Note: This method can only be called for primary attributes
	 *       as secondary attributes can only have a scaled value
	 *       since they are calculated.
	 * 
	 * @param attr the attribute whose value we are retrieving
	 * 
	 * @return the current value of the given attribute
	 */
	int getAttributeValue(Attribute attr);
	
	/**
	 * This method gets the current value of the given attribute,
	 * but scales it so that it is the actual quantity. For example,
	 * the attribute HP could have a value of 3 but in the game this
	 * may correspond to 300 total health.
	 * 
	 * @param attr the attribute whose value we are retrieving and scaling
	 * 
	 * @return the current scaled value of the given attribute
	 */
	double getScaledAttributeValue(Attribute attr);
	
	/**
	 * This method sets the value of the given attribute to the
	 * given value.
	 * 
	 * Note: This method can only be called for primary attributes
	 *       as secondary attributes are calculated.
	 * 
	 * @param attr     the attribute whose value we are setting
	 * @param newValue the new value for the given attribute
	 */
	void setAttributeValue(Attribute attr, int newValue);

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
