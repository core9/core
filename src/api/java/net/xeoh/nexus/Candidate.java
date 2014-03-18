/*
 * Candidate.java
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

/**
 * A plugin candidate which can be located by a locator.
 * 
 * @author Ralf Biedert
 * @since 1.0
 */
public interface Candidate {
    /**
     * Returns the locator that can locate this candidate. 
     * 
     * @since 1.0
     * @return The {@link Abstract2StageLocator} locator that can locate this candidate.
     */
    public Abstract2StageLocator getLocator();

    
    /**
     * Returns the class name of this candidate. In contrast 
     * to the method <code>getCandidateClass()</code> this method does not
     * require the classes bytecode to be read and is much more environmentally 
     * friendly. 
     * 
     * @return The class name of the candidate.
     * @since 1.0
     */
    public String getCandidateClassName();
    
    
    /**
     * Returns the class of this candidate for inspection. In contrast to 
     * <code>getCandidateClassName</code> this method is heavyweight. 
     * 
     * @return The class of the candidate.
     * @since 1.0
     */
    public Class<?> getCandidateClass();
}
