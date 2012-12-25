/*
 * Copyright © 2012-2013 Visual Illusions Entertainment.
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
 * Provides static methods to help with {@link String} manipulations
 * 
 * @since 1.0
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
        return joinString(args, spacer, startIndex, (args.length - 1));
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
        String preRet = sb.toString().trim();
        return preRet.substring(0, preRet.lastIndexOf(spacer)); //Remove last spacer
    }

    /**
     * Pads to the right of a {@link String} with spaces ' '<br>
     * NOTE: Padding also takes into account existing characters so a padAmount less that the give String has no effect
     * 
     * @param toPad
     *            the {@link String} to be padded
     * @param padAmount
     *            the amount to pad
     * @return the padded string
     * @throws UtilityException
     */
    public static final String padRight(String toPad, int padAmount) throws UtilityException {
        return padCharRight(toPad, padAmount, ' ');
    }

    /**
     * Pads to the right of a {@link String} with specified character<br>
     * NOTE: Padding also takes into account existing characters so a padAmount less that the give String has no effect
     * 
     * @param toPad
     *            the {@link String} to be padded
     * @param padAmount
     *            the amount to pad
     * @param padChar
     *            character to pad the String with
     * @return the padded string
     * @throws UtilityException
     */
    public static final String padCharRight(String toPad, int padAmount, char padChar) throws UtilityException {
        if (toPad == null) {
            throw new UtilityException("arg.null", "String toPad");
        }
        else if (toPad.isEmpty()) {
            throw new UtilityException("arg.empty", "String toPad");
        }
        StringBuffer padded = new StringBuffer(toPad);
        while (padded.length() < padAmount) {
            padded.append(padChar);
        }
        return padded.toString();
    }

    /**
     * Pads to the left of a {@link String} with spaces ' '<br>
     * NOTE: Padding also takes into account existing characters so a padAmount less that the give String has no effect
     * 
     * @param toPad
     *            the {@link String} to be padded
     * @param padAmount
     *            the amount to pad
     * @return the padded string
     * @throws UtilityException
     */
    public static final String padLeft(String toPad, int padAmount) throws UtilityException {
        return padCharLeft(toPad, padAmount, ' ');
    }

    /**
     * Pads to the left of a {@link String} with specified character<br>
     * NOTE: Padding also takes into account existing characters so a padAmount less that the give String has no effect
     * 
     * @param toPad
     *            the {@link String} to be padded
     * @param padAmount
     *            the amount to pad
     * @param padChar
     *            character to pad the String with
     * @return the padded string
     * @throws UtilityException
     */
    public static final String padCharLeft(String toPad, int padAmount, char padChar) throws UtilityException {
        if (toPad == null) {
            throw new UtilityException("arg.null", "String toPad");
        }
        else if (toPad.isEmpty()) {
            throw new UtilityException("arg.empty", "String toPad");
        }
        StringBuffer padded = new StringBuffer(toPad);
        while (padded.length() < padAmount) {
            padded.insert(0, padChar);
        }
        return padded.toString();
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
        return stringArrayToByteArray(str.split(splitBy));
    }

    /**
     * Converts a String Array into a byte array
     * 
     * @param strarr
     *            the string array to convert
     * @return byte array
     * @throws UtilityException
     */
    public static byte[] stringArrayToByteArray(String[] strarr) throws UtilityException {
        if (strarr == null) {
            throw new UtilityException("arg.null", "String strarr");
        }
        else if (strarr.length < 1) {
            throw new UtilityException("arg.empty", "String strarr");
        }
        byte[] toRet = new byte[strarr.length];
        for (int index = 0; index < strarr.length; index++) {
            try {
                toRet[index] = Byte.parseByte(strarr[index].trim());
            }
            catch (NumberFormatException nfe) {
                throw new UtilityException("str.nan", strarr[index]);
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
        return joinString(byteArrayToStringArray(bytes), spacer, 0);
    }

    /**
     * Converts the elements of the byte array to Strings
     * 
     * @param bytes
     *            the byte array to convert
     * @return string array
     * @throws UtilityException
     */
    public static String[] byteArrayToStringArray(byte[] bytes) throws UtilityException {
        if (bytes == null) {
            throw new UtilityException("arg.null", "byte[] bytes");
        }
        else if (bytes.length == 0) {
            throw new UtilityException("arg.empty", "byte[] bytes");
        }
        String[] arr = new String[bytes.length];
        for (int index = 0; index < bytes.length; index++) {
            arr[index] = String.valueOf(bytes[index]);
        }
        return arr;
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
        return stringArrayToShortArray(str.split(splitBy));
    }

    /**
     * Converts a String Array into a short array
     * 
     * @param strarr
     *            the string array to convert
     * @return short array
     * @throws UtilityException
     */
    public static short[] stringArrayToShortArray(String[] strarr) throws UtilityException {
        if (strarr == null) {
            throw new UtilityException("arg.null", "String strarr");
        }
        else if (strarr.length < 1) {
            throw new UtilityException("arg.empty", "String strarr");
        }
        short[] toRet = new short[strarr.length];
        for (int index = 0; index < strarr.length; index++) {
            try {
                toRet[index] = Short.parseShort(strarr[index].trim());
            }
            catch (NumberFormatException nfe) {
                throw new UtilityException("str.nan", strarr[index]);
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
        return joinString(shortArrayToStringArray(shorts), spacer, 0);
    }

    /**
     * Converts the elements of the short array to Strings
     * 
     * @param shorts
     *            the short array to be converted
     * @return string array
     * @throws UtilityException
     */
    public static String[] shortArrayToStringArray(short[] shorts) throws UtilityException {
        if (shorts == null) {
            throw new UtilityException("arg.null", "short[] shorts");
        }
        else if (shorts.length == 0) {
            throw new UtilityException("arg.empty", "short[] shorts");
        }
        String[] arr = new String[shorts.length];
        for (int index = 0; index < shorts.length; index++) {
            arr[index] = String.valueOf(shorts[index]);
        }
        return arr;
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
        return stringArrayToIntArray(str.split(splitBy));
    }

    /**
     * Converts a String Array into a int array
     * 
     * @param strarr
     *            the string array to convert
     * @return int array
     * @throws UtilityException
     */
    public static int[] stringArrayToIntArray(String[] strarr) throws UtilityException {
        if (strarr == null) {
            throw new UtilityException("arg.null", "String strarr");
        }
        else if (strarr.length < 1) {
            throw new UtilityException("arg.empty", "String strarr");
        }
        int[] toRet = new int[strarr.length];
        for (int index = 0; index < strarr.length; index++) {
            try {
                toRet[index] = Short.parseShort(strarr[index].trim());
            }
            catch (NumberFormatException nfe) {
                throw new UtilityException("str.nan", strarr[index]);
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
        return joinString(intArrayToStringArray(ints), spacer, 0);
    }

    /**
     * Converts the elements of the int array to Strings
     * 
     * @param ints
     *            the int array to convert
     * @return string array
     * @throws UtilityException
     */
    public static String[] intArrayToStringArray(int[] ints) throws UtilityException {
        if (ints == null) {
            throw new UtilityException("arg.null", "int[] ints");
        }
        else if (ints.length == 0) {
            throw new UtilityException("arg.empty", "int[] ints");
        }
        String[] arr = new String[ints.length];
        for (int index = 0; index < ints.length; index++) {
            arr[index] = String.valueOf(ints[index]);
        }
        return arr;
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
        return stringArrayToLongArray(str.split(splitBy));
    }

    /**
     * Converts a String Array into a long array
     * 
     * @param strarr
     *            the string array to convert
     * @return long array
     * @throws UtilityException
     */
    public static long[] stringArrayToLongArray(String[] strarr) throws UtilityException {
        if (strarr == null) {
            throw new UtilityException("arg.null", "String strarr");
        }
        else if (strarr.length < 1) {
            throw new UtilityException("arg.empty", "String strarr");
        }
        long[] toRet = new long[strarr.length];
        for (int index = 0; index < strarr.length; index++) {
            try {
                toRet[index] = Short.parseShort(strarr[index].trim());
            }
            catch (NumberFormatException nfe) {
                throw new UtilityException("str.nan", strarr[index]);
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
        return joinString(longArrayToStringArray(longs), spacer, 0);
    }

    /**
     * Converts the elements of the long array to Strings
     * 
     * @param longs
     *            the long array to convert
     * @return string array
     * @throws UtilityException
     */
    public static String[] longArrayToStringArray(long[] longs) throws UtilityException {
        if (longs == null) {
            throw new UtilityException("arg.null", "long[] longs");
        }
        else if (longs.length == 0) {
            throw new UtilityException("arg.empty", "long[] longs");
        }
        String[] arr = new String[longs.length];
        for (int index = 0; index < longs.length; index++) {
            arr[index] = String.valueOf(longs[index]);
        }
        return arr;
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
        return stringArrayToFloatArray(str.split(splitBy));
    }

    /**
     * Converts a String Array into a float array
     * 
     * @param strarr
     *            the string array to convert
     * @return float array
     * @throws UtilityException
     */
    public static float[] stringArrayToFloatArray(String[] strarr) throws UtilityException {
        if (strarr == null) {
            throw new UtilityException("arg.null", "String strarr");
        }
        else if (strarr.length < 1) {
            throw new UtilityException("arg.empty", "String strarr");
        }
        float[] toRet = new float[strarr.length];
        for (int index = 0; index < strarr.length; index++) {
            try {
                toRet[index] = Short.parseShort(strarr[index].trim());
            }
            catch (NumberFormatException nfe) {
                throw new UtilityException("str.nan", strarr[index]);
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
        return joinString(floatArrayToStringArray(floats), spacer, 0);
    }

    /**
     * Converts the elements of the float array to Strings
     * 
     * @param floats
     *            the float array to convert
     * @return string array
     * @throws UtilityException
     */
    public static String[] floatArrayToStringArray(float[] floats) throws UtilityException {
        if (floats == null) {
            throw new UtilityException("arg.null", "float[] floats");
        }
        else if (floats.length == 0) {
            throw new UtilityException("arg.empty", "float[] floats");
        }
        String[] arr = new String[floats.length];
        for (int index = 0; index < floats.length; index++) {
            arr[index] = String.valueOf(floats[index]);
        }
        return arr;
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
        return stringArrayToDoubleArray(str.split(splitBy));
    }

    /**
     * Converts a String Array into a float array
     * 
     * @param strarr
     *            the string array to convert
     * @return float array
     * @throws UtilityException
     */
    public static double[] stringArrayToDoubleArray(String[] strarr) throws UtilityException {
        if (strarr == null) {
            throw new UtilityException("arg.null", "String strarr");
        }
        else if (strarr.length < 1) {
            throw new UtilityException("arg.empty", "String strarr");
        }
        double[] toRet = new double[strarr.length];
        for (int index = 0; index < strarr.length; index++) {
            try {
                toRet[index] = Short.parseShort(strarr[index].trim());
            }
            catch (NumberFormatException nfe) {
                throw new UtilityException("str.nan", strarr[index]);
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
        return joinString(doubleArrayToStringArray(doubles), spacer, 0);
    }

    /**
     * Converts the elements of the double array to Strings
     * 
     * @param doubles
     *            the double array to convert
     * @return string array
     * @throws UtilityException
     */
    public static String[] doubleArrayToStringArray(double[] doubles) throws UtilityException {
        if (doubles == null) {
            throw new UtilityException("arg.null", "double[] doubles");
        }
        else if (doubles.length == 0) {
            throw new UtilityException("arg.empty", "double[] doubles");
        }
        String[] arr = new String[doubles.length];
        for (int index = 0; index < doubles.length; index++) {
            arr[index] = String.valueOf(doubles[index]);
        }
        return arr;
    }
}
