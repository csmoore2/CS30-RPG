package game;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import game.entities.Player;
import game.ui.CharacterCreationScreen;
import game.ui.StartScreen;

/**
 * This class contains the entry point into the game; it is where
 * the initial setup takes place and the game starts.
 */
public class Main {
	/**
	 * This is the name of the game.
	 */
	public static final String GAME_NAME = "EleMages: Battle Edition";

	/**
	 * The width of the game's window.
	 */
	public static final int SCREEN_WIDTH = 1280;
	
	/**
	 * The height of the game's window.
	 */
	public static final int SCREEN_HEIGHT = 720;
	
	/**
	 * The number of frames rendered in one second.
	 */
	public static final int FRAMES_PER_SECOND = 60;
	
	/**
	 * The number of milliseconds per frame.
	 */
	public static final int MILLISECONDS_PER_FRAME = 1000/FRAMES_PER_SECOND;

	/**
	 * This is the game's window.
	 */
	public static JFrame window = null;
	
	/**
	 * This is the entry point into the program.
	 * 
	 * @param args command-line arguments
	 */
	public static void main(String[] args) {
		// Create the window and configure it
		window = new JFrame();
		window.setTitle(GAME_NAME);
		window.setResizable(false);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		// Display the start screen and have it call the 'showCharacterSelectionScreen' method
		// when the player is ready to begin the game
		window.add(new StartScreen(Main::showCharacterSelectionScreen));
		window.pack();

		// Center the window and show it
		window.setLocationRelativeTo(null);
		window.setVisible(true);
	}
	
	/**
	 * This method is called by the start screen when the player is ready to
	 * begin the game. This method shows the character selection screen and tells
	 * it to call the 'startGame' method once the player has chosen a character.
	 */
	private static void showCharacterSelectionScreen() {
		// Reinitialize the window
		reinitializeWindow();
		
		// Display the character creation screen so the player can create their character
		// and set it up to call the 'startGame' method when the player is ready to start
		window.add(new CharacterCreationScreen(Main::startGame));
		window.pack();

		// Center the window and show it
		window.setLocationRelativeTo(null);
		window.setVisible(true);
	}

	/**
	 * This method is called by the character selection screen when the
	 * player is ready to start the game. This method creates the world,
	 * sets up the window, and beings both the game loop and world repaint
	 * timer.
	 * 
	 * @param player the character chosen by the player
	 */
	private static void startGame(Player player) {
		// Reinitialize the window
		reinitializeWindow();
		
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
		
		// This is the game loop. We start it running on a daemon thread so that it does not block this thread,
		// which should be the Event Dispatch Thread (EDT) used by Java Swing, and so that it ends when the other
		// threads in this program stop
		Thread gameLoopThread = new Thread(() -> {
			while (true) {
				// Update the world
				world.update();
			}
		});
		gameLoopThread.setDaemon(true);
		gameLoopThread.start();
	}
	
	/**
	 * This method reinitializes the window. This method is required due
	 * to issues when completely switching the content of the window.
	 */
	private static void reinitializeWindow() {
		// Hide the window and then completely reinitialize it
		window.setVisible(false);
		window = new JFrame();
		window.setTitle(GAME_NAME);
		window.setResizable(false);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
