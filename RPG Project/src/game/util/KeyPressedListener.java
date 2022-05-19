package game.util;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * The purpose of this interface is to provide a way for classes
 * to register listeners for key pressed events without having to
 * define the other two methods of the KeyListener interface (keyTyped
 * and keyReleased). This is useful since it allows for functional
 * programming with listeners for key pressed events.
 */
@FunctionalInterface
public interface KeyPressedListener extends KeyListener {
	@Override
	default void keyTyped(KeyEvent e) {}
	
	@Override
	void keyPressed(KeyEvent e);
	
	@Override
	default void keyReleased(KeyEvent e) {}
}
