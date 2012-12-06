package net.visualillusionsent.utils;

/**
 * String Utilities
 * <p>
 * Provides static methods to help with {@link String} manipulations
 * <p>
 * This File is part of the VIUtils<br>
 * (c) 2012 Visual Illusions Entertainment <a href="http://visualillusionsent.net">http://visualillusionsent.net</a>
 * 
 * @since VIUtils 1.0
 * @version 1.0
 * @author Jason (darkdiplomat)
 */
public final class StringUtils {

    /**
     * This class should never be constructed
     */
    private StringUtils() {}

    /**
     * Joins a {@link String} array into a single String
     * 
     * @param args
     *            the {@link String} array to be combined
     * @param spacer
     *            the {@link String} to put between each element of the array
     * @param startIndex
     *            the index to start combining
     * @return the newly joined {@link String}
     * @throws UtilityException
     * <br>
     *             if startIndex is greater than or equal to args.length<br>
     *             or if spacer is equal to null
     */
    public static final String joinString(String[] args, String spacer, int startIndex) throws UtilityException {
        if (startIndex >= args.length) {
            throw new UtilityException("startIndex greater than or equal to args.length");
        }
        else if (spacer == null) {
            throw new UtilityException("spacer equals null");
        }

        StringBuilder sb = new StringBuilder();
        for (int index = startIndex; index < args.length; index++) {
            sb.append(args[index]);
            sb.append(spacer);
        }
        return sb.toString();
    }

    /**
     * Joins a {@link String} array into a single String
     * 
     * @param args
     *            the {@link String} array to be combined
     * @param spacer
     *            the {@link String} to put between each element of the array
     * @param startIndex
     *            the index to start combining
     * @param endIndex
     *            the index to end combining
     * @return the newly joined {@link String}
     * @throws UtilityException
     * <br>
     *             if startIndex is greater than or equal to args.length<br>
     *             or if startIndex is greater than or equal to endIndex<br>
     *             or if endIndex is greater than or equal to args.length<br>
     *             or if spacer is equal to null
     */
    public static final String joinString(String[] args, String spacer, int startIndex, int endIndex) throws UtilityException {
        if (startIndex >= args.length) {
            throw new UtilityException("startIndex greater than or equal to args.length");
        }
        else if (startIndex >= endIndex) {
            throw new UtilityException("startIndex greater than or equal to endIndex");
        }
        else if (endIndex >= args.length) {
            throw new UtilityException("endIndex greater than or equal to args.length");
        }
        else if (spacer == null) {
            throw new UtilityException("spacer equals null");
        }

        StringBuilder sb = new StringBuilder();
        for (int index = startIndex; index < endIndex; index++) {
            sb.append(args[index]);
            sb.append(spacer);
        }
        return sb.toString();
    }

    /**
     * Pads to the right of a {@link String} with spaces ' '<br>
     * NOTE: Padding also takes into account existing characters so a padAmount less that the give String has no effect
     * <p>
     * Code origin: <a href="http://www.rgagnon.com/javadetails/java-0448.html">http://www.rgagnon.com/javadetails/java-0448.html</a>
     * 
     * @param toPad
     *            the {@link String} to be padded
     * @param padAmount
     *            the amount to pad
     * @return the formated string
     */
    public static String padRight(String toPad, int padAmount) {
        return String.format("%1$-".concat(String.valueOf(padAmount)).concat("s"), toPad);
    }

    /**
     * Pads to the left of a {@link String} with spaces ' '<br>
     * NOTE: Padding also takes into account existing characters so a padAmount less that the give String has no effect
     * <p>
     * Code origin: <a href="http://www.rgagnon.com/javadetails/java-0448.html">http://www.rgagnon.com/javadetails/java-0448.html</a>
     * 
     * @param toPad
     *            the {@link String} to be padded
     * @param padAmount
     *            the amount to pad
     * @return the formated string
     */
    public static String padLeft(String toPad, int padAmount) {
        return String.format("%1$".concat(String.valueOf(padAmount)).concat("s"), toPad);
    }
}
