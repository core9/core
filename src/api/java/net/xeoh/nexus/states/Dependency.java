/*
 * Dependency.java
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
package net.xeoh.nexus.states;

import java.util.Collection;
import java.util.LinkedList;

import net.xeoh.nexus.Service;

/**
 * Reflects a dependency a service has.
 * 
 * @author Ralf Biedert
 * @since 1.0
 */
public abstract class Dependency extends State {
    
    /**
     * Creates a new dependency.
     * 
     * @param ownerID
     */
    protected Dependency(String ownerID) {
        super(ownerID);
    }
    
    /**
     * Creates a dependency check for a given service
     * class type.
     * 
     * @param ownerID 
     * 
     * @since 1.0
     * @param clazz The class required.
     * @return A new class dependency.
     */
    public static Dependency CLASS(String ownerID, final Class<?> clazz) {
        return new Dependency(ownerID) {
            @Override
            protected boolean check(Service service) {
                return false;
            }
        };
    }

    /**
     * Returns all {@link Service} objects which would fulfill the dependency.
     * 
     * @param toCheck The plugins to check.
     * @since 1.0
     * @return The services that satisfies the dependency, or an empty collection
     * if none does.
     */
    public Collection<Service> resolve(Collection<Service> toCheck) {
        final LinkedList<Service> rval = new LinkedList<Service>();
        for (Service service : toCheck) {
            if (check(service)) rval.add(service);
        }
        return rval;
    }

    /**
     * The dependency should return true if the service would fulfull the dependency, or
     * false if not.
     * 
     * @since 1.0
     * @param service The service to test.
     * @return True if it fullfills, false if not.
     */
    protected abstract boolean check(Service service);
}
