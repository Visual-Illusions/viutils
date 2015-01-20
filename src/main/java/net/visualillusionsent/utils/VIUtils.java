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
 * VIUtils
 * <p/>
 * Use this class to check the VIUtils version
 *
 * @author Jason (darkdiplomat)
 * @version 1.4.0
 * @since 1.0.4
 */
public final class VIUtils {

    static {
        String tempVer = "1.x.x";
        ProgramStatus tempStatus = ProgramStatus.UNKNOWN;
        try {
            PropertiesFile cfg = new PropertiesFile(JarUtils.getJarPath(VIUtils.class), "viutils/viutils.cfg");
            tempVer = cfg.getString("version");
            tempStatus = ProgramStatus.fromString(cfg.getString("status"));
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }

        VERSION = tempVer;
        STATUS = tempStatus;
    }

    /**
     * The version of this VIUtils
     */
    public static final String VERSION;

    /**
     * The {@link net.visualillusionsent.utils.ProgramStatus} of this VIUtils
     */
    public static final ProgramStatus STATUS;
}
