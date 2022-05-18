package game.ui;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;

import javax.swing.JButton;
import javax.swing.SpringLayout;

import game.Main;
import game.World;
import game.entities.IEnemy;

import static javax.swing.SpringLayout.*;

/**
 * This screen is shown when the player dies while fighting an
 * enemy. This screen will offer the player the option to either
 * retry the fight or quit the game.
 */
public class PlayerDeathScreen implements IScreen {
    /**
     * This is the colour of this screen's background.
     */
    public static final Color BACKGROUND_COLOUR = Color.DARK_GRAY;
	
	/**
	 * This is the font that will be used by buttons in this overlay.
	 */
	public static final Font BUTTON_FONT = new Font("Button Font", Font.BOLD, 32);
	
	/**
	 * This is the size of each button in this overlay.
	 */
	public static final Dimension BUTTON_SIZE = new Dimension(500, 75);
	
	/**
	 * This is the amount of vertical space between two buttons in this overlay.
	 */
	public static final int BUTTON_VERTICAL_SPACING = 100;

    /**
     * This is the button that the player presses if they
     * want to retry the battle they just lost.
     */
    private JButton retryBattleButton;

    /**
     * This is the button that the player presses if they
     * want to quit the game.
     */
    private JButton quitGameButton;

    /**
     * The world.
     */
    private final World world;

    /**
     * This is the enemy the player was facing when they died.
     */
    private final IEnemy enemy;

    /**
     * This constructs a PlayerDeathScreen using the given enemy
     * as the enemy that the player was fighting when they died.
     * 
     * @param worldIn the world
     * @param enemyIn the enemy the player was fighting when they died
     */
    public PlayerDeathScreen(World worldIn, IEnemy enemyIn) {
        world = worldIn;
        enemy = enemyIn;
    }

    /**
     * This method creates and adds all of the java swing components
     * needed by this screen to the screen. The java swing components
     * created by this method are arranged on the screen using the
     * provided spring layout.
     * 
     * @param screen the screen
     * @param layout the screen's layout
     * 
     * @see IScreen#addSwingComponents(Container, SpringLayout)
     */
    @Override
    public void addSwingComponents(Container screen, SpringLayout layout) {
		/******************************************************************
		 *                        RETRY BUTTON                        *
		 ******************************************************************/
		
		// Create a button that allows the player to retry the battle
        // they just lost
		retryBattleButton = new JButton("Retry Battle");
		retryBattleButton.setFont(BUTTON_FONT);
		retryBattleButton.setPreferredSize(BUTTON_SIZE);
		
		// Make the retry battle button first close this screen then
        // reset the enemy and restart the battle with the enemy
		retryBattleButton.addActionListener((a) -> {
            // Close this screen
            world.closeCurrentScreen();
            
            // Reset the enemy and then initiate a battle with it
            enemy.reset();
            world.initiateBattle(enemy);
        });
		
		// Align the retry battle button to be slightly above the vertical centre of the
		// screen and horizontally centered
		layout.putConstraint(VERTICAL_CENTER, retryBattleButton, -BUTTON_VERTICAL_SPACING/2, VERTICAL_CENTER, screen);
		layout.putConstraint(HORIZONTAL_CENTER, retryBattleButton, 0, HORIZONTAL_CENTER, screen);

		// Add the retry battle button to the screen
		screen.add(retryBattleButton);
		
		/******************************************************************
		 *                          QUIT BUTTON                           *
		 ******************************************************************/
		
		// Create a button that allows the player to quit the game
		quitGameButton = new JButton("Quit");
		quitGameButton.setFont(BUTTON_FONT);
		quitGameButton.setPreferredSize(BUTTON_SIZE);
		
		// Have the quit button call Main.quitGame when it is clicked. This will
		// show the user a dialog to confirm they want to exit.
		quitGameButton.addActionListener((a) -> Main.quitGame());
		
		// Align the quit button to be slightly below the vertical centre of the screen
		// and horizontally centered
		layout.putConstraint(VERTICAL_CENTER, quitGameButton, BUTTON_VERTICAL_SPACING/2, VERTICAL_CENTER, screen);
		layout.putConstraint(HORIZONTAL_CENTER, quitGameButton, 0, HORIZONTAL_CENTER, screen);

		// Add the quit button to the screen
		screen.add(quitGameButton);
    }

    /**
     * This method paints the screen which in this case is just a background
     * colour since java swing components are drawn separately.
     * 
     * @param g2d the instance of Graphics2D to use to draw the screen
     * 
     * @see IScreen#paint(Graphics2D)
     */
    @Override
    public void paint(Graphics2D g2d) {
        // Draw the background
        g2d.setBackground(BACKGROUND_COLOUR);
        g2d.fillRect(0, 0, Main.SCREEN_WIDTH, Main.SCREEN_HEIGHT);
    }

    /**
     * This method removes all the java swing components that were previously
     * added by this screen from the screen.
     * 
     * @param screen the screen
     * 
     * @see IScreen#removeSwingComponents(Container)
     */
    @Override
    public void removeSwingComponents(Container screen) {
        // Remove the retry and quit buttons from the screen
        screen.remove(retryBattleButton);
        screen.remove(quitGameButton);
    }
}
