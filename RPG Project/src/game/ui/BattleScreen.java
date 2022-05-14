package game.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.util.EnumMap;
import java.util.Map.Entry;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;

import game.Action;
import game.Attribute;
import game.Main;
import game.World;
import game.entities.ILivingEntity;
import game.entities.Player;

import static javax.swing.SpringLayout.*;

public class BattleScreen implements IScreen {
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
	public static final int PLAYER_IMAGE_X_POS = 240;

	/**
	 * This is the x-position that the enemy's image is drawn at in the screen.
	 */
	public static final int ENEMY_IMAGE_X_POS = 840;

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
	 * This map contains the button associated with each action the player can make. This is used
	 * to easily update the state of the buttons to reflect what the player can and cannot do at
	 * the current state of the battle.
	 */
	private final EnumMap<Action, JButton> actionButtonMap = new EnumMap<>(Action.class);
	
	/**
	 * This stores the JLabel that displays whose turn it is.
	 */
	private final JLabel playerTurnText = new JLabel();
	
	/**
	 * This is the QuantityBar displaying the amount of health the player has.
	 */
	private final QuantityBar playerHealthBar;
	
	/**
	 * This is the QuantityBar displaying the amount of mana the player has.
	 */
	private final QuantityBar playerManaBar;
	
	/**
	 * This is the QuantityBar displaying the amount of health the enemy has.
	 */
	private final QuantityBar enemyHealthBar;
	
	/**
	 * This is the player.
	 */
	private final Player player;
	
	/**
	 * This is the enemy.
	 */
	private final ILivingEntity enemy;
	
	/**
	 * This keeps track of whether or not it is the player's turn.
	 */
	private boolean playerTurn = true;
	
	/**
	 * This creates a BattleScreen using the given World, Player, and enemy (ILivingEntity).
	 * 
	 * @param worldIn  the world
	 * @param playerIn the player
	 * @param enemyIn  the enemy
	 */
	public BattleScreen(World worldIn, Player playerIn, ILivingEntity enemyIn) {
		player = playerIn;
		enemy = enemyIn;

		// TODO: Change to use new methods in IScreen
		
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
		
		/******************************************************************
		 *                             ACTIONS                            *
		 ******************************************************************/
		
		// Create a tabbed pane that holds all the actions the player can take on their turn
		// during the battle. Each tab represents a different category of actions.
		JTabbedPane playerBattleOptions = new JTabbedPane(JTabbedPane.TOP);
		playerBattleOptions.setPreferredSize(new Dimension(150, 150));

		// Create the JPanels that correspond to each tab. They are created with a GridLayout
		// with 0 rows so there can be any number of rows but only 1 column.
		JPanel hitOptions     = new JPanel(new GridLayout(0, 1));
		JPanel poisonOptions  = new JPanel(new GridLayout(0, 1));
		JPanel defenceOptions = new JPanel(new GridLayout(0, 1));
		JPanel specialOptions = new JPanel(new GridLayout(0, 1));
		
		// Loop through each possible action and add them the their corresponding JPanel
		for (Action action : Action.values()) {
			// Build the text the button corresponding to this action will display. This starts with
			// the action's name and its mana cost
			String buttonText = action.getName();
			buttonText += String.format(" (Cost: %d mana", action.getManaCost());
			
			// Hit, poison, and special actions are all attacks and deal damage.
			// Healing actions give the player health.
			// Protection actions reduce incoming damage by a multiplier
			if (action.getType() == Action.Type.HIT || action.getType() == Action.Type.POISON || action.getType() == Action.Type.SPECIAL) {
				buttonText += String.format(", Damage: %d", action.getDamage());
			} else if (action.getType() == Action.Type.HEALING) {
				buttonText += String.format(", +%dHP", (int)action.getDefenceEffect());
			} else if (action.getType() == Action.Type.PROTECTION) {
				buttonText += String.format(", %.2fx incoming damage", action.getDefenceEffect());
			}
			
			// If the action lasts for multiple turns it should be indicated on the button's text
			if (action.getNumTurns() > 1) {
				buttonText += String.format(" for %d turns", action.getNumTurns());
			}
			
			// Add the closing parenthesis
			buttonText += ')';
			
			// Create the button representing the action
			JButton actionButton = new JButton(buttonText);

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
		SpringLayout worldLayout = worldIn.getSpringLayout();
		worldLayout.putConstraint(SOUTH, playerBattleOptions, -25, SOUTH, worldIn);
		worldLayout.putConstraint(WEST, playerBattleOptions, 200, WEST, worldIn);
		worldLayout.putConstraint(EAST, playerBattleOptions, -200, EAST, worldIn);
		
		// Add the tabbed pane with all the different possible actions to the world so it is drawn
		worldIn.add(playerBattleOptions);
		
		/******************************************************************
		 *                           TURN TEXT                            *
		 ******************************************************************/
		
		// Update the text of the JLabel displaying whose turn it is and style it
		playerTurnText.setForeground(DEFAULT_TEXT_COLOUR);
		playerTurnText.setFont(TURN_TEXT_FONT);
		updateTurnText();
		
		// Center the turn text in the screen
		worldLayout.putConstraint(VERTICAL_CENTER, playerTurnText, -25, VERTICAL_CENTER, worldIn);
		worldLayout.putConstraint(HORIZONTAL_CENTER, playerTurnText, 0, HORIZONTAL_CENTER, worldIn);
		
		// Add the turn text JLabel to the world so it will be drawn
		worldIn.add(playerTurnText);
		
		/******************************************************************
		 *                         PLAYER MANA BAR                        *
		 ******************************************************************/
		
		// Create a QuantityBar displaying the player's mana and a label that will be displayed beside it
		playerManaBar = new QuantityBar(0, (int)playerIn.getSecodaryAttributeValue(Attribute.MANA));
		playerManaBar.setStringPainted(true);
		
		JLabel playerManaLabel = new JLabel();
		playerManaLabel.setForeground(DEFAULT_TEXT_COLOUR);
		playerManaLabel.setText("Mana:");
		playerManaLabel.setFont(LABEL_TEXT_FONT);
		
		// Position the label above the battle options on the left side
		worldLayout.putConstraint(SOUTH, playerManaLabel, -5, NORTH, playerBattleOptions);
		worldLayout.putConstraint(WEST, playerManaLabel, 0, WEST, playerBattleOptions);
		
		// Position the player's mana bar right of the label and bounded by the right side of the battle options
		worldLayout.putConstraint(VERTICAL_CENTER, playerManaBar, 0, VERTICAL_CENTER, playerManaLabel);
		worldLayout.putConstraint(WEST, playerManaBar, 10, EAST, playerManaLabel);
		worldLayout.putConstraint(EAST, playerManaBar, 0, EAST, playerBattleOptions);
		
		// Add the player's mana bar and label to the world so they will be drawn
		worldIn.add(playerManaLabel);
		worldIn.add(playerManaBar);
		
		/******************************************************************
		 *                        PLAYER HEALTH BAR                       *
		 ******************************************************************/
		
		// Create a QuantityBar displaying the player's health and a label that will be displayed beside it
		playerHealthBar = new QuantityBar(0, (int)playerIn.getSecodaryAttributeValue(Attribute.HEALTH_POINTS));
		playerHealthBar.setStringPainted(true);
		
		JLabel playerHealthLabel = new JLabel();
		playerHealthLabel.setForeground(DEFAULT_TEXT_COLOUR);
		playerHealthLabel.setText("HP:");
		playerHealthLabel.setFont(LABEL_TEXT_FONT);
		
		// Position the label above the mana bar label
		worldLayout.putConstraint(SOUTH, playerHealthLabel, -5, NORTH, playerManaLabel);
		worldLayout.putConstraint(EAST, playerHealthLabel, 0, EAST, playerManaLabel);
		
		// Position the player's health bar beside the label and bounded by the right side of the battle options
		worldLayout.putConstraint(VERTICAL_CENTER, playerHealthBar, 0, VERTICAL_CENTER, playerHealthLabel);
		worldLayout.putConstraint(WEST, playerHealthBar, 10, EAST, playerHealthLabel);
		worldLayout.putConstraint(EAST, playerHealthBar, 0, EAST, playerBattleOptions);
		
		// Add the player's health bar and label to the world so they will be drawn
		worldIn.add(playerHealthLabel);
		worldIn.add(playerHealthBar);
		
		/******************************************************************
		 *                        ENEMY HEALTH BAR                        *
		 ******************************************************************/
		
		// Create a QuantityBar displaying the enemy's health and a label that will be displayed beside it
		//UIManager.put("ProgressBar.horizontalSize", new DimensionUIResource(300, 300));
		enemyHealthBar = new QuantityBar(0, (int)enemyIn.getSecodaryAttributeValue(Attribute.HEALTH_POINTS));
		enemyHealthBar.setStringPainted(true);
		
		JLabel enemyHealthLabel = new JLabel();
		enemyHealthLabel.setForeground(DEFAULT_TEXT_COLOUR);
		enemyHealthLabel.setText("Enemy HP:");
		enemyHealthLabel.setFont(LABEL_TEXT_FONT);
		
		// Position the label at the top of the screen but with the same horizontal alignment as the
		// player's attributes' labels
		worldLayout.putConstraint(NORTH, enemyHealthLabel, 10, NORTH, worldIn);
		worldLayout.putConstraint(WEST, enemyHealthLabel, 0, WEST, playerManaLabel);
		
		// Position the enemy's health bar beside the label and bounded by the right side of the battle options
		worldLayout.putConstraint(VERTICAL_CENTER, enemyHealthBar, 0, VERTICAL_CENTER, enemyHealthLabel);
		worldLayout.putConstraint(WEST, enemyHealthBar, 10, EAST, enemyHealthLabel);
		worldLayout.putConstraint(EAST, enemyHealthBar, 0, EAST, playerBattleOptions);
		
		// Add the player's health bar and label to the world so they will be drawn
		worldIn.add(enemyHealthLabel);
		worldIn.add(enemyHealthBar);
		
		// Update the player's and enemy's health and mana bars
		updateHealthAndManaBars();
		
		// Update the world's ui to reflect our changes
		SwingUtilities.updateComponentTreeUI(worldIn);
	}
	
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
			
			// Determine whether or not the player can do this action based on the number of ability points required. the mana cost,
			// and whose turn it is
			boolean buttonEnabled = player.getPrimaryAttributeValue(Attribute.ABILITIES) >= action.getRequiredAbilityPoints()
								 && player.getCurrentMana() >= action.getManaCost()
								 && playerTurn;
			
			// If the action is a poison action and the enemy is already poisoned then
			// stop the player from stacking poison effects
			if (action.getType() == Action.Type.POISON) {
				buttonEnabled = buttonEnabled && !enemy.hasPoisonEffect();
			}

			// If the action is a sustained healing action or a protection action and the
			// player already has a sustained healing effect or protection effect then stop
			// the player from stacking defence effects
			if ((action.getType() == Action.Type.HEALING && action.getNumTurns() > 1) || action.getType() == Action.Type.PROTECTION) {
				buttonEnabled = buttonEnabled && !player.hasHealingEffect() && !player.hasProtectionEffect();
			}
			
			// Update the button's state 
			actionButtonPair.getValue().setEnabled(buttonEnabled);
		}
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

	/**
	 * This method performs the given action as the player.
	 * 
	 * @param action the action to perform
	 */
	private void performPlayerAction(Action action) {
		// Remove the action's mana cost from the player's mana
		player.removeMana(action.getManaCost());

		// Perform the aciton based on its type
		switch (action.getType()) {
			// Hit actions just deal damage as does the special attack
			case HIT:
			case SPECIAL: {
				enemy.inflictDamage(action.getDamage());
			} break;

			// Poison actions deal damage over successive turns
			case POISON: {
				enemy.inflictPoison(action.getDamage(), action.getNumTurns());
			} break;

			// Healing actions give the player health
			case HEALING: {
				player.applyHealingEffect((int)action.getDefenceEffect(), action.getNumTurns());
			} break;

			// Protection actions reduce ncoming damage by a multiplier
			case PROTECTION: {
				player.applyProtectionEffect(action.getDefenceEffect(), action.getNumTurns());
			} break;
		}

		// Switch to the enemy's turn
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
		// TODO: Change
		if (!playerTurn) {
			player.inflictDamage(10);
			changeTurns();
		}
	}
	
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
	}
}
