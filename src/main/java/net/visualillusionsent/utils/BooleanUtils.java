package net.visualillusionsent.utils;

import java.util.concurrent.ConcurrentHashMap;

public final class BooleanUtils{

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
     * @return {@code boolean value} associated with the property
     * @throws UtilityException
     *             if value does not parse as boolean
     */
    public final static boolean parseBoolean(String property) throws UtilityException{
        if(boolMatch.containsKey(property)){
            return boolMatch.get(property).booleanValue();
        }
        return false;
    }
}
