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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * Version Checker
 * <p>
 * Used to check if software is the latest version<br>
 * There is an included versionchecker.php in the resources/extras/ folder inside the jar.
 * <p>
 * This File is part of the VIUtils Java Software package (net.visualillusionsent.utils)<br>
 * &copy; 2012 <a href="http://visualillusionsent.net">Visual Illusions Entertainment</a>
 * 
 * @since 1.0
 * @version 1.0
 * @author Jason (darkdiplomat)
 */
public final class VersionChecker {
    private String jarname, version, currver, checkurl;

    /**
     * class constructor
     * 
     * @param jarname
     *            the name of the jar being version checked
     * @param version
     *            A string representation of the software version
     * @param checkurl
     *            A string representation of the url to verify version though
     */
    public VersionChecker(String jarname, String version, String checkurl) {
        this.jarname = jarname;
        this.version = version;
        this.currver = version;
        this.checkurl = checkurl;
    }

    /**
     * checks if version is latest
     * 
     * @return true if latest, false otherwise
     */
    public final boolean isLatest() {
        BufferedReader in = null;
        boolean is = true;
        try {
            in = new BufferedReader(new InputStreamReader(new URL(checkurl).openStream()));
            String inputLine;
            if ((inputLine = in.readLine()) != null) {
                currver = inputLine;
            }
            String checkVer = version.replaceAll("\\.", "");
            String current = currver.replaceAll("\\.", "");
            try {
                if (checkVer.length() < current.length()) {
                    checkVer = StringUtils.padCharRight(checkVer, current.length(), '0');
                }
                else if (current.length() < checkVer.length()) {
                    current = StringUtils.padCharRight(current, checkVer.length(), '0');
                }
                is = Long.parseLong(checkVer) >= Long.parseLong(current);
            }
            catch (NumberFormatException nfe) {
                is = version.equals(currver);
            }
        }
        catch (Exception E) {}
        finally {
            if (in != null) {
                try {
                    in.close();
                }
                catch (IOException e) {}
            }
        }
        return is;
    }

    /**
     * Gets the current version
     * 
     * @return currver
     */
    public final String getCurrentVersion() {
        return currver;
    }

    /**
     * Gets a pre-generated update availible message
     * 
     * @return updateavailiblemessage
     */
    public final String getUpdateAvailibleMessage() {
        if (!isLatest()) {
            return "An update is availible for: '".concat(jarname).concat("' - v").concat(currver);
        }
        else {
            return "Current Version of: '".concat(jarname).concat("' is installed");
        }
    }
}
