/*
 * Copyright 2012 Visual Illusions Entertainment.
 *  
 * This file is part of VIUtils.
 *
 * VIUtils is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * VIUtils is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with VIUtils.
 * If not, see http://www.gnu.org/licenses/lgpl.html
 */
package net.visualillusionsent.utils;

/**
 * String Utilities
 * <p>
 * Provides static methods to help with {@link String} manipulations
 * <p>
 * This File is part of the VIUtils<br>
 * &copy; 2012 <a href="http://visualillusionsent.net">Visual Illusions Entertainment</a>
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
        if (args == null) {
            throw new UtilityException("arg.null", "String[] args");
        }
        else if (args.length == 0) {
            throw new UtilityException("arg.empty", "String[] args");
        }
        else if (startIndex >= args.length) {
            throw new UtilityException("start.gte");
        }
        else if (spacer == null) {
            throw new UtilityException("arg.null", "String spacer");
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
        if (args == null) {
            throw new UtilityException("arg.null", "String[] args");
        }
        else if (args.length == 0) {
            throw new UtilityException("arg.empty", "String[] args");
        }
        else if (startIndex >= args.length) {
            throw new UtilityException("start.gte");
        }
        else if (startIndex >= endIndex) {
            throw new UtilityException("startgte.end");
        }
        else if (endIndex >= args.length) {
            throw new UtilityException("end.gte");
        }
        else if (spacer == null) {
            throw new UtilityException("arg.null", "String spacer");
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
     * @throws UtilityException
     */
    public static String padRight(String toPad, int padAmount) throws UtilityException {
        if (toPad == null) {
            throw new UtilityException("arg.null", "String toPad");
        }
        else if (toPad.isEmpty()) {
            throw new UtilityException("arg.empty", "String toPad");
        }
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
     * @throws UtilityException
     */
    public static String padLeft(String toPad, int padAmount) throws UtilityException {
        if (toPad == null) {
            throw new UtilityException("arg.null", "String toPad");
        }
        else if (toPad.isEmpty()) {
            throw new UtilityException("arg.empty", "String toPad");
        }
        return String.format("%1$".concat(String.valueOf(padAmount)).concat("s"), toPad);
    }

    /**
     * Trims whitespace off each element in a {@link String} array
     * 
     * @param toTrim
     *            the {@link String} array to clean
     * @return whitespace cleaned {@link String} array
     * @throws UtilityException
     */
    public static String[] trimElements(String[] toTrim) throws UtilityException {
        if (toTrim == null) {
            throw new UtilityException("arg.null", "String[] toTrim");
        }
        else if (toTrim.length == 0) {
            throw new UtilityException("arg.empty", "String[] toTrim");
        }
        String[] toRet = toTrim.clone();
        for (int index = 0; index < toRet.length; index++) {
            toRet[index] = toTrim[index].trim();
        }
        return toRet;
    }

    /**
     * Splits a {@link String} at commas ',' and converts the elements to bytes
     * 
     * @param str
     *            the string to be converted
     * @return byte array of the string
     * @throws UtilityException
     */
    public static byte[] stringToByteArray(String str) throws UtilityException {
        return stringToByteArray(str, ",");
    }

    /**
     * Splits a {@link String} at specified character(s) and converts the elements to bytes
     * 
     * @param str
     *            the string to be converted
     * @param splitBy
     *            the character(s) to split at
     * @return byte array of the string
     * @throws UtilityException
     *             if str is null or empty<br>
     *             or if splitBy is null or empty<br>
     *             or if an element is not a number
     */
    public static byte[] stringToByteArray(String str, String splitBy) throws UtilityException {
        if (str == null) {
            throw new UtilityException("arg.null", "String str");
        }
        else if (str.isEmpty()) {
            throw new UtilityException("arg.empty", "String str");
        }
        else if (splitBy == null) {
            throw new UtilityException("arg.null", "String splitBy");
        }
        else if (splitBy.isEmpty()) {
            throw new UtilityException("splitby.empty", "String splitBy");
        }
        String[] strsp = str.split(splitBy);
        byte[] toRet = new byte[strsp.length];
        for (int index = 0; index < strsp.length; index++) {
            try {

                toRet[index] = Byte.parseByte(strsp[index].trim());
            }
            catch (NumberFormatException nfe) {
                throw new UtilityException("str.nan", strsp[index]);
            }
        }
        return toRet;
    }

    /**
     * Joins a byte array into a single {@link String} seperated by a comma
     * 
     * @param bytes
     *            the byte array to be joined
     * @return the joined byte array as a String
     * @throws UtilityException
     *             if bytes is null or empty
     */
    public static String byteArrayToString(byte[] bytes) throws UtilityException {
        return byteArrayToString(bytes, ",");
    }

    /**
     * Joins a byte array into a single {@link String} seperated by specified character(s)
     * 
     * @param bytes
     *            the byte array to be joined
     * @param spacer
     *            the character(s) to space the bytes apart with
     * @return the joined byte array as a String
     * @throws UtilityException
     *             if bytes is null or empty<br>
     *             or if spacer is null
     */
    public static String byteArrayToString(byte[] bytes, String spacer) throws UtilityException {
        if (bytes == null) {
            throw new UtilityException("arg.null", "byte[] bytes");
        }
        else if (bytes.length == 0) {
            throw new UtilityException("arg.empty", "byte[] bytes");
        }
        else if (spacer == null) {
            throw new UtilityException("arg.null", "String spacer");
        }
        StringBuilder build = new StringBuilder();
        for (byte theByte : bytes) {
            build.append(theByte);
            build.append(",");
        }
        String preRet = build.toString().trim();
        return preRet.substring(0, preRet.lastIndexOf(',')); //Remove last spacer
    }

    /**
     * Splits a {@link String} at commas ',' and converts the elements to short
     * 
     * @param str
     *            the string to be converted
     * @return short array of the string
     * @throws UtilityException
     *             if str is null or empty<br>
     *             or if an element is not a number
     */
    public static short[] stringToShortArray(String str) throws UtilityException {
        return stringToShortArray(str, ",");
    }

    /**
     * Splits a {@link String} at specified character(s) and converts the elements to short
     * 
     * @param str
     *            the string to be converted
     * @param splitBy
     *            the character(s) to split at
     * @return short array of the string
     * @throws UtilityException
     *             if str is null or empty<br>
     *             or if splitBy is null or empty<br>
     *             or if an element is not a number
     */
    public static short[] stringToShortArray(String str, String splitBy) throws UtilityException {
        if (str == null) {
            throw new UtilityException("arg.null", "String str");
        }
        else if (str.isEmpty()) {
            throw new UtilityException("arg.empty", "String str");
        }
        else if (splitBy == null) {
            throw new UtilityException("arg.null", "String splitBy");
        }
        else if (splitBy.isEmpty()) {
            throw new UtilityException("splitby.empty", "String splitBy");
        }
        String[] strsp = str.split(splitBy);
        short[] toRet = new short[strsp.length];
        for (int index = 0; index < strsp.length; index++) {
            try {

                toRet[index] = Short.parseShort(strsp[index].trim());
            }
            catch (NumberFormatException nfe) {
                throw new UtilityException("str.nan", strsp[index]);
            }
        }
        return toRet;
    }

    /**
     * Joins a shot array into a single {@link String} seperated by a comma
     * 
     * @param shorts
     *            the short array to be joined
     * @return the joined short array as a String
     * @throws UtilityException
     */
    public static String shortArrayToString(short[] shorts) throws UtilityException {
        return shortArrayToString(shorts, ",");
    }

    /**
     * Joins a short array into a single {@link String} seperated by specified character(s)
     * 
     * @param shorts
     *            the short array to be joined
     * @param spacer
     *            the character(s) to space the shorts apart with
     * @return the joined short array as a String
     * @throws UtilityException
     *             if shorts is null or empty<br>
     *             or if spacer is null
     */
    public static String shortArrayToString(short[] shorts, String spacer) throws UtilityException {
        if (shorts == null) {
            throw new UtilityException("arg.null", "short[] shorts");
        }
        else if (shorts.length == 0) {
            throw new UtilityException("arg.empty", "short[] shorts");
        }
        if (spacer == null) {
            throw new UtilityException("arg.null", "String spacer");
        }
        StringBuilder build = new StringBuilder();
        for (short theShort : shorts) {
            build.append(theShort);
            build.append(spacer);
        }
        String preRet = build.toString().trim();
        return preRet.substring(0, preRet.lastIndexOf("spacer")); //Remove last spacer
    }

    /**
     * Splits a {@link String} at commas ',' and converts the elements to Integers
     * 
     * @param str
     *            the string to be converted
     * @return int array of the string
     * @throws UtilityException
     */
    public static int[] stringToIntArray(String str) throws UtilityException {
        return stringToIntArray(str, ",");
    }

    /**
     * Splits a {@link String} at specified character(s) and converts the elements to int
     * 
     * @param str
     *            the string to be converted
     * @param splitBy
     *            the character(s) to split at
     * @return int array of the string
     * @throws UtilityException
     *             if str is null or empty<br>
     *             or if splitBy is null or empty<br>
     *             or if an element is not a number
     */
    public static int[] stringToIntArray(String str, String splitBy) throws UtilityException {
        if (str == null) {
            throw new UtilityException("arg.null", "String str");
        }
        else if (str.isEmpty()) {
            throw new UtilityException("arg.empty", "String str");
        }
        else if (splitBy == null) {
            throw new UtilityException("arg.null", "String splitBy");
        }
        else if (splitBy.isEmpty()) {
            throw new UtilityException("arg.empty", "String splitBy");
        }
        String[] strsp = str.split(splitBy);
        int[] toRet = new int[strsp.length];
        for (int index = 0; index < strsp.length; index++) {
            try {

                toRet[index] = Integer.parseInt(strsp[index].trim());
            }
            catch (NumberFormatException nfe) {
                throw new UtilityException("str.nan", strsp[index]);
            }
        }
        return toRet;
    }

    /**
     * Joins a int array into a single {@link String} seperated by a comma
     * 
     * @param ints
     *            the int array to be joined
     * @return the joined int array as a String
     * @throws UtilityException
     *             if ints is null or empty
     */
    public static String intArrayToString(int[] ints) throws UtilityException {
        return intArrayToString(ints, ",");
    }

    /**
     * Joins a int array into a single {@link String} seperated by specified character(s)
     * 
     * @param ints
     *            the int array to be joined
     * @param spacer
     *            the character(s) to space the ints apart with
     * @return the joined int array as a String
     * @throws UtilityException
     *             if ints is null or empty<br>
     *             or if spacer is null
     */
    public static String intArrayToString(int[] ints, String spacer) throws UtilityException {
        if (ints == null) {
            throw new UtilityException("arg.null", "int[] ints");
        }
        else if (ints.length == 0) {
            throw new UtilityException("arg.empty", "int[] ints");
        }
        if (spacer == null) {
            throw new UtilityException("arg.null", "String spacer");
        }
        StringBuilder build = new StringBuilder();
        for (int theInt : ints) {
            build.append(theInt);
            build.append(spacer);
        }
        String preRet = build.toString().trim();
        return preRet.substring(0, preRet.lastIndexOf("spacer")); //Remove last spacer
    }

    /**
     * Splits a {@link String} at commas ',' and converts the elements to longs
     * 
     * @param str
     *            the string to be converted
     * @return long array of the string
     * @throws UtilityException
     */
    public static long[] stringToLongArray(String str) throws UtilityException {
        return stringToLongArray(str, ",");
    }

    /**
     * Splits a {@link String} at specified character(s) and converts the elements to long
     * 
     * @param str
     *            the string to be converted
     * @param splitBy
     *            the character(s) to split at
     * @return int array of the string
     * @throws UtilityException
     *             if str is null or empty<br>
     *             or if splitBy is null or empty<br>
     *             or if an element is not a number
     */
    public static long[] stringToLongArray(String str, String splitBy) throws UtilityException {
        if (str == null) {
            throw new UtilityException("arg.null", "String str");
        }
        else if (str.isEmpty()) {
            throw new UtilityException("arg.empty", "String str");
        }
        else if (splitBy == null) {
            throw new UtilityException("arg.null", "String splitBy");
        }
        else if (splitBy.isEmpty()) {
            throw new UtilityException("arg.empty", "String splitBy");
        }
        String[] strsp = str.split(splitBy);
        long[] toRet = new long[strsp.length];
        for (int index = 0; index < strsp.length; index++) {
            try {
                toRet[index] = Long.parseLong(strsp[index].trim());
            }
            catch (NumberFormatException nfe) {
                throw new UtilityException("str.nan", strsp[index]);
            }
        }
        return toRet;
    }

    /**
     * Joins a long array into a single {@link String} seperated by a comma
     * 
     * @param longs
     *            the long array to be joined
     * @return the joined long array as a String
     * @throws UtilityException
     *             if longs is null or empty<br>
     */
    public static String longArrayToString(long[] longs) throws UtilityException {
        return longArrayToString(longs, ",");
    }

    /**
     * Joins a long array into a single {@link String} seperated by specified character(s)
     * 
     * @param longs
     *            the long array to be joined
     * @param spacer
     *            the character(s) to space the longs apart with
     * @return the joined long array as a String
     * @throws UtilityException
     *             if longs is null or empty<br>
     *             or if spacer is null
     */
    public static String longArrayToString(long[] longs, String spacer) throws UtilityException {
        if (longs == null) {
            throw new UtilityException("arg.null", "long[] longs");
        }
        else if (longs.length == 0) {
            throw new UtilityException("arg.empty", "long[] longs");
        }
        if (spacer == null) {
            throw new UtilityException("arg.null", "String spacer");
        }
        StringBuilder build = new StringBuilder();
        for (long theLong : longs) {
            build.append(theLong);
            build.append(spacer);
        }
        String preRet = build.toString().trim();
        return preRet.substring(0, preRet.lastIndexOf("spacer")); //Remove last spacer
    }

    /**
     * Splits a {@link String} at commas ',' and converts the elements to floats
     * 
     * @param str
     *            the string to be converted
     * @return float array of the string
     * @throws UtilityException
     */
    public static float[] stringToFloatArray(String str) throws UtilityException {
        return stringToFloatArray(str, ",");
    }

    /**
     * Splits a {@link String} at specified character(s) and converts the elements to float
     * 
     * @param str
     *            the string to be converted
     * @param splitBy
     *            the character(s) to split at
     * @return float array of the string
     * @throws UtilityException
     *             if str is null or empty<br>
     *             or if splitBy is null or empty<br>
     *             or if an element is not a number
     */
    public static float[] stringToFloatArray(String str, String splitBy) throws UtilityException {
        if (str == null) {
            throw new UtilityException("arg.null", "String str");
        }
        else if (str.isEmpty()) {
            throw new UtilityException("arg.empty", "String str");
        }
        else if (splitBy == null) {
            throw new UtilityException("arg.null", "String splitBy");
        }
        else if (splitBy.isEmpty()) {
            throw new UtilityException("arg.empty", "String splitBy");
        }
        String[] strsp = str.split(splitBy);
        float[] toRet = new float[strsp.length];
        for (int index = 0; index < strsp.length; index++) {
            try {

                toRet[index] = Float.parseFloat(strsp[index].trim());
            }
            catch (NumberFormatException nfe) {
                throw new UtilityException("str.nan", strsp[index]);
            }
        }
        return toRet;
    }

    /**
     * Joins a float array into a single {@link String} seperated by a comma
     * 
     * @param floats
     *            the float array to be joined
     * @return the joined float array as a String
     * @throws UtilityException
     *             if floats is null or empty<br>
     */
    public static String floatArrayToString(float[] floats) throws UtilityException {
        return floatArrayToString(floats, ",");
    }

    /**
     * Joins a float array into a single {@link String} seperated by specified character(s)
     * 
     * @param floats
     *            the float array to be joined
     * @param spacer
     *            the character(s) to space the floats apart with
     * @return the joined float array as a String
     * @throws UtilityException
     *             if floats is null or empty<br>
     *             or if spacer is null
     */
    public static String floatArrayToString(float[] floats, String spacer) throws UtilityException {
        if (floats == null) {
            throw new UtilityException("arg.null", "float[] floats");
        }
        else if (floats.length == 0) {
            throw new UtilityException("arg.empty", "float[] floats");
        }
        if (spacer == null) {
            throw new UtilityException("arg.null", "String spacer");
        }
        StringBuilder build = new StringBuilder();
        for (float theFloat : floats) {
            build.append(theFloat);
            build.append(spacer);
        }
        String preRet = build.toString().trim();
        return preRet.substring(0, preRet.lastIndexOf("spacer")); //Remove last spacer
    }

    /**
     * Splits a {@link String} at commas ',' and converts the elements to double
     * 
     * @param str
     *            the string to be converted
     * @return double array of the string
     * @throws UtilityException
     *             if str is null or empty<br>
     *             or if an element is not a number
     */
    public static double[] stringToDoubleArray(String str) throws UtilityException {
        return stringToDoubleArray(str, ",");
    }

    /**
     * Splits a {@link String} at specified character(s) and converts the elements to double
     * 
     * @param str
     *            the string to be converted
     * @param splitBy
     *            the character(s) to split at
     * @return float array of the string
     * @throws UtilityException
     *             if str is null or empty<br>
     *             or if splitBy is null or empty<br>
     *             or if an element is not a number
     */
    public static double[] stringToDoubleArray(String str, String splitBy) throws UtilityException {
        if (str == null) {
            throw new UtilityException("arg.null", "String str");
        }
        else if (str.isEmpty()) {
            throw new UtilityException("arg.empty", "String str");
        }
        else if (splitBy == null) {
            throw new UtilityException("arg.null", "String splitBy");
        }
        else if (splitBy.isEmpty()) {
            throw new UtilityException("arg.empty", "String splitBy");
        }
        String[] strsp = str.split(splitBy);
        double[] toRet = new double[strsp.length];
        for (int index = 0; index < strsp.length; index++) {
            try {

                toRet[index] = Double.parseDouble(strsp[index].trim());
            }
            catch (NumberFormatException nfe) {
                throw new UtilityException("str.nan", strsp[index]);
            }
        }
        return toRet;
    }

    /**
     * Joins a double array into a single {@link String} seperated by a comma
     * 
     * @param doubles
     *            the doubles array to be joined
     * @return the joined double array as a String
     * @throws UtilityException
     *             if doubles is null or empty<br>
     *             or if spacer is null
     */
    public static String doubleArrayToString(double[] doubles) throws UtilityException {
        return doubleArrayToString(doubles, ",");
    }

    /**
     * Joins a double array into a single {@link String} seperated by specified character(s)
     * 
     * @param doubles
     *            the doule array to be joined
     * @param spacer
     *            the character(s) to space the doubles apart with
     * @return the joined double array as a String
     * @throws UtilityException
     *             if doubles is null or empty<br>
     *             or if spacer is null
     */
    public static String doubleArrayToString(double[] doubles, String spacer) throws UtilityException {
        if (doubles == null) {
            throw new UtilityException("arg.null", "double[] doubles");
        }
        else if (doubles.length == 0) {
            throw new UtilityException("arg.empty", "double[] doubles");
        }
        if (spacer == null) {
            throw new UtilityException("arg.null", "String spacer");
        }
        StringBuilder build = new StringBuilder();
        for (double theDouble : doubles) {
            build.append(theDouble);
            build.append(spacer);
        }
        String preRet = build.toString().trim();
        return preRet.substring(0, preRet.lastIndexOf("spacer")); //Remove last spacer
    }
}
