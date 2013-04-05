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

import java.net.URISyntaxException;
import java.security.CodeSource;
import java.text.MessageFormat;
import java.util.logging.Logger;

/**
 * Message Localization helper
 * <p>
 * If extending this class you should include a folder called resources in your jar and in that a folder called lang.<br>
 * Then include a text file called languages.txt that contains keys like the language.txt included with VIUtils.<br>
 * You should also include a en_US.lang file with default English messages for your implementation.
 * 
 * @since 1.0
 * @version 1.0
 * @author Jason (darkdiplomat)
 */
public abstract class LocaleHelper{

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
     * Jar path container
     */
    private String                jarPath;
    /**
     * Overrides the System default code
     */
    protected String              localeCodeOverride;

    /**
     * Gets the translated message for the given key
     * 
     * @param key
     *            the key to the translated message
     * @return translated message
     */
    public final String localeTranslate(String key){
        try{
            checkLangFiles();
            if(utils_sysLang != null && utils_sysLang.containsKey(key)){
                return utils_sysLang.getString(key);
            }
            return defaultTranslate(key);
        }
        catch(Exception e){
            Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).warning("[VIUtils] Exception thrown from LocaleHelper, check logs.");
            UtilsLogger.warning("Translate Error: ", e);
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
    public final String defaultTranslate(String key){
        try{
            checkLangFiles();
            if(utils_eng.containsKey(key)){
                return utils_eng.getString(key);
            }
        }
        catch(Exception e){
            Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).warning("[VIUtils] Exception thrown from LocaleHelper, check logs.");
            UtilsLogger.warning("Translate Error: ", e);
        }
        //May have forgot a translation and left a regular message, or something went wrong...
        return key;
    }

    /**
     * Gets the translated message for the given key and then formated to include the form string
     * 
     * @deprecated Use {@link #defaultTranslateMessage(String, Object...)} instead
     * @param key
     *            the key to the translated message
     * @param form
     *            the String to format the message String with
     * @return translated message
     * @see String#format(String, Object...)
     */
    @Deprecated
    public final String localeTranslateFormat(String key, String... form){
        try{
            checkLangFiles();
            if(utils_sysLang != null && utils_sysLang.containsKey(key)){
                return String.format(utils_sysLang.getString(key), (Object[])form);
            }
            return defaultTranslateFormat(key, form);
        }
        catch(Exception e){
            Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).warning("[VIUtils] Exception thrown from LocaleHelper, check logs.");
            UtilsLogger.warning("Translate Error: ", e);
        }
        return defaultTranslateFormat(key, form);
    }

    /**
     * Gets the translated message for the given key and then formated to include the form string
     * 
     * @param key
     *            the key to the translated message
     * @param form
     *            the arguments to pass the {@link MessageFormatter}
     * @return translated message
     * @see MessageFormat#format(String, Object...)
     */
    public final String localeTranslateMessage(String key, Object... form){
        try{
            checkLangFiles();
            if(utils_sysLang != null && utils_sysLang.containsKey(key)){
                return MessageFormat.format(utils_sysLang.getString(key), form);
            }
            return defaultTranslateMessage(key, form);
        }
        catch(Exception e){
            Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).warning("[VIUtils] Exception thrown from LocaleHelper, check logs.");
            UtilsLogger.warning("Translate Error", e);
        }
        return defaultTranslateMessage(key, form);
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
    @Deprecated
    public final String defaultTranslateFormat(String key, String... form){
        try{
            checkLangFiles();
            if(utils_eng.containsKey(key)){
                return String.format(utils_eng.getString(key), (Object[])form);
            }
        }
        catch(Exception e){
            Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).warning("[VIUtils] Exception thrown from LocaleHelper, check logs.");
            UtilsLogger.warning("Translate Error: ", e);
        }
        //May have forgot a translation and left a regular message
        return String.format(key, (Object[])form);
    }

    /**
     * Gets the default English message for the given key and then formated to include the form string
     * 
     * @param key
     *            the key to the translated message
     * @param form
     *            the arguments to pass the {@link MessageFormatter}
     * @return translated message
     * @see MessageFormat#format(String, Object...)
     */
    public final String defaultTranslateMessage(String key, Object... form){
        try{
            checkLangFiles();
            if(utils_eng.containsKey(key)){
                return MessageFormat.format(utils_eng.getString(key), form);
            }
        }
        catch(Exception e){
            Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).warning("[VIUtils] Exception thrown from LocaleHelper, check logs.");
            UtilsLogger.warning("Translate Error: ", e);
        }
        //May have forgot a translation and left a regular message
        return MessageFormat.format(key, form);
    }

    private final void checkLangFiles() throws UtilityException, URISyntaxException{
        if(utils_lang == null){
            utils_lang = new PropertiesFile(getJarPath(), "resources/lang/languages.txt");
        }
        if(utils_eng == null){
            utils_eng = new PropertiesFile(getJarPath(), "resources/lang/en_US.lang");
        }
        if(localeCodeOverride != null && localeCodeOverride.matches("([a-z]{2,3})_([A-Z]{2,3})")){
            if(utils_sysLang == null && utils_lang.containsKey(localeCodeOverride)){
                utils_sysLang = new PropertiesFile(getJarPath(), "resources/lang/".concat(utils_lang.getString(localeCodeOverride)).concat(".lang"));
            }
        }
        else if(SystemUtils.SYSTEM_LOCALE != null){
            if(utils_sysLang == null && utils_lang.containsKey(SystemUtils.SYSTEM_LOCALE)){
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
    private final String getJarPath() throws URISyntaxException{
        if(jarPath != null){
            return jarPath;
        }
        CodeSource codeSource = getClass().getProtectionDomain().getCodeSource();
        jarPath = codeSource.getLocation().toURI().getPath();
        return jarPath;
    }
}
