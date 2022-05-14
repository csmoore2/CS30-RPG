package game.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.EnumMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;

import game.Attribute;
import game.Main;
import game.entities.Player;
import game.util.MutableDouble;
import game.util.MutableInteger;

import static javax.swing.SpringLayout.*;

/**
 * This class is responsible for handling the overlay that is displayed
 * while the game is paused. This will include the ability to quit the
 * game and the option for the player to view their attributes and level
 * up if they have enough experience.
 */
public class PauseScreenOverlay implements IScreen {
	/**
	 * This record stores all the ui data associated with a primary
	 * attribute for the pause screen overlay.
	 */
	public static final record PrimaryAttributeUIData(
		JLabel valueLabel, MutableInteger value, MutableInteger playerValue, JButton decrementButton, JButton incrementButton) {}

	/**
	 * This record stores all the ui data associated with a secondary
	 * attribute for the pause screen overlay.
	 */
	public static final record SecondaryAttributeUIData(JLabel valueLabel, MutableDouble value, MutableDouble playerValue) {}

	/**
	 * This stores the colour of the background of the pause menu which is
	 * a semi-transparent black.
	 */
	public static final Color PAUSE_BACKGROUND_COLOUR = new Color(0.0f, 0.0f, 0.0f, 0.5f);
	
	/**
	 * This is the font that will be used to display titles in this overlay.
	 */
	public static final Font TITLE_FONT = new Font("Title Font", Font.BOLD, 32);

	/**
	 * This is the font that will be used for buttons in the attribute panel.
	 */
	public static final Font ATTR_PANEL_BUTTON_FONT = new Font("Attribute Panel Button Font", Font.BOLD, 20);

	/**
	 * This is the font that will be used for the label that displays the number
	 * of attribute points the player has available to spend.
	 */
	public static final Font AVAILABLE_ATTR_POINTS_FONT = new Font(null, Font.BOLD, 16);
	
	/**
	 * This is the font that will be used by the labels for attributes in this overlay.
	 */
	public static final Font ATTR_LABEL_FONT = new Font("Attribute Label Font", Font.BOLD, 20);
	
	/**
	 * This is the font that will be used by the values for attributes in this overlay.
	 */
	public static final Font ATTR_VALUE_FONT = new Font("Attribute Value Font", Font.PLAIN, 20);

	/**
	 * This is the text colour that will be used for attributes' values
	 * when they have been updated.
	 */
	public static final Color ATTR_UPDATED_VALUE_FONT_COLOUR = new Color(0, 128, 0);
	
	/**
	 * This is the font that will be used by buttons in this overlay.
	 */
	public static final Font BUTTON_FONT = new Font("Button Font", Font.BOLD, 32);
	
	/**
	 * This is the size of each button in this HUD.
	 */
	public static final Dimension BUTTON_SIZE = new Dimension(500, 75);
	
	/**
	 * This is the amount of vertical space between two buttons in this overlay.
	 */
	public static final int BUTTON_VERTICAL_SPACING = 100;
	
	/**
	 * This is the border that will be placed around panels displayed by this overlay.
	 */
	public static final Border PANEL_BORDER = new LineBorder(Color.BLACK, 4);

	/**
	 * This is the size of the attribute panel.
	 */
	public static final Dimension ATTRIBUTE_PANEL_SIZE = new Dimension(800, 550);

	/**
	 * This is the string that is used to format the label that displays how many
	 * attribute points the player has available to spend.
	 */
	public static final String AVAILABLE_ATTR_POINTS_FORMAT_STRING = "Attribute Points to Spend: %d";

	/**
	 * This is the vertical spacing between two attributes in the attribute panel.
	 */
	public static final int ATTRIBUTE_PANEL_ATTRIBUTE_SPACING = 50;

	/**
	 * This is the size of an attribute label in the attribute panel.
	 */
	public static final Dimension ATTRIBUTE_PANEL_ATTR_LABEL_SIZE = new Dimension(300, 50);

	/**
	 * This is the size of the decrement and increment buttons in the
	 * attribute panel.
	 */
	public static final Dimension ATTRIBUTE_PANEL_DEC_INC_BUTTON_SIZE = new Dimension(25, 25);
	
	/**
	 * This is the text that will be displayed by the quit game button.
	 */
	public static final String QUIT_BUTTON_TEXT = "Quit Game";
	
	/**
	 * This is the title of the dialog that is shown to the player to ensure
	 * they want to quit the game.
	 */
	public static final String CONFIRM_QUIT_DIALOG_TITLE ="Confirm Exit";
	
	/**
	 * This is the text that is displayed by the dialog that is shown to the player
	 * to ensure they want to quit the game.
	 */
	public static final String CONFIRM_QUIT_DIALOG_TEXT =
			"Are you sure you want to quit the game? (all progress will be lost)";
	
	/**
	 * This map maps each primary attribute to it corresponding ui data record.
	 */
	private final Map<Attribute, PrimaryAttributeUIData> primaryAttributeUIDataMap = new EnumMap<>(Attribute.class);

	/**
	 * This map mapseach secondary attribute to it corresponding ui data record.
	 */
	private final Map<Attribute, SecondaryAttributeUIData> secondaryAttributeUIDataMap = new EnumMap<>(Attribute.class);
	
	/**
	 * This is the player.
	 */
	private Player player;
	
	/**
	 * This is the attribute button.
	 */
	private JButton attributeButton;

	/**
	 * This is the panel that will diaplay the player's attributes
	 * and allow them to level up.
	 */
	private JPanel attributePanel;

	/**
	 * This is the number of attribute points that the player has
	 * available to spend.
	 */
	private int availableAttrPoints;

	/**
	 * This is the label inside the attribute panel that displays
	 * how many attribute points the player has to spend.
	 */
	private JLabel availableAttrPointsLabel;
	
	/**
	 * This is the quit game button.
	 */
	private JButton quitButton;
	
	/**
	 * This constructs the pause screen overlay.
	 * 
	 * @param playerIn the player
	 */
	public PauseScreenOverlay(Player playerIn) {
		player = playerIn;
	}

	/*************************************************************************************/
	/*                                UI CREATION METHODS                                */
	/*************************************************************************************/
	
	/**
	 * This method is used by this class to create all the java
	 * swing components used by the pause screen overlay and to
	 * show the initial option menu.
	 * 
	 * @param screen the screen
	 * @param layout the screen's layout
	 * 
	 * @see IScreen#addSwingComponents(JComponent, SpringLayout)
	 */
	@Override
	public void addSwingComponents(JComponent screen, SpringLayout layout) {
		/******************************************************************
		 *                        ATTRIBUTE BUTTON                        *
		 ******************************************************************/
		
		// Create a button that allows the player to view their abilities and
		// level them up if they have enough experience
		attributeButton = new JButton("View Attributes/Level Up");
		attributeButton.setFont(BUTTON_FONT);
		attributeButton.setPreferredSize(BUTTON_SIZE);
		
		// Make the attribute button show the attribute panel
		attributeButton.addActionListener((a) -> showAttributePanel(screen));
		
		// Align the attribute button to be slightly above the vertical centre of the
		// screen and horizontally centered
		layout.putConstraint(VERTICAL_CENTER, attributeButton, -BUTTON_VERTICAL_SPACING/2, VERTICAL_CENTER, screen);
		layout.putConstraint(HORIZONTAL_CENTER, attributeButton, 0, HORIZONTAL_CENTER, screen);

		// Add the attribute button to the screen
		screen.add(attributeButton);
		
		/******************************************************************
		 *                        ATTRIBUTE PANEL                         *
		 ******************************************************************/
		
		// Create a panel that allows the player to view their attributes
		// and level up
		attributePanel = new JPanel();
		attributePanel.setPreferredSize(ATTRIBUTE_PANEL_SIZE);
		attributePanel.setBorder(PANEL_BORDER);
		
		// Use a spring layout to layout the components inside the attribute panel
		SpringLayout attributePanelLayout = new SpringLayout();
		attributePanel.setLayout(attributePanelLayout);
		
		// Create the ui components inside the attribute panel
		createAttributePanelUIElements(screen, attributePanelLayout);

		// Centre the attribute panel in the screen
		layout.putConstraint(VERTICAL_CENTER, attributePanel, 0, VERTICAL_CENTER, screen);
		layout.putConstraint(HORIZONTAL_CENTER, attributePanel, 0, HORIZONTAL_CENTER, screen);

		// Add the attribute panel to the screen
		screen.add(attributePanel);
		
		/******************************************************************
		 *                          QUIT BUTTON                           *
		 ******************************************************************/
		
		// Create a button that allows the player to quit the game
		quitButton = new JButton(QUIT_BUTTON_TEXT);
		quitButton.setFont(BUTTON_FONT);
		quitButton.setPreferredSize(BUTTON_SIZE);
		
		// Give the quit button a click listener that confirms the user wants to
		// quit the game
		quitButton.addActionListener((a) -> {
			// When the user clicks the button show a dialog to confirm that they want to exit
			int confirmOption = JOptionPane.showConfirmDialog(
				screen, CONFIRM_QUIT_DIALOG_TEXT,  CONFIRM_QUIT_DIALOG_TITLE, JOptionPane.YES_NO_OPTION);
			
			// If the user confirms they want to exit then exit with exit code zero
			if (confirmOption == JOptionPane.YES_OPTION) {
				System.exit(0);
			}
		});
		
		// Align the quit button to be slightly below the vertical centre of the screen
		// and horizontally centered
		layout.putConstraint(VERTICAL_CENTER, quitButton, BUTTON_VERTICAL_SPACING/2, VERTICAL_CENTER, screen);
		layout.putConstraint(HORIZONTAL_CENTER, quitButton, 0, HORIZONTAL_CENTER, screen);

		// Add the quit button to the screen
		screen.add(quitButton);
		
		// Show the option menu by default
		showOptionMenu(screen);
	}
	
	/**
	 * This method creates the ui components that are displayed inside
	 * the attribute panel. It will also lay out the ui components using
	 * the provided spring layout which should be the attribute panel's
	 * spring layout.
	 * 
	 * @param screen the screen
	 * @param layout the attribute panel's spring layout
	 */
	private void createAttributePanelUIElements(JComponent screen, SpringLayout panelLayout) {
		/*****************************************************************************/
		/*                                   TITLE                                   */
		/*****************************************************************************/
		
		// Create a label that displays the title of the panel
		JLabel titleLabel = new JLabel("Attributes");
		titleLabel.setFont(TITLE_FONT);
		titleLabel.setHorizontalAlignment(JLabel.CENTER);
		
		// Center the title label at the top of the panel
		panelLayout.putConstraint(NORTH, titleLabel, 10, NORTH, attributePanel);
		panelLayout.putConstraint(WEST, titleLabel, 0, WEST, attributePanel);
		panelLayout.putConstraint(EAST, titleLabel, 0, EAST, attributePanel);
		
		// Add the title label to the attribute panel
		attributePanel.add(titleLabel);

		/*****************************************************************************/
		/*                     AVAILABLE ATTRIBUTE POINTS LABEL                      */
		/*****************************************************************************/

		// Create a label that displays how many attribute points the player has
		// to spend
		availableAttrPointsLabel = new JLabel();
		availableAttrPointsLabel.setFont(AVAILABLE_ATTR_POINTS_FONT);
		availableAttrPointsLabel.setHorizontalAlignment(JLabel.CENTER);

		// Center the available attribute points label below the title
		panelLayout.putConstraint(NORTH, availableAttrPointsLabel, 10, SOUTH, titleLabel);
		panelLayout.putConstraint(WEST, availableAttrPointsLabel, 0, WEST, attributePanel);
		panelLayout.putConstraint(EAST, availableAttrPointsLabel, 0, EAST, attributePanel);

		// Add the available attribute points label to the attribute panel
		attributePanel.add(availableAttrPointsLabel);
		
		/*****************************************************************************/
		/*                            PRIMARY ATTRIBUTES                             */
		/*****************************************************************************/
		
		// Loop through each primary attribute
		for (int i = 0; i < Attribute.PRIMARY_ATTRIBUTES.length; i++) {
			// Retrieve the attribute
			Attribute attr = Attribute.PRIMARY_ATTRIBUTES[i];

			/*******************************************************/
			/*              ATTRIBUTE LABEL AND VALUE              */
			/*******************************************************/
			
			// Create a label for the attribute
			JLabel attrLabel = new JLabel(attr.getName() + ':');
			attrLabel.setFont(ATTR_LABEL_FONT);
			attrLabel.setHorizontalAlignment(JLabel.RIGHT);
			attrLabel.setPreferredSize(ATTRIBUTE_PANEL_ATTR_LABEL_SIZE);
			
			// Create a label to display the attribute's value
			JLabel attrValue = new JLabel(String.valueOf(player.getPrimaryAttributeValue(attr)));
			attrValue.setFont(ATTR_VALUE_FONT);

			// Align the atttribute label to be on the left side of the panel and
			// below the previous attribute
			final int approximateLabelHeight = 10;
			int verticalSpace = (i+1)*ATTRIBUTE_PANEL_ATTRIBUTE_SPACING + (i-1)*approximateLabelHeight;

			panelLayout.putConstraint(NORTH, attrLabel, verticalSpace, SOUTH, availableAttrPointsLabel);
			panelLayout.putConstraint(WEST, attrLabel, -100, WEST, attributePanel);

			// Align the attribute value to be next to the attribute label and
			// vertically aligned with the attribute label
			panelLayout.putConstraint(VERTICAL_CENTER, attrValue, 0, VERTICAL_CENTER, attrLabel);
			panelLayout.putConstraint(WEST, attrValue, 10, EAST, attrLabel);

			// Add the attribute label and value to the attribute panel
			attributePanel.add(attrLabel);
			attributePanel.add(attrValue);

			/*******************************************************/
			/*      ATTRIBUTE INCREMENT AND DECREMENT BUTTONS      */
			/*******************************************************/

			// Create a button to decrement the attribute's value
			JButton decrementButton = new JButton("-");
			decrementButton.setPreferredSize(ATTRIBUTE_PANEL_DEC_INC_BUTTON_SIZE);
			decrementButton.addActionListener((a) -> decrementAttribute(attr));
			decrementButton.setEnabled(false);

			// Create a button to increment the attribute's value
			JButton incrementButton = new JButton("+");
			incrementButton.setPreferredSize(ATTRIBUTE_PANEL_DEC_INC_BUTTON_SIZE);
			incrementButton.addActionListener((a) -> incrementAttribute(attr));

			// Only enable the increment button if the player can level up
			incrementButton.setEnabled(player.canLevelUp());

			// Align the decrement button to be next to the attribute value
			panelLayout.putConstraint(VERTICAL_CENTER, decrementButton, 0, VERTICAL_CENTER, attrValue);
			panelLayout.putConstraint(WEST, decrementButton, 10, EAST, attrValue);

			// Align the increment button to be next to the decrement button
			panelLayout.putConstraint(VERTICAL_CENTER, incrementButton, 0, VERTICAL_CENTER, decrementButton);
			panelLayout.putConstraint(WEST, incrementButton, 5, EAST, decrementButton);

			// Add the decrement and increment buttons to the attribute panel
			attributePanel.add(decrementButton);
			attributePanel.add(incrementButton);

			/*******************************************************/
			/*              ATTRIBUTE UI DATA RECORD               */
			/*******************************************************/

			// Create the primary attribute's ui data record and insert it into the map
			PrimaryAttributeUIData uiDataRecord = new PrimaryAttributeUIData(
				attrValue,
				new MutableInteger(player.getPrimaryAttributeValue(attr)),
				new MutableInteger(player.getPrimaryAttributeValue(attr)),
				decrementButton,
				incrementButton
			);
			primaryAttributeUIDataMap.put(attr, uiDataRecord);
		}
		
		/*****************************************************************************/
		/*                           SECONDARY ATTRIBUTES                            */
		/*****************************************************************************/
		
		// Loop through each secondary attribute
		for (int i = 0; i < Attribute.SECONDARY_ATTRIBUTES.length; i++) {
			// Retrieve the attribute
			Attribute attr = Attribute.SECONDARY_ATTRIBUTES[i];

			/*******************************************************/
			/*              ATTRIBUTE LABEL AND VALUE              */
			/*******************************************************/
			
			// Create a label for the attribute
			JLabel attrLabel = new JLabel(attr.getName() + ':');
			attrLabel.setFont(ATTR_LABEL_FONT);
			attrLabel.setHorizontalAlignment(JLabel.RIGHT);
			attrLabel.setPreferredSize(ATTRIBUTE_PANEL_ATTR_LABEL_SIZE);
			
			// Create a label to display the attribute's scaled value
			JLabel attrValue = new JLabel(String.valueOf(player.getSecodaryAttributeValue(attr)));
			attrValue.setFont(ATTR_VALUE_FONT);

			// Align the atttribute label to be on the right side of the panel and
			// below the previous attribute
			final int approximateLabelHeight = 10;
			int verticalSpace = (i+1)*ATTRIBUTE_PANEL_ATTRIBUTE_SPACING + (i-1)*approximateLabelHeight;

			panelLayout.putConstraint(NORTH, attrLabel, verticalSpace, SOUTH, availableAttrPointsLabel);
			panelLayout.putConstraint(WEST, attrLabel, -25, HORIZONTAL_CENTER, attributePanel);

			// Align the attribute value to be next to the attribute label and
			// vertically aligned with the attribute label
			panelLayout.putConstraint(VERTICAL_CENTER, attrValue, 0, VERTICAL_CENTER, attrLabel);
			panelLayout.putConstraint(WEST, attrValue, 10, EAST, attrLabel);

			// Add the attribute label and value to the attribute panel
			attributePanel.add(attrLabel);
			attributePanel.add(attrValue);

			/*******************************************************/
			/*              ATTRIBUTE UI DATA RECORD               */
			/*******************************************************/

			// Create the primary attribute's ui data record and insert it into the map
			SecondaryAttributeUIData uiDataRecord = new SecondaryAttributeUIData(
				attrValue,
				new MutableDouble(player.getSecodaryAttributeValue(attr)),
				new MutableDouble(player.getSecodaryAttributeValue(attr))
			);
			secondaryAttributeUIDataMap.put(attr, uiDataRecord);
		}
		
		/*****************************************************************************/
		/*                          APPLY AND EXIT BUTTONS                           */
		/*****************************************************************************/

		// Create a button that allows the player to exit the attribute panel and
		// return to the pause menu
		JButton exitButton = new JButton("Exit");
		exitButton.setFont(ATTR_PANEL_BUTTON_FONT);
		exitButton.addActionListener((a) -> showOptionMenu(screen));

		// Align the exit button to take up the lower left portion
		// of the screen
		panelLayout.putConstraint(SOUTH, exitButton, -5, SOUTH, attributePanel);
		panelLayout.putConstraint(WEST, exitButton, 5, WEST, attributePanel);
		panelLayout.putConstraint(EAST, exitButton, -5, HORIZONTAL_CENTER, attributePanel);

		// Create a button that allows the player to apply the changes they have
		// made to the point distribution of their attributes
		JButton applyButton = new JButton("Apply");
		applyButton.setFont(ATTR_PANEL_BUTTON_FONT);
		applyButton.addActionListener((a) -> applyAttributePanelChanges());

		// Align the exit button to take up the lower right portion
		// of the screen
		panelLayout.putConstraint(SOUTH, applyButton, -5, SOUTH, attributePanel);
		panelLayout.putConstraint(WEST, applyButton, 5, HORIZONTAL_CENTER, attributePanel);
		panelLayout.putConstraint(EAST, applyButton, -5, EAST, attributePanel);

		// Add the exit and apply buttons to the attribute panel
		attributePanel.add(exitButton);
		attributePanel.add(applyButton);
	}

	/*************************************************************************************/
	/*                                 OTHER UI  METHODS                                 */
	/*************************************************************************************/

	/**
	 * This method hides all the java swing components this
	 * overlay uses.
	 */
	private void hideAll() {
		// Hide all the ui components of the option menu
		attributeButton.setVisible(false);
		quitButton.setVisible(false);
		
		// Hide the attribute panel
		attributePanel.setVisible(false);
	}
	
	/**
	 * This method displays the option menu that is first shown
	 * to the user when they pause the game. From this menu the
	 * user can select different options.
	 * 
	 * @param screen the screen
	 */
	public void showOptionMenu(JComponent screen) {
		// Hide all java swing components this overlay previously
		// added to the screen
		hideAll();

		// Show the two options (attributes and quit)
		attributeButton.setVisible(true);
		quitButton.setVisible(true);

		// Update the screen's ui
		SwingUtilities.updateComponentTreeUI(screen);
	}

	/**
	 * This method displays the attribute panel to the user. The
	 * panel can be used by the player to view their attributes
	 * and level them up if they have enough experience points.
	 * 
	 * @param screen the screen
	 */
	public void showAttributePanel(JComponent screen) {
		// Hide all java swing components this overlay previously
		// added to the screen
		hideAll();

		// Show the attribute panel
		attributePanel.setVisible(true);

		// Update the number of attribute points the player has
		// available to spend and then update the ui state
		availableAttrPoints = player.getNumSpendingAttrPoints();
		updateAttributePanelUIState();

		// Update the world's ui
		SwingUtilities.updateComponentTreeUI(screen);
	}
	
	/**
	 * This method removes all the ui components previously added by
	 * this overlay from the screen.
	 * 
	 * @param screen the screen
	 * 
	 * @see IScreen#removeSwingComponents(JComponent)
	 */
	@Override
	public void removeSwingComponents(JComponent screen) {
		// Remove all the ui components of the option menu
		screen.remove(attributeButton);
		screen.remove(quitButton);
		
		// Remove the attribute panel
		screen.remove(attributePanel);
	}

	/*************************************************************************************/
	/*                                 PAINTING METHODS                                  */
	/*************************************************************************************/
	
	@Override
	public void paint(Graphics2D g2d) {
		// Draw the background
		g2d.setColor(PAUSE_BACKGROUND_COLOUR);
		g2d.fillRect(0, 0, Main.SCREEN_WIDTH, Main.SCREEN_HEIGHT);
	}

	/*************************************************************************************/
	/*                              UI FUNCTIONALITY METHODS                             */
	/*************************************************************************************/

	/**
	 * This method increments the given primary attribute by one
	 * but does not apply the change to the player; this method
	 * only changes the ui to reflect the player's new attribute
	 * values if they were to make the change.
	 * 
	 * @param attr the primary attribute to increment
	 */
	private void incrementAttribute(Attribute attr) {
		// Get the primary attribute's ui data, increment it value,
		// and update its value label
		PrimaryAttributeUIData attrUIData = primaryAttributeUIDataMap.get(attr);
		attrUIData.value.val++;
		attrUIData.valueLabel.setText(String.valueOf(attrUIData.value.val));

		// Update the primary attribute's value's label's text colour to show
		// that it has been changed
		attrUIData.valueLabel.setForeground(ATTR_UPDATED_VALUE_FONT_COLOUR);

		// Update the value of each secondary attribute to reflect their
		// new value if the player were to increment the primary attribute
		updateSecondaryAttributes();

		// Since the user incremented this attribute's value they can now decrement
		// to undo their change if they want to
		attrUIData.decrementButton.setEnabled(true);

		// Decrement the number of attribute points the player has available
		// to spend
		availableAttrPoints--;

		// Update the state of the ui to reflect the fact that the player
		// now has one less attribute point available to spend
		updateAttributePanelUIState();
	}

	/**
	 * This method decrements the given primary attribute by one
	 * but does not apply the change to the player; this method
	 * only changes the ui to reflect the player's new attribute
	 * values if they were to make the change.
	 * 
	 * @param attr the primary attribute to increment
	 */
	private void decrementAttribute(Attribute attr) {
		// Get the primary attribute's ui data, decrement it value,
		// and update its value label
		PrimaryAttributeUIData attrUIData = primaryAttributeUIDataMap.get(attr);
		attrUIData.value.val--;
		attrUIData.valueLabel.setText(String.valueOf(attrUIData.value.val));

		// If the attribute is now back at its current value for the player then disable
		// the decrement button and return the value label's text colour to black
		if (attrUIData.value.val == attrUIData.playerValue.val) {
			attrUIData.decrementButton.setEnabled(false);
			attrUIData.valueLabel.setForeground(Color.BLACK);
		}

		// Update the value of each secondary attribute to reflect their
		// new value if the player were to increment the primary attribute
		updateSecondaryAttributes();

		// Increment the number of attribute points the player has available
		// to spend
		availableAttrPoints++;

		// Update the state of the ui to reflect the fact that the player
		// now has one more attribute point available to spend
		updateAttributePanelUIState();
	}

	/**
	 * This method updates the value of each secondary attribute. If any
	 * need to be changed to reflect a change the player has made to their
	 * primary attributes then the text colour of the secondary arrtiute's
	 * value label will be updated to inform the user.
	 */
	private void updateSecondaryAttributes() {
		// Create a dummy player whose sole purpose is returning
		// what the scaled value of a player's secondary attributes
		// would be with any changes the player has made
		Player dummyPlayer = new Player(
			primaryAttributeUIDataMap.get(Attribute.INTELLIGENCE).value.val,
			primaryAttributeUIDataMap.get(Attribute.HEALTH).value.val,
			primaryAttributeUIDataMap.get(Attribute.SPECIAL).value.val,
			primaryAttributeUIDataMap.get(Attribute.ABILITIES).value.val
		);

		// Loop through each secondary attribute, updating their values
		for (Attribute attr : Attribute.SECONDARY_ATTRIBUTES) {
			// Get the attribute's new value
			double newValue = dummyPlayer.getSecodaryAttributeValue(attr);

			// Get the attribute's ui data
			SecondaryAttributeUIData attrUIData = secondaryAttributeUIDataMap.get(attr);

			// If the attribute's new value is not equal to its old
			// one then update it value label
			if (newValue != attrUIData.value.val) {
				// Update the value and update the attribute's value's label's text
				// colour to show that it was updated
				attrUIData.value.val = newValue;
				attrUIData.valueLabel.setText(String.valueOf(attrUIData.value.val));

				// If the attribute's value is not equal to its current value for the player
				// then change its text colour, otherwise ensure its text colour is normal
				if (attrUIData.value.val != attrUIData.playerValue.val) {
					attrUIData.valueLabel.setForeground(ATTR_UPDATED_VALUE_FONT_COLOUR);
				} else {
					attrUIData.valueLabel.setForeground(Color.BLACK);
				}
			}
		}
	}

	/**
	 * The method updates the state of the attribute panel's ui to
	 * reflect how many attribute points the player has available
	 * to spend.
	 */
	private void updateAttributePanelUIState() {
		// Update the label displaying how many attribute points
		// the player has to spend
		availableAttrPointsLabel.setText(String.format(AVAILABLE_ATTR_POINTS_FORMAT_STRING, availableAttrPoints));

		// Loop through each primary attribute to update the state
		// of their buttons
		for (Attribute attr : Attribute.PRIMARY_ATTRIBUTES) {
			// Get the attribute's ui data
			PrimaryAttributeUIData attrUIData = primaryAttributeUIDataMap.get(attr);

			// Only enable the increment button if the player has attribute
			// points to spend
			attrUIData.incrementButton.setEnabled(availableAttrPoints > 0);
		}
	}

	/**
	 * This method applys any changes that the player has made to
	 * their primary attributes.
	 */
	private void applyAttributePanelChanges() {
		// Loop through each primary attribute
		for (Attribute attr : Attribute.PRIMARY_ATTRIBUTES) {
			// Retrieve the attribute's ui data
			PrimaryAttributeUIData attrUIData = primaryAttributeUIDataMap.get(attr);

			// If the player modififed this attribute's value then update
			// its value for the player and revert its ui to its normal state
			if (attrUIData.value.val != attrUIData.playerValue.val) {
				// Update the attribute's value for the player
				player.setPrimaryAttributeValue(attr, attrUIData.value.val);
				attrUIData.playerValue.val = attrUIData.value.val;

				// Update the ui
				attrUIData.valueLabel.setForeground(Color.BLACK);
				attrUIData.decrementButton.setEnabled(false);
			}
		}

		// Loop through each secondary attribute
		for (Attribute attr : Attribute.SECONDARY_ATTRIBUTES) {
			// For each secondary attribute update its ui data's state
			// and set its text colour to black
			SecondaryAttributeUIData attrUIData = secondaryAttributeUIDataMap.get(attr);
			attrUIData.playerValue.val = attrUIData.value.val;
			attrUIData.valueLabel.setForeground(Color.BLACK);
		}
	}
}
