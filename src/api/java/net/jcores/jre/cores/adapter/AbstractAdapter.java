/*
 * AbstractAdapter.java
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

import java.io.Serializable;
import java.util.List;
import java.util.ListIterator;


/**
 * The base class of all adapters. Each adapter should care to be as fast as 
 * possible, parameter checks should in most cases not be performed since jCores
 * will already take care of that.
 * 
 * @author Ralf Biedert
 *
 * @since 1.0
 * @param <T> The type of elements this adapter holds.
 */
public abstract class AbstractAdapter<T> implements Iterable<T>, Serializable {
    
    /** */
    private static final long serialVersionUID = 4367018268996427422L;

    /** 
     * Returns an array copy for this adapter.
     * 
     * @param in The class of the array.
     * @param <N> The type 
     * @return An array of the given type.
     * 
     */
    public abstract <N> N[] array(Class<N> in);
    
    /**
     * Returns an array copy.
     * 
     * @return A copy.
     */
    @SuppressWarnings("unchecked")
    public T[] array() {
        return (T[]) array(clazz());
    }
    
    /** 
     * Returns this size of this adapter
     *  
     * @return . 
     */
    public abstract int size();
    
    /**
     * Returns the element at position i.
     * 
     * @param i
     * 
     * @return .
     */
    public abstract T get(int i);
    
    
    /**
     * Creates an iterator to iterate over all elements.
     * 
     * @return An iterator.
     */
    public abstract ListIterator<T> iterator();
    
    
    /**
     * Returns the item class of the adapter (Collections should return the 
     * actual base type of all elements).
     * 
     * @return Clazz The class (e.g., String.class) of the collection type. 
     */
    public Class<?> clazz() {
        final ListIterator<T> it = iterator();
        
        while(it.hasNext()) {
            T next = it.next();
            if(next == null) continue;
            return next.getClass();
        }
        
        return Object.class;
    }
    
    /**
     * Returns a list for this adapter.
     * 
     * @return The list for this adapter.
     */
    public abstract List<T> unsafelist();
    
    
    /**
     * Returns an array for this adapter.
     * 
     * @return The list for this adapter.
     */
    public T[] unsafearray() {
        return array();
    }
    
    
    /**
     * Returns a slice of the given adapter.
     * 
     * @param start
     * @param end
     * @return The slice.
     */
    public abstract List<T> slice(int start, int end);
}
