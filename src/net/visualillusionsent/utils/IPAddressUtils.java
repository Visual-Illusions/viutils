package net.visualillusionsent.utils;

/**
 * Internet Protocol Address Utilities
 * <p>
 * Provides static methods to help with IP Address manipulations and checking
 * <p>
 * This File is part of the VIUtils<br>
 * (c) 2012 Visual Illusions Entertainment <a href="http://visualillusionsent.net">http://visualillusionsent.net</a>
 * 
 * @since VIUtils 1.0
 * @version 1.0
 * @author Jason (darkdiplomat)
 */
public final class IPAddressUtils {
    /**
     * Internet Protocal Version 4 Syntax checking regex
     */
    private static final String IPv4_REGEX = "\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}";

    /**
     * Internet Protocal Version 6 Syntax checking regex
     */
    private static final String IPv6_REGEX = "\\A(?:[0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}\\z";

    /**
     * This class should never be constructed
     */
    private IPAddressUtils() {}

    /**
     * Checks a IP Address string for proper IPv4 syntax
     * 
     * @param ip
     *            the IP Address to check
     * @return {@code true} if IPv4, {@code false} otherwise
     */
    public static final boolean isIPv4Address(String ip) {
        return ip.matches(IPv4_REGEX);
    }

    /**
     * Checks a IP Address string for proper IPv6 syntax
     * 
     * @param ip
     *            the IP Address to check
     * @return {@code true} if IPv6, {@code false} otherwise
     */
    public static final boolean isIPv6Address(String ip) {
        return ip.matches(IPv6_REGEX);
    }

    /**
     * A convenient method that accepts an IP address represented as a long and
     * returns an byte array of size 4 representing the same IP address.
     * 
     * @param address
     *            the long value representing the IP address.
     * @return An {@code byte[]} of size 4.
     */
    public static byte[] longToIPv4(long address) {
        byte[] ip = new byte[4];
        for (int index = 3; index >= 0; index--) {
            ip[index] = (byte) (address % 256);
            address = address / 256;
        }
        return ip;
    }

    /**
     * A convenient method that accepts an IP address represented by a byte[] of
     * size 4 and returns this as a long representation of the same IP address.
     * 
     * @param address
     *            the byte[] of size 4 representing the IP address.
     * @return a long representation of the IP address.
     * @throws UtilityException
     * <br>
     *             if the byte array is not of length 4
     */
    public static long ipv4ToLong(byte[] address) throws UtilityException {
        if (address.length != 4) {
            throw new UtilityException("byte array must be of length 4");
        }
        long ipNum = 0;
        long multiplier = 1;
        for (byte bytes : address) {
            int byteVal = (bytes + 256) % 256;
            ipNum += byteVal * multiplier;
            multiplier *= 256;
        }
        return ipNum;
    }

    /**
     * Converts an IP Address byte array into a string representation
     * 
     * @param address
     *            the byte array IP Address
     * @return string representation
     * @throws UtilityException
     * <br>
     *             if the byte array is not of length 4
     */
    public static String ipv4BytestoString(byte[] address) throws UtilityException {
        if (address.length != 4) {
            throw new UtilityException("byte array must be of length 4");
        }
        StringBuilder build = new StringBuilder();
        for (byte bytes : address) {
            build.append(bytes);
            build.append(".");
        }
        String temp = build.toString();
        return temp.substring(0, temp.lastIndexOf('.'));
    }
}
