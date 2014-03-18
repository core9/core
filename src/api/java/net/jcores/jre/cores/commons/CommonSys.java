/*
 * CommonFile.java
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
package net.jcores.jre.cores.commons;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.Future;
import java.util.prefs.Preferences;

import net.jcores.jre.CommonCore;
import net.jcores.jre.annotations.SupportsOption;
import net.jcores.jre.interfaces.functions.F0;
import net.jcores.jre.options.ID;
import net.jcores.jre.options.KillSwitch;
import net.jcores.jre.options.Option;
import net.jcores.jre.utils.internal.Options;

/**
 * System functions like background timers, tempdirs or unique IDs.
 * 
 * @author Ralf Biedert
 * @since 1.0
 * 
 */
public class CommonSys extends CommonNamespace {

    /**
     * Creates a common file object.
     * 
     * @param commonCore
     */
    public CommonSys(CommonCore commonCore) {
        super(commonCore);
    }

    /**
     * Returns a temporary file.
     * 
     * @param options The default options supported. 
     * @return A File object for a temporary file.
     */
    public File tempfile(Option... options) {
        try {
            return File.createTempFile("jcores.", ".tmp");
        } catch (IOException e) {
            final Options options$ = Options.$(this.commonCore, options);
            options$.failure(null, e, "tempfile:create", "Unable to create temp file.");
        }

        return new File("/tmp/jcores.failedtmp." + System.nanoTime() + ".tmp");
    }

    /**
     * Returns a temporary directory.
     * 
     * @param options The default options supported. 
     * @return A File object for a temporary directory.
     */
    public File tempdir(Option... options) {
        final File ffile = new File(tempfile(options).getAbsoluteFile() + ".dir/");
        
        // Report if we failed
        if (!ffile.mkdirs()) {
            final Options options$ = Options.$(this.commonCore, options);
            options$.failure(null, null, "tempdir:create", "Unable to create temp dir.");
        }
        
        return ffile;
    }

    /**
     * Executes the given function with the given delay (delay in the
     * sense of wait time between two invocations) indefinitely.
     * 
     * @param f0 The function to execute
     * @param delay The delay at which the function will be executed.
     * @param options May accept a {@link KillSwitch}.
     */
    @SupportsOption(options = { KillSwitch.class })
    public void manyTimes(final F0 f0, final long delay, final Option... options) {
        final Options options$ = Options.$(this.commonCore, options);
        final KillSwitch killswitch = options$.killswitch();
        final Future<?> submit = this.commonCore.executor().getExecutor().submit(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        f0.f();
                        sleep(delay, options);
                    } catch (Exception e) {
                        options$.failure(f0, e, "manytimes:run", "Exception while executing f().");
                    }

                    // Check if we should terminate
                    if (killswitch != null && killswitch.terminated()) return;
                }
            }
        });

        // Register the future
        if (killswitch != null) killswitch.register(submit);
    }

    /**
     * Executes the given function once after the given delay (delay in the
     * sense of time until the first execution happens).
     * 
     * @param f0 The function to execute
     * @param delay The delay after which the function will be executed.
     * @param options May accept a {@link KillSwitch}.
     */
    @SupportsOption(options = { KillSwitch.class })
    public void oneTime(final F0 f0, final long delay, final Option... options) {
        final Options options$ = Options.$(this.commonCore, options);
        final KillSwitch killswitch = options$.killswitch();
        final Future<?> submit = this.commonCore.executor().getExecutor().submit(new Runnable() {
            @Override
            public void run() {
                try {
                    sleep(delay, options);

                    // Check if we should terminate
                    if (killswitch != null && killswitch.terminated()) return;

                    f0.f();
                } catch (Exception e) {
                    options$.failure(f0, e, "onetime:run", "Exception while executing f().");
                }
            }
        });

        // Register the future
        if (killswitch != null) killswitch.register(submit);
    }

    /**
     * Puts the current thread to sleep for some time, without the need for any try/catch block.
     * 
     * @param time The time to sleep.
     * @param options The default options supported.
     * @return A value of <code>0</code> if the sleep was successful, or else the amount
     * of milliseconds which we woke up too early.
     */
    public long sleep(long time, Option... options) {
        final long start = System.currentTimeMillis();
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            Options.$(this.commonCore, options).failure(null, e, "sleep:interrupt", "Sleep interrupted");
            return time - (System.currentTimeMillis() - start);
        }

        return 0;
    }

    /**
     * Creates or returns an ID. 
     * 
     * @param options Accepts the {@link ID} option to specify what type of ID to return. By 
     * default a new, unique ID is returned. 
     * @return Returns an ID.
     */
    @SupportsOption(options = { ID.class })
    public String uniqueID(Option... options) {
        final ID id = Options.$(this.commonCore, options).ID();
        
        
        if(id == ID.SYSTEM || id == ID.USER) {
            try {
                // System nodes apparently fail for Lion when syncing() (see Issue #15).
                final Preferences node = id == ID.SYSTEM ? Preferences.systemNodeForPackage(getClass()) : Preferences.userNodeForPackage(getClass());

                final String rval = node.get("system.id", "UNAVAILABLE");
                if("UNAVAILABLE".equals(rval)) {
                    String newid = UUID.randomUUID().toString();
                    node.put("system.id", newid);
                    node.sync();
                    return newid;
                }
                return rval;
            } catch(Exception e) {
                Options.$(this.commonCore, options).failure(null, e, "uniqueid:create", "Error getting the ID " + id.getClass() + " (on Lion this might be a bug).");
                return "UNAVAILABLE";
            }
        }
        
        return UUID.randomUUID().toString();
    }
    
}
