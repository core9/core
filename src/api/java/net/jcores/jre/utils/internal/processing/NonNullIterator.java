/*
 * NonNullIterator.java
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
package net.jcores.jre.utils.internal.processing;

import java.util.ListIterator;

public class NonNullIterator<T> implements ListIterator<T> {

    /** The parent iterator */
    private ListIterator<T> parent;

    /** The next element we output */
    private T next = null;

    /** The previous element we output */
    private T previous = null;

    /**  */
    private int nextIndex = 0;

    /** */
    private int previousIndex = -1;

    /**
     * Constructs a new non-null iterator.
     * 
     * @param parent
     */
    public NonNullIterator(ListIterator<T> parent) {
        this.parent = parent;
    }

    private void getNext() {
        this.next = null;

        while (this.parent.hasNext()) {
            this.nextIndex = this.parent.nextIndex();
            this.next = this.parent.next();
            if (this.next != null) return;
        }
    }

    private void getPrevious() {
        this.previous = null;

        while (this.parent.hasPrevious()) {
            this.previousIndex = this.parent.previousIndex();
            this.previous = this.parent.previous();
            if (this.previous != null) return;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.ListIterator#hasNext()
     */
    @Override
    public boolean hasNext() {
        // First check if we have a next element
        if (this.next != null) return true;

        getNext();

        // And check what we can return
        return this.next != null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.ListIterator#next()
     */
    @Override
    public T next() {
        final T rval = this.next;
        this.previous = this.next;
        this.previousIndex = this.nextIndex;

        getNext();

        return rval;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.ListIterator#hasPrevious()
     */
    @Override
    public boolean hasPrevious() {
        // First check if we have a next element
        if (this.previous != null) return true;

        getPrevious();

        // And check what we can return
        return this.previous != null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.ListIterator#previous()
     */
    @Override
    public T previous() {
        final T rval = this.previous;
        this.next = this.previous;
        this.nextIndex = this.previousIndex;

        getPrevious();

        return rval;

    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.ListIterator#nextIndex()
     */
    @Override
    public int nextIndex() {
        return this.nextIndex;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.ListIterator#previousIndex()
     */
    @Override
    public int previousIndex() {
        return this.previousIndex;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.ListIterator#remove()
     */
    @Override
    public void remove() {}

    /*
     * (non-Javadoc)
     * 
     * @see java.util.ListIterator#set(java.lang.Object)
     */
    @Override
    public void set(T e) {}

    /*
     * (non-Javadoc)
     * 
     * @see java.util.ListIterator#add(java.lang.Object)
     */
    @Override
    public void add(T e) {}
}
