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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import net.jcores.jre.CommonCore;
import net.jcores.jre.cores.Core;
import net.jcores.jre.cores.CoreObject;
import net.jcores.jre.options.MessageType;

/**
 * @author Ralf Biedert
 */
public class Streams {
    /**
     * Unzips the given stream.
     * 
     * @param inputStream
     * @param destinationDirectory
     * @throws IOException
     */
    //@SuppressWarnings("resource")
    public static void doUnzip(InputStream inputStream, String destinationDirectory)
                                                                                    throws IOException {
        final int BUFFER = 8 * 1024;
        final List<String> zipFiles = new ArrayList<String>();
        final File unzipDestinationDirectory = new File(destinationDirectory);

        unzipDestinationDirectory.mkdirs();

        final ZipInputStream zipFile = (ZipInputStream) ((inputStream instanceof ZipInputStream) ? inputStream : new ZipInputStream(inputStream));

        ZipEntry nextEntry = zipFile.getNextEntry();

        // Process each entry
        while (nextEntry != null) {
            // grab a zip file entry
            final String currentEntry = nextEntry.getName();
            final File destFile = new File(unzipDestinationDirectory, currentEntry);

            if (currentEntry.endsWith(".zip")) {
                zipFiles.add(destFile.getAbsolutePath());
            }

            // grab file's parent directory structure
            final File destinationParent = destFile.getParentFile();

            // create the parent directory structure if needed
            destinationParent.mkdirs();

            try {
                // extract file if not a directory
                if (!nextEntry.isDirectory()) {
                    final BufferedInputStream is = new BufferedInputStream(zipFile);
                    int currentByte;
                    // establish buffer for writing file
                    byte data[] = new byte[BUFFER];

                    // write the current file to disk
                    final FileOutputStream fos = new FileOutputStream(destFile);
                    final BufferedOutputStream dest = new BufferedOutputStream(fos, BUFFER);

                    // read and write until last byte is encountered
                    while ((currentByte = is.read(data, 0, BUFFER)) != -1) {
                        dest.write(data, 0, currentByte);
                    }
                    dest.flush();
                    dest.close();
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }

            // zipFile.closeEntry();
            // nextEntry = zipFile.closeEntry();
            nextEntry = zipFile.getNextEntry();
        }

        zipFile.close();
    }

    /**
     * Reads the content of file as text.
     * 
     * @param cc
     * @param is
     * @return .
     */
    public static String readText(CommonCore cc, InputStream is) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            StringBuilder sb = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }

            return sb.toString();
        } catch (IOException e) {
            cc.report(MessageType.EXCEPTION, "Error reading from stream " + is);
        }

        return null;
    }

    /**
     * Returns an input stream for the requested path.
     * 
     * @param zipFile
     * @param path
     * @return .
     * @throws IOException
     */
    public static InputStream getInputStream(ZipInputStream zipFile, String path)
                                                                                 throws IOException {
        ZipEntry nextEntry = zipFile.getNextEntry();

        // Process each entry
        while (nextEntry != null) {
            // grab a zip file entry
            final String currentEntry = nextEntry.getName();

            if (!currentEntry.equals(path)) {
                nextEntry = zipFile.getNextEntry();
                continue;
            }

            return new BufferedInputStream(zipFile);
        }
        return null;
    }

    /**
     * Lists all element within the given stream.
     * 
     * @param zipFile
     * @return .
     * @throws IOException
     */
    public static List<String> list(ZipInputStream zipFile) throws IOException {
        final List<String> rval = new ArrayList<String>();
        ZipEntry nextEntry = zipFile.getNextEntry();

        // Process each entry
        while (nextEntry != null) {
            rval.add(nextEntry.getName());
            nextEntry = zipFile.getNextEntry();
        }

        return rval;
    }

    /**
     * Hashes the given input stream.
     * 
     * @param fis Input stream to use.
     * @param method Method to use.
     * @return A string with the hash.
     */
    @SuppressWarnings("boxing")
    public static String generateHash(InputStream fis, String method) {
        // Try to generate hash
        try {
            final MessageDigest digest = java.security.MessageDigest.getInstance(method);

            // Read Data
            final byte[] data = new byte[1024 * 1024];
            int avail = fis.available();

            // Update hash
            while (avail > 0) {
                avail = Math.min(avail, data.length);

                // TODO: Mmhm, do we need to consider the return value of this (FindBugs report)
                fis.read(data, 0, avail);

                digest.update(data, 0, avail);
                avail = fis.available();
            }

            final byte[] hash = digest.digest();

            // Assemble hash string
            final StringBuilder sb = new StringBuilder();
            for (final byte b : hash) {
                final String format = String.format("%02x", b);
                sb.append(format);
            }

            fis.close();

            final String hashValue = sb.toString().substring(0, sb.toString().length());
            return hashValue;
        } catch (final NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (final FileNotFoundException e) {
            // e.printStackTrace();
        } catch (final IOException e) {
            e.printStackTrace();
        } finally {
            //
        }
        return null;
    }

    /**
     * Stores the given stream to the file.
     * 
     * @param openStream Stream to store.
     * @param file File to store it to.
     */
    public static void saveTo(InputStream openStream, File file) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
        } catch (FileNotFoundException e1) {
            return;
        }
        final byte[] data = new byte[1024 * 1024];
        int lastRead = 1;

        // Update hash
        while (lastRead > 0) {
            try {
                lastRead = openStream.read(data, 0, data.length);
                if (lastRead <= 0) break;
                fos.write(data, 0, lastRead);
            } catch (IOException e) {
                //
                lastRead = 0;
            }
        }

        try {
            fos.close();
        } catch (IOException e) {
            //
        }
    }

    /**
     * Reads all the data of the given input stream.
     * 
     * @param x Input stream to read from
     * 
     * @return ByteBuffer w. data.
     */
    public static ByteBuffer getByteData(InputStream x) {
        byte globaldata[] = new byte[1024 * 1024];
        byte localdata[] = new byte[1024 * 1024];

        try {
            int total = 0;
            int lastread = x.read(localdata);

            while (lastread > 0) {

                // Expand array if it does not fit
                if (total + lastread > globaldata.length) {
                    final byte newglobal[] = new byte[total + 1024 * 1024];
                    System.arraycopy(globaldata, 0, newglobal, 0, total);
                    globaldata = newglobal;
                }

                // Append new data
                System.arraycopy(globaldata, total, localdata, 0, lastread);

                total += lastread;
                lastread = x.read(localdata);
            }

            return ByteBuffer.wrap(globaldata, 0, total);
        } catch (IOException e) {
            //
        }

        return null;
    }

    /**
     * Serializes a core to the given file.
     * 
     * @param core
     * @param fos
     */
    public static void serializeCore(CoreObject<?> core, FileOutputStream fos) {
        try {
            final GZIPOutputStream goz = new GZIPOutputStream(fos);
            final ObjectOutputStream oos = new ObjectOutputStream(goz);

            oos.writeObject(core);
            oos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * De-serializes the given file.
     * 
     * @param <T> .
     * @param type .
     * @param fis .
     * @param core
     * @return .
     */
    @SuppressWarnings("unchecked")
    public static <T> CoreObject<T> deserializeCore(Class<T> type, InputStream fis,
                                                       CommonCore core) {
        try {
            final GZIPInputStream gis = new GZIPInputStream(fis);
            final ObjectInputStream ois = new ObjectInputStream(gis);

            final CoreObject<T> rval = (CoreObject<T>) ois.readObject();
            final Field field = Core.class.getDeclaredField("commonCore");
            field.setAccessible(true);
            field.set(rval, core);
            return rval;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return null;
    }
}
