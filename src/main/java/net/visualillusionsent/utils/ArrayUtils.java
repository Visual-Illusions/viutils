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

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;

/**
 * Array Utilities
 *
 * @author Jason (darkdiplomat)
 * @author Chris (damagefilter)
 * @version 1.0
 * @since 1.3.0
 */
public final class ArrayUtils {

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
        boolean[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }
    /* END Primitive Arrays */
}
