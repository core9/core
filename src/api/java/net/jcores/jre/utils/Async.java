/*
 * Async.java
 * 
 * Copyright (c) 2011, Ralf Biedert, DFKI. All rights reserved.
 * 
 * Redistributi
import net.jcores.jre.interfaces.functions.F1E;

import java.util.LinkedList;

import java.util.ArrayList;
on and use in source and binary forms, with or without modification, are
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

import static net.jcores.jre.CoreKeeper.$;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingQueue;

import net.jcores.jre.CommonCore;
import net.jcores.jre.cores.CoreObject;
import net.jcores.jre.cores.commons.CommonSys;
import net.jcores.jre.interfaces.functions.F0;
import net.jcores.jre.interfaces.functions.F1;
import net.jcores.jre.options.KillSwitch;
import net.jcores.jre.options.MessageType;
import net.jcores.jre.options.Option;
import net.jcores.jre.utils.internal.Options;

/**
 * Reflects the results of an asynchronous core operations <code>$.async()</code> and <code>$(objects).async()</code>. 
 * 
 * @author Ralf Biedert
 * @param <T> The type this async object wraps.
 * @since 1.0
 */
public class Async<T> {
    /**
     * An entry for the queue feeding the {@link Async} object.
     * 
     * @author Ralf Biedert
     * @param <T> the type of object.
     * @since 1.0
     * 
     */
    public static class QEntry<T> {
        /** The payload */
        protected T object;

        /**
         * Returns the object payload.
         * 
         * @return the object The object payload.
         */
        public T getObject() {
            return this.object;
        }

        protected QEntry() {}
    }

    /** Represents the end of queue, after this nothing will be sent anymore */
    protected static class EOQ extends QEntry<Object> {}

    /** The actual EOQ object */
    protected final static EOQ EOQ = new EOQ();

    /**
     * The queue feeding the {@link Async} object.
     * 
     * @author Ralf Biedert
     * @param <T> the type of object.
     * @since 1.0
     */
    public static class Queue<T> extends LinkedBlockingQueue<QEntry<T>> {
        /** */
        private static final long serialVersionUID = -5727467218361481807L;

        protected Queue() {}

        /**
         * Closes the queue and signals the receiver that nothing more will be added.
         * 
         * @since 1.0
         */
        @SuppressWarnings("unchecked")
        public void close() {
            add((net.jcores.jre.utils.Async.QEntry<T>) EOQ);
        }
    }

    /**
     * Constructs a communications queue.
     * 
     * @since 1.0
     * @return Returns the queue.
     */
    public static final <T> Queue<T> Queue() {
        return new Queue<T>();
    }

    /**
     * Creates a queue entry for the given object.
     * 
     * @since 1.0
     * @param toWrap The object to wrap.
     * @return The generated queue entry.
     */
    public static final <T> QEntry<T> QEntry(T toWrap) {
        QEntry<T> qe = new QEntry<T>();
        qe.object = toWrap;
        return qe;
    }

    /** The queue with the incoming objects. */
    Queue<T> queue;

    /** If we already received a EOQ event */
    boolean closed = false;

    /** Our common core */
    private CommonCore commonCore;

    /**
     * Creates an async object which receives its data through the given queue. It will
     * expect more events to arrive until the queue has been closed.
     * 
     * @param commonCore The common core. 
     * @param queue
     */
    public Async(CommonCore commonCore, Queue<T> queue) {
        this.commonCore = commonCore;
        this.queue = queue;
    }
    
    
    /**
     * Waits until the async has finished and returns all 
     * remaining objects.
     * 
     * @since 1.0
     * @return A {@link CoreObject} with the result.
     */
    public CoreObject<T> await() {
        final Collection<T> result = $.list();
        
        while (true) {
            try {
                // Get the elements, and wait.
                final QEntry<T> take = Async.this.queue.take();
                if(take == EOQ) break;
                
                // And feed them to the listener
                result.add(take.object);
                
            } catch (InterruptedException e) {
                $.report(MessageType.EXCEPTION, "Unexpected Interrupt while waiting at Async.await(). Returning with what we have.");
                break;
            }
        }
        
        return new CoreObject<T>($, result);
    }

    /**
     * Checks if this async object is still active or not.
     * 
     * @since 1.0
     * @return True if new events might arrive, false if not.
     */
    public boolean active() {
        return !this.closed;
    }

    /**
     * Returns the objects which are currently available and have not been
     * collected or removed already otherwise.
     * 
     * @since 1.0
     * @return A {@link CoreObject} with the available elements.
     */
    public CoreObject<T> available() {
        return new CoreObject<T>($, collect());
    }

    /**
     * Registers a listener that will be called when a new object from the queue
     * will be available.
     * 
     * @param f The function to call when an element finished.
     * @param options Supports all options {@link CommonSys}.<code>oneTime()</code> understands (esp. {@link KillSwitch}
     * ).
     * @since 1.0
     * @return This async object.
     */
    public Async<T> onNext(final F1<T, Void> f, Option... options) {
        final Options options$ = Options.$(this.commonCore, options);
        final KillSwitch killswitch = options$.killswitch();

        $.sys.oneTime(new F0() {
            @Override
            public void f() {
                while (true) {
                    try {
                        // Get the elements, and wait.
                        final QEntry<T> take = Async.this.queue.take();
                        if(take == EOQ) return;
                        
                        // And feed them to the listener
                        try {
                            f.f(take.object);
                        } catch (Exception e) {
                            $.report(MessageType.EXCEPTION, "Function f() passed to Async.onNext() threw an exception " + e.getMessage());                            
                        }
                        
                    } catch (InterruptedException e) {
                        if (killswitch != null && killswitch.terminated()) return;
                        $.report(MessageType.EXCEPTION, "Unexpected Interrupt while waiting at Async.onNext(). Terminating handler");
                        return;
                    }
                }
            }
        }, 0, options);

        return this;
    }

    /**
     * Collects all new elements from the other side of the queue that are
     * already there.
     * 
     * @since 1.0
     * @return A collection with all available elements.
     */
    protected Collection<T> collect() {
        if (this.closed) return new ArrayList<T>();

        final Collection<T> rval = new LinkedList<T>();
        QEntry<T> poll = this.queue.poll();

        while (poll != null) {
            // Check if this was the end of the queue.
            if (poll == EOQ) {
                this.closed = true;
                return rval;
            }

            final T object = poll.object;
            if (object == null) continue;

            // Add the new object
            rval.add(object);
            
            // And get the next one
            poll = this.queue.poll();
        }

        return rval;
    }
}
