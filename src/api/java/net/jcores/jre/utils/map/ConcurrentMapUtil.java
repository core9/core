/*
 * SmartMap.java
 * 
 * Copyright (c) 2011, Ralf Biedert All rights reserved.
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
package net.jcores.jre.utils.map;

import java.util.concurrent.ConcurrentMap;

/**
 * A {@link ConcurrentMap} decorator that, similar to <a href="http://code.google.com/p/guava-libraries/">Google's Guava</a>, 
 * provides some extra functions for maps. 
 * 
 * @author Ralf Biedert
 *
 * @param <K> The type of the key.
 * @param <V> The type of the value.
 * @since 1.0
 */
public class ConcurrentMapUtil<K, V> extends MapUtil<K, V> implements ConcurrentMap<K, V> {

    /** Here we also store the object, for concurrent opearations */
    private ConcurrentMap<K, V> concurrentObject;

    public ConcurrentMapUtil(ConcurrentMap<K, V> object) {
        super(object);
        this.concurrentObject = object;
    }

    /* (non-Javadoc)
     * @see java.util.concurrent.ConcurrentMap#putIfAbsent(java.lang.Object, java.lang.Object)
     */
    @Override
    public V putIfAbsent(K key, V value) {
        return this.concurrentObject.putIfAbsent(key, value);
    }

    /* (non-Javadoc)
     * @see java.util.concurrent.ConcurrentMap#remove(java.lang.Object, java.lang.Object)
     */
    @Override
    public boolean remove(Object key, Object value) {
        return this.concurrentObject.remove(key, value);
    }

    /* (non-Javadoc)
     * @see java.util.concurrent.ConcurrentMap#replace(java.lang.Object, java.lang.Object)
     */
    @Override
    public V replace(K key, V value) {
        return this.concurrentObject.replace(key, value);
    }

    /* (non-Javadoc)
     * @see java.util.concurrent.ConcurrentMap#replace(java.lang.Object, java.lang.Object, java.lang.Object)
     */
    @Override
    public boolean replace(K key, V oldValue, V newValue) {
        return this.concurrentObject.replace(key, oldValue, newValue);
    }
    
}
