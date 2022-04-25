package game;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.imageio.ImageIO;

/**
 * This class is a subclass of Screen that is used to display
 * an area specified by an image and a map file. The image is
 * used as the area's background and the map file specifies the
 * location of any special tiles.
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
	 * The is the area's background image.
	 */
	private final BufferedImage mapImage;
	
	/**
	 * This is the map of special tiles that is populated by the map file.
	 */
	private final Tile[][] tileMap = new Tile[ROWS_OF_TILES][TILES_PER_ROW]; 
	
	/**
	 * This creates an AreaScreen by loading the given map image
	 * and populating the tile map with any special tiles specified
	 * by the map file.
	 * 
	 * @param mapImageIn the area's background image file
	 */
	public AreaScreen(File mapImageIn) {
		// Initially we can to fill the tile map with the empty
		// tile so that we never retrieve null from the tile map
		for (Tile[] row : tileMap) {
			Arrays.fill(row, Tile.EMPTY);
		}

		// Try to load the given map image file
		try {
			mapImage = ImageIO.read(mapImageIn);
		} catch (IOException e) {
			// There was an error loading the image so we cannot continue
			throw new RuntimeException("Error loading area background image!", e);
		}
		
		// Ensure that the map image has the same width as the screen
		if (mapImage.getWidth() != Main.SCREEN_WIDTH) {
			throw new IllegalArgumentException(
				String.format(
					"Error: Area background image and screen must have the same width: %dpx!",
					Main.SCREEN_WIDTH
				)
			);
		}

		// Ensure that the map image has the same height as the screen
		if (mapImage.getHeight() != Main.SCREEN_HEIGHT) {
			throw new IllegalArgumentException(
				String.format(
					"Error: Area background image and screen must have the same height: %dpx!",
					Main.SCREEN_HEIGHT
				)
			);
		}
	}
	
	/**
	 * This method is responsible for painting the screen. In this case,
	 * the method is drawing the area's background image and any special
	 * that need to be drawn on the screen.
	 * 
	 * @param g2d the instance of Graphics2D we can use to draw to the screen
	 */
	@Override
	public void paint(Graphics2D g2d) {
		// Draw the area's background image
		g2d.drawImage(mapImage, null, 0, 0);
	}
}
