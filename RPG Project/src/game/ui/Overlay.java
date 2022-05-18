package game.ui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.SpringLayout;
import javax.swing.event.MouseInputListener;

import game.Main;
import game.World;
import game.entities.Player;

/**
 * This class is the class that all overlays (such as the pause screen overlay)
 * must extend. This class along with the world manages the overlay to ensure
 * that it works and is painted correctly.
 */
@SuppressWarnings("serial")
public abstract class Overlay extends JComponent {
	/**
	 * This is the spring layout that is used to lay out
	 * the overlay.
	 */
	protected final SpringLayout layout;
	
	/**
	 * This is the world.
	 */
	protected final World world;
	
	/**
	 * This is the player.
	 */
	protected final Player player;

	/**
	 * This constructs an Overlay and calls the necessary methods
	 * to initialize the ui and add all required java swing components
	 * to the screen.
	 * 
	 * @param worldIn  the world
	 * @param playerIn the player
	 */
	protected Overlay(World worldIn, Player playerIn) {
		world = worldIn;
		player = playerIn;

		// Create the spring layout
		layout = new SpringLayout();
		setLayout(layout);

		// Add a dummy mouse listener so that all mouse events are intercepted
		// and blocked from being passed through to the content pane
		addMouseListener(DummyMouseEventListener.INSTANCE);
		addMouseMotionListener(DummyMouseEventListener.INSTANCE);
	}

	/**
	 * This method is called by Overlay's constructor to create all the
	 * ui components (java swing components) used by the overlay and add
	 * them to the screen.
	 */
	public abstract void createAndAddSwingComponents();

	/**
	 * This method paints the overlay's background using the given
	 * Graphics2D object.
	 * 
	 * @param g2d the Graphics2D object to use to paint the
	 *            overlay's background
	 */
	protected abstract void paintBackground(Graphics2D g2d);

	/**
	 * This method paints the overlay which includes its background
	 * and all of the java swing components it uses.
	 * 
	 * @param g the instance of Graphics to use to draw the overlay
	 */
	@Override
	public final void paint(Graphics g) {
		// If the instance of Graphics is an instance of Graphics2D
		// then paint the overlay's background
		if (g instanceof Graphics2D) {
			paintBackground((Graphics2D)g);
		}

		// Paint the overlay
		super.paint(g);
	}

	/**
	 * This method returns the size that this overlay should be which
	 * should be the size of the screen.
	 */
	@Override
	public final Dimension getPreferredSize() {
		return Main.SCREEN_SIZE;
	}

	/**
	 * This is a mouse input listener used by the Overlay class to listen
	 * for mouse events and do nothing. This is required because otherwise
	 * click events would be passed through the overlay even though it is
	 * a glass pane.
	 */
	private static final class DummyMouseEventListener implements MouseInputListener {
		/**
		 * This is the single instance of this class that can be used for
		 * every case where an instance of this class is required.
		 */
		public static final DummyMouseEventListener INSTANCE = new DummyMouseEventListener();

		/**
		 * The constructor for this class is private since an
		 * instance other than the one provided should never
		 * need to be constructed.
		 */
		private DummyMouseEventListener() {}

		@Override
		public void mouseClicked(MouseEvent e) {}

		@Override
		public void mousePressed(MouseEvent e) {}

		@Override
		public void mouseReleased(MouseEvent e) {}

		@Override
		public void mouseEntered(MouseEvent e) {}

		@Override
		public void mouseExited(MouseEvent e) {}

		@Override
		public void mouseDragged(MouseEvent e) {}

		@Override
		public void mouseMoved(MouseEvent e) {}
	}
}
