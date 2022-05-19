package game;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyListener;
import java.util.Stack;

import javax.swing.JComponent;
import javax.swing.SpringLayout;

import game.entities.ILivingEntity;
import game.entities.Player;
import game.ui.AreaScreen;
import game.ui.BattleScreen;
import game.ui.IScreen;

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
	 * This constructs the world by pushing the starting screen onto the screen stack
	 * and initializing the game's state.
	 * 
	 * @param playerIn the player
	 */
	public World(Player playerIn) {
		super();
		
		// Set our layout to be a SpringLayout so that if anything needs to add JComponents to
		// us they will easily be able to lay them out
		springLayout = new SpringLayout();
		setLayout(springLayout);
		
		// Store a reference to the player and update the player's world
		player = playerIn;
		player.setWorld(this);
		
		// Push the starting screen onto the screen stack
		Main.currentLevel = 1;
		screenStack.push(AreaScreen.createNewAreaScreen("res/greenzonebackground.png"));
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
			
			// If we are not in a battle then paint the player
			if (!inBattle) {
				player.paint(g2d);
			}
			
			// Update 'repaintRequired'
			repaintRequired = false;
		}
	}
	
	@Override
	public void repaint() {
		// If we do not need to repaint the window and are not in a battle then exit
		if (!repaintRequired && !inBattle) return;
		
		super.repaint();
	}
	
	/**
	 * This method tells the world that it needs to perform a repaint.
	 */
	public void markRepaintRequired() {
		repaintRequired = true;
	}
	
	/**
	 * This updates the world.
	 */
	public void update() {
		// If the current screen is a BattleScreen then we need to
		// update its state
		if (!screenStack.empty() && screenStack.peek() instanceof BattleScreen) {
			((BattleScreen) screenStack.peek()).update();
		}
	}
	
	/**
	 * This method returns the SpringLayout used by the world.
	 * 
	 * @return the SpringLayout used by the world
	 */
	public SpringLayout getSpringLayout() {
		return springLayout;
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
		player.updatePosition(newX, newY, false);
	}
	
	/**
	 * This method initiates a battle between the player and the
	 * given enemy.
	 * 
	 * @param enemy the enemy the player is fighting
	 */
	public void initiateBattle(ILivingEntity enemy) {
		// Push a battle screen onto the screen stack
		screenStack.push(new BattleScreen(this, player, enemy));
		
		// Tell the player they are now in a battle
		player.onBattleStart();
		
		// Update the world's state
		inBattle = true;
	}
	
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
		return new Dimension(Main.SCREEN_WIDTH, Main.SCREEN_HEIGHT); 
	}
}
