package game;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Stack;

import javax.swing.JComponent;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;

import game.entity.IEnemy;
import game.entity.Player;
import game.ui.Tile;
import game.ui.overlays.Overlay;
import game.ui.overlays.PauseScreenOverlay;
import game.ui.screens.AreaScreen;
import game.ui.screens.BattleScreen;
import game.ui.screens.IScreen;
import game.ui.screens.PlayerDeathScreen;
import game.util.KeyPressedListener;

/**
 * This class represents the world the player is playing in. This class
 * is responsible for a large portion of the logic that runs the game. This
 * class manages the player, updates the game state, and ensures everything
 * is drawn.
 */
@SuppressWarnings("serial")
public class World extends JComponent {
	/** 
	 * This is the screen stack. The top of the screen stack is the screen
	 * that is currently visible to the user. Pushing a new screen on represents
	 * forward navigation and popping of the top screen represents backwards navigation.
	 */
	private final Stack<IScreen> screenStack = new Stack<>();
	
	/**
	 * This stores the current overlay that is being displayed. Null indicates there
	 * is no overlay. When there is an overlay it is drawn over everything else in the
	 * world and stops the player from moving.
	 */
	private Overlay overlay = null;
	
	/**
	 * This is the SpringLayout used by the world.
	 */
	private final SpringLayout springLayout;
	
	/**
	 * This is the player.
	 */
	private final Player player;
	
	/**
	 * This keeps track of whether or not the player is currently
	 * engaged in a battle.
	 */
	private boolean inBattle = false;
	
	/**
	 * This keeps track of whether or not the world needs to be repainted,
	 * or if repainting can be skipped next time the repaint method is called.
	 */
	private boolean repaintRequired = false;
	
	/**
	 * This variable keeps track of whether the game is paused.
	 */
	private boolean paused = false;
	
	/**
	 * This constructs the world by pushing the starting screen onto the screen stack
	 * and initializing the game's state.
	 * 
	 * @param playerIn the player
	 */
	public World(Player playerIn) {
		// Set our layout to be a SpringLayout so that if anything needs to add JComponents to
		// us they will easily be able to lay them out
		springLayout = new SpringLayout();
		setLayout(springLayout);
		
		// Store a reference to the player and update the player's world
		player = playerIn;
		player.setWorld(this);
		
		// Push the starting screen onto the screen stack
		screenStack.push(AreaScreen.createNewAreaScreen("res/test.jpg"));
		
		// Register the 'pauseKeyListener' method as a key pressed listener
		registerKeyListener((KeyPressedListener)this::pauseKeyListener);
	}

	/*************************************************************************************/
	/*                                KEY LISTENER METHODS                               */
	/*************************************************************************************/
	
	/**
	 * This method registers the given KeyListener to receive
	 * key events.
	 * 
	 * @param keyListener the KeyListener to register
	 * 
	 * @see JComponent#addKeyListener(KeyListener)
	 */
	public void registerKeyListener(KeyListener keyListener) {
		addKeyListener(keyListener);
	}
	
	/**
	 * This method unregisters the given KeyListener so it will
	 * no longer receive key events.
	 * 
	 * @param keyListener the KeyListener to unregister
	 * 
	 * @see JComponent#removeKeyListener(KeyListener)
	 */
	public void unregisterKeyListener(KeyListener keyListener) {
		removeKeyListener(keyListener);
	}

	/*************************************************************************************/
	/*                                 PAINTING METHODS                                  */
	/*************************************************************************************/
	
	/**
	 * This method tells the world that it needs to perform a repaint.
	 */
	public void markRepaintRequired() {
		repaintRequired = true;
	}
	
	/**
	 * This method updates the state of the screen's ui so
	 * that everything is drawn correctly.
	 */
	public void updateUIState() {
		SwingUtilities.updateComponentTreeUI(Main.window);
		markRepaintRequired();
		SwingUtilities.invokeLater(Main.window::repaint);
	}
	
	/**
	 * This method repaints the world.
	 * 
	 * @see JComponent#repaint()
	 */
	@Override
	public void repaint() {
		// If we do not need to repaint the window and are not paused then exit
		if (!repaintRequired && !paused) return;
		
		super.repaint();
	}
	
	/**
	 * This draws the current screen onto the game's window (if the screen
	 * stack is not empty), and then draws the player.
	 * 
	 * @param g the Graphics object to use to draw the world
	 */
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		// Ensure we were passed an instance of Graphics2D
		if (g instanceof Graphics2D) {
			// Convert the Graphics instance to a Graphics2D instance
			Graphics2D g2d = (Graphics2D)g;
			
			// If the screen stack is not empty then draw the screen on the top
			// of the stack using the provided instance of Graphics2D
			if (!screenStack.empty()) {
				screenStack.peek().paint(g2d);
			}
			
			// If an area screen is currently being displayed then paint the player
			if (isAreaDisplayed()) {
				player.paint(g2d);
			}
			
			// Update 'repaintRequired'
			repaintRequired = false;
		}
	}

	/*************************************************************************************/
	/*                              STATE UPDATING METHODS                               */
	/*************************************************************************************/
	
	/**
	 * This updates the world.
	 */
	public void update() {
		// If the game is paused then do not update the world
		if (paused) return;
		
		// If the current screen is a BattleScreen then we need to
		// update its state
		if (!screenStack.empty() && screenStack.peek() instanceof BattleScreen) {
			((BattleScreen) screenStack.peek()).update();
		}
	}

	/**
	 * This method shows the given screen. It will also ensure that
	 * the current screen's java swing components are removed and
	 * the new screen's java swing components are added.
	 */
	public void showScreen(IScreen newScreen) {
		// Remove the current screen's java swing components
		screenStack.peek().removeSwingComponents(this);

		// Add the new screen to the screen stack and add its java
		// swing components to the screen
		screenStack.push(newScreen);
		newScreen.addSwingComponents(this, springLayout);

		// Update the ui's state so that everything is drawn correctly
		updateUIState();
	}

	/**
	 * This method changes the current area and updates the player's
	 * position to where they should start in the new area.
	 * 
	 * @param newArea the area to change to
	 * @param newX    the player's new x-position
	 * @param newY    the player's new y-position
	 */
	public void changeArea(AreaScreen newArea, int newX, int newY) {
		// Ensure the player's new x-position will be within the screen's bounds
		if (newX < 0 || newX > AreaScreen.TILES_PER_ROW) {
			throw new IllegalArgumentException(
					"The player's x-position must be within the screen's bounds after an area change!");
		}

		// Ensure the player's new y-position will be within the screen's bounds
		if (newY < 0 || newY > AreaScreen.ROWS_OF_TILES) {
			throw new IllegalArgumentException(
					"The player's y-position must be within the screen's bounds after an area change!");
		}
		
		// Pop the previous area off the screen stack and push the new area onto the screen stack
		screenStack.pop();
		screenStack.push(newArea);
		
		// Update the player's position
		player.updatePosition(newX, newY);
	}

	/**
	 * This method closes the current screen and returns to the
	 * previous screen in the screen stack.
	 */
	public void closeCurrentScreen() {
		// Remove the current screen's java swing components
		screenStack.peek().removeSwingComponents(this);

		// Pop the current screen off of the screen stack
		screenStack.pop();

		// Readd the new screen's java swing components
		screenStack.peek().addSwingComponents(this, springLayout);

		// Update the ui's state so that everything is drawn correctly
		updateUIState();
	}

	/*************************************************************************************/
	/*                                  BATTLE METHODS                                   */
	/*************************************************************************************/
	
	/**
	 * This method initiates a battle between the player and the
	 * given enemy.
	 * 
	 * @param enemy the enemy the player is fighting
	 */
	public void initiateBattle(IEnemy enemy) {
		// Tell the player they are now in a battle
		player.onBattleStart();

		// Create a battle screen
		BattleScreen battleScreen = new BattleScreen(this, player, enemy);

		// Show the battle screen
		showScreen(battleScreen);
		
		// Update the world's state
		inBattle = true;
	}

	/**
	 * This method is called when a battle is over and we need to
	 * exit out to the regular world.
	 * 
	 * @param playerDead     whether or not the player died
	 * @param experienceGain how much experience the player should gain
	 * @param enemy          the enemy that the player was fighting
	 */
	public void exitBattle(boolean playerDead, int experienceGain, IEnemy enemy) {
		// Update our state
		inBattle = false;

		// Close the battle screen
		closeCurrentScreen();

		// If the player died then show the player death screen. Otherwise
		// give the player their experience and resume the game.
		if (playerDead) {
			showScreen(new PlayerDeathScreen(this, enemy));
		} else {
			player.addExperience(experienceGain);
		}
	}

	/*************************************************************************************/
	/*                                     LISTENERS                                     */
	/*************************************************************************************/
	
	/**
	 * This method is registered as a key pressed listener. When it receives
	 * a key pressed event that corresponds to the escape key being pressed
	 * this method will pause the game.
	 * 
	 * @param keyEvent the key pressed event
	 */
	private void pauseKeyListener(KeyEvent keyEvent) {
		// If the pressed key is the escape key then switch the pause state of the game
		// and update whether the pause HUD is shown according to the new pause state
		if (keyEvent.getKeyCode() == KeyEvent.VK_ESCAPE) {
			paused = !paused;
			
			// If the game is now paused then set the current overlay to a pause screen
			// overlay, otherwise stop showing the pause screen overlay.
			if (paused) {
				// Create the pause overlay and then make it the main
				// window's glass pane so that it is drawn over everything
				// else and can intercept events
				overlay = new PauseScreenOverlay(this, player);
				Main.window.setGlassPane(overlay);
				
				// Create the overlay's java swing components and add them
				// to the screen
				overlay.createAndAddSwingComponents();

				// Make the overlay visible
				overlay.setVisible(true);
			} else {
				// Make the pause screen overlay invisible so that it no
				// longer gets drawn or intercepts events and then set
				// 'overlay' to null
				overlay.setVisible(false);
				overlay = null;
			}
			
			// Update the ui so that everything will be drawn correctly
			updateUIState();
		}
	}
	
	/**
	 * This method is called by the player when their position updates
	 * so that we can perform any necessary interaction with the tile
	 * the player is moving onto.
	 * 
	 * @param newX the player's new x-position
	 * @param newY the player's new y-position
	 */
	public void onPlayerPositionChange(int newX, int newY) {
		// We only need to perform an interaction if the current screen is an AreaScreen
		if (screenStack.peek() instanceof AreaScreen) {
			// Get the current AreaScreen
			AreaScreen currentArea = (AreaScreen)screenStack.peek();
			
			// Get the tile at the player's new position
			Tile newTile = currentArea.getTileAtPos(newX, newY);
			
			// Perform the interaction with the tile at the player's new position
			newTile.performAction(player, this);
		}
	}

	/*************************************************************************************/
	/*                                      GETTERS                                      */
	/*************************************************************************************/
	
	/**
	 * This method returns the SpringLayout used by the world.
	 * 
	 * @return the SpringLayout used by the world
	 */
	public SpringLayout getSpringLayout() {
		return springLayout;
	}
	
	/**
	 * This method returns whether or not an overlay is currently
	 * being displayed.
	 * 
	 * @return whether or not an overlay is currently being displayed
	 */
	public boolean isOverlayDisplayed() {
		return overlay != null;
	}

	/**
	 * This method returns whether or not a battle is currently
	 * underway.
	 * 
	 * @return whether or not a battle is currently underway
	 */
	public boolean inBattle() {
		return inBattle;
	}

	/**
	 * This method returns whether or not an AreaScreen is currently
	 * being displayed.
	 * 
	 * @return whether or not an AreaScreen is currently being displayed
	 */
	public boolean isAreaDisplayed() {
		return screenStack.peek() instanceof AreaScreen;
	}
	
	/**
	 * This method returns whether or not the world is
	 * focusable (true).
	 * 
	 * @return whether or not the world is focusable (true)
	 */
	@Override
	public boolean isFocusable() {
		return true;
	}
	
	/**
	 * This returns the size we want the game's window and therefore the
	 * world to be.
	 * 
	 * @return the dimensions the game's window should have
	 */
	@Override
	public Dimension getPreferredSize() {
		return Main.SCREEN_SIZE; 
	}
}
