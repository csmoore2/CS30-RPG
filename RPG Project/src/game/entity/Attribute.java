package game.entity;

/**
 * This enum declares all the different primary and secondary attributes
 * the player and enemies can have.
 */
public enum Attribute {
	// Primary Attributes
	HEALTH("Health"), INTELLIGENCE("Intelligence"), SPECIAL("Special"),
	ABILITIES("Abilities"),
	
	// Secondary Attributes
	HEALTH_POINTS("Health Points"), MANA("Max Mana"), MANA_REGEN("Mana Regeneration"),
	CRIT_CHANCE("Critical Chance"), DODGE_CHANCE("Dodge Chance"), SPECIAL_DAMAGE("Special Attack Damage");
	
	/**
	 * This array contains all the primary attributes.
	 */
	public static final Attribute[] PRIMARY_ATTRIBUTES = new Attribute[] {
		HEALTH, INTELLIGENCE, SPECIAL, ABILITIES
	};
	
	/**
	 * This array contains all the secondary attributes.
	 */
	public static final Attribute[] SECONDARY_ATTRIBUTES = new Attribute[] {
		HEALTH_POINTS, MANA, MANA_REGEN, CRIT_CHANCE, DODGE_CHANCE, SPECIAL_DAMAGE
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
	
	/**
	 * This method returns the string that should be displayed to the user
	 * to represent the given value of the given attribute.
	 * 
	 * Example: a value of 0.02 for CRIT_CHANCE should display as 2%
	 * 
	 * @param attr  the attribute whose value we are representing
	 * @param value the value of the given attribute
	 * 
	 * @return the string that should be displayed to the user to represent
	 *         the given value of the given attribute
	 */
	public static String getDisplayString(Attribute attr, double value) {
		// Return the formatted string based on which attribute was given
		switch (attr) {
			// Primary attributes are not modified
			case HEALTH:
			case INTELLIGENCE:
			case SPECIAL:
			case ABILITIES:
				return String.valueOf((int)value);
				
			// Health points is displayed with HP after the value
			case HEALTH_POINTS:
				return String.format("%d HP", (int)value);
				
			// Mana is not modified
			case MANA:
				return String.valueOf((int)value);
				
			// Mana regen is displayed with "/turn" after the value
			case MANA_REGEN:
				return String.format("%d/turn", (int)value);
				
			// Critical chance and dodge chance are displayed as percentages
			case CRIT_CHANCE:
			case DODGE_CHANCE:
				return String.format("%d%%", (int)(value * 100));
			
			// Special attack damage is not modified
			case SPECIAL_DAMAGE:
				return String.valueOf((int)value);
			
			// This case should never be reached because this switch statement is exhaustive
			// but it is required to make java happy
			default:
				throw new IllegalArgumentException("An invalid attribute was given!");
		}
	}
}
