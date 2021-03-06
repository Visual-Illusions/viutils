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

import java.io.File;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.logging.Logger;

/**
 * Message Localization helper
 * <p/>
 * If extending this class you should include a folder called resources in your jar and in that a folder called lang.<br>
 * Then include a text file called languages.txt that contains keys like the language.txt included with VIUtils.<br>
 * You should also include a en_US.lang file with default English messages for your implementation.
 * <p/>
 * As of LocaleHelper 1.2, you can now specify an external directory as the path to the lang files.<br>
 * The directory should be set up the same as though it is inside the Jar file.
 *
 * @author Jason (darkdiplomat)
 * @version 1.5
 * @since 1.0.0
 */
public abstract class LocaleHelper {

    /* 1.5 @ VIUtils 1.4.2 */
    private static final float classVersion = 1.5F;
    /* languages.txt quick reference */
    private static final String langTXT = "languages.txt";
    /** Map of supported languages */
    protected final HashMap<String, UnmodifiablePropertiesFile> langs = new HashMap<String, UnmodifiablePropertiesFile>();
    /** The language.txt file for knowing which languages are supported */
    protected final UnmodifiablePropertiesFile utils_lang;
    /** Jar path container, override as needed */
    protected final String jarPath;
    /** The System default code */
    protected final String defaultLocale;
    /** Set to true if external files are used */
    protected final boolean external;
    /** Path to external files */
    protected final String extDir;
    /** Set to true to enable debugging */
    protected boolean debug_enabled = false;

    /** Constructs a default LocaleHelper that will look in the jar for the lang files */
    protected LocaleHelper() {
        this(false, null, (String) null);
    }

    /**
     * Constructs a default LocaleHelper with a default Locale specified
     *
     * @param defaultLocale
     *         the locale to use by default
     */
    protected LocaleHelper(String defaultLocale) {
        this(false, null, defaultLocale);
    }

    /**
     * Constructs a default LocaleHelper with a default Locale specified
     *
     * @param defaultLocale
     *         the locale to use by default
     */
    protected LocaleHelper(Locale defaultLocale) {
        this(false, null, defaultLocale.toString());
    }

    /**
     * Constructs a new LocaleHelper with specifying external files and the directory for those files
     *
     * @param useExternalFiles
     *         {@code true} for external files; {@code false} otherwise
     * @param externalDirectory
     *         the path to the directory for the external files
     * @param defaultLocale
     *         the default Locale to use for messages
     */
    protected LocaleHelper(boolean useExternalFiles, String externalDirectory, Locale defaultLocale) {
        this(useExternalFiles, externalDirectory, defaultLocale.toString());
    }

    /**
     * Constructs a new LocaleHelper with specifying external files and the directory for those files
     *
     * @param useExternalFiles
     *         {@code true} for external files; {@code false} otherwise
     * @param externalDirectory
     *         the path to the directory for the external files
     * @param defaultLocale
     *         the default Locale to use for messages
     */
    protected LocaleHelper(boolean useExternalFiles, String externalDirectory, String defaultLocale) {
        this.defaultLocale = defaultLocale == null ? SystemUtils.SYSTEM_LOCALE : defaultLocale;
        if (externalDirectory == null) {
            this.external = false;
            this.extDir = null;
        }
        else {
            this.external = useExternalFiles;
            String adjustPath = FileUtils.normalizePath(externalDirectory);
            this.extDir = adjustPath.endsWith(File.separator) ? adjustPath : adjustPath.concat(File.separator);
        }
        this.jarPath = JarUtils.getJarPath(getClass());
        if (!external) {
            utils_lang = new UnmodifiablePropertiesFile(jarPath, "resources/lang/".concat(langTXT));
        }
        else {
            utils_lang = new UnmodifiablePropertiesFile(extDir.concat(langTXT));
        }
        loadLang("en_US");
        if (this.defaultLocale != null && this.defaultLocale.matches("([a-z]{2,3})_([A-Z]{2,3})") && utils_lang.containsKey(defaultLocale)) {
            loadLang(this.defaultLocale);
        }
    }

    public final String localeTranslate(String key, String locale) {
        try {
            if (locale != null && locale.matches("([a-z]{2,3})_([A-Z]{2,3})") && utils_lang.containsKey(locale)) {
                if (!langs.containsKey(locale)) {
                    loadLang(locale);
                }
                return langs.get(locale).getString(key);
            }
        }
        catch (Exception e) {
            if (debug_enabled) {
                Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).warning("[VIUtils] Exception thrown from LocaleHelper, check the viutils logs.");
                UtilsLogger.warning("Translate Error: ", e);
            }
        }
        return systemTranslate(key);
    }

    /**
     * Gets the translated message for the given key
     *
     * @param key
     *         the key to the translated message
     *
     * @return translated message
     */
    public final String systemTranslate(String key) {
        try {
            if (defaultLocale != null && langs.containsKey(defaultLocale) && langs.get(defaultLocale).containsKey(key)) {
                return langs.get(defaultLocale).getString(key);
            }
        }
        catch (Exception e) {
            if (debug_enabled) {
                Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).warning("[VIUtils] Exception thrown from LocaleHelper, check the viutils logs.");
                UtilsLogger.warning("Translate Error: ", e);
            }
        }
        return defaultTranslate(key);
    }

    /**
     * Gets the default English message for the given key
     *
     * @param key
     *         the key to the translated message
     *
     * @return translated message
     */
    public final String defaultTranslate(String key) {
        try {
            if (langs.get("en_US").containsKey(key)) {
                return langs.get("en_US").getString(key);
            }
        }
        catch (Exception e) {
            if (debug_enabled) {
                Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).warning("[VIUtils] Exception thrown from LocaleHelper, check the viutils logs.");
                UtilsLogger.warning("Translate Error: ", e);
            }
        }
        //May have forgot a translation and left a regular message, or something went wrong...
        return key;
    }

    public final String localeTranslate(String key, String locale, Object... form) {
        try {
            if (locale != null && locale.matches("([a-z]{2,3})_([A-Z]{2,3})") && utils_lang.containsKey(locale)) {
                if (!langs.containsKey(locale)) {
                    loadLang(locale);
                }
                return MessageFormat.format(langs.get(locale).getString(key), form);
            }
        }
        catch (Exception e) {
            if (debug_enabled) {
                Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).warning("[VIUtils] Exception thrown from LocaleHelper, check the viutils logs.");
                UtilsLogger.warning("Translate Error", e);
            }
        }
        return systemTranslate(key, form);
    }

    /**
     * Gets the translated message for the given key and then formatted to include the form string
     *
     * @param key
     *         the key to the translated message
     * @param form
     *         the arguments to pass the {@link MessageFormat}
     *
     * @return translated message
     *
     * @see MessageFormat#format(String, Object...)
     */
    public final String systemTranslate(String key, Object... form) {
        try {
            if (defaultLocale != null && langs.containsKey(defaultLocale) && langs.get(defaultLocale).containsKey(key)) {
                return MessageFormat.format(langs.get(defaultLocale).getString(key), form);
            }
        }
        catch (Exception e) {
            if (debug_enabled) {
                Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).warning("[VIUtils] Exception thrown from LocaleHelper, check the viutils logs.");
                UtilsLogger.warning("Translate Error", e);
            }
        }
        return defaultTranslate(key, form);
    }

    /**
     * Gets the default English message for the given key and then formatted to include the form string
     *
     * @param key
     *         the key to the translated message
     * @param form
     *         the arguments to pass the {@link MessageFormat}
     *
     * @return translated message
     *
     * @see MessageFormat#format(String, Object...)
     */
    public final String defaultTranslate(String key, Object... form) {
        try {
            if (langs.get("en_US").containsKey(key)) {
                return MessageFormat.format(langs.get("en_US").getString(key), form);
            }
        }
        catch (Exception e) {
            if (debug_enabled) {
                Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).warning("[VIUtils] Exception thrown from LocaleHelper, check the viutils logs.");
                UtilsLogger.warning("Translate Error: ", e);
            }
        }
        //May have forgot a translation and left a regular message
        return MessageFormat.format(key, form);
    }

    /**
     * Reloads the language files.
     *
     * @see UnmodifiablePropertiesFile#reload
     */
    public final void reloadLangFiles() {
        for (UnmodifiablePropertiesFile upf : langs.values()) {
            upf.reload();
        }
    }

    private void loadLang(String locale) {
        if (langs.containsKey(utils_lang.getString(locale))) {
            // Save memory, reuse pointers
            UnmodifiablePropertiesFile temp = langs.get(utils_lang.getString(locale));
            langs.put(locale, temp);
        }
        else {
            if (!external) {
                langs.put(locale, new UnmodifiablePropertiesFile(jarPath, "resources/lang/".concat(utils_lang.getString(locale)).concat(".lang")));
            }
            else {
                langs.put(locale, new UnmodifiablePropertiesFile(extDir.concat(utils_lang.getString(locale)).concat(".lang")));
            }
        }
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
