package game.ui.screens;

import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.Graphics2D;

import javax.swing.JLabel;
import javax.swing.Spring;
import javax.swing.SpringLayout;

import game.Main;

import static javax.swing.SpringLayout.*;

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
	 * This is the font that will be used for the text displayed by this screen.
	 */
	private static final Font TEXT_FONT = new Font(null, Font.PLAIN, 48);

	/**
	 * This method adds all of the necessary java swing components for this screen
	 * to the screen. For this screen this is simply one JLabel.
	 * 
	 * @param screen the screen
	 * @param layout the screen's layout
	 * 
	 * @see IScreen#addSwingComponents(Container, SpringLayout)
	 */
	@Override
	public void addSwingComponents(Container screen, SpringLayout layout) {
		// Create a label to say congratulations to the player
		JLabel congratsLabel = new JLabel(
			"<html><center><b>Congratulations! You defeated Marduk and saved the world!</b></center></html>");
		
		congratsLabel.setHorizontalAlignment(JLabel.CENTER);
		congratsLabel.setFont(TEXT_FONT);
		congratsLabel.setForeground(Color.WHITE);
		
		// Set the label's width to be fixed so that its height is what changes
		layout.getConstraints(congratsLabel).setWidth(Spring.constant(Main.SCREEN_WIDTH));

		// Align the label to be in the centre of the screen
		layout.putConstraint(VERTICAL_CENTER, congratsLabel, 0, VERTICAL_CENTER, screen);

		// Add the label to the screen
		screen.add(congratsLabel);
	}
	
	/**
	 * This method paints the win screen. This just consists of painting
	 * a background colour since the text is a JLabel.
	 * 
	 * @param g2d the instance of Graphics2D to use to draw this screen
	 */
	@Override
	public void paint(Graphics2D g2d) {
		// Draw the background colour (a nice green)
		g2d.setColor(BACKGROUND_COLOUR);
		g2d.fillRect(0, 0, Main.SCREEN_WIDTH, Main.SCREEN_HEIGHT);
	}
}
