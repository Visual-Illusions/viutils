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
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * File Utilities
 * <p>
 * Provides static methods to help with {@link File} manipulations
 * <p>
 * This File is part of the VIUtils<br>
 * &copy; 2012 <a href="http://visualillusionsent.net">Visual Illusions Entertainment</a>
 * 
 * @since VIUtils 1.0
 * @version 1.0
 * @author Jason (darkdiplomat)
 */
public final class FileUtils {

    /**
     * Removes a line from a {@link File}
     * 
     * @param filepath
     *            the path to the {@link File} to have a line removed
     * @param line
     *            the line to be removed
     * @throws UtilityException
     */
    public static final void removeLine(String filepath, String line) throws UtilityException {
        File file = new File(filepath);
        removeLines(file, line);
    }

    /**
     * Removes a line from a {@link File}
     * 
     * @param filepath
     *            the path to the {@link File} to have a line removed
     * @param lines
     *            the lines to be removed
     * @throws UtilityException
     */
    public static final void removeLines(String filepath, String... lines) throws UtilityException {
        File file = new File(filepath);
        removeLines(file, lines);
    }

    /**
     * Removes a line from a {@link File}
     * 
     * @param file
     *            the {@link File} to have a line removed
     * @param line
     *            the line to be removed
     * @throws UtilityException
     */
    public static final void removeLine(File file, String line) throws UtilityException {
        removeLines(file, line);
    }

    /**
     * Removes a line from a {@link File}
     * 
     * @param file
     *            the {@link File} to have lines removed
     * @param lines
     *            the lines to be removed
     * @throws UtilityException
     * <br>
     *             if the {@link File} is null or does not exist<br>
     *             if the {@link File} can not be read<br>
     *             if the {@link File} can not be written to<br>
     *             if lines are null or an empty array<br>
     *             if some other IOException occurrs
     */
    public static final void removeLines(File file, String... lines) throws UtilityException {
        if (file == null) {
            throw new UtilityException("arg.null", "File file");
        }
        else if (lines == null) {
            throw new UtilityException("arg.null", "String... lines");
        }
        else if (lines.length < 1) {
            throw new UtilityException("arg.empty", "String... lines");
        }
        UtilityException ue = null;
        BufferedReader breader = null;
        PrintWriter pwriter = null;

        //Is it a file?
        if (!file.isFile()) {
            throw new UtilityException("file.err.exist");
        }
        //Can we read the file?
        else if (!file.canRead()) {
            throw new UtilityException("file.err.read");
        }
        //Can we write to the file?
        else if (!file.canWrite()) {
            throw new UtilityException("file.err.write");
        }

        try {
            //Convert lines array into a list for easier parsing
            List<String> listlines = Arrays.asList(lines);

            //Store the lines in the file for later output
            List<String> outLines = new ArrayList<String>();

            //Prepare to read File
            breader = new BufferedReader(new FileReader(file));

            //Read from the original file and store the line
            //unless content matches data to be removed.
            String inLine = null;
            while ((inLine = breader.readLine()) != null) {
                if (!listlines.contains(inLine)) {
                    outLines.add(inLine);
                }
            }

            //Write out the lines that shall remain
            pwriter = new PrintWriter(new FileWriter(file, false));
            for (String outLine : outLines) {
                pwriter.println(outLine);
                pwriter.flush();
            }

        }
        catch (IOException ex) {
            ue = new UtilityException("file.ioe", file.getName());
        }
        finally {
            pwriter.close();
            try {
                breader.close();
            }
            catch (IOException e) {}
            if (ue != null) {
                throw ue;
            }
        }
    }
}
