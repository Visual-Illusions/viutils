package net.visualillusionsent.utils;

/**
 * Update Exception
 * <p>
 * Thrown from {@link Updater} when an exception occurs during an update<br>
 * use the {@link #getMessage()} method to retrieve the reason why the update failed
 * <p>
 * This File is part of the VIUtils<br>
 * (c) 2012 Visual Illusions Entertainment <a href="http://visualillusionsent.net">http://visualillusionsent.net</a>
 * 
 * @since VIUtils 1.0
 * @version 1.0
 * @author Jason (darkdiplomat)
 */
public class UpdateException extends Exception {
    /**
     * Serial Version
     */
    private static final long serialVersionUID = 121212062012L;

    /**
     * Class Constructor
     * <p>
     * Should not be constructed outside of VIUtils
     * 
     * @param message
     *            the message of why the exception is being thrown
     */
    UpdateException(String message) {
        super(message);
    }

}
