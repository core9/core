/*
 * CoreClass.java
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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import net.jcores.jre.CommonCore;
import net.jcores.jre.interfaces.functions.F1;
import net.jcores.jre.managers.ManagerClass;
import net.jcores.jre.options.Args;
import net.jcores.jre.options.DefaultOption;
import net.jcores.jre.options.MessageType;
import net.jcores.jre.options.Option;
import net.jcores.jre.utils.internal.Options;
import net.jcores.jre.utils.internal.Streams;

/**
 * Helper functions when dealing with {@link Class} objects. For example, 
 * to dynamically spawn some class you could write:<br/>
 * <br/>
 * 
 * <code>Robot robot = $(Robot.class).spawn().get(0)</code>
 * 
 * 
 * @author Ralf Biedert
 * @since 1.0
 * @param <T> Type of the classes' objects.
 */
public class CoreClass<T> extends CoreObject<Class<T>> {
    /** Used for serialization */
    private static final long serialVersionUID = -5054890786513339808L;

    /** Our class manager */
    protected final ManagerClass manager; // TODO: FindBugs report: what about deserialization?

    /** All known constructors. */
    protected final Map<Class<?>[], Constructor<T>> constructors = new HashMap<Class<?>[], Constructor<T>>();

    /**
     * Creates a new CoreClass.
     * 
     * @param supercore CommonCore to use.
     * @param clazzes Classes to wrap.
     */
	@SuppressWarnings("unchecked")
    public CoreClass(CommonCore supercore, Class<T>... clazzes) {
        super(supercore, clazzes);

        this.manager = supercore.manager(ManagerClass.class);
    }

    /**
     * Returns the bytecode of the given classes.<br/>
     * <br/>
     * 
     * Examples:
     * <ul>
     * <li><code>$(SomeClass.class).bytecode()</code> - Returns a core with the bytecode for the given class file as a
     * {@link CoreByteBuffer}.</li>
     * </ul>
     * 
     * Multi-threaded. Heavyweight. <br/>
     * <br/>
     * 
     * @param options Zero or more {@link DefaultOption} objects.
     * @return A CoreByteBuffer wrapping the classes' bytecode
     */
    public CoreByteBuffer bytecode(final Option... options) {
        final CommonCore cc = this.commonCore;

        return new CoreByteBuffer(this.commonCore, map(new F1<Class<T>, ByteBuffer>() {
            @Override
            public ByteBuffer f(Class<T> x) {
                final String classname = x.getCanonicalName().replaceAll("\\.", "/") + ".class";
                final ClassLoader classloader = x.getClassLoader();

                // For internal object this usually does not work
                if (classloader == null) {
                    final Options options$ = Options.$(cc, options);
                    options$.failure(x, null, "bytecode:none", "Unable to find bytecode.");
                    return null;
                }

                return Streams.getByteData(classloader.getResourceAsStream(classname));
            }
        }).array(ByteBuffer.class));
    }

    /**
     * Spawns the cored classes with the given objects as args. If a wrapped class is an interface,
     * the last implementor registered with <code>implementor()</code> will be spawned. <br/>
     * <br/>
     * Single-threaded, size-of-one.<br/>
     * <br/>
     * 
     * Examples:
     * <ul>
     * <li><code>$(SomeInterface.class).spawn()</code> - Spawns a object implementing SomeInterface that has been
     * registered before with <code>implementor()</code>.</li>
     * </ul>
     * 
     * @param options The options we support, especially {@link Args}.
     * 
     * @return The core containing the spawned objects.
     */
    @SuppressWarnings("unchecked")
    public CoreObject<T> spawn(final Option... options) {
        // Process each element we might have enclosed.
        final CommonCore cc = this.commonCore;
        final Options options$ = Options.$(this.commonCore, options);
        final Object[] args = options$.args();

        return map(new F1<Class<T>, T>() {
            @Override
            public T f(Class<T> x) {
                // Get the class we operate on
                if (x == null) return null;
                Class<T> toSpawn = x;

                // TODO: Selection of implementor could need some improvement
                if (x.isInterface()) {
                    toSpawn = (Class<T>) CoreClass.this.manager.getImplementors(x)[0];
                }

                // Quick pass for most common option
                if (args == null || args.length == 0) {
                    try {
                        toSpawn.newInstance();
                    } catch (InstantiationException e) {
                        options$.failure(x, e, "spawn:instanceexception", "Unable to create a new instance.");
                    } catch (IllegalAccessException e) {
                        options$.failure(x, e, "spawn:illegalaccess", "Unable to access type.");
                    }
                }

                // Get constructor types ...
                Class<?>[] types = new CoreObject<Object>(cc, args).map(new F1<Object, Class<?>>() {
                    public Class<?> f(Object xx) {
                        return xx.getClass();
                    }
                }).array(Class.class);

                try {
                    Constructor<T> constructor = null;

                    // Get constructor from cache ... (try to)
                    synchronized (CoreClass.this.constructors) {
                        constructor = CoreClass.this.constructors.get(types);

                        // Put a new constructor if it wasn't cached before
                        if (constructor == null) {
                            try {
                                constructor = toSpawn.getDeclaredConstructor(types);
                            } catch (NoSuchMethodException e) {
                                // We catch this exception in here, as sometimes we fail to obtain the
                                // proper constructor with the method above. In that case, we try to get
                                // the closest match
                                Constructor<?>[] declaredConstructors = toSpawn.getDeclaredConstructors();
                                for (Constructor<?> ccc : declaredConstructors) {
                                    // Check if the constructor matches
                                    Class<?>[] parameterTypes = ccc.getParameterTypes();
                                    if (parameterTypes.length != types.length) continue;

                                    boolean mismatch = false;

                                    // Check if each parameter is assignable
                                    for (int i = 0; i < types.length; i++) {
                                        if (!parameterTypes[i].isAssignableFrom(types[i]))
                                            mismatch = true;
                                    }

                                    // In case any parameter mismatched, we can't use this constructor
                                    if (mismatch) continue;

                                    constructor = (Constructor<T>) ccc;
                                }
                            }
                            // If we don't have any constructor at this point, we are in trouble
                            if (constructor == null)
                                throw new NoSuchMethodException("No constructor found.");

                            CoreClass.this.constructors.put(types, constructor);
                        }
                    }

                    return constructor.newInstance(args);

                    // NOTE: We do not swallow all execptions silently, becasue spawn() is a bit
                    // special and we cannot return anything that would still be usable.
                } catch (SecurityException e) {
                    options$.failure(x, e, "spawn:security", "Security exception when trying to spawn.");
                } catch (NoSuchMethodException e) {
                    options$.failure(x, e, "spawn:nomethod", "Method not found.");
                } catch (IllegalArgumentException e) {
                    options$.failure(x, e, "spawn:illegalargs", "Illegal passed arguments.");
                } catch (InstantiationException e) {
                    options$.failure(x, e, "spawn:instanceexception:2", "Cannot instantiate.");
                } catch (IllegalAccessException e) {
                    options$.failure(x, e, "spawn:illegalaccess:2", "Unable to access type (2).");
                } catch (InvocationTargetException e) {
                    options$.failure(x, e, "spawn:invocation", "Unable to invoke target.");
                }

                // TODO Make sure to only use weak references, so that we don't run out of memory
                // and prevent
                // garbage colleciton.
                return null;
            }
        });
    }

    /**
     * Registers an implementor for the currently wrapped interface.<br>
     * <br/>
     * 
     * Examples:
     * <ul>
     * <li><code>$(SomeInterface.class).implementor(SomeInterfaceImpl.class)</code> - Registers the implementor for the
     * given interface. Can be spawned with <code>spawn()</code>.</li>
     * </ul>
     * 
     * Single-threaded, size-of-one.<br/>
     * <br/>
     * 
     * @param implemenetor A class implementing the interface enclosed in get(0).
     */
    public void implementor(Class<?> implemenetor) {
        if (size() > 1)
            this.commonCore.report(MessageType.MISUSE, "implementor() should not be used on cores with more than one class!");

        // Get the class we operate on
        final Class<T> clazz = get(null);
        if (clazz == null) return;

        this.manager.registerImplementor(clazz, implemenetor);
    }

}
