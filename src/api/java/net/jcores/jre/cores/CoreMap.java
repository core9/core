/*
 * CoreMap.java
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
package net.jcores.jre.cores;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.jcores.jre.CommonCore;
import net.jcores.jre.cores.adapter.AbstractAdapter;
import net.jcores.jre.interfaces.functions.F1;
import net.jcores.jre.utils.map.MapEntry;
import net.jcores.jre.utils.map.MapUtil;

/**
 * Wraps a single {@link Map} and provides {@link MapEntry} objects along 
 * with helper functions.<br/>
 * <br/>
 * 
 * @author Ralf Biedert
 * @param <K> The type of keys.
 * @param <V> The type of values.
 * @since 1.0
 */
public class CoreMap<K, V> extends CoreObject<MapEntry<K, V>> {

    /** Used for serialization */
    private static final long serialVersionUID = 5115270057138570660L;

    /**
     * Wraps a map.
     * 
     * @param supercore The shared CommonCore.
     * @param entries The entries to wrap.
     */
	@SuppressWarnings("unchecked")
    public CoreMap(CommonCore supercore, MapEntry<K, V>... entries) {
        super(supercore, entries);
    }
    
    /**
     * Wraps a map.
     * 
     * @param supercore The shared CommonCore.
     * @param entries The entries to wrap.
     */
    public CoreMap(CommonCore supercore, List<MapEntry<K, V>> entries) {
        super(supercore, entries);
    }

    /**
     * @param supercore The shared CommonCore.
     * @param adapter The adapter.
     */
    public CoreMap(CommonCore supercore, AbstractAdapter<MapEntry<K, V>> adapter) {
        super(supercore, adapter);
    }


    /**
     * Returns the inverse core of this core, i.e., mapping from values to keys.
     * 
     * @return TODO
     */
    public CoreMap<V, K> inverse() {
        return new CoreMap<V, K>(this.commonCore, map(new F1<MapEntry<K, V>, MapEntry<V, K>>() {
            @Override
            public MapEntry<V, K> f(MapEntry<K, V> x) {
                return new MapEntry<V, K>(x.value(), x.key());
            }

        }).unsafelist());
    }

    /**
     * Return the first found key for the given value.<br/>
     * <br/>
     * 
     * Examples:
     * <ul>
     * <li><code>$(map).key("v")</code> - Returns the first key with <code>map.put("k", "v")</code>.</li>
     * </ul>
     * 
     * 
     * Single-threaded.<br/>
     * <br/>
     * 
     * @param value The value to retrieve a key for.
     * @return The key for the given value, or null if no matching value was found.
     */
    public K key(V value) {
        for(MapEntry<K, V> m : this) {
            if (m == null) continue;
            if (m.value().equals(value)) return m.key();
            
        }

        return null;
    }

    /**
     * Return a {@link CoreObject} with only the keys present from this map.<br/>
     * <br/>
     * 
     * Examples:
     * <ul>
     * <li><code>$(map).keys().print()</code> - Prints all key of this map.</li>
     * </ul>
     * 
     * 
     * Single-threaded.<br/>
     * <br/>
     * 
     * @return A {@link CoreObject} with all the keys of this map.
     */
    public CoreObject<K> keys() {
        return new CoreObject<K>(this.commonCore, map(new F1<MapEntry<K, V>, K>() {
            @Override
            public K f(MapEntry<K, V> x) {
                return x.key();
            }
        }).unsafelist());
    }

    /**
     * Return a true {@link HashMap} (with the decorator {@link MapUtil}) containing all elements
     * enclosed in this core. <br/>
     * 
     * Examples:
     * <ul>
     * <li><code>$(map).inverse().map()</code> - Returns a map with all key-value paris reversed.</li>
     * </ul>
     * 
     * 
     * Single-threaded.<br/>
     * <br/>
     * 
     * @return A {@link MapUtil} with all the entries of this CoreMap.
     */
    public MapUtil<K, V> map() {
        final MapUtil<K, V> rval = new MapUtil<K, V>(new HashMap<K, V>());

        for(MapEntry<K, V> mapEntry : this) {
            if (mapEntry == null) continue;
            rval.put(mapEntry.key(), mapEntry.value());
        }

        return rval;
    }

    /**
     * Return a {@link CoreObject} with only the values present from this map.<br/>
     * <br/>
     * 
     * Examples:
     * <ul>
     * <li><code>$(map).values().unique().size()</code> - Guaranteed to be smaller or equal than
     * <code>$(map).size()</code>.</li>
     * </ul>
     * 
     * 
     * Single-threaded.<br/>
     * <br/>
     * 
     * @return A {@link CoreObject} with all the values of this map.
     */
    public CoreObject<V> values() {
        return new CoreObject<V>(this.commonCore, map(new F1<MapEntry<K, V>, V>() {
            @Override
            public V f(MapEntry<K, V> x) {
                return x.value();
            }
        }).unsafelist());
    }

    /**
     * Return the value for the given key.<br/>
     * <br/>
     * 
     * Examples:
     * <ul>
     * <li><code>$(map).value("v")</code> - Same as <code>map.get("v")</code>.</li>
     * </ul>
     * 
     * 
     * Single-threaded.<br/>
     * <br/>
     * 
     * @param key The key to receive a value for.
     * @return The value for the given key, or null if no matching key was found.
     */
    public V value(K key) {
        for(MapEntry<K, V> t : this) {
            if (t == null) continue;
            if (t.key().equals(key)) return t.value();
        }

        return null;
    }

}
