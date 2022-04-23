package game;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.nio.file.Paths;
import java.util.Stack;

import javax.swing.JComponent;

/**
 * This class represents the world the player is playing in. This class
 * is responsible for a large portion of the logic that runs the game. This
 * class manages the player, updates the game state, and ensures everything
 * is drawn among other things.
 */
@SuppressWarnings("serial")
public class World extends JComponent {
	/** 
	 * This is the screen stack. The top of the screen stack is the screen
	 * that is currently visible to the user. Pushing a new screen on represents
	 * forward navigation and popping of the top screen represents backwards navigation.
	 */
	private Stack<IScreen> screenStack = new Stack<>();
	
	/**
	 * This constructs the world by pushing the starting screen onto the screen stack
	 * and initializing the game's state.
	 */
	public World() {
		super();
		
		// Push the starting screen onto the screen stack
		screenStack.push(new AreaScreen(Paths.get("res", "test.map")));
	}
	
	/**
	 * This draws the current screen onto the game's window.
	 * If the screen stack is empty then nothing is drawn.
	 * 
	 * @param g the Graphics object to use to draw the current screen
	 */
	@Override
	public void paintComponent(Graphics g) {
		// Ensure we were passed an instance of Graphics2D
		if (g instanceof Graphics2D) {
			// Convert the Graphics instance to a Graphics2D instance
			Graphics2D g2d = (Graphics2D)g;
			
			// If the screen stack is not empty then draw the screen on the top
			// of the stack using the provided instance of Graphics2D
			if (!screenStack.empty()) {
				screenStack.peek().paint(g2d);
			}
		}
	}
	
	/**
	 * This updates the world.
	 */
	public void update() {
	}
	
	/**
	 * This returns the size we want the game's window and therefore the
	 * world to be.
	 * 
	 * @return the dimensions the game's window should have
	 */
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(Main.SCREEN_WIDTH, Main.SCREEN_HEIGHT); 
	}
}
