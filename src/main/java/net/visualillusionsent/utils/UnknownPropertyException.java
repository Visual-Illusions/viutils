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
 * @author Jason (darkdiplomat)
 */
public final class UnknownPropertyException extends PropertiesFileException {

    /* 1.0 @ VIUtils 1.4.0 */
    private static final float classVersion = 1.0F;
    /**
     * Serial Version
     */
    private static final long serialVersionUID = 3122014080000L;

    public UnknownPropertyException(String key, String msg) {
        super(Verify.parse(key, msg));
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
