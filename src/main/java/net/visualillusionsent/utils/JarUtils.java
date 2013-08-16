package net.visualillusionsent.utils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.CodeSource;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

public final class JarUtils{

    /**
     * Returns the path to the Jar File that the given class if from
     * 
     * @param clazz
     *            the Class to check Jar path of
     * @return path to the jar
     */
    public static final String getJarPath(Class<?> clazz){
        CodeSource codeSource = clazz.getProtectionDomain().getCodeSource();
        try{
            return codeSource.getLocation().toURI().getPath();
        }
        catch(URISyntaxException e){}
        return null;
    }

    public static final Manifest getManifest(Class<?> clazz) throws IOException{
        return getManifest(new JarFile(getJarPath(clazz)));
    }

    public static final Manifest getManifest(String path) throws IOException{
        return getManifest(new JarFile(path));
    }

    public static final Manifest getManifest(JarFile jarfile) throws IOException{
        if(jarfile == null){
            return null;
        }
        return jarfile.getManifest();
    }
}
