/*
 * AnnotationProcessor.java
 * 
 * Copyright (c) 2011, Ralf Biedert, DFKI. All rights reserved.
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
package net.xeoh.nexus;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;

/**
 * Abstract base class of processors that deal with annotations.
 * 
 * @author Ralf Biedert
 * @since 1.0
 */
public abstract class AbstractAnnotationProcessor extends AbstractProcessor {

    /**
     * Returns all methods defined by this class and makes sure they are accessible.
     * 
     * @param clazz The class to consider. 
     * @since 1.0
     * @return A list of all methods we care fore.
     */
    public static Collection<Method> allMethods(Class<?> clazz) {
        final Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            method.setAccessible(true);
        }
        return Arrays.asList(methods);
    }
    
    /**
     * Returns all methods that are tagged with a given annotation.
     * 
     * @since 1.0
     * @param methods The methods to scan. 
     * @param annotation The annotation to search for. 
     * @return A collection of methods that implement the given annotation.
     */
    @SuppressWarnings("unused")
    public static Collection<Method> findMethodsFor(Collection<Method> methods, Annotation annotation) {
        final Collection<Method> rval = new LinkedList<Method>();

        for (Method method : rval) {
            //method.getA
        }
        
        
        return rval;
    }
}
