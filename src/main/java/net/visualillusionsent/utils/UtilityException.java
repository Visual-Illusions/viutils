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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility Exception
 * <p>
 * Thrown when a used utility has improper arguments given to it or when something goes wrong<br>
 * use the {@link #getMessage()} or {@link #getLocalizedMessage()} method to retrieve the reason why the utility method failed
 * 
 * @since 1.0
 * @version 1.0
 * @author Jason (darkdiplomat)
 */
public final class UtilityException extends RuntimeException{

    private static final float classVersion = 1.0F;
    private static final Map<String, String> errors;
    /**
     * Serial Version
     */
    private static final long serialVersionUID = 042216122012L;
    static{
        HashMap<String, String> temp = new HashMap<String, String>();
        temp.put("arg.null", "'%s' cannot be null");
        temp.put("arg.empty", "'%s' cannot be empty");
        temp.put("file.err.ioe", "An IOException occurred in File: %s");
        temp.put("file.err.exist", "%s is not an existing file");
        temp.put("file.err.read", "Could not read File: %s");
        temp.put("file.err.write", "Could not write to File: %s");
        temp.put("file.err.path", "%s path equals %s path");
        temp.put("file.err.dir", "%s is a Directory, not a file");
        temp.put("key.missing", "Property for KEY: %s was not found.");
        temp.put("prop.nan", "Property for KEY: %s was not a number.");
        temp.put("str.nan", "String Index: %s was not a number");
        temp.put("entry.missing", "JarFile does not contain Entry: %s");
        errors = Collections.unmodifiableMap(temp);
    }

    /**
     * Class Constructor
     * <p>
     * Should not be constructed outside of VIUtils
     * 
     * @param msg
     *            the message of why the exception is being thrown
     */
    UtilityException(String msg){
        super(msg);
    }

    UtilityException(String msg, Throwable thrown){
        super(msg, thrown);
    }

    UtilityException(String error, String... form){
        super(parseError(error, form));
    }

    private final static String parseError(String error, String[] form){
        if(errors.containsKey(error)){
            return String.format(errors.get(error), (Object[])form);
        }
        return error;
    }

    /**
     * Gets this class's version number
     * 
     * @return the class version
     */
    public final static float getClassVersion(){
        return classVersion;
    }
}
