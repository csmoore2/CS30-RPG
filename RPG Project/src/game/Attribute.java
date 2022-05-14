package game;

/**
 * This enum declares all the different primary and secondary attributes
 * the player and enemies can have.
 */
public enum Attribute {
	// Primary Attributes
	INTELLIGENCE("Intelligence"), HEALTH_POINTS("Health Points"), SPECIAL("Special"),
	ABILITIES("Abilities"),
	
	// Secondary Attributes
	MANA("Mana"), MANA_REGEN("Mana Regeneration"), CRIT_CHANCE("Critical Chance"),
	DODGE_CHANCE("Dodge Chance"), SPECIAL_DAMAGE("Special Attack Damage");
	
	/**
	 * This array contains all the primary attributes.
	 */
	public static final Attribute[] PRIMARY_ATTRIBUTES = new Attribute[] {
		INTELLIGENCE, HEALTH_POINTS, SPECIAL, ABILITIES
	};
	
	/**
	 * This array contains all the secondary attributes.
	 */
	public static final Attribute[] SECONDARY_ATTRIBUTES = new Attribute[] {
		MANA, MANA_REGEN, CRIT_CHANCE, DODGE_CHANCE, SPECIAL_DAMAGE
	};
	
	/**
	 * This is the name of the attribute.
	 */
	private final String name;
	
	/**
	 * This constructs a new attribute with the given name.
	 * 
	 * @param nameIn hte name of the attribute
	 */
	private Attribute(String nameIn) {
		name = nameIn;
	}
	
	/**
	 * This method returns the name of the attribute.
	 * 
	 * @return the name of the attribute
	 */
	public String getName() {
		return name;
	}
}
