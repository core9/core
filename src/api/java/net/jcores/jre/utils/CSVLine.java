/*
 * CSVLine.java
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
package net.jcores.jre.utils;

import java.util.Arrays;

import net.jcores.jre.CoreKeeper;
import net.jcores.jre.cores.CoreCSV;

/**
 * A line of a CSV file, as used by the {@link CoreCSV}. 
 * 
 * @author Ralf Biedert
 * @since 1.0
 */
public class CSVLine {
    /** */
    private String[] entries;

    /**
     * Creates a CSVLine object. 
     * 
     * @param entries
     */
    public CSVLine(String ... entries) {
        this.entries = entries;
    }
    
    /**
     * Returns the string at the given index position. 
     * 
     * @param index The index to retrive. 
     * @return The string The string at the given index.
     */
    public String s(int index) {
        return this.entries[index];
    }
    
    /**
     * Returns the string at the given index position converted 
     * as an integer. 
     * 
     * @param index The index to retrieve.
     * @return The integer value. 
     */
    public int i(int index) {
        return Integer.parseInt(s(index));
    }

    /**
     * Returns the string at the given index position converted 
     * as an Integer. 
     * 
     * @param index The index to retrieve.
     * @return The Integer value. 
     */
    public Integer I(int index) {
        return Integer.valueOf(index);
    }


    /**
     * Returns the string at the given index position converted 
     * as a double. 
     * 
     * @param index The index to retrieve.
     * @return The double value. 
     */
    public double d(int index) {
        return Double.parseDouble(s(index));
    }

    /**
     * Returns the string at the given index position converted 
     * as an Double. 
     * 
     * @param index The index to retrieve.
     * @return The Double value. 
     */
    public Double D(int index) {
        return Double.valueOf(index);
    }
    
    /**
     * Returns the size of this line (the number of entries).
     * 
     * @return The number of entries.
     */
    public int size() {
        return this.entries.length;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return CoreKeeper.$(this.entries).join(",");
    }
    
    
    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof CSVLine) {
            CSVLine other = (CSVLine) obj;
            return Arrays.deepEquals(this.entries, other.entries);
        }
        
        return false;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return Arrays.deepHashCode(this.entries);
    }
}
