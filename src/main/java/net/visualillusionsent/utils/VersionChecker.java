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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * Version Checker
 * <p>
 * Used to check if software is the latest version<br>
 * There is an included versionchecker.php in the resources/extras/ folder inside the jar.<br>
 * All non-numerical digits are removed when checking a version.<br>
 * ie: #.#b# or SNAPSHOT-#.#.# becomes ###<br>
 * If the version lengths do not match, 0 is appened to the end for verifications.<br>
 * ie: Version = ## Current = ### Version becomes ##0
 * 
 * @since 1.0
 * @version 1.0
 * @author Jason (darkdiplomat)
 */
public final class VersionChecker {
    private final String jarname, checkurl, version;
    private String currver;
    private long lastCheck = 0L;
    private boolean isLatest = false;

    /**
     * Creates a new {@code VersionChecker}
     * 
     * @param jarname
     *            the name of the jar being version checked
     * @param version
     *            A {@link String} representation of the software version
     * @param checkurl
     *            A {@link String} representation of the url to verify version though
     */
    public VersionChecker(String jarname, String version, String checkurl) {
        this.jarname = jarname;
        this.version = version;
        this.currver = version;
        this.checkurl = checkurl;
    }

    /**
     * Checks if version is latest<br>
     * NOTE: Site queries are limited to once every 5 minutes.
     * 
     * @return {@code true} if latest; {@code false} otherwise
     */
    public final boolean isLatest() {
        BufferedReader in = null;
        long currentTime = System.currentTimeMillis();
        if ((lastCheck + 600000) > currentTime) { //If recently checked, reuse value rather than spam the site
            return isLatest;
        }

        try {
            String inputLine;
            in = new BufferedReader(new InputStreamReader(new URL(checkurl).openStream()));
            if ((inputLine = in.readLine()) != null) {
                currver = inputLine;
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
        lastCheck = currentTime;

        String checkVer = version.replaceAll("[^\\d]", "");
        String current = currver.replaceAll("[^\\d]", "");
        try {
            if (checkVer.length() < current.length()) {
                checkVer = StringUtils.padCharRight(checkVer, current.length(), '0');
            }
            else if (current.length() < checkVer.length()) {
                current = StringUtils.padCharRight(current, checkVer.length(), '0');
            }
            isLatest = Long.parseLong(checkVer) >= Long.parseLong(current);
        }
        catch (Exception e) {
            isLatest = version.equals(currver);
        }

        return isLatest;
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
     * @return update: An update is availible for: 'Jar' - v'Version'<br>
     *         latest: Current Version of: 'Jar' is installed
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
