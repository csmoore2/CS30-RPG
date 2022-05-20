package game.entity;

import game.World;

/**
 * This enum declares all the actions the player can take during
 * their turn in a battle.
 */
public abstract sealed class Action permits PlayerAction, EnemyAction {
	/**
	 * This is the damage multiplier that is used if a critical hit
	 * is achieved.
	 */
	public static final double CRITICAL_DAMAGE_MULTIPLIER = 1.5;

	/**
	 * This stores the name of this action.
	 */
	protected final String name;
	
	/**
	 * This stores the type of action.
	 */
	protected final Type type;

	/**
	 * This is a number representing the effect of this action. Its
	 * meaning is determined by the action's type.
	 */
	protected final double effect;
	
	/**
	 * This stores the number of turns this action lasts, or 0
	 * if the attack does not last multiple turns.
	 */
	protected final int numTurns;
	
	/**
	 * This stores the amount of mana required to use this action,
	 * or 0 is this action requires no mana.
	 */
	protected final int manaCost;
	
	/**
	 * This stores the number of points the player must have in the
	 * ability attribute to use this action.
	 */
	protected final int requiredAbilityPoints;
	
	/**
	 * This creates an Action using all of the given parameters as the
	 * values for its properties.
	 * 
	 * @param nameIn                  the name of this action
	 * @param typeIn                  the type of action
	 * @param effectIn                a number representing the action's effect (its meaning
	 *                                is determined by the action's type)
	 * @param numTurnsIn              the number of turns this action lasts
	 * @param manaCostIn              the amount of mana required to use this action
	 * @param requiredAbilityPointsIn the number of points in the ability attribute the
	 *                                player must have to use this action
	 */
	protected Action(String nameIn, Type typeIn, double effectIn, int numTurnsIn, int manaCostIn, int requiredAbilityPointsIn) {
		name = nameIn;
		type = typeIn;
		effect = effectIn;
		numTurns = numTurnsIn;
		manaCost = manaCostIn;
		requiredAbilityPoints = requiredAbilityPointsIn;
	}

	/**
	 * This method applys the effect that this method has on
	 * the player to the player. For a player's action this
	 * will either be nothing or a positive effect. For an
	 * enemy's action this will either be nothing or a
	 * negative effect
	 * 
	 * @param world  the world
	 * @param player the player
	 * @param enemy  the enemy
	 */
	public abstract void applyPlayerEffect(World world, Player player, IEnemy enemy);

	/**
	 * This method applys the effect that this method has on
	 * the enemy to the enemy. For a player's action this
	 * will either be nothing or a negative effect. For an
	 * enemy's action this will either be nothing or a
	 * positive effect
	 * 
	 * Note: the player is passed to this method in case the effect
	 *       of the action is based on the player's attributes
	 * 
	 * @param world  the world
	 * @param enemy  the enemy
	 * @param player the player
	 */
	public abstract void applyEnemyEffect(World world, IEnemy enemy, Player player);
	
	/**
	 * This method returns the name of this action.
	 * 
	 * @return the name of this action
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * This method returns this action's type.
	 * 
	 * @return the action's type
	 */
	public Type getType() {
		return type;
	}

	/**
	 * This method returns the number representing this action's effect.
	 * 
	 * Note: a player is provided since the damage dealt by the player's
	 *       special attack is a secondary attribute
	 * 
	 * @param player the player
	 * 
	 * @return the number representing this action's effect
	 */
	public double getEffect(Player player) {
		// If this is a special action and this is a player action then
		// return the value of the player's SPECIAL_DAMAGE attribute
		if (type == Type.SPECIAL && this instanceof PlayerAction) {
			return player.getSecondaryAttributeValue(Attribute.SPECIAL_DAMAGE);
		} else {
			return effect;
		}
	}
	
	/**
	 * This method returns the number of turns this actions lasts, or 0 if this
	 * action does not last multiple turns.
	 * 
	 * @return the number of turns this action lasts
	 */
	public int getNumTurns() {
		return numTurns;
	}
	
	/**
	 * This method returns the amount of mana required to use this action.
	 * 
	 * @return the amount of mana required to use this action
	 */
	public int getManaCost() {
		return manaCost;
	}
	
	/**
	 * This method returns the number of points the player must have in the
	 * abilities attribute to use this action.
	 * 
	 * @return the number of points the player must have in the
	 *         abilities attribute to use this action
	 */
	public int getRequiredAbilityPoints() {
		return requiredAbilityPoints;
	}


	/**
	 * This enum specifies the different types of actions that
	 * can be performed by the player during a battle.
	 */
	public static enum Type {
		HIT, POISON, HEALING, PROTECTION, SPECIAL
	}
}
