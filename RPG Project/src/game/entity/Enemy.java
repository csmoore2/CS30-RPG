package game.entity;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import game.Main;
import game.World;

/**
 * This class represents an enemy.
 */
public class Enemy implements IEnemy {
	/**
	 * This is the multiplier that is multiplied with the enemy's base attack damage
	 * to determine how much damage the enemy's poison attacks do each turn.
	 */
	public static final double POISON_DAMAGE_MULTIPLIER = 0.5;

	/**
	 * This is the world.
	 */
	private final World world;

	/**
	 * This is the enemy's image.
	 */
	private final BufferedImage image;

	/**
	 * This is the enemy's maximum amount of health.
	 */
	private final int maxHealth;

	/**
	 * This is the enemy's current amount of health.
	 */
	private int currentHealth;

	/**
	 * This stores the original number of healing potions that
	 * the enemy had.
	 */
	private final int originalNumHealingPotions;

	/**
	 * This is the number of healing potions the enemy has.
	 */
	private int numHealingPotions;

	/**
	 * This is the amount of health one healing potion gives
	 * the enemy.
	 */
	private final int healingPotionHealth;

	/**
	 * This is the base amount of damage dealt by an attack from this enemy.
	 */
	private final int baseAttackDamage;

	/**
	 * This is the number of turns that poison inflicted on the player by
	 * this enemy lasts.
	 */
	private final int numPoisonTurns;

	/**
	 * This is the chance of the enemy making a critical hit.
	 */
	private final double criticalChance;

	/**
	 * This is the chance of the enemy dodging an attack.
	 */
	private final double dodgeChance;

	/**
	 * This is the amount of damage per turn the enemy is taking from poison.
	 */
	private int poisonDamagePerTurn = 0;

	/**
	 * This is the number of turns remaining for which the enemy will be poisoned.
	 */
	private int numPoisonTurnsRemaining = 0;

	/**
	 * This constructs a sem-randomized enemy based on the amount of
	 * experience the player has.
	 * 
	 * @param worldIn   the world
	 * @param playerExp the amount of experience the playerHas
	 */
	public Enemy(World worldIn, int playerExp) {
		world = worldIn;

		// Try loading the enemy's image
		try {
			image = ImageIO.read(new File("res/test2.jpg"));
		} catch (IOException e) {
			throw new RuntimeException("Unable to load enemy's image!", e);
		}

		// TODO: Change and Balance Caculations

		// Calculate the enemy's maximum amount of health and start them off with full health
		maxHealth = (Main.RANDOM.nextInt((playerExp / 25) + 1) + 1) * 2000;
		currentHealth = maxHealth;

		// Calculate the number of healing potions the enemy should have
		originalNumHealingPotions = (playerExp / 50) + Main.RANDOM.nextInt(2);
		numHealingPotions = originalNumHealingPotions;

		// One healing potion should give the enemt 25% of their health back
		healingPotionHealth = (int)(0.25 * maxHealth);

		// Calculate the base damage dealt by an attack from this enemy
		baseAttackDamage = (Main.RANDOM.nextInt((playerExp / 50) + 1) * 100) + (playerExp*2) + 100;

		// Calculate the number of turns this enemy's poison attacks should last
		numPoisonTurns = playerExp >= 150 ? 3 : 2;

		// Calculate the enemy's chance of making a critical hit
		criticalChance = 0.015 * playerExp;

		// Calculate the enemy's chance of dodging an attack
		dodgeChance = 0.01 * playerExp;
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
	 * This method generates the enemy's action during a battle. The enemy's
	 * choice of action is randomly picked based on percentage chances.
	 * 
	 * @param player the player
	 * 
	 * @return the enemy's chosen action for their turn in a battle
	 * 
	 * @see IEnemy#generateBattleAction(Player)
	 */
	@Override
	public EnemyAction generateBattleAction(Player player) {
		// If the enemy has less than half their health then there
		// is a 25% chance they will use a healing potion if they
		// have one
		if (currentHealth < maxHealth/2 && numHealingPotions > 0) {
			int choice = Main.RANDOM.nextInt(100) + 1;
			
			// 1 to 25 = 25% chance
			if (choice <= 25) {
				numHealingPotions--;

				return new EnemyAction(
					"Healing Potion",
					Action.Type.HEALING,
					healingPotionHealth,
					0
				);
			}
		}

		// Choose a random number between 1 and 100 inclusive so the
		// percentages are easy
		int choice = Main.RANDOM.nextInt(100) + 1;

		// There is a 5% chance the enemy will use poison if the
		// player is not poisoned
		if (choice > 95 && !player.hasPoisonEffect()) {
			return new EnemyAction(
				"Poison Dagger",
				Action.Type.POISON,
				baseAttackDamage * POISON_DAMAGE_MULTIPLIER,
				numPoisonTurns
			);
		}

		// There is a 10% or 15% chance the enemy will use an attack
		// that does 50 damage more than their base attack
		if (choice > 85) {
			return new EnemyAction(
				"Sharp Sword",
				Action.Type.HIT,
				baseAttackDamage + 50,
				0
			);
		}

		// There is a 35% change the enemy will use an attack that does
		// 25 damage more than their base attack
		if (choice > 50) {
			return new EnemyAction(
				"Powerful Punch",
				Action.Type.HIT,
				baseAttackDamage + 25,
				0
			);
		}

		// Otherwise the enemy just does their base attack (50% chance)
		return new EnemyAction("Punch", Action.Type.HIT, baseAttackDamage, 0);
	}

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
	public int getExperienceGainOnDeath() {
		return (maxHealth / 1000) * 5;
	}

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
