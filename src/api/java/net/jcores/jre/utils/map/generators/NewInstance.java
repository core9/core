/*
 * NewInstance.java
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
package net.jcores.jre.utils.map.generators;

import java.util.ArrayList;

import net.jcores.jre.annotations.Beta;
import net.jcores.jre.interfaces.functions.F1;

/**
 * A generator that creates new instances of the given type.
 * 
 * @author Ralf Biedert
 * @param <K> The type of the key.
 * @param <V> The type of the value.
 * @since 1.0
 */
public class NewInstance<K, V> implements F1<K, V> {
    /** The class to spawn */
    private Class<? extends V> clazz;
    
    
    /**
     * Creates a new instance generator that creates lists in demand. Does not work as it should (generics mess). 
     * 
     * @param <A> The type of the key.
     * @param <B> The type of the value (will always be list).
     * @return A new generator that returns lists.
     */
    @Beta
    @SuppressWarnings("unchecked")
    public static <A, B> NewInstance<A, B> LIST() {
        return new NewInstance<A, B>((Class<? extends B>) ArrayList.class);
    }
    
    
    /**
     * Constructs a NewInstance object. 
     * 
     * @param clazz
     */
    public NewInstance(Class<? extends V> clazz) {
        this.clazz = clazz;
    }
    
    @Override
    public V f(K x) {
        try {
            return this.clazz.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        
        return null;
    }
}
