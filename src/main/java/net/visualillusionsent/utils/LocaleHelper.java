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

import java.io.File;
import java.text.MessageFormat;
import java.util.logging.Logger;

/**
 * Message Localization helper
 * <p>
 * If extending this class you should include a folder called resources in your jar and in that a folder called lang.<br>
 * Then include a text file called languages.txt that contains keys like the language.txt included with VIUtils.<br>
 * You should also include a en_US.lang file with default English messages for your implementation.
 * <p>
 * As of LocaleHelper 1.2, you can now specify an external directory as the path to the lang files.<br>
 * The directory should be set up the same as though it is inside the Jar file.
 * 
 * @since 1.0
 * @version 1.3
 * @author Jason (darkdiplomat)
 */
public abstract class LocaleHelper{

    private static final float classVersion = 1.3F;
    /* languages.txt quick reference */
    private static final String langTXT = "languages.txt";
    /* en_US.lang quick reference */
    private static final String enUS = "en_US.lang";
    /**
     * The language.txt file for knowing which languages are supported
     */
    protected final UnmodifiablePropertiesFile utils_lang;
    /**
     * The .lang file that has the proper translations
     */
    protected final UnmodifiablePropertiesFile utils_sysLang;
    /**
     * The default English message file
     */
    protected final UnmodifiablePropertiesFile utils_eng;
    /**
     * Jar path container, override as needed
     */
    protected final String jarPath;
    /**
     * Overrides the System default code
     */
    protected String localeCodeOverride;
    /**
     * Set to true if external files are used
     */
    protected final boolean external;
    /**
     * Path to external files
     */
    protected final String extDir;

    /**
     * Constructs a default LocaleHelper that will look in the jar for the lang files
     */
    protected LocaleHelper(){
        this(false, null);
    }

    /**
     * Constructs a new LocaleHelper with specifing external files and the directory for those files
     * 
     * @param useExternalFiles
     *            {@code true} for external files; {@code false} otherwise
     * @param externalDirectory
     *            the path to the directory for the external files
     */
    protected LocaleHelper(boolean useExternalFiles, String externalDirectory){
        if(externalDirectory == null){
            external = false;
            extDir = null;
        }
        else{
            this.external = useExternalFiles;
            String adjustPath = FileUtils.normalizePath(externalDirectory);
            extDir = adjustPath.endsWith(File.separator) ? adjustPath : adjustPath.concat(File.separator);
        }
        this.jarPath = JarUtils.getJarPath(getClass());
        if(!external){
            utils_lang = new UnmodifiablePropertiesFile(jarPath, "resources/lang/".concat(langTXT));
        }
        else{
            utils_lang = new UnmodifiablePropertiesFile(extDir.concat(langTXT));
        }
        if(!external){
            utils_eng = new UnmodifiablePropertiesFile(jarPath, "resources/lang/".concat(enUS));
        }
        else{
            utils_eng = new UnmodifiablePropertiesFile(extDir.concat(enUS));
        }
        if(localeCodeOverride != null && localeCodeOverride.matches("([a-z]{2,3})_([A-Z]{2,3})") && utils_lang.containsKey(localeCodeOverride)){
            if(!external){
                utils_sysLang = new UnmodifiablePropertiesFile(jarPath, "resources/lang/".concat(utils_lang.getString(localeCodeOverride)).concat(".lang"));
            }
            else{
                utils_sysLang = new UnmodifiablePropertiesFile(extDir.concat(utils_lang.getString(localeCodeOverride)).concat(".lang"));
            }
        }
        else if(SystemUtils.SYSTEM_LOCALE != null && utils_lang.containsKey(SystemUtils.SYSTEM_LOCALE)){
            if(!external){
                utils_sysLang = new UnmodifiablePropertiesFile(jarPath, "resources/lang/".concat(utils_lang.getString(SystemUtils.SYSTEM_LOCALE)).concat(".lang"));
            }
            else{
                utils_sysLang = new UnmodifiablePropertiesFile(extDir.concat(utils_lang.getString(SystemUtils.SYSTEM_LOCALE)).concat(".lang"));
            }
        }
        else{
            utils_sysLang = utils_eng;
        }
    }

    /**
     * Gets the translated message for the given key
     * 
     * @param key
     *            the key to the translated message
     * @return translated message
     */
    public final String localeTranslate(String key){
        try{
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
     * @param key
     *            the key to the translated message
     * @param form
     *            the arguments to pass the {@link MessageFormat}
     * @return translated message
     * @see MessageFormat#format(String, Object...)
     */
    public final String localeTranslateMessage(String key, Object... form){
        try{
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
     *            the arguments to pass the {@link MessageFormat}
     * @return translated message
     * @see MessageFormat#format(String, Object...)
     */
    public final String defaultTranslateMessage(String key, Object... form){
        try{
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

    /**
     * Reloads the language files.
     * 
     * @throws UtilityException
     * @see PropertiesFile#reload
     */
    public final void reloadLangFiles() throws UtilityException{
        if(utils_sysLang != null){
            utils_sysLang.reload();
        }
        if(utils_eng != null){
            utils_eng.reload();
        }
    }

    /**
     * Gets this class's version number
     * 
     * @return the class version
     */
    public static final float getClassVersion(){
        return classVersion;
    }
}
