package net.visualillusionsent.utils;

/**
 * @author Jason (darkdiplomat)
 */
public class PropertiesFileException extends RuntimeException {

    PropertiesFileException(String msg) {
        super(msg);
    }

    public PropertiesFileException(String key, String msg) {
        super(Verify.parse(key, msg));
    }
}
