/*
 * CoreJComponent
 * 
 * Copyright (c) 2010, Ralf Biedert All rights reserved.
 * 
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

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import net.jcores.jre.CommonCore;
import net.jcores.jre.interfaces.java.KeyStroke;

/**
 * Base class for {@link Component} related operations (Swing & AWT). For example, 
 * to assign a key listener to some component, write:<br/><br/>
 * 
 * <code>$(panel).keypress(KeyEvent.VK_ESCAPE, handler).</code>
 * 
 * @author Ralf Biedert
 * 
 * @since 1.0
 */
public class CoreComponent extends CoreObject<Component> {

    /** Used for serialization */
    private static final long serialVersionUID = -6859431347201006730L;

    /**
     * Creates an Component core.
     * 
     * @param supercore The common core.
     * @param objects The Component to wrap.
     */
    public CoreComponent(CommonCore supercore, Component... objects) {
        super(supercore, objects);
    }

    /**
     * Adds a key-press handler to the given components.<br/>
     * <br/>
     * 
     *  
     * Examples:
     * <ul>
     * <li><code>$(component).keypress(KeyEvent.VK_ESCAPE, handler)</code> - Adds a keypress listener to the given component that will fire on ESCAPE.</li>
     * </ul>
     * 
     * Single-threaded.<br/>
     * <br/>
     * 
     * @param keyEvent The event to listen for.
     * @param handler The handler to call when the event was observed.
     * 
     * @return This object again.
     */
    public CoreComponent keypress(final int keyEvent, final KeyStroke handler) {
        for (int i = 0; i < size(); i++) {
            final Component component = get(i);
            if (component == null) continue;

            component.addKeyListener(new KeyListener() {

                @Override
                public void keyTyped(KeyEvent e) {
                      //
                }
                
                @Override
                public void keyReleased(KeyEvent e) {
                      //
                  }

                @Override
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() != keyEvent) return;
                    handler.keystroke(e);
                }
            });
        }

        return this;
    }
}
