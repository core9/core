/*
 * StringUtils.java
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
package net.jcores.jre.utils.internal;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Ralf Biedert
 */
public class Strings {

    /**
     * @param line
     * @return .
     */
    public static String[] parseExec(String line) {
        final List<String> rval = new ArrayList<String>();

        int a = -1;
        int b = -1;
        boolean inQuoteMode = false;
        char c = ' ';

        for (int i = 0; i < line.length(); i++) {
            c = line.charAt(i);

            // Check for space.
            if (c == ' ') {
                // If we don't have anything to do, ignore the space and continue;
                if (a < 0) continue;

                // If we have a start, but are in quote mode, just go ahead
                if (inQuoteMode) continue;

                // In all other cases (we have a and are not in quote mode), this is the end
                b = i - 1;

                rval.add(line.substring(a, b + 1));
                a = -1;
                b = -1;
                continue;
            }

            // Check for quote
            if (c == '\'') {
                // If this was prefixed with an \ we don't do anything
                if (i > 0 && line.charAt(i - 1) == '\\') continue;

                // If we are not in quote mode, enter it
                if (!inQuoteMode) {
                    inQuoteMode = true;

                    // In that case, we have to create a string out of the last element
                    if (a >= 0) {
                        b = i - 1;
                        rval.add(line.substring(a, b + 1));
                        a = -1;
                        b = -1;
                    }

                    // Set a
                    a = i + 1;
                    continue;
                }

                // When we were in quote mode, we leave that now and append the last thing we had
                if (inQuoteMode) {
                    inQuoteMode = false;

                    b = i - 1;
                    rval.add(line.substring(a, b + 1).replaceAll("\\\\'", "'"));
                    a = -1;
                    b = -1;
                    continue;
                }
            }
            
            // In case we had no a yet ...
            if(a < 0) {
                a = i;
                continue;
            }
        }

        // In case we terminate the loop with an open a, we close that
        if (a >= 0 && c != '\'' && c != ' ') {
            rval.add(line.substring(a, line.length()));
            a = -1;
            b = -1;
        }

        return rval.toArray(new String[0]);
    }
}
