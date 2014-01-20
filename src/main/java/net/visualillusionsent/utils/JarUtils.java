/*
 * This file is part of ${name}.
 *
 * Copyright © 2012-2014 Visual Illusions Entertainment
 *
 * VIUtils is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.
 * If not, see http://www.gnu.org/licenses/lgpl.html.
 */
package net.visualillusionsent.utils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

/**
 * Jar File Utilities
 *
 * @author Jason (darkdiplomat)
 * @version 1.1
 * @since 1.1.0
 */
public final class JarUtils {
    private static final float classVersion = 1.1F;

    /**
     * Returns the path to the Jar File that the given class if from
     *
     * @param clazz
     *         the Class to check Jar path of
     *
     * @return path to the jar
     */
    public static String getJarPath(Class<?> clazz) {
        CodeSource codeSource = clazz.getProtectionDomain().getCodeSource();
        try {
            return codeSource.getLocation().toURI().getPath();
        }
        catch (URISyntaxException e) {
        }
        return null;
    }

    /**
     * Gets the manifest of the Jar that contains the specified Class
     *
     * @param clazz
     *         the class to check manifest for
     *
     * @return the Manifest if found
     *
     * @throws IOException
     *         if the Manifest is missing or the JarFile is not readable
     */
    public static Manifest getManifest(Class<?> clazz) throws IOException {
        return getManifest(new JarFile(getJarPath(clazz)));
    }

    /**
     * Gets the manifest of the Jar on the specified path
     *
     * @param path
     *         the path to the Jar file
     *
     * @return the Manifest if found
     *
     * @throws IOException
     *         if the Manifest is missing or the JarFile is not readable
     */
    public static Manifest getManifest(String path) throws IOException {
        return getManifest(new JarFile(path));
    }

    /**
     * Gets the manifest of the {@link JarFile}
     *
     * @param jarfile
     *         the {@link JarFile} to get the manifest for
     *
     * @return the {@link Manifest} if found
     *
     * @throws IOException
     *         if the Manifest is missing or the JarFile is not readable
     */
    public static Manifest getManifest(JarFile jarfile) throws IOException {
        if (jarfile == null) {
            return null;
        }
        return jarfile.getManifest();
    }

    /**
     * Gets the {@link JarFile} for a given {@link Class}
     *
     * @param clazz
     *         the {@link Class} to get the {@link JarFile} for
     *
     * @return the {@link JarFile}
     *
     * @throws IOException
     *         if unable to get the JarFile
     */
    public static JarFile getJarForClass(Class<?> clazz) throws IOException {
        return new JarFile(getJarPath(clazz));
    }

    /**
     * Gets all {@link Class} files from a {@link JarFile}
     *
     * @param jarFile
     *         the {@link JarFile} to get classes from
     *
     * @return {@link Class} array
     *
     * @throws ClassNotFoundException
     *         if the jar doesn't appear on the class-path and subsequently unable to load/find classes
     */
    public static Class[] getAllClasses(JarFile jarFile) throws ClassNotFoundException {
        ArrayList<Class<?>> classes = new ArrayList<Class<?>>();
        Enumeration<JarEntry> entries = jarFile.entries();
        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            String entName = entry.getName().replace("/", ".");
            if (entName.endsWith(".class")) {
                Class<?> cls = Class.forName(entName.substring(0, entName.length() - 6));
                classes.add(cls);
            }
        }
        return classes.toArray(new Class[classes.size()]);
    }

    /**
     * Gets all {@link Class} files from a {@link JarFile} that extends a specific {@link Class}
     *
     * @param jarFile
     *         the {@link JarFile} to get classes from
     * @param sCls
     *         the super {@link Class} to check extension of
     *
     * @return {@link Class} array
     *
     * @throws ClassNotFoundException
     *         if the jar doesn't appear on the class-path and subsequently unable to load/find classes
     */
    public static Class[] getAllClassesExtending(JarFile jarFile, Class<?> sCls) throws ClassNotFoundException {
        ArrayList<Class<?>> classes = new ArrayList<Class<?>>();
        Enumeration<JarEntry> entries = jarFile.entries();
        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            String entName = entry.getName().replace("/", ".");
            if (entName.endsWith(".class")) {
                Class<?> cls = Class.forName(entName.substring(0, entName.length() - 6));
                if (sCls.isAssignableFrom(cls)) {
                    classes.add(cls);
                }
            }
        }
        return classes.toArray(new Class[classes.size()]);
    }

    /**
     * Gets all {@link Class} files from a {@link JarFile} in a specified package
     *
     * @param jarFile
     *         the {@link JarFile} to get classes from
     * @param packageName
     *         the name of the package to get classes from
     *
     * @return {@link Class} array
     *
     * @throws ClassNotFoundException
     *         if the jar doesn't appear on the class-path and subsequently unable to load/find classes
     */
    public static Class[] getClassesInPackage(JarFile jarFile, String packageName) throws ClassNotFoundException {
        ArrayList<Class<?>> classes = new ArrayList<Class<?>>();
        Enumeration<JarEntry> entries = jarFile.entries();
        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            String entName = entry.getName().replace("/", ".");
            if (entName.matches(packageName + ".(\\w+).class")) {
                Class<?> cls = Class.forName(entName.substring(0, entName.length() - 6));
                classes.add(cls);
            }
        }
        return classes.toArray(new Class[classes.size()]);
    }

    /**
     * Gets all {@link Class} files from a {@link JarFile} in a specified package and extends a specific {@link Class}
     *
     * @param jarFile
     *         the {@link JarFile} to get classes from
     * @param packageName
     *         the name of the package to get classes from
     * @param sCls
     *         the super {@link Class} to check extension of
     *
     * @return {@link Class} array
     *
     * @throws ClassNotFoundException
     *         if the jar doesn't appear on the class-path and subsequently unable to load/find classes
     */
    public static Class[] getClassesInPackageExtending(JarFile jarFile, String packageName, Class<?> sCls) throws ClassNotFoundException {
        ArrayList<Class<?>> classes = new ArrayList<Class<?>>();
        Enumeration<JarEntry> entries = jarFile.entries();
        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            String entName = entry.getName().replace("/", ".");
            if (entName.matches(packageName + ".(\\w+).class")) {
                Class<?> cls = Class.forName(entName.substring(0, entName.length() - 6));
                if (sCls.isAssignableFrom(cls)) {
                    classes.add(cls);
                }
            }
        }
        return classes.toArray(new Class[classes.size()]);
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
