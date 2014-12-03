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
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;

import static net.visualillusionsent.utils.Verify.notEmpty;
import static net.visualillusionsent.utils.Verify.notEmptyNoTrim;
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
 * @version 1.6
 * @since 1.0.0
 */
public final class PropertiesFile extends AbstractPropertiesFile {

    /* 1.6 @ VIUtils 1.4.1 */
    private static final float classVersion = 1.6F;

    /**
     * {@inheritDoc}
     *
     * @throws PropertiesFileException
     *         if an exception occurs while reading the file or if unable to create the file
     */
    public PropertiesFile(String filePath) {
        super(filePath);
        this.props = new LinkedHashMap<String, String>();
        this.booleanCache = new HashMap<String, Boolean>();
        this.numberCache = new HashMap<String, Number>();
        this.comments = new LinkedHashMap<String, List<String>>();
        this.inlineCom = new LinkedHashMap<String, String>();
        this.header = new LinkedList<String>();
        this.footer = new LinkedList<String>();

        if (propsFile.exists()) {
            try {
                load(new FileInputStream(propsFile));
            }
            catch (FileNotFoundException e) {
                throw new PropertiesFileException("file.err.ioe", filePath);
            }
        }
        else {
            filePath = FileUtils.normalizePath(filePath);
            if (filePath.contains(File.separator)) {
                File temp = new File(filePath.substring(0, filePath.lastIndexOf(File.separator)));
                if (!temp.exists()) {
                    if (!temp.mkdirs()) {
                        throw new PropertiesFileException("Failed to make directory path for FilePath: ".concat(filePath));
                    }
                    save(true);
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     *
     * @throws PropertiesFileException
     *         if an exception occurs while reading the file or if unable to create the file
     */
    public PropertiesFile(File file) {
        super(file);
        this.props = new LinkedHashMap<String, String>();
        this.booleanCache = new HashMap<String, Boolean>();
        this.numberCache = new HashMap<String, Number>();
        this.comments = new LinkedHashMap<String, List<String>>();
        this.inlineCom = new LinkedHashMap<String, String>();
        this.header = new LinkedList<String>();
        this.footer = new LinkedList<String>();
        if (propsFile.exists()) {
            try {
                load(new FileInputStream(propsFile));
            }
            catch (FileNotFoundException e) {
                throw new PropertiesFileException("file.err.ioe", filePath);
            }
        }
        else {
            filePath = FileUtils.normalizePath(filePath);
            if (filePath.contains(File.separator)) {
                File temp = new File(filePath.substring(0, filePath.lastIndexOf(File.separator)));
                if (!temp.exists()) {
                    if (!temp.mkdirs()) {
                        throw new PropertiesFileException("Failed to make directory path for FilePath: ".concat(filePath));
                    }
                    save(true);
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     *
     * @throws PropertiesFileException
     *         if an exception occurs while reading the Zip File
     */
    public PropertiesFile(String zipPath, String entry) {
        super(zipPath, entry);
        ZipEntry ent = zip.getEntry(entry);
        this.props = new LinkedHashMap<String, String>();
        this.booleanCache = new HashMap<String, Boolean>();
        this.numberCache = new HashMap<String, Number>();
        this.comments = new LinkedHashMap<String, List<String>>();
        this.inlineCom = new LinkedHashMap<String, String>();
        this.header = new LinkedList<String>();
        this.footer = new LinkedList<String>();
        try {
            load(zip.getInputStream(ent));
        }
        catch (IOException e) {
            throw new PropertiesFileException("file.err.ioe", filePath);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @throws PropertiesFileException
     *         if an exception occurs while reading the file
     */
    @Override
    protected final void load(InputStream inStream) {
        PropertiesFileException uex = null;
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
                    String[] propsLine = inLine.split("=");
                    try {
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
            uex = new PropertiesFileException("file.err.ioe", filePath);
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
        }
        if (uex != null) {
            throw uex;
        }
    }

    /**
     * {@inheritDoc}
     *
     * @throws PropertiesFileException
     *         if an exception occurs while reading/writing the file
     */
    @Override
    public final void reload() {
        props.clear();
        comments.clear();
        booleanCache.clear();
        numberCache.clear();
        if (zip != null) {
            ZipEntry ent = zip.getEntry(filePath);
            if (ent == null) {
                throw new PropertiesFileException("entry.missing", filePath);
            }
            try {
                load(zip.getInputStream(ent));
            }
            catch (IOException e) {
                throw new PropertiesFileException("file.err.ioe", filePath);
            }
        }
        else {
            try {
                load(new FileInputStream(propsFile));
            }
            catch (FileNotFoundException e) {
                throw new PropertiesFileException("file.err.ioe", filePath);
            }
        }
        this.hasChanged = false;
    }

    /**
     * {@inheritDoc}
     *
     * @throws PropertiesFileException
     *         if an exception occurs while writing the file
     */
    @Override
    public final void save() {
        this.save(false);
    }

    /**
     * {@inheritDoc}
     *
     * @throws PropertiesFileException
     *         if an exception occurs while writing the file
     */
    @Override
    public final void forceSave() {
        this.save(true);
    }

    /**
     * {@inheritDoc}
     *
     * @throws PropertiesFileException
     *         if an exception occurs while writing the file
     */
    @Override
    protected final void save(boolean force) {
        if (zip != null) {
            throw new PropertiesFileException("Saving is not supported with PropertiesFiles inside of Zip/Jar files");
        }
        if (!hasChanged && !force) {
            return;
        }
        PrintWriter out = null;
        try {
            if (propsFile.exists()) {
                if (!propsFile.delete()) {
                    throw new PropertiesFileException("file.err.ioe", filePath);
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
            throw new PropertiesFileException("file.err.ioe", filePath);
        }
        finally {
            if (out != null) {
                out.close();
            }
        }
        this.hasChanged = false; // Changes stored
    }

    /**
     * {@inheritDoc}
     *
     * @throws java.lang.NullPointerException
     *         if {@code key} is null
     * @throws java.lang.IllegalArgumentException
     *         if {@code key}  is empty
     */
    @Override
    public final boolean containsKey(String key) {
        notNull(key, "String key");
        notEmpty(key, "String key");

        return props.containsKey(key);
    }

    /**
     * {@inheritDoc}
     *
     * @throws java.lang.NullPointerException
     *         if a {@code key} is null
     * @throws java.lang.IllegalArgumentException
     *         if a {@code key} is empty
     */
    @Override
    public final boolean containsKeys(String... keys) {
        boolean contains = true;
        for (String key : keys) {
            contains &= containsKey(key);
        }
        return contains;
    }

    /**
     * {@inheritDoc}
     *
     * @throws java.lang.NullPointerException
     *         if {@code key} is null
     * @throws java.lang.IllegalArgumentException
     *         if {@code key} is empty
     */
    @Override
    public final void removeKey(String key) {
        if (containsKey(key)) {
            props.remove(key);
            if (comments.containsKey(key)) {
                comments.remove(key);
            }
            this.hasChanged = true;
        }
    }

    /**
     * {@inheritDoc}
     *
     * @throws java.lang.NullPointerException
     *         if a {@code key} is null
     * @throws java.lang.IllegalArgumentException
     *         if a {@code key} is empty
     */
    @Override
    public final void removeKeys(String... keys) {
        notNull(keys, "String... keys");
        notEmpty(keys, "String... keys");

        for (String key : keys) {
            if (containsKey(key)) {
                props.remove(key);
                if (comments.containsKey(key)) {
                    comments.remove(key);
                }
                this.hasChanged = true;
            }
        }
    }

    /**
     * {@inheritDoc}
     *
     * @throws java.lang.NullPointerException
     *         if {@code key} is null
     * @throws java.lang.IllegalArgumentException
     *         if {@code key} is empty
     * @throws net.visualillusionsent.utils.UnknownPropertyException
     *         if the {@code key} does not exist
     */
    @Override
    public final String getString(String key) {
        if (containsKey(key)) {
            return props.get(key);
        }
        throw new UnknownPropertyException("key.missing", key);
    }

    /**
     * {@inheritDoc}
     *
     * @throws java.lang.NullPointerException
     *         if {@code key} or {@code def} is null
     * @throws java.lang.IllegalArgumentException
     *         if {@code key} is empty
     */
    @Override
    public final String getString(String key, String def) {
        notNull(key, "String key");
        notNull(def, "String def");
        notEmpty(key, "String key");

        if (containsKey(key)) {
            return props.get(key);
        }
        setString(key, def);
        return def;
    }

    /**
     * {@inheritDoc}
     *
     * @throws java.lang.NullPointerException
     *         if {@code key} is null
     *         if {@code value} if null
     * @throws java.lang.IllegalArgumentException
     *         if {@code key} is empty
     */
    @Override
    public final void setString(String key, String value) {
        setString(key, value, (String[]) null);
    }

    /**
     * {@inheritDoc}
     *
     * @throws java.lang.NullPointerException
     *         if {@code key} or {@code value} is null
     * @throws java.lang.IllegalArgumentException
     *         if {@code key} is empty
     */
    @Override
    public final void setString(String key, String value, String... comment) {
        notNull(key, "String key");
        notNull(value, "String value");
        notEmpty(key, "String key");

        if (value.equals(props.get(key))) {
            return;
        }
        if (numberCache.containsKey(key)) {
            numberCache.remove(key); // Don't bother checking if the value was suppose to be a number
        }
        if (booleanCache.containsKey(key)) {
            booleanCache.remove(key); // Don't bother checking if the value was suppose to be a number
        }
        props.put(key, value);
        addComment(key, comment);
        this.hasChanged = true;
    }

    /**
     * {@inheritDoc}
     *
     * @throws java.lang.NullPointerException
     *         if {@code key} is null
     * @throws java.lang.IllegalArgumentException
     *         if {@code key} is empty
     * @throws net.visualillusionsent.utils.UnknownPropertyException
     *         if the {@code key} does not exist
     */
    @Override
    public final String[] getStringArray(String key) {
        return getStringArray(key, ",");
    }

    /**
     * {@inheritDoc}
     *
     * @throws java.lang.NullPointerException
     *         if {@code key} or {@code def} is null
     * @throws java.lang.IllegalArgumentException
     *         if {@code key} is empty
     */
    @Override
    public final String[] getStringArray(String key, String[] def) {
        notNull(def, "String[] def");

        if (containsKey(key)) {
            return getStringArray(key, ",");
        }
        else {
            setStringArray(key, def);
            return def;
        }
    }

    /**
     * {@inheritDoc}
     *
     * @throws java.lang.NullPointerException
     *         if {@code key} or {@code value} is null
     * @throws java.lang.IllegalArgumentException
     *         if {@code key} is empty
     */
    @Override
    public final void setStringArray(String key, String[] value) {
        setStringArray(key, ",", value, (String[]) null);
    }

    /**
     * {@inheritDoc}
     *
     * @throws java.lang.NullPointerException
     *         if {@code key} or {@code value} is null
     * @throws java.lang.IllegalArgumentException
     *         if {@code key} is empty
     */
    @Override
    public final void setStringArray(String key, String[] value, String... comment) {
        setStringArray(key, ",", value, comment);
    }

    /**
     * {@inheritDoc}
     *
     * @throws java.lang.NullPointerException
     *         if {@code key} or {@code delimiter} is null
     * @throws java.lang.IllegalArgumentException
     *         if {@code key} or {@code delimiter} is empty
     * @throws net.visualillusionsent.utils.UnknownPropertyException
     *         if the {@code key} does not exist
     */
    @Override
    public final String[] getStringArray(String key, String delimiter) {
        notNull(delimiter, "String delimiter");
        notEmptyNoTrim(delimiter, "String delimiter");

        return StringUtils.trimElements(getString(key).split(delimiter));
    }

    /**
     * {@inheritDoc}
     *
     * @throws java.lang.NullPointerException
     *         if {@code key} or {@code delimiter} or {@code def} is null
     * @throws java.lang.IllegalArgumentException
     *         if {@code key} or {@code delimiter} is empty
     */
    @Override
    public final String[] getStringArray(String key, String delimiter, String[] def) {
        if (containsKey(key)) {
            return getStringArray(key, delimiter);
        }
        else {
            setStringArray(key, delimiter, def);
            return def;
        }
    }

    /**
     * {@inheritDoc}
     *
     * @throws java.lang.NullPointerException
     *         if {@code key} or {@code delimiter} or {@code value} is null
     * @throws java.lang.IllegalArgumentException
     *         if {@code key} or {@code delimiter} is empty
     */
    @Override
    public final void setStringArray(String key, String delimiter, String[] value) {
        setStringArray(key, delimiter, value, (String[]) null);
    }

    /**
     * {@inheritDoc}
     *
     * @throws java.lang.NullPointerException
     *         if {@code key} or {@code delimiter} or {@code value} is null
     * @throws java.lang.IllegalArgumentException
     *         if {@code key} or {@code delimiter} is empty
     */
    @Override
    public final void setStringArray(String key, String delimiter, String[] value, String... comment) {
        notNull(key, "String key");
        notNull(value, "String[] value");
        notNull(delimiter, "String delimiter");
        notEmpty(key, "String key");
        notEmptyNoTrim(delimiter, "String delimiter");

        String joinedValue = StringUtils.joinString(value, delimiter, 0);
        if (joinedValue.equals(props.get(key))) {
            return;
        }
        props.put(key, joinedValue);
        addComment(key, comment);
        this.hasChanged = true;
    }

    /**
     * {@inheritDoc}
     *
     * @throws java.lang.NullPointerException
     *         if {@code key} is null
     * @throws java.lang.IllegalArgumentException
     *         if {@code key} is empty
     * @throws net.visualillusionsent.utils.UnknownPropertyException
     *         if the {@code key} does not exist
     * @throws java.lang.NumberFormatException
     *         if a value is not a number or out of range
     */
    @Override
    public final byte getByte(String key) {
        notNull(key, "String key");

        if (numberCache.containsKey(key)) { // Caching check
            return numberCache.get(key).byteValue();
        }
        try {
            byte value = Byte.decode(getString(key)); // decode
            numberCache.put(key, value); // Cache
            return value;
        }
        catch (NumberFormatException nfe) {
            // Change Message
            throw new NumberFormatException(Verify.parse("prop.nan", key));
        }
    }

    /**
     * {@inheritDoc}
     *
     * @throws java.lang.NullPointerException
     *         if {@code key} is null
     * @throws java.lang.IllegalArgumentException
     *         if {@code key} is empty
     */
    @Override
    public final byte getByte(String key, byte def) {
        if (containsKey(key)) {
            try {
                return getByte(key);
            }
            catch (NumberFormatException nfex) {
                // Continue with default
            }
        }
        setByte(key, def);
        return def;
    }

    /**
     * {@inheritDoc}
     *
     * @throws java.lang.NullPointerException
     *         if {@code key} is null
     * @throws java.lang.IllegalArgumentException
     *         if {@code key} is empty
     */
    @Override
    public final void setByte(String key, byte value) {
        setByte(key, value, (String[]) null);
    }

    /**
     * {@inheritDoc}
     *
     * @throws java.lang.NullPointerException
     *         if {@code key} is null
     * @throws java.lang.IllegalArgumentException
     *         if {@code key} is empty
     */
    @Override
    public final void setByte(String key, byte value, String... comment) {
        notNull(key, "String key");
        notEmpty(key, "String key");

        String strVal = String.valueOf(value);
        if (strVal.equals(props.get(key))) {
            return;
        }
        numberCache.put(key, value);
        props.put(key, strVal);
        addComment(key, comment);
        this.hasChanged = true;
    }

    /**
     * {@inheritDoc}
     *
     * @throws java.lang.NullPointerException
     *         if {@code key} is null
     * @throws java.lang.IllegalArgumentException
     *         if {@code key} is empty
     * @throws net.visualillusionsent.utils.UnknownPropertyException
     *         if the {@code key} does not exist
     * @throws java.lang.NumberFormatException
     *         if a value is not a number or out of range
     */
    @Override
    public final byte[] getByteArray(String key) {
        return getByteArray(key, ",");
    }

    /**
     * {@inheritDoc}
     *
     * @throws java.lang.NullPointerException
     *         if {@code key} or {@code def} is null
     * @throws java.lang.IllegalArgumentException
     *         if {@code key} is empty
     */
    @Override
    public final byte[] getByteArray(String key, byte[] def) {
        if (containsKey(key)) {
            try {
                return getByteArray(key, ",");
            }
            catch (NumberFormatException nfex) {
                // Continue with default
            }
        }
        setByteArray(key, def);
        return def;
    }

    /**
     * {@inheritDoc}
     *
     * @throws java.lang.NullPointerException
     *         if {@code key} or {@code value} is null
     * @throws java.lang.IllegalArgumentException
     *         if {@code key} is empty
     */
    @Override
    public final void setByteArray(String key, byte[] value) {
        setByteArray(key, ",", value, (String[]) null);
    }

    /**
     * {@inheritDoc}
     *
     * @throws java.lang.NullPointerException
     *         if {@code key} or {@code value} is null
     * @throws java.lang.IllegalArgumentException
     *         if {@code key} is empty
     */
    @Override
    public final void setByteArray(String key, byte[] value, String... comment) {
        setByteArray(key, ",", value, comment);
    }

    /**
     * {@inheritDoc}
     *
     * @throws java.lang.NullPointerException
     *         if {@code key} or {@code delimiter} is null
     * @throws java.lang.IllegalArgumentException
     *         if {@code key} or {@code delimiter} is empty
     * @throws java.lang.NumberFormatException
     *         if a value is not a number or out of range
     */
    @Override
    public final byte[] getByteArray(String key, String delimiter) {
        return StringUtils.stringToByteArray(getString(key), delimiter);
    }

    /**
     * {@inheritDoc}
     *
     * @throws java.lang.NullPointerException
     *         if {@code key} or {@code delimiter} or {@code def} is null
     * @throws java.lang.IllegalArgumentException
     *         if {@code key} or {@code delimiter} is empty
     */
    @Override
    public final byte[] getByteArray(String key, String delimiter, byte[] def) {
        if (containsKey(key)) {
            try {
                return StringUtils.stringToByteArray(getString(key), delimiter);
            }
            catch (NumberFormatException nfex) {
                // Continue with default
            }
        }
        setByteArray(key, delimiter, def);
        return def;
    }

    /**
     * {@inheritDoc}
     *
     * @throws java.lang.NullPointerException
     *         if {@code key} or {@code delimiter} or {@code value} is null
     * @throws java.lang.IllegalArgumentException
     *         if {@code key} or {@code delimiter} is empty
     */
    @Override
    public final void setByteArray(String key, String delimiter, byte[] value) {
        setByteArray(key, delimiter, value, (String[]) null);
    }

    /**
     * {@inheritDoc}
     *
     * @throws java.lang.NullPointerException
     *         if {@code key} or {@code delimiter} or {@code value} is null
     * @throws java.lang.IllegalArgumentException
     *         if {@code key} or {@code delimiter} is empty
     */
    @Override
    public final void setByteArray(String key, String delimiter, byte[] value, String... comment) {
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

    /**
     * {@inheritDoc}
     *
     * @throws java.lang.NullPointerException
     *         if {@code key} is null
     * @throws java.lang.IllegalArgumentException
     *         if {@code key} is empty
     * @throws net.visualillusionsent.utils.UnknownPropertyException
     *         if the {@code key} does not exist
     * @throws java.lang.NumberFormatException
     *         if a value is not a number or out of range
     */
    @Override
    public final short getShort(String key) {
        notNull(key, "String key");

        if (numberCache.containsKey(key)) {
            return numberCache.get(key).shortValue();
        }
        try {
            short value = Short.decode(getString(key));
            numberCache.put(key, value);
            return value;
        }
        catch (NumberFormatException nfe) {
            // Change Message
            throw new NumberFormatException(Verify.parse("prop.nan", key));
        }
    }

    /**
     * {@inheritDoc}
     *
     * @throws java.lang.NullPointerException
     *         if {@code key} is null
     * @throws java.lang.IllegalArgumentException
     *         if {@code key} is empty
     */
    @Override
    public final short getShort(String key, short def) {
        if (containsKey(key)) {
            try {
                return getShort(key);
            }
            catch (NumberFormatException nfex) {
                // Continue with default
            }
        }
        setShort(key, def);
        return def;
    }

    /**
     * {@inheritDoc}
     *
     * @throws java.lang.NullPointerException
     *         if {@code key} is null
     * @throws java.lang.IllegalArgumentException
     *         if {@code key} is empty
     */
    @Override
    public final void setShort(String key, short value) {
        setShort(key, value, (String[]) null);
    }

    /**
     * {@inheritDoc}
     *
     * @throws java.lang.NullPointerException
     *         if {@code key} is null
     * @throws java.lang.IllegalArgumentException
     *         if {@code key} is empty
     */
    @Override
    public final void setShort(String key, short value, String... comment) {
        notNull(key, "String key");
        notEmpty(key, "String key");

        String strValue = String.valueOf(value);
        if (strValue.equals(props.get(key))) {
            return;
        }
        numberCache.put(key, value);
        props.put(key, strValue);
        addComment(key, comment);
        this.hasChanged = true;
    }

    /**
     * {@inheritDoc}
     *
     * @throws java.lang.NullPointerException
     *         if {@code key} is null
     * @throws java.lang.IllegalArgumentException
     *         if {@code key} is empty
     * @throws net.visualillusionsent.utils.UnknownPropertyException
     *         if the {@code key} does not exist
     * @throws java.lang.NumberFormatException
     *         if a value is not a number or out of range
     */
    @Override
    public final short[] getShortArray(String key) {
        return getShortArray(key, ",");
    }

    /**
     * {@inheritDoc}
     *
     * @throws java.lang.NullPointerException
     *         if {@code key} or {@code def} is null
     * @throws java.lang.IllegalArgumentException
     *         if {@code key} is empty
     */
    @Override
    public final short[] getShortArray(String key, short[] def) {
        if (containsKey(key)) {
            try {
                return getShortArray(key, ",");
            }
            catch (NumberFormatException nfex) {
                // Continue with default
            }
        }
        setShortArray(key, def);
        return def;
    }

    /**
     * {@inheritDoc}
     *
     * @throws java.lang.NullPointerException
     *         if {@code key} or {@code value} is null
     * @throws java.lang.IllegalArgumentException
     *         if {@code key} is empty
     */
    @Override
    public final void setShortArray(String key, short[] value) {
        setShortArray(key, ",", value, (String[]) null);
    }

    /**
     * {@inheritDoc}
     *
     * @throws java.lang.NullPointerException
     *         if {@code key} or {@code value} is null
     * @throws java.lang.IllegalArgumentException
     *         if {@code key} is empty
     */
    @Override
    public final void setShortArray(String key, short[] value, String... comment) {
        setShortArray(key, ",", value, comment);
    }

    /**
     * {@inheritDoc}
     *
     * @throws java.lang.NullPointerException
     *         if {@code key} or {@code delimiter} is null
     * @throws java.lang.IllegalArgumentException
     *         if {@code key} or {@code delimiter} is empty
     * @throws java.lang.NumberFormatException
     *         if a value is not a number or out of range
     */
    @Override
    public final short[] getShortArray(String key, String delimiter) {
        return StringUtils.stringToShortArray(getString(key), delimiter);
    }

    /**
     * {@inheritDoc}
     *
     * @throws java.lang.NullPointerException
     *         if {@code key} or {@code delimiter} or {@code def} is null
     * @throws java.lang.IllegalArgumentException
     *         if {@code key} or {@code delimiter} is empty
     */
    @Override
    public final short[] getShortArray(String key, String delimiter, short[] def) {
        if (containsKey(key)) {
            try {
                return StringUtils.stringToShortArray(getString(key), delimiter);
            }
            catch (NumberFormatException nfex) {
                // Continue with default
            }
        }
        setShortArray(key, delimiter, def);
        return def;
    }

    /**
     * {@inheritDoc}
     *
     * @throws java.lang.NullPointerException
     *         if {@code key} or {@code delimiter} or {@code value} is null
     * @throws java.lang.IllegalArgumentException
     *         if {@code key} or {@code delimiter} is empty
     */
    @Override
    public final void setShortArray(String key, String delimiter, short[] value) {
        setShortArray(key, delimiter, value, (String[]) null);
    }

    /**
     * {@inheritDoc}
     *
     * @throws java.lang.NullPointerException
     *         if {@code key} or {@code delimiter} or {@code value} is null
     * @throws java.lang.IllegalArgumentException
     *         if {@code key} or {@code delimiter} is empty
     */
    @Override
    public final void setShortArray(String key, String delimiter, short[] value, String... comment) {
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

    /**
     * {@inheritDoc}
     *
     * @throws java.lang.NullPointerException
     *         if {@code key} is null
     * @throws java.lang.IllegalArgumentException
     *         if {@code key} is empty
     * @throws net.visualillusionsent.utils.UnknownPropertyException
     *         if the {@code key} does not exist
     * @throws java.lang.NumberFormatException
     *         if a value is not a number or out of range
     */
    @Override
    public final int getInt(String key) {
        notNull(key, "String key");

        if (numberCache.containsKey(key)) {
            return numberCache.get(key).intValue();
        }
        try {
            int value = Integer.decode(getString(key));
            numberCache.put(key, value);
            return value;
        }
        catch (NumberFormatException nfe) {
            // Change Message
            throw new NumberFormatException(Verify.parse("prop.nan", key));
        }
    }

    /**
     * {@inheritDoc}
     *
     * @throws java.lang.NullPointerException
     *         if {@code key} is null
     * @throws java.lang.IllegalArgumentException
     *         if {@code key} is empty
     */
    @Override
    public final int getInt(String key, int def) {
        if (containsKey(key)) {
            try {
                return getInt(key);
            }
            catch (NumberFormatException nfex) {
                // Continue with default
            }
        }
        setInt(key, def);
        return def;
    }

    /**
     * {@inheritDoc}
     *
     * @throws java.lang.NullPointerException
     *         if {@code key} is null
     * @throws java.lang.IllegalArgumentException
     *         if {@code key} is empty
     */
    @Override
    public final void setInt(String key, int value) {
        setInt(key, value, (String[]) null);
    }

    /**
     * {@inheritDoc}
     *
     * @throws java.lang.NullPointerException
     *         if {@code key} is null
     * @throws java.lang.IllegalArgumentException
     *         if {@code key} is empty
     */
    @Override
    public final void setInt(String key, int value, String... comment) {
        notNull(key, "String key");
        notEmpty(key, "String key");

        String strValue = String.valueOf(value);
        if (strValue.equals(props.get(key))) {
            return;
        }
        numberCache.put(key, value);
        props.put(key, strValue);
        addComment(key, comment);
        this.hasChanged = true;
    }

    /**
     * {@inheritDoc}
     *
     * @throws java.lang.NullPointerException
     *         if {@code key} is null
     * @throws java.lang.IllegalArgumentException
     *         if {@code key} is empty
     * @throws net.visualillusionsent.utils.UnknownPropertyException
     *         if the {@code key} does not exist
     * @throws java.lang.NumberFormatException
     *         if a value is not a number or out of range
     */
    @Override
    public final int[] getIntArray(String key) {
        return getIntArray(key, ",");
    }

    /**
     * {@inheritDoc}
     *
     * @throws java.lang.NullPointerException
     *         if {@code key} or {@code def} is null
     * @throws java.lang.IllegalArgumentException
     *         if {@code key} is empty
     */
    @Override
    public final int[] getIntArray(String key, int[] def) {
        if (containsKey(key)) {
            try {
                return getIntArray(key, ",");
            }
            catch (NumberFormatException nfex) {
                // Continue with default
            }
        }
        setIntArray(key, def);
        return def;
    }

    /**
     * {@inheritDoc}
     *
     * @throws java.lang.NullPointerException
     *         if {@code key} or {@code value} is null
     * @throws java.lang.IllegalArgumentException
     *         if {@code key} is empty
     */
    @Override
    public final void setIntArray(String key, int[] value) {
        setIntArray(key, ",", value, (String[]) null);
    }

    /**
     * {@inheritDoc}
     *
     * @throws java.lang.NullPointerException
     *         if {@code key} or {@code value} is null
     * @throws java.lang.IllegalArgumentException
     *         if {@code key} is empty
     */
    @Override
    public final void setIntArray(String key, int[] value, String... comment) {
        setIntArray(key, ",", value, comment);
    }

    /**
     * {@inheritDoc}
     *
     * @throws java.lang.NullPointerException
     *         if {@code key} or {@code delimiter} is null
     * @throws java.lang.IllegalArgumentException
     *         if {@code key} or {@code delimiter} is empty
     * @throws java.lang.NumberFormatException
     *         if a value is not a number or out of range
     */
    @Override
    public final int[] getIntArray(String key, String delimiter) {
        return StringUtils.stringToIntArray(getString(key), delimiter);
    }

    /**
     * {@inheritDoc}
     *
     * @throws java.lang.NullPointerException
     *         if {@code key} or {@code delimiter} or {@code def} is null
     * @throws java.lang.IllegalArgumentException
     *         if {@code key} or {@code delimiter} is empty
     */
    @Override
    public final int[] getIntArray(String key, String delimiter, int[] def) {
        if (containsKey(key)) {
            try {
                return StringUtils.stringToIntArray(getString(key), delimiter);
            }
            catch (NumberFormatException nfex) {
                // Continue with default
            }
        }
        setIntArray(key, delimiter, def);
        return def;
    }

    /**
     * {@inheritDoc}
     *
     * @throws java.lang.NullPointerException
     *         if {@code key} or {@code delimiter} or {@code value} is null
     * @throws java.lang.IllegalArgumentException
     *         if {@code key} or {@code delimiter} is empty
     */
    @Override
    public final void setIntArray(String key, String delimiter, int[] value) {
        setIntArray(key, delimiter, value, (String[]) null);
    }

    /**
     * {@inheritDoc}
     *
     * @throws java.lang.NullPointerException
     *         if {@code key} or {@code delimiter} or {@code value} is null
     * @throws java.lang.IllegalArgumentException
     *         if {@code key} or {@code delimiter} is empty
     */
    @Override
    public final void setIntArray(String key, String delimiter, int[] value, String... comment) {
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

    /**
     * {@inheritDoc}
     *
     * @throws java.lang.NullPointerException
     *         if {@code key} is null
     * @throws java.lang.IllegalArgumentException
     *         if {@code key} is empty
     * @throws net.visualillusionsent.utils.UnknownPropertyException
     *         if the {@code key} does not exist
     * @throws java.lang.NumberFormatException
     *         if a value is not a number or out of range
     */
    @Override
    public final long getLong(String key) {
        notNull(key, "String key");

        if (numberCache.containsKey(key)) {
            return numberCache.get(key).longValue();
        }
        try {
            long value = Long.decode(getString(key));
            numberCache.put(key, value);
            return value;
        }
        catch (NumberFormatException nfe) {
            // Change Message
            throw new NumberFormatException(Verify.parse("prop.nan", key));
        }
    }

    /**
     * {@inheritDoc}
     *
     * @throws java.lang.NullPointerException
     *         if {@code key} is null
     * @throws java.lang.IllegalArgumentException
     *         if {@code key} is empty
     */
    @Override
    public final long getLong(String key, long def) {
        if (containsKey(key)) {
            try {
                return getLong(key);
            }
            catch (NumberFormatException nfex) {
                // Continue with default
            }
        }
        setLong(key, def);
        return def;
    }

    /**
     * {@inheritDoc}
     *
     * @throws java.lang.NullPointerException
     *         if {@code key} is null
     * @throws java.lang.IllegalArgumentException
     *         if {@code key} is empty
     */
    @Override
    public final void setLong(String key, long value) {
        setLong(key, value, (String[]) null);
    }

    /**
     * {@inheritDoc}
     *
     * @throws java.lang.NullPointerException
     *         if {@code key} is null
     * @throws java.lang.IllegalArgumentException
     *         if {@code key} is empty
     */
    @Override
    public final void setLong(String key, long value, String... comment) {
        notNull(key, "String key");
        notEmpty(key, "String key");

        String strValue = String.valueOf(value);
        if (strValue.equals(props.get(key))) {
            return;
        }
        numberCache.put(key, value);
        props.put(key, strValue);
        addComment(key, comment);
        this.hasChanged = true;
    }

    /**
     * {@inheritDoc}
     *
     * @throws java.lang.NullPointerException
     *         if {@code key} is null
     * @throws java.lang.IllegalArgumentException
     *         if {@code key} is empty
     * @throws net.visualillusionsent.utils.UnknownPropertyException
     *         if the {@code key} does not exist
     * @throws java.lang.NumberFormatException
     *         if a value is not a number or out of range
     */
    @Override
    public final long[] getLongArray(String key) {
        return getLongArray(key, ",");
    }

    /**
     * {@inheritDoc}
     *
     * @throws java.lang.NullPointerException
     *         if {@code key} or {@code def} is null
     * @throws java.lang.IllegalArgumentException
     *         if {@code key} is empty
     */
    @Override
    public final long[] getLongArray(String key, long[] def) {
        if (containsKey(key)) {
            try {
                return getLongArray(key, ",");
            }
            catch (NumberFormatException nfex) {
                // Continue with default
            }
        }
        setLongArray(key, def);
        return def;
    }

    /**
     * {@inheritDoc}
     *
     * @throws java.lang.NullPointerException
     *         if {@code key} or {@code value} is null
     * @throws java.lang.IllegalArgumentException
     *         if {@code key} is empty
     */
    @Override
    public final void setLongArray(String key, long[] value) {
        setLongArray(key, ",", value, (String[]) null);
    }

    /**
     * {@inheritDoc}
     *
     * @throws java.lang.NullPointerException
     *         if {@code key} or {@code value} is null
     * @throws java.lang.IllegalArgumentException
     *         if {@code key} is empty
     */
    @Override
    public final void setLongArray(String key, long[] value, String... comment) {
        setLongArray(key, ",", value, comment);
    }

    /**
     * {@inheritDoc}
     *
     * @throws java.lang.NullPointerException
     *         if {@code key} or {@code delimiter} is null
     * @throws java.lang.IllegalArgumentException
     *         if {@code key} or {@code delimiter} is empty
     * @throws java.lang.NumberFormatException
     *         if a value is not a number or out of range
     */
    @Override
    public final long[] getLongArray(String key, String delimiter) {
        return StringUtils.stringToLongArray(getString(key), delimiter);
    }

    /**
     * {@inheritDoc}
     *
     * @throws java.lang.NullPointerException
     *         if {@code key} or {@code delimiter} or {@code def} is null
     * @throws java.lang.IllegalArgumentException
     *         if {@code key} or {@code delimiter} is empty
     */
    @Override
    public final long[] getLongArray(String key, String delimiter, long[] def) {
        if (containsKey(key)) {
            try {
                return StringUtils.stringToLongArray(getString(key), delimiter);
            }
            catch (NumberFormatException nfex) {
                // Continue with default
            }
        }
        setLongArray(key, delimiter, def);
        return def;
    }

    /**
     * {@inheritDoc}
     *
     * @throws java.lang.NullPointerException
     *         if {@code key} or {@code delimiter} or {@code value} is null
     * @throws java.lang.IllegalArgumentException
     *         if {@code key} or {@code delimiter} is empty
     */
    @Override
    public final void setLongArray(String key, String delimiter, long[] value) {
        setLongArray(key, delimiter, value, (String[]) null);
    }

    /**
     * {@inheritDoc}
     *
     * @throws java.lang.NullPointerException
     *         if {@code key} or {@code delimiter} or {@code value} is null
     * @throws java.lang.IllegalArgumentException
     *         if {@code key} or {@code delimiter} is empty
     */
    @Override
    public final void setLongArray(String key, String delimiter, long[] value, String... comment) {
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

    /**
     * {@inheritDoc}
     *
     * @throws java.lang.NullPointerException
     *         if {@code key} is null
     * @throws java.lang.IllegalArgumentException
     *         if {@code key} is empty
     * @throws net.visualillusionsent.utils.UnknownPropertyException
     *         if the {@code key} does not exist
     * @throws java.lang.NumberFormatException
     *         if a value is not a number or out of range
     */
    @Override
    public final float getFloat(String key) {
        notNull(key, "String key");
        if (numberCache.containsKey(key)) {
            return numberCache.get(key).floatValue();
        }
        try {
            float value = Float.parseFloat(getString(key));
            numberCache.put(key, value);
            return value;
        }
        catch (NumberFormatException nfe) {
            // Change Message
            throw new NumberFormatException(Verify.parse("prop.nan", key));
        }
    }

    /**
     * {@inheritDoc}
     *
     * @throws java.lang.NullPointerException
     *         if {@code key} is null
     * @throws java.lang.IllegalArgumentException
     *         if {@code key} is empty
     */
    @Override
    public final float getFloat(String key, float def) {
        if (containsKey(key)) {
            try {
                return getFloat(key);
            }
            catch (NumberFormatException nfex) {
                // Continue with default
            }
        }
        setFloat(key, def);
        return def;
    }

    /**
     * {@inheritDoc}
     *
     * @throws java.lang.NullPointerException
     *         if {@code key} is null
     * @throws java.lang.IllegalArgumentException
     *         if {@code key} is empty
     */
    @Override
    public final void setFloat(String key, float value) {
        setFloat(key, value, (String[]) null);
    }

    /**
     * {@inheritDoc}
     *
     * @throws java.lang.NullPointerException
     *         if {@code key} is null
     * @throws java.lang.IllegalArgumentException
     *         if {@code key} is empty
     */
    @Override
    public final void setFloat(String key, float value, String... comment) {
        notNull(key, "String key");
        notEmpty(key, "String key");

        String strValue = String.valueOf(value);
        if (strValue.equals(props.get(key))) {
            return;
        }
        numberCache.put(key, value);
        props.put(key, strValue);
        addComment(key, comment);
        this.hasChanged = true;
    }

    /**
     * {@inheritDoc}
     *
     * @throws java.lang.NullPointerException
     *         if {@code key} is null
     * @throws java.lang.IllegalArgumentException
     *         if {@code key} is empty
     * @throws net.visualillusionsent.utils.UnknownPropertyException
     *         if the {@code key} does not exist
     * @throws java.lang.NumberFormatException
     *         if a value is not a number or out of range
     */
    @Override
    public final float[] getFloatArray(String key) {
        return getFloatArray(key, ",");
    }

    /**
     * {@inheritDoc}
     *
     * @throws java.lang.NullPointerException
     *         if {@code key} or {@code def} is null
     * @throws java.lang.IllegalArgumentException
     *         if {@code key} is empty
     */
    @Override
    public final float[] getFloatArray(String key, float[] def) {
        if (containsKey(key)) {
            try {
                return getFloatArray(key, ",");
            }
            catch (NumberFormatException nfex) {
                // Continue with default
            }
        }
        setFloatArray(key, def);
        return def;
    }

    /**
     * {@inheritDoc}
     *
     * @throws java.lang.NullPointerException
     *         if {@code key} or {@code value} is null
     * @throws java.lang.IllegalArgumentException
     *         if {@code key} is empty
     */
    @Override
    public final void setFloatArray(String key, float[] value) {
        setFloatArray(key, ",", value, (String[]) null);
    }

    /**
     * {@inheritDoc}
     *
     * @throws java.lang.NullPointerException
     *         if {@code key} or {@code value} is null
     * @throws java.lang.IllegalArgumentException
     *         if {@code key} is empty
     */
    @Override
    public final void setFloatArray(String key, float[] value, String... comment) {
        setFloatArray(key, ",", value, comment);
    }

    /**
     * {@inheritDoc}
     *
     * @throws java.lang.NullPointerException
     *         if {@code key} or {@code delimiter} is null
     * @throws java.lang.IllegalArgumentException
     *         if {@code key} or {@code delimiter} is empty
     * @throws java.lang.NumberFormatException
     *         if a value is not a number or out of range
     */
    @Override
    public final float[] getFloatArray(String key, String delimiter) {
        return StringUtils.stringToFloatArray(getString(key), delimiter);
    }

    /**
     * {@inheritDoc}
     *
     * @throws java.lang.NullPointerException
     *         if {@code key} or {@code delimiter} or {@code def} is null
     * @throws java.lang.IllegalArgumentException
     *         if {@code key} or {@code delimiter} is empty
     */
    @Override
    public final float[] getFloatArray(String key, String delimiter, float[] def) {
        if (containsKey(key)) {
            try {
                return StringUtils.stringToFloatArray(getString(key), delimiter);
            }
            catch (NumberFormatException nfex) {
                // Continue with default
            }
        }
        setFloatArray(key, delimiter, def);
        return def;
    }

    /**
     * {@inheritDoc}
     *
     * @throws java.lang.NullPointerException
     *         if {@code key} or {@code delimiter} or {@code value} is null
     * @throws java.lang.IllegalArgumentException
     *         if {@code key} or {@code delimiter} is empty
     */
    @Override
    public final void setFloatArray(String key, String delimiter, float[] value) {
        setFloatArray(key, delimiter, value, (String[]) null);
    }

    /**
     * {@inheritDoc}
     *
     * @throws java.lang.NullPointerException
     *         if {@code key} or {@code delimiter} or {@code value} is null
     * @throws java.lang.IllegalArgumentException
     *         if {@code key} or {@code delimiter} is empty
     */
    @Override
    public final void setFloatArray(String key, String delimiter, float[] value, String... comment) {
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

    /**
     * {@inheritDoc}
     *
     * @throws java.lang.NullPointerException
     *         if {@code key} is null
     * @throws java.lang.IllegalArgumentException
     *         if {@code key} is empty
     * @throws net.visualillusionsent.utils.UnknownPropertyException
     *         if the {@code key} does not exist
     * @throws java.lang.NumberFormatException
     *         if a value is not a number or out of range
     */
    @Override
    public final double getDouble(String key) {
        notNull(key, "String key");
        if (numberCache.containsKey(key)) {
            return numberCache.get(key).doubleValue();
        }
        try {
            double value = Double.parseDouble(getString(key));
            numberCache.put(key, value);
            return value;
        }
        catch (NumberFormatException nfe) {
            // Change Message
            throw new NumberFormatException(Verify.parse("prop.nan", key));
        }
    }

    /**
     * {@inheritDoc}
     *
     * @throws java.lang.NullPointerException
     *         if {@code key} is null
     * @throws java.lang.IllegalArgumentException
     *         if {@code key} is empty
     */
    @Override
    public final double getDouble(String key, double def) {
        if (containsKey(key)) {
            try {
                return getDouble(key);
            }
            catch (NumberFormatException nfex) {
                // Continue with default
            }
        }
        setDouble(key, def);
        return def;
    }

    /**
     * {@inheritDoc}
     *
     * @throws java.lang.NullPointerException
     *         if {@code key} is null
     * @throws java.lang.IllegalArgumentException
     *         if {@code key} is empty
     */
    @Override
    public final void setDouble(String key, double value) {
        setDouble(key, value, (String[]) null);
    }

    /**
     * {@inheritDoc}
     *
     * @throws java.lang.NullPointerException
     *         if {@code key} is null
     * @throws java.lang.IllegalArgumentException
     *         if {@code key} is empty
     */
    @Override
    public final void setDouble(String key, double value, String... comment) {
        notNull(key, "String key");
        notEmpty(key, "String key");

        String strValue = String.valueOf(value);
        if (strValue.equals(props.get(key))) {
            return;
        }
        numberCache.put(key, value);
        props.put(key, strValue);
        addComment(key, comment);
        this.hasChanged = true;
    }

    /**
     * {@inheritDoc}
     *
     * @throws java.lang.NullPointerException
     *         if {@code key} is null
     * @throws java.lang.IllegalArgumentException
     *         if {@code key} is empty
     * @throws net.visualillusionsent.utils.UnknownPropertyException
     *         if the {@code key} does not exist
     * @throws java.lang.NumberFormatException
     *         if a value is not a number or out of range
     */
    @Override
    public final double[] getDoubleArray(String key) {
        return getDoubleArray(key, ",");
    }

    /**
     * {@inheritDoc}
     *
     * @throws java.lang.NullPointerException
     *         if {@code key} or {@code def} is null
     * @throws java.lang.IllegalArgumentException
     *         if {@code key} is empty
     */
    @Override
    public final double[] getDoubleArray(String key, double[] def) {
        if (containsKey(key)) {
            try {
                return getDoubleArray(key, ",");
            }
            catch (NumberFormatException nfex) {
                // Continue with default
            }
        }
        setDoubleArray(key, def);
        return def;
    }

    /**
     * {@inheritDoc}
     *
     * @throws java.lang.NullPointerException
     *         if {@code key} or {@code value} is null
     * @throws java.lang.IllegalArgumentException
     *         if {@code key} is empty
     */
    @Override
    public final void setDoubleArray(String key, double[] value) {
        setDoubleArray(key, ",", value, (String[]) null);
    }

    /**
     * {@inheritDoc}
     *
     * @throws java.lang.NullPointerException
     *         if {@code key} or {@code value} is null
     * @throws java.lang.IllegalArgumentException
     *         if {@code key} is empty
     */
    @Override
    public final void setDoubleArray(String key, double[] value, String... comment) {
        setDoubleArray(key, ",", value, comment);
    }

    /**
     * {@inheritDoc}
     *
     * @throws java.lang.NullPointerException
     *         if {@code key} or {@code delimiter} is null
     * @throws java.lang.IllegalArgumentException
     *         if {@code key} or {@code delimiter} is empty
     * @throws java.lang.NumberFormatException
     *         if a value is not a number or out of range
     */
    @Override
    public final double[] getDoubleArray(String key, String delimiter) {
        return StringUtils.stringToDoubleArray(getString(key), delimiter);
    }

    /**
     * {@inheritDoc}
     *
     * @throws java.lang.NullPointerException
     *         if {@code key} or {@code delimiter} or {@code def} is null
     * @throws java.lang.IllegalArgumentException
     *         if {@code key} or {@code delimiter} is empty
     */
    @Override
    public final double[] getDoubleArray(String key, String delimiter, double[] def) {
        if (containsKey(key)) {
            try {
                return StringUtils.stringToDoubleArray(getString(key), delimiter);
            }
            catch (NumberFormatException nfex) {
                // Continue with default
            }
        }
        setDoubleArray(key, delimiter, def);
        return def;
    }

    /**
     * {@inheritDoc}
     *
     * @throws java.lang.NullPointerException
     *         if {@code key} or {@code delimiter} or {@code value} is null
     * @throws java.lang.IllegalArgumentException
     *         if {@code key} or {@code delimiter} is empty
     */
    @Override
    public final void setDoubleArray(String key, String delimiter, double[] value) {
        setDoubleArray(key, delimiter, value, (String[]) null);
    }

    /**
     * {@inheritDoc}
     *
     * @throws java.lang.NullPointerException
     *         if {@code key} or {@code delimiter} or {@code value} is null
     * @throws java.lang.IllegalArgumentException
     *         if {@code key} or {@code delimiter} is empty
     */
    @Override
    public final void setDoubleArray(String key, String delimiter, double[] value, String... comment) {
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

    /**
     * {@inheritDoc}
     *
     * @throws java.lang.NullPointerException
     *         if {@code key} is null
     * @throws java.lang.IllegalArgumentException
     *         if {@code key} is empty
     * @throws net.visualillusionsent.utils.UnknownPropertyException
     *         if the {@code key} does not exist
     */
    @Override
    public final boolean getBoolean(String key) {
        if (booleanCache.containsKey(key)) {
            return booleanCache.get(key);
        }
        boolean value = BooleanUtils.parseBoolean(getString(key));
        booleanCache.put(key, value);
        return value;
    }

    /**
     * {@inheritDoc}
     *
     * @throws java.lang.NullPointerException
     *         if {@code key} is null
     * @throws java.lang.IllegalArgumentException
     *         if {@code key} is empty
     */
    @Override
    public final boolean getBoolean(String key, boolean def) {
        if (containsKey(key)) {
            return getBoolean(key);
        }
        else {
            setBoolean(key, def);
            return def;
        }
    }

    /**
     * {@inheritDoc}
     *
     * @throws java.lang.NullPointerException
     *         if {@code key} is null
     * @throws java.lang.IllegalArgumentException
     *         if {@code key} is empty
     */
    @Override
    public final void setBoolean(String key, boolean value) {
        setBoolean(key, value, (String[]) null);
    }

    /**
     * {@inheritDoc}
     *
     * @throws java.lang.NullPointerException
     *         if {@code key} is null
     * @throws java.lang.IllegalArgumentException
     *         if {@code key} is empty
     */
    @Override
    public final void setBoolean(String key, boolean value, String... comment) {
        notNull(key, "String key");
        notEmpty(key, "String key");

        String strValue = String.valueOf(value);
        if (strValue.equals(props.get(key))) {
            return;
        }
        booleanCache.put(key, value);
        props.put(key, strValue);
        addComment(key, comment);
        this.hasChanged = true;
    }

    /**
     * {@inheritDoc}
     *
     * @throws java.lang.NullPointerException
     *         if {@code key} is null
     * @throws java.lang.IllegalArgumentException
     *         if {@code key} is empty
     * @throws net.visualillusionsent.utils.UnknownPropertyException
     *         if the {@code key} does not exist
     */
    @Override
    public final boolean[] getBooleanArray(String key) {
        return getBooleanArray(key, ",");
    }

    /**
     * {@inheritDoc}
     *
     * @throws java.lang.NullPointerException
     *         if {@code key} or {@code def} is null
     * @throws java.lang.IllegalArgumentException
     *         if {@code key} is empty
     */
    @Override
    public final boolean[] getBooleanArray(String key, boolean[] def) {
        if (containsKey(key)) {
            return getBooleanArray(key, ",");
        }
        else {
            setBooleanArray(key, def);
            return def;
        }
    }

    /**
     * {@inheritDoc}
     *
     * @throws java.lang.NullPointerException
     *         if {@code key} or {@code value} is null
     * @throws java.lang.IllegalArgumentException
     *         if {@code key} is empty
     */
    @Override
    public final void setBooleanArray(String key, boolean[] value) {
        setBooleanArray(key, ",", value, (String[]) null);
    }

    /**
     * {@inheritDoc}
     *
     * @throws java.lang.NullPointerException
     *         if {@code key} or {@code value} is null
     * @throws java.lang.IllegalArgumentException
     *         if {@code key} is empty
     */
    @Override
    public final void setBooleanArray(String key, boolean[] value, String... comment) {
        setBooleanArray(key, ",", value, comment);
    }

    /**
     * {@inheritDoc}
     *
     * @throws java.lang.NullPointerException
     *         if {@code key} or {@code delimiter} is null
     * @throws java.lang.IllegalArgumentException
     *         if {@code key} or {@code delimiter} is empty
     */
    @Override
    public final boolean[] getBooleanArray(String key, String delimiter) {
        return StringUtils.stringToBooleanArray(getString(key), delimiter);
    }

    /**
     * {@inheritDoc}
     *
     * @throws java.lang.NullPointerException
     *         if {@code key} or {@code delimiter} or {@code def} is null
     * @throws java.lang.IllegalArgumentException
     *         if {@code key} or {@code delimiter} is empty
     */
    @Override
    public final boolean[] getBooleanArray(String key, String delimiter, boolean[] def) {
        if (containsKey(key)) {
            return StringUtils.stringToBooleanArray(getString(key), delimiter);
        }
        else {
            setBooleanArray(key, delimiter, def);
            return def;
        }
    }

    /**
     * {@inheritDoc}
     *
     * @throws java.lang.NullPointerException
     *         if {@code key} or {@code delimiter} or {@code value} is null
     * @throws java.lang.IllegalArgumentException
     *         if {@code key} or {@code delimiter} is empty
     */
    @Override
    public final void setBooleanArray(String key, String delimiter, boolean[] value) {
        setBooleanArray(key, delimiter, value, (String[]) null);
    }

    /**
     * {@inheritDoc}
     *
     * @throws java.lang.NullPointerException
     *         if {@code key} or {@code delimiter} or {@code value} is null
     * @throws java.lang.IllegalArgumentException
     *         if {@code key} or {@code delimiter} is empty
     */
    @Override
    public final void setBooleanArray(String key, String delimiter, boolean[] value, String... comment) {
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

    /**
     * {@inheritDoc}
     *
     * @throws java.lang.NullPointerException
     *         if {@code key} is null
     * @throws java.lang.IllegalArgumentException
     *         if {@code key} is empty
     * @throws net.visualillusionsent.utils.UnknownPropertyException
     *         if the {@code key} does not exist
     */
    @Override
    public final char getCharacter(String key) {
        return getString(key).trim().charAt(0);
    }

    /**
     * {@inheritDoc}
     *
     * @throws java.lang.NullPointerException
     *         if {@code key} is null
     * @throws java.lang.IllegalArgumentException
     *         if {@code key} is empty
     */
    @Override
    public final char getCharacter(String key, char def) {
        if (containsKey(key)) {
            return getString(key).trim().charAt(0);
        }
        else {
            setCharacter(key, def);
            return def;
        }
    }

    /**
     * {@inheritDoc}
     *
     * @throws java.lang.NullPointerException
     *         if {@code key} is null
     * @throws java.lang.IllegalArgumentException
     *         if {@code key} is empty
     */
    @Override
    public final void setCharacter(String key, char ch) {
        setCharacter(key, ch, (String[]) null);
    }

    /**
     * {@inheritDoc}
     *
     * @throws java.lang.NullPointerException
     *         if {@code key} is null
     * @throws java.lang.IllegalArgumentException
     *         if {@code key} is empty
     */
    @Override
    public final void setCharacter(String key, char ch, String... comment) {
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
            return comments.get(key).toArray(new String[comments.get(key).size()]);
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
        if (this.zip != null && this.zip != that.zip) {
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
        return String.format("PropertiesFile[FilePath=%s]", propsFile != null ? propsFile.getAbsolutePath() : zip.getName() + ":" + filePath);
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
        hash = 45 * hash + (zip != null ? zip.hashCode() : 0);
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
