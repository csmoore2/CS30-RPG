package game;

import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import game.ui.screens.AreaScreen;

/**
 * This enum stores the different zones that the player can move
 * through. Each zone has a unique name that is displayed to the
 * player when they move into the zone.
 */
public enum Zone {
    // These are the different zones that the player can be in
    GREEN_HUB("Green Zone / Hub", "res/greenzonebackground.png"),
    FIRE("Fire Zone", "res/firezonebackground.png"),
    GEM("Gem Zone", "res/gemzonebackground.png"),
    ICE("Ice Zone", "res/icezonebackground.png"),
    ROCK("Rock Zone", "res/rockzonebackground.png");

    /**
     * This is the zone's name.
     */
    private final String name;

    /**
     * This is the zone's background image.
     */
    private Image backgroundImage;

    /**
     * This constructs a new Zone with the given name.
     * 
     * @param nameIn                the zone's name
     * @param backgroundImagePathIn the zone's background image's path
     */
    private Zone(String nameIn, String backgroundImagePathIn) {
        name = nameIn;

        // Try to load the zone's background image
        try {
            backgroundImage = ImageIO.read(new File(backgroundImagePathIn));
        } catch (IOException e) {
            throw new RuntimeException("Unable to load a zone's background image!", e);
        }

        // Scale the zone's background image t the size of an area screen
        backgroundImage = backgroundImage.getScaledInstance(
            AreaScreen.AREA_SCREEN_SIZE, AreaScreen.AREA_SCREEN_SIZE, Image.SCALE_SMOOTH);
    }

    /**
     * This method returns the zone's name.
     * 
     * @return the name of the zone
     */
    public String getName() {
        return name;
    }

    /**
     * This method returns the zone's background image.
     * 
     * @return the zone's background image
     */
    public Image getBackgroundImage() {
        return backgroundImage;
    }
}
