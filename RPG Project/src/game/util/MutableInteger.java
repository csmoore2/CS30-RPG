package game.util;

/**
 * This class is a wrapper around a mutable integer. The whole
 * purpose of this class is to circumvent that fact that java
 * records' variables are all final.
 */
public final class MutableInteger {
    /**
     * The integer.
     */
    public int val;

    /**
     * This constructs a MutableInteger with an initial value.
     * 
     * @param initialValue the initial value of the MutableInteger
     */
    public MutableInteger(int initialValue) {
        val = initialValue;
    }
}
