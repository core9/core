/*
 * SoundUtils.java
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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * @author Ralf Biedert
 */
public class Sound {

    private final static int EXTERNAL_BUFFER_SIZE = 524288; // 128Kb
    
    /**
     * Plays the given audio input stream.
     * 
     * @param audioInputStream
     * @param o Options object for failure reporting. 
     */
    public static void playSound(AudioInputStream audioInputStream, Options o) {
        final AudioFormat format = audioInputStream.getFormat();
        SourceDataLine auline = null;
        final DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);

        try {
            auline = (SourceDataLine) AudioSystem.getLine(info);
            auline.open(format);
        } catch (final LineUnavailableException e) {
            o.failure(null, e, "playsound:noline", "Error opening output line.");
            return;
        } catch (final Exception e) {
            o.failure(null, e, "playsound:unknown", "General error opening output line.");
            return;
        }

     
        auline.start();
        int nBytesRead = 0;
        final byte[] abData = new byte[EXTERNAL_BUFFER_SIZE];

        try {
            while (nBytesRead != -1) {
                nBytesRead = audioInputStream.read(abData, 0, abData.length);
                if (nBytesRead >= 0) {
                    auline.write(abData, 0, nBytesRead);
                }
            }
        } catch (final IOException e) {
            o.failure(audioInputStream, e, "playsound:read", "Error getting sound data.");
            return;
        } finally {
            auline.drain();
            auline.close();
        }
    }
    
    
    public static AudioInputStream getStream(File file) {
        try {
            return getStream(new BufferedInputStream(new FileInputStream(file)));
        } catch (final IOException e1) {
            e1.printStackTrace();
            return null;
        }
    }
    
    
    public static AudioInputStream getStream(InputStream file) {
        try {
            return AudioSystem.getAudioInputStream(new BufferedInputStream(file));
        } catch (final UnsupportedAudioFileException e1) {
            e1.printStackTrace();
            return null;
        } catch (final IOException e1) {
            e1.printStackTrace();
            return null;
        }
    }
}
