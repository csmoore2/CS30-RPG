package game.entity.enemy;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import game.World;
import game.entity.Attribute;
import game.entity.EnemyAction;
import game.entity.ILivingEntity;
import game.entity.Player;

/**
 * This class represents a generic enemy.
 */
public abstract class Enemy implements IEnemy {
	/**
	 * This is the multiplier that is multiplied with the enemy's base attack damage
	 * to determine how much damage the enemy's poison attacks do each turn.
	 */
	public static final double POISON_DAMAGE_MULTIPLIER = 0.5;

	/**
	 * This is the world.
	 */
	protected final World world;

	/**
	 * This is the enemy's image.
	 */
	protected BufferedImage image;

	/**
	 * This is the enemy's maximum amount of health.
	 */
	protected int maxHealth;

	/**
	 * This is the enemy's current amount of health.
	 */
	protected int currentHealth;

	/**
	 * This stores the original number of healing potions that
	 * the enemy had.
	 */
	protected int originalNumHealingPotions;

	/**
	 * This is the number of healing potions the enemy has.
	 */
	protected int numHealingPotions;

	/**
	 * This is the amount of health one healing potion gives
	 * the enemy.
	 */
	protected int healingPotionHealth;

	/**
	 * This is the base amount of damage dealt by an attack from this enemy.
	 */
	protected int baseAttackDamage;

	/**
	 * This is the number of turns that poison inflicted on the player by
	 * this enemy lasts.
	 */
	protected int numPoisonTurns;

	/**
	 * This is the chance of the enemy making a critical hit.
	 */
	protected double criticalChance;

	/**
	 * This is the chance of the enemy dodging an attack.
	 */
	protected double dodgeChance;

	/**
	 * This is the amount of damage per turn the enemy is taking from poison.
	 */
	protected int poisonDamagePerTurn = 0;

	/**
	 * This is the number of turns remaining for which the enemy will be poisoned.
	 */
	protected int numPoisonTurnsRemaining = 0;

	/**
	 * This constructs an enemy based on the amount of experience the
	 * player has.
	 * 
	 * @param worldIn   the world
	 * @param playerExp the amount of experience the player has
	 */
	protected Enemy(World worldIn, int playerExp) {
		world = worldIn;

		// Try loading a default image for the enemy
		try {
			image = ImageIO.read(new File("res/test2.jpg"));
		} catch (IOException e) {
			throw new RuntimeException("Unable to load enemy's image!", e);
		}

		// Initialize all of the enemy's attributes
		initializeAttributes(playerExp);
	}
	
	/**
	 * This method is responsible for initialize all of the enemy's attributes:
	 *     - Max Health and Current Health
	 *     - Number of Healing Potions and Amount of Health Given by Healing Potions
	 *     - Base Attack Damage
	 *     - Number of Turns Poison Attacks Last
	 *     - Chance of a Critical Hit
	 *     - Chance of Dodging an Attack
	 *     
	 * @param playerExp the player's current amount of experience
	 */
	protected abstract void initializeAttributes(int playerExp);
	
	/**
	 * This method sets the enemy's image to the one given.
	 * 
	 * @param imageIn the enemy's new image
	 * 
	 * @see IEnemy#setImage(BufferedImage)
	 */
	@Override
	public void setImage(BufferedImage imageIn) {
		image = imageIn;
	}

	/**
	 * This method resets the enemy's health, number of healing potions, and
	 * any status effects so that it can be fought again by the player.
	 * 
	 * @see IEnemy#reset()
	 */
	@Override
	public void reset() {
		// Reset the enemy's health
		currentHealth = maxHealth;

		// Reset the enemy's number of healing potions
		numHealingPotions = originalNumHealingPotions;

		// Reset the poison status effect
		poisonDamagePerTurn = 0;
		numPoisonTurnsRemaining = 0;
	}

	/*************************************************************************************/
	/*                                    ATTRIBUTES                                     */
	/*************************************************************************************/

	@Override
	public void setPrimaryAttributeValue(Attribute attr, int newValue) {
		throw new RuntimeException("Enemies do not have primary attributes!");
	}

	@Override
	public int getPrimaryAttributeValue(Attribute attr) {
		throw new RuntimeException("Enemies do not have primary attributes!");
	}

	@Override
	public double getSecondaryAttributeValue(Attribute attr) {
		// Return the scaled attribute value based on the given attribute
		switch (attr) {
			// HEALTH_POINTS = maximum amount of health
			case HEALTH_POINTS:
				return maxHealth;

			// CRIT_CHANCE = chance of making a critical hit
			case CRIT_CHANCE:
				return criticalChance;

			// DODGE_CHANCE = chance of dodging an attack
			case DODGE_CHANCE:
				return dodgeChance;

			// Everything else is unsupported by enemies
			default:
				throw new IllegalArgumentException(
					String.format("Attribute '%s' is unsupported by enemies!", attr.name()));
		}
	}

	/**
	 * This methods gives the enemy the specified amount of health,
	 * ensuring that the enemy does not have more than their maximum
	 * amount of health.
	 * 
	 * @param amount the amount of health to give the enemy
	 * 
	 * @see ILivingEntity#addHealth(int)
	 */
	@Override
	public void addHealth(int amount) {
		// Give the enemy the specified amount of health but cap the enemy's health at its maximum
		currentHealth = Math.min(currentHealth + amount, maxHealth);
	}

	/*************************************************************************************/
	/*                                   BATTLE METHODS                                  */
	/*************************************************************************************/

	/**
	 * This method is called at the start of the enemy's turn
	 * during a battle. It is responsible for updating the enemy's
	 * status effects.
	 */
	@Override
	public void onBattleTurn() {
		// If the enemy is poisoned then deal the poison damage
		// and decrement the number of turns remaining
		if (hasPoisonEffect()) {
			// Show a message
			world.showMessage(
				String.format(
					"Enemy took %d damage from a poison effect.",
					poisonDamagePerTurn
				),
				4
			);

			// Inflict the damage
			inflictDamage(poisonDamagePerTurn);
			numPoisonTurnsRemaining--;
		}
	}

	/**
	 * This method generates the enemy's action during a battle. Exactly
	 * how this is done will vary between the types of enemies but in general
	 * the enemy picks a random action based on percentage chances.
	 * 
	 * @param player the player
	 * 
	 * @return the enemy's chosen action for their turn in a battle
	 * 
	 * @see IEnemy#generateBattleAction(Player)
	 */
	@Override
	public abstract EnemyAction generateBattleAction(Player player);

	/**
	 * This method inflicts the specified amount of damege on the
	 * enemy.
	 * 
	 * @param damage the amount of damage to inflict on the enemy
	 */
	@Override
	public void inflictDamage(int damage) {
		// Inflict the given amount of damage but do not let the enemy's health
		// fall below zero
		currentHealth = Math.max(currentHealth - damage, 0);
	}

	@Override
	public void inflictPoison(int damagePerTurn, int numTurns) {
		poisonDamagePerTurn = damagePerTurn;
		numPoisonTurnsRemaining = numTurns;
	}

	@Override
	public boolean hasPoisonEffect() {
		return numPoisonTurnsRemaining > 0;
	}

	@Override
	public boolean isDead() {
		// The enemy is dead if they have no health left
		return currentHealth == 0;
	}

	/*************************************************************************************/
	/*                                      GETTERS                                      */
	/*************************************************************************************/

	/**
	 * This method returns the enemy's current amount of health.
	 * 
	 * @return the enemy's current amount of health
	 */
	@Override
	public int getCurrentHealth() {
		return currentHealth;
	}

	/**
	 * This method returns the amount of experience the player should
	 * gain by killing this enemy. It is calculated based on how much
	 * health this enemy had.
	 * 
	 * @return the amount of experience the player should gain by killing
	 *         this enemy
	 */
	@Override
	public abstract int getExperienceGainOnDeath();

	/**
	 * This method returns the enemy's image.
	 * 
	 * @return the enemy's image
	 */
	@Override
	public BufferedImage getImage() {
		return image;
	}
}
