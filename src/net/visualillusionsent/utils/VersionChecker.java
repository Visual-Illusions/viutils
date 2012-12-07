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
import java.io.InputStreamReader;
import java.net.URL;

/**
 * Version Checker
 * <p>
 * Used to check if software is the latest version
 * <p>
 * This File is part of the VIUtils Java Software package (net.visualillusionsent.utils)<br>
 * &copy; 2012 <a href="http://visualillusionsent.net">Visual Illusions Entertainment</a>
 * 
 * @since VIUtils 1.0
 * @version 1.0
 * @author Jason (darkdiplomat)
 */
public final class VersionChecker {
    private String version, currver, checkurl;

    /**
     * class constructor
     * 
     * @param version
     *            A string representation of the software version
     * @param checkurl
     *            A string representation of the url to verify version though
     */
    public VersionChecker(String version, String checkurl) {
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
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(new URL(checkurl).openStream()));
            String inputLine;
            if ((inputLine = in.readLine()) != null) {
                currver = inputLine;
            }
            in.close();
            boolean is = true;
            String checkVer = version.replaceAll("\\.", "");
            String current = currver.replaceAll("\\.", "");
            try {
                if (checkVer.length() < current.length()) {
                    checkVer.concat("0");
                }
                is = Float.parseFloat(checkVer) >= Float.parseFloat(current);
            }
            catch (NumberFormatException nfe) {
                is = version.equals(currver);
            }
            return is;
        }
        catch (Exception E) {}
        return true;
    }

    /**
     * gets the current version
     * 
     * @return currver
     */
    public final String getCurrentVersion() {
        return currver;
    }
}
