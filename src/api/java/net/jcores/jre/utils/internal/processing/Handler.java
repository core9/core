/*
 * Handler.java
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
package net.jcores.jre.utils.internal.processing;

import java.lang.reflect.Array;
import java.util.concurrent.atomic.AtomicReference;

import net.jcores.jre.cores.CoreObject;

/**
 * Handles parallel tasks. You do not need this.
 * 
 * @author Ralf Biedert
 * @param <I> Handler for the type of core.
 * @param <O> The output type for this handler.
 */
public abstract class Handler<I, O> {
    /** The core we handle */
    protected final CoreObject<I> core;

    /** The reference to our return array (atomic, since it will be accessed from many threads). */
    protected final AtomicReference<O[]> returnArray = new AtomicReference<O[]>();

    /**
     * Creates a handler for the given core.
     * 
     * @param core
     */
    public Handler(CoreObject<I> core) {
        this.core = core;
    }

    /**
     * Return the size of the array.
     * 
     * @return .
     */
    public CoreObject<I> core() {
        return this.core;
    }

    /**
     * Tries to update the array and returns the most recent result.
     * 
     * @param object
     * @return .
     */
    public O[] updateReturnArray(O[] object) {
        this.returnArray.compareAndSet(null, object);
        return this.returnArray.get();
    }

    /**
     * Returns the return array.
     * 
     * @return The return array we had.
     */
    @SuppressWarnings("unchecked")
    public O[] getFinalReturnArray() {
        // In case we don't have a return array (which happens when the mapper never
        // returned something sensible), we create a simple object array of our size, so
        // that the core's size stays consistent.
        O rval[] = this.returnArray.get();
        if (rval == null) {
            rval = (O[]) Array.newInstance(Object.class, this.core.size());
        }

        return rval;
    }
}