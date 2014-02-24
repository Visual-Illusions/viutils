/*
 * This file is part of VIUtils.
 *
 * Copyright © 2012-2014 Visual Illusions Entertainment
 *
 * VIUtils is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this library.
 * If not, see http://www.gnu.org/licenses/lgpl.html.
 */
package net.visualillusionsent.utils;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;

/**
 * Verify
 * <p/>
 * Provides methods to help with verifying input
 *
 * @author Jason (darkdiplomat)
 * @version 1.1
 * @since 1.2.0
 */
public final class Verify {

    private static final float classVersion = 1.1F;
    private static final Map<String, String> errors;

    static {
        HashMap<String, String> temp = new HashMap<String, String>();
        temp.put("arg.null", "'%s' cannot be null");
        temp.put("arg.empty", "'%s' cannot be empty");
        temp.put("file.err.ioe", "An IOException occurred in File: %s");
        temp.put("file.err.exist", "%s is not an existing file");
        temp.put("file.err.read", "Could not read File: %s");
        temp.put("file.err.write", "Could not write to File: %s");
        temp.put("file.err.path", "%s path equals %s path");
        temp.put("file.err.dir", "%s is a Directory, not a file");
        temp.put("dir.err.file", "%s is a File, not a Directory");
        temp.put("key.missing", "Property for KEY: %s was not found.");
        temp.put("prop.nan", "Property for KEY: %s was not a number.");
        temp.put("str.nan", "String Index: %s was not a number");
        temp.put("entry.missing", "JarFile does not contain Entry: %s");
        temp.put("num.zeroOrLess", "%s cannot be negative or zero");
        temp.put("num.negative", "%s cannot be negative");
        temp.put("sum.fail", "The underlining Java Runtime Environment does not appear to support the %s Algorithm");
        errors = Collections.unmodifiableMap(temp);
    }

    public enum FileAction {
        EXISTS, //
        ISFILE, //
        NOTFILE, //
        ISDIRECTORY, //
        NOTDIRECTORY, //
        READ, //
        WRITE, //
        EXECUTE, //
    }

    /**
     * Checks an {@link Object} for null
     *
     * @param obj
     *         the object to check
     * @param arg
     *         the argument name to pass with the exception message
     *
     * @throws NullPointerException
     *         if the object is null
     */
    public static void notNull(Object obj, String arg) {
        if (obj == null)
            throw new NullPointerException(parse("arg.null", arg));
    }

    /**
     * Checks if a {@link String} is empty
     *
     * @param str
     *         the {@link String} to check
     * @param arg
     *         the argument name to pass with the exception message
     *
     * @throws IllegalArgumentException
     *         if the {@link String} is empty
     */
    public static void notEmpty(String str, String arg) {
        if (str.trim().isEmpty())
            throw new IllegalArgumentException(parse("arg.empty", arg));
    }

    /**
     * Checks if a {@link String} is empty without trimming the {@link String}
     *
     * @param str
     *         the {@link String} to check
     * @param arg
     *         the argument name to pass with the exception message
     *
     * @throws IllegalArgumentException
     *         if the {@link String} is empty
     */
    public static void notEmptyNoTrim(String str, String arg) {
        if (str.isEmpty())
            throw new IllegalArgumentException(parse("arg.empty", arg));
    }

    /**
     * Checks if a {@code Object[]} is empty
     *
     * @param objArray
     *         the {@code Object[]} to check
     * @param arg
     *         the argument name to pass with the exception message
     *
     * @throws IllegalArgumentException
     *         if the {@code Object[]} is empty
     */
    public static void notEmpty(Object[] objArray, String arg) {
        if (objArray.length <= 0)
            throw new IllegalArgumentException(parse("arg.empty", arg));
    }

    /**
     * Checks if a {@code byte[]} is empty
     *
     * @param byteArray
     *         the {@code byte[]} to check
     * @param arg
     *         the argument name to pass with the exception message
     *
     * @throws IllegalArgumentException
     *         if the {@code byte[]} is empty
     */
    public static void notEmpty(byte[] byteArray, String arg) {
        if (byteArray.length <= 0)
            throw new IllegalArgumentException(parse("arg.empty", arg));
    }

    /**
     * Checks if a {@code short[]} is empty
     *
     * @param shortArray
     *         the {@code short[]} to check
     * @param arg
     *         the argument name to pass with the exception message
     *
     * @throws IllegalArgumentException
     *         if the {@code short[]} is empty
     */
    public static void notEmpty(short[] shortArray, String arg) {
        if (shortArray.length <= 0)
            throw new IllegalArgumentException(parse("arg.empty", arg));
    }

    /**
     * Checks if a {@code int[]} is empty
     *
     * @param intArray
     *         the {@code int[]} to check
     * @param arg
     *         the argument name to pass with the exception message
     *
     * @throws IllegalArgumentException
     *         if the {@code int[]} is empty
     */
    public static void notEmpty(int[] intArray, String arg) {
        if (intArray.length <= 0)
            throw new IllegalArgumentException(parse("arg.empty", arg));
    }

    /**
     * Checks if a {@code long[]} is empty
     *
     * @param longArray
     *         the {@code long[]} to check
     * @param arg
     *         the argument name to pass with the exception message
     *
     * @throws IllegalArgumentException
     *         if the {@code long[]} is empty
     */
    public static void notEmpty(long[] longArray, String arg) {
        if (longArray.length <= 0)
            throw new IllegalArgumentException(parse("arg.empty", arg));
    }

    /**
     * Checks if a {@code float[]} is empty
     *
     * @param floatArray
     *         the {@code float[]} to check
     * @param arg
     *         the argument name to pass with the exception message
     *
     * @throws IllegalArgumentException
     *         if the {@code float[]} is empty
     */
    public static void notEmpty(float[] floatArray, String arg) {
        if (floatArray.length <= 0)
            throw new IllegalArgumentException(parse("arg.empty", arg));
    }

    /**
     * Checks if a {@code double[]} is empty
     *
     * @param doubleArray
     *         the {@code double[]} to check
     * @param arg
     *         the argument name to pass with the exception message
     *
     * @throws IllegalArgumentException
     *         if the {@code double[]} is empty
     */
    public static void notEmpty(double[] doubleArray, String arg) {
        if (doubleArray.length <= 0)
            throw new IllegalArgumentException(parse("arg.empty", arg));
    }

    /**
     * Checks if a {@code boolean[]} is empty
     *
     * @param booleanArray
     *         the {@code boolean[]} to check
     * @param arg
     *         the argument name to pass with the exception message
     *
     * @throws IllegalArgumentException
     *         if the {@code boolean[]} is empty
     */
    public static void notEmpty(boolean[] booleanArray, String arg) {
        if (booleanArray.length <= 0)
            throw new IllegalArgumentException(parse("arg.empty", arg));
    }

    public static void notNegativeOrZero(Number number, String arg) {
        if (number.doubleValue() <= 0)
            throw new IllegalArgumentException(parse("num.zeroOrLess", arg));
    }

    public static void notNegative(Number number, String arg) {
        if (number.doubleValue() < 0)
            throw new IllegalArgumentException(parse("num.negative", arg));
    }

    public static void notOutOfRange(long check, long range, String msg) {
        if (check > range)
            throw new IllegalArgumentException(msg);
    }

    public static void notOutOfRangeEqual(long check, long range, String msg) {
        if (check >= range)
            throw new IllegalArgumentException(msg);
    }

    public static void entryExists(JarEntry entry, String arg) {
        if (entry == null)
            throw new MissingJarEntryException(parse("entry.missing", arg));
    }

    public static void fileCheck(File file, FileAction action) {
        switch (action) {
            case EXISTS:
                if (!file.exists())
                    throw new IllegalArgumentException(parse("file.err.exist", file.getName()));
            case ISFILE:
                if (!file.isFile())
                    throw new IllegalArgumentException(parse("file.err.dir", file.getName()));
            case NOTFILE:
                if (file.isFile())
                    throw new IllegalArgumentException(parse("dir.err.file", file.getName()));
            case ISDIRECTORY:
                if (!file.isDirectory())
                    throw new IllegalArgumentException(parse("dir.err.file", file.getName()));
            case NOTDIRECTORY:
                if (file.isDirectory())
                    throw new IllegalArgumentException(parse("file.err.dir", file.getName()));
            case READ:
                if (!file.canRead())
                    throw new IllegalArgumentException(parse("file.err.read", file.getName()));
            case WRITE:
                if (!file.canWrite())
                    throw new IllegalArgumentException(parse("file.err.write", file.getName()));
        }
    }

    public static String parse(String error, String... form) {
        if (errors.containsKey(error)) {
            return String.format(errors.get(error), (Object[]) form);
        }
        return error;
    }

    /**
     * Gets this class's version number
     *
     * @return the class version
     */
    public static float getClassVersion() {
        return classVersion;
    }
}
