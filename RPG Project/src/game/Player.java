package game;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * This class represents the player. It is responsible for keeping
 * track of the player's attributes and position, updating the player's
 * position, and drawing the player.
 */
public class Player implements KeyListener {
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
			PREMADE_PLAYERS[0] = new Player("Ice Mage", ImageIO.read(new File("res/test.jpg")));
			PREMADE_PLAYERS[1] = new Player("Fire Mage", ImageIO.read(new File("res/test2.jpg")));
			PREMADE_PLAYERS[2] = new Player("Nature Mage", ImageIO.read(new File("res/test.jpg")));
		} catch (IOException e) {
			// If there was an error loading one of the images then we need to quit the game
			throw new RuntimeException("Unable to load image for character!", e);
		}
	}
	
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
	 * drawn so that it fits within one tile.
	 */
	private final AffineTransformOp imageScaleOp;
	
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
	 * Since we are using premade classes there is no need for an instance of
	 * Player to ever be created outside of this class.
	 */
	private Player(String classTypeIn, BufferedImage imageIn) {
		classType = classTypeIn;
		image = imageIn;

		// Determine the x and y scales we will need to use to scale the image to the size of one tile
		double imageXScale = (double)Tile.TILE_SIZE / (double)image.getWidth();
		double imageYScale = (double)Tile.TILE_SIZE / (double)image.getHeight();

		// Create the transformation to do the scale
		AffineTransform scaleTransform = new AffineTransform();
		scaleTransform.scale(imageXScale, imageYScale);

		// Create the transformation operation
		imageScaleOp = new AffineTransformOp(scaleTransform, AffineTransformOp.TYPE_BILINEAR);
	}
	
	/**
	 * This method paints the player at their current position using
	 * the given instance of Graphics2D.
	 * 
	 * @param g2d the instance of Graphics2D to use to paint the player
	 */
	public void paint(Graphics2D g2d) {
		// Draw the player's character's image at the correct position and scaled so that
		// it fits in one tile
		g2d.drawImage(image, imageScaleOp, xPos*Tile.TILE_SIZE, yPos*Tile.TILE_SIZE);
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
	 * This method updates the player's position to the given coordinates.
	 * 
	 * @param newX the player's new x-position
	 * @param newY the player's new y-position
	 */
	public void updatePosition(int newX, int newY) {
		// Update our position
		xPos = newX;
		yPos = newY;
		
		// Inform the world of our change in position
		world.onPlayerPositionChange(newX, newY);
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
				updatePosition(xPos, Math.max(yPos-1, MIN_Y_POS));
				break;
			case 's':
				updatePosition(xPos, Math.min(yPos+1, MAX_Y_POS));
				break;

			// Horizontal Movement (0 = Right)
			case 'a':
				updatePosition(Math.max(xPos-1, MIN_X_POS), yPos);
				break;
			case 'd':
				updatePosition(Math.min(xPos+1, MAX_X_POS), yPos);
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

	/**
	 * This method sets the player's name.
	 * 
	 * @param name the player's new name
	 */
	public void setName(String nameIn) {
		name = nameIn;
	}

	/**
	 * This method gets the player's name.
	 * 
	 * @return the player's name
	 */
	public String getName() {
		return name;
	}

	/**
	 * This method gets the player's class.
	 * 
	 * @return the player's class
	 */
	public String getClassType() {
		return classType;
	}

	/**
	 * This method gets the player's image.
	 * 
	 * @return the player's image
	 */
	public BufferedImage getImage() {
		return image;
	}
}
