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

import java.io.File;
import java.util.List;

import javax.swing.JComponent;

import net.jcores.jre.CommonCore;
import net.jcores.jre.interfaces.functions.F1;
import net.jcores.jre.options.Option;
import net.jcores.jre.utils.internal.structures.SimpleTransferHandler;
import net.jcores.jre.utils.internal.wrapper.Wrapper;

/**
 * Extension of the {@link CoreComponent} to provide helpers for Swing, like <code>onDrop()</code>. For example, 
 * to add a file-drop-listener to some panel, write:<br/><br/>
 * 
 * <code>$(panel).onDrop(handler, Option.DROPTYPE_FILES)</code>
 * 
 * @author Ralf Biedert
 * 
 * @since 1.0
 */
public class CoreJComponent extends CoreComponent {

    /** Used for serialization */
    private static final long serialVersionUID = -6859431347201006730L;

    /**
     * Creates an Component core.
     * 
     * @param supercore The common core.
     * @param objects The Component to wrap.
     */
    public CoreJComponent(CommonCore supercore, JComponent... objects) {
        super(supercore, objects);
    }

    /**
     * Sets the onDrop handler for the given JCompontents.<br/>
     * <br/>
     * 
     * Examples:
     * <ul>
     * <li><code>$(panel).onDrop(handler, Option.DROPTYPE_FILES)</code> - Makes the panel accept file drops from the operating system.</li>
     * </ul> 
     * 
     * Single-threaded.<br/>
     * <br/>
     * 
     * @param handler The handler that will be called with new events.
     * @param options Options to listen to.
     * 
     * @return This object again.
     */
    public CoreJComponent onDrop(final F1<CoreObject<Object>, Void> handler, final Option... options) {
        final CommonCore cc = this.commonCore;

        for (int i = 0; i < size(); i++) {
            final JComponent component = (JComponent) get(i);
            if (component == null) continue;

            component.setTransferHandler(new SimpleTransferHandler(options) {
                /** */
                private static final long serialVersionUID = 1L;
                @SuppressWarnings({ "unchecked", "rawtypes" })
                @Override
                public void files(List<File> files) {
                    handler.f(new CoreObject(cc, (Object[]) Wrapper.convert(files, File.class)));
                }
            });
        }
        
        return this;
    }
}
