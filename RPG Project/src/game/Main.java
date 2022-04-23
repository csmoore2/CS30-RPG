package game;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 * This class contains the entry point into the game; it is where
 * the initial setup takes place and the game starts.
 */
public class Main {
	/**
	 * The width of the game's window.
	 */
	public static final int SCREEN_WIDTH = 1280;
	
	/**
	 * The height of the game's window.
	 */
	public static final int SCREEN_HEIGHT = 736;
	
	/**
	 * This is the entry point into the program.
	 * 
	 * @param args command-line arguments
	 */
	public static void main(String[] args) {
		// Create the window and configure it
		JFrame window = new JFrame();
		window.setName("[Insert Name Here]");
		window.setResizable(false);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		// Create the world
		World world = new World();
		
		// Add the world to the window and make the window the correct size
		window.add(world);
		window.pack();
		
		// Center the window and show it
		window.setLocationRelativeTo(null);
		window.setVisible(true);
		
		// This is the game loop
		while (true) {
			//SwingUtilities.updateComponentTreeUI(window);
			world.update();
		}
	}
}
