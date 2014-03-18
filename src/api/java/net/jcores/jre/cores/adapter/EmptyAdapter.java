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
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;


/**
 * Wraps nothing, used for empty cores.
 * 
 * @author Ralf Biedert
 * @since 1.0
 * @param <T>
 */
public final class EmptyAdapter<T> extends AbstractAdapter<T> {

    /** */
    private static final long serialVersionUID = 3490313697090606615L;

    public EmptyAdapter() {
    }
    
    /* (non-Javadoc)
     * @see net.jcores.shared.cores.adapter.AbstractAdapter#size()
     */
    @Override
    public int size() {
        return 0;
    }

    /* (non-Javadoc)
     * @see net.jcores.shared.cores.adapter.AbstractAdapter#get(int)
     */
    @Override
    public T get(int i) {
        return null;
    }

    /* (non-Javadoc)
     * @see net.jcores.shared.cores.adapter.AbstractAdapter#iterator()
     */
    @Override
    public ListIterator<T> iterator() {
        return new ListIterator<T>() {
            
            /* (non-Javadoc)
             * @see java.util.ListIterator#hasNext()
             */
            @Override
            public boolean hasNext() {
                return false;
            }

            /* (non-Javadoc)
             * @see java.util.ListIterator#next()
             */
            @Override
            public T next() {
                return null;
            }

            @Override
            public boolean hasPrevious() {
                return false;
            }

            @Override
            public T previous() {
                return null;
            }

            @Override
            public int nextIndex() {
                return 0;
            }

            @Override
            public int previousIndex() {
                return 0;
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
     * @see net.jcores.shared.cores.adapter.AbstractAdapter#array(java.lang.Class)
     */
    @SuppressWarnings("unchecked")
    @Override
    public <N> N[] array(Class<N> in) {
        return (N[]) Array.newInstance(in, 0);
    }
    
    /* (non-Javadoc)
     * @see net.jcores.shared.cores.adapter.AbstractAdapter#unsafelist()
     */
    @Override
    public List<T> unsafelist() {
        return new ArrayList<T>();
    }

    /* (non-Javadoc)
     * @see net.jcores.shared.cores.adapter.AbstractAdapter#slice(int, int)
     */
    @Override
    public List<T> slice(int start, int end) {
        return new ArrayList<T>();
    }
}
