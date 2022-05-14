package game.util;

/**
 * This class is a wrapper around a mutable double. The whole
 * purpose of this class is to circumvent that fact that java
 * records' variables are all final.
 */
public final class MutableDouble {
    /**
     * The double.
     */
    public double val;

    /**
     * This constructs a MutableDouble with an initial value.
     * 
     * @param initialValue the initial value of the MutableDouble
     */
    public MutableDouble(double initialValue) {
        val = initialValue;
    }
}
