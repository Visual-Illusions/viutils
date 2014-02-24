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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;

import static net.visualillusionsent.utils.Verify.notEmpty;
import static net.visualillusionsent.utils.Verify.notEmptyNoTrim;
import static net.visualillusionsent.utils.Verify.notNull;

/**
 * An Unmodifiable Properties File implementation
 *
 * @author Jason (darkdiplomat)
 * @version 1.2
 * @since 1.1.0
 */
public final class UnmodifiablePropertiesFile extends AbstractPropertiesFile {
    /* VIU 1.4.0 / 1.2 */
    private static final float classVersion = 1.2F;

    /**
     * {@inheritDoc}
     *
     * @throws PropertiesFileException
     *         if there was an error with reading the properties file
     */
    public UnmodifiablePropertiesFile(String filePath) {
        super(filePath);
        if (propsFile.exists()) {
            try {
                load(new FileInputStream(propsFile));
            }
            catch (FileNotFoundException e) {
                throw new UtilityException("file.err.ioe", filePath);
            }
        }
    }

    /**
     * {@inheritDoc}
     *
     * @throws PropertiesFileException
     *         if there was an error with reading the properties file
     */
    public UnmodifiablePropertiesFile(File file) {
        super(file);
        if (propsFile.exists()) {
            try {
                load(new FileInputStream(propsFile));
            }
            catch (FileNotFoundException e) {
                throw new UtilityException("file.err.ioe", filePath);
            }
        }
    }

    /**
     * {@inheritDoc}
     *
     * @throws PropertiesFileException
     *         if there was an error with reading the properties file
     */
    public UnmodifiablePropertiesFile(String jarPath, String entry) {
        super(jarPath, entry);
        JarEntry ent = jar.getJarEntry(entry);
        try {
            load(jar.getInputStream(ent));
        }
        catch (IOException e) {
            throw new UtilityException("file.err.ioe", filePath);
        }
    }

    /**
     * Loads the Properties File
     *
     * @throws PropertiesFileException
     *         if there was an error with reading the properties file
     */
    @Override
    protected final void load(InputStream instream) {
        HashMap<String, String> tempProps = new HashMap<String, String>();
        HashMap<String, String> tempInLine = new HashMap<String, String>();
        HashMap<String, List<String>> tempCom = new HashMap<String, List<String>>();
        List<String> tempHead = new LinkedList<String>();
        List<String> tempFoot = new LinkedList<String>();
        UtilityException uex = null;
        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(instream, "UTF-8"));
            String inLine;
            LinkedList<String> inComments = new LinkedList<String>();
            while ((inLine = in.readLine()) != null) {
                if (inLine.startsWith(";#")) {
                    tempHead.add(inLine);
                }
                else if (inLine.startsWith("#;")) {
                    tempFoot.add(inLine);
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
                            String inlinec = value.split("#!")[1]; //Don't trim the comment
                            tempInLine.put(key, inlinec);
                            value = value.split("#!")[0];
                        }
                        tempProps.put(key.trim(), value.replace("\\#\\!", "#!").trim()); //remove escape sequence
                    }
                    catch (ArrayIndexOutOfBoundsException aioobe) {
                        //Empty value?
                        if (inLine.contains("=")) {
                            tempProps.put(propsLine[0], "");
                        }
                        //Incomplete property, drop reference
                        else {
                            inComments.clear();
                            continue;
                        }
                    }
                    if (!inComments.isEmpty()) {
                        LinkedList<String> temp = new LinkedList<String>(inComments);
                        Collections.copy(temp, inComments);
                        tempCom.put(propsLine[0], Collections.unmodifiableList(new LinkedList<String>(temp)));
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
            if (uex != null) {
                throw uex;
            }
        }
        this.props = Collections.unmodifiableMap(tempProps);
        this.inlineCom = Collections.unmodifiableMap(tempInLine);
        this.comments = Collections.unmodifiableMap(tempCom);
        this.header = Collections.unmodifiableList(tempHead);
        this.footer = Collections.unmodifiableList(tempFoot);
        this.booleanCache = new HashMap<String, Boolean>();
        this.numberCache = new HashMap<String, Number>();
    }

    /** {@inheritDoc} */
    @Override
    public final void reload() {
        //props.clear();  UNSUPPORTED
        //comments.clear(); UNSUPPORTED
        if (jar != null) {
            JarEntry ent = jar.getJarEntry(filePath);
            if (ent == null) {
                throw new PropertiesFileException("entry.missing", filePath);
            }
            try {
                load(jar.getInputStream(ent));
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
    }

    /**
     * Unsupported Operation with UnmodifiablePropertiesFiles
     *
     * @throws UnsupportedOperationException
     *         Not supported with Unmodifiable Properties Files
     */
    @Override
    protected final void save() {
        throw new UnsupportedOperationException("Unable to modify an UnmodifiablePropertiesFile");
    }

    /**
     * Unsupported Operation with UnmodifiablePropertiesFiles
     *
     * @throws UnsupportedOperationException
     *         Not supported with Unmodifiable Properties Files
     */
    @Override
    protected final void forceSave() {
        throw new UnsupportedOperationException("Unable to modify an UnmodifiablePropertiesFile");
    }

    /**
     * Unsupported Operation with UnmodifiablePropertiesFiles
     *
     * @throws UnsupportedOperationException
     *         Not supported with Unmodifiable Properties Files
     */
    @Override
    protected final void save(boolean force) {
        throw new UnsupportedOperationException("Unable to modify an UnmodifiablePropertiesFile");
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
    protected final void removeKey(String key) {
        throw new UnsupportedOperationException("Unable to modify an UnmodifiablePropertiesFile");
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
        throw new UnsupportedOperationException("Unable to modify an UnmodifiablePropertiesFile");
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
        return def;
    }

    /**
     * Unsupported Operation with UnmodifiablePropertiesFiles
     *
     * @throws UnsupportedOperationException
     *         Not supported with Unmodifiable Properties Files
     */
    @Override
    protected final void setString(String key, String value) {
        throw new UnsupportedOperationException("Unable to modify an UnmodifiablePropertiesFile");
    }

    /**
     * Unsupported Operation with UnmodifiablePropertiesFiles
     *
     * @throws UnsupportedOperationException
     *         Not supported with Unmodifiable Properties Files
     */
    @Override
    protected final void setString(String key, String value, String... comment) {
        throw new UnsupportedOperationException("Unable to modify an UnmodifiablePropertiesFile");
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
        return def;
    }

    /**
     * Unsupported Operation with UnmodifiablePropertiesFiles
     *
     * @throws UnsupportedOperationException
     *         Not supported with Unmodifiable Properties Files
     */
    @Override
    protected final void setStringArray(String key, String[] value) {
        throw new UnsupportedOperationException("Unable to modify an UnmodifiablePropertiesFile");
    }

    /**
     * Unsupported Operation with UnmodifiablePropertiesFiles
     *
     * @throws UnsupportedOperationException
     *         Not supported with Unmodifiable Properties Files
     */
    @Override
    protected final void setStringArray(String key, String[] value, String... comment) {
        throw new UnsupportedOperationException("Unable to modify an UnmodifiablePropertiesFile");
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
        return def;
    }

    /**
     * Unsupported Operation with UnmodifiablePropertiesFiles
     *
     * @throws UnsupportedOperationException
     *         Not supported with Unmodifiable Properties Files
     */
    @Override
    protected final void setStringArray(String key, String delimiter, String[] value) {
        throw new UnsupportedOperationException("Unable to modify an UnmodifiablePropertiesFile");
    }

    /**
     * Unsupported Operation with UnmodifiablePropertiesFiles
     *
     * @throws UnsupportedOperationException
     *         Not supported with Unmodifiable Properties Files
     */
    @Override
    protected final void setStringArray(String key, String delimiter, String[] value, String... comment) {
        throw new UnsupportedOperationException("Unable to modify an UnmodifiablePropertiesFile");
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
        return def;
    }

    /**
     * Unsupported Operation with UnmodifiablePropertiesFiles
     *
     * @throws UnsupportedOperationException
     *         Not supported with Unmodifiable Properties Files
     */
    @Override
    protected final void setByte(String key, byte value) {
        throw new UnsupportedOperationException("Unable to modify an UnmodifiablePropertiesFile");
    }

    /**
     * Unsupported Operation with UnmodifiablePropertiesFiles
     *
     * @throws UnsupportedOperationException
     *         Not supported with Unmodifiable Properties Files
     */
    @Override
    protected final void setByte(String key, byte value, String... comment) {
        throw new UnsupportedOperationException("Unable to modify an UnmodifiablePropertiesFile");
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
     * Unsupported Operation with UnmodifiablePropertiesFiles
     *
     * @throws UnsupportedOperationException
     *         Not supported with Unmodifiable Properties Files
     */
    @Override
    protected final void setByteArray(String key, byte[] value) {
        throw new UnsupportedOperationException("Unable to modify an UnmodifiablePropertiesFile");
    }

    /**
     * Unsupported Operation with UnmodifiablePropertiesFiles
     *
     * @throws UnsupportedOperationException
     *         Not supported with Unmodifiable Properties Files
     */
    @Override
    protected final void setByteArray(String key, byte[] value, String... comment) {
        throw new UnsupportedOperationException("Unable to modify an UnmodifiablePropertiesFile");
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
        return def;
    }

    /**
     * Unsupported Operation with UnmodifiablePropertiesFiles
     *
     * @throws UnsupportedOperationException
     *         Not supported with Unmodifiable Properties Files
     */
    @Override
    protected final void setByteArray(String key, String delimiter, byte[] value) {
        throw new UnsupportedOperationException("Unable to modify an UnmodifiablePropertiesFile");
    }

    /**
     * Unsupported Operation with UnmodifiablePropertiesFiles
     *
     * @throws UnsupportedOperationException
     *         Not supported with Unmodifiable Properties Files
     */
    @Override
    protected final void setByteArray(String key, String delimiter, byte[] value, String... comment) {
        throw new UnsupportedOperationException("Unable to modify an UnmodifiablePropertiesFile");
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
        return def;
    }

    /**
     * Unsupported Operation with UnmodifiablePropertiesFiles
     *
     * @throws UnsupportedOperationException
     *         Not supported with Unmodifiable Properties Files
     */
    @Override
    protected final void setShort(String key, short value) {
        throw new UnsupportedOperationException("Unable to modify an UnmodifiablePropertiesFile");
    }

    /**
     * Unsupported Operation with UnmodifiablePropertiesFiles
     *
     * @throws UnsupportedOperationException
     *         Not supported with Unmodifiable Properties Files
     */
    @Override
    protected final void setShort(String key, short value, String... comment) {
        throw new UnsupportedOperationException("Unable to modify an UnmodifiablePropertiesFile");
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
        return def;
    }

    /**
     * Unsupported Operation with UnmodifiablePropertiesFiles
     *
     * @throws UnsupportedOperationException
     *         Not supported with Unmodifiable Properties Files
     */
    @Override
    protected final void setShortArray(String key, short[] value) {
        throw new UnsupportedOperationException("Unable to modify an UnmodifiablePropertiesFile");
    }

    /**
     * Unsupported Operation with UnmodifiablePropertiesFiles
     *
     * @throws UnsupportedOperationException
     *         Not supported with Unmodifiable Properties Files
     */
    @Override
    protected final void setShortArray(String key, short[] value, String... comment) {
        throw new UnsupportedOperationException("Unable to modify an UnmodifiablePropertiesFile");
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
        return def;
    }

    /**
     * Unsupported Operation with UnmodifiablePropertiesFiles
     *
     * @throws UnsupportedOperationException
     *         Not supported with Unmodifiable Properties Files
     */
    @Override
    protected final void setShortArray(String key, String delimiter, short[] value) {
        throw new UnsupportedOperationException("Unable to modify an UnmodifiablePropertiesFile");
    }

    /**
     * Unsupported Operation with UnmodifiablePropertiesFiles
     *
     * @throws UnsupportedOperationException
     *         Not supported with Unmodifiable Properties Files
     */
    @Override
    protected final void setShortArray(String key, String delimiter, short[] value, String... comment) {
        throw new UnsupportedOperationException("Unable to modify an UnmodifiablePropertiesFile");
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
        return def;
    }

    /**
     * Unsupported Operation with UnmodifiablePropertiesFiles
     *
     * @throws UnsupportedOperationException
     *         Not supported with Unmodifiable Properties Files
     */
    @Override
    protected final void setInt(String key, int value) {
        throw new UnsupportedOperationException("Unable to modify an UnmodifiablePropertiesFile");
    }

    /**
     * Unsupported Operation with UnmodifiablePropertiesFiles
     *
     * @throws UnsupportedOperationException
     *         Not supported with Unmodifiable Properties Files
     */
    @Override
    protected final void setInt(String key, int value, String... comment) {
        throw new UnsupportedOperationException("Unable to modify an UnmodifiablePropertiesFile");
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
        return def;
    }

    /**
     * Unsupported Operation with UnmodifiablePropertiesFiles
     *
     * @throws UnsupportedOperationException
     *         Not supported with Unmodifiable Properties Files
     */
    @Override
    protected final void setIntArray(String key, int[] value) {
        throw new UnsupportedOperationException("Unable to modify an UnmodifiablePropertiesFile");
    }

    /**
     * Unsupported Operation with UnmodifiablePropertiesFiles
     *
     * @throws UnsupportedOperationException
     *         Not supported with Unmodifiable Properties Files
     */
    @Override
    protected final void setIntArray(String key, int[] value, String... comment) {
        throw new UnsupportedOperationException("Unable to modify an UnmodifiablePropertiesFile");
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
        return def;
    }

    /**
     * Unsupported Operation with UnmodifiablePropertiesFiles
     *
     * @throws UnsupportedOperationException
     *         Not supported with Unmodifiable Properties Files
     */
    @Override
    protected final void setIntArray(String key, String delimiter, int[] value) {
        throw new UnsupportedOperationException("Unable to modify an UnmodifiablePropertiesFile");
    }

    /**
     * Unsupported Operation with UnmodifiablePropertiesFiles
     *
     * @throws UnsupportedOperationException
     *         Not supported with Unmodifiable Properties Files
     */
    @Override
    protected final void setIntArray(String key, String delimiter, int[] value, String... comment) {
        throw new UnsupportedOperationException("Unable to modify an UnmodifiablePropertiesFile");
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
        return def;
    }

    /**
     * Unsupported Operation with UnmodifiablePropertiesFiles
     *
     * @throws UnsupportedOperationException
     *         Not supported with Unmodifiable Properties Files
     */
    @Override
    protected final void setLong(String key, long value) {
        throw new UnsupportedOperationException("Unable to modify an UnmodifiablePropertiesFile");
    }

    /**
     * Unsupported Operation with UnmodifiablePropertiesFiles
     *
     * @throws UnsupportedOperationException
     *         Not supported with Unmodifiable Properties Files
     */
    @Override
    protected final void setLong(String key, long value, String... comment) {
        throw new UnsupportedOperationException("Unable to modify an UnmodifiablePropertiesFile");
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
        return def;
    }

    /**
     * Unsupported Operation with UnmodifiablePropertiesFiles
     *
     * @throws UnsupportedOperationException
     *         Not supported with Unmodifiable Properties Files
     */
    @Override
    protected final void setLongArray(String key, long[] value) {
        throw new UnsupportedOperationException("Unable to modify an UnmodifiablePropertiesFile");
    }

    /**
     * Unsupported Operation with UnmodifiablePropertiesFiles
     *
     * @throws UnsupportedOperationException
     *         Not supported with Unmodifiable Properties Files
     */
    @Override
    protected final void setLongArray(String key, long[] value, String... comment) {
        throw new UnsupportedOperationException("Unable to modify an UnmodifiablePropertiesFile");
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
        return def;
    }

    /**
     * Unsupported Operation with UnmodifiablePropertiesFiles
     *
     * @throws UnsupportedOperationException
     *         Not supported with Unmodifiable Properties Files
     */
    @Override
    protected final void setLongArray(String key, String delimiter, long[] value) {
        throw new UnsupportedOperationException("Unable to modify an UnmodifiablePropertiesFile");
    }

    /**
     * Unsupported Operation with UnmodifiablePropertiesFiles
     *
     * @throws UnsupportedOperationException
     *         Not supported with Unmodifiable Properties Files
     */
    @Override
    protected final void setLongArray(String key, String delimiter, long[] value, String... comment) {
        throw new UnsupportedOperationException("Unable to modify an UnmodifiablePropertiesFile");
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
        return def;
    }

    /**
     * Unsupported Operation with UnmodifiablePropertiesFiles
     *
     * @throws UnsupportedOperationException
     *         Not supported with Unmodifiable Properties Files
     */
    @Override
    protected final void setFloat(String key, float value) {
        throw new UnsupportedOperationException("Unable to modify an UnmodifiablePropertiesFile");
    }

    /**
     * Unsupported Operation with UnmodifiablePropertiesFiles
     *
     * @throws UnsupportedOperationException
     *         Not supported with Unmodifiable Properties Files
     */
    @Override
    protected final void setFloat(String key, float value, String... comment) {
        throw new UnsupportedOperationException("Unable to modify an UnmodifiablePropertiesFile");
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
        return def;
    }

    /**
     * Unsupported Operation with UnmodifiablePropertiesFiles
     *
     * @throws UnsupportedOperationException
     *         Not supported with Unmodifiable Properties Files
     */
    @Override
    protected final void setFloatArray(String key, float[] value) {
        throw new UnsupportedOperationException("Unable to modify an UnmodifiablePropertiesFile");
    }

    /**
     * Unsupported Operation with UnmodifiablePropertiesFiles
     *
     * @throws UnsupportedOperationException
     *         Not supported with Unmodifiable Properties Files
     */
    @Override
    protected final void setFloatArray(String key, float[] value, String... comment) {
        throw new UnsupportedOperationException("Unable to modify an UnmodifiablePropertiesFile");
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
        return def;
    }

    /**
     * Unsupported Operation with UnmodifiablePropertiesFiles
     *
     * @throws UnsupportedOperationException
     *         Not supported with Unmodifiable Properties Files
     */
    @Override
    protected final void setFloatArray(String key, String delimiter, float[] value) {
        throw new UnsupportedOperationException("Unable to modify an UnmodifiablePropertiesFile");
    }

    /**
     * Unsupported Operation with UnmodifiablePropertiesFiles
     *
     * @throws UnsupportedOperationException
     *         Not supported with Unmodifiable Properties Files
     */
    @Override
    protected final void setFloatArray(String key, String delimiter, float[] value, String... comment) {
        throw new UnsupportedOperationException("Unable to modify an UnmodifiablePropertiesFile");
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
        return def;
    }

    /**
     * Unsupported Operation with UnmodifiablePropertiesFiles
     *
     * @throws UnsupportedOperationException
     *         Not supported with Unmodifiable Properties Files
     */
    @Override
    protected final void setDouble(String key, double value) {
        throw new UnsupportedOperationException("Unable to modify an UnmodifiablePropertiesFile");
    }

    /**
     * Unsupported Operation with UnmodifiablePropertiesFiles
     *
     * @throws UnsupportedOperationException
     *         Not supported with Unmodifiable Properties Files
     */
    @Override
    protected final void setDouble(String key, double value, String... comment) {
        throw new UnsupportedOperationException("Unable to modify an UnmodifiablePropertiesFile");
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
        return def;
    }

    /**
     * Unsupported Operation with UnmodifiablePropertiesFiles
     *
     * @throws UnsupportedOperationException
     *         Not supported with Unmodifiable Properties Files
     */
    @Override
    protected final void setDoubleArray(String key, double[] value) {
        throw new UnsupportedOperationException("Unable to modify an UnmodifiablePropertiesFile");
    }

    /**
     * Unsupported Operation with UnmodifiablePropertiesFiles
     *
     * @throws UnsupportedOperationException
     *         Not supported with Unmodifiable Properties Files
     */
    @Override
    protected final void setDoubleArray(String key, double[] value, String... comment) {
        throw new UnsupportedOperationException("Unable to modify an UnmodifiablePropertiesFile");
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
        return def;
    }

    /**
     * Unsupported Operation with UnmodifiablePropertiesFiles
     *
     * @throws UnsupportedOperationException
     *         Not supported with Unmodifiable Properties Files
     */
    @Override
    protected final void setDoubleArray(String key, String delimiter, double[] value) {
        throw new UnsupportedOperationException("Unable to modify an UnmodifiablePropertiesFile");
    }

    /**
     * Unsupported Operation with UnmodifiablePropertiesFiles
     *
     * @throws UnsupportedOperationException
     *         Not supported with Unmodifiable Properties Files
     */
    @Override
    protected final void setDoubleArray(String key, String delimiter, double[] value, String... comment) {
        throw new UnsupportedOperationException("Unable to modify an UnmodifiablePropertiesFile");
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
        return def;
    }

    /**
     * Unsupported Operation with UnmodifiablePropertiesFiles
     *
     * @throws UnsupportedOperationException
     *         Not supported with Unmodifiable Properties Files
     */
    @Override
    protected final void setBoolean(String key, boolean value) {
        throw new UnsupportedOperationException("Unable to modify an UnmodifiablePropertiesFile");
    }

    /**
     * Unsupported Operation with UnmodifiablePropertiesFiles
     *
     * @throws UnsupportedOperationException
     *         Not supported with Unmodifiable Properties Files
     */
    @Override
    protected final void setBoolean(String key, boolean value, String... comment) {
        throw new UnsupportedOperationException("Unable to modify an UnmodifiablePropertiesFile");
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
        return def;
    }

    /**
     * Unsupported Operation with UnmodifiablePropertiesFiles
     *
     * @throws UnsupportedOperationException
     *         Not supported with Unmodifiable Properties Files
     */
    @Override
    protected final void setBooleanArray(String key, boolean[] value) {
        throw new UnsupportedOperationException("Unable to modify an UnmodifiablePropertiesFile");
    }

    /**
     * Unsupported Operation with UnmodifiablePropertiesFiles
     *
     * @throws UnsupportedOperationException
     *         Not supported with Unmodifiable Properties Files
     */
    @Override
    protected final void setBooleanArray(String key, boolean[] value, String... comment) {
        throw new UnsupportedOperationException("Unable to modify an UnmodifiablePropertiesFile");
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
        return def;
    }

    /**
     * Unsupported Operation with UnmodifiablePropertiesFiles
     *
     * @throws UnsupportedOperationException
     *         Not supported with Unmodifiable Properties Files
     */
    @Override
    protected final void setBooleanArray(String key, String delimiter, boolean[] value) {
        throw new UnsupportedOperationException("Unable to modify an UnmodifiablePropertiesFile");
    }

    /**
     * Unsupported Operation with UnmodifiablePropertiesFiles
     *
     * @throws UnsupportedOperationException
     *         Not supported with Unmodifiable Properties Files
     */
    @Override
    protected final void setBooleanArray(String key, String delimiter, boolean[] value, String... comment) {
        throw new UnsupportedOperationException("Unable to modify an UnmodifiablePropertiesFile");
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
        return def;
    }

    /**
     * Unsupported Operation with UnmodifiablePropertiesFiles
     *
     * @throws UnsupportedOperationException
     *         Not supported with Unmodifiable Properties Files
     */
    @Override
    protected final void setCharacter(String key, char ch) {
        throw new UnsupportedOperationException("Unable to modify an UnmodifiablePropertiesFile");
    }

    /**
     * Unsupported Operation with UnmodifiablePropertiesFiles
     *
     * @throws UnsupportedOperationException
     *         Not supported with Unmodifiable Properties Files
     */
    @Override
    protected final void setCharacter(String key, char ch, String... comment) {
        throw new UnsupportedOperationException("Unable to modify an UnmodifiablePropertiesFile");
    }

    /**
     * Gets an unmodifiableMap of all keys and properties as Strings
     *
     * @return unmodifiable properties map
     */
    public final Map<String, String> getPropertiesMap() {
        return Collections.unmodifiableMap(props);
    }

    /**
     * Unsupported Operation with UnmodifiablePropertiesFiles
     *
     * @throws UnsupportedOperationException
     *         Not supported with Unmodifiable Properties Files
     */
    @Override
    protected final void addComment(String key, String... comment) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Unable to modify an UnmodifiablePropertiesFile");
    }

    /**
     * Unsupported Operation with UnmodifiablePropertiesFiles
     *
     * @throws UnsupportedOperationException
     *         Not supported with Unmodifiable Properties Files
     */
    @Override
    protected final void setComments(String key, String... comment) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Unable to modify an UnmodifiablePropertiesFile");
    }

    /**
     * Gets all the comments attached to the property key
     *
     * @param key
     *         the property key
     *
     * @return comments if found, {@code null} if no comments found
     */
    public final String[] getComments(String key) {
        if (comments.containsKey(key)) {
            return comments.get(key).toArray(new String[comments.get(key).size()]);
        }
        return null;
    }

    /**
     * Gets all the comments attached to the property key
     *
     * @param key
     *         the property key
     *
     * @return comments if found; {@code null} if no comments found
     */
    public final List<String> getCommentsAsList(String key) {
        if (comments.containsKey(key)) {
            return comments.get(key);
        }
        return null;
    }

    /**
     * Unsupported Operation with UnmodifiablePropertiesFiles
     *
     * @throws UnsupportedOperationException
     *         Not supported with Unmodifiable Properties Files
     */
    @Override
    protected final void removeComment(String key, String comment) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Unable to modify an UnmodifiablePropertiesFile");
    }

    /**
     * Unsupported Operation with UnmodifiablePropertiesFiles
     *
     * @throws UnsupportedOperationException
     *         Not supported with Unmodifiable Properties Files
     */
    @Override
    protected final void removeAllCommentsFromKey(String key) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Unable to modify an UnmodifiablePropertiesFile");
    }

    /**
     * Unsupported Operation with UnmodifiablePropertiesFiles
     *
     * @throws UnsupportedOperationException
     *         Not supported with Unmodifiable Properties Files
     */
    @Override
    protected final void removeAllCommentsFromFile() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Unable to modify an UnmodifiablePropertiesFile");
    }

    /**
     * Unsupported Operation with UnmodifiablePropertiesFiles
     *
     * @throws UnsupportedOperationException
     *         Not supported with Unmodifiable Properties Files
     */
    @Override
    protected final void addHeaderLines(String... lines) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Unable to modify an UnmodifiablePropertiesFile");
    }

    /**
     * Gets the lines of the Header
     *
     * @return the header lines
     */
    public final LinkedList<String> getHeaderLines() {
        LinkedList<String> toRet = new LinkedList<String>(header);
        Collections.copy(header, toRet);
        return toRet;
    }

    /**
     * Unsupported Operation with UnmodifiablePropertiesFiles
     *
     * @throws UnsupportedOperationException
     *         Not supported with Unmodifiable Properties Files
     */
    @Override
    protected final void clearHeader() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Unable to modify an UnmodifiablePropertiesFile");
    }

    /**
     * Unsupported Operation with UnmodifiablePropertiesFiles
     *
     * @throws UnsupportedOperationException
     *         Not supported with Unmodifiable Properties Files
     */
    @Override
    protected final void addFooterLines(String... lines) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Unable to modify an UnmodifiablePropertiesFile");
    }

    /**
     * Gets the lines of the Header
     *
     * @return the header lines
     */
    public final LinkedList<String> getFooterLines() {
        LinkedList<String> toRet = new LinkedList<String>(footer);
        Collections.copy(footer, toRet);
        return toRet;
    }

    /**
     * Unsupported Operation with UnmodifiablePropertiesFiles
     *
     * @throws UnsupportedOperationException
     *         Not supported with Unmodifiable Properties Files
     */
    @Override
    protected final void clearFooter() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Unable to modify an UnmodifiablePropertiesFile");
    }

    /**
     * Gets the InLine comment for a property
     *
     * @param key
     *         the property key to get inline comment for
     *
     * @return the inline comment or {@code null} if no comment
     */
    public final String getInlineComment(String key) {
        return inlineCom.get(key);
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
