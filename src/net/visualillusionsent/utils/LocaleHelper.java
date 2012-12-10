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

import java.net.URISyntaxException;
import java.security.CodeSource;

/**
 * Message Localization helper
 * <p>
 * If extending this class you should include a folder called resources in your jar and in that a folder called lang.<br>
 * Then include a text file called languages.txt that contains keys like the language.txt included with VIUtils.<br>
 * You should also include a en_US.lang file with default English messages for your implementation.
 * <p>
 * This File is part of the VIUtils<br>
 * &copy; 2012 <a href="http://visualillusionsent.net">Visual Illusions Entertainment</a>
 * 
 * @since VIUtils 1.0
 * @version 1.0
 * @author Jason (darkdiplomat)
 */
public abstract class LocaleHelper {
    /**
     * The language.txt file for knowing which languages are supported
     */
    private static PropertiesFile utils_lang;

    /**
     * The .lang file that has the proper translations
     */
    private static PropertiesFile utils_sysLang;

    /**
     * The default English message file
     */
    private static PropertiesFile utils_eng;

    /**
     * Overrides the System default code
     */
    protected static String localeCodeOverride;

    /**
     * Gets the translated message for the given key
     * 
     * @param key
     *            the key to the translated message
     * @return translated message
     */
    public final String localeTranslate(String key) {
        try {
            checkLangFiles();
            if (utils_sysLang.containsKey(key)) {
                return utils_sysLang.getString(key);
            }
            return defaultTranslate(key);
        }
        catch (Exception e) {
            //whoops
        }
        return defaultTranslate(key);
    }

    /**
     * Gets the default English message for the given key
     * 
     * @param key
     *            the key to the translated message
     * @return translated message
     */
    public final String defaultTranslate(String key) {
        try {
            checkLangFiles();
            if (utils_eng.containsKey(key)) {
                return utils_eng.getString(key);
            }
        }
        catch (Exception e) {
            //whoops
        }

        //May have forgot a translation and left a regular message, or something went wrong...
        return key;
    }

    /**
     * Gets the translated message for the given key and then formated to include the form string
     * 
     * @param key
     *            the key to the translated message
     * @param form
     *            the String to format the message String with
     * @return translated message
     * @see String#format(String, Object...)
     */
    public final String localeTranslateFormat(String key, String... form) {

        try {
            checkLangFiles();
            if (utils_sysLang.containsKey(key)) {
                return String.format(utils_sysLang.getString(key), (Object[]) form);
            }
            return defaultTranslateFormat(key, form);
        }
        catch (Exception e) {
            //whoops
            e.printStackTrace();
        }
        return defaultTranslateFormat(key, form);
    }

    /**
     * Gets the default English message for the given key and then formated to include the form string
     * 
     * @param key
     *            the key to the translated message
     * @param form
     *            the String to format the message String with
     * @return translated message
     * @see String#format(String, Object...)
     */
    public final String defaultTranslateFormat(String key, String... form) {
        try {
            checkLangFiles();
            if (utils_eng.containsKey(key)) {
                return String.format(utils_eng.getString(key), (Object[]) form);
            }
        }
        catch (Exception e) {
            //whoops
        }
        //May have forgot a translation and left a regular message
        return String.format(key, (Object[]) form);
    }

    private final void checkLangFiles() throws UtilityException, URISyntaxException {
        if (utils_lang == null) {
            utils_lang = new PropertiesFile(getJarPath(), "resources/lang/languages.txt");
        }
        if (utils_eng == null) {
            utils_eng = new PropertiesFile(getJarPath(), "resources/lang/en_US.lang");
        }
        if (localeCodeOverride != null && !localeCodeOverride.equals("en_US") && localeCodeOverride.matches("([a-z]{2, 3})_([A-Z]{2, 3})")) {
            if (utils_sysLang == null && utils_lang.containsKey(localeCodeOverride)) {
                utils_sysLang = new PropertiesFile(getJarPath(), "resources/lang/".concat(localeCodeOverride).concat(".lang"));
            }
        }
        else if (SystemUtils.SYSTEM_LOCALE != null && !SystemUtils.SYSTEM_LOCALE.equals("en_US")) {
            if (utils_sysLang == null && utils_lang.containsKey(SystemUtils.SYSTEM_LOCALE)) {
                utils_sysLang = new PropertiesFile(getJarPath(), "resources/lang/".concat(utils_sysLang.getString(SystemUtils.SYSTEM_LOCALE)).concat(".lang"));
            }
        }
    }

    /**
     * Gets where this class is implemented and uses it to get the language resources
     * 
     * @return path to the jar with the resources
     * @throws URISyntaxException
     *             caught in the translation methods and ignored internally
     */
    private final String getJarPath() throws URISyntaxException {
        CodeSource codeSource = getClass().getProtectionDomain().getCodeSource();
        return codeSource.getLocation().toURI().getPath();
    }
}
