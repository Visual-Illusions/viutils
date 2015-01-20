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

/**
 * Utility Exception
 * <p/>
 * Thrown when a used utility has improper arguments given to it or when something goes wrong<br>
 * use the {@link #getMessage()} or {@link #getLocalizedMessage()} method to retrieve the reason why the utility method failed
 *
 * @author Jason (darkdiplomat)
 * @version 1.1
 * @since 1.0.0
 */
public class UtilityException extends RuntimeException {

    /* 1.1 @ VIUtils 1.4.0 */
    private static final float classVersion = 1.1F;
    /** Serial Version */
    private static final long serialVersionUID = 20100242014L;

    /**
     * Class Constructor
     * <p/>
     * Should not be constructed outside of VIUtils
     *
     * @param msg
     *         the message of why the exception is being thrown
     */
    UtilityException(String msg) {
        super(msg);
    }

    UtilityException(String msg, Throwable thrown) {
        super(msg, thrown);
    }

    UtilityException(String error, String... form) {
        super(Verify.parse(error, form));
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
