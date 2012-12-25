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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Plugin Jar File Updater
 * <p>
 * Used to update plugin jar files on the fly
 * <p>
 * This File is part of the VIUtils<br>
 * &copy; 2012 <a href="http://visualillusionsent.net">Visual Illusions Entertainment</a>
 * 
 * @since 1.0
 * @version 1.0
 * @author Jason (darkdiplomat)
 */
public final class Updater {
    private String downloadurl, jarloc, jarname;
    private final static int CLASS_LENGTH = ".class".length();

    public Updater(String downloadurl, String jarloc, String jarname) {
        this.downloadurl = downloadurl;
        this.jarloc = jarloc;
        this.jarname = jarname;
    }

    /**
     * Performs jar file update
     * 
     * @return true if successful
     * @throws UpdateException
     *             use the getMessage() method to retrieve the reason why
     */
    public final boolean performUpdate() throws UpdateException {
        UtilsLogger.info("Please wait, downloading latest version of ".concat(jarname).concat("..."));

        if (!jarloc.endsWith(".jar")) {
            UtilsLogger.info("The jar file location needs to end with .jar... Terminating update...");
            throw new UpdateException("update.fail", "Incorrect File Extension");
        }

        File local = new File(jarloc);
        if (!local.exists()) {
            UtilsLogger.warning("Unable to find ".concat(jarloc).concat("... Terminating update..."));
            throw new UpdateException("update.fail", "FileNotFound");
        }

        // BackUp just in case of failure
        File bak = backupjar(jarloc);

        if (bak == null) {
            throw new UpdateException("update.fail", "Backup failed");
        }

        if (loadAllClasses(jarloc)) {
            OutputStream outputStream = null;
            InputStream inputStream = null;
            UpdateException uex = null;
            try {
                outputStream = new FileOutputStream(local);
                URL url = new URI(downloadurl).toURL();
                inputStream = url.openConnection().getInputStream();

                byte[] buffer = new byte[1024];
                int read = 0;

                while ((read = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, read);
                }

                UtilsLogger.info("Successfully downloaded latest version of ".concat(jarname).concat("!"));
                bak.delete();
                return true;
            }
            catch (IOException ioe) {
                UtilsLogger.warning("Failed to download new version. Restoring old version...", ioe);

                // Restore
                if (restorejar(jarloc)) {
                    bak.delete();
                }
                uex = new UpdateException("update.fail", "Failed to download");
            }
            catch (URISyntaxException urise) {
                // Restore
                if (restorejar(jarloc)) {
                    bak.delete();
                }
                UtilsLogger.warning("There was an error with the URI syntax... Restoring old version...", urise);
                uex = new UpdateException("update.fail", "Failed to download");
            }
            finally {
                if (outputStream != null) {
                    try {
                        outputStream.close();
                    }
                    catch (IOException e) {}
                }

                if (inputStream != null) {
                    try {
                        inputStream.close();
                    }
                    catch (IOException e) {}
                }

                if (uex != null) {
                    throw uex;
                }
            }
        }
        return false;
    }

    /**
     * loads all the jar's files for updating
     * 
     * @param jarloc
     *            The location of the jar file to be updated
     * @return true if successfully loaded all classes
     */
    private final boolean loadAllClasses(String jarloc) throws UpdateException {
        try {
            // Load the jar
            JarFile jar = new JarFile(jarloc);

            // Walk through all of the entries
            Enumeration<JarEntry> enumeration = jar.entries();

            while (enumeration.hasMoreElements()) {
                JarEntry entry = enumeration.nextElement();
                String name = entry.getName();

                // is it a class file?
                if (name.endsWith(".class") && !name.contains("$")) {
                    // convert to package
                    String path = name.replaceAll("/", ".");
                    path = path.substring(0, path.length() - CLASS_LENGTH);

                    // Load it
                    Thread.currentThread().getContextClassLoader().loadClass(path);
                }
            }
            jar.close();
            return true;
        }
        catch (IOException ioe) {
            UtilsLogger.severe("An IOException has occurred! Update terminated!", ioe);
            throw new UpdateException("update.fail", "IOException during jar load");
        }
        catch (ClassNotFoundException cnfe) {
            UtilsLogger.severe("An ClassNotFoundException has occurred! Update terminated!", cnfe);
            throw new UpdateException("update.fail", "ClassNotFoundException during jar load");
        }
        catch (Exception e) {
            UtilsLogger.severe("An Unexpected Exception has occurred! Update terminated!", e);
            throw new UpdateException("update.fail", "Unexpected Exception during jar load");
        }
    }

    private final File backupjar(String jarfile) {
        File bak = null;
        OutputStream outputStream = null;
        InputStream inputStream = null;
        try {
            bak = new File(jarloc.substring(0, jarloc.lastIndexOf("/") + 1).concat(jarname).concat(".bak"));
            outputStream = new FileOutputStream(bak);
            inputStream = new FileInputStream(jarfile);

            byte[] buffer = new byte[512];
            int read = 0;

            while ((read = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, read);
            }
        }
        catch (IOException ioe) {
            bak = null;
        }
        finally {
            try {
                outputStream.close();
            }
            catch (IOException e) {}

            try {
                inputStream.close();
            }
            catch (IOException e) {}
        }
        return bak;
    }

    private final boolean restorejar(String jarfile) {
        boolean toRet = false;
        OutputStream outputStream = null;
        InputStream inputStream = null;
        try {
            File bak = new File(jarloc.substring(0, jarloc.lastIndexOf("/") + 1).concat(jarname).concat(".bak"));
            outputStream = new FileOutputStream(jarfile);
            inputStream = new FileInputStream(bak);

            int read = 0;

            while ((read = inputStream.read()) != -1) {
                outputStream.write(read);
            }

            toRet = true;
        }
        catch (IOException IOE) {
            toRet = false;
        }
        finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                }
                catch (IOException e) {}
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                }
                catch (IOException e) {}
            }
        }
        return toRet;
    }
}
