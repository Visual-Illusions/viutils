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
 * VIUtils is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with VIUtils.
 * If not, see http://www.gnu.org/licenses/lgpl.html.
 */
package net.visualillusionsent.utils;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

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
        HashSet<T> res = new HashSet<T>();

        Collections.addAll(res, first);
        Collections.addAll(res, second);
        return res.toArray(template);
    }
}
