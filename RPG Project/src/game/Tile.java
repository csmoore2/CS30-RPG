package game;

import java.awt.Color;

/**
 * This enum specifies every tile in the game and provides
 * methods to access each tile's properties. It also provides
 * a helper function to convert a single character to a tile.
 */
public enum Tile {
	EMPTY(Color.BLACK), GRASS(Color.GREEN), PATH(Color.GRAY);

	/**
	 * This is the width and height of a single tile in pixels.
	 */
	public static final int TILE_SIZE = 32;
	
	/**
	 * These store the tile's properties.
	 */
	private final Color colour;
	
	/**
	 * This method converts the given character to the
	 * appropriate tile. If no such tile exists then the
	 * empty tile is returned.
	 * 
	 * @param c the character to convert into a tile
	 * 
	 * @return the tile that the character represents, or
	 *         the empty tile if no such tile exists
	 */
	public static Tile fromCharacter(char c) {
		switch (c) {
			case 'g': return GRASS;
			case 'p': return PATH;
			case '.': return EMPTY;
			default:  return EMPTY;
		}
	}
	
	/**
	 * This creates a tile with the specified properties.
	 * 
	 * @param colourIn the colour of the tile
	 */
	private Tile(Color colourIn) {
		colour = colourIn;
	}
	
	/**
	 * This method returns the tile's colour.
	 * 
	 * @return the colour of the tile
	 */
	public Color getColour() {
		return colour;
	}
}
