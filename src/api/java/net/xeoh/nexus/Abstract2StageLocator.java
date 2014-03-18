/*
 * Abstract2StageLocator.java
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

import java.util.Collection;

import net.xeoh.nexus.options.Option;

import org.codehaus.classworlds.ClassRealm;
import org.codehaus.classworlds.ClassWorld;
import org.codehaus.classworlds.DuplicateRealmException;

/**
 * A 2StageLocator is a locator that in a first stage is able to list the
 * plugin {@link Candidate} objects it would locate as an actual {@link Service}.
 * 
 * @author Ralf Biedert
 * @since 1.0
 */
public abstract class Abstract2StageLocator extends AbstractLocator {
    protected final static String DEFAULT_REALM = "realm:default";

    /** Our own classworld object */
    protected ClassWorld classWorld;

    /** Our default realm */
    protected ClassRealm realm;

    /** */
    protected Abstract2StageLocator(Option ... options) {
        try {
            this.classWorld = new ClassWorld();
            this.realm = this.classWorld.newRealm(DEFAULT_REALM);
        } catch (DuplicateRealmException e) {
            //
        }
    }

    /**
     * Returns a list of {@link Candidate} objects this locator would
     * locate as {@link Service} objects when <code>locate()</code> is
     * being called.
     * 
     * @since 1.0
     * @return A list of candidates.
     */
    public abstract Collection<Candidate> candidates();

    /**
     * Only locates the given candidates.
     * 
     * @since 1.0
     * @param candidates The candidates to locate.
     * @return A list of located {@link Service} objects.
     */
    public abstract Collection<Service> locate(Collection<Candidate> candidates);
}
