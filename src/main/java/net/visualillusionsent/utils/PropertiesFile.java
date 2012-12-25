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
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Properties File helper
 * <p>
 * Provides methods to help with creating and accessing a Properties File
 * 
 * @since 1.0
 * @version 1.0
 * @author Jason (darkdiplomat)
 */
public final class PropertiesFile {
    private File propsFile;
    private String filepath;
    private JarFile jar;
    private HashMap<String, String> props = new HashMap<String, String>();
    private HashMap<String, String[]> comments = new HashMap<String, String[]>();

    /**
     * Creates or loads a PropertiesFile
     * 
     * @param filepath
     *            the path to the properties file
     * @throws UtilityException
     * <br>
     *             if there was an error with either reading or writing the properties file
     */
    public PropertiesFile(String filepath) throws UtilityException {
        if (filepath == null) {
            throw new UtilityException("arg.null", "String filepath");
        }
        else if (filepath.trim().isEmpty()) {
            throw new UtilityException("arg.empty", "String filepath");
        }
        this.filepath = filepath;
        propsFile = new File(filepath);
        if (propsFile.exists()) {
            try {
                load(new FileInputStream(propsFile));
            }
            catch (FileNotFoundException e) {
                new UtilityException("file.err.ioe", filepath);
            }
        }
        else {
            propsFile.mkdirs();
            save();
        }
    }

    /**
     * Loads a PropertiesFile stored inside a Jar file
     * 
     * @param jarpath
     *            the path to the Jar file
     * @param entry
     *            the name of the file inside of the jar
     * @throws UtilityException
     * <br>
     *             if jarpath is null or empty<br>
     *             or if entry is null or empty<br>
     *             or if the Jar file is not found or unable to be read from<br>
     *             or if the Jar file does not contain the entry
     */
    public PropertiesFile(String jarpath, String entry) throws UtilityException {
        if (jarpath == null) {
            throw new UtilityException("arg.null", "String jarpath");
        }
        else if (jarpath.trim().isEmpty()) {
            throw new UtilityException("arg.empty", "String jarpath");
        }
        else if (entry == null) {
            throw new UtilityException("arg.null", "String entry");
        }
        else if (entry.trim().isEmpty()) {
            throw new UtilityException("arg.empty", "String entry");
        }

        try {
            jar = new JarFile(jarpath);
        }
        catch (IOException ioe) {
            throw new UtilityException("jarfile.missing");
        }

        JarEntry ent = jar.getJarEntry(entry);
        if (ent == null) {
            throw new UtilityException("entry.missing", entry);
        }
        filepath = entry;
        try {
            load(jar.getInputStream(ent));
        }
        catch (IOException e) {
            new UtilityException("file.err.ioe", filepath);
        }
    }

    /**
     * Loads the Properties File
     * 
     * @throws UtilityException
     * <br>
     *             if there was an error with reading the properties file
     */
    private final void load(InputStream instream) throws UtilityException {
        UtilityException uex = null;
        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(instream, "UTF-8"));
            String inLine;
            ArrayList<String> inComments = new ArrayList<String>();
            while ((inLine = in.readLine()) != null) {
                if (inLine.startsWith(";") || inLine.startsWith("#")) {
                    inComments.add(inLine);
                }
                else {
                    String[] propsLine = null;
                    try {
                        propsLine = inLine.split("=");
                        props.put(propsLine[0].trim(), propsLine[1].trim());
                    }
                    catch (ArrayIndexOutOfBoundsException aioobe) {
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
                        String[] commented = new String[inComments.size()];
                        for (int index = 0; index < inComments.size(); index++) {
                            commented[index] = inComments.get(index);
                        }
                        comments.put(propsLine[0], commented);
                        inComments.clear();
                    }
                }
            }
        }
        catch (IOException ioe) {
            UtilsLogger.severe(String.format("An IOException occurred in File: '%s'", filepath), ioe);
            uex = new UtilityException("file.err.ioe", filepath);
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

    /**
     * Reloads the PropertiesFile from its source
     * 
     * @throws UtilityException
     * <br>
     *             if there was an error with reading the properties file<br>
     *             or if the Jar file is not found or unable to be read from<br>
     *             or if the Jar file does not contain the entry
     */
    public final void reload() throws UtilityException {
        if (jar != null) {
            JarEntry ent = jar.getJarEntry(filepath);
            if (ent == null) {
                throw new UtilityException("entry.missing", filepath);
            }
            try {
                load(jar.getInputStream(ent));
            }
            catch (IOException e) {
                new UtilityException("file.err.ioe", filepath);
            }
        }
        else {
            try {
                load(new FileInputStream(propsFile));
            }
            catch (FileNotFoundException e) {
                new UtilityException("file.err.ioe", filepath);
            }
        }
    }

    /**
     * Saves the Properties File<br>
     * <b>NOTE:</b> Saving is not supported for PropertiesFiles inside of Jar Files
     * 
     * @throws UtilityException
     * <br>
     *             if there was an error with writing the properties file<br>
     *             or if save is called for a PropertiesFile inside of a Jar
     */
    public final void save() throws UtilityException {
        if (jar != null) {
            throw new UtilityException("jar.save");
        }

        BufferedWriter out = null;
        try {
            propsFile.delete();
            propsFile = new File(filepath);
            out = new BufferedWriter(new FileWriter(propsFile, true));
            for (String prop : props.keySet()) {
                if (comments.containsKey(prop)) {
                    for (String comment : comments.get(prop)) {
                        out.write(comment);
                        out.newLine();
                    }
                }
                out.write(prop.concat("=").concat(props.get(prop)));
                out.newLine();
            }
        }
        catch (IOException ioe) {
            UtilsLogger.severe(String.format("An IOException occurred in File: '%s'", filepath), ioe);
            throw new UtilityException("file.err.ioe", filepath);
        }
        finally {
            if (out != null) {
                try {
                    out.close();
                }
                catch (IOException e) {
                    //do nothing
                }
            }
        }
    }

    /**
     * Checks if the PropertiesFile contains a key
     * 
     * @param key
     *            the key to check
     * @return {@code true} if the PropertiesFile contains the key, {@code false} otherwise
     * @throws UtilityException
     * <br>
     *             if specified key is null or empty
     */
    public boolean containsKey(String key) throws UtilityException {
        if (key == null) {
            throw new UtilityException("arg.null", "String key");
        }
        else if (key.trim().isEmpty()) {
            throw new UtilityException("arg.empty", "String key");
        }
        return props.containsKey(key);
    }

    /**
     * Removes a key and it's associated property and comments from the PropertiesFile
     * 
     * @param key
     *            the key to be removed
     * @throws UtilityException
     * <br>
     *             if specified key is null or empty
     */
    public void removeKey(String key) throws UtilityException {
        if (key == null) {
            throw new UtilityException("arg.null", "String key");
        }
        else if (key.trim().isEmpty()) {
            throw new UtilityException("arg.empty", "String key");
        }
        else if (props.containsKey(key)) {
            props.remove(key);
            if (comments.containsKey(key)) {
                comments.remove(key);
            }
        }
    }

    /**
     * Gets the property associated to the key as a {@link String}
     * 
     * @param key
     *            the key to get the property for
     * @return the property associated with the key if found
     * @throws UtilityException
     * <br>
     *             if specified key is null or empty <br>
     *             or if property was not found
     */
    public String getString(String key) throws UtilityException {
        if (key == null) {
            throw new UtilityException("arg.null", "String key");
        }
        else if (key.isEmpty()) {
            throw new UtilityException("arg.empty", "String key");
        }
        else if (containsKey(key)) {
            return props.get(key);
        }
        throw new UtilityException("key.missing", key);
    }

    /**
     * Sets a property to be saved to the PropertiesFile
     * 
     * @param key
     *            the key for the property
     * @param value
     *            the property to be stored
     * @throws UtilityException
     * <br>
     *             if specified key is null or empty<br>
     *             or if value is null
     */
    public void setString(String key, String value) throws UtilityException {
        setString(key, value, (String[]) null);
    }

    /**
     * Sets a property to be saved to the PropertiesFile with comments added
     * 
     * @param key
     *            the key for the property
     * @param value
     *            the property to be stored
     * @param comment
     *            the comments to add
     * @throws UtilityException
     * <br>
     *             if specified key is null or empty<br>
     *             or if value is null
     */
    public void setString(String key, String value, String... comment) throws UtilityException {
        if (key == null) {
            throw new UtilityException("arg.null", "String key");
        }
        else if (key.trim().isEmpty()) {
            throw new UtilityException("arg.empty", "String key");
        }
        else if (value == null) {
            throw new UtilityException("arg.null", "String value");
        }
        props.put(key, value);
        addComment(key, comment);
    }

    /**
     * Gets the property associated to the key as a {@link String} Array<br>
     * Seperates at commas ',' and trims extra whitespaces from the new elements
     * 
     * @param key
     *            the key to get the property for
     * @return the property associated with the key if found
     * @throws UtilityException
     * <br>
     *             if specified key is null or empty <br>
     *             or if property was not found
     */
    public String[] getStringArray(String key) throws UtilityException {
        return getStringArray(key, ",");
    }

    /**
     * Sets a property to be saved to the PropertiesFile<br>
     * Seperates elements with a comma ','
     * 
     * @param key
     *            the key for the property
     * @param value
     *            the property to be stored (elements combined using a comma as a spacer)
     * @throws UtilityException
     * <br>
     *             if specified key is null or empty<br>
     *             or if value is null or empty
     */
    public void setStringArray(String key, String[] value) throws UtilityException {
        setStringArray(key, ",", value, (String[]) null);
    }

    /**
     * Sets a property to be saved to the PropertiesFile with comments added<br>
     * Seperates elements with a comma ','
     * 
     * @param key
     *            the key for the property
     * @param value
     *            the property to be stored
     * @param comment
     *            the comments to add
     * @throws UtilityException
     * <br>
     *             if specified key is null or empty<br>
     *             or if value is null or empty
     */
    public void setStringArray(String key, String[] value, String... comment) throws UtilityException {
        setStringArray(key, ",", value, comment);
    }

    /**
     * Gets the property associated to the key as a {@link String} Array<br>
     * Seperates at specified character(s) and trims extra whitespaces from the new elements
     * 
     * @param key
     *            the key to get the property for
     * @param splitBy
     *            the character(s) to split the property value with
     * @return the property associated with the key if found
     * @throws UtilityException
     * <br>
     *             if specified key is null or empty <br>
     *             or if specified splitter is null or empty<br>
     *             or if property was not found
     */
    public String[] getStringArray(String key, String splitBy) throws UtilityException {
        if (splitBy == null) {
            throw new UtilityException("arg.null", "String splitBy");
        }
        else if (splitBy.isEmpty()) {
            throw new UtilityException("arg.empty", "String splitBy");
        }
        return StringUtils.trimElements(getString(key).split(splitBy));
    }

    /**
     * Sets a property to be saved to the PropertiesFile<br>
     * Seperates elements with specified spacer
     * 
     * @param key
     *            the key for the property
     * @param spacer
     *            the character(s) to space the elements with
     * @param value
     *            the property to be stored (elements combined using a comma as a spacer)
     * @throws UtilityException
     * <br>
     *             if specified key is null or empty<br>
     *             or if specified spacer is null or empty<br>
     *             or if value is null or empty
     */
    public void setStringArray(String key, String spacer, String[] value) throws UtilityException {
        setStringArray(key, spacer, value, (String[]) null);
    }

    /**
     * Sets a property to be saved to the PropertiesFile with comments added<br>
     * Seperates elements with specified spacer
     * 
     * @param key
     *            the key for the property
     * @param spacer
     *            the character(s) to space the elements with
     * @param value
     *            the property to be stored
     * @param comment
     *            the comments to add
     * @throws UtilityException
     * <br>
     *             if specified key is null or empty<br>
     *             or if specified spacer is null or empty<br>
     *             or if value is null or empty
     */
    public void setStringArray(String key, String spacer, String[] value, String... comment) throws UtilityException {
        if (key == null) {
            throw new UtilityException("arg.null", "String key");
        }
        else if (key.trim().isEmpty()) {
            throw new UtilityException("arg.empty", "String key");
        }
        else if (spacer == null) {
            throw new UtilityException("arg.null", "String spacer");
        }
        else if (spacer.isEmpty()) {
            throw new UtilityException("arg.empty", "String spacer");
        }
        else if (value == null) {
            throw new UtilityException("arg.null", "String[] value");
        }
        else if (value.length == 0) {
            throw new UtilityException("arg.empty", "String[] value");
        }
        props.put(key, StringUtils.joinString(value, spacer, 0));
        addComment(key, comment);
    }

    /**
     * Gets a byte associated with specified key
     * 
     * @param key
     *            the key to get the property for
     * @return byte associated with the property
     * @throws UtilityException
     * <br>
     *             if specified key is null or empty<br>
     *             or if the property is not a number<br>
     *             or if property is not found
     */
    public byte getByte(String key) throws UtilityException {
        try {
            return Byte.parseByte(getString(key));
        }
        catch (NumberFormatException nfe) {
            throw new UtilityException("prop.nan", key);
        }
    }

    /**
     * Sets a byte as a property to be saved to the PropertiesFile
     * 
     * @param key
     *            the key for the property
     * @param value
     *            the byte value to be stored
     * @throws UtilityException
     * <br>
     *             if specified key is null or empty
     */
    public void setByte(String key, byte value) throws UtilityException {
        setByte(key, value, (String[]) null);
    }

    /**
     * Sets a byte as a property to be saved to the PropertiesFile
     * 
     * @param key
     *            the key for the property
     * @param value
     *            the byte value to be stored
     * @param comment
     *            the comments to add
     * @throws UtilityException
     * <br>
     *             if specified key is null or empty
     */
    public void setByte(String key, byte value, String... comment) throws UtilityException {
        if (key == null) {
            throw new UtilityException("arg.null", "String key");
        }
        else if (key.trim().isEmpty()) {
            throw new UtilityException("arg.empty", "String key");
        }
        props.put(key, String.valueOf(value));
        addComment(key, comment);
    }

    /**
     * Gets the property associated to the key as a byte Array<br>
     * Seperates at commas ',' and trims extra whitespaces from the new elements
     * 
     * @param key
     *            the key to get the property for
     * @return the property associated with the key if found
     * @throws UtilityException
     * <br>
     *             if specified key is null or empty <br>
     *             or if property was not found
     */
    public byte[] getByteArray(String key) throws UtilityException {
        return getByteArray(key, ",");
    }

    /**
     * Sets a property to be saved to the PropertiesFile<br>
     * Seperates elements with a comma ','
     * 
     * @param key
     *            the key for the property
     * @param value
     *            the property to be stored (elements combined using a comma as a spacer)
     * @throws UtilityException
     * <br>
     *             if specified key is null or empty<br>
     *             or if value is null or empty
     */
    public void setByteArray(String key, byte[] value) throws UtilityException {
        setByteArray(key, ",", value, (String[]) null);
    }

    /**
     * Sets a property to be saved to the PropertiesFile with comments added<br>
     * Seperates elements with a comma ','
     * 
     * @param key
     *            the key for the property
     * @param value
     *            the property to be stored
     * @param comment
     *            the comments to add
     * @throws UtilityException
     * <br>
     *             if specified key is null or empty<br>
     *             or if value is null or empty
     */
    public void setByteArray(String key, byte[] value, String... comment) throws UtilityException {
        setByteArray(key, ",", value, comment);
    }

    /**
     * Gets the property associated to the key as a byte array<br>
     * Seperates at specified character(s) and trims extra whitespaces from the new elements
     * 
     * @param key
     *            the key to get the property for
     * @param splitBy
     *            the character(s) to split the property value with
     * @return the property associated with the key if found
     * @throws UtilityException
     * <br>
     *             if specified key is null or empty <br>
     *             or if specified splitter is null or empty<br>
     *             or if property was not found
     */
    public byte[] getByteArray(String key, String splitBy) throws UtilityException {
        if (splitBy == null) {
            throw new UtilityException("arg.null", "String splitBy");
        }
        else if (splitBy.isEmpty()) {
            throw new UtilityException("arg.empty", "String splitBy");
        }
        return StringUtils.stringToByteArray(getString(key), splitBy);
    }

    /**
     * Sets a property to be saved to the PropertiesFile<br>
     * Seperates elements with specified spacer
     * 
     * @param key
     *            the key for the property
     * @param spacer
     *            the character(s) to space the elements with
     * @param value
     *            the property to be stored (elements combined using a comma as a spacer)
     * @throws UtilityException
     * <br>
     *             if specified key is null or empty<br>
     *             or if specified spacer is null or empty<br>
     *             or if value is null or empty
     */
    public void setByteArray(String key, String spacer, byte[] value) throws UtilityException {
        setByteArray(key, spacer, value, (String[]) null);
    }

    /**
     * Sets a property to be saved to the PropertiesFile with comments added<br>
     * Seperates elements with specified spacer
     * 
     * @param key
     *            the key for the property
     * @param spacer
     *            the character(s) to space the elements with
     * @param value
     *            the property to be stored
     * @param comment
     *            the comments to add
     * @throws UtilityException
     * <br>
     *             if specified key is null or empty<br>
     *             or if specified spacer is null or empty<br>
     *             or if value is null or empty
     */
    public void setByteArray(String key, String spacer, byte[] value, String... comment) throws UtilityException {
        if (key == null) {
            throw new UtilityException("arg.null", "String key");
        }
        else if (key.trim().isEmpty()) {
            throw new UtilityException("arg.empty", "String key");
        }
        else if (spacer == null) {
            throw new UtilityException("arg.null", "String spacer");
        }
        else if (spacer.isEmpty()) {
            throw new UtilityException("arg.empty", "String spacer");
        }
        else if (value == null) {
            throw new UtilityException("arg.null", "byte[] value");
        }
        else if (value.length < 1) {
            throw new UtilityException("arg.empty", "byte[] value");
        }
        props.put(key, StringUtils.byteArrayToString(value, spacer));
        addComment(key, comment);
    }

    /**
     * Gets a short associated with specified key
     * 
     * @param key
     *            the key to get the property for
     * @return byte associated with the property
     * @throws UtilityException
     * <br>
     *             if specified key is null or empty<br>
     *             or if the property is not a number<br>
     *             or if property is not found
     */
    public short getShort(String key) throws UtilityException {
        try {
            return Short.parseShort(getString(key));
        }
        catch (NumberFormatException nfe) {
            throw new UtilityException("prop.nan", key);
        }
    }

    /**
     * Sets a short as a property to be saved to the PropertiesFile
     * 
     * @param key
     *            the key for the property
     * @param value
     *            the short value to be stored
     * @throws UtilityException
     * <br>
     *             if specified key is null or empty
     */
    public void setShort(String key, short value) throws UtilityException {
        setShort(key, value, (String[]) null);
    }

    /**
     * Sets a short as a property to be saved to the PropertiesFile
     * 
     * @param key
     *            the key for the property
     * @param value
     *            the short value to be stored
     * @param comment
     *            the comments to add
     * @throws UtilityException
     * <br>
     *             if specified key is null or empty
     */
    public void setShort(String key, short value, String... comment) throws UtilityException {
        if (key == null) {
            throw new UtilityException("arg.null", "String key");
        }
        else if (key.trim().isEmpty()) {
            throw new UtilityException("arg.empty", "String key");
        }
        props.put(key, String.valueOf(value));
        addComment(key, comment);
    }

    /**
     * Gets the property associated to the key as a short Array<br>
     * Seperates at commas ',' and trims extra whitespaces from the new elements
     * 
     * @param key
     *            the key to get the property for
     * @return the property associated with the key if found
     * @throws UtilityException
     * <br>
     *             if specified key is null or empty <br>
     *             or if property was not found
     */
    public short[] getShortArray(String key) throws UtilityException {
        return getShortArray(key, ",");
    }

    /**
     * Sets a property to be saved to the PropertiesFile
     * 
     * @param key
     *            the key for the property
     * @param value
     *            the property to be stored (elements combined using a comma as a spacer)
     * @throws UtilityException
     * <br>
     *             if specified key is null or empty<br>
     *             or if value is null or empty
     */
    public void setShortArray(String key, short[] value) throws UtilityException {
        setShortArray(key, ",", value, (String[]) null);
    }

    /**
     * Sets a property to be saved to the PropertiesFile with comments added
     * 
     * @param key
     *            the key for the property
     * @param value
     *            the property to be stored
     * @param comment
     *            the comments to add
     * @throws UtilityException
     * <br>
     *             if specified key is null or empty<br>
     *             or if value is null or empty
     */
    public void setShortArray(String key, short[] value, String... comment) throws UtilityException {
        setShortArray(key, ",", value, comment);
    }

    /**
     * Gets the property associated to the key as a short array<br>
     * Seperates at specified character(s) and trims extra whitespaces from the new elements
     * 
     * @param key
     *            the key to get the property for
     * @param splitBy
     *            the character(s) to split the property value with
     * @return the property associated with the key if found
     * @throws UtilityException
     * <br>
     *             if specified key is null or empty <br>
     *             or if specified splitter is null or empty<br>
     *             or if property was not found
     */
    public short[] getShortArray(String key, String splitBy) throws UtilityException {
        if (splitBy == null) {
            throw new UtilityException("arg.null", "String splitBy");
        }
        else if (splitBy.isEmpty()) {
            throw new UtilityException("arg.empty", "String splitBy");
        }
        return StringUtils.stringToShortArray(getString(key), splitBy);
    }

    /**
     * Sets a property to be saved to the PropertiesFile
     * 
     * @param key
     *            the key for the property
     * @param spacer
     *            the character(s) to space the elements with
     * @param value
     *            the property to be stored (elements combined using a comma as a spacer)
     * @throws UtilityException
     * <br>
     *             if specified key is null or empty<br>
     *             or if specified spacer is null or empty<br>
     *             or if value is null or empty
     */
    public void setShortArray(String key, String spacer, short[] value) throws UtilityException {
        setShortArray(key, spacer, value, (String[]) null);
    }

    /**
     * Sets a property to be saved to the PropertiesFile with comments added
     * 
     * @param key
     *            the key for the property
     * @param spacer
     *            the character(s) to space the elements with
     * @param value
     *            the property to be stored
     * @param comment
     *            the comments to add
     * @throws UtilityException
     * <br>
     *             if specified key is null or empty<br>
     *             or if specified spacer is null or empty<br>
     *             or if value is null or empty
     */
    public void setShortArray(String key, String spacer, short[] value, String... comment) throws UtilityException {
        if (key == null) {
            throw new UtilityException("arg.null", "String key");
        }
        else if (key.trim().isEmpty()) {
            throw new UtilityException("arg.empty", "String key");
        }
        else if (spacer == null) {
            throw new UtilityException("arg.null", "String spacer");
        }
        else if (spacer.isEmpty()) {
            throw new UtilityException("arg.empty", "String spacer");
        }
        else if (value == null) {
            throw new UtilityException("arg.null", "short[] value");
        }
        else if (value.length < 1) {
            throw new UtilityException("arg.empty", "short[] value");
        }
        props.put(key, StringUtils.shortArrayToString(value, spacer));
        addComment(key, comment);
    }

    /**
     * Gets an int associated with specified key
     * 
     * @param key
     *            the key to get the property for
     * @return int associated with the property
     * @throws UtilityException
     * <br>
     *             if specified key is null or empty<br>
     *             or if the property is not a number<br>
     *             or if property is not found
     */
    public int getInt(String key) throws UtilityException {
        try {
            return Integer.parseInt(getString(key));
        }
        catch (NumberFormatException nfe) {
            throw new UtilityException("prop.nan", key);
        }
    }

    /**
     * Sets an int as a property to be saved to the PropertiesFile
     * 
     * @param key
     *            the key for the property
     * @param value
     *            the int value to be stored
     * @throws UtilityException
     * <br>
     *             if specified key is null or empty
     */
    public void setInt(String key, int value) throws UtilityException {
        setInt(key, value, (String[]) null);
    }

    /**
     * Sets an int as a property to be saved to the PropertiesFile
     * 
     * @param key
     *            the key for the property
     * @param value
     *            the int value to be stored
     * @param comment
     *            the comments to add
     * @throws UtilityException
     * <br>
     *             if specified key is null or empty
     */
    public void setInt(String key, int value, String... comment) throws UtilityException {
        if (key == null) {
            throw new UtilityException("arg.null", "String key");
        }
        else if (key.trim().isEmpty()) {
            throw new UtilityException("arg.empty", "String key");
        }
        props.put(key, String.valueOf(value));
        addComment(key, comment);
    }

    /**
     * Gets the property associated to the key as a int Array<br>
     * Seperates at commas ',' and trims extra whitespaces from the new elements
     * 
     * @param key
     *            the key to get the property for
     * @return the property associated with the key if found
     * @throws UtilityException
     * <br>
     *             if specified key is null or empty <br>
     *             or if property was not found
     */
    public int[] getIntArray(String key) throws UtilityException {
        return getIntArray(key, ",");
    }

    /**
     * Sets a property to be saved to the PropertiesFile
     * 
     * @param key
     *            the key for the property
     * @param value
     *            the property to be stored (elements combined using a comma as a spacer)
     * @throws UtilityException
     * <br>
     *             if specified key is null or empty<br>
     *             or if value is null or empty
     */
    public void setIntArray(String key, int[] value) throws UtilityException {
        setIntArray(key, ",", value, (String[]) null);
    }

    /**
     * Sets a property to be saved to the PropertiesFile with comments added
     * 
     * @param key
     *            the key for the property
     * @param value
     *            the property to be stored
     * @param comment
     *            the comments to add
     * @throws UtilityException
     * <br>
     *             if specified key is null or empty<br>
     *             or if value is null or empty
     */
    public void setIntArray(String key, int[] value, String... comment) throws UtilityException {
        setIntArray(key, ",", value, comment);
    }

    /**
     * Gets the property associated to the key as a int array<br>
     * Seperates at specified character(s) and trims extra whitespaces from the new elements
     * 
     * @param key
     *            the key to get the property for
     * @param splitBy
     *            the character(s) to split the property value with
     * @return the property associated with the key if found
     * @throws UtilityException
     * <br>
     *             if specified key is null or empty <br>
     *             or if specified splitter is null or empty<br>
     *             or if property was not found
     */
    public int[] getIntArray(String key, String splitBy) throws UtilityException {
        if (splitBy == null) {
            throw new UtilityException("arg.null", "String splitBy");
        }
        else if (splitBy.isEmpty()) {
            throw new UtilityException("arg.empty", "String splitBy");
        }
        return StringUtils.stringToIntArray(getString(key), splitBy);
    }

    /**
     * Sets a property to be saved to the PropertiesFile
     * 
     * @param key
     *            the key for the property
     * @param spacer
     *            the character(s) to space the elements with
     * @param value
     *            the property to be stored (elements combined using a comma as a spacer)
     * @throws UtilityException
     * <br>
     *             if specified key is null or empty<br>
     *             or if specified spacer is null or empty<br>
     *             or if value is null or empty
     */
    public void setIntArray(String key, String spacer, int[] value) throws UtilityException {
        setIntArray(key, spacer, value, (String[]) null);
    }

    /**
     * Sets a property to be saved to the PropertiesFile with comments added
     * 
     * @param key
     *            the key for the property
     * @param spacer
     *            the character(s) to space the elements with
     * @param value
     *            the property to be stored
     * @param comment
     *            the comments to add
     * @throws UtilityException
     * <br>
     *             if specified key is null or empty<br>
     *             or if specified spacer is null or empty<br>
     *             or if value is null or empty
     */
    public void setIntArray(String key, String spacer, int[] value, String... comment) throws UtilityException {
        if (key == null) {
            throw new UtilityException("arg.null", "String key");
        }
        else if (key.trim().isEmpty()) {
            throw new UtilityException("arg.empty", "String key");
        }
        else if (spacer == null) {
            throw new UtilityException("arg.null", "String spacer");
        }
        else if (spacer.isEmpty()) {
            throw new UtilityException("arg.empty", "String spacer");
        }
        else if (value == null) {
            throw new UtilityException("arg.null", "int[] value");
        }
        else if (value.length == 0) {
            throw new UtilityException("arg.empty", "int[] value");
        }
        props.put(key, StringUtils.intArrayToString(value, spacer));
        addComment(key, comment);
    }

    /**
     * Gets a long associated with specified key
     * 
     * @param key
     *            the key to get the property for
     * @return long associated with the property
     * @throws UtilityException
     * <br>
     *             if specified key is null or empty<br>
     *             or if the property is not a number<br>
     *             or if property is not found
     */
    public long getLong(String key) throws UtilityException {
        try {
            return Long.parseLong(getString(key));
        }
        catch (NumberFormatException nfe) {
            throw new UtilityException("prop.nan", key);
        }
    }

    /**
     * Sets a long as a property to be saved to the PropertiesFile
     * 
     * @param key
     *            the key for the property
     * @param value
     *            the long value to be stored
     * @throws UtilityException
     * <br>
     *             if specified key is null or empty
     */
    public void setLong(String key, long value) throws UtilityException {
        setLong(key, value, (String[]) null);
    }

    /**
     * Sets a long as a property to be saved to the PropertiesFile
     * 
     * @param key
     *            the key for the property
     * @param value
     *            the long value to be stored
     * @param comment
     *            the comments to add
     * @throws UtilityException
     * <br>
     *             if specified key is null or empty
     */
    public void setLong(String key, long value, String... comment) throws UtilityException {
        if (key == null) {
            throw new UtilityException("arg.null", "String key");
        }
        else if (key.trim().isEmpty()) {
            throw new UtilityException("arg.empty", "String key");
        }
        props.put(key, String.valueOf(value));
        addComment(key, comment);
    }

    /**
     * Gets the property associated to the key as a long Array<br>
     * Seperates at commas ',' and trims extra whitespaces from the new elements
     * 
     * @param key
     *            the key to get the property for
     * @return the property associated with the key if found
     * @throws UtilityException
     * <br>
     *             if specified key is null or empty <br>
     *             or if property was not found
     */
    public long[] getLongArray(String key) throws UtilityException {
        return getLongArray(key, ",");
    }

    /**
     * Sets a property to be saved to the PropertiesFile
     * 
     * @param key
     *            the key for the property
     * @param value
     *            the property to be stored (elements combined using a comma as a spacer)
     * @throws UtilityException
     * <br>
     *             if specified key is null or empty<br>
     *             or if value is null or empty
     */
    public void setLongArray(String key, long[] value) throws UtilityException {
        setLongArray(key, ",", value, (String[]) null);
    }

    /**
     * Sets a property to be saved to the PropertiesFile with comments added
     * 
     * @param key
     *            the key for the property
     * @param value
     *            the property to be stored
     * @param comment
     *            the comments to add
     * @throws UtilityException
     * <br>
     *             if specified key is null or empty<br>
     *             or if value is null or empty
     */
    public void setLongArray(String key, long[] value, String... comment) throws UtilityException {
        setLongArray(key, ",", value, comment);
    }

    /**
     * Gets the property associated to the key as a long array<br>
     * Seperates at specified character(s) and trims extra whitespaces from the new elements
     * 
     * @param key
     *            the key to get the property for
     * @param splitBy
     *            the character(s) to split the property value with
     * @return the property associated with the key if found
     * @throws UtilityException
     * <br>
     *             if specified key is null or empty <br>
     *             or if specified splitter is null or empty<br>
     *             or if property was not found
     */
    public long[] getLongArray(String key, String splitBy) throws UtilityException {
        if (splitBy == null) {
            throw new UtilityException("arg.null", "String splitBy");
        }
        else if (splitBy.isEmpty()) {
            throw new UtilityException("arg.empty", "String splitBy");
        }
        return StringUtils.stringToLongArray(getString(key), splitBy);
    }

    /**
     * Sets a property to be saved to the PropertiesFile
     * 
     * @param key
     *            the key for the property
     * @param spacer
     *            the character(s) to space the elements with
     * @param value
     *            the property to be stored (elements combined using a comma as a spacer)
     * @throws UtilityException
     * <br>
     *             if specified key is null or empty<br>
     *             or if specified spacer is null or empty<br>
     *             or if value is null or empty
     */
    public void setLongArray(String key, String spacer, long[] value) throws UtilityException {
        setLongArray(key, spacer, value, (String[]) null);
    }

    /**
     * Sets a property to be saved to the PropertiesFile with comments added
     * 
     * @param key
     *            the key for the property
     * @param spacer
     *            the character(s) to space the elements with
     * @param value
     *            the property to be stored
     * @param comment
     *            the comments to add
     * @throws UtilityException
     * <br>
     *             if specified key is null or empty<br>
     *             or if specified spacer is null or empty<br>
     *             or if value is null or empty
     */
    public void setLongArray(String key, String spacer, long[] value, String... comment) throws UtilityException {
        if (key == null) {
            throw new UtilityException("arg.null", "String key");
        }
        else if (key.trim().isEmpty()) {
            throw new UtilityException("arg.empty", "String key");
        }
        else if (spacer == null) {
            throw new UtilityException("arg.null", "String spacer");
        }
        else if (spacer.isEmpty()) {
            throw new UtilityException("arg.empty", "String spacer");
        }
        else if (value == null) {
            throw new UtilityException("arg.null", "long[] value");
        }
        else if (value.length == 0) {
            throw new UtilityException("arg.empty", "long[] value");
        }
        props.put(key, StringUtils.longArrayToString(value, spacer));
        addComment(key, comment);
    }

    /**
     * Gets a float associated with specified key
     * 
     * @param key
     *            the key to get the property for
     * @return float associated with the property
     * @throws UtilityException
     * <br>
     *             if specified key is null or empty<br>
     *             or if the property is not a number<br>
     *             or if property is not found
     */
    public float getFloat(String key) throws UtilityException {
        try {
            return Float.parseFloat(getString(key));
        }
        catch (NumberFormatException nfe) {
            throw new UtilityException("prop.nan", key);
        }
    }

    /**
     * Sets a float as a property to be saved to the PropertiesFile
     * 
     * @param key
     *            the key for the property
     * @param value
     *            the float value to be stored
     * @throws UtilityException
     * <br>
     *             if specified key is null or empty
     */
    public void setFloat(String key, float value) throws UtilityException {
        setFloat(key, value, (String[]) null);
    }

    /**
     * Sets a float as a property to be saved to the PropertiesFile
     * 
     * @param key
     *            the key for the property
     * @param value
     *            the float value to be stored
     * @param comment
     *            the comments to add
     * @throws UtilityException
     * <br>
     *             if specified key is null or empty
     */
    public void setFloat(String key, float value, String... comment) throws UtilityException {
        if (key == null) {
            throw new UtilityException("arg.null", "String key");
        }
        else if (key.trim().isEmpty()) {
            throw new UtilityException("arg.empty", "String key");
        }
        props.put(key, String.valueOf(value));
        addComment(key, comment);
    }

    /**
     * Gets the property associated to the key as a float Array<br>
     * Seperates at commas ',' and trims extra whitespaces from the new elements
     * 
     * @param key
     *            the key to get the property for
     * @return the property associated with the key if found
     * @throws UtilityException
     * <br>
     *             if specified key is null or empty <br>
     *             or if property was not found
     */
    public float[] getFloatArray(String key) throws UtilityException {
        return getFloatArray(key, ",");
    }

    /**
     * Sets a property to be saved to the PropertiesFile
     * 
     * @param key
     *            the key for the property
     * @param value
     *            the property to be stored (elements combined using a comma as a spacer)
     * @throws UtilityException
     * <br>
     *             if specified key is null or empty<br>
     *             or if value is null or empty
     */
    public void setFloatArray(String key, float[] value) throws UtilityException {
        setFloatArray(key, ",", value, (String[]) null);
    }

    /**
     * Sets a property to be saved to the PropertiesFile with comments added
     * 
     * @param key
     *            the key for the property
     * @param value
     *            the property to be stored
     * @param comment
     *            the comments to add
     * @throws UtilityException
     * <br>
     *             if specified key is null or empty<br>
     *             or if value is null or empty
     */
    public void setFloatArray(String key, float[] value, String... comment) throws UtilityException {
        setFloatArray(key, ",", value, comment);
    }

    /**
     * Gets the property associated to the key as a float array<br>
     * Seperates at specified character(s) and trims extra whitespaces from the new elements
     * 
     * @param key
     *            the key to get the property for
     * @param splitBy
     *            the character(s) to split the property value with
     * @return the property associated with the key if found
     * @throws UtilityException
     * <br>
     *             if specified key is null or empty <br>
     *             or if specified splitter is null or empty<br>
     *             or if property was not found
     */
    public float[] getFloatArray(String key, String splitBy) throws UtilityException {
        if (splitBy == null) {
            throw new UtilityException("arg.null", "String splitBy");
        }
        else if (splitBy.isEmpty()) {
            throw new UtilityException("arg.empty", "String splitBy");
        }
        return StringUtils.stringToFloatArray(getString(key), splitBy);
    }

    /**
     * Sets a property to be saved to the PropertiesFile
     * 
     * @param key
     *            the key for the property
     * @param spacer
     *            the character(s) to space the elements with
     * @param value
     *            the property to be stored (elements combined using a comma as a spacer)
     * @throws UtilityException
     * <br>
     *             if specified key is null or empty<br>
     *             or if specified spacer is null or empty<br>
     *             or if value is null or empty
     */
    public void setFloatArray(String key, String spacer, float[] value) throws UtilityException {
        setFloatArray(key, spacer, value, (String[]) null);
    }

    /**
     * Sets a property to be saved to the PropertiesFile with comments added
     * 
     * @param key
     *            the key for the property
     * @param spacer
     *            the character(s) to space the elements with
     * @param value
     *            the property to be stored
     * @param comment
     *            the comments to add
     * @throws UtilityException
     * <br>
     *             if specified key is null or empty<br>
     *             or if specified spacer is null or empty<br>
     *             or if value is null or empty
     */
    public void setFloatArray(String key, String spacer, float[] value, String... comment) throws UtilityException {
        if (key == null) {
            throw new UtilityException("arg.null", "String key");
        }
        else if (key.trim().isEmpty()) {
            throw new UtilityException("arg.empty", "String key");
        }
        else if (spacer == null) {
            throw new UtilityException("arg.null", "String spacer");
        }
        else if (spacer.isEmpty()) {
            throw new UtilityException("arg.empty", "String spacer");
        }
        else if (value == null) {
            throw new UtilityException("arg.null", "float[] value");
        }
        else if (value.length == 0) {
            throw new UtilityException("arg.empty", "float[] value");
        }
        props.put(key, StringUtils.floatArrayToString(value, spacer));
        addComment(key, comment);
    }

    /**
     * Gets a double associated with specified key
     * 
     * @param key
     *            the key to get the property for
     * @return double associated with the property
     * @throws UtilityException
     * <br>
     *             if specified key is null or empty<br>
     *             or if the property is not a number<br>
     *             or if property is not found
     */
    public double getDouble(String key) throws UtilityException {
        try {
            return Double.parseDouble(getString(key));
        }
        catch (NumberFormatException nfe) {
            throw new UtilityException("prop.nan");
        }
    }

    /**
     * Sets a double as a property to be saved to the PropertiesFile
     * 
     * @param key
     *            the key for the property
     * @param value
     *            the double value to be stored
     * @throws UtilityException
     * <br>
     *             if specified key is null or empty
     */
    public void setDouble(String key, double value) throws UtilityException {
        setDouble(key, value, (String[]) null);
    }

    /**
     * Sets a double as a property to be saved to the PropertiesFile
     * 
     * @param key
     *            the key for the property
     * @param value
     *            the double value to be stored
     * @param comment
     *            the comments to add
     * @throws UtilityException
     * <br>
     *             if specified key is null or empty
     */
    public void setDouble(String key, double value, String... comment) throws UtilityException {
        if (key == null) {
            throw new UtilityException("arg.null", "String key");
        }
        else if (key.trim().isEmpty()) {
            throw new UtilityException("arg.empty", "String key");
        }
        props.put(key, String.valueOf(value));
        addComment(key, comment);
    }

    /**
     * Gets the property associated to the key as a double Array<br>
     * Seperates at commas ',' and trims extra whitespaces from the new elements
     * 
     * @param key
     *            the key to get the property for
     * @return the property associated with the key if found
     * @throws UtilityException
     * <br>
     *             if specified key is null or empty <br>
     *             or if property was not found
     */
    public double[] getDoubleArray(String key) throws UtilityException {
        return getDoubleArray(key, ",");
    }

    /**
     * Sets a property to be saved to the PropertiesFile
     * 
     * @param key
     *            the key for the property
     * @param value
     *            the property to be stored (elements combined using a comma as a spacer)
     * @throws UtilityException
     * <br>
     *             if specified key is null or empty<br>
     *             or if value is null or empty
     */
    public void setDoubleArray(String key, double[] value) throws UtilityException {
        setDoubleArray(key, ",", value, (String[]) null);
    }

    /**
     * Sets a property to be saved to the PropertiesFile with comments added
     * 
     * @param key
     *            the key for the property
     * @param value
     *            the property to be stored
     * @param comment
     *            the comments to add
     * @throws UtilityException
     * <br>
     *             if specified key is null or empty<br>
     *             or if value is null or empty
     */
    public void setDoubleArray(String key, double[] value, String... comment) throws UtilityException {
        setDoubleArray(key, ",", value, comment);
    }

    /**
     * Gets the property associated to the key as a float array<br>
     * Seperates at specified character(s) and trims extra whitespaces from the new elements
     * 
     * @param key
     *            the key to get the property for
     * @param splitBy
     *            the character(s) to split the property value with
     * @return the property associated with the key if found
     * @throws UtilityException
     * <br>
     *             if specified key is null or empty <br>
     *             or if specified splitter is null or empty<br>
     *             or if property was not found
     */
    public double[] getDoubleArray(String key, String splitBy) throws UtilityException {
        if (splitBy == null) {
            throw new UtilityException("arg.null", "String splitBy");
        }
        else if (splitBy.isEmpty()) {
            throw new UtilityException("arg.empty", "String splitBy");
        }
        return StringUtils.stringToDoubleArray(getString(key), splitBy);
    }

    /**
     * Sets a property to be saved to the PropertiesFile
     * 
     * @param key
     *            the key for the property
     * @param spacer
     *            the character(s) to space the elements with
     * @param value
     *            the property to be stored (elements combined using a comma as a spacer)
     * @throws UtilityException
     * <br>
     *             if specified key is null or empty<br>
     *             or if specified spacer is null or empty<br>
     *             or if value is null or empty
     */
    public void setDoubleArray(String key, String spacer, double[] value) throws UtilityException {
        setDoubleArray(key, spacer, value, (String[]) null);
    }

    /**
     * Sets a property to be saved to the PropertiesFile with comments added
     * 
     * @param key
     *            the key for the property
     * @param spacer
     *            the character(s) to space the elements with
     * @param value
     *            the property to be stored
     * @param comment
     *            the comments to add
     * @throws UtilityException
     * <br>
     *             if specified key is null or empty<br>
     *             or if specified spacer is null or empty<br>
     *             or if value is null or empty
     */
    public void setDoubleArray(String key, String spacer, double[] value, String... comment) throws UtilityException {
        if (key == null) {
            throw new UtilityException("arg.null", "String key");
        }
        else if (key.trim().isEmpty()) {
            throw new UtilityException("arg.empty", "String key");
        }
        else if (spacer == null) {
            throw new UtilityException("arg.null", "String spacer");
        }
        else if (spacer.isEmpty()) {
            throw new UtilityException("arg.empty", "String spacer");
        }
        else if (value == null) {
            throw new UtilityException("arg.null", "double[] value");
        }
        else if (value.length == 0) {
            throw new UtilityException("arg.empty", "double[] value");
        }
        props.put(key, StringUtils.doubleArrayToString(value, spacer));
        addComment(key, comment);
    }

    /**
     * Gets a boolean associated with specified key
     * 
     * @param key
     *            the key to get the property for
     * @return boolean associated with the property
     * @throws UtilityException
     * <br>
     *             if specified key is null or empty<br>
     *             or if property is not found
     * @see #parseBoolean(String)
     */
    public boolean getBoolean(String key) throws UtilityException {
        return parseBoolean(getString(key));
    }

    /**
     * Sets a boolean as a property to be saved to the PropertiesFile
     * 
     * @param key
     *            the key for the property
     * @param value
     *            the boolean value to be stored
     * @throws UtilityException
     * <br>
     *             if specified key is null or empty
     */
    public void setBoolean(String key, boolean value) throws UtilityException {
        setBoolean(key, value, (String[]) null);
    }

    /**
     * Sets a boolean as a property to be saved to the PropertiesFile
     * 
     * @param key
     *            the key for the property
     * @param value
     *            the double value to be stored
     * @param comment
     *            the comments to add
     * @throws UtilityException
     * <br>
     *             if specified key is null or empty
     */
    public void setBoolean(String key, boolean value, String... comment) throws UtilityException {
        if (key == null) {
            throw new UtilityException("arg.null", "String key");
        }
        else if (key.trim().isEmpty()) {
            throw new UtilityException("arg.empty", "String key");
        }
        props.put(key, String.valueOf(value));
        addComment(key, comment);
    }

    /**
     * Gets a character associated with specified key
     * 
     * @param key
     *            the key to get the property for
     * @return character associated with the property
     * @throws UtilityException
     * <br>
     *             if specified key is null or empty<br>
     *             or if property is not found
     */
    public char getCharacter(String key) throws UtilityException {
        if (key == null) {
            throw new UtilityException("arg.null", "String key");
        }
        else if (key.trim().isEmpty()) {
            throw new UtilityException("arg.empty", "String key");
        }
        return getString(key).trim().charAt(0);
    }

    /**
     * Sets a character as a property to be saved to the PropertiesFile
     * 
     * @param key
     *            the key for the property
     * @param ch
     *            the boolean value to be stored
     * @throws UtilityException
     * <br>
     *             if specified key is null or empty
     */
    public void setCharacter(String key, char ch) throws UtilityException {
        setCharacter(key, ch, (String[]) null);
    }

    /**
     * Sets a character as a property to be saved to the PropertiesFile
     * 
     * @param key
     *            the key for the property
     * @param ch
     *            the double value to be stored
     * @param comment
     *            the comments to add
     * @throws UtilityException
     * <br>
     *             if specified key is null or empty
     */
    public void setCharacter(String key, char ch, String... comment) throws UtilityException {
        if (key == null) {
            throw new UtilityException("arg.null", "String key");
        }
        else if (key.trim().isEmpty()) {
            throw new UtilityException("arg.empty", "String key");
        }
        props.put(key, String.valueOf(ch));
        addComment(key, comment);
    }

    /**
     * Gets an unmodifiableMap of all keys and properties as Strings
     * 
     * @return unmodifiable properties map
     */
    public Map<String, String> getPropertiesMap() {
        return Collections.unmodifiableMap(props);
    }

    /**
     * Method for adding comments to keys
     * 
     * @param key
     *            the key to add comments for
     * @param comment
     *            the comment(s) to be added
     */
    public void addComment(String key, String... comment) {
        if (comment != null && comment.length > 0) {
            for (int i = 0; i < comment.length; i++) {
                if (comment[i] == null) {
                    comment[i] = "";
                }
                if (!comment[i].startsWith(";") && !comment[i].startsWith("#")) {
                    comment[i] = ";".concat(comment[i]);
                }
            }
            comments.put(key, comment);
        }
    }

    /**
     * Gets all the comments attached to the property key
     * 
     * @param key
     *            the property key
     * @return comments if found, {@code null} if no comments found
     */
    public String[] getComments(String key) {
        if (comments.containsKey(key)) {
            return comments.get(key);
        }
        return null;
    }

    /**
     * Removes a comment from a property key
     * 
     * @param key
     *            the property key
     * @param comment
     *            the comment to be removed
     */
    public void removeComment(String key, String comment) {
        if (comments.containsKey(key)) {
            List<String> comms = Arrays.asList(comments.get(key));
            comms.remove(comment);
            comments.put(key, comms.toArray(new String[] {}));
        }
    }

    /**
     * Removes all the comments from a property key
     * 
     * @param key
     *            the property key to remove comments for
     */
    public void removeAllCommentsFromKey(String key) {
        if (comments.containsKey(key)) {
            comments.remove(key);
        }
    }

    /**
     * Removes all the comments from all the properties in the file
     */
    public void removeAllCommentsFromFile() {
        comments.clear();
    }

    /**
     * Boolean parsing handler
     * 
     * @param property
     *            the property to parse
     * @return boolean value associated with the property
     * @throws UtilityException
     *             if value does not parse as boolean
     */
    private final boolean parseBoolean(String property) throws UtilityException {
        if (property.matches("(?i:on|true|yes|1|allow)")) {
            return true;
        }
        else if (property.matches("(?i:off|false|no|0|deny)")) {
            return false;
        }
        else {
            if (property.matches("(?i:".concat(UtilsLocaleHelper.localeTranslation("on")).concat("|").concat(UtilsLocaleHelper.localeTranslation("true")).concat("|").concat(UtilsLocaleHelper.localeTranslation("yes")).concat("|").concat(UtilsLocaleHelper.localeTranslation("allow")).concat(")"))) {
                return true;
            }
            else if (property.matches("(?i:".concat(UtilsLocaleHelper.localeTranslation("off")).concat("|").concat(UtilsLocaleHelper.localeTranslation("false")).concat("|").concat(UtilsLocaleHelper.localeTranslation("no")).concat("|").concat(UtilsLocaleHelper.localeTranslation("deny")).concat(")"))) {
                return false;
            }
            throw new UtilityException("Property not a boolean");
        }
    }
}
