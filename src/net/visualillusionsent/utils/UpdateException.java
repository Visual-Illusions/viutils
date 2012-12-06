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
 * Update Exception
 * <p>
 * Thrown from {@link Updater} when an exception occurs during an update<br>
 * use the {@link #getMessage()} method to retrieve the reason why the update failed
 * <p>
 * This File is part of the VIUtils<br>
 * &copy; 2012 Visual Illusions Entertainment <a href="http://visualillusionsent.net">http://visualillusionsent.net</a>
 * 
 * @since VIUtils 1.0
 * @version 1.0
 * @author Jason (darkdiplomat)
 */
public class UpdateException extends Exception {
    /**
     * Serial Version
     */
    private static final long serialVersionUID = 121212062012L;

    /**
     * Class Constructor
     * <p>
     * Should not be constructed outside of VIUtils
     * 
     * @param message
     *            the message of why the exception is being thrown
     */
    UpdateException(String message) {
        super(message);
    }

}
