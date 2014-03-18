/*
 * AbstractService.java
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

import net.xeoh.nexus.states.StateManager;

/**
 * The abstract implementation for the {@link Service} interface.
 * 
 * @author Ralf Biedert
 * @param <T> The type of the service.
 * @since 1.0
 * @see InternalService
 */
public abstract class AbstractService<T> implements Service {
    /** The actual service object */
    protected T object;

    /** Our state manager */
    protected StateManager stateManager = new StateManager();

    /**
     * Constructs an abstract service object with the given service.
     * 
     * @param object The object that provides service.
     */
    public AbstractService(T object) {
        this.object = object;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.jcores.kernel.Service#getService()
     */
    public T getService() {
        return this.object;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.xeoh.nexus.Service#getState()
     */
    @Override
    public StateManager getStates() {
        return this.stateManager;
    }
}
