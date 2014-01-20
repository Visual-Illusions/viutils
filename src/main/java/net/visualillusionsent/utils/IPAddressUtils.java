/*
 * This file is part of ${name}.
 *
 * Copyright © 2012-2014 Visual Illusions Entertainment
 *
 * VIUtils is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.
 * If not, see http://www.gnu.org/licenses/lgpl.html.
 */
package net.visualillusionsent.utils;

import java.util.regex.Pattern;

import static net.visualillusionsent.utils.Verify.notEmpty;
import static net.visualillusionsent.utils.Verify.notNull;

/**
 * Provides static methods to help with IP Address manipulations and checking
 *
 * @author Jason (darkdiplomat)
 * @version 1.0
 * @since 1.0.0
 */
public final class IPAddressUtils {

    private static final float classVersion = 1.0F;
    /** Internet Protocol Version 4 Syntax checking pattern */
    private static final Pattern IPv4_REGEX = Pattern.compile("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}");
    /** Internet Protocol Version 6 Syntax checking pattern */
    private static final Pattern IPv6_REGEX = Pattern.compile("\\A(?:[0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}\\z");

    /** This class should never be externally constructed */
    private IPAddressUtils() {
    }

    /**
     * Checks a IP Address string for proper IPv4 syntax
     *
     * @param ip
     *         the IP Address to check
     *
     * @return {@code true} if IPv4, {@code false} otherwise
     *
     * @throws UtilityException
     *         <br>
     *         if ip is null or empty
     */
    public static final boolean isIPv4Address(String ip) throws UtilityException {
        notNull(ip, "String ip");
        notEmpty(ip, "String ip");

        return IPv4_REGEX.matcher(ip).matches();
    }

    /**
     * Checks a IP Address string for proper IPv6 syntax
     *
     * @param ip
     *         the IP Address to check
     *
     * @return {@code true} if IPv6, {@code false} otherwise
     *
     * @throws UtilityException
     *         <br>
     *         if ip is null or empty
     */
    public static final boolean isIPv6Address(String ip) throws UtilityException {
        notNull(ip, "String ip");
        notEmpty(ip, "String ip");

        return IPv6_REGEX.matcher(ip).matches();
    }

    /**
     * A convenient method that accepts an IP address represented as a long and
     * returns an byte array of size 4 representing the same IP address.
     *
     * @param address
     *         the long value representing the IP address.
     *
     * @return An {@code byte[]} of size 4.
     */
    public static final byte[] longToIPv4(long address) {
        byte[] ip = new byte[4];
        for (int index = 0; index < 4; index++) {
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
     *         the byte[] of size 4 representing the IP address.
     *
     * @return a long representation of the IP address.
     *
     * @throws UtilityException
     *         <br>
     *         if the byte array is not of length 4
     */
    public static final long ipv4ToLong(byte[] address) throws UtilityException {
        if (address.length != 4) { //TODO: Verify Method?
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
     *         the byte array IP Address
     *
     * @return string representation
     *
     * @throws UtilityException
     *         <br>
     *         if the byte array is not of length 4
     */
    public static final String ipv4BytestoString(byte[] address) throws UtilityException {
        if (address.length != 4) { //TODO: Verify Method?
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

    /**
     * Gets this class's version number
     *
     * @return the class version
     */
    public static final float getClassVersion() {
        return classVersion;
    }
}
