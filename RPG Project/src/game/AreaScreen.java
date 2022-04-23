package game;

import java.awt.Graphics2D;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This class is a subclass of Screen that is used to display
 * an area specified by a map file. A map file consists of lines
 * of characters, each representing a tile, and each line representing
 * a row.
 */
public class AreaScreen implements IScreen {
	/**
	 * This is the number tiles in one row of tiles on the screen.
	 */
	public static final int TILES_PER_ROW = Main.SCREEN_WIDTH  / Tile.TILE_SIZE;
	
	/**
	 * This is the number of rows of tiles on the screen.
	 */
	public static final int ROWS_OF_TILES = Main.SCREEN_HEIGHT / Tile.TILE_SIZE;
	
	/**
	 * This is the map of tiles that is populated from a file.
	 */
	private final Tile[][] tileMap = new Tile[ROWS_OF_TILES][TILES_PER_ROW]; 
	
	/**
	 * This creates an AreaScreen by populating a tile map
	 * from the specified file.
	 * 
	 * @param filePath the path to the file which specifies
	 *                 the map of this area
	 */
	public AreaScreen(Path filePath) {
		// Initially we can to fill the tile map with the empty
		// tile so that we never retrieve null from the tile map
		for (Tile[] row : tileMap) {
			Arrays.fill(row, Tile.EMPTY);
		}
		
		// Initialize the tile map from the file
		initTileMapFromFile(filePath);
	}
	
	/**
	 * This method populates the tile map of this AreaScreen using the
	 * specified file. Each line in the file represents a row of tiles
	 * and each character in a line represents a single file.
	 * 
	 * @param filePath the path to the file which specifies the map
	 *                 of this area
	 */
	private void initTileMapFromFile(Path filePath) {
		// Try retrieving the lines from the file. We use a try-with-resources
		// block so we do not have to worry about closing the file ourself.
		try (Stream<String> lineStream = Files.lines(filePath)) {
			// Convert the Stream to a List
			List<String> lines = lineStream.collect(Collectors.toList());
			
			// Loop through each line (row of tiles)
			for (int row = 0; row < lines.size() && row < ROWS_OF_TILES; row++) {
				String tileRow = lines.get(row);
				
				// Loop through each character (tile) in the row
				for (int col = 0; col < tileRow.length() && col < TILES_PER_ROW; col++) {
					// Add the tile to the tile map, first converting the character to
					// the appropriate value of Tile
					tileMap[row][col] = Tile.fromCharacter(tileRow.charAt(col));
				}
			}
		} catch (IOException e) {
			// If an IOException is thrown then print the stack trace so we know what happened
			e.printStackTrace();
		}
	}
	
	/**
	 * This method is responsible for painting the screen. In this case,
	 * the method is simply looping through each tile and drawing it as
	 * a coloured rectangle to the screen at the correct location.
	 * 
	 * @param g2d the instance of Graphics2D we can use to draw to the screen
	 */
	@Override
	public void paint(Graphics2D g2d) {
		// Loop through each tile in each row
		for (int row = 0; row < ROWS_OF_TILES; row++) {
			for (int col = 0; col < TILES_PER_ROW; col++) {
				// Retrieve the tile and tell the Graphics2D object to use the tile's colour
				Tile tile = tileMap[row][col];
				g2d.setColor(tile.getColour());
				
				// Draw the tile as a coloured rectangle at the correct location
				g2d.fillRect(col*Tile.TILE_SIZE, row*Tile.TILE_SIZE, Tile.TILE_SIZE, Tile.TILE_SIZE);
			}
		}
	}
}
