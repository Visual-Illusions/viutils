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

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;

import static net.visualillusionsent.utils.Verify.notNull;

/**
 * Array Utilities
 *
 * @author Jason (darkdiplomat)
 * @author Chris (damagefilter)
 * @version 1.1
 * @since 1.3.0
 */
public final class ArrayUtils {
    /* 1.1 @ VIUtils 1.4.0 */
    private static final float classVersion = 1.1F;

    /* Permission Granted!
     * Logs: irc.esper.net - Channel #vi-dev
     * [16Jan2014 15:41:34] <darkdiplomat> damagefilter, do you care if i add your arrayMerge stuff to ArrayUtils in VIUtils?
     * [16Jan2014 15:49:45] <damagefilter> uh no, go ahead.
     */

    /**
     * Merge 2 arrays. This will just merge two arrays.
     *
     * @param first
     *         the first array to be merged
     * @param second
     *         the second array to be merged
     *
     * @return array containing all elements of the 2 given ones
     */
    public static <T> T[] arrayMerge(T[] first, T[] second) {
        notNull(first, "T[] first");
        notNull(second, "T[] second");

        T[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }

    /**
     * Merge 2 arrays. This will remove duplicates.
     *
     * @param first
     *         the first array to be merged
     * @param second
     *         the second array to be merged
     * @param template
     *         the array to use as a template for merging the arrays into one
     *
     * @return array containing all elements of the given 2 arrays, minus duplicate entries
     */
    public static <T> T[] safeArrayMerge(T[] first, T[] second, T[] template) {
        notNull(first, "T[] first");
        notNull(second, "T[] second");

        LinkedHashSet<T> res = new LinkedHashSet<T>(); //Using a LinkedHashSet so as to not disorder the Array element positions
        Collections.addAll(res, first);
        Collections.addAll(res, second);
        return res.toArray(template);
    }

    /* Primitive Arrays */

    /**
     * Merge 2 {@code byte} arrays. This will just merge two arrays.
     *
     * @param first
     *         the first array to be merged
     * @param second
     *         the second array to be merged
     *
     * @return array containing all elements of the 2 given ones
     */
    public static byte[] arrayMerge(byte[] first, byte[] second) {
        notNull(first, "byte[] first");
        notNull(second, "byte[] second");

        byte[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }

    /**
     * Merge 2 {@code short} arrays. This will just merge two arrays.
     *
     * @param first
     *         the first array to be merged
     * @param second
     *         the second array to be merged
     *
     * @return array containing all elements of the 2 given ones
     */
    public static short[] arrayMerge(short[] first, short[] second) {
        notNull(first, "short[] first");
        notNull(second, "short[] second");

        short[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }

    /**
     * Merge 2 {@code int} arrays. This will just merge two arrays.
     *
     * @param first
     *         the first array to be merged
     * @param second
     *         the second array to be merged
     *
     * @return array containing all elements of the 2 given ones
     */
    public static int[] arrayMerge(int[] first, int[] second) {
        notNull(first, "int[] first");
        notNull(second, "int[] second");

        int[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }

    /**
     * Merge 2 {@code long} arrays. This will just merge two arrays.
     *
     * @param first
     *         the first array to be merged
     * @param second
     *         the second array to be merged
     *
     * @return array containing all elements of the 2 given ones
     */
    public static long[] arrayMerge(long[] first, long[] second) {
        notNull(first, "long[] first");
        notNull(second, "long[] second");

        long[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }

    /**
     * Merge 2 {@code float} arrays. This will just merge two arrays.
     *
     * @param first
     *         the first array to be merged
     * @param second
     *         the second array to be merged
     *
     * @return array containing all elements of the 2 given ones
     */
    public static float[] arrayMerge(float[] first, float[] second) {
        notNull(first, "float[] first");
        notNull(second, "float[] second");

        float[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }

    /**
     * Merge 2 {@code double} arrays. This will just merge two arrays.
     *
     * @param first
     *         the first array to be merged
     * @param second
     *         the second array to be merged
     *
     * @return array containing all elements of the 2 given ones
     */
    public static double[] arrayMerge(double[] first, double[] second) {
        notNull(first, "double[] first");
        notNull(second, "double[] second");

        double[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }

    /**
     * Merge 2 {@code char} arrays. This will just merge two arrays.
     *
     * @param first
     *         the first array to be merged
     * @param second
     *         the second array to be merged
     *
     * @return array containing all elements of the 2 given ones
     */
    public static char[] arrayMerge(char[] first, char[] second) {
        notNull(first, "char[] first");
        notNull(second, "char[] second");

        char[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }

    /**
     * Merge 2 {@code boolean} arrays. This will just merge two arrays.
     *
     * @param first
     *         the first array to be merged
     * @param second
     *         the second array to be merged
     *
     * @return array containing all elements of the 2 given ones
     */
    public static boolean[] arrayMerge(boolean[] first, boolean[] second) {
        notNull(first, "boolean[] first");
        notNull(second, "boolean[] second");

        boolean[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }
    /* END Primitive Arrays */

    public static Byte[] toBoxed(byte[] value) {
        notNull(value, "byte[] value");

        Byte[] working = new Byte[value.length];
        for (int index = 0; index < value.length; index++) {
            working[index] = value[index];
        }
        return working;
    }

    public static Short[] toBoxed(short[] value) {
        notNull(value, "short[] value");

        Short[] working = new Short[value.length];
        for (int index = 0; index < value.length; index++) {
            working[index] = value[index];
        }
        return working;
    }

    public static Integer[] toBoxed(int[] value) {
        notNull(value, "int[] value");

        Integer[] working = new Integer[value.length];
        for (int index = 0; index < value.length; index++) {
            working[index] = value[index];
        }
        return working;
    }

    public static Long[] toBoxed(long[] value) {
        notNull(value, "long[] value");

        Long[] working = new Long[value.length];
        for (int index = 0; index < value.length; index++) {
            working[index] = value[index];
        }
        return working;
    }

    public static Float[] toBoxed(float[] value) {
        notNull(value, "float[] value");

        Float[] working = new Float[value.length];
        for (int index = 0; index < value.length; index++) {
            working[index] = value[index];
        }
        return working;
    }

    public static Double[] toBoxed(double[] value) {
        notNull(value, "double[] value");

        Double[] working = new Double[value.length];
        for (int index = 0; index < value.length; index++) {
            working[index] = value[index];
        }
        return working;
    }

    public static Boolean[] toBoxed(boolean[] value) {
        notNull(value, "boolean[] value");

        Boolean[] working = new Boolean[value.length];
        for (int index = 0; index < value.length; index++) {
            working[index] = value[index];
        }
        return working;
    }

    public static Character[] toBoxed(char[] value) {
        notNull(value, "char[] value");

        Character[] working = new Character[value.length];
        for (int index = 0; index < value.length; index++) {
            working[index] = value[index];
        }
        return working;
    }

    public static byte[] toPrimative(Byte[] value) {
        notNull(value, "Byte[] value");

        byte[] working = new byte[value.length];
        for (int index = 0; index < value.length; index++) {
            working[index] = value[index];
        }
        return working;
    }

    public static short[] toPrimative(Short[] value) {
        notNull(value, "Short[] value");

        short[] working = new short[value.length];
        for (int index = 0; index < value.length; index++) {
            working[index] = value[index];
        }
        return working;
    }

    public static int[] toPrimative(Integer[] value) {
        notNull(value, "Integer[] value");

        int[] working = new int[value.length];
        for (int index = 0; index < value.length; index++) {
            working[index] = value[index];
        }
        return working;
    }

    public static long[] toPrimative(Long[] value) {
        notNull(value, "Long[] value");

        long[] working = new long[value.length];
        for (int index = 0; index < value.length; index++) {
            working[index] = value[index];
        }
        return working;
    }

    public static float[] toPrimative(Float[] value) {
        notNull(value, "Float[] value");

        float[] working = new float[value.length];
        for (int index = 0; index < value.length; index++) {
            working[index] = value[index];
        }
        return working;
    }

    public static double[] toPrimative(Double[] value) {
        notNull(value, "Double[] value");

        double[] working = new double[value.length];
        for (int index = 0; index < value.length; index++) {
            working[index] = value[index];
        }
        return working;
    }

    public static boolean[] toPrimative(Boolean[] value) {
        notNull(value, "Boolean[] value");

        boolean[] working = new boolean[value.length];
        for (int index = 0; index < value.length; index++) {
            working[index] = value[index];
        }
        return working;
    }

    public static char[] toPrimative(Character[] value) {
        notNull(value, "Character[] value");

        char[] working = new char[value.length];
        for (int index = 0; index < value.length; index++) {
            working[index] = value[index];
        }
        return working;
    }

    /**
     * Checks if an {@code array} contains a {@code value}
     *
     * @param array
     *         the {@code array} to check
     * @param value
     *         the {@code value} to check
     *
     * @return {@code true} if the {@code array} contains the {@code value}
     */
    public static <T> boolean contains(final T[] array, final T value) {
        for (final T element : array) {
            if (element == value || value != null && value.equals(element)) {
                return true;
            }
        }
        return false;
    }
}
