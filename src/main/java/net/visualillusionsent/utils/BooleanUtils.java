/*
 * This file is part of VIUtils.
 *
 * Copyright © 2012-2015 Visual Illusions Entertainment
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

import java.util.concurrent.ConcurrentHashMap;

import static net.visualillusionsent.utils.Verify.notEmpty;
import static net.visualillusionsent.utils.Verify.notNull;

/**
 * BooleanUtils
 * <p/>
 * Provides methods to help with parsing Boolean values<br>
 *
 * @author Jason (darkdiplomat)
 * @version 1.2
 * @since 1.0.4
 */
public final class BooleanUtils {
    /* 1.2 @ VIUtils 1.4.0 */
    private static final float classVersion = 1.2F;
    private static final ConcurrentHashMap<String, Boolean> boolMatch = new ConcurrentHashMap<String, Boolean>();

    static {
        boolMatch.put("yes", true);
        boolMatch.put("true", true);
        boolMatch.put("on", true);
        boolMatch.put("allow", true);
        boolMatch.put("grant", true);
        boolMatch.put("1", true);
        boolMatch.put("false", false);
        boolMatch.put("no", false);
        boolMatch.put("off", false);
        boolMatch.put("deny", false);
        boolMatch.put("0", false);
    }

    /**
     * Register String to boolean associations
     *
     * @param key
     *         the String key to assign a boolean value for
     * @param value
     *         the boolean value to be assigned
     *
     * @return {@code null} if added, {@link Boolean} value if the key already had something associated.
     *
     * @throws java.lang.NullPointerException
     *         if key is null
     * @throws java.lang.IllegalArgumentException
     *         if key is empty
     */
    public static Boolean registerBoolean(final String key, final boolean value) {
        notNull(key, "String key");
        notEmpty(key, "String key");

        return boolMatch.putIfAbsent(key, value);
    }

    /**
     * Boolean parsing handler
     *
     * @param key
     *         the key to parse
     *
     * @return {@code boolean value} associated with the key, or {@code false} if a value isn't associated.
     *
     * @throws java.lang.NullPointerException
     *         if key is null
     * @throws java.lang.IllegalArgumentException
     *         if key is empty
     */
    public static boolean parseBoolean(final String key) {
        notNull(key, "String key");
        notEmpty(key, "String key");

        return boolMatch.containsKey(key) && boolMatch.get(key);
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
