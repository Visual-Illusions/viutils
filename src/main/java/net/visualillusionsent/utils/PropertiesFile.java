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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;

import static net.visualillusionsent.utils.Verify.notEmpty;
import static net.visualillusionsent.utils.Verify.notNull;

/**
 * Properties File helper
 * <p/>
 * Provides methods to help with creating and accessing a Properties File<br>
 * Lines that start with {@literal ;#} are seen as header comments<br>
 * Lines that start with {@literal #;} are seen as footer comments<br>
 * Other comments can be prefixed with either # or ; and will be attached to the top of they property that follows it<br>
 * Inline comments can be performed using #! {@literal <Comment>} at the end of a line<br>
 * If #! is needed as a property it can be escaped with \ ie: \#\!
 *
 * @author Jason (darkdiplomat)
 * @version 1.4
 * @since 1.0.0
 */
public final class PropertiesFile extends AbstractPropertiesFile {

    /* VIU 1.3.0 / 1.4 */
    private static final float classVersion = 1.4F;

    /**
     * Creates or loads a PropertiesFile
     *
     * @param filePath
     *         the path to the properties file
     *
     * @throws UtilityException
     *         <br>
     *         if there was an error with either reading or writing the properties file
     */
    public PropertiesFile(String filePath) throws UtilityException {
        super(filePath);
        this.props = new LinkedHashMap<String, String>();
        this.comments = new LinkedHashMap<String, List<String>>();
        this.inlineCom = new LinkedHashMap<String, String>();
        this.header = new LinkedList<String>();
        this.footer = new LinkedList<String>();
        if (propsFile.exists()) {
            try {
                load(new FileInputStream(propsFile));
            }
            catch (FileNotFoundException e) {
                throw new UtilityException("file.err.ioe", filePath);
            }
        }
        else {
            filePath = FileUtils.normalizePath(filePath);
            if (filePath.contains(File.separator)) {
                File temp = new File(filePath.substring(0, filePath.lastIndexOf(File.separator)));
                if (!temp.exists()) {
                    if (!temp.mkdirs()) {
                        throw new UtilityException("Failed to make directory path for FilePath: ".concat(filePath));
                    }
                    save(true);
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public PropertiesFile(File file) throws UtilityException {
        super(file);
        this.props = new LinkedHashMap<String, String>();
        this.comments = new LinkedHashMap<String, List<String>>();
        this.inlineCom = new LinkedHashMap<String, String>();
        this.header = new LinkedList<String>();
        this.footer = new LinkedList<String>();
        if (propsFile.exists()) {
            try {
                load(new FileInputStream(propsFile));
            }
            catch (FileNotFoundException e) {
                throw new UtilityException("file.err.ioe", filePath);
            }
        }
        else {
            filePath = FileUtils.normalizePath(filePath);
            if (filePath.contains(File.separator)) {
                File temp = new File(filePath.substring(0, filePath.lastIndexOf(File.separator)));
                if (!temp.exists()) {
                    if (!temp.mkdirs()) {
                        throw new UtilityException("Failed to make directory path for FilePath: ".concat(filePath));
                    }
                    save(true);
                }
            }
        }
    }

    /**
     * Loads a PropertiesFile stored inside a Jar file
     *
     * @param jarPath
     *         the path to the Jar file
     * @param entry
     *         the name of the file inside of the jar
     *
     * @throws UtilityException
     *         <br>
     *         if jarPath is null or empty<br>
     *         or if entry is null or empty<br>
     *         or if the Jar file is not found or unable to be read from<br>
     *         or if the Jar file does not contain the entry
     */
    public PropertiesFile(String jarPath, String entry) throws UtilityException {
        super(jarPath, entry);
        JarEntry ent = jar.getJarEntry(entry);
        this.props = new LinkedHashMap<String, String>();
        this.comments = new LinkedHashMap<String, List<String>>();
        this.inlineCom = new LinkedHashMap<String, String>();
        this.header = new LinkedList<String>();
        this.footer = new LinkedList<String>();
        try {
            load(jar.getInputStream(ent));
        }
        catch (IOException e) {
            throw new UtilityException("file.err.ioe", filePath);
        }
    }

    /** {@inheritDoc} */
    @Override
    protected final void load(InputStream inStream) throws UtilityException {
        UtilityException uex = null;
        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(inStream, "UTF-8"));
            String inLine;
            LinkedList<String> inComments = new LinkedList<String>();
            while ((inLine = in.readLine()) != null) {
                if (inLine.startsWith(";#")) {
                    header.add(inLine);
                }
                else if (inLine.startsWith("#;")) {
                    footer.add(inLine);
                }
                else if (inLine.startsWith(";") || inLine.startsWith("#")) {
                    inComments.add(inLine);
                }
                else {
                    String[] propsLine = null;
                    try {
                        propsLine = inLine.split("=");
                        String key = propsLine[0].trim();
                        String value = propsLine[1];
                        if (value.contains("#!")) {
                            inlineCom.put(key, value.split("#!")[1]);
                            value = value.split("#!")[0];
                        }
                        props.put(key.trim(), value.replace("\\#\\!", "#!").trim()); //remove escape sequence
                    }
                    catch (ArrayIndexOutOfBoundsException aioobex) {
                        //Empty value?
                        if (inLine.contains("=")) {
                            props.put(propsLine[0], "");
                        }
                        //Incomplete property, drop reference
                        else {
                            inComments.clear();
                            continue;
                        }
                    }
                    if (!inComments.isEmpty()) {
                        comments.put(propsLine[0], new LinkedList<String>(inComments));
                        inComments.clear();
                    }
                }
            }
        }
        catch (IOException ioe) {
            UtilsLogger.severe(String.format("An IOException occurred in File: '%s'", filePath), ioe);
            uex = new UtilityException("file.err.ioe", filePath);
        }
        finally {
            if (in != null) {
                try {
                    in.close();
                }
                catch (IOException e) {
                    //do nothing
                }
            }
            if (uex != null) {
                throw uex;
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public final void reload() throws UtilityException {
        props.clear();
        comments.clear();
        if (jar != null) {
            JarEntry ent = jar.getJarEntry(filePath);
            if (ent == null) {
                throw new UtilityException("entry.missing", filePath);
            }
            try {
                load(jar.getInputStream(ent));
            }
            catch (IOException e) {
                throw new UtilityException("file.err.ioe", filePath);
            }
        }
        else {
            try {
                load(new FileInputStream(propsFile));
            }
            catch (FileNotFoundException e) {
                throw new UtilityException("file.err.ioe", filePath);
            }
        }
        this.hasChanged = false;
    }

    /** {@inheritDoc} */
    @Override
    public final void save() throws UtilityException {
        this.save(false);
    }

    /** {@inheritDoc} */
    @Override
    public final void forceSave() throws UtilityException {
        this.save(true);
    }

    /** {@inheritDoc} */
    @Override
    protected final void save(boolean force) throws UtilityException {
        if (jar != null) {
            throw new UtilityException("Saving is not supported with PropertiesFiles inside of Jar files");
        }
        if (!hasChanged && !force) {
            return;
        }
        PrintWriter out = null;
        try {
            if (propsFile.exists()) {
                if (!propsFile.delete()) {
                    throw new UtilityException("file.err.ioe", filePath);
                }
            }
            propsFile = new File(filePath);
            out = new PrintWriter(new FileWriter(propsFile, true));
            for (String headerLn : header) {
                out.println(headerLn);
            }
            for (String prop : props.keySet()) {
                if (comments.containsKey(prop)) {
                    for (String comment : comments.get(prop)) {
                        out.println(comment);
                    }
                }
                String inLineC = inlineCom.get(prop);
                out.println(prop.concat("=").concat(props.get(prop).replace("#!", "\\#\\!").concat(inLineC == null ? "" : " #!".concat(inLineC))));
            }
            for (String footerLn : footer) {
                out.println(footerLn);
            }
        }
        catch (IOException ioe) {
            UtilsLogger.severe(String.format("An IOException occurred in File: '%s'", filePath), ioe);
            throw new UtilityException("file.err.ioe", filePath);
        }
        finally {
            if (out != null) {
                out.close();
            }
        }
        this.hasChanged = false; // Changes stored
    }

    /** {@inheritDoc} */
    @Override
    public final boolean containsKey(String key) throws UtilityException {
        notNull(key, "String key");
        notEmpty(key, "String key");

        return props.containsKey(key);
    }

    /** {@inheritDoc} */
    @Override
    public final boolean containsKeys(String... keys) throws UtilityException {
        boolean contains = true;
        for (String key : keys) {
            contains &= containsKey(key);
        }
        return contains;
    }

    /** {@inheritDoc} */
    @Override
    public final void removeKey(String key) throws UtilityException {
        notNull(key, "String key");
        notEmpty(key, "String key");

        if (props.containsKey(key)) {
            props.remove(key);
            if (comments.containsKey(key)) {
                comments.remove(key);
            }
            this.hasChanged = true;
        }
    }

    /** {@inheritDoc} */
    @Override
    public final String getString(String key) throws UtilityException {
        notNull(key, "String key");
        notEmpty(key, "String key");

        if (containsKey(key)) {
            return props.get(key);
        }
        throw new UtilityException("key.missing", key);
    }

    /** {@inheritDoc} */
    @Override
    public final String getString(String key, String def) throws UtilityException {
        notNull(key, "String key");
        notEmpty(key, "String key");

        if (containsKey(key)) {
            return props.get(key);
        }
        else {
            setString(key, def);
            return def;
        }
    }

    /** {@inheritDoc} */
    @Override
    public final void setString(String key, String value) throws UtilityException {
        setString(key, value, (String[]) null);
    }

    /** {@inheritDoc} */
    @Override
    public final void setString(String key, String value, String... comment) throws UtilityException {
        notNull(key, "String key");
        notNull(value, "String value");
        notEmpty(key, "String key");

        if (value.equals(props.get(key))) {
            return;
        }
        props.put(key, value);
        addComment(key, comment);
        this.hasChanged = true;
    }

    /** {@inheritDoc} */
    @Override
    public final String[] getStringArray(String key) throws UtilityException {
        return getStringArray(key, ",");
    }

    /** {@inheritDoc} */
    @Override
    public final String[] getStringArray(String key, String[] def) throws UtilityException {
        if (containsKey(key)) {
            return getStringArray(key, ",");
        }
        else {
            setStringArray(key, def);
            return def;
        }
    }

    /** {@inheritDoc} */
    @Override
    public final void setStringArray(String key, String[] value) throws UtilityException {
        setStringArray(key, ",", value, (String[]) null);
    }

    /** {@inheritDoc} */
    @Override
    public final void setStringArray(String key, String[] value, String... comment) throws UtilityException {
        setStringArray(key, ",", value, comment);
    }

    /** {@inheritDoc} */
    @Override
    public final String[] getStringArray(String key, String delimiter) throws UtilityException {
        notNull(delimiter, "String delimiter");
        notEmpty(delimiter, "String delimiter");

        return StringUtils.trimElements(getString(key).split(delimiter));
    }

    /** {@inheritDoc} */
    @Override
    public final String[] getStringArray(String key, String delimiter, String[] def) throws UtilityException {
        if (containsKey(key)) {
            return getStringArray(key, delimiter);
        }
        else {
            setStringArray(key, delimiter, def);
            return def;
        }
    }

    /** {@inheritDoc} */
    @Override
    public final void setStringArray(String key, String delimiter, String[] value) throws UtilityException {
        setStringArray(key, delimiter, value, (String[]) null);
    }

    /** {@inheritDoc} */
    @Override
    public final void setStringArray(String key, String delimiter, String[] value, String... comment) throws UtilityException {
        notNull(key, "String key");
        notEmpty(key, "String key");

        String joinedValue = StringUtils.joinString(value, delimiter, 0);
        if (joinedValue.equals(props.get(key))) {
            return;
        }
        props.put(key, joinedValue);
        addComment(key, comment);
        this.hasChanged = true;
    }

    /** {@inheritDoc} */
    @Override
    public final byte getByte(String key) throws UtilityException {
        try {
            return Byte.parseByte(getString(key));
        }
        catch (NumberFormatException nfe) {
            throw new UtilityException("prop.nan", key);
        }
    }

    /** {@inheritDoc} */
    @Override
    public final byte getByte(String key, byte def) throws UtilityException {
        if (containsKey(key)) {
            try {
                return Byte.parseByte(getString(key));
            }
            catch (NumberFormatException nfe) {
                throw new UtilityException("prop.nan", key);
            }
        }
        else {
            setByte(key, def);
            return def;
        }
    }

    /** {@inheritDoc} */
    @Override
    public final void setByte(String key, byte value) throws UtilityException {
        setByte(key, value, (String[]) null);
    }

    /** {@inheritDoc} */
    @Override
    public final void setByte(String key, byte value, String... comment) throws UtilityException {
        notNull(key, "String key");
        notEmpty(key, "String key");

        String strVal = String.valueOf(value);
        if (strVal.equals(props.get(key))) {
            return;
        }
        props.put(key, String.valueOf(value));
        addComment(key, comment);
        this.hasChanged = true;
    }

    /** {@inheritDoc} */
    @Override
    public final byte[] getByteArray(String key) throws UtilityException {
        return getByteArray(key, ",");
    }

    /** {@inheritDoc} */
    @Override
    public final byte[] getByteArray(String key, byte[] def) throws UtilityException {
        if (containsKey(key)) {
            return getByteArray(key, ",");
        }
        else {
            setByteArray(key, def);
            return def;
        }
    }

    /** {@inheritDoc} */
    @Override
    public final void setByteArray(String key, byte[] value) throws UtilityException {
        setByteArray(key, ",", value, (String[]) null);
    }

    /** {@inheritDoc} */
    @Override
    public final void setByteArray(String key, byte[] value, String... comment) throws UtilityException {
        setByteArray(key, ",", value, comment);
    }

    /** {@inheritDoc} */
    @Override
    public final byte[] getByteArray(String key, String delimiter) throws UtilityException {
        return StringUtils.stringToByteArray(getString(key), delimiter);
    }

    /** {@inheritDoc} */
    @Override
    public final byte[] getByteArray(String key, String delimiter, byte[] def) throws UtilityException {
        if (containsKey(key)) {
            return StringUtils.stringToByteArray(getString(key), delimiter);
        }
        else {
            setByteArray(key, delimiter, def);
            return def;
        }
    }

    /** {@inheritDoc} */
    @Override
    public final void setByteArray(String key, String delimiter, byte[] value) throws UtilityException {
        setByteArray(key, delimiter, value, (String[]) null);
    }

    /** {@inheritDoc} */
    @Override
    public final void setByteArray(String key, String delimiter, byte[] value, String... comment) throws UtilityException {
        notNull(key, "String key");
        notEmpty(key, "String key");

        String strValue = StringUtils.byteArrayToString(value, delimiter);
        if (strValue.equals(props.get(key))) {
            return;
        }
        props.put(key, strValue);
        addComment(key, comment);
        this.hasChanged = true;
    }

    /** {@inheritDoc} */
    @Override
    public final short getShort(String key) throws UtilityException {
        try {
            return Short.parseShort(getString(key));
        }
        catch (NumberFormatException nfe) {
            throw new UtilityException("prop.nan", key);
        }
    }

    /** {@inheritDoc} */
    @Override
    public final short getShort(String key, short def) throws UtilityException {
        if (containsKey(key)) {
            try {
                return Short.parseShort(getString(key));
            }
            catch (NumberFormatException nfe) {
                throw new UtilityException("prop.nan", key);
            }
        }
        else {
            setShort(key, def);
            return def;
        }
    }

    /** {@inheritDoc} */
    @Override
    public final void setShort(String key, short value) throws UtilityException {
        setShort(key, value, (String[]) null);
    }

    /** {@inheritDoc} */
    @Override
    public final void setShort(String key, short value, String... comment) throws UtilityException {
        notNull(key, "String key");
        notEmpty(key, "String key");

        String strValue = String.valueOf(value);
        if (strValue.equals(props.get(key))) {
            return;
        }
        props.put(key, strValue);
        addComment(key, comment);
        this.hasChanged = true;
    }

    /** {@inheritDoc} */
    @Override
    public final short[] getShortArray(String key) throws UtilityException {
        return getShortArray(key, ",");
    }

    /** {@inheritDoc} */
    @Override
    public final short[] getShortArray(String key, short[] def) throws UtilityException {
        if (containsKey(key)) {
            return getShortArray(key, ",");
        }
        else {
            setShortArray(key, def);
            return def;
        }
    }

    /** {@inheritDoc} */
    @Override
    public final void setShortArray(String key, short[] value) throws UtilityException {
        setShortArray(key, ",", value, (String[]) null);
    }

    /** {@inheritDoc} */
    @Override
    public final void setShortArray(String key, short[] value, String... comment) throws UtilityException {
        setShortArray(key, ",", value, comment);
    }

    /** {@inheritDoc} */
    @Override
    public final short[] getShortArray(String key, String delimiter) throws UtilityException {
        return StringUtils.stringToShortArray(getString(key), delimiter);
    }

    /** {@inheritDoc} */
    @Override
    public final short[] getShortArray(String key, String delimiter, short[] def) throws UtilityException {
        if (containsKey(key)) {
            return StringUtils.stringToShortArray(getString(key), delimiter);
        }
        else {
            setShortArray(key, delimiter, def);
            return def;
        }
    }

    /** {@inheritDoc} */
    @Override
    public final void setShortArray(String key, String delimiter, short[] value) throws UtilityException {
        setShortArray(key, delimiter, value, (String[]) null);
    }

    /** {@inheritDoc} */
    @Override
    public final void setShortArray(String key, String delimiter, short[] value, String... comment) throws UtilityException {
        notNull(key, "String key");
        notEmpty(key, "String key");

        String strValue = StringUtils.shortArrayToString(value, delimiter);
        if (strValue.equals(props.get(key))) {
            return;
        }
        props.put(key, strValue);
        addComment(key, comment);
        this.hasChanged = true;
    }

    /** {@inheritDoc} */
    @Override
    public final int getInt(String key) throws UtilityException {
        try {
            return Integer.parseInt(getString(key));
        }
        catch (NumberFormatException nfe) {
            throw new UtilityException("prop.nan", key);
        }
    }

    /** {@inheritDoc} */
    @Override
    public final int getInt(String key, int def) throws UtilityException {
        if (containsKey(key)) {
            try {
                return Integer.parseInt(getString(key));
            }
            catch (NumberFormatException nfe) {
                throw new UtilityException("prop.nan", key);
            }
        }
        else {
            setInt(key, def);
            return def;
        }
    }

    /** {@inheritDoc} */
    @Override
    public final void setInt(String key, int value) throws UtilityException {
        setInt(key, value, (String[]) null);
    }

    /** {@inheritDoc} */
    @Override
    public final void setInt(String key, int value, String... comment) throws UtilityException {
        notNull(key, "String key");
        notEmpty(key, "String key");

        String strValue = String.valueOf(value);
        if (strValue.equals(props.get(key))) {
            return;
        }
        props.put(key, strValue);
        addComment(key, comment);
        this.hasChanged = true;
    }

    /** {@inheritDoc} */
    @Override
    public final int[] getIntArray(String key) throws UtilityException {
        return getIntArray(key, ",");
    }

    /** {@inheritDoc} */
    @Override
    public final int[] getIntArray(String key, int[] def) throws UtilityException {
        if (containsKey(key)) {
            return getIntArray(key, ",");
        }
        else {
            setIntArray(key, def);
            return def;
        }
    }

    /** {@inheritDoc} */
    @Override
    public final void setIntArray(String key, int[] value) throws UtilityException {
        setIntArray(key, ",", value, (String[]) null);
    }

    /** {@inheritDoc} */
    @Override
    public final void setIntArray(String key, int[] value, String... comment) throws UtilityException {
        setIntArray(key, ",", value, comment);
    }

    /** {@inheritDoc} */
    @Override
    public final int[] getIntArray(String key, String delimiter) throws UtilityException {
        return StringUtils.stringToIntArray(getString(key), delimiter);
    }

    /** {@inheritDoc} */
    @Override
    public final int[] getIntArray(String key, String delimiter, int[] def) throws UtilityException {
        if (containsKey(key)) {
            return StringUtils.stringToIntArray(getString(key), delimiter);
        }
        else {
            setIntArray(key, delimiter, def);
            return def;
        }
    }

    /** {@inheritDoc} */
    @Override
    public final void setIntArray(String key, String delimiter, int[] value) throws UtilityException {
        setIntArray(key, delimiter, value, (String[]) null);
    }

    /** {@inheritDoc} */
    @Override
    public final void setIntArray(String key, String delimiter, int[] value, String... comment) throws UtilityException {
        notNull(key, "String key");
        notEmpty(key, "String key");

        String strValue = StringUtils.intArrayToString(value, delimiter);
        if (strValue.equals(props.get(key))) {
            return;
        }
        props.put(key, strValue);
        addComment(key, comment);
        this.hasChanged = true;
    }

    /** {@inheritDoc} */
    @Override
    public final long getLong(String key) throws UtilityException {
        try {
            return Long.parseLong(getString(key));
        }
        catch (NumberFormatException nfe) {
            throw new UtilityException("prop.nan", key);
        }
    }

    /** {@inheritDoc} */
    @Override
    public final long getLong(String key, long def) throws UtilityException {
        if (containsKey(key)) {
            try {
                return Long.parseLong(getString(key));
            }
            catch (NumberFormatException nfe) {
                throw new UtilityException("prop.nan", key);
            }
        }
        else {
            setLong(key, def);
            return def;
        }
    }

    /** {@inheritDoc} */
    @Override
    public final void setLong(String key, long value) throws UtilityException {
        setLong(key, value, (String[]) null);
    }

    /** {@inheritDoc} */
    @Override
    public final void setLong(String key, long value, String... comment) throws UtilityException {
        notNull(key, "String key");
        notEmpty(key, "String key");

        String strValue = String.valueOf(value);
        if (strValue.equals(props.get(key))) {
            return;
        }
        props.put(key, strValue);
        addComment(key, comment);
        this.hasChanged = true;
    }

    /** {@inheritDoc} */
    @Override
    public final long[] getLongArray(String key) throws UtilityException {
        return getLongArray(key, ",");
    }

    /** {@inheritDoc} */
    @Override
    public final long[] getLongArray(String key, long[] def) throws UtilityException {
        if (containsKey(key)) {
            return getLongArray(key, ",");
        }
        else {
            setLongArray(key, def);
            return def;
        }
    }

    /** {@inheritDoc} */
    @Override
    public final void setLongArray(String key, long[] value) throws UtilityException {
        setLongArray(key, ",", value, (String[]) null);
    }

    /** {@inheritDoc} */
    @Override
    public final void setLongArray(String key, long[] value, String... comment) throws UtilityException {
        setLongArray(key, ",", value, comment);
    }

    /** {@inheritDoc} */
    @Override
    public final long[] getLongArray(String key, String delimiter) throws UtilityException {
        return StringUtils.stringToLongArray(getString(key), delimiter);
    }

    /** {@inheritDoc} */
    @Override
    public final long[] getLongArray(String key, String delimiter, long[] def) throws UtilityException {
        notNull(key, "String key");
        notEmpty(key, "String key");

        if (containsKey(key)) {
            return StringUtils.stringToLongArray(getString(key), delimiter);
        }
        else {
            setLongArray(key, delimiter, def);
            return def;
        }
    }

    /** {@inheritDoc} */
    @Override
    public final void setLongArray(String key, String delimiter, long[] value) throws UtilityException {
        setLongArray(key, delimiter, value, (String[]) null);
    }

    /** {@inheritDoc} */
    @Override
    public final void setLongArray(String key, String delimiter, long[] value, String... comment) throws UtilityException {
        notNull(key, "String key");
        notEmpty(key, "String key");

        String strValue = StringUtils.longArrayToString(value, delimiter);
        if (strValue.equals(props.get(key))) {
            return;
        }
        props.put(key, strValue);
        addComment(key, comment);
        this.hasChanged = true;
    }

    /** {@inheritDoc} */
    @Override
    public final float getFloat(String key) throws UtilityException {
        try {
            return Float.parseFloat(getString(key));
        }
        catch (NumberFormatException nfe) {
            throw new UtilityException("prop.nan", key);
        }
    }

    /** {@inheritDoc} */
    @Override
    public final float getFloat(String key, float def) throws UtilityException {
        if (containsKey(key)) {
            try {
                return Float.parseFloat(getString(key));
            }
            catch (NumberFormatException nfe) {
                throw new UtilityException("prop.nan", key);
            }
        }
        else {
            setFloat(key, def);
            return def;
        }
    }

    /** {@inheritDoc} */
    @Override
    public final void setFloat(String key, float value) throws UtilityException {
        setFloat(key, value, (String[]) null);
    }

    /** {@inheritDoc} */
    @Override
    public final void setFloat(String key, float value, String... comment) throws UtilityException {
        notNull(key, "String key");
        notEmpty(key, "String key");

        String strValue = String.valueOf(value);
        if (strValue.equals(props.get(key))) {
            return;
        }
        props.put(key, strValue);
        addComment(key, comment);
        this.hasChanged = true;
    }

    /** {@inheritDoc} */
    @Override
    public final float[] getFloatArray(String key) throws UtilityException {
        return getFloatArray(key, ",");
    }

    /** {@inheritDoc} */
    @Override
    public final float[] getFloatArray(String key, float[] def) throws UtilityException {
        if (containsKey(key)) {
            return getFloatArray(key, ",");
        }
        else {
            setFloatArray(key, def);
            return def;
        }
    }

    /** {@inheritDoc} */
    @Override
    public final void setFloatArray(String key, float[] value) throws UtilityException {
        setFloatArray(key, ",", value, (String[]) null);
    }

    /** {@inheritDoc} */
    @Override
    public final void setFloatArray(String key, float[] value, String... comment) throws UtilityException {
        setFloatArray(key, ",", value, comment);
    }

    /** {@inheritDoc} */
    @Override
    public final float[] getFloatArray(String key, String delimiter) throws UtilityException {
        return StringUtils.stringToFloatArray(getString(key), delimiter);
    }

    /** {@inheritDoc} */
    @Override
    public final float[] getFloatArray(String key, String delimiter, float[] def) throws UtilityException {
        if (containsKey(key)) {
            return StringUtils.stringToFloatArray(getString(key), delimiter);
        }
        else {
            setFloatArray(key, delimiter, def);
            return def;
        }
    }

    /** {@inheritDoc} */
    @Override
    public final void setFloatArray(String key, String delimiter, float[] value) throws UtilityException {
        setFloatArray(key, delimiter, value, (String[]) null);
    }

    /** {@inheritDoc} */
    @Override
    public final void setFloatArray(String key, String delimiter, float[] value, String... comment) throws UtilityException {
        notNull(key, "String key");
        notEmpty(key, "String key");

        String strValue = StringUtils.floatArrayToString(value, delimiter);
        if (strValue.equals(props.get(key))) {
            return;
        }
        props.put(key, strValue);
        addComment(key, comment);
        this.hasChanged = true;
    }

    /** {@inheritDoc} */
    @Override
    public final double getDouble(String key) throws UtilityException {
        try {
            return Double.parseDouble(getString(key));
        }
        catch (NumberFormatException nfe) {
            throw new UtilityException("prop.nan");
        }
    }

    /** {@inheritDoc} */
    @Override
    public final double getDouble(String key, double def) throws UtilityException {
        if (containsKey(key)) {
            try {
                return Double.parseDouble(getString(key));
            }
            catch (NumberFormatException nfe) {
                throw new UtilityException("prop.nan", key);
            }
        }
        else {
            setDouble(key, def);
            return def;
        }
    }

    /** {@inheritDoc} */
    @Override
    public final void setDouble(String key, double value) throws UtilityException {
        setDouble(key, value, (String[]) null);
    }

    /** {@inheritDoc} */
    @Override
    public final void setDouble(String key, double value, String... comment) throws UtilityException {
        notNull(key, "String key");
        notEmpty(key, "String key");

        String strValue = String.valueOf(value);
        if (strValue.equals(props.get(key))) {
            return;
        }
        props.put(key, strValue);
        addComment(key, comment);
        this.hasChanged = true;
    }

    /** {@inheritDoc} */
    @Override
    public final double[] getDoubleArray(String key) throws UtilityException {
        return getDoubleArray(key, ",");
    }

    /** {@inheritDoc} */
    @Override
    public final double[] getDoubleArray(String key, double[] def) throws UtilityException {
        if (containsKey(key)) {
            return getDoubleArray(key, ",");
        }
        else {
            setDoubleArray(key, def);
            return def;
        }
    }

    /** {@inheritDoc} */
    @Override
    public final void setDoubleArray(String key, double[] value) throws UtilityException {
        setDoubleArray(key, ",", value, (String[]) null);
    }

    /** {@inheritDoc} */
    @Override
    public final void setDoubleArray(String key, double[] value, String... comment) throws UtilityException {
        setDoubleArray(key, ",", value, comment);
    }

    /** {@inheritDoc} */
    @Override
    public final double[] getDoubleArray(String key, String delimiter) throws UtilityException {
        return StringUtils.stringToDoubleArray(getString(key), delimiter);
    }

    /** {@inheritDoc} */
    @Override
    public final double[] getDoubleArray(String key, String delimiter, double[] def) throws UtilityException {
        if (containsKey(key)) {
            return StringUtils.stringToDoubleArray(getString(key), delimiter);
        }
        else {
            setDoubleArray(key, delimiter, def);
            return def;
        }
    }

    /** {@inheritDoc} */
    @Override
    public final void setDoubleArray(String key, String delimiter, double[] value) throws UtilityException {
        setDoubleArray(key, delimiter, value, (String[]) null);
    }

    /** {@inheritDoc} */
    @Override
    public final void setDoubleArray(String key, String delimiter, double[] value, String... comment) throws UtilityException {
        notNull(key, "String key");
        notEmpty(key, "String key");

        String strValue = StringUtils.doubleArrayToString(value, delimiter);
        if (strValue.equals(props.get(key))) {
            return;
        }
        props.put(key, strValue);
        addComment(key, comment);
        this.hasChanged = true;
    }

    /** {@inheritDoc} */
    @Override
    public final boolean getBoolean(String key) throws UtilityException {
        return BooleanUtils.parseBoolean(getString(key));
    }

    /** {@inheritDoc} */
    @Override
    public final boolean getBoolean(String key, boolean def) throws UtilityException {
        if (containsKey(key)) {
            return BooleanUtils.parseBoolean(getString(key));
        }
        else {
            setBoolean(key, def);
            return def;
        }
    }

    /** {@inheritDoc} */
    @Override
    public final void setBoolean(String key, boolean value) throws UtilityException {
        setBoolean(key, value, (String[]) null);
    }

    /** {@inheritDoc} */
    @Override
    public final void setBoolean(String key, boolean value, String... comment) throws UtilityException {
        notNull(key, "String key");
        notEmpty(key, "String key");

        String strValue = String.valueOf(value);
        if (strValue.equals(props.get(key))) {
            return;
        }
        props.put(key, strValue);
        addComment(key, comment);
        this.hasChanged = true;
    }

    /** {@inheritDoc} */
    @Override
    public final boolean[] getBooleanArray(String key) throws UtilityException {
        return getBooleanArray(key, ",");
    }

    /** {@inheritDoc} */
    @Override
    public final boolean[] getBooleanArray(String key, boolean[] def) throws UtilityException {
        if (containsKey(key)) {
            return getBooleanArray(key, ",");
        }
        else {
            setBooleanArray(key, def);
            return def;
        }
    }

    /** {@inheritDoc} */
    @Override
    public final void setBooleanArray(String key, boolean[] value) throws UtilityException {
        setBooleanArray(key, ",", value, (String[]) null);
    }

    /** {@inheritDoc} */
    @Override
    public final void setBooleanArray(String key, boolean[] value, String... comment) throws UtilityException {
        setBooleanArray(key, ",", value, comment);
    }

    /** {@inheritDoc} */
    @Override
    public final boolean[] getBooleanArray(String key, String delimiter) throws UtilityException {
        return StringUtils.stringToBooleanArray(getString(key), delimiter);
    }

    /** {@inheritDoc} */
    @Override
    public final boolean[] getBooleanArray(String key, String delimiter, boolean[] def) throws UtilityException {
        if (containsKey(key)) {
            return StringUtils.stringToBooleanArray(getString(key), delimiter);
        }
        else {
            setBooleanArray(key, delimiter, def);
            return def;
        }
    }

    /** {@inheritDoc} */
    @Override
    public final void setBooleanArray(String key, String delimiter, boolean[] value) throws UtilityException {
        setBooleanArray(key, delimiter, value, (String[]) null);
    }

    /** {@inheritDoc} */
    @Override
    public final void setBooleanArray(String key, String delimiter, boolean[] value, String... comment) throws UtilityException {
        notNull(key, "String key");
        notEmpty(key, "String key");

        String strValue = StringUtils.booleanArrayToString(value, delimiter);
        if (strValue.equals(props.get(key))) {
            return;
        }
        props.put(key, strValue);
        addComment(key, comment);
        this.hasChanged = true;
    }

    /** {@inheritDoc} */
    @Override
    public final char getCharacter(String key) throws UtilityException {
        return getString(key).trim().charAt(0);
    }

    /** {@inheritDoc} */
    @Override
    public final char getCharacter(String key, char def) throws UtilityException {
        if (containsKey(key)) {
            return getString(key).trim().charAt(0);
        }
        else {
            setCharacter(key, def);
            return def;
        }
    }

    /** {@inheritDoc} */
    @Override
    public final void setCharacter(String key, char ch) throws UtilityException {
        setCharacter(key, ch, (String[]) null);
    }

    /** {@inheritDoc} */
    @Override
    public final void setCharacter(String key, char ch, String... comment) throws UtilityException {
        notNull(key, "String key");
        notEmpty(key, "String key");

        String strValue = String.valueOf(ch);
        if (strValue.equals(props.get(key))) {
            return;
        }
        props.put(key, strValue);
        addComment(key, comment);
        this.hasChanged = true;
    }

    /** {@inheritDoc} */
    @Override
    public final Map<String, String> getPropertiesMap() {
        return Collections.unmodifiableMap(props);
    }

    /** {@inheritDoc} */
    @Override
    public final void addComment(String key, String... comment) {
        if (containsKey(key)) {
            if (comment != null && comment.length > 0) {
                List<String> the_comments = comments.containsKey(key) ? comments.get(key) : new LinkedList<String>();
                for (int i = 0; i < comment.length; i++) {
                    if (comment[i] == null) {
                        comment[i] = "";
                    }
                    if (!comment[i].startsWith(";") && !comment[i].startsWith("#")) {
                        comment[i] = ";".concat(comment[i]);
                    }
                    the_comments.add(comment[i]);
                }
                if (!comments.containsKey(key)) { //Basicly, the list pointer should be enough to change the list in the map without re-adding
                    comments.put(key, the_comments);
                }
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public final void setComments(String key, String... comment) {
        if (containsKey(key)) {
            if (comments.containsKey(key)) {
                comments.get(key).clear();
            }
            this.addComment(key, comment);
        }
    }

    /** {@inheritDoc} */
    @Override
    public final String[] getComments(String key) {
        if (comments.containsKey(key)) {
            return comments.get(key).toArray(new String[0]);
        }
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public final List<String> getCommentsAsList(String key) {
        if (comments.containsKey(key)) {
            return comments.get(key);
        }
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public final void removeComment(String key, String comment) {
        if (comments.containsKey(key)) {
            comments.get(key).remove(comment);
        }
    }

    /** {@inheritDoc} */
    @Override
    public final void removeAllCommentsFromKey(String key) {
        if (comments.containsKey(key)) {
            comments.remove(key);
        }
    }

    /** {@inheritDoc} */
    @Override
    public final void removeAllCommentsFromFile() {
        comments.clear();
        header.clear();
        footer.clear();
    }

    /** {@inheritDoc} */
    @Override
    public final void addHeaderLines(String... lines) {
        if (lines != null && lines.length > 0) {
            for (String line : lines) {
                if (line == null) {
                    header.add(";# ");
                }
                else if (line.startsWith(";#")) {
                    header.add(line);
                }
                else {
                    header.add(";#".concat(line));
                }
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public final LinkedList<String> getHeaderLines() {
        LinkedList<String> toRet = new LinkedList<String>(header);
        Collections.copy(header, toRet);
        return toRet;
    }

    /** {@inheritDoc} */
    @Override
    public final void clearHeader() {
        header.clear();
    }

    /** {@inheritDoc} */
    @Override
    public final void addFooterLines(String... lines) {
        if (lines != null && lines.length > 0) {
            for (String line : lines) {
                if (line == null) {
                    footer.add("#; ");
                }
                else if (line.startsWith("#;")) {
                    footer.add(line);
                }
                else {
                    header.add("#;".concat(line));
                }
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public final LinkedList<String> getFooterLines() {
        LinkedList<String> toRet = new LinkedList<String>(footer);
        Collections.copy(footer, toRet);
        return toRet;
    }

    /** {@inheritDoc} */
    @Override
    public final void clearFooter() {
        footer.clear();
    }

    /** {@inheritDoc} */
    @Override
    public final String getInlineComment(String key) {
        return inlineCom.get(key);
    }

    /**
     * Checks is an {@link Object} is equal to the {@code PropertiesFile}
     *
     * @return {@code true} if equal; {@code false} otherwise
     *
     * @see Object#equals(Object)
     */
    public final boolean equals(Object obj) {
        if (!(obj instanceof PropertiesFile)) {
            return false;
        }
        PropertiesFile that = (PropertiesFile) obj;
        if (!this.filePath.equals(that.filePath)) {
            return false;
        }
        if (this.propsFile != null && this.propsFile != that.propsFile) {
            return false;
        }
        if (this.jar != null && this.jar != that.jar) {
            return false;
        }
        return true;
    }

    /**
     * Returns a string representation of the {@code PropertiesFile} as {@code PropertiesFile[FilePath=%s]}
     *
     * @return string representation of the {@code PropertiesFile}
     *
     * @see Object#toString()
     */
    @Override
    public final String toString() {
        return String.format("PropertiesFile[FilePath=%s]", propsFile != null ? propsFile.getAbsolutePath() : jar.getName() + ":" + filePath);
    }

    /**
     * Returns a hash code value for the {@code PropertiesFile}.
     *
     * @return hash
     *
     * @see Object#hashCode()
     */
    @Override
    public final int hashCode() {
        int hash = 9;
        hash = 45 * hash + filePath.hashCode();
        hash = 54 * hash + (propsFile != null ? propsFile.hashCode() : 0);
        hash = 45 * hash + (jar != null ? jar.hashCode() : 0);
        return hash;
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
