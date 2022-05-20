package game.entity.enemy;

import game.Main;
import game.World;
import game.entity.Action;
import game.entity.EnemyAction;
import game.entity.Player;

/**
 * This class represents the final boss that the player faces
 * after defeating all of the bosses in the main zones and collecting
 * all of the keys.
 */
public class FinalBossEnemy extends Enemy {
	/**
	 * This constructs a semi-randomized boss based on the amount of
	 * experience the player has.
	 * 
	 * @param worldIn   the world
	 * @param playerExp the amount of experience the player has
	 */
	public FinalBossEnemy(World worldIn, int playerExp) {
		super(worldIn, playerExp);
	}

	/**
	 * This method initializes all of this boss's attributes.
	 * 
	 * @param playerExp the amount of experience the player currently has
	 * 
	 * @see Enemy#initializeAttributes(int)
	 */
	@Override
	protected void initializeAttributes(int playerExp) {
		// Calculate the boss's maximum amount of health and start them off with full health
		maxHealth = (Main.RANDOM.nextInt((playerExp / 25) + 1) + 1) * 4000;
		currentHealth = maxHealth;

		// Calculate the number of healing potions the boss should have
		originalNumHealingPotions = 1;
		numHealingPotions = originalNumHealingPotions;

		// One healing potion should give the boss 50% of their health back
		healingPotionHealth = (int)(0.5 * maxHealth);

		// Calculate the base damage dealt by an attack from this boss
		baseAttackDamage = (Main.RANDOM.nextInt((playerExp / 50) + 1) * 300) + (playerExp*2) + 100;

		// Calculate the number of turns this boss's poison attacks should last
		numPoisonTurns = playerExp >= 300 ? 3 : 2;

		// Calculate the boss's chance of making a critical hit
		criticalChance = 0.025 * playerExp;

		// Calculate the boss's chance of dodging an attack
		dodgeChance = 0.01 * playerExp;
	}

	/**
	 * This method generates the boss's action during a battle. The boss's
	 * choice of action is randomly picked based on percentage chances.
	 * 
	 * @param player the player
	 * 
	 * @return the boss's chosen action for their turn in a battle
	 * 
	 * @see Enemy#generateBattleAction(Player)
	 */
	@Override
	public EnemyAction generateBattleAction(Player player) {
		// If the boss has less than half their health then there
		// is a 50% chance they will use a healing potion if they
		// have one
		if (currentHealth < maxHealth/2 && numHealingPotions > 0) {
			int choice = Main.RANDOM.nextInt(100) + 1;
			
			// 1 to 50 = 50% chance
			if (choice <= 50) {
				numHealingPotions--;

				return new EnemyAction(
					"Heavenly Healing",
					Action.Type.HEALING,
					healingPotionHealth,
					0
				);
			}
		}

		// Choose a random number between 1 and 100 inclusive so the
		// percentages are easy
		int choice = Main.RANDOM.nextInt(100) + 1;

		// There is a 5% chance the boss will use poison if the
		// player is not poisoned
		if (choice > 95 && !player.hasPoisonEffect()) {
			return new EnemyAction(
				"Interstellar Poison",
				Action.Type.POISON,
				baseAttackDamage * POISON_DAMAGE_MULTIPLIER,
				numPoisonTurns
			);
		}

		// There is a 10% or 15% chance the boss will use an attack
		// that does 100 damage more than their base attack
		if (choice > 85) {
			return new EnemyAction(
				"Supernova Destruction",
				Action.Type.HIT,
				baseAttackDamage + 100,
				0
			);
		}

		// There is a 35% change the boss will use an attack that does
		// 50 damage more than their base attack
		if (choice > 50) {
			return new EnemyAction(
				"Galactic Hit",
				Action.Type.HIT,
				baseAttackDamage + 50,
				0
			);
		}

		// Otherwise the boss just does their base attack (50% chance)
		return new EnemyAction("Star Punch", Action.Type.HIT, baseAttackDamage, 0);
	}

	/*************************************************************************************/
	/*                                      GETTERS                                      */
	/*************************************************************************************/

	/**
	 * This method returns the amount of experience the player should
	 * gain by killing this boss. For the final boss this is zero since
	 * the player has won the game and no longer needs any experience.
	 * 
	 * @return the amount of experience the player should gain by killing
	 *         this boss
	 * 
	 * @see Enemy#getExperienceGainOnDeath()
	 */
	@Override
	public int getExperienceGainOnDeath() {
		return 0;
	}
}
