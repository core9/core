/*
 * Blacklist.java
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
import java.util.LinkedList;

/**
 * A blacklist lets only classes through who are not on the list.
 * 
 * @author Ralf Biedert
 * @since 1.0
 */
public class Blacklist extends AbstractCandidateFilter {

    /**
     * Constructs a new black list based on the set of filter rules.
     * 
     * @param rules The rules to apply.
     */
    public Blacklist(Collection<FilterRule> rules) {
        super(rules);
    }
    
    /**
     * Constructs a new black list based on the set of filter rules.
     * 
     * @param rule The rules to apply.
     */
    public Blacklist(FilterRule rule) {
        super(rule);
    }
    

    /*
     * (non-Javadoc)
     * 
     * @see net.xeoh.nexus.CandidateFilter#filter(java.util.Collection)
     */
    @Override
    public Collection<Candidate> filter(Collection<Candidate> input) {
        final Collection<Candidate> rval = new LinkedList<Candidate>();

        for (Candidate candidate : input) {
            boolean found = false;

            // Check all filters. If one matches, we won't accept it
            for (FilterRule rule : this.rules) {
                if (rule.matches(candidate)) {
                    found = true;
                    break;
                }
            }
            
            // Only add if nothing was found
            if(!found) rval.add(candidate);
        }

        return rval;
    }
}
