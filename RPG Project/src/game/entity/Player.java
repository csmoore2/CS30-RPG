package game.entity;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;


import javax.imageio.ImageIO;

import game.Main;
import game.Walls;
import game.World;
import game.ui.Tile;
import game.ui.screens.AreaScreen;
import game.util.KeyPressedListener;

/**
 * This class represents the player. It is responsible for keeping
 * track of the player's attributes and position, updating the player's
 * position, and drawing the player.
 */
public class Player implements ILivingEntity {
	/**
	 * These players represent the different classes the player will have to choose from.
	 */
	public static final Player[] PREMADE_PLAYERS = new Player[3];
	
	/**
	 * This is called when this class is loaded. Here it is used to initialize the different
	 * classes the player can choose from.
	 */
	static {
		// A try-catch statement is used in case there is a problem loading one of the images
		try {
			PREMADE_PLAYERS[0] = new Player("Mage 1", ImageIO.read(new File("res/char1.png")), 2, 1, 1, 0);
			PREMADE_PLAYERS[1] = new Player("Mage 2", ImageIO.read(new File("res/char2.png")), 2, 1, 1, 0);
			PREMADE_PLAYERS[2] = new Player("Mage 3", ImageIO.read(new File("res/char3.png")), 2, 1, 1, 0);
		} catch (IOException e) {
			// If there was an error loading one of the images then we need to quit the game
			throw new RuntimeException("Unable to load image for character!", e);
		}
	}

	/**
	 * This is the initial amount of mana the player starts with at
	 * the beginning of each battle.
	 */
	public static final int INITIAL_MANA = 500;

	/**
	 * This is the number of attribute points the player starts with.
	 */
	public static final int NUM_INITIAL_ATTR_POINTS = 4;

	/**
	 * This is the number of experience points the player has to gain
	 * to level up.
	 */
	public static final int EXPERIENCE_PER_LEVEL = 50;

	/**
	 * This is the number of attribute points the player gets to spend
	 * each time they level up.
	 */
	public static final int ATTR_POINTS_PER_LEVEL = 2;
	
	/**
	 * The minimum possible x-position of the player.
	 */
	public static final int MIN_X_POS = 0;
	
	/**
	 * The minimum possible y-position of the player.
	 */
	public static final int MIN_Y_POS = 0;
	
	/**
	 * The maximum possible x-position of the player.
	 */
	public static final int MAX_X_POS = AreaScreen.TILES_PER_ROW - 1;
	
	/**
	 * The maximum possible y-position of the player.
	 */
	public static final int MAX_Y_POS = AreaScreen.ROWS_OF_TILES - 1;
	
	/**
	 * The player's name.
	 */
	private String name = "";
	
	/**
	 * The player's class.
	 */
	private final String classType;

	/**
	 * This is the player's character's image.
	 */
	private final BufferedImage image;

	/**
	 * This is the transformation operation that will be applied to the image when it is
	 * drawn so that it fits within one tile. Player facing the right direction.
	 */
	private final AffineTransformOp rightImageScaleOp;
	
	/**
	 * This is the transformation operation that will be applied to the image when it is
	 * drawn so that it fits within one tile. Player facing the left direction.
	 */
	private final AffineTransformOp leftImageScaleOp;
	
	/**
	 * The x-position of the player in the world.
	 */
	public int xPos = 0;
	
	/**
	 * The y-position of the player in the world.
	 */
	public int yPos = 0;
	
	/**
	 * The world the player is in.
	 */
	private World world = null;
	
	/**
	 * This is the player's current amount of experience.
	 */
	private int experience = 0;
	
	/**
	 * This is the value of the player's intelligence primary attribute.
	 */
	private int intelligenceAttr;

	/**
	 * This is the value of the player's health primary attribute.
	 */
	private int healthAttr;

	/**
	 * This is the value of the player's special primary attribute.
	 */
	private int specialAttr;

	/**
	 * This is the value of the player's abilities primary attribute.
	 */
	private int abilitiesAttr;
	
	/**
	 * This stores the player's current amount of mana.
	 */
	private int currentMana = INITIAL_MANA;
	
	/**
	 * This stores the player's current amount of health.
	 */
	private int currentHealth;

	/**
	 * This is the amount of damage per turn the player is taking from poison.
	 */
	private int poisonDamagePerTurn = 0;

	/**
	 * This is the number of turns remaining for which the player will be poisoned.
	 */
	private int numPoisonTurnsRemaining = 0;

	/**
	 * This is the amount of health per turn the player is gaining from a healing effect.
	 */
	private int healingPerTurn = 0;

	/**
	 * This is the number of turns remaining for which the player has the sustained healing effect.
	 */
	private int numHealingTurnsRemaining = 0;

	/**
	 * This is the multiplier that is being applied to incoming damage to the player due
	 * to a protection effect.
	 */
	private double incomingDamageMultiplier = 0;

	/**
	 * This is the number of turns remaining for which the player has the protection effect.
	 */
	private int numProtectionTurnsRemaining = 0;
	
	/**
	 * A boolean to tell whether or not the character is currently facing the right of the screen
	 */
	private boolean facingRight = true;
	
	/**
	 * This constructs a dummy player with the given values for its attributes.
	 * 
	 * @param intelligenceIn this is the value of the player's intelligence attribute
	 * @param healthPointsIn this is the value of the player's health points attribute
	 * @param specialIn      this is the value of the player's special attribute
	 * @param abilitiesIn    this is the value of the player's abilities attribute
	 */
	public Player(int intelligenceIn, int healthPointsIn, int specialIn, int abilitiesIn) {
		// Assign values for the player's attributes
		intelligenceAttr = intelligenceIn;
		healthAttr = healthPointsIn;
		specialAttr = specialIn;
		abilitiesAttr = abilitiesIn;

		// These variabes are unnecessary for a dummy player
		classType = null;
		image = null;
		leftImageScaleOp = null;
		rightImageScaleOp = null;
	}
	
	/**
	 * This constructs a player based off of the given parameters.
	 * 
	 * @param classTypeIn this is the player's class
	 * @param imageIn     this is the player's character's image
	 * @param intelligenceIn this is the initial value of the player's intelligence attribute
	 * @param healthPointsIn this is the initial value of the player's health points attribute
	 * @param specialIn      this is the initial value of the player's special attribute
	 * @param abilitiesIn    this is the initial value of the player's abilities attribute
	 */
	private Player(String classTypeIn, BufferedImage imageIn, int intelligenceIn, int healthPointsIn, int specialIn, int abilitiesIn) {
		// Assign the player's class and image
		classType = classTypeIn;
		image = imageIn;
		
		// Assign initial values for the player's attributes
		intelligenceAttr = intelligenceIn;
		healthAttr = healthPointsIn;
		specialAttr = specialIn;
		abilitiesAttr = abilitiesIn;

		// Determine the x and y scales we will need to use to scale the image to the size of one tile
		double imageXScale = (double)Tile.TILE_SIZE / (double)image.getWidth();
		double imageYScale = (double)Tile.TILE_SIZE / (double)image.getHeight();

		// Create the transformation to do the scale
		AffineTransform scaleTransform = new AffineTransform();
		scaleTransform.scale(imageXScale, imageYScale);

		// Create the right-sided transformation operation
		rightImageScaleOp = new AffineTransformOp(scaleTransform, AffineTransformOp.TYPE_BILINEAR);
		
		// Reset the transformation
		scaleTransform = new AffineTransform();
		scaleTransform.translate((double) Tile.TILE_SIZE,0.0);
		scaleTransform.scale(-imageXScale, imageYScale);
		
		// Create the left-sided transformation operation
		leftImageScaleOp = new AffineTransformOp(scaleTransform, AffineTransformOp.TYPE_BILINEAR);
		
		// Set the player's current amount of health to its maximum
		currentHealth = (int)getSecondaryAttributeValue(Attribute.HEALTH_POINTS);
	}
	
	/**
	 * This method sets the player's world and registers us
	 * as a listener for key events.
	 * 
	 * @param worldIn the player's new world
	 */
	public void setWorld(World worldIn) {
		// If we already have a world then throw an exception since we
		// cannot be in two worlds
		if (world != null) {
			throw new IllegalStateException("The player is already in a world!");
		}

		// Set the player's new world
		world = worldIn;
		
		// Register the 'movementKeyPressedListener' method as a listener for
		// key pressed events
		world.registerKeyListener((KeyPressedListener)this::movementKeyPressedListener);
	}

	/*************************************************************************************/
	/*                                 GRAPHICS METHODS                                  */
	/*************************************************************************************/
	
	/**
	 * This method paints the player at their current position using
	 * the given instance of Graphics2D.
	 * 
	 * @param g2d the instance of Graphics2D to use to paint the player
	 */
	public void paint(Graphics2D g2d) {
		// Draw the player's character's image at the correct position and scaled so that
		// it fits in one tile
		if (facingRight)
		{
			g2d.drawImage(image, rightImageScaleOp, xPos*Tile.TILE_SIZE, yPos*Tile.TILE_SIZE);
		} else {
			g2d.drawImage(image, leftImageScaleOp, xPos*Tile.TILE_SIZE, yPos*Tile.TILE_SIZE);
		}
		
	}

	/**
	 * This method returns the player's image.
	 * 
	 * @return the player's image
	 */
	@Override
	public BufferedImage getImage() {
		return image;
	}

	/*************************************************************************************/
	/*                                    ATTRIBUTES                                     */
	/*************************************************************************************/

	@Override
	public void setPrimaryAttributeValue(Attribute attr, int newValue) {
		// Update the given primary attribute or, if the given attribute
		// is a secondary attribute, throw an IllegalArgumentException
		switch (attr) {
			case INTELLIGENCE:
				intelligenceAttr = newValue;
				break;
				
			case HEALTH:
				healthAttr = newValue;
				break;
				
			case SPECIAL:
				specialAttr = newValue;
				break;
				
			case ABILITIES:
				abilitiesAttr = newValue;
				break;
				
			default:
				throw new IllegalArgumentException(
					"Only primary attributes can be used with this method!");
		}
	}
	
	@Override
	public int getPrimaryAttributeValue(Attribute attr) {
		// Return the value of the given primary attribute or, if the given
		// attribute is a secondary attribute, throw an IllegalArgumentException
		switch (attr) {
			case INTELLIGENCE:
				return intelligenceAttr;
				
			case HEALTH:
				return healthAttr;
				
			case SPECIAL:
				return specialAttr;
				
			case ABILITIES:
				return abilitiesAttr;
				
			default:
				throw new IllegalArgumentException(
					"Only primary attributes can be used with this method!");
		}
	}
	
	@Override
	public double getSecondaryAttributeValue(Attribute attr) {
		// Calculate the value of the given secondary attribute or, if the given
		// attribute is a primary attribute, throw an IllegalArgumentException
		switch (attr) {
			case HEALTH_POINTS:
				return 1000 + (1000 * healthAttr);
			
			case MANA:
				return 500 + (500 * intelligenceAttr);
			
			case MANA_REGEN:
				return 100 + (50 * intelligenceAttr);
			
			case CRIT_CHANCE:
				return 0.05 + (0.01 * intelligenceAttr) + (0.02 * abilitiesAttr);
			
			case DODGE_CHANCE:
				return 0.05 + (0.02 * intelligenceAttr) + (0.01 * abilitiesAttr);
			
			case SPECIAL_DAMAGE:
				return 800 + (100 * specialAttr);
				
			default:
				throw new IllegalArgumentException(
					"Only secondary attributes can be used with this method!");
		}
	}

	/**
	 * This method adds the given amount of mana to the player's
	 * current amount of mana, ensuring that the player does not
	 * have more than their maximum amount of mana.
	 * 
	 * @param amount the amount of mana to add to the player
	 */
	public void addMana(int amount) {
		currentMana = Math.min(currentMana + amount, (int)getSecondaryAttributeValue(Attribute.MANA));
	}

	/**
	 * This method removes the given amount of mana from the player's
	 * current amount of mana.
	 * 
	 * @param amount the amount of mana to remove from the player
	 */
	public void removeMana(int amount) {
		currentMana -= amount;
	}

	/**
	 * This methods gives the player the specified amount of health,
	 * ensuring that the player does not have more than their maximum
	 * amount of health.
	 * 
	 * @param amount the amount of health to give the player
	 * 
	 * @see ILivingEntity#addHealth(int)
	 */
	@Override
	public void addHealth(int amount) {
		// Give the player the specified amount of health but cap the player's health at its maximum
		currentHealth = Math.min(currentHealth + amount, (int)getSecondaryAttributeValue(Attribute.HEALTH_POINTS));
	}

	/*************************************************************************************/
	/*                                 BATTLE LISTENERS                                  */
	/*************************************************************************************/
	
	/**
	 * This method is called when the player enters a battle so the player's
	 * state can be modified accordingly.
	 */
	public void onBattleStart() {
		// Reset the player's mana to the amount it should be at the start
		// of a battle
		currentMana = INITIAL_MANA;

		// Reset the player's health to its maximum value
		currentHealth = (int)getSecondaryAttributeValue(Attribute.HEALTH_POINTS);
	}

	/**
	 * This method is called at the start of the player's turn
	 * during a battle. It is responsible for updating the player's
	 * status effects.
	 */
	@Override
	public void onBattleTurn() {
		// If the player is poisoned then deal the poison damage
		// and decrement the number of turns remaining
		if (hasPoisonEffect()) {
			inflictDamage(poisonDamagePerTurn);
			numPoisonTurnsRemaining--;
		}

		// If the player has an active healing effect then give them the health
		// they should gain and decrement the number of turns remaining
		if (hasHealingEffect()) {
			addHealth(healingPerTurn);
			numHealingTurnsRemaining--;
		}

		// If the player has an active protection effect then decrement the
		// number of turns remaining
		if (hasProtectionEffect()) {
			numProtectionTurnsRemaining--;
		}

		// Regenerate part of the player's mana
		addMana((int)getSecondaryAttributeValue(Attribute.MANA_REGEN));
	}

	/*************************************************************************************/
	/*                                   BATTLE METHODS                                  */
	/*************************************************************************************/
	
	/**
	 * This method applys a healing effect to the player. The player
	 * will receive the specified amount of health each turn (including
	 * this one) for the specified number of turns.
	 * 
	 * @param healthPerTurn the amount of health the player gains each turn
	 * @param numTurns      the number of turns this effect lasts
	 */
	public void applyHealingEffect(int healthPerTurnIn, int numTurns) {
		// Give the player the specified amount of health this turn
		addHealth(healthPerTurnIn);

		// If the effect lasts multiple turns then update the appropriate variables
		if (numTurns > 0) {
			healingPerTurn = healthPerTurnIn;
			numHealingTurnsRemaining = numTurns;
		}
	}

	/**
	 * This method returns whether or not the player has an active
	 * healing effect.
	 * 
	 * @return whether or not the player has an active healing effect
	 */
	public boolean hasHealingEffect() {
		return numHealingTurnsRemaining > 0;
	}

	/**
	 * This method applies a protection effect to the player. Any incoming
	 * damage to the player will be multiplied by the specified multiplier
	 * for the specified number of turns.
	 * 
	 * @param incomingDamageMultiplierIn the multiplier to multiply incoming damage by
	 * @param numTurns                   the number of turns this effect lasts
	 */
	public void applyProtectionEffect(double incomingDamageMultiplierIn, int numTurns) {
		incomingDamageMultiplier = incomingDamageMultiplierIn;
		numProtectionTurnsRemaining = numTurns;
	}

	/**
	 * This method returns whether or not the player has an active
	 * protection effect.
	 * 
	 * @return whether or not the player has an active protection effect
	 */
	public boolean hasProtectionEffect() {
		return numProtectionTurnsRemaining > 0;
	}
 
	/**
	 * This method inflicts the specified amount of damege on the
	 * player after accounting for any active protection effects the
	 * player has.
	 * 
	 * @param damage the amount of damage to inflict on the player
	 */
	@Override
	public void inflictDamage(int damage) {
		// If the player has an active protection effect then multiply the amount
		// of damage by the protection multiplier
		if (hasProtectionEffect()) {
			damage *= incomingDamageMultiplier;
		}

		// Inflict the given amount of damage but do not let the player's health
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
		// The player is dead if they have no health left
		return currentHealth == 0;
	}

	/*************************************************************************************/
	/*                                    LVELLING UP                                    */
	/*************************************************************************************/

	/**
	 * This method returns the total number of attribute points
	 * invested in the player's attributes.
	 * 
	 * @return the total number of attribute points invested in
	 *         the player's attributes
	 */
	private int getTotalNumberOfAttributePoints() {
		return intelligenceAttr + healthAttr + specialAttr + abilitiesAttr;
	}

	/**
	 * This method returns the number of attribute points the
	 * player has gained from their experience points.
	 * 
	 * @return the number of attribute points the player has
	 *         gained from their experience points
	 */
	private int getAttributePointsFromExperience() {
		return (experience / EXPERIENCE_PER_LEVEL) * ATTR_POINTS_PER_LEVEL;
	}

	/**
	 * This method returns whether or not the player can level
	 * up (whether they have attribute points available to spend).
	 * 
	 * @return whether or not the player can level up
	 */
	public boolean canLevelUp() {
		int numAvailableAttributePoints = NUM_INITIAL_ATTR_POINTS + getAttributePointsFromExperience();
		return numAvailableAttributePoints - getTotalNumberOfAttributePoints() > 0;
	}

	/**
	 * This method returns the number of attribute points that the
	 * player has to spend on their attributes.
	 * 
	 * @return the number of attribute points the player has
	 *         available to spend
	 */
	public int getNumSpendingAttrPoints() {
		int numAvailableAttributePoints = NUM_INITIAL_ATTR_POINTS + getAttributePointsFromExperience();
		return numAvailableAttributePoints - getTotalNumberOfAttributePoints();
	}

	/*************************************************************************************/
	/*                                     MOVEMENT                                      */
	/*************************************************************************************/
	
	/**
	 * This method updates the player's position to the given coordinates.
	 * 
	 * @param newX the player's new x-position
	 * @param newY the player's new y-position
	 */
	public void updatePosition(int newX, int newY, boolean loading) {
		// Update our position
	
		if (Walls.arrays[Main.currentLevel-1][newX][newY] != 1)
			{
			//newX, newY
			xPos = newX;
			yPos = newY;
			if (loading)
			{
				// Inform the world of our change in position
				world.onPlayerPositionChange(newX, newY);}
			};
	}

	/**
	 * This method is invoked when a key is pressed. This class
	 * uses this event to update the player's position.
	 * 
	 * Note: This method is invoked on the Event Dispatch Thread (EDT)
	 * 
	 * @param e the key pressed event
	 * 
	 * @see KeyListener#keyPressed(KeyEvent)
	 */
	private void movementKeyPressedListener(KeyEvent e) {
		// If the player is not in a world then their position cannot change
		if (world == null) return;
		
		// Do not allow movement if an overlay is currently being displayed,
		// an area screen is not being displayed, or the player is dead
		if (world.isOverlayDisplayed() || !world.isAreaDisplayed() || isDead()) return;
		
		// Update the player's position according the the key pressed
		switch (e.getKeyChar()) {
			// Vertical Movement (0 = top)
			case 'w':
				updatePosition(xPos, Math.max(yPos-1, MIN_Y_POS), true);
				break;
			case 's':
				updatePosition(xPos, Math.min(yPos+1, MAX_Y_POS), true);
				break;

			// Horizontal Movement (0 = Right)
			case 'a':
				updatePosition(Math.max(xPos-1, MIN_X_POS), yPos, true);
				facingRight = false;
				break;
			case 'd':
				updatePosition(Math.min(xPos+1, MAX_X_POS), yPos, true);
				facingRight = true;
				break;
				
			// If the key was not one that updates the player's position
			// then we do not want to continue executing past this point
			default: return;
		}
		
		// Tell the world it needs to perform a repaint
		world.markRepaintRequired();
	}

	/*************************************************************************************/
	/*                                GETTERS AND SETTERS                                */
	/*************************************************************************************/

	/**
	 * This method sets the player's name.
	 * 
	 * @param name the player's new name
	 */
	public void setName(String nameIn) {
		name = nameIn;
	}

	/**
	 * This method returns the player's name.
	 * 
	 * @return the player's name
	 */
	public String getName() {
		return name;
	}

	/**
	 * This method returns the player's class.
	 * 
	 * @return the player's class
	 */
	public String getClassType() {
		return classType;
	}

	/**
	 * This method returns the player's x-position.
	 * 
	 * @return the player's x-position
	 */
	public int getX() {
		return xPos;
	}

	/**
	 * This method returns the player's y-position.
	 * 
	 * @return the player's y-position
	 */
	public int getY() {
		return yPos;
	}

	/**
	 * This method adds the given amount of experience to the
	 * player's amount of experience.
	 * 
	 * @param amount the amount of experience to give the player
	 */
	public void addExperience(int amount) {
		experience += amount;
	}

	/**
	 * This method returns the player's current amount of experience.
	 * 
	 * @return the amount of experience the player has
	 */
	public int getExperience() {
		return experience;
	}
	
	/**
	 * This method returns the player's current amount of mana.
	 * 
	 * @return the player's current amount of mana
	 */
	public int getCurrentMana() {
		return currentMana;
	}
	
	/**
	 * This method returns the player's current amount of health.
	 * 
	 * @return the player's current amount of health
	 */
	@Override
	public int getCurrentHealth() {
		return currentHealth;
	}
}
