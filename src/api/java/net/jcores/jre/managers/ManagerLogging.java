/*
 * ClassManager.java
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
package net.jcores.jre.managers;

import java.util.logging.Level;
import java.util.logging.Logger;

import net.jcores.jre.CommonCore;
import net.jcores.jre.interfaces.internal.logging.LoggingHandler;


/**
 * Manager for logging, addressing Issue #9. 
 * 
 * @since 1.0
 * @author Ralf Biedert
 */
public class ManagerLogging extends Manager {
    /** The current logging handler */
    private volatile LoggingHandler handler;
    
    public ManagerLogging() {
        handler(new LoggingHandler() {
            /** Common logger */
            private final Logger logger = Logger.getLogger(CommonCore.class.getName());

            @Override
            public void log(String log, Level level) {
                this.logger.log(level, log);
            }
        });
    }

    /**
     * Sets the current handler
     * 
     * @param loggingHandler The handler to set.
     */
    public void handler(LoggingHandler loggingHandler) {
        this.handler = loggingHandler;
    }
    
    /**
     * Returns the current handler
     * 
     * @return The current handler
     */
    public LoggingHandler handler() {
        return this.handler;
    }
}
