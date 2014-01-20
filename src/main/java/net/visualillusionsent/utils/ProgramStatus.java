/*
 * This file is part of VIUtils.
 *
 * Copyright © 2012-2014 Visual Illusions Entertainment
 *
 * VIUtils is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.
 * If not, see http://www.gnu.org/licenses/lgpl.html.
 */
package net.visualillusionsent.utils;

/**
 * Program Status helper enum
 * <p/>
 * Specifically used to tell VersionChecker the release status of the program.<br>
 * Can be used externally as a helpful way to state the same thing.
 *
 * @author Jason (darkdiplomat)
 * @version 1.1
 * @since 1.0.0
 */
public enum ProgramStatus {
    UNKNOWN, //
    ALPHA, //
    BETA, //
    SNAPSHOT, //
    RELEASE_CANDIDATE, //
    STABLE;

    private static final float classVersion = 1.1F;

    /** {@inheritDoc} */
    public final String toString() {
        if (this == RELEASE_CANDIDATE) {
            return name().replace("_", " ");
        }
        else {
            return name();
        }
    }

    /**
     * Safer replacement for {@link #valueOf(String)}
     *
     * @param status
     *         the name of the {@code ProgramStatus}
     *
     * @return the matching {@code ProgramStatus} if found; {@link #UNKNOWN} if not found
     */
    public static ProgramStatus fromString(String status) {
        if (status.toUpperCase().equals("RELEASE CANDIDATE")) {
            return valueOf("RELEASE_CANDIDATE");
        }
        try {
            return valueOf(status.toUpperCase());
        }
        catch (IllegalArgumentException iaex) {
            return UNKNOWN;
        }
    }

    /**
     * Gets this class's version number
     *
     * @return the class version
     */
    public static final float getClassVersion() {
        return classVersion;
    }
}
