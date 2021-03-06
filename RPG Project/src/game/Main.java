package game;

import java.awt.Dimension;
import java.security.SecureRandom;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import game.entity.Player;
import game.ui.screens.CharacterCreationScreen;
import game.ui.screens.StartScreen;

/**
 * This class contains the entry point into the game; it is where
 * the initial setup takes place and the game starts.
 */
public class Main {
	/**
	 * This is the instance of Random that will be used by everything in the game when
	 * generating random information.
	 */
	public static final Random RANDOM = new SecureRandom();

	/**
	 * This is the name of the game.
	 */
	public static final String GAME_NAME = "Elemental Battle of the Mages";

	/**
	 * This is the name of the game but formatted so that it will look nice
	 * on the start screen.
	 */
	public static final String GAME_NAME_START_SCREEN = "<html><center>Elemental Battle of<br/>the Mages</center></html>";

	/**
	 * The width of the game's window.
	 */
	public static final int SCREEN_WIDTH = 1050;
	
	/**
	 * The height of the game's window.
	 */
	public static final int SCREEN_HEIGHT = 700;
	
	/**
	 * This is the size of the screen.
	 */
	public static final Dimension SCREEN_SIZE = new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT);
	
	/**
	 * The number of frames rendered in one second.
	 */
	public static final int FRAMES_PER_SECOND = 60;
	
	/**
	 * The number of milliseconds per frame.
	 */
	public static final int MILLISECONDS_PER_FRAME = 1000/FRAMES_PER_SECOND;
	
	/**
	 * This is the title of the dialog that is shown to the player to ensure
	 * they want to quit the game.
	 */
	public static final String CONFIRM_QUIT_DIALOG_TITLE = "Confirm Exit";
	
	/**
	 * This is the text that is displayed by the dialog that is shown to the player
	 * to ensure they want to quit the game.
	 */
	public static final String CONFIRM_QUIT_DIALOG_TEXT =
			"Are you sure you want to quit the game? (all progress will be lost)";

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
		// Create the world
		World world = new World(player);

		// Reinitialize the window
		reinitializeWindow();

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

		// Show the introduction to the player
		showIntroduction(world);
		
		// This is the game loop. We start it running on a daemon thread so that it does not block this thread,
		// which should be the Event Dispatch Thread (EDT) used by Java Swing, and so that it ends when the other
		// threads in this program stop
		Thread gameLoopThread = new Thread(() -> {
			// Update the world forever
			while (true) {
				// Update the world
				world.update();
			}
		}, "Game Loop Thread");
		gameLoopThread.setDaemon(true);
		gameLoopThread.start();
	}

	/**
	 * This method shows the introduction to the player as a series of messages
	 * on the screen. The introduction covers the story line as well as the basic
	 * controls for the game.
	 * 
	 * @param world the world
	 */
	private static void showIntroduction(World world) {
		world.showMessage("Welcome to <b>Elemental Battle of the Mages</b>!", 5);
		world.showMessage("You are the last descendant of a family of powerful mages who used "     +
		                  "their magic for good. The rest of your family was eliminated in <b>The " +
						  "Last Great Elemental War</b> against <b>Marduk</b> and his followers.",
						  15);
		world.showMessage("It is now up to you to to defeat all of <b>Marduk's</b> followers and " +
		                  "then <b>Marduk</b> himself. By doing so you will free the world from "  +
						  "the evil that grips it and usher in a golden age of magic.",
						  20);
		world.showMessage("Go now! In the areas surrounding this place you will find <b>Marduk's</b> "  +
		                  "followers and the keys to the realm where <b>Marduk</b> himself dwells. Be " +
						  "careful though, there are minor mages along your path who will try to "      +
						  "defeat you to gain favour with <b>Marduk</b>.",
						  20);
		world.showMessage("To move you must use the w, a, s, and d keys on your keyboard. If at any "   +
		                  "time you wish to leave simply pause the game by pressing escape and exit "   +
						  "the game. Furthermore, for every <b>100 experience points</b> you gain you " +
						  "level up which will give you <b>1 attribute point</b> to spend. These "      +
						  "points can be spent by selecting \"View Attributes/Level Up\" in the "       +
						  "pause screen. Note, though, that you cannot pause the game while a "         +
						  "message box is being displayed.",
						  30);
		world.showMessage(
			String.format("It is important to know that the effectiveness of an effect (like an attack "       +
						  "or health potion) may differ from the attributed value, due to random variation. "  +
						  "Also a tip: if you are having trouble with an enemy try wandering around to world " +
						  "to encounter some random enemies and gain experience points. One final note: if "   +
						  "the movement keys are not working try clicking inside the game's window. The time " +
						  "has now come for you to go out into the world. The whole world is counting on you " +
						  "%s. May luck be on your side!",
						  world.getPlayer().getName()
			),
			25);
	}

	/**
	 * This method quits the game but first asks the user to confirm
	 * that they do want to exit since they will lose all of their
	 * progress by exiting.
	 */
	public static void quitGame() {
		// When the user clicks the button show a dialog to confirm that they want to exit
		int confirmOption = JOptionPane.showConfirmDialog(
			window, CONFIRM_QUIT_DIALOG_TEXT,  CONFIRM_QUIT_DIALOG_TITLE, JOptionPane.YES_NO_OPTION);
		
		// If the user confirms they want to exit then exit with exit code zero
		if (confirmOption == JOptionPane.YES_OPTION) {
			System.exit(0);
		}
	}
}
