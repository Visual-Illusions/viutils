/*
 * This file is part of ${name}.
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.MessageFormat;
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;

/**
 * @author Jason (darkdiplomat)
 */
public final class ProgramChecker {

    private static final float classVersion = 1.0F;
    private static final String progNamePreForm = "program=%s",
            versionForm = "%d.%d.%d",
            userAgentPreForm = MessageFormat.format("Java/{2} ({3} {4}; {5}; %s/%s;) ProgramChecker/{1,number,0.0} VIUtils/{0}",
                    VIUtils.VERSION,
                    classVersion,
                    SystemUtils.JAVA_VERSION,
                    SystemUtils.OPERATING_SYSTEM,
                    SystemUtils.OS_VERSION,
                    SystemUtils.OS_ARCHITECTURE
            );

    private final String progName, userAgent, postOut;
    private final URL extURL;
    private final long[] version;
    private final ProgramStatus status;

    private long lastCheck;
    private Long[] latestReported = new Long[]{ 0L, 0L, 0L };
    private ProgramStatus checkStatus = ProgramStatus.UNKNOWN;
    private Status checkResponse = Status.ERROR;
    private String errorMsg = "ERROR: No query made yet";
    private int connTimeOut = 500;
    private long queryInterval = TimeUnit.MINUTES.toMillis(5);

    public ProgramChecker(String progName, long verMajor, long verMinor, long verRev, URL extURL, ProgramStatus status) {
        this.progName = progName;
        this.version = new long[]{ verMajor, verMinor, verRev };
        this.extURL = extURL;
        this.status = status;
        this.userAgent = String.format(userAgentPreForm, progName, String.format(versionForm, verMajor, verMinor, verRev));
        this.postOut = String.format(progNamePreForm, progName);
    }

    public ProgramChecker(String progName, String version, String extURL, String status) throws Exception {
        this.progName = progName;
        this.version = parseVersionString(version);
        this.extURL = new URL(extURL);
        this.status = ProgramStatus.fromString(status);
        this.userAgent = String.format(userAgentPreForm, progName, String.format(versionForm, this.version[0], this.version[1], this.version[2]));
        this.postOut = String.format(progNamePreForm, progName);
    }

    private static long[] parseVersionString(String version) {
        long[] temp = new long[]{ 0, 0, 0 };

        StringTokenizer tokenizer = new StringTokenizer(version, ".");
        int index = 0;
        while (tokenizer.hasMoreTokens()) {
            temp[index++] = Long.parseLong(tokenizer.nextToken());
        }
        return temp;
    }

    public enum Status {
        LATEST,
        UPDATE,
        ERROR;
    }

    public final void setConnectionTimeOut(int timeOut) {
        this.connTimeOut = timeOut;
        if (connTimeOut < 0) {
            connTimeOut = 0;
        }
    }

    public final void setQueryInterval(long interval) {
        this.queryInterval = interval;
        if (this.queryInterval < TimeUnit.MINUTES.toMillis(1)) {
            queryInterval = TimeUnit.MINUTES.toMillis(1);
        }
    }

    /**
     * Parse the input from the PHP Script
     *
     * @return inputLine
     * PHP Script output line of versions/builds
     */
    private String getInput() {
        String received = "";
        HttpURLConnection httpConn = null;
        try {
            httpConn = getConnection();
            httpConn.connect();

            httpConn.getOutputStream().write(postOut.getBytes());
            httpConn.getOutputStream().flush();

            BufferedReader in = new BufferedReader(new InputStreamReader(httpConn.getInputStream()));
            String temp;
            while ((temp = in.readLine()) != null) {
                received += temp;
            }
        }
        catch (Exception ex) {
            received = "ERROR: " + ex.getMessage();
        }
        finally {
            if (httpConn != null) {
                httpConn.disconnect();
            }
        }
        if (received.isEmpty()) {
            received = null;
        }
        return received;
    }

    private HttpURLConnection getConnection() throws IOException {
        HttpURLConnection httpConn = (HttpURLConnection) extURL.openConnection();
        httpConn.setConnectTimeout(connTimeOut);
        httpConn.setReadTimeout(connTimeOut);
        httpConn.setRequestMethod("POST");
        httpConn.setRequestProperty("User-Agent", userAgent);
        httpConn.setDoOutput(true);

        return httpConn;
    }

    private void parseInput(String input) {
        /* {"VERSION":{"MAJOR":"#","MINOR":"#","MICRO":"#"},"STATUS":"$"} */

        if (input.matches("\\{\"VERSION\":\\{\"MAJOR\":\"\\d+\",\"MINOR\":\"\\d+\",\"REVISION\":\"\\d+\"\\},\"STATUS\":\"\\w+\"\\}")) {
            long versionMajor, versionMinor, versionRev;
            ProgramStatus status;

            StringTokenizer tokenizer = new StringTokenizer(input, "{\":,}");
            try {
                tokenizer.nextToken(); // VERSION
                tokenizer.nextToken(); // MAJOR
                versionMajor = Long.parseLong(tokenizer.nextToken()); // DIGIT for MAJOR
                tokenizer.nextToken(); // MINOR
                versionMinor = Long.parseLong(tokenizer.nextToken()); // DIGIT for MINOR
                tokenizer.nextToken(); // REVISION
                versionRev = Long.parseLong(tokenizer.nextToken()); // DIGIT for REVISION
                tokenizer.nextToken(); // STATUS
                status = ProgramStatus.fromString(tokenizer.nextToken()); // STATUS name
            }
            catch (NumberFormatException nfex) {
                //This probably won't happen given the regex works as intended
                checkResponse = Status.ERROR;
                errorMsg = "ERROR: Invalid Response received - Bad Version Numbers";
                return;
            }

            this.latestReported = new Long[]{ versionMajor, versionMinor, versionRev };
            this.checkResponse = Status.UPDATE; // Assume an UPDATE is required

            if (this.version[0] > versionMajor) {
                this.checkResponse = Status.LATEST;
            }
            else if (this.version[0] == versionMajor) {
                if (this.version[1] > versionMinor) {
                    this.checkResponse = Status.LATEST;
                }
                else if (this.version[1] == versionMinor) {
                    if (this.version[2] > versionRev) {
                        this.checkResponse = Status.LATEST;
                    }
                    else if (this.version[2] == versionRev) {
                        if (this.status.ordinal() >= status.ordinal()) {
                            this.checkResponse = Status.LATEST;
                        }
                    }
                }
            }
        }
        else {
            checkResponse = Status.ERROR;
            errorMsg = "ERROR: Invalid Response received - Bad Syntax";
        }
    }


    public Status isLatest() {
        long currentTime = System.currentTimeMillis();
        if ((lastCheck + queryInterval) <= currentTime) {
            lastCheck = currentTime;
            String response = getInput();
            if (response.startsWith("ERROR:") || response.startsWith("Fatal")) {
                checkResponse = Status.ERROR;
                errorMsg = response;
            }
            else {
                parseInput(response);
            }
        }
        return checkResponse;
    }

    /**
     * Gets the Version reported as Latest in String form
     *
     * @return {@link String} representation of the latest version
     */
    public final String getVersionReported() {
        return String.format(versionForm, latestReported);
    }

    /**
     * Gets the Version reported as Latest as a {@code Long array}<br/>
     * Index 0 = Major, 1 = Minor, 2 = Micro
     *
     * @return {@code Long array} representation of the latest version
     */
    public final Long[] getVersionReport() {
        return latestReported.clone();
    }

    /**
     * Gets the {@link net.visualillusionsent.utils.ProgramStatus} reported as latest
     *
     * @return {@link net.visualillusionsent.utils.ProgramStatus} reported
     */
    public final ProgramStatus getStatusReport() {
        return checkStatus;
    }

    /**
     * Gets a pre-generated status message
     *
     * @return Status == UPDATE: An update is available for: 'ProgramName' - v'Version' 'Status'<br/>
     * Status == LATEST: Current Version of: 'ProgramName' is installed<br/>
     * Status == ERROR: {error message}
     */
    public final String getStatusMessage() {
        Status status = isLatest();
        switch (status) {
            case UPDATE:
                return "An update is available for: '".concat(progName).concat("' - v").concat(getVersionReported()).concat(" ".concat(checkStatus.toString()));
            case LATEST:
                return "Current Version of: '".concat(progName).concat("' is installed");
            default:
                return errorMsg;
        }
    }

    /**
     * Gets this class's version number
     *
     * @return the class version
     */
    public final static float getClassVersion() {
        return classVersion;
    }
}
