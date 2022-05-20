package game;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.Stack;

import javax.swing.JComponent;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;

import game.entity.IEnemy;
import game.entity.Player;
import game.ui.Tile;
import game.ui.overlays.MessageOverlay;
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
	public final Stack<IScreen> screenStack = new Stack<>();
	
	/**
	 * This record represents a message that is displayed on the screen to
	 * the user for a specified amount of time in seconds.
	 * 
	 * @param message the message to display
	 * @param time    the amount of time, in seconds, to display the message for
	 */
	public static final record Message(String message, int time) {}
	
	/**
	 * This is the message queue. It contains all of the messages waiting to be displayed
	 * to the player. Once this queue is empty no more messages are waiting to be displayed
	 * to the player and the game will resume.
	 */
	private final Queue<Message> messageQueue = new ArrayDeque<>();
	
	/**
	 * This is the message that is currently being displayed to the user.
	 */
	private Message currentMessage = null;
	
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
	 * This variable keeps track of how many enemies remain in each zone
	 * [enemies remaining in fire, gem, ice, rock]
	 */
	public int[] enemiesRemaining = {3,3,3,3};
	
	/**
	 * This constructs the world by pushing the starting screen onto the screen stack
	 * and initializing the game's state.
	 * 
	 * @param playerIn the player
	 */
	public World(Player playerIn) {
		// Set the current level to 1 since the player starts in the green zone
		Main.currentLevel = 1;

		// Set our layout to be a SpringLayout so that if anything needs to add JComponents to
		// us they will easily be able to lay them out
		springLayout = new SpringLayout();
		setLayout(springLayout);
		
		// Store a reference to the player and update the player's world
		player = playerIn;
		player.setWorld(this);
		
		// Push the starting screen onto the screen stack
		screenStack.push(AreaScreen.createNewAreaScreen("res/greenzonebackground.png"));
		
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
	/*                             SCREEN MANAGEMENT METHODS                             */
	/*************************************************************************************/

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
		
		// Update the player's position, ensuring that the player does not activate the effect
		// of the tile they are moving to
		player.updatePosition(newX, newY, false);
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
	/*                             OVERLAY MANAGEMENT METHODS                            */
	/*************************************************************************************/
	
	/**
	 * This method shows the given overlay and pauses the game.
	 * 
	 * @param overlay the overlay to show
	 */
	public void showOverlayAndPause(Overlay overlayIn) {
		// Puase the game
		paused = true;
		
		// Make the overlay the main window's glass pane so that it is
		// drawn over everything else and can intercept events
		overlay = overlayIn;
		Main.window.setGlassPane(overlay);
		
		// Create the overlay's java swing components and add them
		// to the screen
		overlay.createAndAddSwingComponents();

		// Make the overlay visible
		overlay.setVisible(true);
		
		// Update the ui so that everything will be drawn correctly
		updateUIState();
	}
	
	/**
	 * This method hides the current overlay assuming that it
	 * is not null and resumes the game.
	 */
	public void hideOverlayAndResume() {
		// Hide the overlay
		overlay.setVisible(false);
		
		// Resume the game
		paused = false;
		
		// Update the ui so that everything will be drawn correctly
		updateUIState();
	}

	/*************************************************************************************/
	/*                                   UPDATE METHOD                                   */
	/*************************************************************************************/
	
	/**
	 * This updates the world which involves displaying messages to the user
	 * if the message queue is not empty and updating the current screen if
	 * it needs updating.
	 */
	public void update() {
		// If an overlay is being displayed then do not update the world. However,
		// do update the overlay.
		if (isOverlayDisplayed()) {
			overlay.update();
			return;
		}
		
		// If we are not currently displaying a message and the message stack is
		// not empty then start displaying the messages in the message stack
		if (currentMessage == null && messageQueue.peek() != null) {
			// Get the next message from the message stack
			currentMessage = messageQueue.poll();
			
			// Show a message overlay to display the message
			showOverlayAndPause(new MessageOverlay(this, player, currentMessage));
		}
		
		// If a screen is being displayed and it is a BattleScreen then we need to
		// update its state
		if (!screenStack.empty()) {
			if (screenStack.peek() instanceof BattleScreen) {
				((BattleScreen) screenStack.peek()).update();
			}
		}
	}

	/*************************************************************************************/
	/*                                  MESSAGE METHODS                                  */
	/*************************************************************************************/
	
	/**
	 * This method adds the given message to the message queue. Once the message reaches
	 * the end of the queue it will be displayed on the screen to the player for the amount
	 * of time, in seconds, specified by the message.
	 * 
	 * Note: the message will be formatted as HTML so HTML tags such as <b> and <i> will work
	 * 
	 * @param message the message to display to the player
	 * @param time    the amount of time, in seconds, to display the message for
	 */
	public void showMessage(String messageIn, int timeIn) {
		// Wrap the message with an html tag so that the text will be formatted as HTML
		// which will allow for the text to be formatted and so that the text will wrap
		// to a new line if it cannot fit in a single line
		messageIn = "<html>" + messageIn + "</html>";
		
		// Create the Message object and add it to the message queue
		messageQueue.add(new Message(messageIn, timeIn));
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
			
			// Remove the battle tile which triggered this fight
			AreaScreen currentScreen = (AreaScreen) screenStack.peek();
			currentScreen.tileMap[player.yPos][player.xPos] = null;
			
			enemiesRemaining[Main.currentLevel-2]-=1;
			if (enemiesRemaining[Main.currentLevel-2] <= 0)
			{
				switch (Main.currentLevel) {
				case 2:
					currentScreen.tileMap[2][4] = null;
					Walls.arrays[1][4][1] = 0;
					break;
				case 3:
					currentScreen.tileMap[4][6] = null;
					Walls.arrays[2][7][4] = 0;
					break;
				case 4:
					currentScreen.tileMap[6][4] = null;
					Walls.arrays[3][4][7] = 0;
					break;
				case 5:
					currentScreen.tileMap[4][2] = null;
					Walls.arrays[4][1][4] = 0;
					break;
				}
			}
			
		}
	}

	/*************************************************************************************/
	/*                                     LISTENERS                                     */
	/*************************************************************************************/
	
	/**
	 * This method is registered as a key pressed listener. When it receives
	 * a key typed event that corresponds to the escape key being typed
	 * this method will pause the game.
	 * 
	 * @param keyEvent the key pressed event
	 * 
	 * @see KeyListener#keyPressed(KeyEvent)
	 */
	private void pauseKeyListener(KeyEvent keyEvent) {
		// If an overlay is currently being displayed other that the pause
		// screen overlay then we should not allow the player to view the
		// pause screen overlay.
		if (isOverlayDisplayed() && !(overlay instanceof PauseScreenOverlay)) return;
		
		// If the pressed key is the escape key then either show the pause screen
		// overlay if the game is not paused or hide the pause screen overlay if
		// the game is paused
		if (keyEvent.getKeyCode() == KeyEvent.VK_ESCAPE) {
			if (!paused) {
				// The game is not paused so pause the game and show the pause screen
				// overlay
				showOverlayAndPause(new PauseScreenOverlay(this, player));
			} else {
				// The game is paused so resume the game and stop showing the pause
				// screen overlay
				hideOverlayAndResume();
			}
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
	
	/**
	 * This is called by MessageOverlay when it has finished displaying its message
	 * for the amount of time specified by the message. This method will then update
	 * 'currentMessage' to be null, stop showing the message overlay, and resume the game.
	 */
	public void onMessageFinishDisplay() {
		// Stop showing the message overlay and resume the game
		hideOverlayAndResume();
		
		// Update 'currentMessage' to be null
		currentMessage = null;
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
		return overlay != null && overlay.isVisible();
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
