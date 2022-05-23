package game;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayDeque;
import java.util.EnumMap;
import java.util.Map;
import java.util.Queue;
import java.util.Stack;

import javax.swing.JComponent;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;

import game.entity.Player;
import game.entity.enemy.BossEnemy;
import game.entity.enemy.Enemy;
import game.entity.enemy.FinalBossEnemy;
import game.entity.enemy.IEnemy;
import game.entity.enemy.MainEnemy;
import game.entity.enemy.RandomEncounterEnemy;
import game.ref.Images;
import game.ui.Tile;
import game.ui.overlays.MessageOverlay;
import game.ui.overlays.Overlay;
import game.ui.overlays.PauseScreenOverlay;
import game.ui.screens.AreaScreen;
import game.ui.screens.BattleScreen;
import game.ui.screens.IScreen;
import game.ui.screens.PlayerDeathScreen;
import game.ui.screens.WinScreen;
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
	 * This is the zone that the player is currently in.
	 */
	private Zone currentZone = Zone.GREEN_HUB;
	
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
	public Map<Zone, Integer> enemiesRemaining = new EnumMap<>(Zone.class);
	
	/**
	 * This variable keeps track of how many steps the player has taken since they were
	 * last in a random encounter.
	 */
	private int randomEncounterStepCounter = 0;
	
	/**
	 * This constructs the world by pushing the starting screen onto the screen stack
	 * and initializing the game's state.
	 * 
	 * @param playerIn the player
	 */
	public World(Player playerIn) {
		// Initialize the map of walls for every zone
		Walls.initializeWalls();
		
		// Initialize the static images used by the game
		Images.initializeImages();

		// There are three enemies each in the fire, gem, ice, and rock zones
		enemiesRemaining.put(Zone.FIRE, 3);
		enemiesRemaining.put(Zone.GEM, 3);
		enemiesRemaining.put(Zone.ICE, 3);
		enemiesRemaining.put(Zone.ROCK, 3);

		// Set our layout to be a SpringLayout so that if anything needs to add JComponents to
		// us they will easily be able to lay them out
		springLayout = new SpringLayout();
		setLayout(springLayout);
		
		// Store a reference to the player and update the player's world
		player = playerIn;
		player.setWorld(this);
		
		// Show the starting screen (the green zone / hub zone)
		showScreen(AreaScreen.createNewAreaScreen(this, Zone.GREEN_HUB));
		
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
	public synchronized void showScreen(IScreen newScreen) {
		// Remove the current screen's java swing components if there is a current screen
		if (!screenStack.empty()) {
			screenStack.peek().removeSwingComponents(this);
		}

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
	public synchronized void changeArea(AreaScreen newArea, int newX, int newY) {
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

		// Update which zone the player is currently in
		currentZone = newArea.getZone();

		// Let the area screen know that the player's zone has changed
		newArea.onPlayerZoneChanged();

		// Update the ui's state so that everything is drawn correctly
		updateUIState();
	}
	
	/**
	 * This method removes the tile at the given location by setting the tile
	 * at the given location to an empty tile assuming that the current screen
	 * is an area screen.
	 * 
	 * @param col the column of the tile to remove
	 * @param row the row of the tile to remove
	 */
	public synchronized void removeTileAtLocation(int row, int col) {
		// Remove the tile if the current screen is an area screen
		if (!screenStack.empty() && screenStack.peek() instanceof AreaScreen) {
			((AreaScreen) screenStack.peek()).setTileAtPos(row, col, Tile.EMPTY_TILE);
		}
	}

	/**
	 * This method closes the current screen and returns to the
	 * previous screen in the screen stack.
	 */
	public synchronized void closeCurrentScreen() {
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
	public synchronized void update() {
		// If an overlay is being displayed then do not update the world. However,
		// do update the overlay.
		if (isOverlayDisplayed()) {
			overlay.update();
			return;
		}
		
		// If the message stack is not empty then start displaying the messages
		// in the message stack
		if (messageQueue.peek() != null) {
			// Show a message overlay to display the next message from the message queue
			showOverlayAndPause(new MessageOverlay(this, player, messageQueue.poll()));
		}
		
		// If a battle is happening then we need to update the battle screen
		if (inBattle) {
			// Update the battle screen if one is being displayed
			if (!screenStack.empty() && screenStack.peek() instanceof BattleScreen) {
				((BattleScreen) screenStack.peek()).update();
			}
		}
		
		// If the player has all the keys then we should begin the final boss fight
		if (player.hasAllKeys()) {
			initiateFinalBossBattle();
		}
	}

	/*************************************************************************************/
	/*                                   STORY METHODS                                   */
	/*************************************************************************************/
	
	/**
	 * This method initiates the battle between the player and the final boss. This
	 * involves showing some story dialog and then initiating the actual battle.
	 */
	private void initiateFinalBossBattle() {
		// Show some story dialog
		showMessage("As you pick up the final key you hear a distant rumble and a voice thunders "    +
		            "down from the sky: <i>I AM <b>MARDUK</b>! You think you can defeat me you puny " +
					"mage?</i>",
					15);
		showMessage("<i>Face me then and meet your doom...</i>", 5);
		showMessage("A shadowy figure appears in the sky and slowly descends towards you. As it "  +
					"approaches you begin to sense the dark magicaly power that seems to emanate " +
					"from it.",
					15);
		showMessage("When <b>Marduk</b> reaches you he attacks and you begin battling for your life...", 10);
		
		// Initiate the battle
		initiateBattle(new FinalBossEnemy(this, player.getExperience()));
	}

	/**
	 * This method concludes the game after the player has defeated the final boss. This
	 * involves showing some story dialog and then showing the win screen.
	 */
	private void concludeGame() {
		// Show some story dialog
		showMessage("As you deal the killing blow to <b>Marduk</b> his voice fills your head. " +
	                "<i>Noooooo this cannot be happening............</i>.",
	                5);
		showMessage("At once everything around you seems to become lighter as the last remnant "   +
	                "of dark magic leaves the world. People being to celebrate and cheer as they " +
				    "realize <b>Mardok</b> is no more and they can live their lives in peace.",
				    10);
		
		// Show the win screen
		showScreen(new WinScreen());
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
	public synchronized void showMessage(String messageIn, int timeIn) {
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
		// Show a different message based on whether or not this is a random
		// encounter. However, do not show any message if the enemy is the
		// final boss since that battle is triggered by story dialog
		if (enemy instanceof RandomEncounterEnemy) {
			showMessage("A mage leaps out and challenges you to a battle!", 5);
		} else if (!(enemy instanceof FinalBossEnemy)) {
			showMessage("You challenge the enemy to a battle!", 5);
		}
		
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
	public synchronized void exitBattle(boolean playerDead, int experienceGain, IEnemy enemy) {
		// Update our state
		inBattle = false;

		// Close the battle screen
		closeCurrentScreen();

		// If the player died then show the player death screen. Otherwise
		// give the player their experience and resume the game.
		if (playerDead) {
			// Show a message
			showMessage("You died!", 5);
			
			// Show the player death screen
			showScreen(new PlayerDeathScreen(this, enemy));
		} else {
			// If the player just defeated the final boss then conclude the story and
			// finish the game. Otherwise conclude the battle normally.
			if (enemy instanceof FinalBossEnemy) {
				concludeGame();
				return;
			}
			
			// Show a message
			showMessage(
				String.format(
					"You defeated the enemy and gained %d experience!",
					experienceGain
				),
				4
			);

			// Give the player their experience
			player.addExperience(experienceGain);
			
			// Remove the battle tile which triggered this fight
			removeTileAtLocation(player.getY(), player.getX());
			
			// If the enemy was a boss then show a message
			if (enemy instanceof BossEnemy) {
				showMessage("As <b>Marduk's</b> lieutenant collapses and turns to ashes you hear a "   +
			                "distant rumble of thunder as though <b>Marduk</b> himself is expressing " +
						    "displeasure at your victory.",
						    10);
			}
			
			// If the enemy was a main enemy then decrement the number of main enemies
			// remaining in this zone
			if (enemy instanceof MainEnemy) {
				// Decrement the number of enemies remaining in this zone
				enemiesRemaining.put(currentZone, enemiesRemaining.get(currentZone) - 1);
	
				// If there are no more enemies in this zone then open up the boss and show a message
				if (enemiesRemaining.get(currentZone) <= 0) {
					// Show a message
					showMessage("As you defeat the final of <b>Marduk's</b> followers in the area " +
					            "you hear a sound like shattering glass and a barrier breaks, "    +
								"revealing one of <b>Marduk's</b> lieutenants.",
								10);
					
					// Open up the boss
					switch (currentZone) {
						case FIRE:
							removeTileAtLocation(2, 4);
							Walls.setWallAtPosition(currentZone, 1, 4, false);
							break;
						case GEM:
							removeTileAtLocation(4, 6);
							Walls.setWallAtPosition(currentZone, 4, 7, false);
							break;
						case ICE:
							removeTileAtLocation(6, 4);
							Walls.setWallAtPosition(currentZone, 7, 4, false);
							break;
						case ROCK:
							removeTileAtLocation(4, 2);
							Walls.setWallAtPosition(currentZone, 4, 1, false);
							break;
						default:
							break;
					}
				}
			}

			// Update the player panel's UI
			if (screenStack.peek() instanceof AreaScreen) {
				((AreaScreen)screenStack.peek()).onPlayerUpdate();
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
			Tile newTile = currentArea.getTileAtPos(newY, newX);
			
			// If the tile is empty check for a random encounter, otherwise perform the tile's action
			if (newTile == Tile.EMPTY_TILE && randomEncounterStepCounter >=4) {
				// There is an 8% chance of a random encounter
				if ((Main.RANDOM.nextInt(100) + 1) <= 8) {
					// Reset the random encounter step counter
					randomEncounterStepCounter = 0;
					
					// Create a new enemy
					Enemy enemy = new RandomEncounterEnemy(this, player.getExperience());

					// Set the enemy's image based on the current zone
					enemy.setImage(Images.randomEnemyImage);

					// Start the battle
					initiateBattle(enemy);
				}
			} else {
				// Increment the random encounter step counter
				randomEncounterStepCounter++;
				
				// Perform the interaction with the tile at the player's new position
				newTile.performAction(player, this);
			}
		}
	}
	
	/**
	 * This is called by MessageOverlay when it has finished displaying its message
	 * for the amount of time specified by the message. This method will then stop
	 * showing the message overlay, and resume the game.
	 */
	public void onMessageFinishDisplay() {
		// Stop showing the message overlay and resume the game
		hideOverlayAndResume();
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
	 * This method returns the player.
	 * 
	 * @return the player
	 */
	public Player getPlayer() {
		return player;
	}

	/**
	 * This method returns the zone that the player is currently in.
	 * 
	 * @return the zone that the player is currently in
	 */
	public Zone getCurrentZone() {
		return currentZone;
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
	 * This method returns whether or not there is another message in
	 * the message queue.
	 * 
	 * @return whether or not there is another message in the message queue
	 */
	public synchronized boolean hasNextMessage() {
		return messageQueue.peek() != null;
	}

	/**
	 * This method returns the next message in the message queue and
	 * also removes it from the message queue.
	 * 
	 * @return the next message in the message queue
	 */
	public synchronized Message getNextMessage() {
		return messageQueue.poll();
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
