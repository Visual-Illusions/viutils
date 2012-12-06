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
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Properties File helper
 * <p>
 * Provides methods to help with creating and accessing a Properties File
 * <p>
 * This File is part of the VIUtils<br>
 * &copy; 2012 Visual Illusions Entertainment <a href="http://visualillusionsent.net">http://visualillusionsent.net</a>
 * 
 * @since VIUtils 1.0
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
     *             if there was an error with either reading or writing the properties file
     */
    public PropertiesFile(String filepath) throws UtilityException {
        if (filepath == null) {
            throw new UtilityException("File path cannot be null");
        }
        else if (filepath.trim().isEmpty()) {
            throw new UtilityException("File path cannot be empty");
        }
        this.filepath = filepath;
        propsFile = new File(filepath);
        if (propsFile.exists()) {
            load();
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
     *             if jarpath is null or empty<br>
     *             or if entry is null or empty<br>
     *             or if the Jar file is not found or unable to be read from<br>
     *             or if the Jar file does not contain the entry
     */
    public PropertiesFile(String jarpath, String entry) throws UtilityException {
        if (jarpath == null) {
            throw new UtilityException("JarFile cannot be null");
        }
        else if (jarpath.trim().isEmpty()) {
            throw new UtilityException("JarFile cannot be empty");
        }
        else if (entry == null) {
            throw new UtilityException("Entry cannot be null");
        }
        else if (entry.trim().isEmpty()) {
            throw new UtilityException("Entry cannot be empty");
        }

        try {
            jar = new JarFile(jarpath);
        }
        catch (IOException ioe) {
            throw new UtilityException("Unable to get JarFile");
        }

        JarEntry ent = jar.getJarEntry(entry);
        if (ent == null) {
            throw new UtilityException("JarFile does not contain Entry: ".concat(entry));
        }
        filepath = entry;
        load();
    }

    /**
     * Loads the Properties File
     * 
     * @throws UtilityException
     *             if there was an error with reading the properties file
     */
    public void load() throws UtilityException {
        if (jar != null) {
            loadFromJar();
            return;
        }
        BufferedReader in = null;
        try {
            in = new BufferedReader(new FileReader(propsFile));
            String inLine;
            ArrayList<String> inComments = new ArrayList<String>();
            while ((inLine = in.readLine()) != null) {
                if (inLine.startsWith(";") || inLine.startsWith("#")) {
                    inComments.add(inLine);
                }
                else {
                    try {
                        String[] propsLine = inLine.split("=");
                        props.put(propsLine[0].trim(), propsLine[1].trim());
                        if (!inComments.isEmpty()) {
                            String[] commented = new String[inComments.size()];
                            for (int index = 0; index < inComments.size(); index++) {
                                commented[index] = inComments.get(index);
                            }
                            comments.put(propsLine[0], commented);
                            inComments.clear();
                        }
                    }
                    catch (ArrayIndexOutOfBoundsException aioobe) {
                        //Incomplete Property, drop reference to it
                        inComments.clear();
                        continue;
                    }
                }
            }
        }
        catch (IOException ioe) {
            UtilsLogger.severe(String.format("A IOException occurred in File: '%s'", filepath), ioe);
            throw new UtilityException(String.format("A IOException occurred in File: '%s'", filepath));
        }
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            }
            catch (IOException e) {
                //do nothing
            }
        }
    }

    private void loadFromJar() throws UtilityException {
        JarEntry entry = jar.getJarEntry(filepath);
        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(jar.getInputStream(entry)));
            String inLine;
            ArrayList<String> inComments = new ArrayList<String>();
            while ((inLine = in.readLine()) != null) {
                if (inLine.startsWith(";") || inLine.startsWith("#")) {
                    inComments.add(inLine);
                }
                else {
                    try {
                        String[] propsLine = inLine.split("=");
                        props.put(propsLine[0].trim(), propsLine[1].trim());
                        if (!inComments.isEmpty()) {
                            String[] commented = new String[inComments.size()];
                            for (int index = 0; index < inComments.size(); index++) {
                                commented[index] = inComments.get(index);
                            }
                            comments.put(propsLine[0], commented);
                            inComments.clear();
                        }
                    }
                    catch (ArrayIndexOutOfBoundsException aioobe) {
                        //Incomplete Property, drop reference to it
                        inComments.clear();
                        continue;
                    }
                }
            }
        }
        catch (IOException ioe) {
            UtilsLogger.severe(String.format("A IOException occurred in File: '%s'", filepath), ioe);
            throw new UtilityException(String.format("A IOException occurred in File: '%s'", filepath));
        }
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            }
            catch (IOException e) {
                //do nothing
            }
        }
    }

    /**
     * Saves the Properties File<br>
     * <b>NOTE:</b> Saving is not supported for PropertieFiles inside of Jar Files
     * 
     * @throws UtilityException
     *             if there was an error with writing the properties file<br>
     *             or if save is called for a PropertiesFile inside of a Jar
     */
    public void save() throws UtilityException {
        if (jar != null) {
            throw new UtilityException("Saving is not supported with PropertiesFiles inside of Jar files");
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
            UtilsLogger.severe(String.format("A IOException occurred in File: '%s'", filepath), ioe);
            throw new UtilityException(String.format("A IOException occurred in File: '%s'", filepath));
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
     *             if specified key is null or empty
     */
    public boolean containsKey(String key) throws UtilityException {
        if (key == null) {
            throw new UtilityException("Key cannot be null");
        }
        else if (key.trim().isEmpty()) {
            throw new UtilityException("Key cannot be empty");
        }
        return props.containsKey(key);
    }

    /**
     * Removes a key and it's associated property and comments from the PropertiesFile
     * 
     * @param key
     *            the key to be removed
     * @throws UtilityException
     *             if specified key is null or empty
     */
    public void removeKey(String key) throws UtilityException {
        if (key == null) {
            throw new UtilityException("Key cannot be null");
        }
        else if (key.trim().isEmpty()) {
            throw new UtilityException("Key cannot be empty");
        }
        if (props.containsKey(key)) {
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
     *             if specified key is null or empty <br>
     *             or if property was not found
     */
    public String getString(String key) throws UtilityException {
        if (key == null) {
            throw new UtilityException("Key cannot be null");
        }
        else if (key.isEmpty()) {
            throw new UtilityException("Key cannot be empty");
        }
        if (containsKey(key)) {
            return props.get(key);
        }
        throw new UtilityException("Property for KEY: ".concat(key).concat(" was not found."));
    }

    /**
     * Sets a property to be saved to the PropertiesFile
     * 
     * @param key
     *            the key for the property
     * @param value
     *            the property to be stored
     * @throws UtilityException
     *             if specified key is null or empty<br>
     *             or if value is null
     */
    public void setString(String key, String value) throws UtilityException {
        if (key == null) {
            throw new UtilityException("Key cannot be null");
        }
        else if (key.trim().isEmpty()) {
            throw new UtilityException("Key cannot be empty");
        }
        else if (value == null) {
            throw new UtilityException("Value cannot be null");
        }
        props.put(key, value);
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
     *             if specified key is null or empty<br>
     *             or if value is null
     */
    public void setString(String key, String value, String... comment) throws UtilityException {
        if (key == null) {
            throw new UtilityException("Key cannot be null");
        }
        else if (key.trim().isEmpty()) {
            throw new UtilityException("Key cannot be empty");
        }
        else if (value == null) {
            throw new UtilityException("Value cannot be null");
        }
        props.put(key, value);
        addComment(key, comment);
    }

    /**
     * Gets a byte associated with specified key
     * 
     * @param key
     *            the key to get the property for
     * @return byte associated with the property
     * @throws UtilityException
     *             if specified key is null or empty<br>
     *             or if the property is not a number<br>
     *             or if property is not found
     */
    public byte getByte(String key) throws UtilityException {
        if (key == null) {
            throw new UtilityException("Key cannot be null");
        }
        else if (key.trim().isEmpty()) {
            throw new UtilityException("Key cannot be empty");
        }
        if (containsKey(key)) {
            try {
                return Byte.parseByte(getString(key));
            }
            catch (NumberFormatException nfe) {
                throw new UtilityException("Property for KEY: ".concat(key).concat(" was not a number."));
            }
        }
        throw new UtilityException("Property for KEY: ".concat(key).concat(" was not found."));
    }

    /**
     * Sets a byte as a property to be saved to the PropertiesFile
     * 
     * @param key
     *            the key for the property
     * @param value
     *            the byte value to be stored
     * @throws UtilityException
     *             if specified key is null or empty
     */
    public void setByte(String key, byte value) throws UtilityException {
        if (key == null) {
            throw new UtilityException("Key cannot be null");
        }
        else if (key.trim().isEmpty()) {
            throw new UtilityException("Key cannot be empty");
        }
        props.put(key, String.valueOf(value));
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
     *             if specified key is null or empty
     */
    public void setByte(String key, byte value, String... comment) throws UtilityException {
        if (key == null) {
            throw new UtilityException("Key cannot be null");
        }
        else if (key.trim().isEmpty()) {
            throw new UtilityException("Key cannot be empty");
        }
        props.put(key, String.valueOf(value));
        addComment(key, comment);
    }

    /**
     * Gets a short associated with specified key
     * 
     * @param key
     *            the key to get the property for
     * @return byte associated with the property
     * @throws UtilityException
     *             if specified key is null or empty<br>
     *             or if the property is not a number<br>
     *             or if property is not found
     */
    public short getShort(String key) throws UtilityException {
        if (key == null) {
            throw new UtilityException("Key cannot be null");
        }
        else if (key.trim().isEmpty()) {
            throw new UtilityException("Key cannot be empty");
        }
        if (containsKey(key)) {
            try {
                return Short.parseShort(getString(key));
            }
            catch (NumberFormatException nfe) {
                throw new UtilityException("Property for KEY: ".concat(key).concat(" was not a number."));
            }
        }
        throw new UtilityException("Property for KEY: ".concat(key).concat(" was not found."));
    }

    /**
     * Sets a short as a property to be saved to the PropertiesFile
     * 
     * @param key
     *            the key for the property
     * @param value
     *            the short value to be stored
     * @throws UtilityException
     *             if specified key is null or empty
     */
    public void setShort(String key, short value) throws UtilityException {
        if (key == null) {
            throw new UtilityException("Key cannot be null");
        }
        else if (key.trim().isEmpty()) {
            throw new UtilityException("Key cannot be empty");
        }
        props.put(key, String.valueOf(value));
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
     *             if specified key is null or empty
     */
    public void setShort(String key, short value, String... comment) throws UtilityException {
        if (key == null) {
            throw new UtilityException("Key cannot be null");
        }
        else if (key.trim().isEmpty()) {
            throw new UtilityException("Key cannot be empty");
        }
        props.put(key, String.valueOf(value));
        addComment(key, comment);
    }

    /**
     * Gets an int associated with specified key
     * 
     * @param key
     *            the key to get the property for
     * @return int associated with the property
     * @throws UtilityException
     *             if specified key is null or empty<br>
     *             or if the property is not a number<br>
     *             or if property is not found
     */
    public int getInt(String key) throws UtilityException {
        if (key == null) {
            throw new UtilityException("Key cannot be null");
        }
        else if (key.trim().isEmpty()) {
            throw new UtilityException("Key cannot be empty");
        }
        if (containsKey(key)) {
            try {
                return Integer.parseInt(getString(key));
            }
            catch (NumberFormatException nfe) {
                throw new UtilityException("Property for KEY: ".concat(key).concat(" was not a number."));
            }
        }
        throw new UtilityException("Property for KEY: ".concat(key).concat(" was not found."));
    }

    /**
     * Sets an int as a property to be saved to the PropertiesFile
     * 
     * @param key
     *            the key for the property
     * @param value
     *            the int value to be stored
     * @throws UtilityException
     *             if specified key is null or empty
     */
    public void setInt(String key, int value) throws UtilityException {
        if (key == null) {
            throw new UtilityException("Key cannot be null");
        }
        else if (key.trim().isEmpty()) {
            throw new UtilityException("Key cannot be empty");
        }
        props.put(key, String.valueOf(value));
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
     *             if specified key is null or empty
     */
    public void setInt(String key, int value, String... comment) throws UtilityException {
        if (key == null) {
            throw new UtilityException("Key cannot be null");
        }
        else if (key.trim().isEmpty()) {
            throw new UtilityException("Key cannot be empty");
        }
        props.put(key, String.valueOf(value));
        addComment(key, comment);
    }

    /**
     * Gets a long associated with specified key
     * 
     * @param key
     *            the key to get the property for
     * @return long associated with the property
     * @throws UtilityException
     *             if specified key is null or empty<br>
     *             or if the property is not a number<br>
     *             or if property is not found
     */
    public long getLong(String key) throws UtilityException {
        if (key == null) {
            throw new UtilityException("Key cannot be null");
        }
        else if (key.trim().isEmpty()) {
            throw new UtilityException("Key cannot be empty");
        }
        if (containsKey(key)) {
            try {
                return Long.parseLong(getString(key));
            }
            catch (NumberFormatException nfe) {
                throw new UtilityException("Property for KEY: ".concat(key).concat(" was not a number."));
            }
        }
        throw new UtilityException("Property for KEY: ".concat(key).concat(" was not found."));
    }

    /**
     * Sets a long as a property to be saved to the PropertiesFile
     * 
     * @param key
     *            the key for the property
     * @param value
     *            the long value to be stored
     * @throws UtilityException
     *             if specified key is null or empty
     */
    public void setLong(String key, long value) throws UtilityException {
        if (key == null) {
            throw new UtilityException("Key cannot be null");
        }
        else if (key.trim().isEmpty()) {
            throw new UtilityException("Key cannot be empty");
        }
        props.put(key, String.valueOf(value));
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
     *             if specified key is null or empty
     */
    public void setLong(String key, long value, String... comment) throws UtilityException {
        if (key == null) {
            throw new UtilityException("Key cannot be null");
        }
        else if (key.trim().isEmpty()) {
            throw new UtilityException("Key cannot be empty");
        }
        props.put(key, String.valueOf(value));
        addComment(key, comment);
    }

    /**
     * Gets a float associated with specified key
     * 
     * @param key
     *            the key to get the property for
     * @return float associated with the property
     * @throws UtilityException
     *             if specified key is null or empty<br>
     *             or if the property is not a number<br>
     *             or if property is not found
     */
    public float getFloat(String key) throws UtilityException {
        if (key == null) {
            throw new UtilityException("Key cannot be null");
        }
        else if (key.trim().isEmpty()) {
            throw new UtilityException("Key cannot be empty");
        }
        if (containsKey(key)) {
            try {
                return Float.parseFloat(getString(key));
            }
            catch (NumberFormatException nfe) {
                throw new UtilityException("Property for KEY: ".concat(key).concat(" was not a number."));
            }
        }
        throw new UtilityException("Property for KEY: ".concat(key).concat(" was not found."));
    }

    /**
     * Sets a float as a property to be saved to the PropertiesFile
     * 
     * @param key
     *            the key for the property
     * @param value
     *            the float value to be stored
     * @throws UtilityException
     *             if specified key is null or empty
     */
    public void setFloat(String key, float value) throws UtilityException {
        if (key == null) {
            throw new UtilityException("Key cannot be null");
        }
        else if (key.trim().isEmpty()) {
            throw new UtilityException("Key cannot be empty");
        }
        props.put(key, String.valueOf(value));
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
     *             if specified key is null or empty
     */
    public void setFloat(String key, float value, String... comment) throws UtilityException {
        if (key == null) {
            throw new UtilityException("Key cannot be null");
        }
        else if (key.trim().isEmpty()) {
            throw new UtilityException("Key cannot be empty");
        }
        props.put(key, String.valueOf(value));
        addComment(key, comment);
    }

    /**
     * Gets a double associated with specified key
     * 
     * @param key
     *            the key to get the property for
     * @return double associated with the property
     * @throws UtilityException
     *             if specified key is null or empty<br>
     *             or if the property is not a number<br>
     *             or if property is not found
     */
    public double getDouble(String key) throws UtilityException {
        if (key == null) {
            throw new UtilityException("Key cannot be null");
        }
        else if (key.trim().isEmpty()) {
            throw new UtilityException("Key cannot be empty");
        }
        if (containsKey(key)) {
            try {
                return Double.parseDouble(getString(key));
            }
            catch (NumberFormatException nfe) {
                throw new UtilityException("Property for KEY: ".concat(key).concat(" was not a number."));
            }
        }
        throw new UtilityException("Property for KEY: ".concat(key).concat(" was not found."));
    }

    /**
     * Sets a double as a property to be saved to the PropertiesFile
     * 
     * @param key
     *            the key for the property
     * @param value
     *            the double value to be stored
     * @throws UtilityException
     *             if specified key is null or empty
     */
    public void setDouble(String key, double value) throws UtilityException {
        if (key == null) {
            throw new UtilityException("Key cannot be null");
        }
        else if (key.trim().isEmpty()) {
            throw new UtilityException("Key cannot be empty");
        }
        props.put(key, String.valueOf(value));
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
     *             if specified key is null or empty
     */
    public void setDouble(String key, double value, String... comment) throws UtilityException {
        if (key == null) {
            throw new UtilityException("Key cannot be null");
        }
        else if (key.trim().isEmpty()) {
            throw new UtilityException("Key cannot be empty");
        }
        props.put(key, String.valueOf(value));
        addComment(key, comment);
    }

    /**
     * Gets a boolean associated with specified key
     * 
     * @param key
     *            the key to get the property for
     * @return boolean associated with the property
     * @throws UtilityException
     *             if specified key is null or empty<br>
     *             or if property is not found
     * @see #parseBoolean(String)
     */
    public boolean getBoolean(String key) throws UtilityException {
        if (key == null) {
            throw new UtilityException("Key cannot be null");
        }
        else if (key.trim().isEmpty()) {
            throw new UtilityException("Key cannot be empty");
        }
        if (containsKey(key)) {
            return parseBoolean(getString(key));
        }
        throw new UtilityException("Property for KEY: ".concat(key).concat(" was not found."));
    }

    /**
     * Sets a boolean as a property to be saved to the PropertiesFile
     * 
     * @param key
     *            the key for the property
     * @param value
     *            the boolean value to be stored
     * @throws UtilityException
     *             if specified key is null or empty
     */
    public void setBoolean(String key, boolean value) throws UtilityException {
        if (key == null) {
            throw new UtilityException("Key cannot be null");
        }
        else if (key.trim().isEmpty()) {
            throw new UtilityException("Key cannot be empty");
        }
        props.put(key, String.valueOf(value));
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
     *             if specified key is null or empty
     */
    public void setBoolean(String key, boolean value, String... comment) throws UtilityException {
        if (key == null) {
            throw new UtilityException("Key cannot be null");
        }
        else if (key.trim().isEmpty()) {
            throw new UtilityException("Key cannot be empty");
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
     *             if specified key is null or empty<br>
     *             or if property is not found
     */
    public char getCharacter(String key) throws UtilityException {
        if (key == null) {
            throw new UtilityException("Key cannot be null");
        }
        else if (key.trim().isEmpty()) {
            throw new UtilityException("Key cannot be empty");
        }
        return getString(key).trim().charAt(0);
    }

    /**
     * Sets a character as a property to be saved to the PropertiesFile
     * 
     * @param key
     *            the key for the property
     * @param value
     *            the boolean value to be stored
     * @throws UtilityException
     *             if specified key is null or empty
     */
    public void setCharacter(String key, char ch) throws UtilityException {
        if (key == null) {
            throw new UtilityException("Key cannot be null");
        }
        else if (key.trim().isEmpty()) {
            throw new UtilityException("Key cannot be empty");
        }
        props.put(key, String.valueOf(ch));
    }

    /**
     * Sets a character as a property to be saved to the PropertiesFile
     * 
     * @param key
     *            the key for the property
     * @param value
     *            the double value to be stored
     * @param comment
     *            the comments to add
     * @throws UtilityException
     *             if specified key is null or empty
     */
    public void setCharacter(String key, char ch, String... comment) throws UtilityException {
        if (key == null) {
            throw new UtilityException("Key cannot be null");
        }
        else if (key.trim().isEmpty()) {
            throw new UtilityException("Key cannot be empty");
        }
        props.put(key, String.valueOf(ch));
        addComment(key, comment);
    }

    public Map<String, String> getPropertiesMap() {
        return Collections.unmodifiableMap(props);
    }

    /**
     * Internal Method for adding comments to keys
     * 
     * @param key
     *            the key to add comments for
     * @param comment
     *            the comment(s) to be added
     */
    private void addComment(String key, String... comment) {
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
            throw new UtilityException("Property not a boolean");
        }
    }
}
