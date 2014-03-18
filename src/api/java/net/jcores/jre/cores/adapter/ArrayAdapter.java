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

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;


/**
 * Adapts object arrays. 
 * 
 * @author Ralf Biedert
 *
 * @since 1.0
 * @param <T>
 */
public final class ArrayAdapter<T> extends AbstractAdapter<T> {
    /**  */
    private static final long serialVersionUID = 8808538840410854684L;

    /** */
    final T[] array;
    
    final int size;


	@SuppressWarnings("unchecked")
    public ArrayAdapter(T... array) {
        this(array == null ? 0 : array.length, array);
    }
    
    /**
     * @param size
     * @param array
     */

	@SuppressWarnings("unchecked")
    public ArrayAdapter(int size, T... array) {
        this.array = array;
        this.size = size;
    }
    
    
    /* (non-Javadoc)
     * @see net.jcores.shared.cores.adapter.AbstractAdapter#size()
     */
    @Override
    public int size() {
        return this.size;
    }

    /* (non-Javadoc)
     * @see net.jcores.shared.cores.adapter.AbstractAdapter#get(int)
     */
    @Override
    public T get(int i) {
        return this.array[i];
    }

    /* (non-Javadoc)
     * @see net.jcores.shared.cores.adapter.AbstractAdapter#iterator()
     */
    @Override
    public ListIterator<T> iterator() {
        return new ListIterator<T>() {
            int i = 0;
            
            /* (non-Javadoc)
             * @see java.util.ListIterator#hasNext()
             */
            @Override
            public boolean hasNext() {
                if(this.i < ArrayAdapter.this.size) return true;
                return false;
            }

            /* (non-Javadoc)
             * @see java.util.ListIterator#next()
             */
            @Override
            public T next() {
                return ArrayAdapter.this.array[this.i++];
            }

            @Override
            public boolean hasPrevious() {
                if(this.i - 1 >= 0) return true;
                return false;
            }

            @Override
            public T previous() {
                this.i -= 1;
                return ArrayAdapter.this.array[this.i--];
            }

            @Override
            public int nextIndex() {
                return this.i;
            }

            @Override
            public int previousIndex() {
                return this.i - 1;
            }

            @Override
            public void remove() {
                // 
            }

            @Override
            public void set(T e) {
                // 
            }

            @Override
            public void add(T e) {
                //
            }
        };
    }
    
    
    /* (non-Javadoc)
     * @see net.jcores.shared.cores.adapter.AbstractAdapter#clazz()
     */
    @Override
    public Class<?> clazz() {
        return this.array.getClass().getComponentType();
    }

    /* (non-Javadoc)
     * @see net.jcores.shared.cores.adapter.AbstractAdapter#array(java.lang.Class)
     */
    @SuppressWarnings("unchecked")
    @Override
    public <N> N[] array(Class<N> in) {
        final N[] n = (N[]) Array.newInstance(in, 0);

        if (this.array != null)
            return (N[]) Arrays.copyOf(this.array, this.size, n.getClass());

        return n;   
    }
    
    /* (non-Javadoc)
     * @see net.jcores.shared.cores.adapter.AbstractAdapter#array()
     */
    @Override
    public T[] array() {
        return Arrays.copyOf(this.array, this.size);
    }
    
    /**
     * @return .
     */
    @Override
    public T[] unsafearray() {
        return this.array;
    }

    /* (non-Javadoc)
     * @see net.jcores.shared.cores.adapter.AbstractAdapter#list()
     */
    @Override
    public List<T> unsafelist() {
        return Arrays.asList(this.array).subList(0, this.size);
    }

    /* (non-Javadoc)
     * @see net.jcores.shared.cores.adapter.AbstractAdapter#slice(int, int)
     */
    @Override
    public List<T> slice(int start, int end) {
        return Arrays.asList(Arrays.copyOfRange(this.array, start, end));
    }
}
