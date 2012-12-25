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
 * The internal Locale Helper for UtilsHelper
 * 
 * @since 1.0
 * @version 1.0
 * @author Jason (darkdiplomat)
 */
final class UtilsLocaleHelper extends LocaleHelper {
    private static UtilsLocaleHelper instance;

    static {
        instance = new UtilsLocaleHelper();
    }

    /**
     * This class should not be constructed
     */
    private UtilsLocaleHelper() {}

    /**
     * Static method call back to {@link LocaleHelper#localeTranslate(String)}
     * 
     * @param key
     *            the key to the translated message
     * @return translated message
     * @see LocaleHelper#localeTranslate(String)
     */
    public static String localeTranslation(String key) {
        return instance.localeTranslate(key);
    }

    /**
     * Static method call back to {@link LocaleHelper#defaultTranslate(String)}
     * 
     * @param key
     *            the key to the default English message
     * @return default English message
     * @see LocaleHelper#localeTranslate(String)
     */
    public static String defaultTranslation(String key) {
        return instance.defaultTranslate(key);
    }

    /**
     * Static method call back to {@link LocaleHelper#localeTranslateFormat(String, String...)}
     * 
     * @param key
     *            the key to the translated message
     * @param form
     *            the String to format the message String with
     * @return translated message
     * @see LocaleHelper#localeTranslateFormat(String, String...)
     * @see String#format(String, Object...)
     */
    public static String localeTranslationFormat(String key, String... form) {
        return instance.localeTranslateFormat(key, form);
    }

    /**
     * Static method call back to {@link LocaleHelper#defaultTranslateFormat(String, String...)}
     * 
     * @param key
     *            the key to the translated message
     * @param form
     *            the String to format the message String with
     * @return translated message
     * @see LocaleHelper#defaultTranslateFormat(String, String...)
     * @see String#format(String, Object...)
     */
    public static String defaultTranslationFormat(String key, String... form) {
        return instance.defaultTranslateFormat(key, form);
    }
}
