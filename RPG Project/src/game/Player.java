package game;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * This class represents the player. It is responsible for keeping
 * track of the player's attributes and position, updating the player's
 * position, and drawing the player.
 */
public class Player implements KeyListener {
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
	 * The x-position of the player in the world.
	 */
	private int xPos = 0;
	
	/**
	 * The y-position of the player in the world.
	 */
	private int yPos = 0;
	
	/**
	 * The world the player is in.
	 */
	private World world = null;
	
	/**
	 * This method paints the player at their current position using
	 * the given instance of Graphics2D.
	 * 
	 * @param g2d the instance of Graphics2D to use to paint the player
	 */
	public void paint(Graphics2D g2d) {
		// For now, draw the player as a blue circle
		g2d.setColor(Color.BLUE);
		g2d.fillOval(xPos*Tile.TILE_SIZE, yPos*Tile.TILE_SIZE, Tile.TILE_SIZE, Tile.TILE_SIZE);
	}
	
	/**
	 * This method sets the player's world and registers us
	 * as a listener for key events.
	 * 
	 * @param worldIn the player's new world
	 */
	public void setWorld(World worldIn) {
		// If we currently have another world then we should stop
		// listening for key events from it before we switch worlds
		if (world != null) world.unregisterKeyListener(this);
		
		// Set the player's new world
		world = worldIn;
		
		// Register us as a listener for key events from the world
		world.registerKeyListener(this);
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
	@Override
	public void keyPressed(KeyEvent e) {
		// If the player is not in a world then their position cannot change
		if (world == null) return;
		
		// Update the player's position according the the key pressed
		switch (e.getKeyChar()) {
			// Vertical Movement (0 = top)
			case 'w':
				yPos = Math.max(yPos-1, MIN_Y_POS);
				break;
			case 's':
				yPos = Math.min(yPos+1, MAX_Y_POS);
				break;

			// Horizontal Movement (0 = Right)
			case 'a':
				xPos = Math.max(xPos-1, MIN_X_POS);
				break;
			case 'd':
				xPos = Math.min(xPos+1, MAX_X_POS);
				break;
				
			// If the key was not one that updates the player's position
			// then we do not want to continue executing past this point
			default: return;
		}
		
		// Tell the world it needs to perform a repaint
		world.markRepaintRequired();
	}

	@Override
	public void keyTyped(KeyEvent e) {}

	@Override
	public void keyReleased(KeyEvent e) {}
}
