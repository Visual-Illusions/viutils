package net.visualillusionsent.utils;

/**
 * @author Jason (darkdiplomat)
 */
public final class InvalidPropertyException extends PropertiesFileException {

    public InvalidPropertyException(String key, String msg) {
        super(Verify.parse(key, msg));
    }

}
