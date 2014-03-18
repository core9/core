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

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.zip.ZipInputStream;

import net.jcores.jre.CommonCore;
import net.jcores.jre.cores.adapter.AbstractAdapter;
import net.jcores.jre.interfaces.functions.F1;
import net.jcores.jre.options.MessageType;
import net.jcores.jre.utils.internal.Streams;
import net.jcores.jre.utils.internal.wrapper.InputStreamWrapper;

/**
 * Utility functions working on {@link ZipInputStream} objects. For example, 
 * to unzip all files in a given zipstream to the directory <code>target</code>, write:<br/><br/>
 * 
 * <code>$(zipstream).unzip("target")</code><br/>
 * <br/>
 * 
 * <b>Important note: See {@link CoreInputStream} regarding <i>consuming</i> methods.</b>
 * 
 * @author Ralf Biedert
 * @since 1.0
 */
public class CoreZipInputStream extends CoreObject<ZipInputStream> {

    /** Used for serialization */
    private static final long serialVersionUID = 5934382074823292082L;

    /** Indicates if multiple get() operations have been performed */
    private boolean multipleGet = false;

    /**
     * Creates an ZipInputStream core.
     * 
     * @param supercore The common core.
     * @param objects The strings to wrap.
     */
    public CoreZipInputStream(CommonCore supercore, ZipInputStream... objects) {
        super(supercore, objects);
    }

    /**
     * @param supercore The shared CommonCore.
     * @param adapter The adapter.
     */
    public CoreZipInputStream(CommonCore supercore, AbstractAdapter<ZipInputStream> adapter) {
        super(supercore, adapter);
    }

    /**
     * Unzips all enclosed streams to the given directory. Usually only called with
     * a single enclosed object. <br/>
     * <br/>
     * 
     * 
     * Examples:
     * <ul>
     * <li><code>$("file.zip").file().input().zipstream().unzip("/destination")</code> - Unzips the given file to the destination directory.</li>
     * </ul>
     * 
     * Multi-threaded. Consuming.<br/>
     * <br/>
     * 
     * @param destination The destination to unzip the given files to. All necessary
     * directories will be created.
     * 
     * @return Return <code>this</code>.
     */
    public CoreZipInputStream unzip(final String destination) {
        map(new F1<ZipInputStream, Void>() {
            @Override
            public Void f(ZipInputStream x) {
                try {
                    Streams.doUnzip(x, destination);
                    x.close();
                } catch (IOException e) {
                    CoreZipInputStream.this.commonCore.report(MessageType.EXCEPTION, "IO error processing " + e + ".");
                }
                return null;
            }
        });

        return this;
    }

    /**
     * Lists all entries within all ZIP files. Usually only called with a single enclosed
     * element.<br/>
     * <br/>
     * 
     * Examples:
     * <ul>
     * <li><code>$("file.zip").file().input().zipstream().dir().print()</code> - Prints what is packed into the zip file.</li>
     * </ul>
     * 
     * Multi-threaded. Consuming.<br/>
     * <br/>
     * 
     * @return A CoreString, enclosing a list of all entries is returned.
     */
    public CoreString dir() {
        return map(new F1<ZipInputStream, List<String>>() {
            @Override
            public List<String> f(ZipInputStream x) {
                try {
                    final List<String> list = Streams.list(x);
                    x.close();
                    return list;
                } catch (IOException e) {
                    CoreZipInputStream.this.commonCore.report(MessageType.EXCEPTION, "IO error processing " + e + ".");
                }
                return null;
            }
        }).expand(String.class).as(CoreString.class);
    }

    /**
     * Returns an input stream for the given ZIP-file-entry. This only uses the first
     * element
     * within the core, if there is any.<br/>
     * <br/>
     * 
     * YOU MUST NOT CALL THIS FUNCTION SEVERAL TIMES on the same core. The reason is, the
     * internal input stream is 'drained' by each get and the method will be unable to
     * see prior entries after retrieving latter ones. For example, given the zip file
     * contains
     * three entries A, B, C. If you get("B"), the input stream will be consumed until B
     * is found
     * which is returned. If you then get("A") there is no way of rolling back the stream,
     * so all
     * this method sees is 'C' and it cannot return 'A'.<br/>
     * <br/>
     * 
     * Examples:
     * <ul>
     * <li><code>$("file.zip").file().input().zipstream().get("README.txt")</code> - Returns an {@link InputStream} for the top-level element <code>README.txt</code> of the given archive.</li>
     * </ul>
     * 
     * Single-threaded, size-of-one. Consuming.<br/>
     * <br/>
     * 
     * @param path The zip-entry-path to obtain.
     * 
     * @return The opened InputStream for the given zip entry, or null if nothing was
     * found.
     */
    public InputStream get(String path) {
        if (this.multipleGet) {
            this.commonCore.report(MessageType.MISUSE, "You must not call get() on a ZipStream multiple times! Latter calls might fail. Check the documentation.");
        }

        final ZipInputStream zipInputStream = get(0);
        if (zipInputStream == null) return null;

        try {
            final InputStream inputStream = Streams.getInputStream(zipInputStream, path);

            // We CAN NOT close the parent stream right away, because then we would
            // invalidate
            // the returned stream. Instead we have to wait until the returned stream is
            // closed as
            // well.
            return new InputStreamWrapper(inputStream) {
                @Override
                public void close() throws IOException {
                    super.close();
                    inputStream.close();
                }
            };
        } catch (IOException e) {
            //
        } finally {
            this.multipleGet = true;
        }

        return null;
    }
}
