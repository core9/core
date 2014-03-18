/*
 * StateManager.java
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

/**
 * Manages the states of a service.
 * 
 * @author Ralf Biedert
 * @since 1.0
 */
public class StateManager {
    /** All our states */
    final Collection<State> states = new LinkedList<State>();

    /**
     * Returns all active states.
     * 
     * @since 1.0
     * @return All states
     */
    public Collection<State> getStates() {
        return this.states;
    }

    /**
     * Returns all active states which have been created by <code>ownerID</code>.
     * 
     * @param ownerID The ownerID for which to search.
     * 
     * @since 1.0
     * @return All states
     */
    public Collection<State> getStatesBy(String ownerID) {
        final Collection<State> rval = new ArrayList<State>();
        
        for (State state : this.states) {
            if (state.getOwnerID().equals(ownerID)) rval.add(state);
        }

        return this.states;
    }

    /**
     * Adds the given state to our state information.
     * 
     * @since 1.0
     * @param state The state to add.
     */
    public void addState(State state) {
        this.states.add(state);
    }

    /**
     * Removes the given state from our state information.
     * 
     * @since 1.0
     * @param state The state to remove.
     */
    public void removeState(State state) {
        this.states.remove(state);
    }
}
