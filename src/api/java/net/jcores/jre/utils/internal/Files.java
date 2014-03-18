/*
 * FileUtil.java
 * 
 * Copyright (c) 2010, Ralf Biedert All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 * 
 * Redistributions of source code must retain the above copyright notice, this list of
 * conditions and the following disclaimer. Redistributions in binary form must reproduce the
 * above copyright notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 
 * Neither the name of the author nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS
 * OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
 * TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 */
package net.jcores.jre.utils.internal;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import net.jcores.jre.CommonCore;
import net.jcores.jre.CoreKeeper;
import net.jcores.jre.options.MessageType;

/**
 * @author Ralf Biedert
 */
public class Files {
    /**
     * Reads the content of file as text.
     * 
     * @param cc
     * @param file
     * @return .
     */
    public static String readText(CommonCore cc, File file) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            StringBuilder sb = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }

            return sb.toString();
        } catch (FileNotFoundException e) {
            cc.report(MessageType.EXCEPTION, "File not found " + file);
        } catch (IOException e) {
            cc.report(MessageType.EXCEPTION, "Error reading from file " + file);
        } finally {
            if (reader != null) try {
                reader.close();
            } catch (IOException e) {
                cc.report(MessageType.EXCEPTION, "Error closing file " + file);
            }
        }

        return null;
    }

    /**
     * Lists all elements under the given root.
     * 
     * @param root
     * @param listDirs
     * @return A list of elements
     */
    public static File[] dir(File root, boolean listDirs) {
        final List<File> rval = new ArrayList<File>();

        // Get top level files ...
        List<File> next = new ArrayList<File>();
        File[] listed = root.listFiles();

        // In case there are no files in the given diretory element, return
        if (listed == null) return null;

        // Queue all we found
        next.addAll(Arrays.asList(listed));

        while (next.size() > 0) {
            // Take over all from the next queue
            listed = next.toArray(new File[0]);
            next.clear();

            // Now check for each item
            for (File file : listed) {
                if (!file.isDirectory()) {
                    rval.add(file);
                    continue;
                }

                if (listDirs) rval.add(file);

                final File[] listFiles = file.listFiles();
                if (listFiles == null) continue;
                next.addAll(Arrays.asList(listFiles));
            }
        }

        return rval.toArray(new File[0]);
    }

    /**
     * Copies a file .
     * 
     * @param cc
     * @param from
     * @param to
     * @return .
     */
    public static File[] copy(CommonCore cc, File from, File to) {

        // Create directory if they don't exist
        final boolean todir = to.getAbsolutePath().endsWith("/") || to.isDirectory();
        final boolean fromdir = from.getAbsolutePath().endsWith("/") || from.isDirectory();

        // If we had a source dir
        if (fromdir) {
            final File[] elements = dir(from, false);
            final List<File> files = new ArrayList<File>();
            for (File file : elements) {
                final String subname = file.getAbsolutePath().replace(from.getAbsolutePath(), "");
                files.addAll(Arrays.asList(copy(cc, file, new File(to + "/" + subname))));
            }

            return files.toArray(new File[0]);
        }

        // If its a dir, create the dir, if its a file, create its parent
        if (todir) to.mkdirs();
        else {
            if (!to.getParentFile().mkdirs())
                cc.report(MessageType.EXCEPTION, "Unable to create directory " + to.getParentFile());
        }

        final File realTo = todir ? new File(to.getAbsoluteFile() + "/" + from.getName()) : to;

        // Streams for input and output
        FileInputStream fis = null;
        FileOutputStream fos = null;

        // Now copy the actual files. TODO: Also copy from when it is a directory!
        try {
            fis = new FileInputStream(from);
            fos = new FileOutputStream(realTo);

            byte[] buf = new byte[64 * 1024];
            int i = 0;
            while ((i = fis.read(buf)) != -1) {
                fos.write(buf, 0, i);
            }
        } catch (Exception e) {
            cc.report(MessageType.EXCEPTION, "Error copying file " + from + " " + to + " due to a " + e.getMessage());
        } finally {
            if (fis != null) try {
                fis.close();
            } catch (IOException e) {}
            if (fos != null) try {
                fos.close();
            } catch (IOException e) {}
        }

        return new File[] { realTo };
    }

    /**
     * Zips a number of files into the target.
     * 
     * @param target
     * @param t
     */
    public static void zipFiles(File target, File[] t) {
        final byte[] buffer = new byte[32 * 1024]; // Create a buffer for copying
        int bytesRead;

        try {
            // Open output zip file
            FileOutputStream fos = new FileOutputStream(target);
            ZipOutputStream out = new ZipOutputStream(fos);
            out.setLevel(9);

            // Process all given files
            for (File file : t) {
                // If it is a file, store it directly, otherwise store subfiles
                final File toStore[] = file.isDirectory() ? CoreKeeper.$(file).dir().array(File.class) : CoreKeeper.$(file).array(File.class);
                final String absolute = file.getAbsolutePath();

                for (File file2 : toStore) {
                    // Now check for each item. If this item was added because the original entry denoted
                    // a file, then add this entry by its name only. Otherwise add the entry as something
                    // starting relative to its path
                    String entryname = file.isDirectory() ? file2.getAbsolutePath().substring(absolute.length() + 1) : file2.getName();
                    entryname = entryname.replaceAll("\\\\", "/");
                    entryname = file2.isDirectory() ? entryname + "/" : entryname;

                    try {
                        final FileInputStream in = new FileInputStream(file2);
                        final ZipEntry entry = new ZipEntry(entryname);
                        out.putNextEntry(entry);
                        while ((bytesRead = in.read(buffer)) != -1)
                            out.write(buffer, 0, bytesRead);
                        in.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    out.closeEntry();
                }
            }

            // Close our result
            out.close();
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * JARs a number of files into the target. Why does Java have to be so shitty
     * that on some VMs a JAR created as ZIP won't be recognized anymore?!
     * 
     * @param target
     * @param manifest
     * @param t
     */
    public static void jarFiles(File target, Manifest manifest, File[] t) {
        final byte[] buffer = new byte[32 * 1024]; // Create a buffer for copying
        int bytesRead;

        try {
            // Open output zip file
            FileOutputStream fos = new FileOutputStream(target);
            JarOutputStream out = manifest == null ? new JarOutputStream(fos) : new JarOutputStream(fos, manifest);
            out.setLevel(9);

            // Process all given files
            for (File file : t) {
                // If it is a file, store it directly, otherwise store subfiles
                final File toStore[] = file.isDirectory() ? CoreKeeper.$(file).dir().array(File.class) : CoreKeeper.$(file).array(File.class);
                final String absolute = file.getAbsolutePath();

                for (File file2 : toStore) {
                    // Now check for each item. If this item was added because the original entry denoted
                    // a file, then add this entry by its name only. Otherwise add the entry as something
                    // starting relative to its path
                    String entryname = file.isDirectory() ? file2.getAbsolutePath().substring(absolute.length() + 1) : file2.getName();
                    entryname = entryname.replaceAll("\\\\", "/");
                    entryname = file2.isDirectory() ? entryname + "/" : entryname;

                    // There are some items we should skip by default
                    final String lc = entryname.toLowerCase();
                    if (lc.endsWith("meta-inf/eclipsef.rsa")) continue;
                    if (lc.endsWith("meta-inf/eclipsef.sf")) continue;

                    try {
                        final FileInputStream in = new FileInputStream(file2);
                        final JarEntry entry = new JarEntry(entryname);
                        out.putNextEntry(entry);
                        while ((bytesRead = in.read(buffer)) != -1)
                            out.write(buffer, 0, bytesRead);
                        in.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    out.closeEntry();
                }
            }

            // Close our result
            out.close();
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
