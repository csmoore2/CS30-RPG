package game.entity.enemy;

import game.Main;
import game.World;
import game.entity.Action;
import game.entity.EnemyAction;
import game.entity.Player;

/**
 * This class represents an enemy that the player can randomly encounter
 * while moving through the world.
 */
public class RandomEncounterEnemy extends Enemy {
	/**
	 * This constructs a semi-randomized enemy based on the amount of
	 * experience the player has.
	 * 
	 * @param worldIn   the world
	 * @param playerExp the amount of experience the player has
	 */
	public RandomEncounterEnemy(World worldIn, int playerExp) {
		super(worldIn, playerExp);
	}

	/**
	 * This method initializes all of this enemy's attributes.
	 * 
	 * @param playerExp the amount of experience the player currently has
	 * 
	 * @see Enemy#initializeAttributes(int)
	 */
	@Override
	protected void initializeAttributes(int playerExp) {
		// Calculate the enemy's maximum amount of health and start them off with full health
		maxHealth = (Main.RANDOM.nextInt((playerExp / 25) + 1) + 1) * 2000;
		currentHealth = maxHealth;

		// Calculate the number of healing potions the enemy should have
		originalNumHealingPotions = (playerExp / 50) + Main.RANDOM.nextInt(2);
		numHealingPotions = originalNumHealingPotions;

		// One healing potion should give the enemy 20% of their health back
		healingPotionHealth = (int)(0.2 * maxHealth);

		// Calculate the base damage dealt by an attack from this enemy
		baseAttackDamage = (Main.RANDOM.nextInt((playerExp / 50) + 1) * 100) + (playerExp*2) + 100;

		// Calculate the number of turns this enemy's poison attacks should last
		numPoisonTurns = playerExp >= 150 ? 3 : 2;

		// Calculate the enemy's chance of making a critical hit
		criticalChance = 0.01 * playerExp;

		// Calculate the enemy's chance of dodging an attack
		dodgeChance = 0.005 * playerExp;
	}

	/**
	 * This method generates the enemy's action during a battle. The enemy's
	 * choice of action is randomly picked based on percentage chances.
	 * 
	 * @param player the player
	 * 
	 * @return the enemy's chosen action for their turn in a battle
	 * 
	 * @see Enemy#generateBattleAction(Player)
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
				"Poisoned Dagger",
				Action.Type.POISON,
				baseAttackDamage * POISON_DAMAGE_MULTIPLIER,
				numPoisonTurns
			);
		}

		// There is a 10% or 15% chance the enemy will use an attack
		// that does 50 damage more than their base attack
		if (choice > 85) {
			return new EnemyAction(
				"Ice Shard",
				Action.Type.HIT,
				baseAttackDamage + 50,
				0
			);
		}

		// There is a 35% change the enemy will use an attack that does
		// 25 damage more than their base attack
		if (choice > 50) {
			return new EnemyAction(
				"Boulder Bash",
				Action.Type.HIT,
				baseAttackDamage + 25,
				0
			);
		}

		// Otherwise the enemy just does their base attack (50% chance)
		return new EnemyAction("Magic Bolt", Action.Type.HIT, baseAttackDamage, 0);
	}

	/*************************************************************************************/
	/*                                      GETTERS                                      */
	/*************************************************************************************/

	/**
	 * This method returns the amount of experience the player should
	 * gain by killing this enemy. For a random encounter the player
	 * does not gain much experience.
	 * 
	 * @return the amount of experience the player should gain by killing
	 *         this enemy
	 * 
	 * @see Enemy#getExperienceGainOnDeath()
	 */
	@Override
	public int getExperienceGainOnDeath() {
		return (maxHealth / 1000) * 5;
	}
}
