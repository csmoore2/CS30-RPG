package game.ui.screens;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.File;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.Spring;
import javax.swing.SpringLayout;

import game.Main;
import game.World;
import game.Zone;
import game.entity.Enemy;
import game.ui.Tile;

import static javax.swing.SpringLayout.*;

/**
 * This class implements IScreen and is used to display an area
 * specified by a zone. The zone's background image is used as the
 * area's background.
 */
public class AreaScreen implements IScreen {
	/*************************************************************************************/
	/*                                   SIZE CONSTANTS                                  */
	/*************************************************************************************/

	/**
	 * This is the length of one of the sides of the area screen. Since an area is square
	 * this means that every side has the same length and therefore this stores the length
	 * of every side of the area screen.
	 */
	public static final int AREA_SCREEN_SIZE = Main.SCREEN_HEIGHT;

	/**
	 * This is the width of the panel that displays various pieces of impormation about
	 * the player while they are in the world.
	 */
	public static final int PLAYER_PANEL_WIDTH = Main.SCREEN_WIDTH - AREA_SCREEN_SIZE;

	/**
	 * This is the width of the image of the player that will be shown on the right
	 * hand side of the screen.
	 */
	public static final int PLAYER_IMAGE_SIZE = PLAYER_PANEL_WIDTH / 2;

	/**
	 * This is the number tiles in one row of tiles on the screen.
	 */
	public static final int TILES_PER_ROW = 9;
	
	/**
	 * This is the number of rows of tiles on the screen.
	 */
	public static final int ROWS_OF_TILES = 9;

	/*************************************************************************************/
	/*                                    UI CONSTANTS                                   */
	/*************************************************************************************/

	/**
	 * This is the format string that will be used to nicely format the player's
	 * name for display.
	 */
	public static final String PLAYER_NAME_FORMAT = "<html><center><b>%s</b></center></html>";

	/**
	 * This is the font that will be used to display the player's name.
	 */
	public static final Font PLAYER_NAME_FONT = new Font("Player Name Font", Font.PLAIN, 48);

	/**
	 * This is the format string that will be used to nicely format the player's current
	 * area for display.
	 */
	public static final String PLAYER_ZONE_FORMAT = "<html><center><b>Current Zone:</b><br/>%s</center></html>";

	/**
	 * This is the format string that will be used to nicely format the player's level
	 * for display.
	 */
	public static final String PLAYER_LEVEL_FORMAT = "<html><center><b>Level:</b><br/>%d</center></html>";

	/**
	 * This is the format string that will be used to nicely format the amount of
	 * experience the player needs to gain to reach the next level for display.
	 */
	public static final String PLAYER_EXP_TO_NEXT_LEVEL_FORMAT = "<html><center><b>Experience to Next Level:</b><br/>%d</center></html>";

	/**
	 * This is the font that will be used to display various JLabels in the player panel.
	 */
	public static final Font PLAYER_PANEL_GENERAL_LABEL_FONT = new Font("Player Panel General Label Font", Font.PLAIN, 32);
	
	/**
	 * This is a cache of all the previously loaded area screens so they can be reused.
	 */
	public static final Map<Zone, AreaScreen> AREA_SCREEN_CACHE = new EnumMap<>(Zone.class);

	/*************************************************************************************/
	/*                                   UI COMPONENTS                                   */
	/*************************************************************************************/

	/**
	 * This variable keeps track of whether or not the ui components for the player
	 * panel have been initialized.
	 */
	private static boolean uiComponentsInitialized = false;

	/**
	 * This is the JLabel that displays the player's name.
	 */
	private static JLabel playerName;

	/**
	 * This is the JLabel that displays the player's image.
	 */
	private static JLabel playerImage;

	/**
	 * This is the JLabel that displays the zone that the player is currently in.
	 */
	private static JLabel playerZone;

	/**
	 * This is the JLabel that displays the player's current level.
	 */
	private static JLabel playerLevel;

	/**
	 * This is the JLabel that displays the amount of experience the player needs
	 * to gain to reach the next level.
	 */
	private static JLabel playerExpToNextLevel;

	/*************************************************************************************/
	/*                                 INSTANCE VARIABLES                                */
	/*************************************************************************************/

	/**
	 * This is the world.
	 */
	private final World world;

	/**
	 * This is the zone that the area screen represents.
	 */
	private final Zone zone;
	
	/**
	 * This keeps track of whether or not the AreaScreen's tile map has been populated.
	 */
	private boolean tileMapPopulated = false;
	
	/**
	 * This is the map of special tiles that is populated by the map file.
	 */
	public final Tile[][] tileMap = new Tile[ROWS_OF_TILES][TILES_PER_ROW];
	
	/**
	 * This method is what is called by other classes to create instances
	 * of AreaScreen. This method will create a new AreaScreen if an AreaScreen
	 * with the same zone has not been previously created. Otherwise this
	 * method return the previously created AreaScreen.
	 * 
	 * @param worldIn        the world
	 * @param zoneIn         the area screen's zone
	 * 
	 * @return an AreaScreen that represents the given zone
	 */
	public static AreaScreen createNewAreaScreen(World worldIn, Zone zoneIn) {
		// Either get or create the AreaScreen
		AreaScreen screen = AREA_SCREEN_CACHE.computeIfAbsent(
			zoneIn,
			(zone) -> new AreaScreen(worldIn, zone)
		);
		
		// Populate the area's tile map (if it has not already been populated)
		screen.populateTileMap();
		
		// Return the AreaScreen
		return screen;
	}
	
	/**
	 * This creates an AreaScreen to represent the given zone with
	 * a empty tile map by default.
	 * 
	 * @param worldIn        the world
	 * @param zoneIn         the zone the area screen represents
	 */
	private AreaScreen(World worldIn, Zone zoneIn) {
		world = worldIn;
		zone = zoneIn;
		
		// Initially we can to fill the tile map with the empty
		// tile so that we never retrieve null from the tile map
		for (Tile[] row : tileMap) {
			Arrays.fill(row, Tile.EMPTY_TILE);
		}
	}

	/*************************************************************************************/
	/*                                   TILE METHODS                                    */
	/*************************************************************************************/
	
	/**
	 * This method populates the tile map based on the zone the area screen
	 * is representing.
	 */
	private void populateTileMap() {
		// If the tile map has already been populated or is being populated then do nothing
		if (tileMapPopulated) return;
		
		// Update 'tileMapPopulated'
		tileMapPopulated = true;
		
		// Defines buffered images for enemies, bosses, locked image screens, and keys
		BufferedImage enemyFireImage;
		BufferedImage bossFireImage;
		BufferedImage enemyGemImage;
		BufferedImage bossGemImage;
		BufferedImage enemyIceImage;
		BufferedImage bossIceImage;
		BufferedImage enemyRockImage;
		BufferedImage bossRockImage;
		BufferedImage lockedFireImage;
		BufferedImage lockedGemImage;
		BufferedImage lockedIceImage;
		BufferedImage lockedRockImage;
		BufferedImage keyImage;
		
		// Attempts to initialize the buffered images for each of the enemies and bosses
		try {
			enemyFireImage = ImageIO.read(new File("res/enemyfire.png"));
			bossFireImage = ImageIO.read(new File("res/bossfire.png"));
			enemyGemImage = ImageIO.read(new File("res/enemygem.png"));
			bossGemImage = ImageIO.read(new File("res/bossgem.png"));
			enemyIceImage = ImageIO.read(new File("res/enemyice.png"));
			bossIceImage = ImageIO.read(new File("res/bossice.png"));
			enemyRockImage = ImageIO.read(new File("res/enemyrock.png"));
			bossRockImage = ImageIO.read(new File("res/bossrock.png"));
			lockedFireImage = ImageIO.read(new File("res/lockedOutFire.png"));
			lockedGemImage = ImageIO.read(new File("res/lockedOutGem.png"));
			lockedIceImage = ImageIO.read(new File("res/lockedOutIce.png"));
			lockedRockImage = ImageIO.read(new File("res/lockedOutRock.png"));
			keyImage = ImageIO.read(new File("res/key.png"));
		} catch (IOException e) {
			// There was an error loading the image so we cannot continue
			throw new RuntimeException("Error loading area background image!", e);
		}
		
		// Defines loading and battle titles, dependent upon which level the player is currently on
		switch (zone) {
			case GREEN_HUB: // Green zone/hub
				// Loading zone tiles
				tileMap[0][4] = new Tile.LoadingZone(world, Zone.FIRE, 4, 8);
				tileMap[4][0] = new Tile.LoadingZone(world, Zone.ROCK, 8, 4);
				tileMap[8][4] = new Tile.LoadingZone(world, Zone.ICE,  4, 0);
				tileMap[4][8] = new Tile.LoadingZone(world, Zone.GEM,  0, 4);	
				break;
			case FIRE: // Fire zone
				// Loading zone tile
				tileMap[8][4] = new Tile.LoadingZone(world, Zone.GREEN_HUB, 4, 0);
				
				// Battle trigger tiles
				tileMap[7][0] = new Tile.BattleTrigger((player) -> new Enemy(world, player.getExperience()), enemyFireImage);
				tileMap[3][0] = new Tile.BattleTrigger((player) -> new Enemy(world, player.getExperience()), enemyFireImage);
				tileMap[5][8] = new Tile.BattleTrigger((player) -> new Enemy(world, player.getExperience()), enemyFireImage);
				tileMap[1][4] = new Tile.BattleTrigger((player) -> new Enemy(world, player.getExperience()), bossFireImage);
				
				// Locked boss tile
				tileMap[2][4] = new Tile.LockedOffBoss(lockedFireImage);
				
				// Key tile
				tileMap[0][4] = new Tile.KeyTile(keyImage);
				break;
			case GEM: // Gem zone
				// Loading zone tile
				tileMap[4][0] = new Tile.LoadingZone(world, Zone.GREEN_HUB, 8, 4);
				
				// Battle trigger tiles
				tileMap[8][1] = new Tile.BattleTrigger((player) -> new Enemy(world, player.getExperience()), enemyGemImage);
				tileMap[8][5] = new Tile.BattleTrigger((player) -> new Enemy(world, player.getExperience()), enemyGemImage);
				tileMap[0][3] = new Tile.BattleTrigger((player) -> new Enemy(world, player.getExperience()), enemyGemImage);
				tileMap[4][7] = new Tile.BattleTrigger((player) -> new Enemy(world, player.getExperience()), bossGemImage);
				
				// Locked boss tile
				tileMap[4][6] = new Tile.LockedOffBoss(lockedGemImage);
				
				// Key tile
				tileMap[4][8] = new Tile.KeyTile(keyImage);
				break;
			case ICE: // Ice zone
				// Loading zone tile
				tileMap[0][4] = new Tile.LoadingZone(world, Zone.GREEN_HUB, 4, 8);
				
				// Battle trigger tiles
				tileMap[1][8] = new Tile.BattleTrigger((player) -> new Enemy(world, player.getExperience()), enemyIceImage);
				tileMap[5][8] = new Tile.BattleTrigger((player) -> new Enemy(world, player.getExperience()), enemyIceImage);
				tileMap[3][0] = new Tile.BattleTrigger((player) -> new Enemy(world, player.getExperience()), enemyIceImage);
				tileMap[7][4] = new Tile.BattleTrigger((player) -> new Enemy(world, player.getExperience()), bossIceImage);
				
				// Locked boss tile
				tileMap[6][4] = new Tile.LockedOffBoss(lockedIceImage);
				
				// Key tile
				tileMap[8][4] = new Tile.KeyTile(keyImage);
				break;
			case ROCK: // Rock zone
				// Loading zone tile
				tileMap[4][8] = new Tile.LoadingZone(world, Zone.GREEN_HUB, 0, 4);
				
				// Battle trigger tiles
				tileMap[0][3] = new Tile.BattleTrigger((player) -> new Enemy(world, player.getExperience()), enemyRockImage);
				tileMap[0][7] = new Tile.BattleTrigger((player) -> new Enemy(world, player.getExperience()), enemyRockImage);
				tileMap[8][5] = new Tile.BattleTrigger((player) -> new Enemy(world, player.getExperience()), enemyRockImage);
				tileMap[4][1] = new Tile.BattleTrigger((player) -> new Enemy(world, player.getExperience()), bossRockImage);
				
				// Locked boss tile
				tileMap[4][2] = new Tile.LockedOffBoss(lockedRockImage);
				
				// Key tile
				tileMap[4][0] = new Tile.KeyTile(keyImage);
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

	/*************************************************************************************/
	/*                                 PAINTING METHODS                                  */
	/*************************************************************************************/
	
	/**
	 * This method is responsible for painting the screen. In this case,
	 * the method is drawing the area's background image and any special
	 * that need to be drawn on the screen.
	 * 
	 * @param g2d the instance of Graphics2D we can use to draw to the screen
	 */
	@Override
	public void paint(Graphics2D g2d) {
		// Draw a black background under the background image
		g2d.setColor(Color.BLACK);
		g2d.fillRect(0, 0, Main.SCREEN_WIDTH, Main.SCREEN_HEIGHT);

		// Draw the area's background image (the zone's background image)
		g2d.drawImage(zone.getBackgroundImage(), null, null);
		
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

	/*************************************************************************************/
	/*                                UI COMPONENT METHODS                               */
	/*************************************************************************************/

	/**
	 * This method creates all of the java swing components required for the player panel.
	 * 
	 * @param world  the world
	 * @param screen the screen
	 * @param layout the screen's layout
	 */
	private static void createSwingComponents(World world, Container screen, SpringLayout layout) {
		/*****************************************************************************/
		/*                                PLAYER NAME                                */
		/*****************************************************************************/

		// Create a label to display the player's name
		playerName = new JLabel(String.format(
			PLAYER_NAME_FORMAT,
			world.getPlayer().getName()
		));
		playerName.setFont(PLAYER_NAME_FONT);
		playerName.setForeground(Color.WHITE);
		playerName.setHorizontalAlignment(JLabel.CENTER);
		
		// Set the label's width to be fixed so that its height is what changes
		layout.getConstraints(playerName).setWidth(Spring.constant(PLAYER_PANEL_WIDTH));

		// Align the player's name to be at the top right side of the screen
		layout.putConstraint(NORTH, playerName, 10, NORTH, screen);
		layout.putConstraint(EAST, playerName, 0, EAST, screen);

		// Add the label dsplaying the player's name to the screen but invisible
		playerName.setVisible(false);
		screen.add(playerName);

		/*****************************************************************************/
		/*                               PLAYER IMAGE                                */
		/*****************************************************************************/

		// Create a JLabel to display the player's image
		playerImage = new JLabel();
		playerImage.setPreferredSize(new Dimension(PLAYER_IMAGE_SIZE, PLAYER_IMAGE_SIZE));
		playerImage.setHorizontalAlignment(JLabel.CENTER);
		playerImage.setIcon(new ImageIcon( world.getPlayer().getImage().getScaledInstance(
			PLAYER_IMAGE_SIZE, PLAYER_IMAGE_SIZE, Image.SCALE_SMOOTH
		)));

		// Align the player's image to be below their name label
		layout.putConstraint(NORTH, playerImage, 20, SOUTH, playerName);
		layout.putConstraint(EAST, playerImage, 0, EAST, playerName);
		layout.putConstraint(WEST, playerImage, 0, WEST, playerName);

		// Add the label dsplaying the player's image to the screen but invisible
		playerImage.setVisible(false);
		screen.add(playerImage);

		/*****************************************************************************/
		/*                                PLAYER ZONE                                */
		/*****************************************************************************/

		// Create a label to display the name of the zone that the player is currently in
		playerZone = new JLabel(String.format(
			PLAYER_ZONE_FORMAT,
			world.getCurrentZone().getName()
		));
		playerZone.setFont(PLAYER_PANEL_GENERAL_LABEL_FONT);
		playerZone.setForeground(Color.WHITE);
		playerZone.setHorizontalAlignment(JLabel.CENTER);
		
		// Set the label's width to be fixed so that its height is what changes
		layout.getConstraints(playerZone).setWidth(Spring.constant(PLAYER_PANEL_WIDTH));

		// Align the player's zone to be below the player's image
		layout.putConstraint(NORTH, playerZone, 10, SOUTH, playerImage);
		layout.putConstraint(EAST, playerZone, 0, EAST, screen);

		// Add the label dsplaying the player's current zone to the screen but invisible
		playerZone.setVisible(false);
		screen.add(playerZone);

		/*****************************************************************************/
		/*                               PLAYER LEVEL                                */
		/*****************************************************************************/

		// Create a label to display the player's level
		playerLevel = new JLabel(String.format(
			PLAYER_LEVEL_FORMAT,
			world.getPlayer().getLevel()
		));
		playerLevel.setFont(PLAYER_PANEL_GENERAL_LABEL_FONT);
		playerLevel.setForeground(Color.WHITE);
		playerLevel.setHorizontalAlignment(JLabel.CENTER);
		
		// Set the label's width to be fixed so that its height is what changes
		layout.getConstraints(playerLevel).setWidth(Spring.constant(PLAYER_PANEL_WIDTH));

		// Align the player's level to be below the player's zone
		layout.putConstraint(NORTH, playerLevel, 40, SOUTH, playerZone);
		layout.putConstraint(EAST, playerLevel, 0, EAST, screen);

		// Add the label dsplaying the player's level to the screen but invisible
		playerLevel.setVisible(false);
		screen.add(playerLevel);

		/*****************************************************************************/
		/*                      PLAYER EXPERIENCE TO NEXT LEVEL                      */
		/*****************************************************************************/

		// Create a label to display the amount of experience the player needs to reach
		// the next level
		playerExpToNextLevel = new JLabel(String.format(
			PLAYER_EXP_TO_NEXT_LEVEL_FORMAT,
			world.getPlayer().getExperienceToNextLevel()
		));
		playerExpToNextLevel.setFont(PLAYER_PANEL_GENERAL_LABEL_FONT);
		playerExpToNextLevel.setForeground(Color.WHITE);
		playerExpToNextLevel.setHorizontalAlignment(JLabel.CENTER);
		
		// Set the label's width to be fixed so that its height is what changes
		layout.getConstraints(playerExpToNextLevel).setWidth(Spring.constant(PLAYER_PANEL_WIDTH));

		// Align the player's experience to next level label to be below the player's level
		layout.putConstraint(NORTH, playerExpToNextLevel, 40, SOUTH, playerLevel);
		layout.putConstraint(EAST, playerExpToNextLevel, 0, EAST, screen);

		// Add the label dsplaying the player's experience to next level to the screen but invisible
		playerExpToNextLevel.setVisible(false);
		screen.add(playerExpToNextLevel);

		// We have now initialized all of the ui components needed for the player
		// panel so update 'uiComponentsInitialized'
		uiComponentsInitialized = true;
	}

	/**
	 * This method adds all of the java swing compnents used by the player panel
	 * to the screen.
	 * 
	 * @param screen the screen
	 * @param layout the screen's layout
	 * 
	 * @see IScreen#addSwingComponents(Container, SpringLayout)
	 */
	@Override
	public void addSwingComponents(Container screen, SpringLayout layout) {
		// If the player panel's ui components have not been created, create them
		if (!uiComponentsInitialized) {
			createSwingComponents(world, screen, layout);
		}

		// Make all of the components of the player panel visible
		playerName.setVisible(true);
		playerImage.setVisible(true);
		playerZone.setVisible(true);
		playerLevel.setVisible(true);
		playerExpToNextLevel.setVisible(true);
	}

	/**
	 * This method removes all of the java swing components used by the player
	 * panel from the screen.
	 * 
	 * @param screen the screen
	 * 
	 * @see IScreen#removeSwingComponents(Container)
	 */
	@Override
	public void removeSwingComponents(Container screen) {
		// Make all of the components of the player panel invisible
		playerName.setVisible(false);
		playerImage.setVisible(false);
		playerZone.setVisible(false);
		playerLevel.setVisible(false);
		playerExpToNextLevel.setVisible(false);
	}

	/*************************************************************************************/
	/*                                UI UPDATING METHODS                                */
	/*************************************************************************************/

	/**
	 * This method is called by world to let us know that the player has changed
	 * zones and we need to update the label displaying the player's current zone.
	 */
	public void onPlayerZoneChanged() {
		// Update the text of the label displaying the player's current zone
		playerZone.setText(String.format(
			PLAYER_ZONE_FORMAT,
			world.getCurrentZone().getName()
		));
	}

	/*************************************************************************************/
	/*                                      GETTERS                                      */
	/*************************************************************************************/

	/**
	 * This method returns the zone that this area screen represents.
	 * 
	 * @return the zone that this area screen represents
	 */
	public Zone getZone() {
		return zone;
	}
}
