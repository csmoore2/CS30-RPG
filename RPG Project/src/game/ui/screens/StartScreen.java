package game.ui.screens;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.SpringLayout;

import game.Main;

import static javax.swing.SpringLayout.*;

/**
 * This class extends JComponent rather than implementing IScreen,
 * due to the fact that it needs to use other JComponents, and is
 * used to display the start screen which is shown when the game first
 * starts. This screen will consist of a background, the game's title,
 * and the start button.
 */
@SuppressWarnings("serial")
public class StartScreen extends JComponent {
	/**
	 * This is the spacing that will be used between the margins of the
	 * title text and start button, and the vertical centre of the screen.
	 */
	public static final int VERTICAL_CENTRE_MARGIN_SPACING = 100;
	
	/**
	 * This is the font that will be used for the game's title.
	 */
	public static final Font TITLE_TEXT_FONT = new Font("Title Font", Font.BOLD, 80);
	
	/**
	 * This is the font that will be used for the start button.
	 */
	public static final Font START_BUTTON_FONT = new Font("Start Button Font", Font.PLAIN, 30);
	
	/**
	 * This is the text that will be displayed on the start button.
	 */
	public static final String START_BUTTON_TEXT = "Start Game!";
	
	/**
	 * This is the path to the background image of this screen. Since this is hardcoded
	 * we will assume that it is the correct size and does not need to be scaled.
	 */
	public static final String BACKGROUND_IMAGE_PATH = "res/test2.jpg";
	
	/**
	 * This stores the background image of this screen.
	 */
	private final BufferedImage backgroundImage;
	
	/**
	 * This constructs the start screen by adding the title text and start
	 * button to the screen with the appropriate positions and configurations.
	 * The callback provided to this constructor is called when the start button
	 * is clicked and should start the game.
	 * 
	 * @param startGameCallback the callback that starts the game when the start
	 *                          button is clicked
	 */
	public StartScreen(Runnable startGameCallback) {
		// Attempt to load the background image. If this is unsuccessful
		// then exit the program with an exception.
		try {
			backgroundImage = ImageIO.read(new File(BACKGROUND_IMAGE_PATH));
		} catch (IOException e) {
			throw new RuntimeException("Unable to load start screen background image!", e);
		}
		
		// Use a SpringLayout to layout this screen
		SpringLayout layout = new SpringLayout();
		setLayout(layout);
		
		// Create a JLabel to display the game's title
		JLabel titleText = new JLabel(Main.GAME_NAME);
		titleText.setFont(TITLE_TEXT_FONT);
		
		// Align the title text to be centred horizontally and above the vertical centre of the screen
		layout.putConstraint(VERTICAL_CENTER, titleText, -VERTICAL_CENTRE_MARGIN_SPACING, VERTICAL_CENTER, this);
		layout.putConstraint(HORIZONTAL_CENTER, titleText, 0, HORIZONTAL_CENTER, this);
		
		// Create a button that will start the game when clicked. This will
		// be accomplished by calling the callback provided to the constructor.
		JButton startButton = new JButton(START_BUTTON_TEXT);
		startButton.setFont(START_BUTTON_FONT);
		startButton.addActionListener((a) -> startGameCallback.run());
		
		// Align the start button to be centred horizontally and below the vertical centre of the screen
		layout.putConstraint(VERTICAL_CENTER, startButton, VERTICAL_CENTRE_MARGIN_SPACING, VERTICAL_CENTER, this);
		layout.putConstraint(HORIZONTAL_CENTER, startButton, 0, HORIZONTAL_CENTER, this);
		
		// Add the title text and start button to the screen
		add(titleText);
		add(startButton);
	}
	
	/**
	 * This draws background image of the screen.
	 * 
	 * @param g the Graphics object to use to draw the screen
	 */
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		// Draw the background image as long as 'g' is an instance of Graphics2D
		if (g instanceof Graphics2D) {
			((Graphics2D) g).drawImage(backgroundImage, null, 0, 0);
		}
	}
	
	/**
	 * This returns the size we want the screen to be.
	 * 
	 * @return the dimensions this screen should have
	 */
	@Override
	public Dimension getPreferredSize() {
		return Main.SCREEN_SIZE;
	}
}
