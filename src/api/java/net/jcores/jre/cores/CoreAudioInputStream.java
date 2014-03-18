/*
 * CoreAudioInputStream
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
package net.jcores.jre.cores;

import javax.sound.sampled.AudioInputStream;

import net.jcores.jre.CommonCore;
import net.jcores.jre.interfaces.functions.F1;
import net.jcores.jre.options.Option;
import net.jcores.jre.utils.internal.Options;
import net.jcores.jre.utils.internal.Sound;

/**
 * Provides functionality like <code>play()</code> to {@link AudioInputStream} objects. For example, 
 * to play an audio file, write:<br/><br/>
 * 
 * <code>$("sound.wav").file().audio().play()</code><br/>
 * <br/>
 * 
 * <b>Important note: See {@link CoreInputStream} regarding <i>consuming</i> methods.</b>
 * 
 * @author Ralf Biedert
 * @since 1.0
 */
public class CoreAudioInputStream extends CoreObject<AudioInputStream> {

    /** Used for serialization */
    private static final long serialVersionUID = -7643964446329787050L;

    /**
     * Creates an AudioInputStream core.
     * 
     * @param supercore The common core.
     * @param objects The strings to wrap.
     */
    public CoreAudioInputStream(CommonCore supercore, AudioInputStream... objects) {
        super(supercore, objects);
    }

    /**
     * Plays all enclosed audio streams on the standard sound device.<br/>
     * <br/>
     * 
     * Examples:
     * <ul>
     * <li><code>$("sound.wav").file().audio().play()</code> - Plays the file <code>sound.wav</code>.</li>
     * </ul>
     * 
     * Single-threaded. Consuming.<br/>
     * <br/>
     * 
     * @param options The default options supported. 
     * @return Return <code>this</code>.
     */
    public CoreAudioInputStream play(final Option... options) {
        final Options $ = Options.$(this.commonCore, options);
        
        forEach(new F1<AudioInputStream, Void>() {
            @Override
            public Void f(AudioInputStream x) {
                Sound.playSound(x, $);
                return null;
            }
        }, options);

        return this;
    }
}
