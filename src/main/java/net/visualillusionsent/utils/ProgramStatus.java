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
 * Program Status helper enum
 * <p>
 * Specifically used to tell VersionChecker the release status of the program.<br>
 * Can be used externally as a helpful way to state the same thing.
 * 
 * @since 1.0
 * @version 1.0
 * @author Jason (darkdiplomat)
 */
public enum ProgramStatus {
    UNKNOWN, //
    ALPHA, //
    BETA, //
    RELEASE_CANADATE, //
    STABLE;

    public String toString() {
        return super.toString().replace("_", " ");
    }
}
