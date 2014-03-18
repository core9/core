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
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Wraps arbitrary collections with on-demand element access and caching.
 * 
 * @author Ralf Biedert
 * @since 1.0
 * @param <I> The type of the incoming collection 
 * @param <O> The type of the adapter.
 */
public class CollectionAdapter<I, O> extends AbstractAdapter<O> implements List<O> {
    /** */
    private static final long serialVersionUID = 7010286694628298017L;

    /** Our primary collection iterator */
    Iterator<I> iterator;

    /** Our cache array */
    AtomicReferenceArray<O> array;

    /** Specifies up to which index we have elements in our array cache */
    AtomicInteger inCache;

    /** Locks access to the collection's iterator */
    ReentrantLock collectionLock;
    
    /** The inclusive start index which we handle */
    final int start;
    
    /** The inclusive end index we handle */
    final int end;

    public CollectionAdapter(Collection<I> collection) {
        this.inCache = new AtomicInteger(-1);
        this.collectionLock = new ReentrantLock();
        this.iterator = collection.iterator();
        this.array = new AtomicReferenceArray<O>(collection.size());
        this.start = 0;
        this.end = collection.size() - 1;
    }

    private CollectionAdapter(int start, int end) {
        this.start = start;
        this.end = end;
    }

    
    /**
     * Standard converter just converts
     * 
     * @param i
     * @return
     */
    @SuppressWarnings("unchecked")
    protected O converter(I i) {
        return (O) i;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see net.jcores.shared.cores.adapter.AbstractAdapter#size()
     */
    @Override
    public int size() {
        return (this.end - this.start) + 1;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.jcores.shared.cores.adapter.AbstractAdapter#get(int)
     */
    @Override
    public O get(int i) {
        final int ii = i + this.start;
        cacheUntil(ii);
        return _get(ii);
    }
    
    private final O _get(int i) {
        return this.array.get(i);
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.jcores.shared.cores.adapter.AbstractAdapter#iterator()
     */
    @Override
    public ListIterator<O> iterator() {
        return new ListIterator<O>() {
            volatile int i = 0;
            
            @Override
            public boolean hasNext() {
                return CollectionAdapter.this.start + this.i <= CollectionAdapter.this.end;
            }

            @Override
            public O next() {
                cacheUntil(CollectionAdapter.this.start + this.i);
                return get(this.i++);
            }

            @Override
            public boolean hasPrevious() {
                return this.i > CollectionAdapter.this.start;
            }

            @Override
            public O previous() {
                this.i--;
                return get(CollectionAdapter.this.start + this.i);
            }

            @Override
            public int nextIndex() {
                return CollectionAdapter.this.start + this.i;
            }

            @Override
            public int previousIndex() {
                return CollectionAdapter.this.start + this.i - 1;
            }

            @Override
            public void remove() {
            }

            @Override
            public void set(O e) {
            }

            @Override
            public void add(O e) {
            }
        };
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.jcores.shared.cores.adapter.AbstractAdapter#array(java.lang.Class)
     */
    @SuppressWarnings("unchecked")
    @Override
    public <N> N[] array(Class<N> in) {
        cacheAll();

        final N[] rval = (N[]) Array.newInstance(in, size());
        for (int i = this.start; i < rval.length; i++) {
            rval[i] = (N) _get(i);
        }

        return rval;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.jcores.shared.cores.adapter.AbstractAdapter#unsafelist()
     */
    @Override
    public List<O> unsafelist() {
        return this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.jcores.shared.cores.adapter.AbstractAdapter#slice(int, int)
     */
    @Override
    public List<O> slice(int a, int b) {
        final CollectionAdapter<I, O> adapter = new CollectionAdapter<I, O>(this.start + a, this.start + a + (b - a) - 1);
        adapter.array = this.array;
        adapter.collectionLock = this.collectionLock;
        adapter.inCache = this.inCache;
        adapter.iterator = this.iterator;
        
        return adapter;
    }

    /**
     * Cache all our elemnts
     */
    protected void cacheAll() {
        cacheUntil(this.end);
    }

    
    /**
     * Cache the collection until the given element 
     * 
     * @param request
     */
    protected void cacheUntil(int request) {
        // When the cached value already exceeds the limit we dont have to do anything
        if (this.inCache.intValue() >= request) return;

        this.collectionLock.lock();
        try {
            //System.out.println(Thread.currentThread() + ": " + this.inCache + " -> " + request);
            // Iterator might have been gone due to another thread that just exited the lock while we entered
            if (this.iterator == null) return;
            
            while (this.iterator.hasNext()) {
                int i = this.inCache.get();
                this.array.set(i + 1, converter(this.iterator.next()));
                this.inCache.set(i + 1);
                if (i > request) break;
            }

            // Eventually dump the iterator to free up space
            if (!this.iterator.hasNext()) {
                this.iterator = null;
            }
        } finally {
            this.collectionLock.unlock();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.List#isEmpty()
     */
    @Override
    public boolean isEmpty() {
        return this.end - this.start < 0;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.List#contains(java.lang.Object)
     */
    @Override
    public boolean contains(Object o) {
        final ListIterator<O> i = iterator();
        while (i.hasNext()) {
            O next = i.next();
            if (next == null) continue;
            if (next.equals(o)) return true;
        }
        return false;
    }

    /* (non-Javadoc)
     * @see java.util.List#toArray()
     */
    @Override
    public Object[] toArray() {
        cacheAll();

        final Object[] rval = (Object[]) Array.newInstance(Object.class, size());
        for (int i = this.start; i < rval.length; i++) {
            rval[i] = _get(i);
        }

        return rval;
    }

    /* (non-Javadoc)
     * @see java.util.List#toArray(T[])
     */
    @SuppressWarnings({ "unchecked" })
    @Override
    public <T> T[] toArray(T[] a) {
        cacheAll();

        // Our return value array
        T[] rval = null;

        // Check if the passed array fits the data
        if (a.length >= this.array.length()) {
            rval = a;
        } else {
            rval = (T[]) Array.newInstance(a.getClass().getComponentType(), size());
        }

        // Fill the array
        for (int i = this.start; i < rval.length; i++) {
            rval[i] = (T) _get(i);
        }

        return rval;

    }

    /* (non-Javadoc)
     * @see java.util.List#add(java.lang.Object)
     */
    @Override
    public boolean add(O e) {
        return false;
    }

    /* (non-Javadoc)
     * @see java.util.List#remove(java.lang.Object)
     */
    @Override
    public boolean remove(Object o) {
        return false;
    }

    /* (non-Javadoc)
     * @see java.util.List#containsAll(java.util.Collection)
     */
    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object o : c) {
            if (!contains(o)) return false;
        }
        return true;
    }

    /* (non-Javadoc)
     * @see java.util.List#addAll(java.util.Collection)
     */
    @Override
    public boolean addAll(Collection<? extends O> c) {
        return false;
    }

    /* (non-Javadoc)
     * @see java.util.List#addAll(int, java.util.Collection)
     */
    @Override
    public boolean addAll(int index, Collection<? extends O> c) {
        return false;
    }

    /* (non-Javadoc)
     * @see java.util.List#removeAll(java.util.Collection)
     */
    @Override
    public boolean removeAll(Collection<?> c) {
        return false;
    }

    /* (non-Javadoc)
     * @see java.util.List#retainAll(java.util.Collection)
     */
    @Override
    public boolean retainAll(Collection<?> c) {
        return false;
    }

    /* (non-Javadoc)
     * @see java.util.List#clear()
     */
    @Override
    public void clear() {}

    /* (non-Javadoc)
     * @see java.util.List#set(int, java.lang.Object)
     */
    @Override
    public O set(int index, O element) {
        return null;
    }

    /* (non-Javadoc)
     * @see java.util.List#add(int, java.lang.Object)
     */
    @Override
    public void add(int index, O element) {}

    /* (non-Javadoc)
     * @see java.util.List#remove(int)
     */
    @Override
    public O remove(int index) {
        return null;
    }

    /* (non-Javadoc)
     * @see java.util.List#indexOf(java.lang.Object)
     */
    @Override
    public int indexOf(Object o) {
        final ListIterator<O> i = iterator();
        while (i.hasNext()) {
            int index = i.nextIndex();
            O next = i.next();
            if (next == null) continue;
            if (next.equals(o)) return index;
        }

        return -1;
    }

    /* (non-Javadoc)
     * @see java.util.List#lastIndexOf(java.lang.Object)
     */
    @Override
    public int lastIndexOf(Object o) {
        cacheAll();
        for (int i = this.end; i >= this.start; i--) {
            O next = get(i);
            if (next == null) continue;
            if (next.equals(o)) return i;
        }
        return -1;
    }

    /* (non-Javadoc)
     * @see java.util.List#listIterator()
     */
    @Override
    public ListIterator<O> listIterator() {
        return iterator();
    }

    /* (non-Javadoc)
     * @see java.util.List#listIterator(int)
     */
    @Override
    public ListIterator<O> listIterator(int index) {
        ListIterator<O> iterator2 = iterator();
        for (int i = 0; i < index; i++) {
            iterator2.next();
        }

        return iterator2;
    }

    /* (non-Javadoc)
     * @see java.util.List#subList(int, int)
     */
    @Override
    public List<O> subList(int fromIndex, int toIndex) {
        return slice(fromIndex, toIndex);
    }
}
