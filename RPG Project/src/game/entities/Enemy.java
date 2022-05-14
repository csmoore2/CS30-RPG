package game.entities;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;

import game.Attribute;

/**
 * This class represents an enemy.
 */
public class Enemy implements ILivingEntity {
	/**
	 * This is the instance of Random that will be used by every instance of Enemy
	 * when generating random information. It is seeded by the current time in milliseconds.
	 */
	public static final Random RANDOM = new Random(System.currentTimeMillis());

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
	 * This is the number of healing potions the enemy has.
	 */
	private int numHealingPotions;

	/**
	 * This is the amount of damage dealt by an attack from this enemy.
	 */
	private int attackDamage;

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
	 * @param playerExp the amount of experience the playerHas
	 */
	public Enemy(int playerExp) {
		// Try loading the enemy's image
		try {
			image = ImageIO.read(new File("res/test2.jpg"));
		} catch (IOException e) {
			throw new RuntimeException("Unable to load enemy's image!", e);
		}

		// TODO: Change and Balance Caculations

		// Calculate the enemy's maximum amount of health and start them off with full health
		maxHealth = (RANDOM.nextInt((playerExp / 25) + 1) + 1) * 2000;
		currentHealth = maxHealth;

		// Calculate the number of healing potions the enemy should have
		numHealingPotions = (playerExp / 50) + RANDOM.nextInt(3);

		// Calculate the damage dealt by an attack from this enemy
		attackDamage = (RANDOM.nextInt((playerExp / 50) + 1) * 100) + (playerExp*2) + 100;

		// Calculate the enemy's chance of making a critical hit
		criticalChance = 0.015 * playerExp;

		// Calculate the enemy's chance of dodging an attack
		dodgeChance = 0.01 * playerExp;
	}

	@Override
	public void setPrimaryAttributeValue(Attribute attr, int newValue) {
		throw new RuntimeException("Enemies do not have primary attributes!");
	}

	@Override
	public int getPrimaryAttributeValue(Attribute attr) {
		throw new RuntimeException("Enemies do not have primary attributes!");
	}

	@Override
	public double getSecodaryAttributeValue(Attribute attr) {
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
					String.format("Attribute %s is unsupported by enemies!", attr.name()));
		}
	}

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
			inflictDamage(poisonDamagePerTurn);
			numPoisonTurnsRemaining--;
		}
	}

	/**
	 * This method returns the enemy's current amount of health.
	 * 
	 * @return the enemy's current amount of health
	 */
	@Override
	public int getCurrentHealth() {
		return currentHealth;
	}

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
