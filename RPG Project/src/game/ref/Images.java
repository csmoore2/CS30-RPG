package game.ref;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * This class contains various images that are required by the game
 * which can be statically loaded and reused.
 */
public final class Images {
	// Defines buffered images for enemies, bosses, locked image screens, and keys
	public static BufferedImage enemyFireImage;
	public static BufferedImage bossFireImage;
	public static BufferedImage enemyGemImage;
	public static BufferedImage bossGemImage;
	public static BufferedImage enemyIceImage;
	public static BufferedImage bossIceImage;
	public static BufferedImage enemyRockImage;
	public static BufferedImage bossRockImage;
	public static BufferedImage lockedFireImage;
	public static BufferedImage lockedGemImage;
	public static BufferedImage lockedIceImage;
	public static BufferedImage lockedRockImage;
	public static BufferedImage keyImage;
	
	/**
	 * This method is called to load all of the images defined above.
	 */
	public static void initializeImages() {
		// Try to load all of the images defined above
		try {
			// Attempts to initialize the buffered images for each of the enemies and bosses
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
			// There was an error loading an image so we cannot continue
			throw new RuntimeException("Error loading image!", e);
		}
	}
}
