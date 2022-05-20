package game.ui;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.function.Function;

import game.Main;
import game.World;
import game.entity.IEnemy;
import game.entity.Player;
import game.ui.screens.AreaScreen;

/**
 * This class will be used as the superclass for all the
 * special tiles that are placed over an area's background image to trigger events.
 */
public class Tile {
	/**
	 * This is the width and height of a single tile in pixels.
	 */
	public static final int TILE_SIZE = Main.SCREEN_WIDTH/9;
	
	
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
	 * @param world the player's world
	 */
	public void performAction(Player player, World world) {}
	
	/**
	 * This class represents a loading zone tile. This tile is not
	 * painted, but when the player steps on it this tile moves the
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
		 * The new current level that the loading zone tile should switch to.
		 */
		public int tileLevelTo;
		
		
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
		public LoadingZone(String mapImagePathIn, int levelTo, int playerNewXIn, int playerNewYIn) {
			tileLevelTo = levelTo;
			Main.setCurrLevel(tileLevelTo);
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
			Main.setCurrLevel(tileLevelTo);
			world.changeArea(newScreen, playerNewX, playerNewY);
		}
	}
	
	/**
	 * This class represents a battle trigger tile. This tile is painted and
	 * when the player steps on it a battle is triggered.
	 */
	public static final class BattleTrigger extends Tile {
		/**
		 * This is a function that will generate an enemy given
		 * the player.
		 */
		private final Function<Player, IEnemy> enemyProducer;

		/**
		 * The is the enemy's displayed image
		 */
		private final BufferedImage enemyImage;

		/**
		 * This constructs a new BattleTrigger tile with the given enemy
		 * producing method as the method that produces the enemy for the
		 * player to fight when they step on this tile.
		 * 
		 * @param enemyProducerIn a function that, given the player, will produce
		 *                        an enemy that the player must fight when they
		 *                        step on this tile
		 *                        
		 * @param enemyImageIn the image that is inputed, representing the enemy tile
		 */
		public BattleTrigger(Function<Player, IEnemy> enemyProducerIn, BufferedImage enemyImageIn) {
			enemyProducer = enemyProducerIn;
			enemyImage = enemyImageIn;
			
		}

		/**
		 * This method is called when the player steps on this tile. In this
		 * case, that triggers a battle between the player and an enemy produced
		 * by the function stored in 'enemyProducer'.
		 * 
		 * @param player the player
		 * @param world  the player's world
		 * 
		 * @see Tile#performAction(Player, World)
		 */
		@Override
		public void performAction(Player player, World world) {
			// Initiate a battle between the player and an enemy produced
			// by 'enemyProducer'
			world.initiateBattle(enemyProducer.apply(player));
		}

		/**
		 * This method generates an enemy using this tile's enemy
		 * producing function and the given player.
		 * 
		 * @param player the player
		 * 
		 * @return the enemy generated by 'enemyProducer' using the
		 *         given player
		 */
		public IEnemy generateEnemy(Player player) {
			return enemyProducer.apply(player);
		}
		
		@Override
		public void paint(Graphics2D g2d, int y, int x) {
			// Create the transformation to do the scale
			AffineTransform scaleTransform = new AffineTransform();
			scaleTransform.scale((Main.SCREEN_WIDTH/9.0)/enemyImage.getWidth(), (Main.SCREEN_HEIGHT/9.0)/enemyImage.getHeight());
			
			// Create the transformation operation
			AffineTransformOp imageScaleOp = new AffineTransformOp(scaleTransform, AffineTransformOp.TYPE_BILINEAR);
			
			// Draw the enemy's image
			g2d.drawImage(enemyImage, imageScaleOp, x, y);
		}
		
		@Override
		public boolean shouldPaint() { return true; }
		
	}
	
	/**
	 * This class represents a locked off boss tile, that displays while a boss remains inacessible to the player.
	 */
	public static final class LockedOffBoss extends Tile {
		/**
		 * The is the tile's displayed image
		 */
		private final BufferedImage lockedImage;
		
		/**
		 * This constructs a new locked off boss tile.
		 * 
		 * @param lockedImage
		 *                        
		 */
		public LockedOffBoss(BufferedImage lockedImage) {
			this.lockedImage = lockedImage;
			
		}
	
		@Override
		public void paint(Graphics2D g2d, int y, int x) {
			// Create the transformation to do the scale
			AffineTransform scaleTransform = new AffineTransform();
			scaleTransform.scale((Main.SCREEN_WIDTH/9.0)/lockedImage.getWidth(), (Main.SCREEN_HEIGHT/9.0)/lockedImage.getHeight());

			// Create the transformation operation
			AffineTransformOp imageScaleOp = new AffineTransformOp(scaleTransform, AffineTransformOp.TYPE_BILINEAR);
			
			// Draw the tile's image
			g2d.drawImage(lockedImage, imageScaleOp, x, y);
		}
		
		@Override
		public boolean shouldPaint() { return true; }
		
	}
	
	/**
	 * This class represents a key tile. This tile is painted,
	 * and when the player steps on it, it is collected.
	 */
	public static final class KeyTile extends Tile {
		/**
		 * The is the tile's displayed image
		 */
		private final BufferedImage keyImage;
		
		/**
		 * This constructs a new locked off boss tile.
		 * 
		 * @param lockedImage
		 *                        
		 */
		public KeyTile(BufferedImage keyImage) {
			this.keyImage = keyImage;
			
		}
		
		/**
		 * This method is called when the player steps on this tile. In this
		 * case, is will perform the action of removing the key tile from tile map, and adding it to the inventory.
		 * 
		 * @param player the player
		 * @param world  the player's world
		 * 
		 * @see Tile#performAction(Player, World)
		 */
		@Override
		public void performAction(Player player, World world) {
			// Removes the tile from the respective location of the tile map, and increases the counter by 1
			AreaScreen currentScreen = (AreaScreen) world.screenStack.peek();
			Main.keysGet += 1;
			switch (Main.currentLevel) {
			case 2:
				currentScreen.tileMap[0][4] = null;
				break;
			case 3:
				currentScreen.tileMap[4][8] = null;
				break;
			case 4:
				currentScreen.tileMap[8][4] = null;
				break;
			case 5:
				currentScreen.tileMap[4][0] = null;
				break;
			}
		}
	
		@Override
		public void paint(Graphics2D g2d, int y, int x) {
			// Create the transformation to do the scale
			AffineTransform scaleTransform = new AffineTransform();
			scaleTransform.scale((Main.SCREEN_WIDTH/9.0)/keyImage.getWidth(), (Main.SCREEN_HEIGHT/9.0)/keyImage.getHeight());

			// Create the transformation operation
			AffineTransformOp imageScaleOp = new AffineTransformOp(scaleTransform, AffineTransformOp.TYPE_BILINEAR);
			
			// Draw the tile's image
			g2d.drawImage(keyImage, imageScaleOp, x, y);
		}
		
		@Override
		public boolean shouldPaint() { return true; }
		
	}
}
