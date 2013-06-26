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
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Properties File helper
 * <p>
 * Provides methods to help with creating and accessing a Properties File<br>
 * Lines that start with {@literal ;#} are seen as header comments<br>
 * Lines that start with {@literal #;} are seen as footer comments<br>
 * Other comments can be prefixed with either # or ; and will be attached to the top of they property that follows it
 * 
 * @since 1.0
 * @version 1.2
 * @author Jason (darkdiplomat)
 */
public final class PropertiesFile{

    private static final float classVersion = 1.2F;
    private File propsFile;
    private String filepath;
    private JarFile jar;
    private LinkedHashMap<String, String> props = new LinkedHashMap<String, String>();
    private LinkedHashMap<String, LinkedList<String>> comments = new LinkedHashMap<String, LinkedList<String>>();
    private LinkedList<String> header = new LinkedList<String>();
    private LinkedList<String> footer = new LinkedList<String>();

    /**
     * Creates or loads a PropertiesFile
     * 
     * @param filepath
     *            the path to the properties file
     * @throws UtilityException
     * <br>
     *             if there was an error with either reading or writing the properties file
     */
    public PropertiesFile(String filepath) throws UtilityException{
        if(filepath == null){
            throw new UtilityException("arg.null", "String filepath");
        }
        else if(filepath.trim().isEmpty()){
            throw new UtilityException("arg.empty", "String filepath");
        }
        this.filepath = filepath;
        propsFile = new File(filepath);
        if(propsFile.exists()){
            try{
                load(new FileInputStream(propsFile));
            }
            catch(FileNotFoundException e){
                throw new UtilityException("file.err.ioe", filepath);
            }
        }
        else{
            filepath = FileUtils.normalizePath(filepath);
            if(filepath.contains(File.separator)){
                File temp = new File(filepath.substring(0, filepath.lastIndexOf(File.separator)));
                if(!temp.exists()){
                    if(!temp.mkdirs()){
                        throw new UtilityException("Failed to make directory path for FilePath: ".concat(filepath));
                    }
                    save();
                }
            }
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
    public PropertiesFile(String jarpath, String entry) throws UtilityException{
        if(jarpath == null){
            throw new UtilityException("arg.null", "String jarpath");
        }
        else if(jarpath.trim().isEmpty()){
            throw new UtilityException("arg.empty", "String jarpath");
        }
        else if(entry == null){
            throw new UtilityException("arg.null", "String entry");
        }
        else if(entry.trim().isEmpty()){
            throw new UtilityException("arg.empty", "String entry");
        }
        try{
            jar = new JarFile(jarpath);
        }
        catch(IOException ioe){
            throw new UtilityException("Unable to get JarFile");
        }
        JarEntry ent = jar.getJarEntry(entry);
        if(ent == null){
            throw new UtilityException("entry.missing", entry);
        }
        filepath = entry;
        try{
            load(jar.getInputStream(ent));
        }
        catch(IOException e){
            throw new UtilityException("file.err.ioe", filepath);
        }
    }

    /**
     * Loads the Properties File
     * 
     * @throws UtilityException
     * <br>
     *             if there was an error with reading the properties file
     */
    @SuppressWarnings("unchecked")
    private final void load(InputStream instream) throws UtilityException{
        UtilityException uex = null;
        BufferedReader in = null;
        try{
            in = new BufferedReader(new InputStreamReader(instream, "UTF-8"));
            String inLine;
            LinkedList<String> inComments = new LinkedList<String>();
            while((inLine = in.readLine()) != null){
                if(inLine.startsWith(";#")){
                    header.add(inLine);
                }
                else if(inLine.startsWith("#;")){
                    footer.add(inLine);
                }
                else if(inLine.startsWith(";") || inLine.startsWith("#")){
                    inComments.add(inLine);
                }
                else{
                    String[] propsLine = null;
                    try{
                        propsLine = inLine.split("=");
                        props.put(propsLine[0].trim(), propsLine[1].trim());
                    }
                    catch(ArrayIndexOutOfBoundsException aioobe){
                        //Empty value?
                        if(inLine.contains("=")){
                            props.put(propsLine[0], "");
                        }
                        //Incomplete property, drop reference
                        else{
                            inComments.clear();
                            continue;
                        }
                    }
                    if(!inComments.isEmpty()){
                        comments.put(propsLine[0], (LinkedList<String>)inComments.clone());
                        inComments.clear();
                    }
                }
            }
        }
        catch(IOException ioe){
            UtilsLogger.severe(String.format("An IOException occurred in File: '%s'", filepath), ioe);
            uex = new UtilityException("file.err.ioe", filepath);
        }
        finally{
            if(in != null){
                try{
                    in.close();
                }
                catch(IOException e){
                    //do nothing
                }
            }
            if(uex != null){
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
    public final void reload() throws UtilityException{
        props.clear();
        comments.clear();
        if(jar != null){
            JarEntry ent = jar.getJarEntry(filepath);
            if(ent == null){
                throw new UtilityException("entry.missing", filepath);
            }
            try{
                load(jar.getInputStream(ent));
            }
            catch(IOException e){
                throw new UtilityException("file.err.ioe", filepath);
            }
        }
        else{
            try{
                load(new FileInputStream(propsFile));
            }
            catch(FileNotFoundException e){
                throw new UtilityException("file.err.ioe", filepath);
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
    public final void save() throws UtilityException{
        if(jar != null){
            throw new UtilityException("Saving is not supported with PropertiesFiles inside of Jar files");
        }
        BufferedWriter out = null;
        try{
            if(propsFile.exists()){
                if(!propsFile.delete()){
                    throw new UtilityException("file.err.ioe", filepath);
                }
            }
            propsFile = new File(filepath);
            out = new BufferedWriter(new FileWriter(propsFile, true));
            for(String headerLn : header){
                out.write(headerLn);
                out.newLine();
            }
            for(String prop : props.keySet()){
                if(comments.containsKey(prop)){
                    for(String comment : comments.get(prop)){
                        out.write(comment);
                        out.newLine();
                    }
                }
                out.write(prop.concat("=").concat(props.get(prop)));
                out.newLine();
            }
            for(String footerLn : footer){
                out.write(footerLn);
                out.newLine();
            }
        }
        catch(IOException ioe){
            UtilsLogger.severe(String.format("An IOException occurred in File: '%s'", filepath), ioe);
            throw new UtilityException("file.err.ioe", filepath);
        }
        finally{
            if(out != null){
                try{
                    out.close();
                }
                catch(IOException e){}
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
    public final boolean containsKey(String key) throws UtilityException{
        if(key == null){
            throw new UtilityException("arg.null", "String key");
        }
        else if(key.trim().isEmpty()){
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
    public final void removeKey(String key) throws UtilityException{
        if(key == null){
            throw new UtilityException("arg.null", "String key");
        }
        else if(key.trim().isEmpty()){
            throw new UtilityException("arg.empty", "String key");
        }
        else if(props.containsKey(key)){
            props.remove(key);
            if(comments.containsKey(key)){
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
    public final String getString(String key) throws UtilityException{
        if(key == null){
            throw new UtilityException("arg.null", "String key");
        }
        else if(key.isEmpty()){
            throw new UtilityException("arg.empty", "String key");
        }
        else if(containsKey(key)){
            return props.get(key);
        }
        throw new UtilityException("key.missing", key);
    }

    /**
     * Gets the property associated to the key as a {@link String} or returns the default specified<br>
     * NOTE: This will not save the properties file, it will add the key and value to the map for later saving.
     * 
     * @param key
     *            the key to get the property for
     * @param def
     *            the default value to use if key is not found
     * @return the property associated with the key if found
     * @throws UtilityException
     * <br>
     *             if specified key is null or empty <br>
     */
    public final String getString(String key, String def) throws UtilityException{
        if(key == null){
            throw new UtilityException("arg.null", "String key");
        }
        else if(key.isEmpty()){
            throw new UtilityException("arg.empty", "String key");
        }
        else if(containsKey(key)){
            return props.get(key);
        }
        else{
            setString(key, def);
            return def;
        }
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
    public final void setString(String key, String value) throws UtilityException{
        setString(key, value, (String[])null);
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
    public final void setString(String key, String value, String... comment) throws UtilityException{
        if(key == null){
            throw new UtilityException("arg.null", "String key");
        }
        else if(key.trim().isEmpty()){
            throw new UtilityException("arg.empty", "String key");
        }
        else if(value == null){
            throw new UtilityException("arg.null", "String value");
        }
        props.put(key, value);
        addComment(key, comment);
    }

    /**
     * Gets the property associated to the key as a {@link String} Array<br>
     * Separates at commas ',' and trims extra whitespace from the new elements
     * 
     * @param key
     *            the key to get the property for
     * @return the property associated with the key if found
     * @throws UtilityException
     * <br>
     *             if specified key is null or empty <br>
     *             or if property was not found
     */
    public final String[] getStringArray(String key) throws UtilityException{
        return getStringArray(key, ",");
    }

    /**
     * Gets the property associated to the key as a {@link String} Array or returns the default specified<br>
     * Separates at commas ',' and trims extra whitespace from the new elements<br>
     * NOTE: This will not save the properties file, it will add the key and value to the map for later saving.
     * 
     * @param key
     *            the key to get the property for
     * @param def
     *            the default value to use if key is not found
     * @return the property associated with the key if found
     * @throws UtilityException
     * <br>
     *             if specified key is null or empty <br>
     */
    public final String[] getStringArray(String key, String[] def) throws UtilityException{
        if(containsKey(key)){
            return getStringArray(key, ",");
        }
        else{
            setStringArray(key, def);
            return def;
        }
    }

    /**
     * Sets a property to be saved to the PropertiesFile<br>
     * Separates elements with a comma ','
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
    public final void setStringArray(String key, String[] value) throws UtilityException{
        setStringArray(key, ",", value, (String[])null);
    }

    /**
     * Sets a property to be saved to the PropertiesFile with comments added<br>
     * Separates elements with a comma ','
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
    public final void setStringArray(String key, String[] value, String... comment) throws UtilityException{
        setStringArray(key, ",", value, comment);
    }

    /**
     * Gets the property associated to the key as a {@link String} Array<br>
     * Separates at specified character(s) and trims extra whitespace from the new elements
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
    public final String[] getStringArray(String key, String splitBy) throws UtilityException{
        if(splitBy == null){
            throw new UtilityException("arg.null", "String splitBy");
        }
        else if(splitBy.isEmpty()){
            throw new UtilityException("arg.empty", "String splitBy");
        }
        return StringUtils.trimElements(getString(key).split(splitBy));
    }

    /**
     * Gets the property associated to the key as a {@link String} Array or returns the default specified<br>
     * Separates at specified character(s) and trims extra whitespace from the new elements<br>
     * NOTE: This will not save the properties file, it will add the key and value to the map for later saving.
     * 
     * @param key
     *            the key to get the property for
     * @param splitBy
     *            the character(s) to split the property value with
     * @param def
     *            the default value to use if key is not found
     * @return the property associated with the key if found
     * @throws UtilityException
     * <br>
     *             if specified key is null or empty <br>
     *             or if specified splitter is null or empty<br>
     */
    public final String[] getStringArray(String key, String splitBy, String[] def) throws UtilityException{
        if(splitBy == null){
            throw new UtilityException("arg.null", "String splitBy");
        }
        else if(splitBy.isEmpty()){
            throw new UtilityException("arg.empty", "String splitBy");
        }
        if(containsKey(key)){
            return StringUtils.trimElements(getString(key).split(splitBy));
        }
        else{
            setStringArray(key, splitBy, def);
            return def;
        }
    }

    /**
     * Sets a property to be saved to the PropertiesFile<br>
     * Separates elements with specified spacer
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
    public final void setStringArray(String key, String spacer, String[] value) throws UtilityException{
        setStringArray(key, spacer, value, (String[])null);
    }

    /**
     * Sets a property to be saved to the PropertiesFile with comments added<br>
     * Separates elements with specified spacer
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
    public final void setStringArray(String key, String spacer, String[] value, String... comment) throws UtilityException{
        if(key == null){
            throw new UtilityException("arg.null", "String key");
        }
        else if(key.trim().isEmpty()){
            throw new UtilityException("arg.empty", "String key");
        }
        else if(spacer == null){
            throw new UtilityException("arg.null", "String spacer");
        }
        else if(spacer.isEmpty()){
            throw new UtilityException("arg.empty", "String spacer");
        }
        else if(value == null){
            throw new UtilityException("arg.null", "String[] value");
        }
        else if(value.length == 0){
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
    public final byte getByte(String key) throws UtilityException{
        try{
            return Byte.parseByte(getString(key));
        }
        catch(NumberFormatException nfe){
            throw new UtilityException("prop.nan", key);
        }
    }

    /**
     * Gets a byte associated with specified key or returns the default specified<br>
     * NOTE: This will not save the properties file, it will add the key and value to the map for later saving.
     * 
     * @param key
     *            the key to get the property for
     * @param def
     *            the default value to use if key is not found
     * @return byte associated with the property
     * @throws UtilityException
     * <br>
     *             if specified key is null or empty<br>
     *             or if the property is not a number<br>
     */
    public final byte getByte(String key, byte def) throws UtilityException{
        if(containsKey(key)){
            try{
                return Byte.parseByte(getString(key));
            }
            catch(NumberFormatException nfe){
                throw new UtilityException("prop.nan", key);
            }
        }
        else{
            setByte(key, def);
            return def;
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
    public final void setByte(String key, byte value) throws UtilityException{
        setByte(key, value, (String[])null);
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
    public final void setByte(String key, byte value, String... comment) throws UtilityException{
        if(key == null){
            throw new UtilityException("arg.null", "String key");
        }
        else if(key.trim().isEmpty()){
            throw new UtilityException("arg.empty", "String key");
        }
        props.put(key, String.valueOf(value));
        addComment(key, comment);
    }

    /**
     * Gets the property associated to the key as a byte Array<br>
     * Separates at commas ',' and trims extra whitespace from the new elements
     * 
     * @param key
     *            the key to get the property for
     * @return the property associated with the key if found
     * @throws UtilityException
     * <br>
     *             if specified key is null or empty <br>
     *             or if property was not found
     */
    public final byte[] getByteArray(String key) throws UtilityException{
        return getByteArray(key, ",");
    }

    /**
     * Gets the property associated to the key as a byte Array or returns the default specified<br>
     * Separates at commas ',' and trims extra whitespace from the new elements<br>
     * NOTE: This will not save the properties file, it will add the key and value to the map for later saving.
     * 
     * @param key
     *            the key to get the property for
     * @param def
     *            the default value to use if key is not found
     * @return the property associated with the key if found
     * @throws UtilityException
     * <br>
     *             if specified key is null or empty
     */
    public final byte[] getByteArray(String key, byte[] def) throws UtilityException{
        if(containsKey(key)){
            return getByteArray(key, ",");
        }
        else{
            setByteArray(key, def);
            return def;
        }
    }

    /**
     * Sets a property to be saved to the PropertiesFile<br>
     * Separates elements with a comma ','
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
    public final void setByteArray(String key, byte[] value) throws UtilityException{
        setByteArray(key, ",", value, (String[])null);
    }

    /**
     * Sets a property to be saved to the PropertiesFile with comments added<br>
     * Separates elements with a comma ','
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
    public final void setByteArray(String key, byte[] value, String... comment) throws UtilityException{
        setByteArray(key, ",", value, comment);
    }

    /**
     * Gets the property associated to the key as a byte array<br>
     * Separates at specified character(s) and trims extra whitespace from the new elements
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
    public final byte[] getByteArray(String key, String splitBy) throws UtilityException{
        if(splitBy == null){
            throw new UtilityException("arg.null", "String splitBy");
        }
        else if(splitBy.isEmpty()){
            throw new UtilityException("arg.empty", "String splitBy");
        }
        return StringUtils.stringToByteArray(getString(key), splitBy);
    }

    /**
     * Gets the property associated to the key as a byte array or returns the default specified<br>
     * Separates at specified character(s) and trims extra whitespace from the new elements<br>
     * NOTE: This will not save the properties file, it will add the key and value to the map for later saving.
     * 
     * @param key
     *            the key to get the property for
     * @param splitBy
     *            the character(s) to split the property value with
     * @param def
     *            the default value to use if key is not found
     * @return the property associated with the key if found
     * @throws UtilityException
     * <br>
     *             if specified key is null or empty <br>
     *             or if specified splitter is null or empty
     */
    public final byte[] getByteArray(String key, String splitBy, byte[] def) throws UtilityException{
        if(splitBy == null){
            throw new UtilityException("arg.null", "String splitBy");
        }
        else if(splitBy.isEmpty()){
            throw new UtilityException("arg.empty", "String splitBy");
        }
        if(containsKey(key)){
            return StringUtils.stringToByteArray(getString(key), splitBy);
        }
        else{
            setByteArray(key, splitBy, def);
            return def;
        }
    }

    /**
     * Sets a property to be saved to the PropertiesFile<br>
     * Separates elements with specified spacer
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
    public final void setByteArray(String key, String spacer, byte[] value) throws UtilityException{
        setByteArray(key, spacer, value, (String[])null);
    }

    /**
     * Sets a property to be saved to the PropertiesFile with comments added<br>
     * Separates elements with specified spacer
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
    public final void setByteArray(String key, String spacer, byte[] value, String... comment) throws UtilityException{
        if(key == null){
            throw new UtilityException("arg.null", "String key");
        }
        else if(key.trim().isEmpty()){
            throw new UtilityException("arg.empty", "String key");
        }
        else if(spacer == null){
            throw new UtilityException("arg.null", "String spacer");
        }
        else if(spacer.isEmpty()){
            throw new UtilityException("arg.empty", "String spacer");
        }
        else if(value == null){
            throw new UtilityException("arg.null", "byte[] value");
        }
        else if(value.length < 1){
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
     * @return short associated with the property
     * @throws UtilityException
     * <br>
     *             if specified key is null or empty<br>
     *             or if the property is not a number<br>
     *             or if property is not found
     */
    public final short getShort(String key) throws UtilityException{
        try{
            return Short.parseShort(getString(key));
        }
        catch(NumberFormatException nfe){
            throw new UtilityException("prop.nan", key);
        }
    }

    /**
     * Gets a short associated with specified key or returns the default specified<br>
     * NOTE: This will not save the properties file, it will add the key and value to the map for later saving.
     * 
     * @param key
     *            the key to get the property for
     * @param def
     *            the default value to use if key is not found
     * @return short associated with the property
     * @throws UtilityException
     * <br>
     *             if specified key is null or empty<br>
     *             or if the property is not a number<br>
     *             or if property is not found
     */
    public final short getShort(String key, short def) throws UtilityException{
        if(containsKey(key)){
            try{
                return Short.parseShort(getString(key));
            }
            catch(NumberFormatException nfe){
                throw new UtilityException("prop.nan", key);
            }
        }
        else{
            setShort(key, def);
            return def;
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
    public final void setShort(String key, short value) throws UtilityException{
        setShort(key, value, (String[])null);
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
    public final void setShort(String key, short value, String... comment) throws UtilityException{
        if(key == null){
            throw new UtilityException("arg.null", "String key");
        }
        else if(key.trim().isEmpty()){
            throw new UtilityException("arg.empty", "String key");
        }
        props.put(key, String.valueOf(value));
        addComment(key, comment);
    }

    /**
     * Gets the property associated to the key as a short Array<br>
     * Separates at commas ',' and trims extra whitespace from the new elements
     * 
     * @param key
     *            the key to get the property for
     * @return the property associated with the key if found
     * @throws UtilityException
     * <br>
     *             if specified key is null or empty <br>
     *             or if property was not found
     */
    public final short[] getShortArray(String key) throws UtilityException{
        return getShortArray(key, ",");
    }

    /**
     * Gets the property associated to the key as a short Array or returns the default specified<br>
     * Separates at commas ',' and trims extra whitespace from the new elements<br>
     * NOTE: This will not save the properties file, it will add the key and value to the map for later saving.
     * 
     * @param key
     *            the key to get the property for
     * @param def
     *            the default value to use if key is not found
     * @return the property associated with the key if found
     * @throws UtilityException
     * <br>
     *             if specified key is null or empty <br>
     */
    public final short[] getShortArray(String key, short[] def) throws UtilityException{
        if(containsKey(key)){
            return getShortArray(key, ",");
        }
        else{
            setShortArray(key, def);
            return def;
        }
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
    public final void setShortArray(String key, short[] value) throws UtilityException{
        setShortArray(key, ",", value, (String[])null);
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
    public final void setShortArray(String key, short[] value, String... comment) throws UtilityException{
        setShortArray(key, ",", value, comment);
    }

    /**
     * Gets the property associated to the key as a short array<br>
     * Separates at specified character(s) and trims extra whitespace from the new elements
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
    public final short[] getShortArray(String key, String splitBy) throws UtilityException{
        if(splitBy == null){
            throw new UtilityException("arg.null", "String splitBy");
        }
        else if(splitBy.isEmpty()){
            throw new UtilityException("arg.empty", "String splitBy");
        }
        return StringUtils.stringToShortArray(getString(key), splitBy);
    }

    /**
     * Gets the property associated to the key as a short array or returns the default specified<br>
     * Separates at commas ',' and trims extra whitespace from the new elements<br>
     * NOTE: This will not save the properties file, it will add the key and value to the map for later saving.
     * 
     * @param key
     *            the key to get the property for
     * @param splitBy
     *            the character(s) to split the property value with
     * @param def
     *            the default value to use if key is not found
     * @return the property associated with the key if found
     * @throws UtilityException
     * <br>
     *             if specified key is null or empty <br>
     *             or if specified splitter is null or empty
     */
    public final short[] getShortArray(String key, String splitBy, short[] def) throws UtilityException{
        if(splitBy == null){
            throw new UtilityException("arg.null", "String splitBy");
        }
        else if(splitBy.isEmpty()){
            throw new UtilityException("arg.empty", "String splitBy");
        }
        if(containsKey(key)){
            return StringUtils.stringToShortArray(getString(key), splitBy);
        }
        else{
            setShortArray(key, splitBy, def);
            return def;
        }
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
    public final void setShortArray(String key, String spacer, short[] value) throws UtilityException{
        setShortArray(key, spacer, value, (String[])null);
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
    public final void setShortArray(String key, String spacer, short[] value, String... comment) throws UtilityException{
        if(key == null){
            throw new UtilityException("arg.null", "String key");
        }
        else if(key.trim().isEmpty()){
            throw new UtilityException("arg.empty", "String key");
        }
        else if(spacer == null){
            throw new UtilityException("arg.null", "String spacer");
        }
        else if(spacer.isEmpty()){
            throw new UtilityException("arg.empty", "String spacer");
        }
        else if(value == null){
            throw new UtilityException("arg.null", "short[] value");
        }
        else if(value.length < 1){
            throw new UtilityException("arg.empty", "short[] value");
        }
        props.put(key, StringUtils.shortArrayToString(value, spacer));
        addComment(key, comment);
    }

    /**
     * Gets an integer associated with specified key
     * 
     * @param key
     *            the key to get the property for
     * @return integer associated with the property
     * @throws UtilityException
     * <br>
     *             if specified key is null or empty<br>
     *             or if the property is not a number<br>
     *             or if property is not found
     */
    public final int getInt(String key) throws UtilityException{
        try{
            return Integer.parseInt(getString(key));
        }
        catch(NumberFormatException nfe){
            throw new UtilityException("prop.nan", key);
        }
    }

    /**
     * Gets an integer associated with specified key or returns the default specified<br>
     * NOTE: This will not save the properties file, it will add the key and value to the map for later saving.
     * 
     * @param key
     *            the key to get the property for
     * @param def
     *            the default value to use if key is not found
     * @return integer associated with the property
     * @throws UtilityException
     * <br>
     *             if specified key is null or empty<br>
     *             or if the property is not a number
     */
    public final int getInt(String key, int def) throws UtilityException{
        if(containsKey(key)){
            try{
                return Integer.parseInt(getString(key));
            }
            catch(NumberFormatException nfe){
                throw new UtilityException("prop.nan", key);
            }
        }
        else{
            setInt(key, def);
            return def;
        }
    }

    /**
     * Sets an integer as a property to be saved to the PropertiesFile
     * 
     * @param key
     *            the key for the property
     * @param value
     *            the integer value to be stored
     * @throws UtilityException
     * <br>
     *             if specified key is null or empty
     */
    public final void setInt(String key, int value) throws UtilityException{
        setInt(key, value, (String[])null);
    }

    /**
     * Sets an integer as a property to be saved to the PropertiesFile
     * 
     * @param key
     *            the key for the property
     * @param value
     *            the integer value to be stored
     * @param comment
     *            the comments to add
     * @throws UtilityException
     * <br>
     *             if specified key is null or empty
     */
    public final void setInt(String key, int value, String... comment) throws UtilityException{
        if(key == null){
            throw new UtilityException("arg.null", "String key");
        }
        else if(key.trim().isEmpty()){
            throw new UtilityException("arg.empty", "String key");
        }
        props.put(key, String.valueOf(value));
        addComment(key, comment);
    }

    /**
     * Gets the property associated to the key as a integer Array<br>
     * Separates at commas ',' and trims extra whitespace from the new elements
     * 
     * @param key
     *            the key to get the property for
     * @return the property associated with the key if found
     * @throws UtilityException
     * <br>
     *             if specified key is null or empty <br>
     *             or if property was not found
     */
    public final int[] getIntArray(String key) throws UtilityException{
        return getIntArray(key, ",");
    }

    /**
     * Gets the property associated to the key as a integer Array or returns the default specified<br>
     * Separates at commas ',' and trims extra whitespace from the new elements<br>
     * NOTE: This will not save the properties file, it will add the key and value to the map for later saving.
     * 
     * @param key
     *            the key to get the property for
     * @param def
     *            the default value to use if key is not found
     * @return the property associated with the key if found
     * @throws UtilityException
     * <br>
     *             if specified key is null or empty
     */
    public final int[] getIntArray(String key, int[] def) throws UtilityException{
        if(containsKey(key)){
            return getIntArray(key, ",");
        }
        else{
            setIntArray(key, def);
            return def;
        }
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
    public final void setIntArray(String key, int[] value) throws UtilityException{
        setIntArray(key, ",", value, (String[])null);
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
    public final void setIntArray(String key, int[] value, String... comment) throws UtilityException{
        setIntArray(key, ",", value, comment);
    }

    /**
     * Gets the property associated to the key as a integer array<br>
     * Separates at specified character(s) and trims extra whitespace from the new elements
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
    public final int[] getIntArray(String key, String splitBy) throws UtilityException{
        if(splitBy == null){
            throw new UtilityException("arg.null", "String splitBy");
        }
        else if(splitBy.isEmpty()){
            throw new UtilityException("arg.empty", "String splitBy");
        }
        return StringUtils.stringToIntArray(getString(key), splitBy);
    }

    /**
     * Gets the property associated to the key as a integer array or returns the default specified<br>
     * Separates at specified character(s) and trims extra whitespace from the new elements<br>
     * NOTE: This will not save the properties file, it will add the key and value to the map for later saving.
     * 
     * @param key
     *            the key to get the property for
     * @param splitBy
     *            the character(s) to split the property value with
     * @param def
     *            the default value to use if key is not found
     * @return the property associated with the key if found
     * @throws UtilityException
     * <br>
     *             if specified key is null or empty <br>
     *             or if specified splitter is null or empty
     */
    public final int[] getIntArray(String key, String splitBy, int[] def) throws UtilityException{
        if(splitBy == null){
            throw new UtilityException("arg.null", "String splitBy");
        }
        else if(splitBy.isEmpty()){
            throw new UtilityException("arg.empty", "String splitBy");
        }
        if(containsKey(key)){
            return StringUtils.stringToIntArray(getString(key), splitBy);
        }
        else{
            setIntArray(key, splitBy, def);
            return def;
        }
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
    public final void setIntArray(String key, String spacer, int[] value) throws UtilityException{
        setIntArray(key, spacer, value, (String[])null);
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
    public final void setIntArray(String key, String spacer, int[] value, String... comment) throws UtilityException{
        if(key == null){
            throw new UtilityException("arg.null", "String key");
        }
        else if(key.trim().isEmpty()){
            throw new UtilityException("arg.empty", "String key");
        }
        else if(spacer == null){
            throw new UtilityException("arg.null", "String spacer");
        }
        else if(spacer.isEmpty()){
            throw new UtilityException("arg.empty", "String spacer");
        }
        else if(value == null){
            throw new UtilityException("arg.null", "int[] value");
        }
        else if(value.length == 0){
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
    public final long getLong(String key) throws UtilityException{
        try{
            return Long.parseLong(getString(key));
        }
        catch(NumberFormatException nfe){
            throw new UtilityException("prop.nan", key);
        }
    }

    /**
     * Gets a long associated with specified key or returns the default specified<br>
     * NOTE: This will not save the properties file, it will add the key and value to the map for later saving.
     * 
     * @param key
     *            the key to get the property for
     * @param def
     *            the default value to use if key is not found
     * @return long associated with the property
     * @throws UtilityException
     * <br>
     *             if specified key is null or empty<br>
     *             or if the property is not a number
     */
    public final long getLong(String key, long def) throws UtilityException{
        if(containsKey(key)){
            try{
                return Long.parseLong(getString(key));
            }
            catch(NumberFormatException nfe){
                throw new UtilityException("prop.nan", key);
            }
        }
        else{
            setLong(key, def);
            return def;
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
    public final void setLong(String key, long value) throws UtilityException{
        setLong(key, value, (String[])null);
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
    public final void setLong(String key, long value, String... comment) throws UtilityException{
        if(key == null){
            throw new UtilityException("arg.null", "String key");
        }
        else if(key.trim().isEmpty()){
            throw new UtilityException("arg.empty", "String key");
        }
        props.put(key, String.valueOf(value));
        addComment(key, comment);
    }

    /**
     * Gets the property associated to the key as a long Array<br>
     * Separates at commas ',' and trims extra whitespace from the new elements
     * 
     * @param key
     *            the key to get the property for
     * @return the property associated with the key if found
     * @throws UtilityException
     * <br>
     *             if specified key is null or empty <br>
     *             or if property was not found
     */
    public final long[] getLongArray(String key) throws UtilityException{
        return getLongArray(key, ",");
    }

    /**
     * Gets the property associated to the key as a long Array or returns the default specified<br>
     * Separates at commas ',' and trims extra whitespace from the new elements<br>
     * NOTE: This will not save the properties file, it will add the key and value to the map for later saving.
     * 
     * @param key
     *            the key to get the property for
     * @param def
     *            the default value to use if key is not found
     * @return the property associated with the key if found
     * @throws UtilityException
     * <br>
     *             if specified key is null or empty <br>
     *             or if property was not found
     */
    public final long[] getLongArray(String key, long[] def) throws UtilityException{
        if(containsKey(key)){
            return getLongArray(key, ",");
        }
        else{
            setLongArray(key, def);
            return def;
        }
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
    public final void setLongArray(String key, long[] value) throws UtilityException{
        setLongArray(key, ",", value, (String[])null);
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
    public final void setLongArray(String key, long[] value, String... comment) throws UtilityException{
        setLongArray(key, ",", value, comment);
    }

    /**
     * Gets the property associated to the key as a long array<br>
     * Separates at specified character(s) and trims extra whitespace from the new elements
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
    public final long[] getLongArray(String key, String splitBy) throws UtilityException{
        if(splitBy == null){
            throw new UtilityException("arg.null", "String splitBy");
        }
        else if(splitBy.isEmpty()){
            throw new UtilityException("arg.empty", "String splitBy");
        }
        return StringUtils.stringToLongArray(getString(key), splitBy);
    }

    /**
     * Gets the property associated to the key as a long array or returns the default specified<br>
     * Separates at specified character(s) and trims extra whitespace from the new elements<br>
     * NOTE: This will not save the properties file, it will add the key and value to the map for later saving.
     * 
     * @param key
     *            the key to get the property for
     * @param splitBy
     *            the character(s) to split the property value with
     * @param def
     *            the default value to use if key is not found
     * @return the property associated with the key if found
     * @throws UtilityException
     * <br>
     *             if specified key is null or empty <br>
     *             or if specified splitter is null or empty<br>
     *             or if property was not found
     */
    public final long[] getLongArray(String key, String splitBy, long[] def) throws UtilityException{
        if(splitBy == null){
            throw new UtilityException("arg.null", "String splitBy");
        }
        else if(splitBy.isEmpty()){
            throw new UtilityException("arg.empty", "String splitBy");
        }
        if(containsKey(key)){
            return StringUtils.stringToLongArray(getString(key), splitBy);
        }
        else{
            setLongArray(key, splitBy, def);
            return def;
        }
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
    public final void setLongArray(String key, String spacer, long[] value) throws UtilityException{
        setLongArray(key, spacer, value, (String[])null);
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
    public final void setLongArray(String key, String spacer, long[] value, String... comment) throws UtilityException{
        if(key == null){
            throw new UtilityException("arg.null", "String key");
        }
        else if(key.trim().isEmpty()){
            throw new UtilityException("arg.empty", "String key");
        }
        else if(spacer == null){
            throw new UtilityException("arg.null", "String spacer");
        }
        else if(spacer.isEmpty()){
            throw new UtilityException("arg.empty", "String spacer");
        }
        else if(value == null){
            throw new UtilityException("arg.null", "long[] value");
        }
        else if(value.length == 0){
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
    public final float getFloat(String key) throws UtilityException{
        try{
            return Float.parseFloat(getString(key));
        }
        catch(NumberFormatException nfe){
            throw new UtilityException("prop.nan", key);
        }
    }

    /**
     * Gets a float associated with specified key or returns the default specified<br>
     * NOTE: This will not save the properties file, it will add the key and value to the map for later saving.
     * 
     * @param key
     *            the key to get the property for
     * @param def
     *            the default value to use if key is not found
     * @return float associated with the property
     * @throws UtilityException
     * <br>
     *             if specified key is null or empty<br>
     *             or if the property is not a number
     */
    public final float getFloat(String key, float def) throws UtilityException{
        if(containsKey(key)){
            try{
                return Float.parseFloat(getString(key));
            }
            catch(NumberFormatException nfe){
                throw new UtilityException("prop.nan", key);
            }
        }
        else{
            setFloat(key, def);
            return def;
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
    public final void setFloat(String key, float value) throws UtilityException{
        setFloat(key, value, (String[])null);
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
    public final void setFloat(String key, float value, String... comment) throws UtilityException{
        if(key == null){
            throw new UtilityException("arg.null", "String key");
        }
        else if(key.trim().isEmpty()){
            throw new UtilityException("arg.empty", "String key");
        }
        props.put(key, String.valueOf(value));
        addComment(key, comment);
    }

    /**
     * Gets the property associated to the key as a float Array<br>
     * Separates at commas ',' and trims extra whitespace from the new elements
     * 
     * @param key
     *            the key to get the property for
     * @return the property associated with the key if found
     * @throws UtilityException
     * <br>
     *             if specified key is null or empty <br>
     *             or if property was not found
     */
    public final float[] getFloatArray(String key) throws UtilityException{
        return getFloatArray(key, ",");
    }

    /**
     * Gets the property associated to the key as a float Array or returns the default specified<br>
     * Separates at commas ',' and trims extra whitespace from the new elements<br>
     * NOTE: This will not save the properties file, it will add the key and value to the map for later saving.
     * 
     * @param key
     *            the key to get the property for
     * @param def
     *            the default value to use if key is not found
     * @return the property associated with the key if found
     * @throws UtilityException
     * <br>
     *             if specified key is null or empty <br>
     */
    public final float[] getFloatArray(String key, float[] def) throws UtilityException{
        if(containsKey(key)){
            return getFloatArray(key, ",");
        }
        else{
            setFloatArray(key, def);
            return def;
        }
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
    public final void setFloatArray(String key, float[] value) throws UtilityException{
        setFloatArray(key, ",", value, (String[])null);
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
    public final void setFloatArray(String key, float[] value, String... comment) throws UtilityException{
        setFloatArray(key, ",", value, comment);
    }

    /**
     * Gets the property associated to the key as a float array<br>
     * Separates at specified character(s) and trims extra whitespace from the new elements
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
    public final float[] getFloatArray(String key, String splitBy) throws UtilityException{
        if(splitBy == null){
            throw new UtilityException("arg.null", "String splitBy");
        }
        else if(splitBy.isEmpty()){
            throw new UtilityException("arg.empty", "String splitBy");
        }
        return StringUtils.stringToFloatArray(getString(key), splitBy);
    }

    /**
     * Gets the property associated to the key as a float array or returns the default specified<br>
     * Separates at specified character(s) and trims extra whitespace from the new elements<br>
     * NOTE: This will not save the properties file, it will add the key and value to the map for later saving.
     * 
     * @param key
     *            the key to get the property for
     * @param splitBy
     *            the character(s) to split the property value with
     * @param def
     *            the default value to use if key is not found
     * @return the property associated with the key if found
     * @throws UtilityException
     * <br>
     *             if specified key is null or empty <br>
     *             or if specified splitter is null or empty<br>
     *             or if property was not found
     */
    public final float[] getFloatArray(String key, String splitBy, float[] def) throws UtilityException{
        if(splitBy == null){
            throw new UtilityException("arg.null", "String splitBy");
        }
        else if(splitBy.isEmpty()){
            throw new UtilityException("arg.empty", "String splitBy");
        }
        if(containsKey(key)){
            return StringUtils.stringToFloatArray(getString(key), splitBy);
        }
        else{
            setFloatArray(key, splitBy, def);
            return def;
        }
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
    public final void setFloatArray(String key, String spacer, float[] value) throws UtilityException{
        setFloatArray(key, spacer, value, (String[])null);
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
    public final void setFloatArray(String key, String spacer, float[] value, String... comment) throws UtilityException{
        if(key == null){
            throw new UtilityException("arg.null", "String key");
        }
        else if(key.trim().isEmpty()){
            throw new UtilityException("arg.empty", "String key");
        }
        else if(spacer == null){
            throw new UtilityException("arg.null", "String spacer");
        }
        else if(spacer.isEmpty()){
            throw new UtilityException("arg.empty", "String spacer");
        }
        else if(value == null){
            throw new UtilityException("arg.null", "float[] value");
        }
        else if(value.length == 0){
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
    public final double getDouble(String key) throws UtilityException{
        try{
            return Double.parseDouble(getString(key));
        }
        catch(NumberFormatException nfe){
            throw new UtilityException("prop.nan");
        }
    }

    /**
     * Gets a double associated with specified key or returns the default specified<br>
     * NOTE: This will not save the properties file, it will add the key and value to the map for later saving.
     * 
     * @param key
     *            the key to get the property for
     * @param def
     *            the default value to use if key is not found
     * @return double associated with the property
     * @throws UtilityException
     * <br>
     *             if specified key is null or empty<br>
     *             or if the property is not a number
     */
    public final double getDouble(String key, double def) throws UtilityException{
        if(containsKey(key)){
            try{
                return Double.parseDouble(getString(key));
            }
            catch(NumberFormatException nfe){
                throw new UtilityException("prop.nan", key);
            }
        }
        else{
            setDouble(key, def);
            return def;
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
    public final void setDouble(String key, double value) throws UtilityException{
        setDouble(key, value, (String[])null);
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
    public final void setDouble(String key, double value, String... comment) throws UtilityException{
        if(key == null){
            throw new UtilityException("arg.null", "String key");
        }
        else if(key.trim().isEmpty()){
            throw new UtilityException("arg.empty", "String key");
        }
        props.put(key, String.valueOf(value));
        addComment(key, comment);
    }

    /**
     * Gets the property associated to the key as a double Array<br>
     * Separates at commas ',' and trims extra whitespace from the new elements
     * 
     * @param key
     *            the key to get the property for
     * @return the property associated with the key if found
     * @throws UtilityException
     * <br>
     *             if specified key is null or empty <br>
     *             or if property was not found
     */
    public final double[] getDoubleArray(String key) throws UtilityException{
        return getDoubleArray(key, ",");
    }

    /**
     * Gets the property associated to the key as a double Array or returns the default specified<br>
     * Separates at commas ',' and trims extra whitespace from the new elements<br>
     * NOTE: This will not save the properties file, it will add the key and value to the map for later saving.
     * 
     * @param key
     *            the key to get the property for
     * @param def
     *            the default value to use if key is not found
     * @return the property associated with the key if found
     * @throws UtilityException
     * <br>
     *             if specified key is null or empty <br>
     */
    public final double[] getDoubleArray(String key, double[] def) throws UtilityException{
        if(containsKey(key)){
            return getDoubleArray(key, ",");
        }
        else{
            setDoubleArray(key, def);
            return def;
        }
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
    public final void setDoubleArray(String key, double[] value) throws UtilityException{
        setDoubleArray(key, ",", value, (String[])null);
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
    public final void setDoubleArray(String key, double[] value, String... comment) throws UtilityException{
        setDoubleArray(key, ",", value, comment);
    }

    /**
     * Gets the property associated to the key as a float array<br>
     * Separates at specified character(s) and trims extra whitespace from the new elements
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
    public final double[] getDoubleArray(String key, String splitBy) throws UtilityException{
        if(splitBy == null){
            throw new UtilityException("arg.null", "String splitBy");
        }
        else if(splitBy.isEmpty()){
            throw new UtilityException("arg.empty", "String splitBy");
        }
        return StringUtils.stringToDoubleArray(getString(key), splitBy);
    }

    /**
     * Gets the property associated to the key as a double array or returns the default specified<br>
     * Separates at specified character(s) and trims extra whitespace from the new elements<br>
     * NOTE: This will not save the properties file, it will add the key and value to the map for later saving.
     * 
     * @param key
     *            the key to get the property for
     * @param splitBy
     *            the character(s) to split the property value with
     * @param def
     *            the default value to use if key is not found
     * @return the property associated with the key if found
     * @throws UtilityException
     * <br>
     *             if specified key is null or empty <br>
     *             or if specified splitter is null or empty
     */
    public final double[] getDoubleArray(String key, String splitBy, double[] def) throws UtilityException{
        if(splitBy == null){
            throw new UtilityException("arg.null", "String splitBy");
        }
        else if(splitBy.isEmpty()){
            throw new UtilityException("arg.empty", "String splitBy");
        }
        if(containsKey(key)){
            return StringUtils.stringToDoubleArray(getString(key), splitBy);
        }
        else{
            setDoubleArray(key, splitBy, def);
            return def;
        }
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
    public final void setDoubleArray(String key, String spacer, double[] value) throws UtilityException{
        setDoubleArray(key, spacer, value, (String[])null);
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
    public final void setDoubleArray(String key, String spacer, double[] value, String... comment) throws UtilityException{
        if(key == null){
            throw new UtilityException("arg.null", "String key");
        }
        else if(key.trim().isEmpty()){
            throw new UtilityException("arg.empty", "String key");
        }
        else if(spacer == null){
            throw new UtilityException("arg.null", "String spacer");
        }
        else if(spacer.isEmpty()){
            throw new UtilityException("arg.empty", "String spacer");
        }
        else if(value == null){
            throw new UtilityException("arg.null", "double[] value");
        }
        else if(value.length == 0){
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
     * @see BooleanUtils#parseBoolean(String)
     */
    public final boolean getBoolean(String key) throws UtilityException{
        return BooleanUtils.parseBoolean(getString(key));
    }

    /**
     * Gets a boolean associated with specified key
     * 
     * @param key
     *            the key to get the property for
     * @param def
     *            the default value to use if key is not found
     * @return boolean associated with the property
     * @throws UtilityException
     * <br>
     *             if specified key is null or empty<br>
     *             or if property is not found
     * @see BooleanUtils#parseBoolean(String)
     */
    public final boolean getBoolean(String key, boolean def) throws UtilityException{
        if(containsKey(key)){
            return BooleanUtils.parseBoolean(getString(key));
        }
        else{
            setBoolean(key, def);
            return def;
        }
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
    public final void setBoolean(String key, boolean value) throws UtilityException{
        setBoolean(key, value, (String[])null);
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
    public final void setBoolean(String key, boolean value, String... comment) throws UtilityException{
        if(key == null){
            throw new UtilityException("arg.null", "String key");
        }
        else if(key.trim().isEmpty()){
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
    public final char getCharacter(String key) throws UtilityException{
        return getString(key).trim().charAt(0);
    }

    /**
     * Gets a character associated with specified key
     * 
     * @param key
     *            the key to get the property for
     * @param def
     *            the default value to use if key is not found
     * @return character associated with the property
     * @throws UtilityException
     * <br>
     *             if specified key is null or empty<br>
     */
    public final char getCharacter(String key, char def) throws UtilityException{
        if(containsKey(key)){
            return getString(key).trim().charAt(0);
        }
        else{
            setCharacter(key, def);
            return def;
        }
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
    public final void setCharacter(String key, char ch) throws UtilityException{
        setCharacter(key, ch, (String[])null);
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
    public final void setCharacter(String key, char ch, String... comment) throws UtilityException{
        if(key == null){
            throw new UtilityException("arg.null", "String key");
        }
        else if(key.trim().isEmpty()){
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
    public final Map<String, String> getPropertiesMap(){
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
    public final void addComment(String key, String... comment){
        if(containsKey(key)){
            if(comment != null && comment.length > 0){
                LinkedList<String> the_comments = comments.containsKey(key) ? comments.get(key) : new LinkedList<String>();
                for(int i = 0; i < comment.length; i++){
                    if(comment[i] == null){
                        comment[i] = "";
                    }
                    if(!comment[i].startsWith(";") && !comment[i].startsWith("#")){
                        comment[i] = ";".concat(comment[i]);
                    }
                    the_comments.add(comment[i]);
                }
                if(!comments.containsKey(key)){ //Basicly, the list pointer should be enough to change the list in the map without re-adding
                    comments.put(key, the_comments);
                }
            }
        }
    }

    /**
     * Sets the comments for a given property.<br>
     * NOTE: If comment is null or length of 0, this will essentially remove all comments.
     * 
     * @param key
     *            the key to the property to set comments for
     * @param comment
     *            the comment(s) to set
     */
    public final void setComments(String key, String... comment){
        if(containsKey(key)){
            if(comments.containsKey(key)){
                comments.get(key).clear();
            }
            this.addComment(key, comment);
        }
    }

    /**
     * Gets all the comments attached to the property key
     * 
     * @param key
     *            the property key
     * @return comments if found, {@code null} if no comments found
     */
    public final String[] getComments(String key){
        if(comments.containsKey(key)){
            return comments.get(key).toArray(new String[0]);
        }
        return null;
    }

    /**
     * Gets all the comments attached to the property key
     * 
     * @param key
     *            the property key
     * @return comments if found; {@code null} if no comments found
     */
    public final List<String> getCommentsAsList(String key){
        if(comments.containsKey(key)){
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
    public final void removeComment(String key, String comment){
        if(comments.containsKey(key)){
            comments.get(key).remove(comment);
        }
    }

    /**
     * Removes all the comments from a property key
     * 
     * @param key
     *            the property key to remove comments for
     */
    public final void removeAllCommentsFromKey(String key){
        if(comments.containsKey(key)){
            comments.remove(key);
        }
    }

    /**
     * Removes all the comments from all the properties in the file
     */
    public final void removeAllCommentsFromFile(){
        comments.clear();
        header.clear();
        footer.clear();
    }

    /**
     * Adds lines to the Header of the PropertiesFile
     * 
     * @param lines
     *            the lines to be added
     */
    public final void addHeaderLines(String... lines){
        if(lines != null && lines.length > 0){
            for(String line : lines){
                if(line == null){
                    header.add(";# ");
                }
                else if(line.startsWith(";#")){
                    header.add(line);
                }
                else{
                    header.add(";#".concat(line));
                }
            }
        }
    }

    /**
     * Gets the lines of the Header
     * 
     * @return the header lines
     */
    @SuppressWarnings("unchecked")
    public final LinkedList<String> getHeaderLines(){
        return (LinkedList<String>)header.clone();
    }

    /**
     * Clears the header
     */
    public final void clearHeader(){
        header.clear();
    }

    /**
     * Adds lines to the Footer of the PropertiesFile
     * 
     * @param lines
     *            the lines to be added
     */
    public final void addFooterLines(String... lines){
        if(lines != null && lines.length > 0){
            for(String line : lines){
                if(line == null){
                    footer.add("#; ");
                }
                else if(line.startsWith("#;")){
                    footer.add(line);
                }
                else{
                    header.add("#;".concat(line));
                }
            }
        }
    }

    /**
     * Gets the lines of the Header
     * 
     * @return the header lines
     */
    @SuppressWarnings("unchecked")
    public final LinkedList<String> getFooterLines(){
        return (LinkedList<String>)footer.clone();
    }

    /**
     * Clears the footer
     */
    public final void clearFooter(){
        footer.clear();
    }

    /**
     * Checks is an {@link Object} is equal to the {@code PropertiesFile}
     * 
     * @return {@code true} if equal; {@code false} otherwise
     * @see Object#equals(Object)
     */
    public final boolean equals(Object obj){
        if(!(obj instanceof PropertiesFile)){
            return false;
        }
        PropertiesFile that = (PropertiesFile)obj;
        if(!this.filepath.equals(that.filepath)){
            return false;
        }
        if(this.propsFile != null && this.propsFile != that.propsFile){
            return false;
        }
        if(this.jar != null && this.jar != that.jar){
            return false;
        }
        return true;
    }

    /**
     * Returns a string representation of the {@code PropertiesFile} as {@code PropertiesFile[FilePath=%s]}
     * 
     * @return string representation of the {@code PropertiesFile}
     * @see Object#toString()
     */
    @Override
    public final String toString(){
        return String.format("PropertiesFile[FilePath=%s]", propsFile != null ? propsFile.getAbsolutePath() : jar.getName() + ":" + filepath);
    }

    /**
     * Returns a hash code value for the {@code PropertiesFile}.
     * 
     * @return hash
     * @see Object#hashCode()
     */
    @Override
    public final int hashCode(){
        int hash = 9;
        hash = 45 * hash + filepath.hashCode();
        hash = 54 * hash + (propsFile != null ? propsFile.hashCode() : 0);
        hash = 45 * hash + (jar != null ? jar.hashCode() : 0);
        return hash;
    }

    /**
     * Gets this class's version number
     * 
     * @return the class version
     */
    public static final float getClassVersion(){
        return classVersion;
    }
}
