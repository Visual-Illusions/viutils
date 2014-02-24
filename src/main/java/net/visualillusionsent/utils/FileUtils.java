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
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this library.
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
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import static net.visualillusionsent.utils.Verify.FileAction.EXISTS;
import static net.visualillusionsent.utils.Verify.FileAction.ISFILE;
import static net.visualillusionsent.utils.Verify.FileAction.NOTDIRECTORY;
import static net.visualillusionsent.utils.Verify.FileAction.READ;
import static net.visualillusionsent.utils.Verify.FileAction.WRITE;
import static net.visualillusionsent.utils.Verify.fileCheck;
import static net.visualillusionsent.utils.Verify.notEmpty;
import static net.visualillusionsent.utils.Verify.notNull;

/**
 * Provides static methods to help with {@link File} manipulations
 *
 * @author Jason (darkdiplomat)
 * @version 1.3
 * @since 1.0
 */
public final class FileUtils {
    /* 1.3 @ VIUtils 1.4.0 */
    private static final float classVersion = 1.3F;

    /** This class should never be externally constructed */
    private FileUtils() {
    }

    public enum FileSignatures {
        JAVA_CLASS("0xCA,0xFE,0xBA,0xBE"), //
        JAVA_PACK200("0xCA,0xFE,0xD0,0x0D"), //
        PNG("0x89,0x50,0x4E,0x47,0x0D,0x0A,0x1A,0x0A"), //
        ICO("0x00,0x00,0x01,0x00"), //
        GIF_87A("0x47,0x49,0x46,0x38,0x37,0x61"), //
        GIF_89A("0x47,0x49,0x46,0x38,0x39,0x61"), //
        JPEG("0xFF,0xD8,0xFF"), //
        ;

        private final byte[] signature;

        private FileSignatures(String signature) {
            this.signature = StringUtils.stringToByteArray(signature);
        }

        public byte[] getSignature() {
            return signature;
        }

    }

    /**
     * Removes a line from a {@link File}
     *
     * @param filePath
     *         the path to the {@link File} to have a line removed
     * @param line
     *         the line to be removed
     *
     * @throws java.lang.NullPointerException
     *         if {@code filePath} or {@code line} is null
     * @throws java.lang.IllegalArgumentException
     *         if {@code filePath} or {@code line} is empty
     * @throws java.io.IOException
     *         if a read/write error occurs
     * @see #removeLines(File, String...)
     */
    public static void removeLine(String filePath, String line) throws IOException {
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
     *         * @throws java.lang.NullPointerException
     *         if filePath is null or lines are null
     *
     * @throws java.lang.NullPointerException
     *         if {@code filePath} or {@code lines} is null
     * @throws java.lang.IllegalArgumentException
     *         if {@code filePath} or {@code lines} is empty
     * @throws java.io.IOException
     *         if a read/write error occurs
     * @see #removeLines(File, String...)
     */
    public static void removeLines(String filePath, String... lines) throws IOException {
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
     * @throws java.lang.NullPointerException
     *         if {@code file} or {@code line} is null
     * @throws java.lang.IllegalArgumentException
     *         if {@code line} is empty
     * @throws java.io.IOException
     *         if a read/write error occurs
     * @see #removeLines(File, String...)
     */
    public static void removeLine(File file, String line) throws IOException {
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
     * @throws java.lang.NullPointerException
     *         if {@code file} or {@code lines} is null
     * @throws java.lang.IllegalArgumentException
     *         if {@code lines} is empty
     * @throws java.io.IOException
     *         if a read/write error occurs
     */
    public static void removeLines(File file, String... lines) throws IOException {
        notNull(file, "File file");
        notNull(lines, "String... lines");
        notEmpty(lines, "String... lines");
        fileCheck(file, EXISTS);
        fileCheck(file, READ);
        fileCheck(file, WRITE);

        IOException ioexThrown = null;
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
        catch (IOException ioex) {
            ioexThrown = ioex;
        }
        finally {
            if (pWriter != null)
                pWriter.close();
            try {
                if (bReader != null)
                    bReader.close();
            }
            catch (IOException e) {
                //IGNORED
            }
        }
        if (ioexThrown != null) {
            throw ioexThrown;
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
     * @throws java.io.IOException
     *         if a read/write error occurs
     * @throws java.lang.NullPointerException
     *         if {@code toClone} or {@code clone} is null
     * @throws java.lang.IllegalArgumentException
     *         if {@code toClone} is not a file, if {@code clone} is a directory, or if {@code toClone}'s path is equal to {@code clone}'s path
     * @see #cloneFile(File, File)
     */
    public static void cloneFile(String toClone, String clone) throws IOException {
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
     * @throws java.io.IOException
     *         if a read/write error occurs
     * @throws java.lang.NullPointerException
     *         if {@code toClone} or {@code clone} is null
     * @throws java.lang.IllegalArgumentException
     *         if {@code toClone} is not a file, if {@code clone} is a directory, or if {@code toClone}'s path is equal to {@code clone}'s path
     * @see #cloneFile(File, File)
     */
    public static void cloneFile(File toClone, String clone) throws IOException {
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
     * @throws java.lang.NullPointerException
     *         if {@code toClone} or {@code clone} is null
     * @throws java.lang.IllegalArgumentException
     *         if {@code toClone} is not a file, if {@code clone} is a directory, or if {@code toClone}'s path is equal to {@code clone}'s path
     * @throws java.io.IOException
     *         if a read/write error occurs
     */
    public static void cloneFile(File toClone, File clone) throws IOException {
        notNull(toClone, "File toClone");
        notNull(clone, "File clone");
        fileCheck(clone, NOTDIRECTORY); // If its a Directory, Error
        fileCheck(toClone, ISFILE); // If its not a file, Error
        fileCheck(toClone, READ); // If can't read, Error

        if (toClone.getPath().equals(clone.getPath())) {
            throw new UtilityException("file.err.path", toClone.getName(), clone.getName());
        }
        else if (toClone.getAbsolutePath().equals(clone.getAbsolutePath())) {
            throw new UtilityException("file.err.path", toClone.getName(), clone.getName());
        }

        IOException ioexThrown = null;
        FileInputStream instream = null;
        FileOutputStream outstream = null;
        try {
            instream = new FileInputStream(toClone);
            outstream = new FileOutputStream(clone);
            ReadableByteChannel rbc = Channels.newChannel(instream);
            outstream.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        }
        catch (IOException ex) {
            ioexThrown = ex;
        }
        finally {
            try {
                if (instream != null)
                    instream.close();
                if (outstream != null)
                    outstream.close();
            }
            catch (IOException e) {
                // IGNORED
            }

        }
        if (ioexThrown != null) {
            throw ioexThrown;
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
     * @throws java.lang.NullPointerException
     *         if {@code jarPath} or {@code fileToMove} or {@code pathTo} is null
     * @throws java.lang.IllegalArgumentException
     *         if {@code jarPath} or {@code fileToMove} or {@code pathTo} is empty
     * @throws java.io.IOException
     *         if a read/write error occurs
     */
    public static void cloneFileFromJar(String jarPath, String fileToMove, String pathTo) throws IOException {
        notNull(jarPath, "String jarPath");
        notNull(fileToMove, "String fileToMove");
        notNull(pathTo, "String pathTo");
        notEmpty(jarPath, "String jarPath");
        notEmpty(fileToMove, "String fileToMove");
        notEmpty(pathTo, "String pathTo");

        JarFile jar = null;
        FileOutputStream out = null;
        IOException exThrown = null;
        try {
            jar = new JarFile(jarPath);
            JarEntry entry = jar.getJarEntry(fileToMove);

            InputStream in = jar.getInputStream(entry);
            out = new FileOutputStream(pathTo);
            ReadableByteChannel rbc = Channels.newChannel(in);
            out.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        }
        catch (IOException ioex) {
            exThrown = ioex;
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
                // IGNORED
            }
        }
        if (exThrown != null) {
            throw exThrown;
        }
    }

    /**
     * Normalizes File paths to the OS Specific file separators (\ on Windows and / on Unix(-like) based systems)
     *
     * @param path
     *         the path to be normalized
     *
     * @return the normalized file path
     *
     * @throws java.lang.NullPointerException
     *         if {@code path} is null
     * @throws java.lang.IllegalArgumentException
     *         if {@code path} is empty
     */
    public static String normalizePath(String path) {
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
     * Checks file path files' checksums for matching sums
     *
     * @param filePathA
     *         the first file path to check
     * @param filePathB
     *         the second file path to check
     * @param algorithm
     *         the algorithm to use
     *
     * @return {@code true} if the sums match; {@code false} if not
     *
     * @throws java.lang.NullPointerException
     *         if {@code filePathA} or {@code filePathB} or {@code algorithm} is null
     * @throws java.lang.IllegalArgumentException
     *         if {@code filePathA} or {@code filePathB} or {@code algorithm} is empty
     * @throws java.io.IOException
     *         if an Input/Output exception occurs
     * @throws java.security.NoSuchAlgorithmException
     *         if specified algorithm is not supported
     */
    public static boolean checkSumMatch(String filePathA, String filePathB, String algorithm) throws IOException, NoSuchAlgorithmException {
        notNull(filePathA, "String filePathA");
        notNull(filePathB, "String filePathB");
        notEmpty(filePathA, "String filePathA");
        notEmpty(filePathB, "String filePathB");

        return checkSumMatch(new File(filePathA), new File(filePathB), algorithm);
    }

    /**
     * Checks file path files' MD5 sums for matching sums
     *
     * @param filePathA
     *         the first file path to check
     * @param filePathB
     *         the second file path to check
     *
     * @return {@code true} if the sums match; {@code false} if not
     *
     * @throws java.lang.NullPointerException
     *         if {@code filePathA} or {@code filePathB} is null
     * @throws java.lang.IllegalArgumentException
     *         if {@code filePathA} or {@code filePathB} is empty
     * @throws java.io.IOException
     *         if an Input/Output exception occurs
     * @throws net.visualillusionsent.utils.UtilityException
     *         if the MD5 Algorithm is unsupported by the Runtime Environment
     */
    public static boolean md5SumMatch(String filePathA, String filePathB) throws IOException {
        try {
            return checkSumMatch(filePathA, filePathB, "MD5");
        }
        catch (NoSuchAlgorithmException nsaex) {
            throw new UtilityException("sum.fail", "MD5");
        }
    }

    /**
     * Checks file path files' SHA-1 sums for matching sums
     *
     * @param filePathA
     *         the first file path to check
     * @param filePathB
     *         the second file path to check
     *
     * @return {@code true} if the sums match; {@code false} if not
     *
     * @throws java.lang.NullPointerException
     *         if {@code filePathA} or {@code filePathB} is null
     * @throws java.lang.IllegalArgumentException
     *         if {@code filePathA} or {@code filePathB} is empty
     * @throws java.io.IOException
     *         if an Input/Output exception occurs
     * @throws net.visualillusionsent.utils.UtilityException
     *         if the SHA-1 Algorithm is unsupported by the Runtime Environment
     */
    public static boolean sha1SumMatch(String filePathA, String filePathB) throws IOException {
        try {
            return checkSumMatch(filePathA, filePathB, "SHA-1");
        }
        catch (NoSuchAlgorithmException e) {
            throw new UtilityException("sum.fail", "SHA-1");
        }
    }

    /**
     * Checks file path files' SHA-256 sums for matching sums
     *
     * @param filePathA
     *         the first file path to check
     * @param filePathB
     *         the second file path to check
     *
     * @return {@code true} if the sums match; {@code false} if not
     *
     * @throws java.lang.NullPointerException
     *         if {@code filePathA} or {@code filePathB} is null
     * @throws java.lang.IllegalArgumentException
     *         if {@code filePathA} or {@code filePathB} is empty
     * @throws java.io.IOException
     *         if an Input/Output exception occurs
     * @throws net.visualillusionsent.utils.UtilityException
     *         if the SHA-256 Algorithm is unsupported by the Runtime Environment
     */
    public static boolean sha256SumMatch(String filePathA, String filePathB) throws IOException {
        try {
            return checkSumMatch(filePathA, filePathB, "SHA-256");
        }
        catch (NoSuchAlgorithmException e) {
            throw new UtilityException("sum.fail", "SHA-256");
        }
    }

    /**
     * Checks files' checksums for matching sums
     *
     * @param fileA
     *         the first file to check
     * @param fileB
     *         the second file to check
     * @param algorithm
     *         the algorithm to use
     *
     * @return {@code true} if the sums match; {@code false} if not
     *
     * @throws java.lang.NullPointerException
     *         if {@code fileA} or {@code fileB} or {@code algorithm} is null
     * @throws java.lang.IllegalArgumentException
     *         if {@code algorithm} is empty
     * @throws java.io.IOException
     *         if an Input/Output exception occurs
     * @throws java.security.NoSuchAlgorithmException
     *         if specified algorithm is not supported
     */
    public static boolean checkSumMatch(File fileA, File fileB, String algorithm) throws IOException, NoSuchAlgorithmException {
        notNull(fileA, "File fileA");
        notNull(fileB, "File fileB");

        return checkSumMatch(new FileInputStream(fileA), new FileInputStream(fileB), algorithm);
    }

    /**
     * Checks files' MD5 sums for matching sums
     *
     * @param fileA
     *         the first file to check
     * @param fileB
     *         the second file to check
     *
     * @return {@code true} if the sums match; {@code false} if not
     *
     * @throws java.lang.NullPointerException
     *         if {@code fileA} or {@code fileB} is null
     * @throws java.lang.IllegalArgumentException
     *         if {@code algorithm} is empty
     * @throws java.io.IOException
     *         if an Input/Output exception occurs
     * @throws net.visualillusionsent.utils.UtilityException
     *         if the MD5 Algorithm is unsupported by the Runtime Environment
     */
    public static boolean md5SumMatch(File fileA, File fileB) throws IOException {
        try {
            return checkSumMatch(fileA, fileB, "MD5");
        }
        catch (NoSuchAlgorithmException e) {
            throw new UtilityException("sum.fail", "MD5");
        }
    }

    /**
     * Checks files' SHA-1 sums for matching sums
     *
     * @param fileA
     *         the first file to check
     * @param fileB
     *         the second file to check
     *
     * @return {@code true} if the sums match; {@code false} if not
     *
     * @throws java.lang.NullPointerException
     *         if {@code fileA} or {@code fileB} is null
     * @throws java.lang.IllegalArgumentException
     *         if {@code algorithm} is empty
     * @throws java.io.IOException
     *         if an Input/Output exception occurs
     * @throws net.visualillusionsent.utils.UtilityException
     *         if the SHA-1 Algorithm is unsupported by the Runtime Environment
     */
    public static boolean sha1SumMatch(File fileA, File fileB) throws IOException {
        try {
            return checkSumMatch(fileA, fileB, "SHA-1");
        }
        catch (NoSuchAlgorithmException e) {
            throw new UtilityException("sum.fail", "SHA-1");
        }
    }

    /**
     * Checks files' SHA-256 sums for matching sums
     *
     * @param fileA
     *         the first file to check
     * @param fileB
     *         the second file to check
     *
     * @return {@code true} if the sums match; {@code false} if not
     *
     * @throws java.lang.NullPointerException
     *         if {@code fileA} or {@code fileB} is null
     * @throws java.lang.IllegalArgumentException
     *         if {@code algorithm} is empty
     * @throws java.io.IOException
     *         if an Input/Output exception occurs
     * @throws net.visualillusionsent.utils.UtilityException
     *         if the SHA-256 Algorithm is unsupported by the Runtime Environment
     */
    public static boolean sha256SumMatch(File fileA, File fileB) throws IOException {
        try {
            return checkSumMatch(fileA, fileB, "SHA-256");
        }
        catch (NoSuchAlgorithmException e) {
            throw new UtilityException("sum.fail", "SHA-256");
        }
    }

    /**
     * Checks {@link InputStream}(s) checksums for matching sums
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
     * @throws java.lang.NullPointerException
     *         if {@code inStreamA} or {@code inStreamB} or {@code algorithm} is null
     * @throws java.lang.IllegalArgumentException
     *         if {@code algorithm} is empty
     * @throws java.io.IOException
     *         if an Input/Output exception occurs
     * @throws java.security.NoSuchAlgorithmException
     *         if specified algorithm is not supported
     */
    public static boolean checkSumMatch(InputStream inStreamA, InputStream inStreamB, String algorithm) throws IOException, NoSuchAlgorithmException {
        notNull(inStreamA, "InputStream inStreamA");
        notNull(inStreamB, "InputStream inStreamB");
        notNull(algorithm, "String algorithm");
        notEmpty(algorithm, "String algorithm");

        byte[] digestLocal, digestJar;
        MessageDigest md = MessageDigest.getInstance(algorithm);
        DigestInputStream dis = new DigestInputStream(inStreamA, md);
        dis.read(new byte[dis.available()]);
        digestLocal = md.digest();
        dis = new DigestInputStream(inStreamB, md);
        dis.read(new byte[dis.available()]);
        digestJar = md.digest();

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
     * @return {@code true} if the sums match; {@code false} if not
     *
     * @throws java.lang.NullPointerException
     *         if {@code inStreamA} or {@code inStreamB} is null
     * @throws java.io.IOException
     *         if an Input/Output exception occurs
     * @throws net.visualillusionsent.utils.UtilityException
     *         if the MD5 Algorithm is unsupported by the Runtime Environment
     */
    public static boolean md5SumMatch(InputStream inStreamA, InputStream inStreamB) throws IOException {
        try {
            return checkSumMatch(inStreamA, inStreamB, "MD5");
        }
        catch (NoSuchAlgorithmException e) {
            throw new UtilityException("sum.fail", "MD5");
        }
    }

    /**
     * Checks {@link InputStream} SHA-1 sums for matching sums
     *
     * @param inStreamA
     *         the first {@link InputStream} to check
     * @param inStreamB
     *         the second {@link InputStream} to check
     *
     * @return {@code true} if the sums match; {@code false} if not
     *
     * @throws java.lang.NullPointerException
     *         if {@code inStreamA} or {@code inStreamB} is null
     * @throws java.io.IOException
     *         if an Input/Output exception occurs
     * @throws net.visualillusionsent.utils.UtilityException
     *         if the SHA-1 Algorithm is unsupported by the Runtime Environment
     */
    public static boolean sha1SumMatch(InputStream inStreamA, InputStream inStreamB) throws IOException {
        try {
            return checkSumMatch(inStreamA, inStreamB, "SHA-1");
        }
        catch (NoSuchAlgorithmException e) {
            throw new UtilityException("sum.fail", "SHA-1");
        }
    }

    /**
     * Checks {@link InputStream} SHA-256 sums for matching sums
     *
     * @param inStreamA
     *         the first {@link InputStream} to check
     * @param inStreamB
     *         the second {@link InputStream} to check
     *
     * @return {@code true} if the sums match; {@code false} if not
     *
     * @throws java.lang.NullPointerException
     *         if {@code inStreamA} or {@code inStreamB} is null
     * @throws java.io.IOException
     *         if an Input/Output exception occurs
     * @throws net.visualillusionsent.utils.UtilityException
     *         if the SHA-256 Algorithm is unsupported by the Runtime Environment
     */
    public static boolean sha256SumMatch(InputStream inStreamA, InputStream inStreamB) throws IOException {
        try {
            return checkSumMatch(inStreamA, inStreamB, "SHA-256");
        }
        catch (NoSuchAlgorithmException e) {
            throw new UtilityException("sum.fail", "SHA-256");
        }
    }

    /**
     * Gets the checksum for a file at the specified path using the specified algorithm
     *
     * @param filePath
     *         the file path to the file to checksum
     * @param algorithm
     *         the algorithm to use
     *
     * @return the byte array check-sum
     *
     * @throws java.lang.NullPointerException
     *         if {@code filePath} or {@code algorithm} is null
     * @throws java.lang.IllegalArgumentException
     *         if {@code filePath} or {@code algorithm} is empty
     * @throws java.io.IOException
     *         if an Input/Output exception occurs
     * @throws java.security.NoSuchAlgorithmException
     *         if specified algorithm is not supported
     */
    public static byte[] checkSum(String filePath, String algorithm) throws IOException, NoSuchAlgorithmException {
        notNull(filePath, "String filePath");
        notEmpty(filePath, "String filePath");

        return checkSum(new File(filePath), algorithm);
    }

    /**
     * Gets a MD5 sum of a file at the specified path
     *
     * @param filePath
     *         the file path to the file to MD5 sum
     *
     * @return the MD5 sum
     *
     * @throws java.lang.NullPointerException
     *         if {@code filePath} is null
     * @throws java.lang.IllegalArgumentException
     *         if {@code filePath} is empty
     * @throws java.io.IOException
     *         if an Input/Output exception occurs
     * @throws net.visualillusionsent.utils.UtilityException
     *         if the MD5 Algorithm is unsupported by the Runtime Environment
     */
    public static byte[] md5sum(String filePath) throws IOException {
        try {
            return checkSum(filePath, "MD5");
        }
        catch (NoSuchAlgorithmException e) {
            throw new UtilityException("sum.fail", "MD5");
        }
    }

    /**
     * Gets a SHA-1 sum of a file at the specified path
     *
     * @param filePath
     *         the file path to the file to SHA-1 sum
     *
     * @return the SHA-1 sum
     *
     * @throws java.lang.NullPointerException
     *         if {@code filePath} is null
     * @throws java.lang.IllegalArgumentException
     *         if {@code filePath} is empty
     * @throws java.io.IOException
     *         if an Input/Output exception occurs
     * @throws net.visualillusionsent.utils.UtilityException
     *         if the SHA-1 Algorithm is unsupported by the Runtime Environment
     */
    public static byte[] sha1sum(String filePath) throws IOException {
        try {
            return checkSum(filePath, "SHA-1");
        }
        catch (NoSuchAlgorithmException e) {
            throw new UtilityException("sum.fail", "SHA-1");
        }
    }

    /**
     * Gets a SHA-256 sum of a file at the specified path
     *
     * @param filePath
     *         the file path to the file to SHA-256 sum
     *
     * @return the SHA-256 sum
     *
     * @throws java.lang.NullPointerException
     *         if {@code filePath} is null
     * @throws java.lang.IllegalArgumentException
     *         if {@code filePath} is empty
     * @throws java.io.IOException
     *         if an Input/Output exception occurs
     * @throws net.visualillusionsent.utils.UtilityException
     *         if the SHA-256 Algorithm is unsupported by the Runtime Environment
     */
    public static byte[] sha256sum(String filePath) throws IOException {
        try {
            return checkSum(filePath, "SHA-256");
        }
        catch (NoSuchAlgorithmException e) {
            throw new UtilityException("sum.fail", "SHA-256");
        }
    }

    /**
     * Gets a checksum of the specified file
     *
     * @param file
     *         the file to checksum
     *
     * @return the checksum
     *
     * @throws java.lang.NullPointerException
     *         if {@code file} is null
     * @throws java.lang.IllegalArgumentException
     *         if {@code algorithm} is empty
     * @throws java.io.IOException
     *         if an Input/Output exception occurs
     * @throws java.security.NoSuchAlgorithmException
     *         if specified algorithm is not supported
     */
    public static byte[] checkSum(File file, String algorithm) throws IOException, NoSuchAlgorithmException {
        notNull(file, "File file");

        return checkSum(new FileInputStream(file), algorithm);
    }

    /**
     * Gets a MD5 sum of the specified file
     *
     * @param file
     *         the file to MD5 sum
     *
     * @return the MD5 sum
     *
     * @throws java.lang.NullPointerException
     *         if {@code file} is null
     * @throws java.io.IOException
     *         if an Input/Output exception occurs
     * @throws net.visualillusionsent.utils.UtilityException
     *         if the MD5 Algorithm is unsupported by the Runtime Environment
     */
    public static byte[] md5sum(File file) throws IOException {
        try {
            return checkSum(file, "MD5");
        }
        catch (NoSuchAlgorithmException e) {
            throw new UtilityException("sum.fail", "MD5");
        }
    }

    /**
     * Gets a SHA-1 sum of the specified file
     *
     * @param file
     *         the file to SHA-1 sum
     *
     * @return the SHA-1 sum
     *
     * @throws java.lang.NullPointerException
     *         if {@code file} is null
     * @throws java.io.IOException
     *         if an Input/Output exception occurs
     * @throws net.visualillusionsent.utils.UtilityException
     *         if the SHA-1 Algorithm is unsupported by the Runtime Environment
     */
    public static byte[] sha1sum(File file) throws IOException {
        try {
            return checkSum(file, "SHA-1");
        }
        catch (NoSuchAlgorithmException e) {
            throw new UtilityException("sum.fail", "SHA-1");
        }
    }

    /**
     * Gets a SHA-256 sum of the specified file
     *
     * @param file
     *         the file to SHA-256 sum
     *
     * @return the SHA-256 sum
     *
     * @throws java.lang.NullPointerException
     *         if {@code file} is null
     * @throws java.io.IOException
     *         if an Input/Output exception occurs
     * @throws net.visualillusionsent.utils.UtilityException
     *         if the SHA-256 Algorithm is unsupported by the Runtime Environment
     */
    public static byte[] sha256sum(File file) throws IOException {
        try {
            return checkSum(file, "SHA-256");
        }
        catch (NoSuchAlgorithmException e) {
            throw new UtilityException("sum.fail", "SHA-256");
        }
    }

    /**
     * Gets the checksum of an {@link InputStream}
     *
     * @param inStream
     *         the {@link InputStream} to checksum
     * @param algorithm
     *         the algorithm to use
     *
     * @return the checksum
     *
     * @throws java.lang.NullPointerException
     *         if {@code inStream} or {@code algorithm} is null
     * @throws java.lang.IllegalArgumentException
     *         if {@code algorithm} is empty
     * @throws java.io.IOException
     *         if an Input/Output exception occurs
     */
    public static byte[] checkSum(InputStream inStream, String algorithm) throws IOException, NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance(algorithm);
        DigestInputStream dis = new DigestInputStream(inStream, md);
        dis.read(new byte[dis.available()]);
        return md.digest();
    }

    /**
     * Gets the MD5 sum of an {@link InputStream}
     *
     * @param inStream
     *         the {@link InputStream} to MD5 sum
     *
     * @return the MD5 sum
     *
     * @throws java.lang.NullPointerException
     *         if {@code inStream} is null
     * @throws java.io.IOException
     *         if an Input/Output exception occurs
     * @throws net.visualillusionsent.utils.UtilityException
     *         if the MD5 Algorithm is unsupported by the Runtime Environment
     */
    public static byte[] md5sum(InputStream inStream) throws IOException {
        try {
            return checkSum(inStream, "MD5");
        }
        catch (NoSuchAlgorithmException e) {
            throw new UtilityException("sum.fail", "MD5");
        }
    }

    /**
     * Gets the SHA-1 sum of an {@link InputStream}
     *
     * @param inStream
     *         the {@link InputStream} to SHA-1 sum
     *
     * @return the SHA-1 sum
     *
     * @throws java.lang.NullPointerException
     *         if {@code inStream} is null
     * @throws java.io.IOException
     *         if an Input/Output exception occurs
     * @throws net.visualillusionsent.utils.UtilityException
     *         if the MD5 Algorithm is unsupported by the Runtime Environment
     */
    public static byte[] sha1sum(InputStream inStream) throws IOException {
        try {
            return checkSum(inStream, "SHA-1");
        }
        catch (NoSuchAlgorithmException e) {
            throw new UtilityException("sum.fail", "SHA-1");
        }
    }

    /**
     * Gets the SHA-256 sum of an {@link InputStream}
     *
     * @param inStream
     *         the {@link InputStream} to SHA-256 sum
     *
     * @return the SHA-256 sum
     *
     * @throws java.lang.NullPointerException
     *         if {@code inStream} is null
     * @throws java.io.IOException
     *         if an Input/Output exception occurs
     * @throws net.visualillusionsent.utils.UtilityException
     *         if the MD5 Algorithm is unsupported by the Runtime Environment
     */
    public static byte[] sha256sum(InputStream inStream) throws IOException {
        try {
            return checkSum(inStream, "SHA-256");
        }
        catch (NoSuchAlgorithmException e) {
            throw new UtilityException("sum.fail", "SHA-256");
        }
    }

    /**
     * Downloads a file from the specified url to the specified filePath
     *
     * @param url
     *         the url to download from
     * @param filePath
     *         the file path to download to
     *
     * @throws java.lang.NullPointerException
     *         if {@code url} or {@code filePath} is null
     * @throws java.lang.IllegalArgumentException
     *         if {@code url} or {@code filePath} is empty
     * @throws java.net.MalformedURLException
     *         if {@code url} specifies an unknown protocol.
     * @throws java.io.IOException
     *         if a read/write exception occurs
     */
    public static void downloadFile(String url, String filePath) throws IOException {
        notNull(url, "String url");
        notEmpty(url, "String url");

        downloadFile(new URL(url), filePath);
    }


    /**
     * Downloads a file from the specified {@link URL} to the specified filePath
     *
     * @param url
     *         the {@link URL} to download from
     * @param filePath
     *         the file path to download to
     *
     * @throws java.lang.NullPointerException
     *         if {@code url} or {@code filePath} is null
     * @throws java.lang.IllegalArgumentException
     *         if {@code filePath} is empty
     * @throws java.io.IOException
     *         if a read/write exception occurs
     */
    public static void downloadFile(URL url, String filePath) throws UtilityException, IOException {
        notNull(url, "URL url");
        notNull(filePath, "String filePath");
        notEmpty(filePath, "String filePath");

        ReadableByteChannel rbc = Channels.newChannel(url.openStream());
        FileOutputStream fos = new FileOutputStream(filePath);
        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
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
