/*
 * This file is part of VIUtils.
 *
 * Copyright © 2012-2013 Visual Illusions Entertainment
 *
 * VIUtils is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * VIUtils is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with VIUtils.
 * If not, see http://www.gnu.org/licenses/lgpl.html.
 */
package net.visualillusionsent.utils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.CodeSource;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

/**
 * Jar File Utilities
 *
 * @author Jason (darkdiplomat)
 * @version 1.0
 * @since 1.1.0
 */
public final class JarUtils {
    private static final float classVersion = 1.0F;

    /**
     * Returns the path to the Jar File that the given class if from
     *
     * @param clazz
     *         the Class to check Jar path of
     *
     * @return path to the jar
     */
    public static final String getJarPath(Class<?> clazz) {
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
    public static final Manifest getManifest(Class<?> clazz) throws IOException {
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
    public static final Manifest getManifest(String path) throws IOException {
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
    public static final Manifest getManifest(JarFile jarfile) throws IOException {
        if (jarfile == null) {
            return null;
        }
        return jarfile.getManifest();
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
