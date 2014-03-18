/*
 * CoreCSV.java
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
package net.jcores.jre.cores;

import net.jcores.jre.CommonCore;
import net.jcores.jre.interfaces.functions.F1;
import net.jcores.jre.utils.CSVLine;


/**
 * Wraps {@link CSVLine} objects (as returned by a {@link CoreString}) and provides additional 
 * functions.<br/><br/>
 * 
 * @author Ralf Biedert
 * 
 * @since 1.0
 */
public class CoreCSV extends CoreObject<CSVLine> {

    /** Used for serialization */
    private static final long serialVersionUID = 7366734773387957013L;

    /**
     * Creates an {@link CSVLine} core.
     * 
     * @param supercore The common core.
     * @param objects The CSVLines to wrap.
     */
    public CoreCSV(CommonCore supercore, CSVLine... objects) {
        super(supercore, objects);
    }

    
    /**
     * Returns the <code>ith</code> column of this {@link CSVLine}s.<br/>
     * <br/>
     * 
     * Examples:
     * <ul>
     * <li><code>$(lines).column(0).number().sum()</code> - Computes the sum of the values in the first column.</li>
     * </ul> 
     * 
     * Single-threaded.<br/>
     * <br/>
     * 
     * @param i The column to select. 
     * @return A CoreString object with all strings of the first column.
     */
    public CoreString column(final int i) {
        return new CoreString(this.commonCore, map(new F1<CSVLine, String>() {
            public String f(CSVLine x) {
               return x.s(i);
            }
        }).array(String.class));
    }

}
