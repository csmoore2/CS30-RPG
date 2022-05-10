package game.ui;

import java.awt.Graphics2D;

/**
 * This interface specifies methods common to all screens. This
 * also provides a handy way for the world to handle screens.
 */
public interface IScreen {
	/**
	 * This method paints the screen using the given
	 * Graphics2D object.
	 * 
	 * @param g2d the Graphics2D object to use to paint the screen
	 */
	void paint(Graphics2D g2d);
}
