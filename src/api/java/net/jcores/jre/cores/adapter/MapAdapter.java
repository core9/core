/*
 * ArrayAdapter.java
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
package net.jcores.jre.cores.adapter;

import java.util.ArrayList;
import java.util.Map;

import net.jcores.jre.utils.map.MapEntry;

/**
 * Wraps maps and provides {@link MapEntry} objects on demand.
 * 
 * @author Ralf Biedert
 * 
 * @param <K> The type of the key.
 * @param <V> The type of the value.
 * @since 1.0
 */
public final class MapAdapter<K, V> extends CollectionAdapter<K, MapEntry<K, V>> {
    /**  */
    private static final long serialVersionUID = -7801489204260137868L;

    /** Our map, which we need to resolve values */
    Map<K, V> map;

    public MapAdapter(Map<K, V> map) {
        super(map != null ? map.keySet() : new ArrayList<K>());
        this.map = map;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.jcores.shared.cores.adapter.CollectionAdapter#converter(java.lang.Object)
     */
    @Override
    protected MapEntry<K, V> converter(final K i) {
        return new MapEntry<K, V>(i, null) {

            V cache = null;

            /* (non-Javadoc)
             * @see net.jcores.shared.utils.map.MapEntry#value()
             */
            @Override
            public V value() {
                if (this.cache == null) {
                    this.cache = MapAdapter.this.map.get(i);
                }

                return this.cache;
            }
        };
    }
}
