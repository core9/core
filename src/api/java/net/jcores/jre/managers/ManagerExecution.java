/*
 * ManagerExecution.java
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
package net.jcores.jre.managers;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import net.jcores.jre.interfaces.functions.F0;
import net.jcores.jre.utils.internal.structures.ProfileInformation;

/**
 * Manages the execution of threads.
 * 
 * @since 1.0
 * @author Ralf Biedert
 */
public class ManagerExecution extends Manager {
    /** The executor with which we execute tasks. */
    ExecutorService executor;
    
    /** Our profile information so we know how long new tasks took */
    ProfileInformation profile;

    /** Creates our execution manager. */
    public ManagerExecution() {
        // Create an executor that does not prevent us from quitting.
        this.executor = Executors.newCachedThreadPool(new ThreadFactory() {
            public Thread newThread(Runnable r) {
                final Thread t = new Thread(r);
                t.setDaemon(true);
                return t;
            }
        });
        
        this.profile = profile();
    }

    
    /**
     * Returns our executor.
     * 
     * @return Our executor.
     */
    public ExecutorService getExecutor() {
        return this.executor;
    }
    
    /**
     * Benchmark the VM. Dirty, but should give us some rough estimates
     * 
     * @return
     */
    private ProfileInformation profile() {
        final ProfileInformation p = new ProfileInformation();
        final int RUNS = 10;
        final int N = 5;

        // Measure how long it takes to fork a thread and to wait for it again. We
        // test 10 times and take the average of the last 5 runs.
        long times[] = new long[RUNS];
        for (int i = 0; i < RUNS; i++) {
            times[i] = measure(new F0() {
                @Override
                public void f() {
                    final CyclicBarrier barrier = new CyclicBarrier(2);

                    // Execute the given task in the executor.
                    ManagerExecution.this.executor.submit(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                barrier.await();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            } catch (BrokenBarrierException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                    try {
                        barrier.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (BrokenBarrierException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        // Now take the average
        for (int i = RUNS - N; i < times.length; i++) {
            p.forkTime += times[i];
        }

        p.forkTime /= N;
        p.numCPUs = Runtime.getRuntime().availableProcessors();

        return p;
    }


    /**
     * Measures how long the execution of the given function took. The result 
     * will be returned in nanoseconds.
     * 
     * @param f The function to execute.
     * @return The elapsed time in nanoseconds.
     */
    private long measure(F0 f) {
        final long start = System.nanoTime();
        f.f();
        final long end = System.nanoTime();
        return end - start;
    }
    
    /**
     * Returns the profile information we generated.
     * 
     * @return The profile information.
     */
    public ProfileInformation getProfile() {
        return this.profile;
    }
}
