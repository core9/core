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
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.zip.ZipInputStream;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;

import net.jcores.jre.CommonCore;
import net.jcores.jre.CoreKeeper;
import net.jcores.jre.cores.adapter.EmptyAdapter;
import net.jcores.jre.interfaces.functions.F1;
import net.jcores.jre.options.Hash;
import net.jcores.jre.options.MessageType;
import net.jcores.jre.options.Option;
import net.jcores.jre.utils.internal.Sound;
import net.jcores.jre.utils.internal.Streams;

/**
 * Wraps {@link InputStream} objects and exposes some convenience functions. For example, 
 * to read the text of a given stream, or return an empty string if there was no
 * text, write:<br/><br/>
 * 
 * <code>$(stream).text().get("")</code>
 * <br/>
 * <br/>
 * 
 * Note that some functions <b>consume</b> the input stream and close it
 * afterwards. After calling a consuming function the associated stream may not
 * be used anymore. As a result, no two consuming methods may be called, either
 * on the same core, on the wrapped streams or on trailing cores. Instead,
 * a fresh InputStream has to be provided every time. This means, you must
 * do:<br/>
 * <br/>
 * 
 * <code>
 * // Fine<br/> 
 * $(...).input().consuming("x")<br/>
 * $(...).input().consuming("y")<br/>
 * </code><br/>
 * 
 * instead of:<br/>
 * <br/>
 * 
 * <code>
 * // Illegal<br/>
 * input = $(...).input();<br/>
 * input.consuming("x")<br/>
 * input.consuming("y")<br/>
 * </code><br/>
 * 
 * or:<br/>
 * <br/>
 * 
 * <code>
 * // Illegal<br/>
 * input = $(...).input().consuming("x").consuming("y");
 * </code><br/>
 * <br/>
 * 
 * Unfortunately, at the time of writing, consuming methods are the only way to
 * ensure streams and file handles are closed properly. On some platforms
 * (like Mac OS) not closing streams usually has a negligible effect, on other
 * platforms (Win32) you might run into trouble overwriting files.
 * 
 * 
 * @since 1.0
 * @author Ralf Biedert
 */
public class CoreInputStream extends CoreObject<InputStream> {

    /** Used for serialization */
    private static final long serialVersionUID = 1520313333781137198L;

    /**
     * Creates an input stream core.
     * 
     * @param supercore The common core.
     * @param objects The input stream to wrap.
     */
    public CoreInputStream(CommonCore supercore, InputStream... objects) {
        super(supercore, objects);
    }

    
    /**
     * Returns a {@link CoreAudioInputStream} for the input streams enclosed in this core.<br/>
     * <br/>
     * 
     * Examples:
     * <ul>
     * <li><code>$(inputstream).audio().play()</code> - Plays the file in the {@link InputStream}.</li>
     * </ul>
     * 
     * Multi-threaded.<br/>
     * <br/>
     * 
     * @return A CoreAudioInputStream object.
     */
    public CoreAudioInputStream audio() {
        return new CoreAudioInputStream(this.commonCore, map(new F1<InputStream, AudioInputStream>() {
            public AudioInputStream f(InputStream x) {
                return Sound.getStream(x);
            }
        }).array(AudioInputStream.class));
    }

    
    /**
     * Closes all contained streams.<br/>
     * <br/>
     * 
     * Examples:
     * <ul>
     * <li><code>$(s1, s2, s3).close()</code> - Closes all three stream objects.</li>
     * </ul> 
     * 
     * Single-threaded. Consuming.<br/>
     * <br/>
     */
    public void close() {
        for (int i = 0; i < size(); i++) {
            final InputStream inputStream = get(i);
            try {
                inputStream.close();
            } catch (IOException e) {
                this.commonCore.report(MessageType.EXCEPTION, "Error closing stream " + inputStream + ".");
            }
        }
    }

    /**
     * De-serializes the previously serialized core from the enclosed file. Objects that
     * are not serializable are ignored.<br/>
     * <br/>
     * 
     * Examples:
     * <ul>
     * <li><code>$(input).deserialize(String.class)</code> - Deserializes a {@link CoreObject} of Strings that was serialized before with <code>core.serialize()</code>.</li>
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
        final CoreObject<T> core = Streams.deserializeCore(type, get(0), this.commonCore);
        if (core != null) return core;

        return new CoreObject<T>(this.commonCore, new EmptyAdapter<T>());
    }

    /**
     * Treats the given input streams as <code>ZipInputStreams</code> and tries to unzip
     * them to the given directory, creating sub directories as necessary. This is a shorthand
     * notation for <code>zipstream().unzip()</code><br/>
     * <br/>
     * 
     * Examples:
     * <ul>
     * <li><code>$(input).unzip("/tmp")</code> - Unzips the data in the InputStream into a temporary directory.</li>
     * </ul> 
     * 
     * Multi-threaded. Consuming.<br/>
     * <br/>
     * 
     * @param destination The destination to write to.
     */
    public void unzip(final String destination) {
        map(new F1<InputStream, Void>() {
            @Override
            public Void f(InputStream x) {
                try {
                    Streams.doUnzip(x, destination);
                    x.close();
                } catch (IOException e) {
                    CoreInputStream.this.commonCore.report(MessageType.EXCEPTION, "IO error processing " + x + ".");
                }
                return null;
            }
        });
    }

    /**
     * Converts the given input streams to zip streams.<br/>
     * <br/>
     * 
     * 
     * Examples:
     * <ul>
     * <li><code>$(input).zipstream().dir().print()</code> - Lists the content of the given InputStream representing a ZIP.</li>
     * </ul> 
     *  
     * Multi-threaded.<br/>
     * <br/>
     * 
     * @return A CoreZipInputStream.
     */
    public CoreZipInputStream zipstream() {
        return map(new F1<InputStream, ZipInputStream>() {
            public ZipInputStream f(InputStream x) {
                return new ZipInputStream(x);
            }
        }).as(CoreZipInputStream.class);
    }

    
    
    /**
     * Returns a {@link CoreBufferedImage} for the input streams enclosed in this core.<br/>
     * <br/>
     * 
     * Examples:
     * <ul>
     * <li><code>$(inputstream).images().get(0)</code> - Loads the image from the given {@link InputStream}.</li>
     * </ul>
     * 
     * Multi-threaded.<br/>
     * <br/>
     * 
     * @return A CoreBufferedImage object.
     */
    public CoreBufferedImage image() {
        return new CoreBufferedImage(this.commonCore, map(new F1<InputStream, BufferedImage>() {
            public BufferedImage f(InputStream x) {
                try {
                    return ImageIO.read(x);
                } catch (IOException e) {
                    CoreInputStream.this.commonCore.report(MessageType.EXCEPTION, "Error loading image from stream " + x);
                }
                
                return null;
            }
        }).array(BufferedImage.class));
    }

    
    
    /**
     * Returns all lines of all files joint. A core will be returned in which each
     * entry is a String containing the specific file's content.<br/>
     * <br/>
     * 
     * Examples:
     * <ul>
     * <li><code>$(input).text().print()</code> - Loads the text from the stream and prints it to the console.</li>
     * </ul> 
     * 
     * Multi-threaded. Consuming.<br/>
     * <br/>
     * 
     * @return A CoreString containing all contained text.
     */
    public CoreString text() {
        return new CoreString(this.commonCore, map(new F1<InputStream, String>() {
            public String f(final InputStream x) {
                String readText = Streams.readText(CoreInputStream.this.commonCore, x);

                try {
                    x.close();
                } catch (IOException e) {
                    CoreInputStream.this.commonCore.report(MessageType.EXCEPTION, "Error closing stream " + x + ".");
                }

                return readText;
            }
        }).array(String.class));
    }

    /**
     * Creates a hash of the given input streams.<br/>
     * <br/>
     * 
     * Examples:
     * <ul>
     * <li><code>$(input).hash().print()</code> - Prints a hash for the data in the stream.</li>
     * </ul> 
     * 
     * Multi-threaded. Consuming.<br/>
     * <br/>
     * 
     * @param options Accepts a {@link Hash} method.
     * @return A CoreString containing the generated hashes.
     */
    public CoreString hash(Option... options) {
        final String method = CoreKeeper.$(options).get(Hash.class, Hash.MD5).getMethod();

        return new CoreString(this.commonCore, map(new F1<InputStream, String>() {
            public String f(final InputStream x) {
                String generateHash = Streams.generateHash(x, method);

                try {
                    x.close();
                } catch (IOException e) {
                    CoreInputStream.this.commonCore.report(MessageType.EXCEPTION, "Error closing stream " + x + ".");
                }

                return generateHash;
            }
        }).array(String.class));
    }

    /**
     * Uses the enclosed input streams and reads their data into byte buffers.<br/>
     * <br/>
     * 
     * Examples:
     * <ul>
     * <li><code>$(input).data()</code> - Loads all data from the stream and stores it into a {@link CoreByteBuffer}.</li>
     * </ul> 
     * 
     * Multi-threaded. Consuming.<br/>
     * <br/>
     * 
     * @return A CoreByteBuffer with binary content.
     */
    public CoreByteBuffer data() {
        return new CoreByteBuffer(this.commonCore, map(new F1<InputStream, ByteBuffer>() {
            public ByteBuffer f(InputStream x) {
                ByteBuffer byteData = Streams.getByteData(x);

                try {
                    x.close();
                } catch (IOException e) {
                    CoreInputStream.this.commonCore.report(MessageType.EXCEPTION, "Error closing stream " + x + ".");
                }

                return byteData;
            }
        }).array(ByteBuffer.class));
    }
}
