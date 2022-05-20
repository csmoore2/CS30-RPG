package game.ui.screens;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.EnumMap;
import java.util.function.Consumer;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;

import game.Main;
import game.entity.Attribute;
import game.entity.Player;

import static javax.swing.SpringLayout.*;

/**
 * This class extends JComponent rather than implementing IScreen,
 * due to the fact that it needs to use other JComponents, and is
 * used to display the character selection screen which is shown after
 * the start screen is shown. Along with the graphics, this screen will
 * handle the logic for character selection.
 */
@SuppressWarnings("serial")
public class CharacterCreationScreen extends JComponent {
	/**
	 * This is the text of the label over the class panel.
	 */
	public static final String CLASS_PANEL_LABEL_TEXT = "Classes";

	/**
	 * This is the text of the label over the attribute panel.
	 */
	public static final String ATTRIBUTE_PANEL_LABEL_TEXT = "Attributes";

	/**
	 * This is the format string that will be used to display each secondary attribute's
	 * name and value in the attribute panel.
	 */
	public static final String ATTRIBUTE_LABEL_FORMAT = "<html><center><b>%s:</b><br/>%s</center></html>";

	/**
	 * This is the text that is initially displayed in the player's name field (the default name).
	 */
	public static final String NAME_TEXT_FIELD_INITIAL_TEXT = "Untitled Character";
	
	/**
	 * This is the text for the start button.
	 */
	public static final String START_BUTTON_TEXT = "Start";

	/**
	 * This is the default horizontal and vertical padding between elements.
	 */
	public static final int DEFAULT_PADDING = 10;

	/**
	 * This is the horizontal padding that will be used for the text field for
	 * the player's name.
	 */
	public static final int NAME_TEXT_FIELD_HORIZONTAL_PADDING = 100;

	/**
	 * This is the default width of a panel.
	 */
	public static final int DEFAULT_PANEL_WIDTH = 300;

	/**
	 * This is the preferred size of the class panel.
	 */
	public static final Dimension CLASS_PANEL_DIMENSIONS = new Dimension(DEFAULT_PANEL_WIDTH, 300);

	/**
	 * This is the preferred size of the attribute panel.
	 */
	public static final Dimension ATTR_PANEL_DIMENSIONS = new Dimension(DEFAULT_PANEL_WIDTH, 315);

	/**
	 * This is the preferred size of the start button.
	 */
	public static final Dimension START_BUTTON_SIZE = new Dimension(200, 50);

	/**
	 * This is thedefault border that will be used around panels and other components
	 * where appropriate.
	 */
	public static final Border DEFAULT_BORDER = new LineBorder(Color.BLACK, 4, true);

	/**
	 * This is the font that will be used for the buttons that represent each premade class.
	 */
	public static final Font CLASS_BUTTON_FONT = new Font("Class Button Font", Font.BOLD, 16);

	/**
	 * This is the font that will be used for the labels that show the name of each player's
	 * secondary attributes.
	 */
	public static final Font ATTRIBUTE_FONT = new Font("Attribute Font", Font.PLAIN, 18);

	/**
	 * This is the font that will be used for the start button.
	 */
	public static final Font START_BUTTON_FONT = new Font("Start Button Font", Font.BOLD, 24);

	/**
	 * This is the font that will be used for labels.
	 */
	public static final Font LABEL_FONT = new Font("Label Font", Font.BOLD, 24);

	/**
	 * This is the font that will be used for the player's name.
	 */
	public static final Font PLAYER_NAME_FONT = new Font("Player Name Font", Font.PLAIN, 40);

	/**
	 * This is the colour that will be used as the background for the panels.
	 */
	public static final Color PANEL_BACKGROUND_COLOUR = Color.LIGHT_GRAY;

	/**
	 * This is the colour that will be used as the background for text fields.
	 */
	public static final Color TEXT_FIELD_BACKGROUND_COLOUR = Color.WHITE;

	/**
	 * This is the premade player that is currently selected.
	 */
	private Player selectedPlayer = Player.PREMADE_PLAYERS[0];

	/**
	 * This map stores the JLabel associated with each secondary attribute so that
	 * they can be updated to reflect the currently selected player's attributes.
	 */
	private final EnumMap<Attribute, JLabel> secondaryAttributeLabelMap = new EnumMap<>(Attribute.class);

	/**
	 * This is the "label" that displays the currently selected character's image.
	 */
	private final JLabel characterImageView;
	
	/**
	 * This constructs a new CharacterCreationScreen by initializing all of the required
	 * user interface (UI) components and adding them to the screen.
	 * 
	 * @param startGameListenerIn this the function that will be called when the start
	 *                            game button is pressed
	 */
	public CharacterCreationScreen(Consumer<Player> startGameListenerIn) {
		// Use a SpringLayout to make the layout of this screen. This will allow us to specify
		// constraints to layout objects rather than using exact positions.
		SpringLayout layout = new SpringLayout();
		setLayout(layout);

		/*************************************************************************************/
		/*                                    CLASS PANEL                                    */
		/*************************************************************************************/

		// This is the label for the panel that will contain the different classes the player
		// can select from
		JLabel classLabel = new JLabel(CLASS_PANEL_LABEL_TEXT);
		classLabel.setFont(LABEL_FONT);
		classLabel.setHorizontalAlignment(JLabel.CENTER);
		
		// This is the panel that will contain the different classes the player can select from
		JPanel classPanel = new JPanel();
		classPanel.setPreferredSize(CLASS_PANEL_DIMENSIONS);
		classPanel.setBackground(PANEL_BACKGROUND_COLOUR);
		classPanel.setBorder(DEFAULT_BORDER);
		
		// The class panel will be a list of the different classes, so we will use a GridLayout
		// with one column and a number of rows equivalent to the number of available classes
		classPanel.setLayout(new GridLayout(Player.PREMADE_PLAYERS.length, 1));
		
		// Loop through each premade class so we can add them to the panel
		for (int i = 0; i < Player.PREMADE_PLAYERS.length; i++) {
			// Create a button with the class' name as the text so the user can select
			// between the classes
			JButton classButton = new JButton();
			classButton.setFont(CLASS_BUTTON_FONT);

			// Set the button's text to the name of the current class and ensure it is horizontally centered
			classButton.setText(Player.PREMADE_PLAYERS[i].getClassType());
			classButton.setHorizontalAlignment(JTextField.CENTER);

			// Set a listener so that when the button for the class is clicked, the class will be selected
			final int characterIndex = i;
			classButton.addActionListener((action) -> {
				selectCharacter(characterIndex);
			});

			// Add the button (the class) to the class panel
			classPanel.add(classButton);
		}
		
		// Align the label for the class panel to be 10px from the top of the screen and the same width
		// as 'classPanel' so the text is centered
		layout.putConstraint(NORTH, classLabel, DEFAULT_PADDING, NORTH, this);
		layout.putConstraint(WEST, classLabel, 0, WEST, classPanel);
		layout.putConstraint(EAST, classLabel, 0, EAST, classPanel);

		// Align 'classPanel' to be 5px from the bottom of its label and 10px from the left side of the screen
		layout.putConstraint(WEST, classPanel, DEFAULT_PADDING, WEST, this);
		layout.putConstraint(NORTH, classPanel, DEFAULT_PADDING/2, SOUTH, classLabel);

		// Add the attribute panel and its label to the screen
		add(classLabel);
		add(classPanel);

		/*************************************************************************************/
		/*                                  ATTRIBUTE PANEL                                  */
		/*************************************************************************************/

		// This is the label for the panel that will contain the attribute point layout the player will have if
		// they pick the currently selected class
		JLabel attrLabel = new JLabel(ATTRIBUTE_PANEL_LABEL_TEXT);
		attrLabel.setFont(LABEL_FONT);
		attrLabel.setHorizontalAlignment(JLabel.CENTER);
		
		// This is the panel that will contain the attribute point layout the player will have if
		// they pick the currently selected class
		JPanel attrPanel = new JPanel();
		attrPanel.setBackground(PANEL_BACKGROUND_COLOUR);
		attrPanel.setBorder(DEFAULT_BORDER);

		// Give the attribute panel a grid layout with one columns and a variable
		// number of rows
		attrPanel.setLayout(new GridLayout(0, 1));

		// Loop through the secondary attributes, adding them to the attribute panel
		for (Attribute attr : Attribute.SECONDARY_ATTRIBUTES) {
			// Create a JLabel to hold the attribute's name and value
			JLabel attributeLabel = new JLabel();
			attributeLabel.setFont(ATTRIBUTE_FONT);
			attributeLabel.setHorizontalAlignment(JLabel.CENTER);

			// Give the attribute's label a white background
			attributeLabel.setOpaque(true);
			attributeLabel.setBackground(Color.WHITE);

			// Have the JLabel show the attribute's name and value in a nicely formatted way using HTML
			attributeLabel.setText(String.format(
				ATTRIBUTE_LABEL_FORMAT,
				attr.getName(),
				Attribute.getDisplayString(attr, selectedPlayer.getSecondaryAttributeValue(attr))
			));

			// Add the attribute's label to the attribute panel
			attrPanel.add(attributeLabel);

			// Add the attribute's label to th secondary attribute label map
			secondaryAttributeLabelMap.put(attr, attributeLabel);
		}

		// Align the label for the attribute panel to be 10px from the top of the screen and the same width
		// as 'attrPanel' so the text is centered
		layout.putConstraint(NORTH, attrLabel, DEFAULT_PADDING, SOUTH, classPanel);
		layout.putConstraint(WEST, attrLabel, 0, WEST, attrPanel);
		layout.putConstraint(EAST, attrLabel, 0, EAST, attrPanel);
		
		// Align 'attrPanel' to be 5px from the bottom of its label, the same width as the class panel,
		// and 10px from the bottom of the screen
		layout.putConstraint(NORTH, attrPanel, DEFAULT_PADDING/2, SOUTH, attrLabel);
		layout.putConstraint(SOUTH, attrPanel, -DEFAULT_PADDING, SOUTH, this);
		layout.putConstraint(EAST, attrPanel, 0, EAST, classPanel);
		layout.putConstraint(WEST, attrPanel, 0, WEST, classPanel);

		// Add the attribute panel and its label to the screen
		add(attrLabel);
		add(attrPanel);

		/*************************************************************************************/
		/*                                    NAME FIELD                                     */
		/*************************************************************************************/
		
		// This is the text field into which the player will enter their chosen name
		JTextField nameTextField = new JTextField(NAME_TEXT_FIELD_INITIAL_TEXT);
		nameTextField.setBackground(TEXT_FIELD_BACKGROUND_COLOUR);
		nameTextField.setBorder(DEFAULT_BORDER);
		nameTextField.setFont(PLAYER_NAME_FONT);
		nameTextField.setHorizontalAlignment(JTextField.CENTER);
		
		// Align 'nameTextField' to be 10px from the top of the screen, and to be 100px from both
		// the right edge of the screen and from the right edge of 'classPanel'
		layout.putConstraint(WEST, nameTextField, NAME_TEXT_FIELD_HORIZONTAL_PADDING, EAST, classPanel);
		layout.putConstraint(EAST, nameTextField, -NAME_TEXT_FIELD_HORIZONTAL_PADDING, EAST, this);
		layout.putConstraint(NORTH, nameTextField, DEFAULT_PADDING, NORTH, this);
		
		// Add the name field to the screen
		add(nameTextField);

		/*************************************************************************************/
		/*                                   START BUTTON                                    */
		/*************************************************************************************/
		
		// This is the button the user will press to start the game with their chosen class and name
		JButton startButton = new JButton();
		startButton.setText(START_BUTTON_TEXT);
		startButton.setFont(START_BUTTON_FONT);
		startButton.setPreferredSize(START_BUTTON_SIZE);

		// Make the start button set the player's name, change the start button to say loading, and then
		// call the function given by 'startGameListenerIn' when it is clicked
		startButton.addActionListener((action) -> {
			// Ensure that the player has a name
			if (nameTextField.getText().strip().isEmpty()) {
				JOptionPane.showMessageDialog(this, "You must have a name!");
				return;
			}
			
			// Ensure that the player's name is not longer than 25 characters
			if (nameTextField.getText().strip().length() > 25) {
				JOptionPane.showMessageDialog(this, "Your name must not exceed 25 characters in length!");
				return;
			}
			
			// Set the player's name
			selectedPlayer.setName(nameTextField.getText().strip());

			// Change the start button to say loading and disable it
			startButton.setText("Loading...");
			startButton.setEnabled(false);

			// Call the start game listener
			SwingUtilities.invokeLater(() -> startGameListenerIn.accept(selectedPlayer));
		});
		
		// Align the start button to be 10px from the bottom of the screen and 10px from the
		// right edge of the screen
		layout.putConstraint(EAST, startButton, -DEFAULT_PADDING, EAST, this);
		layout.putConstraint(SOUTH, startButton, -DEFAULT_PADDING, SOUTH, this);
		
		// Add the start button to the screen
		add(startButton);

		/*************************************************************************************/
		/*                               CHARACTER IMAGE VIEW                                */
		/*************************************************************************************/

		// This is the "label" that will display the image of character for the player's chosen class
		characterImageView = new JLabel();
		characterImageView.setIcon(new ImageIcon(selectedPlayer.getImage()));
		characterImageView.setHorizontalAlignment(JLabel.CENTER);

		// Align the character's image to be 10px from the top of the start button, 10px from the bottom of
		// the name text field, 10px from the edge of the class panel, and 10px from the right edge of the screen
		layout.putConstraint(NORTH, characterImageView, DEFAULT_PADDING, SOUTH, nameTextField);
		layout.putConstraint(SOUTH, characterImageView, -DEFAULT_PADDING, NORTH, startButton);
		layout.putConstraint(WEST, characterImageView, DEFAULT_PADDING, EAST, classPanel);
		layout.putConstraint(EAST, characterImageView, -DEFAULT_PADDING, EAST, this);
		
		// Add the "label" displaying the selected character's image to the screen
		add(characterImageView);
	}

	/**
	 * This method selects the character specified by the given index
	 * into the PREMADE_PLAYERS array.
	 * 
	 * @param index the index of the character in the PREMADE_PLAYERS array
	 */
	private void selectCharacter(int index) {
		selectedPlayer = Player.PREMADE_PLAYERS[index];
		updateUi();
	}

	/**
	 * This method updates ui components that need updating when the screen's
	 * state changes.
	 */
	private void updateUi() {
		// Update the image of the currently selected character
		characterImageView.setIcon(new ImageIcon(selectedPlayer.getImage()));

		// Loop through each secondary attribute to update its label to reflect the
		// currently selected player's attributes
		for (Attribute attr : Attribute.SECONDARY_ATTRIBUTES) {
			// Get the attribute's label
			JLabel attributeLabel = secondaryAttributeLabelMap.get(attr);

			// Update the attribute's label's text
			attributeLabel.setText(String.format(
				ATTRIBUTE_LABEL_FORMAT,
				attr.getName(),
				Attribute.getDisplayString(attr, selectedPlayer.getSecondaryAttributeValue(attr))
			));
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
