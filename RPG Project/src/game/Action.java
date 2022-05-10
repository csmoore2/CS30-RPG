package game;

/**
 * This enum declares all the actions the player can take during
 * their turn in a battle.
 */
public enum Action {
	// These are the basic hit (attack) options the player has
	HIT_WEAK  ("Weak Hit",   Type.HIT, 300, 0, 0, 0,   0),
	HIT_MEDIUM("Medium Hit", Type.HIT, 500, 0, 0, 150, 1),
	HIT_STRONG("Strong Hit", Type.HIT, 900, 0, 0, 400, 4),
	
	// These are the poison (multi-turn damage) attack options the player has
	POISON_WEAK  ("Weak Poison",   Type.POISON, 100, 0, 3, 0,   0 ),
	POISON_MEDIUM("Medium Poison", Type.POISON, 150, 0, 4, 300, 3 ),
	POISON_STRONG("Strong Poison", Type.POISON, 200, 0, 5, 600, 10),
	
	// These are the options the player has to heal themself
	HEALING_WEAK     ("Weak Healing",      Type.HEALING, 0, 250, 0, 200, 0),
	HEALING_STRONG   ("Strong Healing",    Type.HEALING, 0, 500, 0, 500, 6),
	HEALING_SUSTAINED("Sustained Healing", Type.HEALING, 0, 500, 4, 700, 8),
	
	// These are the options the player has to protect themself
	PROTECTION_WEAK  ("Weak Protection",   Type.PROTECTION, 0, 0.5, 3, 200, 0),
	PROTECTION_STRONG("Strong Protection", Type.PROTECTION, 0, 0.5, 5, 500, 5),
	
	// This is the player's special attack
	// TODO: Use lambda function to calculate damage based on player's attributes
	SPECIAL("Special Attack", Type.SPECIAL, -1, 0, 0, 1000, 0);
	
	/**
	 * This stores the name of this action.
	 */
	private final String name;
	
	/**
	 * This stores the type of action.
	 */
	private final Type type;
	
	/**
	 * This stores the amount of damage done by this action, or
	 * 0 if the action does no damage.
	 */
	private final int damage;
	
	/**
	 * This stores the defence effect of the action. If this action
	 * is a healing one then an integer, representing the amount of
	 * health gained, is stored here. If this action is a protection
	 * one then a double, representing the damage multiplier to attacks
	 * against the player, is stored here. Otherwise 0 is stored here.
	 */
	private final double defenceEffect;
	
	/**
	 * This stores the number of turns this action lasts, or 0
	 * if the attack does not last multiple turns.
	 */
	private final int numTurns;
	
	/**
	 * This stores the amount of mana required to use this action,
	 * or 0 is this action requires no mana.
	 */
	private final int manaCost;
	
	/**
	 * This stores the number of points the player must have in the
	 * ability attribute to use this action.
	 */
	private final int requiredAbilityPoints;
	
	/**
	 * This creates an Action using all of the given parameters as the
	 * values for its properties.
	 * 
	 * @param nameIn                  the name of this action
	 * @param typeIn                  the type of action
	 * @param damageIn                the amount of damage the action does
	 * @param defenceEffectIn         the defence effect of this action (either health healed or incoming damage multiplier)
	 * @param numTurnsIn              the number of turns this action lasts
	 * @param manaCostIn              the amount of mana required to use this action
	 * @param requiredAbilityPointsIn the number of points in the ability attribute the
	 *                                player must have to use this action
	 */
	private Action(String nameIn, Type typeIn, int damageIn, double defenceEffectIn, int numTurnsIn, int manaCostIn, int requiredAbilityPointsIn) {
		name = nameIn;
		type = typeIn;
		damage = damageIn;
		defenceEffect = defenceEffectIn;
		numTurns = numTurnsIn;
		manaCost = manaCostIn;
		requiredAbilityPoints = requiredAbilityPointsIn;
	}
	
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
	 * This method returns the amount of damage done by this action.
	 * 
	 * @return the amount of damage done by this action
	 */
	public int getDamage() {
		return damage;
	}
	
	/**
	 * This method returns the defence effect of this action. It will either be an
	 * integer representing the amount of health it heals, a double representing a
	 * multiplier applied to incoming damage, or 0 if this action has no defence effect.
	 * 
	 * @return the defence effect of this action
	 */
	public double getDefenceEffect() {
		return defenceEffect;
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
	public enum Type {
		HIT, POISON, HEALING, PROTECTION, SPECIAL
	}
}
