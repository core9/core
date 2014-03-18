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

import static net.jcores.jre.CoreKeeper.$;

import java.nio.ByteBuffer;

import net.jcores.jre.CommonCore;
import net.jcores.jre.interfaces.functions.F1;
import net.jcores.jre.options.Hash;
import net.jcores.jre.options.Option;
import net.jcores.jre.utils.internal.Bytes;
import net.jcores.jre.utils.internal.Options;

/**
 * Adds <code>hash()</code> and others to {@link ByteBuffer} objects. For example, 
 * to hash some binary data with MD5 you would write:<br/><br/>
 * 
 * <code>String hash = $(data).hash(Option.HASH_MD5).get(0)</code>
 * 
 * 
 * @author Ralf Biedert
 * 
 * @since 1.0
 */
public class CoreByteBuffer extends CoreObject<ByteBuffer> {

    /** Used for serialization */
    private static final long serialVersionUID = 6075205848624531993L;

    /**
     * Creates an ZipInputStream core.
     * 
     * @param supercore The common core.
     * @param objects The ByteBuffers to wrap.
     */
    public CoreByteBuffer(CommonCore supercore, ByteBuffer... objects) {
        super(supercore, objects);
    }

    /**
     * Creates a hash of the given data.<br/>
     * <br/>
     * 
     * Examples:
     * <ul>
     * <li><code>$(d1, d2, d3).hash(Hash.MD5).get(-1)</code> - Creates a hash for each of the passed data objects 
     * and returns the hash value for the last one.</li>
     * </ul>
     * 
     * Multi-threaded.<br/>
     * <br/>
     * 
     * @param options Accepts a {@link Hash} options for the method to use.
     * @since 1.0
     * @return A CoreString containing the generated hashes.
     */
    public CoreString hash(Option... options) {
        final Options options$ = Options.$(this.commonCore, options);
        final String method = $(options).get(Hash.class, Hash.MD5).getMethod();

        return new CoreString(this.commonCore, map(new F1<ByteBuffer, String>() {
            public String f(final ByteBuffer x) {
                return Bytes.generateHash(x, method, options$);
            }
        }).array(String.class));
    }
}
