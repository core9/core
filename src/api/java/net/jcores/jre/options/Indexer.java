/*
 * OptionIndex.java
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
package net.jcores.jre.options;

import java.util.concurrent.ConcurrentHashMap;

import net.jcores.jre.cores.CoreObject;
import net.jcores.jre.interfaces.functions.F1;

/**
 * Can be passed to a {@link CoreObject}'s <code>forEach()</code> / <code>map()</code> to
 * retrieve the current loop index. A use case might look like this:<br/><br/>
 * 
 * <code>final OptionIndexer i = Indexer.NEW();</code><br/>
 * <code>$(objects).forEach(new F1<Object, Object>() { public Void f(Object x) {</code><br/>
 * <code>&nbsp;&nbsp;&nbsp;System.out.println("Current index: " + i.i());</code><br/>
 * <code>}}, i);</code><br/><br/>
 * 
 * An single indexer can be shared among different jCores invocations of the same level (e.g., 
 * chained invocations), but not in nested invocations (i.e., jCores calls within calls).
 * 
 * @author Ralf Biedert
 */
public class Indexer extends Option {
    /**
     * Returns a new {@link Indexer}.
     * 
     * @since 1.0
     * @return A new indexer.
     */
    public static final Indexer NEW() {
        return new Indexer();
    }
    
    /** There must only be one instance */
    private Indexer() {}


    /** Stores all indices for each thread */
    ConcurrentHashMap<Thread, Integer> indices = new ConcurrentHashMap<Thread, Integer>();

    /**
     * Returns the current index. This method <b>only</b> works inside the {@link F1} function, as the 
     * value is bound to the currently executing thread. 
     * 
     * @return The current index or -1 in case the index was not found (which should never happen when 
     * you call the method only within the loop).
     */
    @SuppressWarnings("boxing")
    public int i() {
    	final Thread currentThread = Thread.currentThread();
    	
    	if(!this.indices.containsKey(currentThread)) return -1;
    	
        return this.indices.get(currentThread);
    }

    /**
     * Sets the current index.
     * 
     * @param index The index to set.
     */
    @SuppressWarnings("boxing")
    public void i(int index) {
        this.indices.put(Thread.currentThread(), index);
    }

}
