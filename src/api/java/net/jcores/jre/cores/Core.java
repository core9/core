/*
 * Core.java
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

import java.io.Serializable;
import java.util.ListIterator;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicInteger;

import net.jcores.jre.CommonCore;
import net.jcores.jre.options.Option;
import net.jcores.jre.utils.internal.processing.Folder;
import net.jcores.jre.utils.internal.processing.Mapper;
import net.jcores.jre.utils.internal.structures.ProfileInformation;

/**
 * The abstract base class of all cores. Contains commonly used methods and variables. In
 * general
 * you should not need to bother with this class, as in most cases you will extend
 * CoreObject for
 * your own cores, not this class.
 * 
 * @since 1.0
 * @author Ralf Biedert
 */
public abstract class Core implements Serializable {

    /** Used for serialization */
    private static final long serialVersionUID = 2195880634253143587L;

    /** Our 'parent' core. */
    protected transient CommonCore commonCore;

    /**
     * Creates the core object for the given collection.
     * 
     * @param core
     */
    protected Core(CommonCore core) {
        this.commonCore = core;
    }

    /**
     * Returns the size of enclosed elements, counting null elements. The size of the core 
     * is equivalent to the size of the contained array.<br/><br/>
     * 
     * Examples:
     * <ul>
     * <li><code>$("a", "b", "c").size()</code> - Returns 3.</li>
     * </ul>
     * 
     * @return The number of element slots this core encloses.
     */
    public abstract int size();

    /**
     * Starts a new parallel mapping process.
     * 
     * @param mapper The mapper to use.
     * @param options Relevant options: <code>OptionMapType</code>.
     */
    @SuppressWarnings("rawtypes")
    protected void map(final Mapper mapper, final Option... options) {
        final int size = mapper.core().size();
        final CommonCore cc = this.commonCore;

        // Quick pass for the probably most common events
        if (size <= 0) return;
        if (size == 1) {
            mapper.handle(0);
            return;
        }
        
        // Compute the later step size and the number of threads.
        final ProfileInformation profileInfo = cc.profileInformation();
        final int STEP_SIZE = Math.max(size() / 10, 1);
        final AtomicInteger index = new AtomicInteger();
        
        // Test-convert the first item and measure time. If time and size are above
        // a certain threshold, parallelize, otherwise map sequentially. However, in here we 
        // only test the first one
        long delta = 0;

        final ListIterator iterator = mapper.core().iterator();
        while(iterator.hasNext()) {
            final int i = iterator.nextIndex();
            final Object o = iterator.next();
            
            // Skipp all null elements
            if(o == null) continue;
            
            // Set the base count to the next position we should consider (in case we break the look)
            index.set(i + 1);

            // Now map the given value
            final long start = System.nanoTime();
            mapper.handle(i);
            delta = System.nanoTime() - start;
            
            break;
        }
        
        // Next, we check if have a speed gain when we move parallel. In general, we do not 
        // have a speed gain when the time it takes to spawn threads takes longer than it would 
        // take to finish the loop single-threaded
        final int toGo = size - index.get();
        final long estTime = delta * toGo;

        // Request a CPU for each element we have (in case we have many, we only receive maxCPU, in case we have 
        // very few, we don't block all CPUs.
        final int NUM_THREADS = Math.min(toGo, this.commonCore.profileInformation().numCPUs); // cc.requestCPUs(toGo);
        
        // We use a safetey factor of 2 for the fork time (FIXME: Should investigate what's the best factor),
        // also, we only spawn something if there is more than one element still to go.
        if((estTime < 2 * profileInfo.forkTime && toGo > 1) || NUM_THREADS < 2) {
            // Instantly release all CPUs when we go singlethreaded
            // this.commonCore.releaseCPUs(NUM_THREADS);
            
            // In this case, we go single threaded
            while(iterator.hasNext()) {
                final int i = iterator.nextIndex();
                iterator.next(); // We need to get the next() that the nextIndex increases. 
                mapper.handle(i);
            }
            
            return;
        }


        // TODO: Get proper value for step size (same problem, see below)
        // TODO: Check size, if small, don't do all this setup in here ...
        // NAH, even for two objects we can have a speed gain if the calls
        // are very slow ...

        
        // Okay, in this case the loop was broken and we decided to go parallel. In that case
        // setup the barrier and spawn threads for all our processors so that we process the array.
        final CyclicBarrier barrier = new CyclicBarrier(NUM_THREADS + 1);
        final AtomicInteger baseCount = new AtomicInteger();

        final Runnable runner = new Runnable() {
            public void run() {
                int bc = baseCount.getAndIncrement() * STEP_SIZE;
                int lower = Math.max(index.get(), bc);
                

                // Get new basecount for every pass ...
                while (lower < size) {
                    final int max = Math.min(Math.min(lower + STEP_SIZE, size), bc + STEP_SIZE);

                    // Pass over all elements
                    for (int i = lower; i < max; i++) {
                        mapper.handle(i);
                    }

                    bc = baseCount.getAndIncrement() * STEP_SIZE;
                    lower = bc;
                }

                // Signal finish
                try {
                    barrier.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                }
            }
        };

        // Execute all our runnables.
        for(int i=0; i<NUM_THREADS; i++) {
            cc.executor().getExecutor().execute(runner);
        }

        // Wait for all threads to finish ...
        try {
            barrier.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (BrokenBarrierException e) {
            e.printStackTrace();
        }
        
        // Release all CPUs we used
        // cc.releaseCPUs(NUM_THREADS);
    }

    /**
     * Starts a parallel folding process.
     * 
     * @param folder The folder to use.
     * @param options Relevant options: <code>OptionMapType</code>.
     */
    @SuppressWarnings("rawtypes")
    protected void fold(final Folder folder, final Option... options) {
        final int size = folder.core().size();

        // Quick pass for the probably most common events
        if (size <= 1) return;
        if (size == 2) {
            folder.handle(0, 1, 0);
            return;
        }

        final int NUM_THREADS = this.commonCore.profileInformation().numCPUs; 

        // Indicates which level (in the folding hierarchy) we are and where the next
        // thread should proceed. The base count indicates where which element should be
        // selected next by the thread, the level indicates how many times we already passed
        // through the whole array.
        final AtomicInteger baseCount = new AtomicInteger();
        final AtomicInteger round = new AtomicInteger();

        // Synchronizes threads. Each thread waits at the level-barrier when it finished the last level,
        // and waits the the global barrier when it is completely done. The main thread will also
        // wait at the global barrier (thus +1) for all spawned threads.
        final CyclicBarrier levelbarrier = new CyclicBarrier(NUM_THREADS);
        final CyclicBarrier barrier = new CyclicBarrier(NUM_THREADS + 1);
        
        // Algorithm example for 10 elements and 4 CPUs :
        // Content: a b c d e f g h i j
        // Array:   _ _ _ _ _ _ _ _ _ _
        // Index:   0 1 2 3 4 5 6 7 8 9
        
        // In general, we process in levels, in parallel. In each level, each thread
        // takes the next two elements, merges them, and stores them in the position of 
        // the first element. This means, after each level the number of elements we need to 
        // consider halves.
        
        // 1) We create 4 runner threads
        // 2) Each runner starts by picking / computing
        //    2.1) The current level (0 in the beginning) - lvl
        //    2.2) The distance of between X and Y for the fold operation in this level 2^0 = 1
        // 3) Each level, we check if the distance is bigger than the actual size (10 in this 
        //    case) of the array. If it is, we know we have finished.
        // 4) The upper bound for each run is the size of the array minus the stepping distance
        //    (10 - 1) = 9
        // 5) The index X for this operation is the base count (0 * 1) = 0
        // 6) The index Y is X + dist (0 + 1) = 1
        // 7) Until we reached the upper bound:
        //    7.1) handle the two elements
        //    7.2) Take the next available elements (X, Y)
        // ...
        
        // Given fold = max
        
        // Level 0, dist = 1
        // 0 x 1 -> 0
        // Content: b b c d e f g h i j
        // 2 x 3 -> 2
        // Content: b b d d e f g h i j
        // 4 x 5 -> 4
        // Content: b b d d f f g h i j
        // 6 x 7 -> 6
        // Content: b b d d f f h h i j
        // 8 x 9 -> 8
        // Content: b b d d f f h h i i
        
        // Level 1, dist = 2
        // 0 x 2 -> 0
        // Content: d b d d f f h h i i
        // 4 x 6 -> 4
        // Content: d b d d h f h h i i
        // 4 x 8 -> 4
        // Content: d b d d h i h h i i

        // Level 2, dist = 4
        // 0 x 4 -> 4
        // Content: i b d d h i h h i i

        // Return index[0]
        
        
        final Runnable runner = new Runnable() {
            public void run() {
                int rnd = round.get();
                int elementDistance = (int) Math.pow(2, rnd);

                // Each thread processes as long as the distance between elements is smaller
                // than the size
                while (elementDistance < size) {
                	// Each round the upper bound gets lower as we don't have to consider
                	// the last elements
                    final int upperBound = size - elementDistance;

                    
                    // Now we take a next element pair 
                    int lastHandledI = -1;
                    int i = baseCount.getAndAdd(2) * elementDistance;
                    int j = i + elementDistance;

                    // And each thread loop in here, until the right element
                    // has left the righter bound
                    while (j <= upperBound) {
                    	// Process two elements and store them
                        folder.handle(i, j, i);

                        // Remember what we processed last
                        lastHandledI = i;
                        
                        // Take the next available element pairs
                        i = baseCount.getAndAdd(2) * elementDistance;
                        j = i + elementDistance;
                    }

                    
                    // Ideally, at this point, we are through the array. However, it might be the case 
                    // that an uneven number of elements was in that loop run
                    // 10 -> 5 pairs
                    // 11 -> 5 pairs, one left
                    
                    // So we check now if we were the thread processing the last element pair
                    // in that turn, and if there was a sole element left
                    
                    // This is the case when we processed something (lastHandledI > 0)
                    // and when element 2nd element to the right (the next one) exists
                    // but its pair (the 3rd one) does not. 
                    
                    // In that case we connect this element wit the next one 
                    
                    if (lastHandledI + 2 * elementDistance <= upperBound && 
                        	lastHandledI + 3 * elementDistance > upperBound &&
                        	lastHandledI >= 0) {
                        	folder.handle(lastHandledI, lastHandledI + 2 * elementDistance, lastHandledI);
                    }
                
                    // At this point we finished the round
                    try {
                        levelbarrier.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (BrokenBarrierException e) {
                        e.printStackTrace();
                    }
                    
                    
                    // If we were the one who changed the level, we also change the baseCount 
                    // back to 0
                    if (round.compareAndSet(rnd, rnd + 1)) {
                        baseCount.set(0);
                    }
                    
                   
                    // We first have to wait twice, as otherwise an other thread
                    // might be quicker than us in returning up to the loop again and use the
                    // old level value 
                    

                    // Now we all wait that the level has been updated
                    try {
                        levelbarrier.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (BrokenBarrierException e) {
                        e.printStackTrace();
                    }
                    
                    // And get new parameters
                    rnd = round.get();
                    elementDistance = (int) Math.pow(2, rnd);
                }

                try {
                    barrier.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                }
            }
        };

        // Execute all our runnables.
        for(int i=0; i<NUM_THREADS; i++) {
            this.commonCore.executor().getExecutor().execute(runner);
        }

        // Wait for all threads to finish ...
        try {
            barrier.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (BrokenBarrierException e) {
            e.printStackTrace();
        }
    }
}
