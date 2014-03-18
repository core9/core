/*
 * CommonCore.java
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
package net.jcores.jre;

import static net.jcores.jre.CoreKeeper.$;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;

import net.jcores.jre.annotations.AttentionWithClassloaders;
import net.jcores.jre.annotations.Beta;
import net.jcores.jre.annotations.SupportsOption;
import net.jcores.jre.cores.Core;
import net.jcores.jre.cores.CoreNumber;
import net.jcores.jre.cores.CoreObject;
import net.jcores.jre.cores.adapter.ListAdapter;
import net.jcores.jre.cores.commons.CommonAlgorithmic;
import net.jcores.jre.cores.commons.CommonNet;
import net.jcores.jre.cores.commons.CommonSys;
import net.jcores.jre.cores.commons.CommonUI;
import net.jcores.jre.interfaces.functions.F0;
import net.jcores.jre.interfaces.functions.F0R;
import net.jcores.jre.managers.Manager;
import net.jcores.jre.managers.ManagerClass;
import net.jcores.jre.managers.ManagerDebugGUI;
import net.jcores.jre.managers.ManagerDeveloperFeedback;
import net.jcores.jre.managers.ManagerExecution;
import net.jcores.jre.managers.ManagerLogging;
import net.jcores.jre.options.KillSwitch;
import net.jcores.jre.options.MessageType;
import net.jcores.jre.options.Option;
import net.jcores.jre.utils.Async;
import net.jcores.jre.utils.Async.Queue;
import net.jcores.jre.utils.internal.Options;
import net.jcores.jre.utils.internal.Reporter;
import net.jcores.jre.utils.internal.structures.ProfileInformation;
import net.jcores.jre.utils.map.ConcurrentMapUtil;
import net.jcores.jre.utils.map.MapUtil;
import net.xeoh.nexus.DefaultNexus;
import net.xeoh.nexus.InternalService;
import net.xeoh.nexus.Nexus;

/**
 * The common core is a notso-singleton object shared by all created {@link Core} instances of the current class loader.
 * It is mainly a cache, contains helper and utility
 * methods and takes care of the {@link Manager} objects. For example, to but the current thread
 * to sleep (without the ugly try/catch), you would write:<br/>
 * <br/>
 * 
 * <code>$.sys.sleep(1000);</code> <br/>
 * <br/>
 * 
 * Methods and object commonly required by the other cores. All methods in here are (and must be!)
 * thread safe. While most methods (like <code>async()</code>, <code>box()</code>, <code>clone()</code>, ...) are safe
 * to use in any application, there are a few methods (like <code>manager()</code>, <code>nexus()</code>,
 * <code>log()</code>) which should be used with care in
 * multi-classloader-applications like application servers. While they will work in local parts of the
 * code they usually fail to share state between remote parts (e.g., one web application could not
 * register an object with <code>nexus()</code> another web application could see). Methods marked
 * with {@link AttentionWithClassloaders} are such candidates. You can still use them in big apps,
 * but you should know what you are doing.
 * 
 * @author Ralf Biedert
 * @since 1.0
 */
public class CommonCore {
    /** Stores error reports */
    private final Reporter reporter = new Reporter(this);

    /** Stores our managers and possibly application services */
    private final Nexus nexus = new DefaultNexus();

    /** Random variable */
    private final Random random = new Random();

    /** Keeps the profile information */
    private ProfileInformation profileInformation;

    /** Executes commands */
    private ManagerExecution executionManager;

    /** Method to clone objects */
    private Method cloneMethod;

    /** Common system utilities */
    public final CommonSys sys = new CommonSys(this);

    /** Common algorithmic utilities */
    public final CommonAlgorithmic alg = new CommonAlgorithmic(this);

    /** Common ui utilities */
    public final CommonUI ui = new CommonUI(this);

    /** Common net utilities */
    public final CommonNet net = new CommonNet(this);

    /** The ID of this CommonCore */
    protected final String id;

    /**
     * Constructs the common core.
     */
    protected CommonCore() {
        // Register managers we know
        manager(ManagerExecution.class, new ManagerExecution());
        manager(ManagerClass.class, new ManagerClass());
        manager(ManagerDeveloperFeedback.class, new ManagerDeveloperFeedback());
        manager(ManagerDebugGUI.class, new ManagerDebugGUI());
        manager(ManagerLogging.class, new ManagerLogging());

        try {
            this.cloneMethod = Object.class.getDeclaredMethod("clone");
            this.cloneMethod.setAccessible(true);
        } catch (Exception e) {
            report(MessageType.EXCEPTION, "Unable to get cloning method for objects. $.clone() will not work: " + e.getMessage());
        }

        this.id = this.sys.uniqueID();
    }

    /** Updates the managers and their returned information in this core */
    protected void updateManagerInformation() {
        this.executionManager = manager(ManagerExecution.class);
        this.profileInformation = this.executionManager.getProfile();
    }

    /**
     * Executes the given function asynchronously and returns an {@link Async} object which
     * will hold the result.<br/>
     * <br/>
     * 
     * Examples:
     * <ul>
     * <li><code>$.async(lookup)</code> - Performs some lookup task in the background.</li>
     * </ul>
     * 
     * Single-threaded. <br/>
     * <br/>
     * 
     * @param f The function to execute asynchronously on the enclosed objects.
     * @param options Supports all options {@link CommonSys}.<code>oneTime()</code> understands (esp. {@link KillSwitch}
     * ).
     * @param <R> Return type for the {@link Async} object.
     * @return An {@link Async} object that will hold the results (in an arbitrary order).
     */
    @SupportsOption(options = { KillSwitch.class })
    public <R> Async<R> async(final F0R<R> f, final Option... options) {
        final Queue<R> queue = Async.Queue();
        final Async<R> async = new Async<R>(this, queue);

        // Maybe use the executor right away?
        this.sys.oneTime(new F0() {
            @Override
            public void f() {
                try {
                    queue.add(Async.QEntry(f.f()));
                    queue.close();
                } catch (Exception e) {
                    final Options options$ = Options.$(CommonCore.this, options);
                    options$.failure(f, e, "async:exception", "General exception invoking async function.");
                    queue.close();
                }
            }
        }, 0, options);

        return async;
    }

    /**
     * Wraps number of ints and returns an Integer array.
     * 
     * @since 1.0
     * @param object The numbers to wrap.
     * @return An Integer array.
     */
    public Integer[] box(int... object) {
        int i = 0;

        final Integer[] myIntegers = new Integer[object.length];
        for (int val : object)
            myIntegers[i++] = Integer.valueOf(val);

        return myIntegers;
    }

    /**
     * Wraps number of doubles and returns a Double array.
     * 
     * @since 1.0
     * @param object The numbers to wrap.
     * @return An Double array.
     */
    public Double[] box(double... object) {
        int i = 0;

        final Double[] myIntegers = new Double[object.length];
        for (double val : object)
            myIntegers[i++] = Double.valueOf(val);

        return myIntegers;
    }

    /**
     * Wraps number of floats and returns a new Float array.
     * 
     * @param object The numbers to wrap.
     * @return A CoreNumber wrapping the given compounds.
     */
    public Float[] box(float... object) {
        int i = 0;

        final Float[] myIntegers = new Float[object.length];
        for (float val : object)
            myIntegers[i++] = Float.valueOf(val);

        return myIntegers;
    }

    /**
     * Wraps number of longs and returns an Long array.
     * 
     * @since 1.0
     * @param object The numbers to wrap.
     * @return An Integer array.
     */
    public Long[] box(long... object) {
        int i = 0;

        final Long[] myIntegers = new Long[object.length];
        for (long val : object)
            myIntegers[i++] = Long.valueOf(val);

        return myIntegers;
    }

    /**
     * Clones the given object if it is cloneable.
     * 
     * @since 1.0
     * @param <T>
     * @param object The object to clone
     * @param options The supported default options.
     * @return A clone of the object, or null if the object could not be cloned.
     */
    @SuppressWarnings("unchecked")
    public <T> T clone(T object, Option... options) {
        if (!(object instanceof Cloneable)) return null;

        try {
            return (T) this.cloneMethod.invoke(object);
        } catch (Exception e) {
            Options.$(this, options).failure(object, e, "clone:unknown", "Unable to clone object.");
        }

        return null;
    }

    /**
     * Clones the given array and returns a <b>shallow</b> copy (i.e., the elements themselves
     * are the same in both arrays).
     * 
     * @since 1.0
     * @param <T>
     * @param object The array to clone
     * @return A cloned (copied) array.
     */
    public <T> T[] clone(T[] object) {
        if (object == null) return null;
        return Arrays.copyOf(object, object.length);
    }

    /**
     * Returns a new and empty {@link ConcurrentMapUtil}.
     * 
     * @param <K> The type of the key.
     * @param <V> The type of the value.
     * @since 1.0
     * @return Returns a new map.
     */
    public <K, V> ConcurrentMapUtil<K, V> concurrentMap() {
        return new ConcurrentMapUtil<K, V>(new ConcurrentHashMap<K, V>());
    }

    /**
     * Wraps a given map into out {@link ConcurrentMapUtil}.
     * 
     * @param <K> The type of the key.
     * @param <V> The type of the value.
     * @param map The map to wrap.
     * @return Returns a wrapped map.
     */
    public <K, V> ConcurrentMapUtil<K, V> concurrentMap(ConcurrentMap<K, V> map) {
        return new ConcurrentMapUtil<K, V>(map);
    }

    /**
     * Returns a core consisting of <code>n</code> times the given object.
     * 
     * @param object The object to fill the core with.
     * @param n The number of times we put the object into the core.
     * @param <T> The type of the object.
     * @since 1.0
     * @return A core of size <code>n</code> filled with the given object.
     * 
     */
    public <T> CoreObject<T> create(T object, int n) {
        final ArrayList<T> list = new ArrayList<T>(n);
        for (int i = 0; i < n; i++) {
            list.add(object);
        }
        return new CoreObject<T>(this, new ListAdapter<T>(list));
    }

    /**
     * Clones the given collection and returns a <b>shallow</b> copy (i.e., the elements themselves
     * are the same in both arrays).
     * 
     * @since 1.0
     * @param <T>
     * @param collection The collection to clone
     * @return A cloned (copied) collection.
     */
    public <T> Collection<T> clone(Collection<T> collection) {
        if (collection == null) return null;
        return new ArrayList<T>(collection);
    }

    /**
     * Returns the ID of this {@link CommonCore}.
     * 
     * @since 1.0
     * @return The unique ID of this {@link CommonCore}.
     */
    public String coreID() {
        return this.id;
    }

    /**
     * Clones the given array and returns a <b>deep</b> copy (i.e., the elements themselves
     * are the cloned in both arrays).
     * 
     * @since 1.0
     * @param <T>
     * @param object The array to clone
     * @return A cloned array.
     */
    public <T> T[] deepclone(T[] object) {
        if (object == null) return null;

        final T[] copyOf = Arrays.copyOf(object, object.length);
        for (int i = 0; i < copyOf.length; i++) {
            copyOf[i] = clone(copyOf[i]);
        }

        return copyOf;
    }

    /**
     * Returns our default jCores executor.
     * 
     * @since 1.0
     * @return The default executor service.
     */
    public ManagerExecution executor() {
        return this.executionManager;
    }

    /**
     * Call this function if you want to give the jCores team runtime feedback of your
     * application. The library will then, at certain points, upload profiling and
     * execution results to improve the performance and usability. Everything stays anonymous!
     * 
     * @since 1.0
     */
    @Beta
    public void feedback() {
        //
    }

    /**
     * Returns the default nexus for jCores. Do <i>not</i> use this instance
     * to manage <i>globals</i> or singletons for your application, since there might be
     * cases where two parts of your app won't see each other (e.g., different
     * classloaders). The {@link CommonCore} in jCores is merely a local <i>cache</i>, shared
     * by the stataic {@link CoreKeeper} (with some added extras), that keeps track of non-essential,
     * sharable objects which are expensive to create (like profiling information).<br/>
     * <br/>
     * 
     * @since 1.0
     * @return The default {@link Nexus} used by jCores (which might be used by your
     * application as well if you know what you are doing, but keep in mind the warning above).
     */
    @AttentionWithClassloaders
    public Nexus nexus() {
        return this.nexus;
    }

    /**
     * Sets a manager of a given type, only needed for core developers.
     * 
     * @param <T> Manager's type.
     * @param clazz Manager's class.
     * @param manager The actual manager to put.
     * @return Return the manager that was already in the list, if there was one, or the current manager which was also
     * set.
     */
    @AttentionWithClassloaders
    public <T extends Manager> T manager(Class<T> clazz, T manager) {
        // Perpare adding the manager to the kernel
        final Collection<InternalService<T>> services = new ArrayList<InternalService<T>>();
        services.add(new InternalService<T>(manager));

        // And add it, and update the manager information
        this.nexus.register(services);
        updateManagerInformation();

        // Eventually return the result.
        return this.nexus.get(clazz);
    }

    /**
     * Returns a manager of the given type, only needed for core developers.
     * 
     * @param <T> Manager's type.
     * @param clazz Manager's class.
     * @return Returns the currently set manager.
     */
    @AttentionWithClassloaders
    public <T extends Manager> T manager(Class<T> clazz) {
        return this.nexus.get(clazz);
    }

    /**
     * Returns a new and empty (hash) {@link MapUtil}.
     * 
     * @param <K> The type of the key.
     * @param <V> The type of the value.
     * @since 1.0
     * @return Returns a new map.
     */
    public <K, V> MapUtil<K, V> map() {
        return new MapUtil<K, V>(new HashMap<K, V>());
    }

    /**
     * Wraps a given map into out {@link MapUtil}.
     * 
     * @param <K> The type of the key.
     * @param <V> The type of the value.
     * @param map The map to wrap.
     * @return Returns a wrapped map.
     */
    public <K, V> MapUtil<K, V> map(Map<K, V> map) {
        return new MapUtil<K, V>(map);
    }

    /**
     * Returns a new (linked) {@link List} when the number of elements to
     * store is not known.
     * 
     * @since 1.0
     * @param <T> The type of the list.
     * @return The new list.
     */
    public <T> List<T> list() {
        return new LinkedList<T>();
    }

    /**
     * Returns a new (array) {@link List} when the number of elements to store is
     * approximately known.
     * 
     * @since 1.0
     * @param <T> The type of the list.
     * @param n The approximate number of elements to store.
     * @return The new list.
     */
    public <T> List<T> list(int n) {
        return new ArrayList<T>(n);
    }

    /**
     * Logs the given string. This method might, but is not required, to use the official Java logging
     * facilities.
     * 
     * @param string The string to log.
     * @param level Log level to use.
     */
    @AttentionWithClassloaders
    public void log(String string, Level level) {
        this.manager(ManagerLogging.class).handler().log(string, level);
    }

    /**
     * Measures how long the execution of the given function took. The result will be returned in nanoseconds.
     * 
     * @param f The function to execute.
     * @return The elapsed time in nanoseconds.
     */
    public long measure(F0 f) {
        final long start = System.nanoTime();
        f.f();
        final long end = System.nanoTime();
        return end - start;
    }

    /**
     * Measures how long the execution of the given function took <code>n</code> times. A CoreNumber object will be
     * returned with the results.
     * 
     * @param f The function to execute.
     * @param n The number of times <code>f</code> should be executed.
     * @return A {@link CoreNumber} object with the elapsed times in nanoseconds.
     */
    @SuppressWarnings("boxing")
    public CoreNumber measure(F0 f, int n) {
        final List<Long> results = $.list(n);

        for (int i = 0; i < n; i++) {
            results.add(measure(f));
        }

        return CoreKeeper.$(results).as(CoreNumber.class);
    }

    /**
     * Returns the profiling information gathered at startup. Only required internally.
     * 
     * @return The current profile information.
     */
    public ProfileInformation profileInformation() {
        return this.profileInformation;
    }

    /**
     * Creates a CoreNumber object with numbers ranging from 0 (inclusive) up to <code>end</code> (exclusive).
     * 
     * @param end The last number (exclusive).
     * @return A core number object.
     */
    public CoreNumber range(int end) {
        return range(0, end, 1);
    }

    /**
     * Creates a CoreNumber object with the given <code>start</code> (inclusive) and <code>end</code> (exclusive) and a
     * stepping of +-1 (depending on whether start is smaller or larger than end).
     * 
     * @param from The first number (inclusive)
     * @param end The last number (exclusive).
     * @return A core number object.
     */
    public CoreNumber range(int from, int end) {
        return range(from, end, from <= end ? 1 : -1);
    }

    /**
     * Creates a CoreNumber object with the given <code>start</code> (inclusive), <code>end</code> (exclusive) and
     * stepping.
     * 
     * @param from The first number (inclusive)
     * @param end The last number (exclusive).
     * @param stepping The stepping
     * 
     * @return A core number object.
     */
    public CoreNumber range(int from, int end, int stepping) {
        // FIXME: Stepping problems
        final int rval[] = new int[Math.abs((end - from) / stepping)];
        int ptr = 0;

        if (from <= end) {
            for (int i = from; i < end; i += stepping) {
                rval[ptr++] = i;
            }
        } else {
            for (int i = from; i > end; i += stepping) {
                rval[ptr++] = i;
            }
        }

        return new CoreNumber(this, box(rval));
    }

    /**
     * Returns a new, empty {@link AtomicReference}.
     * 
     * @since 1.0
     * @param <T> The type of the reference.
     * @return A new, empty reference.
     */
    public <T> AtomicReference<T> reference() {
        return new AtomicReference<T>();
    }

    /**
     * Returns a new, already set {@link AtomicReference}.
     * 
     * @since 1.0
     * @param object The initial value.
     * @param <T> The type of the reference.
     * @return A new reference.
     */
    public <T> AtomicReference<T> reference(T object) {
        return new AtomicReference<T>(object);
    }

    /**
     * Reports the problem to our internal problem queue, only used by core developers. Use report() for all
     * internal error and problem reporting and use log() for user requested
     * logging.
     * 
     * @param type Type of the message.
     * @param problem Problem description.
     */
    public void report(MessageType type, String problem) {
        this.reporter.record(problem);
    }

    /**
     * Prints all known problem reports to the console. This is the end-user
     * version (which means, <i>you</i> can use it) to print what went wrong during
     * core operation. See the console for output.
     */
    public void report() {
        this.reporter.printRecords();
    }

    /**
     * Returns our shared {@link Random} object, initialized some time ago.
     * 
     * @return The initialized random object.
     */
    public Random random() {
        return this.random;
    }

    /**
     * Unboxes a number of Integers.
     * 
     * @param object The numbers to unbox.
     * @return An int array.
     */
    @SuppressWarnings("boxing")
    public int[] unbox(Integer... object) {
        int i = 0;

        final int[] myIntegers = new int[object.length];
        for (int val : object)
            myIntegers[i++] = val;

        return myIntegers;
    }

    /**
     * Returns jCores' version, for example <code>1.0.4-201108111256</code>. The version string will
     * always consist of two parts: the actual version (with usually a major, minor and revision part),
     * as well as a dash-separated build number.
     * 
     * @return The current version.
     */
    public String version() {
        return $(getClass().getResourceAsStream("jcores.version")).text().split("\n").hashmap().get("build");
    }

    /**
     * Requests a number of CPUs. The system will check how many CPUs are available
     * and allocate up to <code>request</code> units. The number of allocated CPUs is
     * returned.<br/>
     * <br/>
     * 
     * This function is only used internally. Also note that it is essential to call <code>releaseCPUs</code> after the
     * application stopped using them.
     * 
     * @param request The number of CPUs to request.
     * 
     * @return The actual number of CPUs available.
     */
    public int requestCPUs(int request) {
        // When looking at our benchmarks, it seems this does not speed up things, see Issue #12.
        return Math.min(profileInformation().numCPUs, request);

        /*
         * synchronized (this.freeCPUs) {
         * final int free = this.freeCPUs.get();
         * 
         * // No free CPUs means to party
         * if(free == 0) return 0;
         * 
         * // More requested than free, return what we have
         * if(request > free) {
         * this.freeCPUs.set(0);
         * return free;
         * }
         * 
         * // In other cases, subtract what we have
         * this.freeCPUs.set(free - request);
         * return request;
         * }
         */
    }

    /**
     * Releases a number of CPUSs previously allocated.<br/>
     * <br/>
     * 
     * This function is only used internally. Also note that it is essential to call <code>releaseCPUs</code> after the
     * application stopped using them.
     * 
     * @param toRelease The number of CPUs to release.
     */
    public void releaseCPUs(int toRelease) {
        // When looking at our benchmarks, it seems this does not speed up things, see Issue #12.
        /*
         * synchronized (this.freeCPUs) {
         * final int free = this.freeCPUs.get();
         * this.freeCPUs.set(Math.min(this.profileInformation.numCPUs, free + toRelease));
         * }
         */
    }
}
