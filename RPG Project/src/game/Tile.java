package game;

import java.awt.Graphics2D;

/**
 * This class will be used as the superclass for all the
 * special tiles that are placed over an area's background image to trigger events.
 */
public class Tile {
	/**
	 * This is the width and height of a single tile in pixels.
	 */
	public static final int TILE_SIZE = 20;
	
	/**
	 * This is an empty tile; it is not painted nor does it trigger anything.
	 */
	public static final Tile EMPTY_TILE = new Tile();
	
	/**
	 * This method returns true if the tile should be painted,
	 * and otherwise false.
	 * 
	 * @return whether or not the tile should be painted
	 */
	public boolean shouldPaint() { return false; }
	
	/**
	 * This paints the tile using the given instance of Graphics2D at the
	 * given x-position and y-position.
	 * 
	 * @param g2d the instance of Graphics2D to use to draw the tile
	 * @param x   the x-position of the top left corner of the tile on the screen
	 * @param y   the y-position of the top left corner of the tile on the screen
	 */
	public void paint(Graphics2D g2d, int x, int y) {}
	
	/**
	 * This performs the tile's action when the player steps on
	 * the tile.
	 * 
	 * @param player the player
	 * @param world  the player's world
	 */
	public void performAction(Player player, World world) {}
	
	/**
	 * This class represents a loading zone tile. This tile is not
	 * painted but when the player steps on it, this tile moves the
	 * player to a new map.
	 */
	public static final class LoadingZone extends Tile {
		/**
		 * This is the area this loading zone will move the player to.
		 */
		private final AreaScreen newScreen;
		
		/**
		 * This will be the player's new x-position after the area change.
		 */
		private final int playerNewX;
		
		/**
		 * This will be the player's new y-position after the area change.
		 */
		private final int playerNewY;
		
		/**
		 * This creates a new loading zone tile using the given parameters.
		 * 
		 * @param mapImagePathIn this is the path to the background image
		 *                       of area the loading zone will load
		 * @param playerNewXIn   this is the player's new x-position after
		 *                       the area change
		 * @param playerNewYIn   this is the player's new y-position after
		 *                       the area change
		 */
		public LoadingZone(String mapImagePathIn, int playerNewXIn, int playerNewYIn) {
			newScreen = AreaScreen.createNewAreaScreen(mapImagePathIn);
			playerNewX = playerNewXIn;
			playerNewY = playerNewYIn;
		}
		
		/**
		 * This method changes the area to the area stored in 'newScreen' and
		 * updates the player's position to the position they should be at after
		 * the area change.
		 * 
		 * @param player the player
		 * @param world  the player's world
		 * 
		 * @see Tile#performAction(Player, World)
		 */
		@Override
		public void performAction(Player player, World world) {
			world.changeArea(newScreen, playerNewX, playerNewY);
		}
	}
}
