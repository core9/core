/*
 * Mapper.java
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
package net.jcores.jre.utils.internal.processing;

import java.lang.reflect.Array;

import net.jcores.jre.cores.CoreObject;
import net.jcores.jre.options.Option;
import net.jcores.jre.options.Debug;
import net.jcores.jre.options.Indexer;
import net.jcores.jre.options.MapType;

/**
 * Used by the cores when calling the inner core's mapping function. You do not need this.
 * 
 * @author Ralf Biedert
 * @param <I> The in-type.
 * @param <O> The out-type.
 */
public abstract class Mapper<I, O> extends Handler<I, O> {
    /**
     * Contians the evaluated options for a mapping operation.
     * 
     * @author Ralf Biedert
     */
    public static class MapOptions {
        /** If we should print debug information */
        public final boolean debug;

        /** The target type */
        public final Class<?> type;

        /** The used indexer */
        public final Indexer indexer;

        public MapOptions(Option... options) {
            // Variables to set after we processed the options
            boolean _debug = false;
            Class<?> _type = null;
            Indexer _indexer = null;

            // Check options if we have
            for (Option option : options) {
                if (option instanceof MapType) {
                    _type = ((MapType) option).getType();
                }
                if (option instanceof Debug) {
                    _debug = true;
                }

                // In case we have a map type, get it directly
                if (option instanceof Indexer) {
                    _indexer = (Indexer) option;
                }
            }

            this.debug = _debug;
            this.type = _type;
            this.indexer = _indexer;
        }
    }

    /** Our map options */
    protected final MapOptions options;

    /**
     * @param core
     * @param options
     */
    @SuppressWarnings("unchecked")
    public Mapper(CoreObject<I> core, Option... options) {
        super(core);

        this.options = new MapOptions(options);

        // If the return type is already known, create it
        if (this.options.type != null) {
            updateReturnArray((O[]) Array.newInstance(this.options.type, core.size()));
        }
    }

    /**
     * Overwrite this method and handle element number i.
     * 
     * This method is called highly parallelized.
     * 
     * @param i
     */
    public abstract void handle(int i);

}
