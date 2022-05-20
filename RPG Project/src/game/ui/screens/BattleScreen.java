package game.ui.screens;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SpringLayout;

import game.Main;
import game.World;
import game.entity.Action;
import game.entity.Attribute;
import game.entity.EnemyAction;
import game.entity.Player;
import game.entity.PlayerAction;
import game.entity.enemy.IEnemy;
import game.ui.QuantityBar;

import static javax.swing.SpringLayout.*;

public class BattleScreen implements IScreen {
	/**
	 * This is the size that each status effect icon will be scaled to be.
	 */
	public static final int STATUS_EFFECT_ICON_SIZE = 50;

	/**
	 * This is the width we want the player's and enemy's images to have.
	 */
	public static final int TARGET_PICTURE_WIDTH = 200;
	
	/**
	 * This is the height we want the player's and enemy's images to have.
	 */
	public static final int TARGET_PICTURE_HEIGHT = 200;

	/**
	 * This is the x-position that the player's image is drawn at in the screen.
	 */
	public static final int PLAYER_IMAGE_X_POS = 15;

	/**
	 * This is the x-position that the enemy's image is drawn at in the screen.
	 */
	public static final int ENEMY_IMAGE_X_POS = Main.SCREEN_WIDTH - TARGET_PICTURE_WIDTH - 25;

	/**
	 * This is the y-position that the player's and enemy's images are drawn at
	 * in the screen.
	 */
	public static final int IMAGE_Y_POS = 240;
	
	/**
	 * This is the default colour for text.
	 */
	public static final Color DEFAULT_TEXT_COLOUR = Color.WHITE;
	
	/**
	 * This is the font that will be used to display the turn text in the background.
	 */
	public static final Font TURN_TEXT_FONT = new Font("Turn Text Font", Font.BOLD, 48);
	
	/**
	 * This is the font that will be used to display labels for objects.
	 */
	public static final Font LABEL_TEXT_FONT = new Font("Label Text Font", Font.BOLD, 20);

	/**
	 * This is the icon that will be displayed under the player's image to
	 * indicate they have an active protection effect.
	 */
	private Image protectionEffectIcon;

	/**
	 * This is the icon that will be displayed under the player's image to
	 * indicate they have an active healing effect.
	 */
	private Image healingEffectIcon;

	/**
	 * This is the icon that will be displayed under the player's image to
	 * indicate they have an active poison effect.
	 */
	private Image poisonEffectIcon;
	
	/**
	 * This is the transformation that will be applied to the player's image so
	 * that it is the size we want it to be.
	 */
	private final AffineTransformOp playerImageScaleOp;
	
	/**
	 * This is the transformation that will be applied to the enemy's image so
	 * that it is the size we want it to be.
	 */
	private final AffineTransformOp enemyImageScaleOp;

	/**
	 * This is the tabbed pane that contains all the buttons representing the
	 * player's available actions while in battle.
	 */
	private JTabbedPane playerBattleOptions;
	
	/**
	 * This map contains the button associated with each action the player can make. This is used
	 * to easily update the state of the buttons to reflect what the player can and cannot do at
	 * the current state of the battle.
	 */
	private final Map<Action, JButton> actionButtonMap = new HashMap<>();
	
	/**
	 * This stores the JLabel that displays whose turn it is.
	 */
	private JLabel playerTurnText = new JLabel();

	/**
	 * This is the label for the player's mana bar.
	 */
	private JLabel playerManaLabel;
	
	/**
	 * This is the QuantityBar displaying the amount of mana the player has.
	 */
	private QuantityBar playerManaBar;

	/**
	 * This is the label for the player's health bar.
	 */
	private JLabel playerHealthLabel;
	
	/**
	 * This is the QuantityBar displaying the amount of health the player has.
	 */
	private QuantityBar playerHealthBar;

	/**
	 * This is the label for the enemy's health bar.
	 */
	private JLabel enemyHealthLabel;
	
	/**
	 * This is the QuantityBar displaying the amount of health the enemy has.
	 */
	private QuantityBar enemyHealthBar;

	/**
	 * This is the world.
	 */
	private final World world;
	
	/**
	 * This is the player.
	 */
	private final Player player;
	
	/**
	 * This is the enemy.
	 */
	private final IEnemy enemy;
	
	/**
	 * This keeps track of whether or not it is the player's turn.
	 */
	private boolean playerTurn = true;
	
	/**
	 * This creates a BattleScreen using the given World, Player,
	 * and enemy.
	 * 
	 * @param worldIn  the world
	 * @param playerIn the player
	 * @param enemyIn  the enemy
	 */
	public BattleScreen(World worldIn, Player playerIn, IEnemy enemyIn) {
		world = worldIn;
		player = playerIn;
		enemy = enemyIn;
		
		/******************************************************************
		 *                      STATUS EFFECT IMAGES                      *
		 ******************************************************************/

		// Try to load the images for each status effect
		try {
			// Try to load the image for the protection effect
			protectionEffectIcon = ImageIO.read(new File("res/shield.png"));
			protectionEffectIcon = protectionEffectIcon.getScaledInstance(
				STATUS_EFFECT_ICON_SIZE,
				STATUS_EFFECT_ICON_SIZE,
				Image.SCALE_SMOOTH
			);

			// Try to load the image for the healing effect
			healingEffectIcon = ImageIO.read(new File("res/heart.png"));
			healingEffectIcon = healingEffectIcon.getScaledInstance(
				STATUS_EFFECT_ICON_SIZE,
				STATUS_EFFECT_ICON_SIZE,
				Image.SCALE_SMOOTH
			);

			// Try to load the image for the poison effect
			poisonEffectIcon = ImageIO.read(new File("res/poison.png"));
			poisonEffectIcon = poisonEffectIcon.getScaledInstance(
				STATUS_EFFECT_ICON_SIZE,
				STATUS_EFFECT_ICON_SIZE,
				Image.SCALE_SMOOTH
			);
		} catch (IOException e) {
			throw new RuntimeException("Unable to load status effect icon!", e);
		}
		
		/******************************************************************
		 *                          PLAYER IMAGE                          *
		 ******************************************************************/

		// Determine how the player's image will need to be scaled so that it is the right size
		double playerImageScaleX = (double)TARGET_PICTURE_WIDTH  / (double)playerIn.getImage().getWidth();
		double playerImageScaleY = (double)TARGET_PICTURE_WIDTH / (double)playerIn.getImage().getHeight();
		
		// Create a transformation that will be applied to the player's image so it is
		// the right size
		AffineTransform playerTransform = new AffineTransform();
		playerTransform.scale(playerImageScaleX, playerImageScaleY);
		
		// Create the transformation operation
		playerImageScaleOp = new AffineTransformOp(playerTransform, AffineTransformOp.TYPE_BILINEAR);
		
		/******************************************************************
		 *                          ENEMY IMAGE                           *
		 ******************************************************************/

		// Determine how the enemy's image will need to be scaled so that it is the right size
		double enemyImageScaleX = (double)TARGET_PICTURE_WIDTH  / (double)enemyIn.getImage().getWidth();
		double enemyImageScaleY = (double)TARGET_PICTURE_WIDTH / (double)enemyIn.getImage().getHeight();
		
		// Create a transformation that will be applied to the enemy's image so it is
		// the right size
		AffineTransform enemyTransform = new AffineTransform();
		enemyTransform.scale(enemyImageScaleX, enemyImageScaleY);
		
		// Create the transformation operation
		enemyImageScaleOp = new AffineTransformOp(enemyTransform, AffineTransformOp.TYPE_BILINEAR);
	}

	/*************************************************************************************/
	/*                                UI CREATION METHODS                                */
	/*************************************************************************************/

	/**
	 * This method is used by this class to create all the java
	 * swing components used by the battle screen and to add them
	 * to the screen.
	 * 
	 * @param screen the screen
	 * @param layout the screen's layout
	 * 
	 * @see IScreen#addSwingComponents(JComponent, SpringLayout)
	 */
	@Override
	public void addSwingComponents(Container screen, SpringLayout layout) {
		/******************************************************************
		 *                             ACTIONS                            *
		 ******************************************************************/
		
		// Create a tabbed pane that holds all the actions the player can take on their turn
		// during the battle. Each tab represents a different category of actions.
		playerBattleOptions = new JTabbedPane(JTabbedPane.TOP);
		playerBattleOptions.setPreferredSize(new Dimension(150, 150));

		// Create the JPanels that correspond to each tab. They are created with a GridLayout
		// with 0 rows so there can be any number of rows but only 1 column.
		JPanel hitOptions     = new JPanel(new GridLayout(0, 1));
		JPanel poisonOptions  = new JPanel(new GridLayout(0, 1));
		JPanel defenceOptions = new JPanel(new GridLayout(0, 1));
		JPanel specialOptions = new JPanel(new GridLayout(0, 1));
		
		// Loop through each possible action the player has and add them the their corresponding JPanel
		for (PlayerAction action : PlayerAction.PLAYER_BATTLE_ACTIONS) {
			// Create the button representing the action
			JButton actionButton = new JButton(generateEffectStringForAction(action));

			// Tell the button to perform the action as the player when it is clicked
			actionButton.addActionListener((a) -> performPlayerAction(action));
			
			// Add the action to the appropriate JPanel as a JButton according to its type and use
			// the previously generated text as the button's label
			switch (action.getType()) {
				// Hit actions are added to the 'hitOptions' JPanel
				case HIT:
					hitOptions.add(actionButton);
					break;

				// Poison actions are added to the 'poisonOptions' JPanel
				case POISON:
					poisonOptions.add(actionButton);
					break;

				// Healing and protection actions are added to the 'defenceOptions' JPanel
				case HEALING:
				case PROTECTION:
					defenceOptions.add(actionButton);
					break;

				// Special actions are added to the 'specialOptions' JPanel
				case SPECIAL:
					specialOptions.add(actionButton);
					break;
			}
			
			// Add the button to the action button map
			actionButtonMap.put(action, actionButton);
		}
		
		// Update the state of each action's button
		updateActions();
		
		// Add the panels with the actions to the tabbed pane as tabs
		playerBattleOptions.addTab("Hit", hitOptions);
		playerBattleOptions.addTab("Poison", poisonOptions);
		playerBattleOptions.addTab("Defence", defenceOptions);
		playerBattleOptions.addTab("Special", specialOptions);
		
		// Align the tabbed pane so it is in the bottom centre of the screen
		layout.putConstraint(SOUTH, playerBattleOptions, -25, SOUTH, screen);
		layout.putConstraint(WEST, playerBattleOptions, 200, WEST, screen);
		layout.putConstraint(EAST, playerBattleOptions, -200, EAST, screen);
		
		// Add the tabbed pane with all the different possible actions to the world so it is drawn
		screen.add(playerBattleOptions);
		
		/******************************************************************
		 *                           TURN TEXT                            *
		 ******************************************************************/
		
		// Update the text of the JLabel displaying whose turn it is and style it
		playerTurnText.setForeground(DEFAULT_TEXT_COLOUR);
		playerTurnText.setFont(TURN_TEXT_FONT);
		updateTurnText();
		
		// Center the turn text in the screen
		layout.putConstraint(VERTICAL_CENTER, playerTurnText, -25, VERTICAL_CENTER, screen);
		layout.putConstraint(HORIZONTAL_CENTER, playerTurnText, 0, HORIZONTAL_CENTER, screen);
		
		// Add the turn text JLabel to the world so it will be drawn
		screen.add(playerTurnText);
		
		/******************************************************************
		 *                         PLAYER MANA BAR                        *
		 ******************************************************************/
		
		// Create a QuantityBar displaying the player's mana and a label that will be displayed beside it
		playerManaBar = new QuantityBar(0, (int)player.getSecondaryAttributeValue(Attribute.MANA));
		playerManaBar.setStringPainted(true);
		
		playerManaLabel = new JLabel();
		playerManaLabel.setForeground(DEFAULT_TEXT_COLOUR);
		playerManaLabel.setText("Mana:");
		playerManaLabel.setFont(LABEL_TEXT_FONT);
		
		// Position the label above the battle options on the left side
		layout.putConstraint(SOUTH, playerManaLabel, -5, NORTH, playerBattleOptions);
		layout.putConstraint(WEST, playerManaLabel, 0, WEST, playerBattleOptions);
		
		// Position the player's mana bar right of the label and bounded by the right side of the battle options
		layout.putConstraint(VERTICAL_CENTER, playerManaBar, 0, VERTICAL_CENTER, playerManaLabel);
		layout.putConstraint(WEST, playerManaBar, 10, EAST, playerManaLabel);
		layout.putConstraint(EAST, playerManaBar, 0, EAST, playerBattleOptions);
		
		// Add the player's mana bar and label to the world so they will be drawn
		screen.add(playerManaLabel);
		screen.add(playerManaBar);
		
		/******************************************************************
		 *                        PLAYER HEALTH BAR                       *
		 ******************************************************************/
		
		// Create a QuantityBar displaying the player's health and a label that will be displayed beside it
		playerHealthBar = new QuantityBar(0, (int)player.getSecondaryAttributeValue(Attribute.HEALTH_POINTS));
		playerHealthBar.setStringPainted(true);
		
		playerHealthLabel = new JLabel();
		playerHealthLabel.setForeground(DEFAULT_TEXT_COLOUR);
		playerHealthLabel.setText("HP:");
		playerHealthLabel.setFont(LABEL_TEXT_FONT);
		
		// Position the label above the mana bar label
		layout.putConstraint(SOUTH, playerHealthLabel, -5, NORTH, playerManaLabel);
		layout.putConstraint(EAST, playerHealthLabel, 0, EAST, playerManaLabel);
		
		// Position the player's health bar beside the label and bounded by the right side of the battle options
		layout.putConstraint(VERTICAL_CENTER, playerHealthBar, 0, VERTICAL_CENTER, playerHealthLabel);
		layout.putConstraint(WEST, playerHealthBar, 10, EAST, playerHealthLabel);
		layout.putConstraint(EAST, playerHealthBar, 0, EAST, playerBattleOptions);
		
		// Add the player's health bar and label to the world so they will be drawn
		screen.add(playerHealthLabel);
		screen.add(playerHealthBar);
		
		/******************************************************************
		 *                        ENEMY HEALTH BAR                        *
		 ******************************************************************/
		
		// Create a QuantityBar displaying the enemy's health and a label that will be displayed beside it
		//UIManager.put("ProgressBar.horizontalSize", new DimensionUIResource(300, 300));
		enemyHealthBar = new QuantityBar(0, (int)enemy.getSecondaryAttributeValue(Attribute.HEALTH_POINTS));
		enemyHealthBar.setStringPainted(true);
		
		enemyHealthLabel = new JLabel();
		enemyHealthLabel.setForeground(DEFAULT_TEXT_COLOUR);
		enemyHealthLabel.setText("Enemy HP:");
		enemyHealthLabel.setFont(LABEL_TEXT_FONT);
		
		// Position the label at the top of the screen but with the same horizontal alignment as the
		// player's attributes' labels
		layout.putConstraint(NORTH, enemyHealthLabel, 10, NORTH, screen);
		layout.putConstraint(WEST, enemyHealthLabel, 0, WEST, playerManaLabel);
		
		// Position the enemy's health bar beside the label and bounded by the right side of the battle options
		layout.putConstraint(VERTICAL_CENTER, enemyHealthBar, 0, VERTICAL_CENTER, enemyHealthLabel);
		layout.putConstraint(WEST, enemyHealthBar, 10, EAST, enemyHealthLabel);
		layout.putConstraint(EAST, enemyHealthBar, 0, EAST, playerBattleOptions);
		
		// Add the player's health bar and label to the world so they will be drawn
		screen.add(enemyHealthLabel);
		screen.add(enemyHealthBar);
		
		// Update the player's and enemy's health and mana bars
		updateHealthAndManaBars();
	}

	/**
	 * This method removes all the ui components previously added by
	 * us to the screen.
	 * 
	 * @param screen the screen
	 * 
	 * @see IScreen#removeSwingComponents(JComponent)
	 */
	@Override
	public void removeSwingComponents(Container screen) {
		// Remove the player's available actions and the turn text
		screen.remove(playerBattleOptions);
		screen.remove(playerTurnText);

		// Remove the player's mana bar
		screen.remove(playerManaLabel);
		screen.remove(playerManaBar);

		// Remove the player's health bar
		screen.remove(playerHealthLabel);
		screen.remove(playerHealthBar);

		// Remove the enemy's health bar
		screen.remove(enemyHealthLabel);
		screen.remove(enemyHealthBar);
	}

	/*************************************************************************************/
	/*                             UI STATE UPDATING METHODS                             */
	/*************************************************************************************/
	
	/**
	 * This method updates the text of the JLabel displaying whose turn
	 * it is to reflect whose turn it currently is.
	 */
	private void updateTurnText() {
		playerTurnText.setText(playerTurn ? "Your Turn!" : "Enemy Turn!");
	}
	
	/**
	 * This method updates the state of each action's associated button based on the
	 * player's current attributes and the state of the battle.
	 */
	private void updateActions() {
		for (Entry<Action, JButton> actionButtonPair : actionButtonMap.entrySet()) {
			Action action = actionButtonPair.getKey();

			// This list contains the reasons that the action is disabled
			List<String> disabledReasons = new ArrayList<>();
			
			// Determine whether or not the player can do this action based on the number of ability points required. the mana cost,
			// and whose turn it is
			boolean buttonEnabled = player.getPrimaryAttributeValue(Attribute.ABILITIES) >= action.getRequiredAbilityPoints()
								 && player.getCurrentMana() >= action.getManaCost()
								 && playerTurn;

			// If the button was disabled because the player does not have enough ability
			// points then add it to the list of reasons that the button is disabled
			if (player.getPrimaryAttributeValue(Attribute.ABILITIES) < action.getRequiredAbilityPoints()) {
				disabledReasons.add(String.format(
					"%d ability points required",
					action.getRequiredAbilityPoints()
				));
			}

			// If the button was disabled because the player does not have enough mana then add
			// it to the list of reasons that the button is disabled
			if (player.getCurrentMana() < action.getManaCost()) {
				disabledReasons.add("not enough mana");
			}
			
			// If the action is a poison action and the enemy is already poisoned then
			// stop the player from stacking poison effects
			if (action.getType() == Action.Type.POISON) {
				buttonEnabled = buttonEnabled && !enemy.hasPoisonEffect();

				// If the button was disabled because the enemy is already poisoned then add
				// it to the list of reasons that the button is disabled
				if (enemy.hasPoisonEffect()) {
					disabledReasons.add("enemy already poisoned");
				}
			}

			// If the action is a sustained healing action or a protection action and the
			// player already has a sustained healing effect or protection effect then stop
			// the player from stacking defence effects
			if ((action.getType() == Action.Type.HEALING && action.getNumTurns() > 1) || action.getType() == Action.Type.PROTECTION) {
				buttonEnabled = buttonEnabled && !player.hasHealingEffect() && !player.hasProtectionEffect();

				// If the button was disabled because the player already has a multi-turn healing
				// or protection effect then add it to the list of reasons that the button is disabled
				if (player.hasHealingEffect() || player.hasProtectionEffect()) {
					disabledReasons.add("cannot stack effects");
				}
			}
			
			// Update the button's state 
			actionButtonPair.getValue().setEnabled(buttonEnabled);

			// If the button is disabled then append the reasons it is disabled to its text
			// instead of the actions effects. Otherwise display the action and its effects
			if (!buttonEnabled) {
				// Combine all the reasons the button is disabled into one string
				String disabledReasonStr = Arrays.toString(disabledReasons.toArray());

				// Append the reasons to the end of the button's text
				actionButtonPair.getValue().setText(action.getName() + ' ' + disabledReasonStr);
			} else {
				actionButtonPair.getValue().setText(generateEffectStringForAction(action));
			}
		}
	}

	/**
	 * This method generates a string that contains all of the given
	 * action's effects.
	 * 
	 * @param action the action whose effect string we are building
	 * 
	 * @return a string that contains all of the given action's effects
	 */
	private String generateEffectStringForAction(Action action) {
		// Build the string that contains all of the action's effects. This starts with
		// the action's name and its mana cost
		String effectString = action.getName();
		effectString += String.format(" (Cost: %d mana", action.getManaCost());
		
		// Hit, poison, and special actions are all attacks and deal damage.
		// Healing actions give the player health.
		// Protection actions reduce incoming damage by a multiplier
		if (action.getType() == Action.Type.HIT || action.getType() == Action.Type.POISON || action.getType() == Action.Type.SPECIAL) {
			effectString += String.format(", Damage: %d", (int)action.getEffect(player));
		} else if (action.getType() == Action.Type.HEALING) {
			effectString += String.format(", +%dHP", (int)action.getEffect(player));
		} else if (action.getType() == Action.Type.PROTECTION) {
			effectString += String.format(", %.2fx incoming damage", action.getEffect(player));
		}
		
		// If the action lasts for multiple turns it should be indicated in the effect string
		if (action.getNumTurns() > 1) {
			effectString += String.format(" for %d turns", action.getNumTurns());
		}
		
		// Add the closing parenthesis
		effectString += ')';

		// Return the result
		return effectString;
	}
	
	/**
	 * This method updates the state of the player's and enemy's mana and health bars
	 * to reflect the amount of mana and health they currently have.
	 */
	private void updateHealthAndManaBars() {
		// Update the player's health and mana bars
		playerHealthBar.setValue(player.getCurrentHealth());
		playerManaBar.setValue(player.getCurrentMana());

		// Update the enemy's health bar
		enemyHealthBar.setValue(enemy.getCurrentHealth());
	}

	/*************************************************************************************/
	/*                                     PAINTING                                      */
	/*************************************************************************************/
	
	/**
	 * This method paints the battle screen using the given instance of
	 * Graphics2D.
	 * 
	 * @param g2d the instance of Graphics2D to use to draw the battle screen
	 */
	@Override
	public void paint(Graphics2D g2d) {
		// Draw background image
		g2d.setColor(Color.BLACK);
		g2d.fillRect(0, 0, Main.SCREEN_WIDTH, Main.SCREEN_HEIGHT);
		
		// Draw the player
		g2d.drawImage(player.getImage(), playerImageScaleOp, PLAYER_IMAGE_X_POS, IMAGE_Y_POS);

		// Draw the enemy
		g2d.drawImage(enemy.getImage(), enemyImageScaleOp, ENEMY_IMAGE_X_POS, IMAGE_Y_POS);

		// If the player has a protection effect draw the protection icon below their image
		// to the left of the centre icon (healing)
		if (player.hasProtectionEffect()) {
			g2d.drawImage(
				protectionEffectIcon,
				PLAYER_IMAGE_X_POS  + (TARGET_PICTURE_WIDTH / 2) - (STATUS_EFFECT_ICON_SIZE / 2) - 10 - STATUS_EFFECT_ICON_SIZE,
				IMAGE_Y_POS + TARGET_PICTURE_HEIGHT + 10,
				null,
				null
			);
		}

		// If the player has a healing effect draw the healing icon below their image in the centre
		if (player.hasHealingEffect()) {
			g2d.drawImage(
				healingEffectIcon,
				PLAYER_IMAGE_X_POS + (TARGET_PICTURE_WIDTH / 2) - (STATUS_EFFECT_ICON_SIZE / 2),
				IMAGE_Y_POS + TARGET_PICTURE_HEIGHT + 10,
				null,
				null
			);
		}

		// If the player has a poison effect draw the poison icon below their image to the right
		// of the centre icon (healing)
		if (player.hasPoisonEffect()) {
			g2d.drawImage(
				poisonEffectIcon,
				PLAYER_IMAGE_X_POS + (TARGET_PICTURE_WIDTH / 2) - (STATUS_EFFECT_ICON_SIZE / 2) + 10 + STATUS_EFFECT_ICON_SIZE,
				IMAGE_Y_POS + TARGET_PICTURE_HEIGHT + 10,
				null,
				null
			);
		}

		// If the enemy has a poison effect draw the poison icon below their image in the centre
		if (enemy.hasPoisonEffect()) {
			g2d.drawImage(
				poisonEffectIcon,
				ENEMY_IMAGE_X_POS + (TARGET_PICTURE_WIDTH / 2) - (STATUS_EFFECT_ICON_SIZE / 2),
				IMAGE_Y_POS + TARGET_PICTURE_HEIGHT + 10,
				null,
				null
			);
		}
	}

	/*************************************************************************************/
	/*                               BATTLE LOGIC METHODS                                */
	/*************************************************************************************/

	/**
	 * This method performs the given action that was chosen
	 * by the player
	 * 
	 * @param action the action to perform
	 */
	private void performPlayerAction(PlayerAction action) {
		// Display a message about the action on the screen
		world.showMessage(String.format("Player used %s.", action.getName()), 2);

		// Remove the action's mana cost from the player's mana
		player.removeMana(action.getManaCost());

		// Perform the action's player and enemy effects
		action.applyPlayerEffect(world, player, enemy);
		action.applyEnemyEffect(world, enemy, player);

		// Switch to the enemy's turn
		changeTurns();
	}

	/**
	 * This method performs the given action that was generated
	 * by the enemy.
	 * 
	 * @param action the action to perform
	 */
	private void performEnemyAction(EnemyAction action) {
		// Display a message about the action on the screen
		world.showMessage(String.format("Enemy used %s.", action.getName()), 2);

		// Perform the action's enemy and player effects
		action.applyPlayerEffect(world, player, enemy);
		action.applyEnemyEffect(world, enemy, player);

		// Switch to the player's turn
		changeTurns();
	}

	/**
	 * This method switches whose turn it is.
	 */
	private void changeTurns() {
		// Switch the playerTurn variable
		playerTurn = !playerTurn;

		// Call the onBattleTurn method for whoever's turn it now is
		if (playerTurn) {
			player.onBattleTurn();
		} else {
			enemy.onBattleTurn();
		}

		// Update the turn text
		updateTurnText();

		// Update the player's action's buttons
		updateActions();

		// Update the player's and enemy's health and mana bars
		updateHealthAndManaBars();
	}

	/**
	 * This method updates the current state of the battle. It checks is there is
	 * a winner and will perform the enemy's action when it is their turn.
	 */
	public void update() {
		// Check if the battle is over and if so then exit the battle
		if (player.isDead() || enemy.isDead()) {
			world.exitBattle(player.isDead(), enemy.getExperienceGainOnDeath(), enemy);
		} else {
			// Otherwise, if it is the enemy's turn then pause for half a second so
			// that the enemy's turn is not over in a matter of milliseconds
			// and then perform the action generated by the enemy
			if (!playerTurn) {
				try {
					Thread.sleep(500);
					performEnemyAction(enemy.generateBattleAction(player));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
		}
	}
}
