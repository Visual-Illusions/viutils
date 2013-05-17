package net.visualillusionsent.utils;

import java.util.concurrent.ConcurrentHashMap;

/**
 * BooleanUtils
 * <p>
 * Provides methods to help with parsing Boolean values<br>
 * 
 * @since 1.4
 * @version 1.0
 * @author Jason (darkdiplomat)
 */
public final class BooleanUtils{

    private static final float classVersion = 1.1F;
    private static final ConcurrentHashMap<String, Boolean> boolMatch = new ConcurrentHashMap<String, Boolean>();
    static{
        boolMatch.put("yes", true);
        boolMatch.put("true", true);
        boolMatch.put("on", true);
        boolMatch.put("allow", true);
        boolMatch.put("grant", true);
        boolMatch.put("1", true);
        boolMatch.put("false", false);
        boolMatch.put("no", false);
        boolMatch.put("off", false);
        boolMatch.put("deny", false);
        boolMatch.put("0", false);
    }

    /**
     * Register String to boolean associations
     * 
     * @param key
     *            the String key to assign a boolean value for
     * @param value
     *            the boolean value to be assigned
     * @return {@code null} if added, {@link Boolean} value if the key already had something associated.
     */
    public final static Boolean registerBoolean(final String key, final boolean value){
        return boolMatch.putIfAbsent(key, value);
    }

    /**
     * Boolean parsing handler
     * 
     * @param property
     *            the property to parse
     * @return {@code boolean value} associated with the property, or {@code false} if a value isn't associated.
     */
    public final static boolean parseBoolean(String property){
        if(boolMatch.containsKey(property)){
            return boolMatch.get(property).booleanValue();
        }
        return false;
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
