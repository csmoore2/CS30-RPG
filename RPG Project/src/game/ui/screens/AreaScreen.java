package game.ui.screens;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import game.Main;
import game.ui.Tile;

/**
 * This class implements IScreen and is used to display an area
 * specified by an image and a map file. The image is used as the
 * area's background and the map file specifies the location of
 * any special tiles.
 */
public class AreaScreen implements IScreen {
	/**
	 * This is the number tiles in one row of tiles on the screen.
	 */
	public static final int TILES_PER_ROW = 9;
	
	/**
	 * This is the number of rows of tiles on the screen.
	 */
	public static final int ROWS_OF_TILES = 9;
	
	/**
	 * This is a cache of all the previously loaded area screens so they can be reused.
	 */
	public static final Map<String, AreaScreen> AREA_SCREEN_CACHE = new HashMap<>();
	
	/**
	 * This keeps track of whether or not the AreaScreen's tile map has been populated.
	 */
	private boolean tileMapPopulated = false;
	
	/**
	 * The is the area's background image.
	 */
	private final BufferedImage mapImage;
	
	/**
	 * This is the scale transformation operation that is applied to the background
	 * image to make it the same size at the screen.
	 */
	private final AffineTransformOp imageScaleOp;
	
	/**
	 * This is the map of special tiles that is populated by the map file.
	 */
	private final Tile[][] tileMap = new Tile[ROWS_OF_TILES][TILES_PER_ROW];
	
	/**
	 * This method is what is called by other classes to create instances
	 * of AreaScreen. This method will create a new AreaScreen if an AreaScreen
	 * with the same background has not been previously created. Otherwise this
	 * method return the previously created AreaScreen.
	 * 
	 * @param mapImagePathIn the path to the area's background image
	 * 
	 * @return an AreaScreen that uses the given background image
	 */
	public static AreaScreen createNewAreaScreen(String mapImagePathIn) {
		// Either get or create the AreaScreen
		AreaScreen screen = AREA_SCREEN_CACHE.computeIfAbsent(
			mapImagePathIn,
			(imagePath) -> new AreaScreen(imagePath)
		);
		
		// Populate the area's tile map (if it has not already been populated)
		screen.populateTileMap(mapImagePathIn);
		
		// Return the AreaScreen
		return screen;
	}
	
	/**
	 * This creates an AreaScreen by loading the given map image
	 * and populating the tile map with any special tiles specified
	 * by the map file.
	 * 
	 * @param mapImagePathIn the path to the area's background image
	 */
	private AreaScreen(String mapImagePathIn) {
		// Try to load the given map image file
		try {
			mapImage = ImageIO.read(new File(mapImagePathIn));
		} catch (IOException e) {
			// There was an error loading the image so we cannot continue
			throw new RuntimeException("Error loading area background image!", e);
		}
		
		// Determine the scale factors that will need to be applied to the image
		// to make it fit the screen
		double scaleFactorX = (double) Main.SCREEN_WIDTH  / mapImage.getWidth();
		double scaleFactorY = (double) Main.SCREEN_HEIGHT / mapImage.getHeight();
		
		// Create the transformation to do the scale
		AffineTransform scaleTransform = new AffineTransform();
		scaleTransform.scale(scaleFactorX, scaleFactorY);
		
		// Create the transformation operation
		imageScaleOp = new AffineTransformOp(scaleTransform, AffineTransformOp.TYPE_BILINEAR);
		
		// Initially we can to fill the tile map with the empty
		// tile so that we never retrieve null from the tile map
		for (Tile[] row : tileMap) {
			Arrays.fill(row, Tile.EMPTY_TILE);
		}
	}
	
	/**
	 * This method populates the tile map from the background image's accompanying
	 * map file.
	 */
	private void populateTileMap(String mapImagePathIn) {
		// If the tile map has already been populated or is being populated then do nothing
		if (tileMapPopulated) return;
		
		// Update 'tileMapPopulated'
		tileMapPopulated = true;
		
		// Defines loading and battle titles, dependent upon which level the player is currently on
		switch (Main.currentLevel) {
			case 1:
				tileMap[0][4] = new Tile.LoadingZone("res/firezonebackground.png", 2, 4, 8);
				tileMap[4][0] = new Tile.LoadingZone("res/rockzonebackground.png", 5, 8, 4);
				tileMap[8][4] = new Tile.LoadingZone("res/icezonebackground.png", 4, 4, 0);
				tileMap[4][8] = new Tile.LoadingZone("res/gemzonebackground.png", 3, 0, 4);
				break;
			case 2:
				tileMap[8][4] = new Tile.LoadingZone("res/greenzonebackground.png", 1, 4, 0);
				break;
			case 3:
				tileMap[4][0] = new Tile.LoadingZone("res/greenzonebackground.png", 1, 8, 4);
				break;
			case 4:
				tileMap[0][4] = new Tile.LoadingZone("res/greenzonebackground.png", 1, 4, 8);
				break;
			case 5:
				tileMap[4][8] = new Tile.LoadingZone("res/greenzonebackground.png", 1, 0, 4);
				break;
			case 6:
				break;
		}
	}
	
	/**
	 * This method returns the tile at the given position.
	 * 
	 * @param x the x-position of the tile
	 * @param y the y-position of the tile
	 * 
	 * @return the tile at the given position
	 */
	public Tile getTileAtPos(int x, int y) {
		return tileMap[y][x];
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
		g2d.drawImage(mapImage, imageScaleOp, 0, 0);
		
		// Loop through each tile in the tile map
		for (int row = 0; row < ROWS_OF_TILES; row++) {
			for (int col = 0; col < TILES_PER_ROW; col++) {
				// Check if the tile should be painted
				if (tileMap[row][col].shouldPaint()) {
					// The tile should be painted, so paint it
					tileMap[row][col].paint(g2d, row*Tile.TILE_SIZE, col*Tile.TILE_SIZE);
				}
			}
		}
	}
}
