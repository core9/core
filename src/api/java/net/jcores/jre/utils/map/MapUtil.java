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

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import net.jcores.jre.interfaces.functions.F1;
import net.jcores.jre.utils.VanillaUtil;

/**
 * A map decorator that, similar to <a href="http://code.google.com/p/guava-libraries/">Google's Guava</a>, 
 * provides some extra functions for maps. 
 * 
 * @author Ralf Biedert
 *
 * @param <K> The type of the key.
 * @param <V> The type of the value.
 * @since 1.0
 */
public class MapUtil<K, V> extends VanillaUtil<Map<K, V>> implements Map<K, V> {
    
    /** The generator that will be tried when get would return null */
    private F1<K, V> generator;
    
    /** The default to return when get and the generator would return null */
    private V dfault;


    /**
     * Wraps the given map.
     * 
     * @param object The map to wrap.
     */
    public MapUtil(Map<K, V> object) {
        super(object);
    }
    
    /**
     * Sets the default which will be returned when <code>get()</code> and <code>generator(g)</code> 
     * would otherwise return <code>null</code>.
     * 
     * @param dflt The default to return.
     * @return This map.
     */
    public MapUtil<K, V> dfault(V dflt) {
        this.dfault = dflt;
        return this;
    }
    
    
    /**
     * Sets the default which will be returned when <code>get()</code> would otherwise return <code>null</code>.
     * 
     * @param gen The generator to use.
     * @return This map.
     */
    public MapUtil<K, V> generator(F1<K, V> gen) {
        this.generator = gen;
        return this;
    }


    /**
     * Returns a key mapped to the given type.
     * 
     * @param <R> The return type.
     * @param key The key to query.
     * @param type The type as which the object should be returned.
     * @return The corresponding object if it existed, or null.
     */
    @SuppressWarnings("unchecked")
    public <R> R get(String key, Class<R> type) {
        return (R) get(key);
    }

    /**
     * Returns a key mapped to the given type.
     * 
     * @param <R> The return type.
     * @param key The key to query.
     * @param dflt The default to return if otherwise null would be returned. 
     * @return The corresponding object if it existed, or the default.
     */
    @SuppressWarnings("unchecked")
    public <R> R get(String key, R dflt) {
        Object t = get(key);
        if (t == null) return dflt;
        return (R) t;
    }

    
    /**
     * Tries to return the given key as an integer.
     * 
     * @param key The key to return.
     * 
     * @return The integer value, <code>0</code> if the object was <code>null</code>, 
     * or <code>hashCode()</code> if the object was unknown.
     */
    public int i(String key) {
        final Object elem = get(key);

        // If we didnt have anything, return 0
        if (elem == null) return 0;

        // If the object if of type number
        if (elem instanceof Number) { return ((Number) elem).intValue(); }

        // If the object if of type number
        if (elem instanceof String) { return Integer.parseInt((String) elem); }

        // Last resort, return the hash code ...
        return elem.hashCode();
    }
    
    /**
     * Tries to return the given key as a long.
     * 
     * @param key The key to return.
     * 
     * @return The long value, <code>0</code> if the object was <code>null</code>, 
     * or <code>hashCode()</code> if the object was unknown.
     */
    public long l(String key) {
        final Object elem = get(key);

        // If we didnt have anything, return 0
        if (elem == null) return 0;

        // If the object if of type number
        if (elem instanceof Number) { return ((Number) elem).longValue(); }

        // If the object if of type number
        if (elem instanceof String) { return Long.parseLong((String) elem); }

        // Last resort, return the hash code ...
        return elem.hashCode();
    }
    

    /**
     * Returns the given key as a string.
     * 
     * @param key The key to return.
     * 
     * @return Returns <code>map.get(key).toString()</code>.
     */
    public String s(String key) {
        final Object elem = get(key);
        if (elem == null) return null;
        return elem.toString();
    }

    /**
     * Returns the given key as an integer.
     * 
     * @param key The key to return.
     * 
     * @return The integer value, <code>0</code> if the object was <code>null</code>, 
     * or <code>hashCode()</code> if the object was unknown.
     */
    public double d(String key) {
        final Object elem = get(key);

        // If we didnt have anything, return 0
        if (elem == null) return 0;

        // If the object if of type number
        if (elem instanceof Number) { return ((Number) elem).doubleValue(); }

        // If the object if of type number
        if (elem instanceof String) { return Double.parseDouble((String) elem); }

        // Last resort, return the hash code ...
        return elem.hashCode();
    }


    /**
     * Puts the given integer into the slot named key.
     * 
     * @param key The key to put.
     * @param value The integer value to put.
     */
    @SuppressWarnings({"unchecked" })
    public void put(K key, int value) {
        put(key, (V) Integer.valueOf(value));
    }

    /**
     * Puts the given integer into the slot named key
     * 
     * @param key The key to put.
     * @param value THe double value to put.
     */
    @SuppressWarnings({ "unchecked" })
    public void put(K key, double value) {
        put(key, (V) Double.valueOf(value));
    }

    /* (non-Javadoc)
     * @see java.util.Map#clear()
     */
    public void clear() {
        this.object.clear();
    }

    /* (non-Javadoc)
     * @see java.util.Map#containsKey(java.lang.Object)
     */
    public boolean containsKey(Object key) {
        return this.object.containsKey(key);
    }
    
    /* (non-Javadoc)
     * @see java.util.Map#containsValue(java.lang.Object)
     */
    public boolean containsValue(Object value) {
        return this.object.containsValue(value);
    }
   
    /* (non-Javadoc)
     * @see java.util.Map#entrySet()
     */
    public Set<java.util.Map.Entry<K, V>> entrySet() {
        return this.object.entrySet();
    }

    /* (non-Javadoc)
     * @see java.util.Map#get(java.lang.Object)
     */
    @SuppressWarnings("unchecked")
    public V get(Object key) {
        V v = this.object.get(key);
        
        // Check if we have to generate the object
        if(v == null && this.generator != null) {
            v = this.generator.f((K) key);
            put((K) key, v);
        }
        
        // Check if we can use teh default
        if(v == null && this.dfault != null) {
            v = this.dfault;
            put((K) key, v);            
        }
        
        return v;
    }

    /* (non-Javadoc)
     * @see java.util.Map#isEmpty()
     */
    public boolean isEmpty() {
        return this.object.isEmpty();
    }

 
    /* (non-Javadoc)
     * @see java.util.Map#keySet()
     */
    public Set<K> keySet() {
        return this.object.keySet();
    }

    
    /* (non-Javadoc)
     * @see java.util.Map#put(java.lang.Object, java.lang.Object)
     */
    public V put(K key, V value) {
        return this.object.put(key, value);
    }

   
    /* (non-Javadoc)
     * @see java.util.Map#putAll(java.util.Map)
     */
    public void putAll(Map<? extends K, ? extends V> t) {
        this.object.putAll(t);
    }

  
    /* (non-Javadoc)
     * @see java.util.Map#remove(java.lang.Object)
     */
    public V remove(Object key) {
        return this.object.remove(key);
    }

 
    /* (non-Javadoc)
     * @see java.util.Map#size()
     */
    public int size() {
        return this.object.size();
    }

 
    /* (non-Javadoc)
     * @see java.util.Map#values()
     */
    public Collection<V> values() {
        return this.object.values();
    }
}
