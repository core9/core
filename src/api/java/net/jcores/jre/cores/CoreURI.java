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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import net.jcores.jre.CommonCore;
import net.jcores.jre.CoreKeeper;
import net.jcores.jre.annotations.SupportsOption;
import net.jcores.jre.cores.adapter.AbstractAdapter;
import net.jcores.jre.interfaces.functions.F1;
import net.jcores.jre.options.MessageType;
import net.jcores.jre.options.OnFailure;
import net.jcores.jre.options.Option;
import net.jcores.jre.utils.internal.Options;
import net.jcores.jre.utils.internal.Streams;

/**
 * {@link URI} and {@link URL} helper functions. For example, 
 * to download a file to some the <code>downloads</code> directory:<br/><br/>
 * 
 * <code>$(uri).download("downloads")</code>
 * 
 * @author Ralf Biedert
 * 
 * @since 1.0
 */
public class CoreURI extends CoreObject<URI> {

    /** Used for serialization */
    private static final long serialVersionUID = 7366734773387957013L;

    /**
     * Creates an URI core.
     * 
     * @param supercore The common core.
     * @param objects The adapter to wrap.
     */
    public CoreURI(CommonCore supercore, URI... objects) {
        super(supercore, objects);
    }
    
    /**
     * Creates an URI core.
     * 
     * @param supercore The common core.
     * @param adapter The adapter to wrap.
     */
    public CoreURI(CommonCore supercore, AbstractAdapter<URI> adapter) {
        super(supercore, adapter);
    }


    /**
     * Opens the associated input stream.<br/>
     * <br/>
     * 
     * Examples:
     * <ul>
     * <li><code>$("http://jcores.net/index.html").uri().input()</code> - Opens an input stream for the given URI.</li>
     * </ul>
     * 
     * Multi-threaded.<br/>
     * <br/>
     * @param options Optional arguments, especially {@link OnFailure}.
     * 
     * @return A CoreInputStream object enclosing the opened input streams.
     */
    @SupportsOption(options = {OnFailure.class})
    public CoreInputStream input(Option ... options) {
        final Options options$ = Options.$(this.commonCore, options);

        return new CoreInputStream(this.commonCore, map(new F1<URI, InputStream>() {
            public InputStream f(URI x) {
                try {
                    final URL url = x.toURL();
                    final InputStream openStream = url.openStream();
                    return openStream;
                } catch (MalformedURLException e) {
                    options$.failure(x, e, "input:urimalformed", "Malformed URI.");
                } catch (IOException e) {
                    options$.failure(x, e, "input:ioerror", "Error opening the URI.");
                }

                return null;
            }
        }).array(InputStream.class));
    }
    
    /**
     * Downloads the enclosed URIs to a temporary directories and returns core
     * containing their filenames.<br/>
     * <br/>
     * 
     * Examples:
     * <ul>
     * <li><code>$("http://jcores.net/index.html").uri().download()</code> - Downloads the file at the given URI to the temporary directory.</li>
     * </ul>
     * 
     * Multi-threaded.<br/>
     * <br/>
     * 
     * @return A CoreFile object enclosing the files of all downloaded URIs.
     */
    public CoreFile download() {
        final CommonCore cc = this.commonCore;

        return new CoreFile(this.commonCore, map(new F1<URI, File>() {
            public File f(URI x) {
                try {
                    final URL url = x.toURL();
                    final InputStream openStream = url.openStream();
                    final File file = File.createTempFile("jcores.download.", ".tmp");

                    Streams.saveTo(openStream, file);

                    openStream.close();

                    return file;
                } catch (MalformedURLException e) {
                    cc.report(MessageType.EXCEPTION, "URI " + x + " could not be transformed into an URL.");
                } catch (IOException e) {
                    cc.report(MessageType.EXCEPTION, "URI " + x + " could not be opened for reading.");
                }

                return null;
            }
        }).array(File.class));
    }

    /**
     * Downloads the enclosed URIs to the given directory, using the filename encoded
     * within the uri and returns a core containing their filenames.<br/>
     * <br/>
     * 
     * Examples:
     * <ul>
     * <li><code>$("http://jcores.net/index.html").uri().download("downloads")</code> - Downloads the file at the given URI to downloads directory.</li>
     * </ul>
     * 
     * Multi-threaded.<br/>
     * <br/>
     * 
     * @param path The directory to which the files will be downloaded.
     * 
     * @return A CoreFile object enclosing the files of all downloaded URIs.
     */
    public CoreFile download(final String path) {
        // Create output directory 
        new File(path).mkdirs();
        final CommonCore cc = this.commonCore;

        return new CoreFile(this.commonCore, map(new F1<URI, File>() {
            public File f(URI x) {
                try {
                    final String filepath = CoreKeeper.$(x.getPath()).split("/").get(-1);
                    final URL url = x.toURL();
                    final InputStream openStream = url.openStream();
                    final File file = new File(path + "/" + filepath);

                    Streams.saveTo(openStream, file);

                    openStream.close();

                    return file;
                } catch (MalformedURLException e) {
                    cc.report(MessageType.EXCEPTION, "URI " + x + " could not be transformed into an URL.");
                } catch (IOException e) {
                    cc.report(MessageType.EXCEPTION, "URI " + x + " could not be opened for reading.");
                }

                return null;
            }
        }).array(File.class));
    }


    /**
     * Tries to convert all URIs to local File objects.<br/>
     * <br/>
     * 
     * Examples:
     * <ul>
     * <li><code>$(uri).file().delete()</code> - Deletes a local file that was present as an URI.</li>
     * </ul>
     * 
     * Multi-threaded.<br/>
     * <br/>
     * 
     * @return A CoreFile object enclosing all successfully converted file handles
     */
    public CoreFile file() {
        return new CoreFile(this.commonCore, map(new F1<URI, File>() {
            public File f(URI x) {
                try {
                    return new File(x);
                } catch (Exception e) {
                    //
                }
                return null;
            }
        }).array(File.class));
    }

}
