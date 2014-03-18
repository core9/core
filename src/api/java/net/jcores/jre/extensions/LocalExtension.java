/*
 * AbstractSingletonExtension.java
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
package net.jcores.jre.extensions;

import net.jcores.jre.CommonCore;
import net.jcores.jre.cores.CoreNumber;
import net.jcores.jre.cores.CoreObject;
import net.jcores.jre.cores.adapter.AbstractAdapter;
import net.jcores.jre.cores.adapter.EmptyAdapter;

/**
 * Base class when creating a wrapping extension, i.e., one
 * that is called as <code>$(objects).as(MyExtension.class).f()</code>. 
 * 
 * <br/><br/>
 * Note that in 
 * contrast to the {@link GlobalExtension} you are not forced to extend this class
 * in order to create an extension (you could extend a more specific core like 
 * {@link CoreNumber} directly). In that case, make sure that you specify a constructor 
 * which accepts a {@link CommonCore} and an {@link AbstractAdapter} anyways.
 * 
 * @author Ralf Biedert
 * @param <T> The type of the object to wrap.
 * @since 1.0
 */
public abstract class LocalExtension<T> extends CoreObject<T> {
    /** */
    private static final long serialVersionUID = 3624295339294079716L;
    
    
    /** Must not be called. */
    private LocalExtension() {
        super(null, new EmptyAdapter<T>());
        throw new IllegalStateException();
    }

    /**
     * Constructor that should be invoked by your class.
     * 
     * @param commonCore The commonCore passed to your object.
     * @param adapter The adapter containing the wrapped objects.
     */
    public LocalExtension(CommonCore commonCore, AbstractAdapter<T> adapter) {
        super(commonCore, adapter);
    }
}
