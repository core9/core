/*
 * OnFailure.java
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
package net.jcores.jre.options;

import net.jcores.jre.utils.errorhandling.Failure;
import net.jcores.jre.utils.errorhandling.JCoresException;


/**
 * Can be passed to various functions to register a callback when 
 * something went wrong and specifies how the function should react.
 * By default only an internal report is filed.
 * 
 * @author Ralf Biedert
 * @since 1.0
 */
public class OnFailure extends DefaultOption {
    /** Print something to the console when a failure occured. */
    public static OnFailure PRINT = new OnFailure(new OnFailure.Listener() {
        @Override
        public void onFailure(Object object, Exception cause, String code, String details) {
            System.err.println(details);
        }
    });

    /** Throws a new {@link JCoresException} when something went wrong. */
    public static OnFailure THROW = new OnFailure(new OnFailure.Listener() {
        @Override
        public void onFailure(Object object, Exception cause, String code, String details) {
            final Failure failure = new Failure();
            failure.object = object;
            failure.cause = cause;
            failure.code = code;
            failure.details = details;
            throw new JCoresException(failure);
        }
    });

    
    /**
     * Call the listener when a failure occured.
     * 
     * @since 1.0
     * @param listener The listener to call.
     * @return The OnFailure object.
     */
    public static OnFailure DO(Listener listener) {
        return new OnFailure(listener);
    }

    /** The listener */
    private Listener listener;

    /**
     * Registers an onFailure listener.
     * 
     * @param listener The listener to register.
     */
    private OnFailure(Listener listener) {
        this.listener = listener;
    }
    
    /**
     * Returns the failure listener.
     * 
     * @return The Failure listener.
     */ 
    public Listener getListener() {
        return this.listener;
    }

    /**
     * The failure listener interface.
     * 
     * @author Ralf Biedert
     * @since 1.0
     */
    public interface Listener {
        /**
         * Called when an exception was raised for the given object.
         * 
         * @since 1.0
         * @param object The object for which the failure occured.
         * @param cause The exception that was thrown (may be null).
         * @param code The code of the failue.
         * @param details A human readable debug string.
         */
        public void onFailure(Object object, Exception cause, String code, String details);
    }
}
