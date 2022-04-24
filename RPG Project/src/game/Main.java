package game;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

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
	 * The number of frames rendered in one second.
	 */
	public static int FRAMES_PER_SECOND = 60;
	
	/**
	 * The number of milliseconds per frame.
	 */
	public static int MILLISECONDS_PER_FRAME = 1000/FRAMES_PER_SECOND;
	
	/**
	 * This is the entry point into the program.
	 * 
	 * @param args command-line arguments
	 */
	public static void main(String[] args) {
		// Create the window and configure it
		JFrame window = new JFrame();
		window.setTitle("EleMages: Battle Edition");
		window.setResizable(false);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		// Create the player
		Player player = new Player();
		
		// Create the world
		World world = new World(player);
		
		// Add the world to the window and make the window the correct size
		window.add(world);
		window.pack();
		
		// Center the window and show it
		window.setLocationRelativeTo(null);
		window.setVisible(true);
		
		// Create a Swing Timer to repaint the world every MILLISECONDS_PER_FRAME
		// milliseconds so we end up with a frame rate approximately equivalent
		// to FRAMES_PER_SECOND
		Timer worldRepaintTimer = new Timer(MILLISECONDS_PER_FRAME, e -> world.repaint());
		
		// Start the world repaint timer on the Event Dispatch Thread (EDT)
		SwingUtilities.invokeLater(worldRepaintTimer::start);
		
		// This is the game loop
		while (true) {
			// Update the world
			world.update();
		}
	}
}
