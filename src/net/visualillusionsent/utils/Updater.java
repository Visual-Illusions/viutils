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
 * &copy; 2012 Visual Illusions Entertainment <a href="http://visualillusionsent.net">http://visualillusionsent.net</a>
 * 
 * @since VIUtils 1.0
 * @version 1.0
 * @author Jason (darkdiplomat)
 */
public class Updater {
    private String downloadurl, jarloc, jarname;

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
    public boolean performUpdate() throws UpdateException {
        UtilsLogger.info("Please wait, downloading latest version of " + jarname + "...");

        if (!jarloc.endsWith(".jar")) {
            UtilsLogger.info("The jar file location needs to end with .jar... Terminating update...");
            throw new UpdateException("Failed to update due to: 'Incorrect File Extension'");
        }

        File local = new File(jarloc);
        if (!local.exists()) {
            UtilsLogger.warning("[VIUtils] Unable to find " + jarloc + "... Terminating update...");
            throw new UpdateException("Failed to update due to: 'FileNotFound'");
        }

        // BackUp just in case of failure
        File bak = null;
        try {
            bak = backupjar(jarloc);
        }
        catch (IOException e) {
            throw new UpdateException("Failed to update due to: 'Backup failed'");
        }

        if (loadAllClasses(jarloc)) {
            try {
                OutputStream outputStream = new FileOutputStream(local);
                URL url = new URI(downloadurl).toURL();
                InputStream inputStream = url.openConnection().getInputStream();

                byte[] buffer = new byte[1024];
                int read = 0;

                while ((read = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, read);
                }

                outputStream.close();
                inputStream.close();
                UtilsLogger.info("Successfully downloaded latest version of " + jarname + "!");
                bak.delete();
                return true;
            }
            catch (IOException ioe) {
                UtilsLogger.warning("Failed to download new version. Restoring old version...", ioe);

                // Restore
                if (restorejar(jarloc)) {
                    bak.delete();
                }
                throw new UpdateException("Failed to update due to: 'Failed to download'");
            }
            catch (URISyntaxException urise) {
                // Restore
                if (restorejar(jarloc)) {
                    bak.delete();
                }
                UtilsLogger.warning("There was an error with the URI syntax... Restoring old version...", urise);
                throw new UpdateException("Failed to update due to: 'Failed to download'");
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
    private boolean loadAllClasses(String jarloc) throws UpdateException {
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
                    path = path.substring(0, path.length() - ".class".length());

                    // Load it
                    ClassLoader.getSystemClassLoader().loadClass(path);
                }
            }
            jar.close();
            return true;
        }
        catch (IOException ioe) {
            UtilsLogger.severe("An IOException has occurred! Update terminated!", ioe);
            throw new UpdateException("Failed to update due to: 'IOException during jar load'");
        }
        catch (ClassNotFoundException cnfe) {
            UtilsLogger.severe("An ClassNotFoundException has occurred! Update terminated!", cnfe);
            throw new UpdateException("Failed to update due to: 'ClassNotFoundException during jar load'");
        }
        catch (Exception e) {
            UtilsLogger.severe("An Unexpected Exception has occurred! Update terminated!", e);
            throw new UpdateException("Failed to update due to: 'Unexpected Exception during jar load'");
        }
    }

    private File backupjar(String jarfile) throws IOException {
        File bak = new File(jarloc.substring(0, jarloc.lastIndexOf("/") + 1) + jarname + ".bak");
        OutputStream outputStream = new FileOutputStream(bak);
        InputStream inputStream = new FileInputStream(jarfile);

        int read = 0;

        while ((read = inputStream.read()) != -1) {
            outputStream.write(read);
        }

        outputStream.close();
        inputStream.close();
        return bak;
    }

    private boolean restorejar(String jarfile) {
        try {
            File bak = new File(jarloc.substring(0, jarloc.lastIndexOf("/") + 1) + jarname + ".bak");
            OutputStream outputStream = new FileOutputStream(jarfile);
            InputStream inputStream = new FileInputStream(bak);

            int read = 0;

            while ((read = inputStream.read()) != -1) {
                outputStream.write(read);
            }

            outputStream.close();
            inputStream.close();
            return true;
        }
        catch (IOException IOE) {}
        return false;
    }
}
