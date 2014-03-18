/*
 * JCoresException.java
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
package net.jcores.jre.utils.errorhandling;


/**
 * An exception that can be thrown upon an error when the caller 
 * requests it. <br/><br/> 
 * 
 * <b>Internal note:</b> A {@link JCoresException} should never be caught 
 * internally (i.e., within jCores) but should reach the user's 
 * code. Thus if jCores (or an extension) internally calls jCores and 
 * catches an exception since it tries to deal with errors on its own, 
 * this exception should be re-trown nonetheless.  
 * 
 * @author Ralf Biedert
 * @since 1.0
 */
public class JCoresException extends RuntimeException {
    /** */
    private static final long serialVersionUID = 8306755748035816666L;
    
    /** The occured failure */
    private Failure failure;
    
    /**
     * Constructs a new exception.
     * 
     * @param failure
     */
    public JCoresException(Failure failure) {
        this.failure = failure;
    }
    
    /**
     * Returns the failure associated with this Exception.
     * 
     * @since 1.0
     * @return The {@link Failure}.
     */
    public Failure getFailure() {
        return this.failure;
    }
}
