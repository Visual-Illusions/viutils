/*
 * This file is part of VIUtils.
 *
 * Copyright © 2012-2013 Visual Illusions Entertainment
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

/**
 * Provides static fields and methods to help with getting System information
 * 
 * @since 1.0.0
 * @version 1.0
 * @author Jason (darkdiplomat)
 */
public final class SystemUtils{

    private static final float classVersion = 1.0F;

    /**
     * This class should never be externally constructed
     */
    private SystemUtils(){}

    /**
     * The System Line Separator (Windows = \r\n Unix = \n Older Macs = \r)
     */
    public static final String LINE_SEP = System.getProperty("line.separator");
    /**
     * The System Operation System
     */
    public static final String SYSTEM_OS = System.getProperty("os.name");
    /**
     * The System Architecture (x86 [32Bit] x64 x86-64 [64Bit])
     */
    public static final String SYSTEM_ARCH = System.getProperty("os.arch");
    /**
     * The System Version
     */
    public static final String SYSTEM_VERSION = System.getProperty("os.version");
    /**
     * The System Country
     */
    public static final String SYSTEM_COUNTRY = System.getProperty("user.country");
    /**
     * The System Language
     */
    public static final String SYSTEM_LANGUAGE = System.getProperty("user.language");
    /**
     * The System Locale
     */
    public static final String SYSTEM_LOCALE = SYSTEM_LANGUAGE.concat("_").concat(SYSTEM_COUNTRY);
    /**
     * The Java Version
     */
    public static final String JAVA_VERSION = System.getProperty("java.version");

    /**
     * Tells if the OS is a Windows based OS
     * 
     * @return {@code true} if Windows; {@code false} otherwise
     */
    public static final boolean isWindows(){
        return (SYSTEM_OS.toLowerCase().indexOf("win") >= 0);
    }

    /**
     * Tells if the OS is a MacOS based OS
     * 
     * @return {@code true} if MacOS; {@code false} otherwise
     */
    public static final boolean isMac(){
        return (SYSTEM_OS.toLowerCase().indexOf("mac") >= 0);
    }

    /**
     * Tells if the OS is a Unix based OS (Linux/MacOSX)
     * 
     * @return {@code true} if Unix; {@code false} otherwise
     */
    public static final boolean isUnix(){
        return (SYSTEM_OS.toLowerCase().indexOf("nix") >= 0 || SYSTEM_OS.toLowerCase().indexOf("nux") >= 0 || SYSTEM_OS.toLowerCase().indexOf("aix") > 0);
    }

    /**
     * Tells if the OS is a Solaris OS
     * 
     * @return {@code true} if Solaris; {@code false} otherwise
     */
    public static final boolean isSolaris(){
        return (SYSTEM_OS.toLowerCase().indexOf("sunos") >= 0);
    }

    /**
     * Gets this class's version number
     * 
     * @return the class version
     */
    public final static float getClassVersion(){
        return classVersion;
    }
}
