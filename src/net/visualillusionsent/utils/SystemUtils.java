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

/**
 * System Utilities
 * <p>
 * Provides static methods to help with getting System information
 * <p>
 * This File is part of the VIUtils<br>
 * &copy; 2012 Visual Illusions Entertainment <a href="http://visualillusionsent.net">http://visualillusionsent.net</a>
 * 
 * @since VIUtils 1.0
 * @version 1.0
 * @author Jason (darkdiplomat)
 */
public final class SystemUtils {
    private SystemUtils() {}

    /**
     * The System Line Separator (Windows = \r\n Unix = \n Older Macs = \r)
     */
    public static String LINE_SEP = System.getProperty("line.separator");

    /**
     * The System Operation System
     */
    public static String SYSTEM_OS = System.getProperty("os.name");

    /**
     * The System Architecture (x86 [32Bit] x64 x86-64 [64Bit])
     */
    public static String SYSTEM_ARCH = System.getProperty("os.arch");

    /**
     * The System Version
     */
    public static String SYSTEM_VERSION = System.getProperty("os.version");

}
