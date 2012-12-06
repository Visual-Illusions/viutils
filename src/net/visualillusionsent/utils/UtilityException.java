package net.visualillusionsent.utils;

/**
 * Utility Exception
 * <p>
 * Thrown when a used utility has improper arguments given to it or when something goes wrong<br>
 * use the {@link #getMessage()} method to retrieve the reason why the utility method failed
 * <p>
 * This File is part of the VIUtils<br>
 * (c) 2012 Visual Illusions Entertainment <a href="http://visualillusionsent.net">http://visualillusionsent.net</a>
 * 
 * @since VIUtils 1.0
 * @version 1.0
 * @author Jason (darkdiplomat)
 */
public class UtilityException extends Exception {

    /**
     * Serial Version
     */
    private static final long serialVersionUID = 042216122012L;

    /**
     * Class Constructor
     * <p>
     * Should not be constructed outside of VIUtils
     * 
     * @param message
     *            the message of why the exception is being thrown
     */
    UtilityException(String msg) {
        super(msg);
    }
}
