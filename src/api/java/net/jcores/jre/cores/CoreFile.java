/*
 * CoreFile.java
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
package net.jcores.jre.cores;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.List;
import java.util.jar.Manifest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;

import net.jcores.jre.CommonCore;
import net.jcores.jre.CoreKeeper;
import net.jcores.jre.cores.adapter.AbstractAdapter;
import net.jcores.jre.interfaces.functions.F1;
import net.jcores.jre.interfaces.functions.F1Object2Bool;
import net.jcores.jre.options.DefaultOption;
import net.jcores.jre.options.ListDirectories;
import net.jcores.jre.options.MessageType;
import net.jcores.jre.options.Option;
import net.jcores.jre.utils.internal.Files;
import net.jcores.jre.utils.internal.Options;
import net.jcores.jre.utils.internal.Sound;
import net.jcores.jre.utils.internal.Streams;

/**
 * Convenience functions for {@link File} objects, e.g. <code>text()</code>. For example,
 * to list all files in a given path (denoted by a file-object),
 * write:<br/>
 * <br/>
 * 
 * <code>$(path).dir().print()</code>
 * 
 * 
 * @author Ralf Biedert
 * @since 1.0
 */
public class CoreFile extends CoreObject<File> {

    /** Used for serialization */
    private static final long serialVersionUID = -8743359735096052185L;

    /**
     * Creates a file core.
     * 
     * @param supercore The common core.
     * @param files The files to wrap.
     */
    public CoreFile(CommonCore supercore, File... files) {
        super(supercore, files);
    }

    /**
     * @param supercore The shared CommonCore.
     * @param adapter The adapter.
     */
    public CoreFile(CommonCore supercore, AbstractAdapter<File> adapter) {
        super(supercore, adapter);
    }

    /**
     * Appends the object.toString() to all given files. The files will be created if they don't
     * exist. The function is usually only called with a single enclosed file object. <br/>
     * <br/>
     * 
     * Examples:
     * <ul>
     * <li><code>$("test.txt").file().append("Hello World")</code> - Appends <code>'Hello World'</code> to the file
     * <code>'test.txt'</code>.</li>
     * <li><code>$("result.csv").file().delete().append("a,b,c")</code> - Writes a data set into a file, the previous
     * content will have been removed.</li>
     * </ul>
     * 
     * 
     * Multi-threaded.<br/>
     * <br/>
     * 
     * @param object The object to write to all enclosed files.
     * @param options The {@link DefaultOption} objects we support.
     * 
     * @return The same core file object (<code>this</code>).
     */
    public CoreFile append(Object object, final Option... options) {
        if (object == null) return this;

        final CommonCore cc = this.commonCore;
        final String string = object.toString();

        map(new F1<File, Object>() {
            public Object f(File x) {
                try {
                    PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream(x, true)), "UTF-8"));
                    printWriter.append(string);
                    printWriter.flush();
                    printWriter.close();
                } catch (FileNotFoundException e) {
                    Options.$(cc, options).failure(x, e, "append:filenotfound", "File could not be found.");
                } catch (UnsupportedEncodingException e) {
                    Options.$(cc, options).failure(x, e, "append:badencoding", "Encoding not supported.");
                }
                return null;
            }
        });

        return this;
    }

    /**
     * Appends the object.toString() to all given files plus an additional new line. The files will be created 
     * if they don't exist. The function is usually only called with a single enclosed file object. <br/>
     * <br/>
     * 
     * Examples:
     * <ul>
     * <li><code>$("test.txt").file().appendln("Hello World")</code> - Appends <code>'Hello World\n'</code> to the file
     * <code>'test.txt'</code>.</li>
     * </ul>
     * 
     * Multi-threaded.<br/>
     * <br/>
     * 
     * @param object The object to write to all enclosed files.
     * @param options The {@link DefaultOption} objects we support.
     * 
     * @return The same core file object (<code>this</code>).
     */
    public CoreFile appendln(Object object, final Option... options) {
        if (object == null) return this;
        return append(object.toString() + "\n", options);
    }

    
    /**
     * Treats the given files as audio files and returns a {@link CoreAudioInputStream} for them.<br/>
     * <br/>
     * 
     * Examples:
     * <ul>
     * <li><code>$("test.wav").file().audio().play()</code> - Treats the file as an audio file and plays it.</li>
     * </ul>
     * 
     * Multi-threaded.<br/>
     * <br/>
     * 
     * @return The new core for the {@link AudioInputStream} objects.
     */
    public CoreAudioInputStream audio() {
        return new CoreAudioInputStream(this.commonCore, map(new F1<File, AudioInputStream>() {
            @Override
            public AudioInputStream f(File x) {
                return Sound.getStream(x);
            }
        }).array(AudioInputStream.class));
    }

    /**
     * Copies all enclosed files to the destination. If <code>destination</code> is a directory all enclosed objects
     * will be copied into that directory. If the destination is a file then it will be overwritten. In that case, and
     * if this core encloses multiple files or directories, it is undefined what the content of <code>destination</code>
     * will be afterwards<br/>
     * <br/>
     * 
     * 
     * Examples:
     * <ul>
     * <li><code>$("source.zip").file().copy("/dest/")</code> - Copies the file <code>source.zip</code> to the toplevel
     * folder <code>/dest</code></li>
     * <li><code>$("src/").file().copy("bin/")</code> - Copies the directory <code>src</code> to the directory
     * <code>bin</code></li>
     * </ul>
     * 
     * Multi-threaded.<br/>
     * <br/>
     * 
     * @param destination The destination to write to. Can be a directory or a file. Directories <b>must end with a
     * slash
     * (<code>/</code>) or pre-exist</b>, otherwise they will be treated as files!
     * 
     * @return The new core file object, containing all files that have been copied..
     */
    public CoreFile copy(String destination) {
        if (destination == null) {
            this.commonCore.report(MessageType.MISUSE, "Destination null for copy().");
            return this;
        }

        final File dest = new File(destination);
        final CommonCore cc = this.commonCore;

        return new CoreFile(this.commonCore, map(new F1<File, File[]>() {
            @Override
            public File[] f(File x) {
                return Files.copy(cc, x, dest);
            }
        }).expand(File.class).array(File.class));
    }

    /**
     * Opens the enclosed file streams as binary files and reads their data into byte
     * buffers.
     * File stream which could not be opened will be returned as null.<br/>
     * <br/>
     * 
     * Examples:
     * <ul>
     * <li><code>$("data.jar").file().data().hash().print()</code> - Prints the (MD5) hash of the given file.</li>
     * </ul>
     * 
     * Multi-threaded.<br/>
     * <br/>
     * 
     * @return A CoreByteBuffer with binary content.
     */
    public CoreByteBuffer data() {
        final CommonCore cc = this.commonCore;
        return new CoreByteBuffer(this.commonCore, map(new F1<File, ByteBuffer>() {
            @SuppressWarnings("resource")
            public ByteBuffer f(File x) {
                try {
                    final FileChannel channel = new FileInputStream(x).getChannel();
                    final long size = channel.size();

                    final ByteBuffer buffer = ByteBuffer.allocate((int) size);
                    int read = channel.read(buffer);

                    if (read != size) {
                        cc.report(MessageType.EXCEPTION, "Error reading data() from " + x + ". Size mismatch (" + read + " != " + size + ")");
                        return null;
                    }

                    channel.close();
                    return buffer;
                } catch (FileNotFoundException e) {
                    cc.report(MessageType.EXCEPTION, "Error reading data() from " + x + ". File not found!");
                    return null;
                } catch (IOException e) {
                    cc.report(MessageType.EXCEPTION, "Error reading data() from " + x + ". IOException!");
                    return null;
                }
            }
        }).array(ByteBuffer.class));
    }

    /**
     * Deletes the given file objects, recursively. Also deletes directories. Unless the
     * files or directories are write protected or locked they should be gone afterwards.<br/>
     * <br/>
     * 
     * Examples:
     * <ul>
     * <li><code>$(".").dir().filter(".*png$").delete()</code> - Deletes all PNG files below the given directory.</li>
     * </ul>
     * 
     * Multi-threaded.<br/>
     * <br/>
     * 
     * @return The same core file object (<code>this</code>).
     */
    public CoreFile delete() {
        final CommonCore cc = this.commonCore;
        map(new F1<File, Void>() {
            public Void f(File x) {
                int lastSize = Integer.MAX_VALUE;
                List<File> list = CoreKeeper.$(x).dir(ListDirectories.DO).list();

                while (list.size() < lastSize) {
                    lastSize = list.size();

                    for (File file : list) {
                        file.delete();
                    }

                    list = CoreKeeper.$(x).dir(ListDirectories.DO).list();
                }

                // Try to delete the entry
                if (!x.delete()) {
                    cc.report(MessageType.EXCEPTION, "Unable to delete " + x);
                }

                return null;
            }
        });

        return this;
    }

    /**
     * De-serializes the previously serialized {@link CoreObject} from the enclosed file.
     * Objects that are not serializable are ignored.<br/>
     * <br/>
     * 
     * 
     * Examples:
     * <ul>
     * <li><code>$("storage.ser").file().deserialize(String.class)</code> - Restores a {@link Core} that previously
     * contained a number of Strings and that was written with <code>core.serialize("storage.ser")</code>.</li>
     * </ul>
     * 
     * Single-threaded. Size-of-one.<br/>
     * <br/>
     * 
     * @param <T> Type of the returned core's content.
     * @param type Type of the returned core's content.
     * @param options Currently not used.
     * @return The previously serialized core (using <code>.serialize()</code>).
     * @see CoreObject
     */
    public <T> CoreObject<T> deserialize(Class<T> type, Option... options) {

        if (size() > 1)
            this.commonCore.report(MessageType.MISUSE, "deserialize() should not be used on cores with more than one class!");

        // Try to restore the core, and don't forget to set the commonCore
        try {
            final CoreObject<T> core = Streams.deserializeCore(type, new FileInputStream(get(0)), this.commonCore);
            if (core != null) return core;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

        return new CoreObject<T>(this.commonCore, type, null);
    }

    /**
     * Lists the contents of all sub directories. A CoreFile with all found files
     * in all sub directories is returned.<br/>
     * <br/>
     * 
     * Examples:
     * <ul>
     * <li><code>$(".").file().dir().print()</code> - Lists all files below the current directory.</li>
     * </ul>
     * 
     * Multi-threaded.<br/>
     * <br/>
     * 
     * @param options Accepts {@link ListDirectories} in case sub directories should be considered as well.
     * 
     * @return A CoreFile with all found files (and, if selected, directories).
     */
    public CoreFile dir(Option... options) {

        // Check if we should emit diretories
        final boolean listDirs = net.jcores.jre.CoreKeeper.$(options).contains(ListDirectories.DO);

        return map(new F1<File, File[]>() {
            @Override
            public File[] f(File x) {
                return Files.dir(x, listDirs);
            }
        }).expand(File.class).unique().as(CoreFile.class);
    }

    /**
     * Returns the file sizes for all enclose file objects<br/>
     * <br/>
     * 
     * Examples:
     * <ul>
     * <li><code>$(".").file().dir().filesize().sum()</code> - Computes how much space all files below this directory
     * consume.</li>
     * </ul>
     * 
     * Multi-threaded.<br/>
     * <br/>
     * 
     * @return A CoreFile containing a filtered subset of our elements.
     */
    public CoreNumber filesize() {
        return new CoreNumber(this.commonCore, map(new F1<File, Long>() {
            @SuppressWarnings("boxing")
            public Long f(final File x) {
                return x.length();
            }
        }).array(Long.class));
    }

    /**
     * Filters all files by their name using the given regular expression. <br/>
     * <br/>
     * 
     * Examples:
     * <ul>
     * <li><code>$(".").file().dir().filter(".*java$").print()</code> - Prints all files that end with
     * <code>.java</code>.</li>
     * </ul>
     * 
     * Multi-threaded.<br/>
     * <br/>
     * 
     * @param regex The regular expression to use.
     * @param options Currently none used.
     * 
     * @return A CoreFile containing a filtered subset of our elements.
     */
    @Override
    public CoreFile filter(final String regex, Option... options) {
        final Pattern p = Pattern.compile(regex);
        return new CoreFile(this.commonCore, filter(new F1Object2Bool<File>() {
            public boolean f(File x) {
                final Matcher matcher = p.matcher(x.getAbsolutePath());
                return matcher.matches();
            }
        }, options).array(File.class));
    }

    /**
     * Tries to load all enclosed files as images.<br/>
     * <br/>
     * 
     * Examples:
     * <ul>
     * <li><code>$("image.jpg").file().images().get(0)</code> - Returns a {@link BufferedImage} for the specified file.</li>
     * </ul>
     * 
     * Multi-threaded.<br/>
     * <br/>
     * 
     * @return A CoreBufferedImage with the loaded images.
     */
    public CoreBufferedImage image() {
        final CommonCore cc = this.commonCore;
        return new CoreBufferedImage(this.commonCore, map(new F1<File, BufferedImage>() {
            public BufferedImage f(File x) {
                try {
                    return ImageIO.read(x);
                } catch (IOException e) {
                    cc.report(MessageType.EXCEPTION, "Error loading image " + x);
                }
                return null;
            }
        }).array(BufferedImage.class));
    }

    /**
     * Opens the given file objects as input streams. File stream which could not be
     * opened will be returned as null.<br/>
     * <br/>
     * 
     * Examples:
     * <ul>
     * <li><code>$("data.raw").file().input().get(0)</code> - Returns an {@link InputStream} for the specified file.</li>
     * </ul>
     * 
     * Multi-threaded.<br/>
     * <br/>
     * 
     * @return A CoreInputStream with the opened files.
     */
    public CoreInputStream input() {
        return new CoreInputStream(this.commonCore, map(new F1<File, InputStream>() {
            public InputStream f(File x) {
                try {
                    return new BufferedInputStream(new FileInputStream(x));
                } catch (FileNotFoundException e) {}
                return null;
            }
        }).array(InputStream.class));
    }

    /**
     * Puts all enclosed files into the JAR file <code>target</code>. If files are enclosed individually they will be
     * stored as a top-level entry. If directories are enclosed in this core, the relative paths below that directory
     * are preserved.<br/>
     * <br/>
     * 
     * Examples:
     * <ul>
     * <li><code>$("bin").file().jar("application.jar")</code> - Creates a JAR and puts the content of the folder in it.
     * </li>
     * </ul>
     * 
     * Single-threaded.<br/>
     * <br/>
     * 
     * @param target The file to write the ZIP to.
     * @param manifest The manifest to add to the JAR file.
     * @param options Currently none used.
     * @return This Core again.
     */
    public CoreFile jar(String target, Manifest manifest, Option... options) {
        Files.jarFiles(new File(target), manifest, this.unsafeadapter().array());
        return this;
    }

    /**
     * Creates all enclosed directories and return this object again.<br/>
     * <br/>
     * 
     * Examples:
     * <ul>
     * <li><code>$("output").file().mkdir()</code> - Creates the folder <code>output</code>.</li>
     * </ul>
     * 
     * Single-threaded.<br/>
     * <br/>
     * 
     * @return This Core again.
     */
    public CoreFile mkdir() {
        for (int i = 0; i < size(); i++) {
            final File file = get(i);
            if (file == null) continue;
            file.mkdirs();
        }

        return this;
    }

    /**
     * Returns all lines of all files joint. A core will be returned in which each
     * entry is a String containing the specific file's content. This is a shorthand
     * notation for <code>inputstream().text()</code><br/>
     * <br/>
     * 
     * Examples:
     * <ul>
     * <li><code>$("README.txt").file().text().print()</code> - Prints the README file.</li>
     * </ul>
     * 
     * 
     * Multi-threaded.<br/>
     * <br/>
     * 
     * @return A CoreString object containing the files' contents.
     */
    public CoreString text() {
        final CommonCore cc = this.commonCore;
        return new CoreString(this.commonCore, map(new F1<File, String>() {
            public String f(final File x) {
                return Files.readText(cc, x);
            }
        }).array(String.class));
    }

    /**
     * Converts all files to URIs.<br/>
     * <br/>
     * 
     * Examples:
     * <ul>
     * <li><code>$("code.jar").file().uri().get(0)</code> - Returns the URI for the file <code>code.jar</code>.</li>
     * </ul>
     * 
     * Multi-threaded.<br/>
     * <br/>
     * 
     * @return A CoreURI object with all converted files.
     */
    public CoreURI uri() {
        return new CoreURI(this.commonCore, map(new F1<File, URI>() {
            public URI f(File x) {
                return x.toURI();
            }
        }).array(URI.class));
    }

    /**
     * Puts all enclosed files into the ZIP file <code>target</code>. If files are enclosed individually they will be
     * stored as a top-level entry. If directories are enclosed in this core, the relative paths below that directory
     * are preserved.<br/>
     * <br/>
     * 
     * Examples:
     * <ul>
     * <li><code>$("file.a", "file.b").file().zip("archive.zip")</code> - Creates a zip and puts the two given files in
     * it.</li>
     * </ul>
     * 
     * Single-threaded.<br/>
     * <br/>
     * 
     * @param target The file to write the ZIP to.
     * @param options Currently none used.
     * @return This Core again.
     */
    public CoreFile zip(String target, Option... options) {
        Files.zipFiles(new File(target), this.adapter.array());
        return this;
    }
}
