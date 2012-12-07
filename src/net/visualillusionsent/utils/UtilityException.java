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
 * Utility Exception
 * <p>
 * Thrown when a used utility has improper arguments given to it or when something goes wrong<br>
 * use the {@link #getMessage()} method to retrieve the reason why the utility method failed
 * <p>
 * This File is part of the VIUtils<br>
 * &copy; 2012 <a href="http://visualillusionsent.net">Visual Illusions Entertainment</a>
 * 
 * @since VIUtils 1.0
 * @version 1.0
 * @author Jason (darkdiplomat)
 */
public class UtilityException extends Exception {
    private String form;

    /**
     * Serial Version
     */
    private static final long serialVersionUID = 042216122012L;

    /**
     * Class Constructor
     * <p>
     * Should not be constructed outside of VIUtils
     * 
     * @param msg
     *            the message of why the exception is being thrown
     */
    UtilityException(String msg) {
        super(msg);
    }

    /**
     * Class Constructor
     * <p>
     * Should not be constructed outside of VIUtils
     * 
     * @param msg
     *            the message of why the exception is being thrown
     * @param form
     *            the string to use in {@link String#format(String, Object...)}
     */
    UtilityException(String msg, String form) {
        super(msg);
        this.form = form;
    }

    /**
     * Gets the Message in English
     * 
     * @return message in English
     */
    public String getMessage() {
        if (form != null) {
            return UtilsLocaleHelper.defaultTranslationFormat(super.getMessage(), form);
        }
        return UtilsLocaleHelper.defaultTranslation(super.getMessage());
    }

    /**
     * Gets the Message in the System Language if translation found and English if not found
     * 
     * @return message in System Language if found, English otherwise
     */
    public String getLocalizeMessage() {
        if (form != null) {
            return UtilsLocaleHelper.localeTranslationFormat(super.getMessage(), form);
        }
        return UtilsLocaleHelper.localeTranslation(super.getMessage());
    }
}
