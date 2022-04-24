package game;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyListener;
import java.nio.file.Paths;
import java.util.Stack;

import javax.swing.JComponent;

/**
 * This class represents the world the player is playing in. This class
 * is responsible for a large portion of the logic that runs the game. This
 * class manages the player, updates the game state, and ensures everything
 * is drawn.
 */
@SuppressWarnings("serial")
public class World extends JComponent {
	/** 
	 * This is the screen stack. The top of the screen stack is the screen
	 * that is currently visible to the user. Pushing a new screen on represents
	 * forward navigation and popping of the top screen represents backwards navigation.
	 */
	private final Stack<IScreen> screenStack = new Stack<>();
	
	/**
	 * This is the player.
	 */
	private final Player player;
	
	/**
	 * This keeps track of whether or not the world needs to be repainted,
	 * or if repainting can be skipped next time the repaint method is called.
	 */
	private boolean repaintRequired = false;
	
	/**
	 * This constructs the world by pushing the starting screen onto the screen stack
	 * and initializing the game's state.
	 * 
	 * @param playerIn the player
	 */
	public World(Player playerIn) {
		super();
		
		// Store a reference to the player and update the player's world
		player = playerIn;
		player.setWorld(this);
		
		// Push the starting screen onto the screen stack
		screenStack.push(new AreaScreen(Paths.get("res", "test.map")));
	}
	
	/**
	 * This draws the current screen onto the game's window (if the screen
	 * stack is not empty), and then draws the player.
	 * 
	 * @param g the Graphics object to use to draw the world
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
			
			// Paint the player
			player.paint(g2d);
			
			// Update 'repaintRequired'
			repaintRequired = false;
		}
	}
	
	@Override
	public void repaint() {
		// If we do not need to repaint the window then exit
		if (!repaintRequired) return;
		
		super.repaint();
	}
	
	/**
	 * This method tells the world that it needs to perform a repaint.
	 */
	public void markRepaintRequired() {
		repaintRequired = true;
	}
	
	/**
	 * This updates the world.
	 */
	public void update() {
	}
	
	/**
	 * This method registers the given KeyListener to receive
	 * key events.
	 * 
	 * @param keyListener the KeyListener to register
	 * 
	 * @see JComponent#addKeyListener(KeyListener)
	 */
	public void registerKeyListener(KeyListener keyListener) {
		addKeyListener(keyListener);
	}
	
	/**
	 * This method unregisters the given KeyListener so it will
	 * no longer receive key events.
	 * 
	 * @param keyListener the KeyListener to unregister
	 * 
	 * @see JComponent#removeKeyListener(KeyListener)
	 */
	public void unregisterKeyListener(KeyListener keyListener) {
		removeKeyListener(keyListener);
	}
	
	@Override
	public boolean isFocusable() {
		return true;
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
