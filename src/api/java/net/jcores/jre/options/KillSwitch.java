/*
 * KillSwitch.java
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
package net.jcores.jre.options;

import static net.jcores.jre.CoreKeeper.$;

import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

import net.jcores.jre.interfaces.functions.F0;


/**
 * When the kill switch is activated the asynchronous operation to which
 * it is passed will be terminated. 
 * 
 * @author Ralf Biedert
 */
public class KillSwitch extends Option {
    /**
     * Constructs a new {@link KillSwitch}.
     * 
     * @since 1.0
     * @return The new {@link KillSwitch}.
     */
    public static final KillSwitch NEW() {
        return new KillSwitch();
    }

    /**
     * Constructs a timed kill switch that will automatically trigger after the 
     * given delay.
     * 
     * @since 1.0
     * @param delay The delay in ms.
     * @return A running {@link KillSwitch}.
     */
    public static final KillSwitch TIMED(long delay) {
        final KillSwitch killSwitch = new KillSwitch();
        
        $.sys.oneTime(new F0() {
            @Override
            public void f() {
                killSwitch.terminate();
            }
        }, delay);
        
        return killSwitch;
    }

    
    /** Futures we might want to kill */
    final Collection<Future<?>> futures = new LinkedList<Future<?>>();
    
    /** If we have been terminated yet */
    final AtomicBoolean terminated = new AtomicBoolean(false);
    
    /** There must only be one instance */
    private KillSwitch() {}
    
    /** Terminates the process this kill switch was bound to */
    public void terminate() {
        this.terminated.set(true);
        
        for (Future<?> f : this.futures) {
            f.cancel(true);
        }
    }

    /**
     * Registers a future to be killed. 
     * 
     * @since 1.0
     * @param submit The future to kill.
     */
    public void register(Future<?> submit) {
        this.futures.add(submit);
    }
    
    
    /**
     * Returns if this switch has been activated or not.
     * 
     * @since 1.0
     * @return True if it has.
     */
    public boolean terminated() {
        return this.terminated.get();
    }
}
