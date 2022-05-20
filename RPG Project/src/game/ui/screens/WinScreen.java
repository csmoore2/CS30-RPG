package game.ui.screens;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import game.Main;

/**
 * This is the screen that is shown the the player when they win the game.
 * It consists of a background colour and some text.
 */
public class WinScreen implements IScreen {
	/**
	 * This is the background colour of this screen.
	 */
	private static final Color BACKGROUND_COLOUR = new Color(0, 100, 0);
	
	/**
	 * This method paints the win screen. This just consists of painting
	 * a background colour and some text.
	 * 
	 * @param g2d the instance of Graphics2D to use to draw this screen
	 */
	@Override
	public void paint(Graphics2D g2d) {
		// Draw the background colour (a nice green)
		g2d.setColor(BACKGROUND_COLOUR);
		g2d.fillRect(0, 0, Main.SCREEN_WIDTH, Main.SCREEN_HEIGHT);
		
		// Draw the text "Congratulations!"
		g2d.setColor(Color.WHITE);
		g2d.setFont(new Font(null, Font.BOLD, 48));
		g2d.drawString("Congratulations!", (Main.SCREEN_WIDTH / 2) - 200, Main.SCREEN_HEIGHT / 2);
	}
}
