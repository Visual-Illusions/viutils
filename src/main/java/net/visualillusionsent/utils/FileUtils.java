/*
 * This file is part of VIUtils.
 *
 * Copyright © 2012-2013 Visual Illusions Entertainment
 *
 * VIUtils is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * VIUtils is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with VIUtils.
 * If not, see http://www.gnu.org/licenses/lgpl.html.
 */
package net.visualillusionsent.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import static net.visualillusionsent.utils.Verify.notEmpty;
import static net.visualillusionsent.utils.Verify.notNull;

/**
 * Provides static methods to help with {@link File} manipulations
 *
 * @author Jason (darkdiplomat)
 * @version 1.2
 * @since 1.0
 */
public final class FileUtils {

    private static final float classVersion = 1.2F;

    /** This class should never be externally constructed */
    private FileUtils() {
    }

    /**
     * Removes a line from a {@link File}
     *
     * @param filePath
     *         the path to the {@link File} to have a line removed
     * @param line
     *         the line to be removed
     *
     * @throws UtilityException
     *         <br>
     *         if filePath is null or empty
     * @see #removeLines(File, String...)
     */
    public static final void removeLine(String filePath, String line) throws UtilityException {
        notNull(filePath, "String filePath");
        notEmpty(filePath, "String filePath");

        removeLines(new File(filePath), line);
    }

    /**
     * Removes a line from a {@link File}
     *
     * @param filePath
     *         the path to the {@link File} to have a line removed
     * @param lines
     *         the lines to be removed
     *
     * @throws UtilityException
     *         <br>
     *         if filepath is null or empty
     * @see #removeLines(File, String...)
     */
    public static final void removeLines(String filePath, String... lines) throws UtilityException {
        notNull(filePath, "String filePath");
        notEmpty(filePath, "String filePath");

        removeLines(new File(filePath), lines);
    }

    /**
     * Removes a line from a {@link File}
     *
     * @param file
     *         the {@link File} to have a line removed
     * @param line
     *         the line to be removed
     *
     * @throws UtilityException
     * @see #removeLines(File, String...)
     */
    public static final void removeLine(File file, String line) throws UtilityException {
        removeLines(file, line);
    }

    /**
     * Removes a line from a {@link File}
     *
     * @param file
     *         the {@link File} to have lines removed
     * @param lines
     *         the lines to be removed
     *
     * @throws UtilityException
     *         <br>
     *         if the {@link File} is null or does not exist<br>
     *         if the {@link File} can not be read<br>
     *         if the {@link File} can not be written to<br>
     *         if lines are null or an empty array<br>
     *         if some other IOException occurs
     */
    public static final void removeLines(File file, String... lines) throws UtilityException {
        notNull(file, "File file");
        notNull(lines, "String... lines");
        notEmpty(lines, "String... lines");

        if (!file.isFile()) {
            throw new UtilityException("file.err.exist", file.getName());
        }
        else if (!file.canRead()) {
            throw new UtilityException("file.err.read", file.getName());
        }
        else if (!file.canWrite()) {
            throw new UtilityException("file.err.write", file.getName());
        }

        UtilityException ue = null;
        BufferedReader bReader = null;
        PrintWriter pWriter = null;
        try {
            List<String> listLines = Arrays.asList(lines);
            List<String> outLines = new ArrayList<String>();
            bReader = new BufferedReader(new FileReader(file));
            String inLine;
            while ((inLine = bReader.readLine()) != null) {
                if (!listLines.contains(inLine)) {
                    outLines.add(inLine);
                }
            }
            pWriter = new PrintWriter(new FileWriter(file, false));
            for (String outLine : outLines) {
                pWriter.println(outLine);
                pWriter.flush();
            }
        }
        catch (IOException ex) {
            ue = new UtilityException("file.ioe", file.getName());
        }
        finally {
            pWriter.close();
            try {
                bReader.close();
            }
            catch (IOException e) {
            }
            if (ue != null) {
                throw ue;
            }
        }
    }

    /**
     * Clones a {@link File}
     *
     * @param toClone
     *         the file path to the {@link File} to clone
     * @param clone
     *         the file path to clone to
     *
     * @throws UtilityException
     *         <br>
     *         if toClone is null or empty<br>
     *         if clone is null or empty<br>
     * @see #cloneFile(File, File)
     */
    public static final void cloneFile(String toClone, String clone) throws UtilityException {
        notNull(toClone, "String toClone");
        notNull(clone, "String clone");
        notEmpty(toClone, "String toClone");
        notEmpty(clone, "String clone");

        cloneFile(new File(toClone), new File(clone));
    }

    /**
     * Clones a {@link File}
     *
     * @param toClone
     *         the {@link File} to clone
     * @param clone
     *         the file path to clone to
     *
     * @throws UtilityException
     *         <br>
     *         if toClone is null<br>
     *         if clone is null or empty
     * @see #cloneFile(File, File)
     */
    public static final void cloneFile(File toClone, String clone) throws UtilityException {
        notNull(toClone, "File toClone");
        notNull(clone, "String clone");
        notEmpty(clone, "String clone");

        cloneFile(toClone, new File(clone));
    }

    /**
     * Clones a {@link File}
     *
     * @param toClone
     *         the {@link File} to clone
     * @param clone
     *         the {@link File} to clone to
     *
     * @throws UtilityException
     *         <br>
     *         if toClone is null, does not exist, is not a file, or can not be read<br>
     *         if clone is null, path matches toClone, or is a directory<br>
     *         if some other IOException occurs
     */
    public static final void cloneFile(File toClone, File clone) throws UtilityException {
        notNull(toClone, "File toClone");
        notNull(clone, "File clone");

        if (clone.isDirectory()) {
            throw new UtilityException("file.err.dir", clone.getName());
        }
        else if (toClone.getPath().equals(clone.getPath())) {
            throw new UtilityException("file.err.path", toClone.getName(), clone.getName());
        }
        else if (!toClone.isFile()) {
            throw new UtilityException("file.err.exist", toClone.getName());
        }
        else if (!toClone.canRead()) {
            throw new UtilityException("file.err.read", toClone.getName());
        }
        else if (toClone.getAbsolutePath().equals(clone.getAbsolutePath())) {
            throw new UtilityException("file.err.path", toClone.getName(), clone.getName());
        }

        UtilityException ue = null;
        FileInputStream instream = null;
        FileOutputStream outstream = null;
        try {
            instream = new FileInputStream(toClone);
            outstream = new FileOutputStream(clone);
            int inByte;
            byte[] buffer = new byte[1024];
            while ((inByte = instream.read(buffer)) != -1) {
                outstream.write(buffer, 0, inByte);
                outstream.flush();
            }
        }
        catch (IOException ex) {
            ue = new UtilityException("file.err.ioe", clone.getName());
        }
        finally {
            try {
                instream.close();
                outstream.close();
            }
            catch (IOException e) {
            }
            if (ue != null) {
                throw ue;
            }
        }
    }

    /**
     * Clones a {@link File} from a {@link JarFile}
     *
     * @param jarPath
     *         the path to the {@link JarFile}
     * @param fileToMove
     *         the file to be cloned (ie: resources/README.txt)
     * @param pathTo
     *         the path to clone the file to
     *
     * @throws UtilityException
     *         <br>
     *         if an IOException occurs
     */
    public static final void cloneFileFromJar(String jarPath, String fileToMove, String pathTo) throws UtilityException {
        notNull(jarPath, "String jarPath");
        notNull(fileToMove, "String fileToMove");
        notNull(pathTo, "String pathTo");
        notEmpty(jarPath, "String jarPath");
        notEmpty(fileToMove, "String fileToMove");
        notEmpty(pathTo, "String pathTo");

        JarFile jar = null;
        FileOutputStream out = null;
        UtilityException toThrow = null;
        try {
            jar = new JarFile(jarPath);
            JarEntry entry = jar.getJarEntry(fileToMove);
            InputStream in = jar.getInputStream(entry);
            out = new FileOutputStream(pathTo);
            int readBytes;
            byte[] buffer = new byte[1024];
            while ((readBytes = in.read(buffer, 0, buffer.length)) != -1) {
                out.write(buffer, 0, readBytes);
            }
        }
        catch (IOException e) {
            toThrow = new UtilityException("Exception occurred while moving file from Jar...", e);
        }
        catch (NullPointerException npe) {
            toThrow = new UtilityException("The Jar did not contain the file to be moved...", npe);
        }
        finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (jar != null) {
                    jar.close();
                }
            }
            catch (IOException e) {
            }
            if (toThrow != null) {
                throw toThrow;
            }
        }
    }

    /**
     * Normalizes File paths to the OS Specific file separators (\ on Windows and / on Unix based systems)
     *
     * @param path
     *         the path to be normalized
     *
     * @return the normalized file path
     */
    public static final String normalizePath(String path) {
        notNull(path, "String path");
        notEmpty(path, "String path");

        if (SystemUtils.isWindows()) {
            return path.replace('/', '\\');
        }
        else {
            return path.replace('\\', '/');
        }
    }

    /**
     * Checks {@link InputStream}(s) check-sums for matching sums
     *
     * @param inStreamA
     *         the first {@link InputStream} to check
     * @param inStreamB
     *         the second {@link InputStream} to check
     * @param algorithm
     *         the algorithm to use with {@link MessageDigest} (MD5, SHA-1, and SHA-256 are required to be supported)
     *
     * @return {@code true} if the sums match; {@code false} if not
     *
     * @throws UtilityException
     *         if an exception occurs where a check-sum cannot be completed
     */
    public static final boolean checkSumMatch(InputStream inStreamA, InputStream inStreamB, String algorithm) throws UtilityException {
        notNull(inStreamA, "InputStream inStreamA");
        notNull(inStreamB, "InputStream inStreamB");
        notNull(algorithm, "String algorithm");
        notEmpty(algorithm, "String algorithm");

        byte[] digestLocal, digestJar;
        try {
            MessageDigest md = MessageDigest.getInstance(algorithm);
            DigestInputStream dis = new DigestInputStream(inStreamA, md);
            dis.read(new byte[dis.available()]);
            digestLocal = md.digest();
            dis = new DigestInputStream(inStreamB, md);
            dis.read(new byte[dis.available()]);
            digestJar = md.digest();
        }
        catch (Exception ex) {
            throw new UtilityException("InputStream checksum failure (Algorithm: " + algorithm + ")", ex);
        }

        return MessageDigest.isEqual(digestLocal, digestJar);
    }

    /**
     * Checks {@link InputStream} MD5 sums for matching sums
     *
     * @param inStreamA
     *         the first {@link InputStream} to check
     * @param inStreamB
     *         the second {@link InputStream} to check
     *
     * @throws UtilityException
     *         if an exception occurs where a check-sum cannot be completed
     * @return{@code true} if the sums match; {@code false} if not
     */
    public static final boolean md5SumMatch(InputStream inStreamA, InputStream inStreamB) throws UtilityException {
        return checkSumMatch(inStreamA, inStreamB, "MD5");
    }

    /**
     * Checks {@link InputStream} SHA-1 sums for matching sums
     *
     * @param inStreamA
     *         the first {@link InputStream} to check
     * @param inStreamB
     *         the second {@link InputStream} to check
     *
     * @throws UtilityException
     *         if an exception occurs where a check-sum cannot be completed
     * @return{@code true} if the sums match; {@code false} if not
     */
    public static final boolean sha1SumMatch(InputStream inStreamA, InputStream inStreamB) throws UtilityException {
        return checkSumMatch(inStreamA, inStreamB, "SHA-1");
    }

    /**
     * Checks {@link InputStream} SHA-256 sums for matching sums
     *
     * @param inStreamA
     *         the first {@link InputStream} to check
     * @param inStreamB
     *         the second {@link InputStream} to check
     *
     * @throws UtilityException
     *         if an exception occurs where a check-sum cannot be completed
     * @return{@code true} if the sums match; {@code false} if not
     */
    public static final boolean sha256SumMatch(InputStream inStreamA, InputStream inStreamB) throws UtilityException {
        return checkSumMatch(inStreamA, inStreamB, "SHA-256");
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
