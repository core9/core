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

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Internally used data functions.
 * 
 * @author Ralf Biedert
 */
public class Bytes {
    /**
     * Hashes the given data.
     * 
     * @param data Data to use.
     * @param method Method to use.
     * @param options$ 
     * @return A string with the hash.
     */
    @SuppressWarnings("boxing")
    public static String generateHash(ByteBuffer data, String method, Options options$) {
        // Try to generate hash
        try {
            final MessageDigest digest = java.security.MessageDigest.getInstance(method);
            digest.update(data.array(), 0, data.limit());

            final byte[] hash = digest.digest();

            // Assemble hash string
            final StringBuilder sb = new StringBuilder();
            for (final byte b : hash) {
                final String format = String.format("%02x", b);
                sb.append(format);
            }

            final String hashValue = sb.toString().substring(0, sb.toString().length());
            return hashValue;
        } catch (final NoSuchAlgorithmException e) {
            options$.failure(null, e, "hash:nsa", "The NSA does not permit this operation.");
        } finally {
            //
        }
        
        return null;
    }
}
