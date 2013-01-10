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
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

/**
 * Version Checker
 * <p>
 * Used to check if software is the latest version<br>
 * There is an included versionchecker.php in the /resources/extras/ folder inside the jar.<br>
 * 
 * @since 1.0
 * @version 1.0
 * @author Jason (darkdiplomat)
 */
public final class VersionChecker {
    private final String programName, checkurl, user_agent;
    private final String post = "program=%s&version=%s&build=%s&isBeta=%b&isRC=%b";
    private final String formated_Post;
    private String currver;
    private long lastCheck = 0L;
    private boolean isLatest = false;

    /**
     * Creates a new {@code VersionChecker}<br>
     * In your access log you will see something like: Java/{version} ({OS.Version}; {JarName}/{Version}; VersionChecker/{Version}) VIUtils/{Version}<br>
     * 
     * @param programName
     *            the name of the Program being version checked
     * @param version
     *            A {@link String} representation of the software version
     * @param build
     *            A {@link String} representation of the build number
     * @param isBeta
     *            A {@code boolean} value of whether the program is a beta build
     * @param isRC
     *            A {@code boolean} value of whether the program is a release candidate build
     * @param checkurl
     *            A {@link String} representation of the url to verify version though (ie: http://visualillusionsent.net/testing/versionchecker.php)
     */
    public VersionChecker(String programName, String version, String build, String checkurl, boolean isBeta, boolean isRC) {
        this.programName = programName;
        this.currver = version;
        this.checkurl = checkurl;
        this.user_agent = "Java/" + SystemUtils.JAVA_VERSION + " (" + SystemUtils.SYSTEM_OS + "; " + programName + "/" + version + "; VersionChecker/1.0) VIUtils/1.0";
        this.formated_Post = String.format(post, programName, version, build, isBeta, isRC);
    }

    /**
     * Checks if version is latest<br>
     * NOTE: Site queries are limited to once every 5 minutes.
     * 
     * @return {@code true} if latest or on error; {@code false} otherwise
     */
    public final boolean isLatest() {
        BufferedReader in = null;
        OutputStreamWriter out = null;
        long currentTime = System.currentTimeMillis();
        if ((lastCheck + 600000) > currentTime) { //If recently checked, reuse value rather than spam the site
            return isLatest;
        }

        String inputLine = null;
        try {
            URL url = new URI(checkurl).toURL();
            HttpURLConnection huc = (HttpURLConnection) url.openConnection();
            HttpURLConnection.setFollowRedirects(true);
            huc.setConnectTimeout(2000);
            huc.setReadTimeout(2000);
            huc.setRequestMethod("POST");
            huc.setRequestProperty("User-Agent", user_agent);
            huc.setDoOutput(true);
            huc.setDoInput(true);
            huc.connect();
            out = new OutputStreamWriter(huc.getOutputStream());
            out.write(formated_Post);
            out.flush();
            in = new BufferedReader(new InputStreamReader(huc.getInputStream()));

            if ((inputLine = in.readLine()) != null) { /* Response Received */}
            lastCheck = currentTime;
        }
        catch (Exception ex) {}
        finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            }
            catch (IOException ioe) {}
        }
        if (inputLine == null || inputLine.startsWith("Error") || inputLine.equals("<br />")) {
            //some sort of error on the web side, ignore it for now
            return true;
        }
        else {
            String[] input = inputLine.split(":");
            isLatest = Boolean.parseBoolean(input[0]);
            if (input.length > 1) {
                currver = input[1];
            }
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
     * @return update: An update is availible for: 'ProgramName' - v'Version'<br>
     *         latest: Current Version of: 'ProgramName' is installed
     */
    public final String getUpdateAvailibleMessage() {
        if (!isLatest()) {
            return "An update is availible for: '".concat(programName).concat("' - v").concat(currver);
        }
        else {
            return "Current Version of: '".concat(programName).concat("' is installed");
        }
    }
}
