package game.ui;

import java.awt.Graphics2D;

import javax.swing.JComponent;
import javax.swing.SpringLayout;

/**
 * This interface specifies methods common to all screens. This
 * also provides a handy way for the world to handle screens.
 */
public interface IScreen {
	/**
	 * This method is called by the world when the screen is
	 * first shown so that the screen can add java swing
	 * components to the screen.
	 * 
	 * @param screen the screen
	 * @param layout the screen's layout
	 */
	default void addSwingComponents(JComponent screen, SpringLayout layout) {}

	/**
	 * This method paints the screen using the given
	 * Graphics2D object.
	 * 
	 * @param g2d the Graphics2D object to use to paint the screen
	 */
	void paint(Graphics2D g2d);

	/**
	 * This method is called by the world when the screen is
	 * about to be hidden so that the screen can remove any
	 * java swing components it added to the screen.
	 * 
	 * @param screen the screen
	 */
	default void removeSwingComponents(JComponent screen) {}
}
