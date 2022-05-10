package game.ui;

import javax.swing.JProgressBar;

/**
 * This class is an extension of JProgressBar that is used to display quantities.
 * The only difference between this and JProgressBar is that this will display
 * the current amount and total amount in its string rather than a percentage.
 */
@SuppressWarnings("serial")
public class QuantityBar extends JProgressBar {
	/**
	 * This is the format for the string that will be displayed on the QuantityBar.
	 */
	public static final String STRING_FORMAT = "%d / %d";
	
	/**
	 * Creates a QuantityBar with the given parameters
	 * 
	 * @param min the minimum value for the QuantityBar
	 * @param max the maximum value for the QuantityBar
	 * 
	 * @see JProgressBar#JProgressBar(int, int)
	 */
	public QuantityBar(int min, int max) {
		super(min, max);
	}
    
	
	@Override
	public String getString() {
		return String.format(STRING_FORMAT, getValue(), getMaximum());
	}
}
