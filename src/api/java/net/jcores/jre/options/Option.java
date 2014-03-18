/*
 * Option.java
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
package net.jcores.jre.options;


/**
 * Base class for all options. Many jCores methods accept optional arguments in the 
 * form: <br/><br/>
 * 
 * <code>$().f(arg1, <b>your_options_here</b>)</code> <br/><br/>
 * 
 * In this case <code>arg1</code> is a required parameter, while options are always 
 * varargs, which means you could write each: <br/><br/>
 * 
 * <ul>
 * <li><code>$().f(arg1)</code></li>
 * <li><code>$().f(arg1<b>, optionA</b>)</code></li>
 * <li><code>$().f(arg1<b>, optionA, optionB</b>)</code></li>
 * <li><code>$().f(arg1<b>, optionA, optionB, ...</b>)</code></li>
 * </ul>
 * 
 * @since 1.0
 * @author Ralf Biedert
 */
public class Option {
    /** We don't allow for user-created options at the moment. */
    protected Option() { /* */ }
}
