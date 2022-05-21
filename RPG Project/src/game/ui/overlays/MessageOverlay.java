package game.ui.overlays;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import javax.swing.JLabel;
import javax.swing.Spring;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;

import game.World;
import game.World.Message;
import game.entity.Player;

import static javax.swing.SpringLayout.*;

/**
 * This class represents the overlay that is displayed to the player when
 * a message is being shown to them. This overlay has no background and
 * consists of just a single box in the middle of the screen. When the
 * amount of time the message is shown for elapses then the overlay hides
 * itself and resumes the game.
 */
@SuppressWarnings("serial")
public class MessageOverlay extends Overlay {
	/**
	 * This is the width that the JLabel displaying the message to the
	 * player will have.
	 */
	private static final Spring MESSAGE_LABEL_WIDTH = Spring.constant(800);
	
	/**
	 * This is the border that will be displayed around the message JLabel that
	 * is displaying the message to the user.
	 */
	private static final Border MESSAGE_LABEL_BORDER = new LineBorder(Color.BLACK, 4);
	
	/**
	 * This is the font that will be used by the message JLabel that is displaying
	 * the message to the user.
	 */
	private static final Font MESSAGE_LABEL_FONT = new Font("Message Label Font", Font.PLAIN, 20);
	
	/**
	 * This is the message that will be displayed to the player.
	 */
	private Message message;

	/**
	 * This label displays the messages to the player.
	 */
	private JLabel messageLabel;
	
	/**
	 * This is the time that the overlay began showing the message
	 * to the player.
	 */
	private long startTime = -1;
	
	/**
	 * This constructs the message overlay using the given parameters.
	 * 
	 * @param worldIn   the world
	 * @param playerIn  the player
	 * @param messageIn the message that will be displayed to the player
	 */
	public MessageOverlay(World worldIn, Player playerIn, Message messageIn) {
		super(worldIn, playerIn);
		message = messageIn;
	}
	/**
	 * This method is used by this class to create all the java swing
	 * components used by this overlay. For this overlay this just means
	 * the single box in the centre of the screen that displays the message.
	 * 
	 * @see Overlay#createAndAddSwingComponents()
	 */
	@Override
	public void createAndAddSwingComponents() {
		// Create a JLabel to display the message
		messageLabel = new JLabel(message.message());
		messageLabel.setFont(MESSAGE_LABEL_FONT);
		messageLabel.setHorizontalAlignment(JLabel.CENTER);
		
		// Set the label's background colour to white and give it a border
		messageLabel.setOpaque(true);
		messageLabel.setBackground(Color.WHITE);
		messageLabel.setBorder(MESSAGE_LABEL_BORDER);
		
		// Set the label's width to be fixed so that its height is what changes
		layout.getConstraints(messageLabel).setWidth(MESSAGE_LABEL_WIDTH);
		
		// Align the label with the message to be in the centre of the screen
		layout.putConstraint(VERTICAL_CENTER, messageLabel, 0, VERTICAL_CENTER, this);
		layout.putConstraint(HORIZONTAL_CENTER, messageLabel, 0, HORIZONTAL_CENTER, this);
		
		// Add the label to the screen and set the time that we started displaying the label
		// to the current time
		add(messageLabel);
		startTime = System.currentTimeMillis();
	}

	/**
	 * This method makes the overlay start showing the new given message
	 * and discard its previous message/
	 * 
	 * @param messageIn the new message to display
	 */
	private void showMessage(Message messageIn) {
		// Change the message
		message = messageIn;

		// Change the message displayed by the messageLabel
		messageLabel.setText(messageIn.message());

		// Set the start time to now
		startTime = System.currentTimeMillis();
	}
	
	/**
	 * This method is called by World to update the overlay. This overlay uses the
	 * update method to check if the amount of time we are supposed to display the
	 * message for has elapsed. If it has then it notifies the world to stop showing
	 * this overlay and resume the game.
	 */
	@Override
	public void update() {
		// If we have started showing the message to the player check if the
		// amount of time we are supposed to show the message for has elapsed
		if (startTime != -1) {
			// Get the current time and calculate the elapsed time
			long currentTime = System.currentTimeMillis();
			long elapsedMilliseconds = currentTime - startTime;
			
			// Convert the elapsed time to seconds
			long elapsedSeconds = elapsedMilliseconds / 1000;
			
			// Check if the elapsed time is greater than or equal to the amount of time we
			// are supposed to show the message for. If it is then check for a new message.
			if (elapsedSeconds >= message.time()) {
				// If there is a new message to display then show it. Otherwise, notify
				// the world that we are done showing messages
				if (world.hasNextMessage()) {
					showMessage(world.getNextMessage());
				} else {
					world.onMessageFinishDisplay();
				}
			}
		}
	}

	/**
	 * This screen does not have a background image or colour. Therefore
	 * this method is unused.
	 * 
	 * @param g2d the instance of Graphics2D to use to draw the background
	 *            of this overlay
	 * 
	 * @see Overlay#paintBackground(Graphics2D)
	 */
	@Override
	protected void paintBackground(Graphics2D g2d) {}
}
