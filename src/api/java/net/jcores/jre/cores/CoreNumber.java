/*
 * CoreFile.java
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

import java.text.DecimalFormat;

import net.jcores.jre.CommonCore;
import net.jcores.jre.cores.adapter.AbstractAdapter;
import net.jcores.jre.interfaces.functions.F1;
import net.jcores.jre.interfaces.functions.F2ReduceObjects;
import net.jcores.jre.options.Option;

/**
 * A core holding {@link Number} objects (like {@link Integer}, {@link Double}, ...). For example,
 * to calulate the variance of a number of numbers, write:<br/>
 * <br/>
 * 
 * <code>$(5, 0, 8, 6, 6, 7).variance()</code>
 * 
 * @author Ralf Biedert
 * @since 1.0
 */
public class CoreNumber extends CoreObject<Number> {

    /** Used for serialization */
    private static final long serialVersionUID = -8437925527295825364L;

    /**
     * Wraps a number of numbers.
     * 
     * @param supercore The shared CommonCore.
     * @param objects The numbers to wrap.
     */
    public CoreNumber(CommonCore supercore, Number... objects) {
        super(supercore, objects);
    }

    /**
     * @param supercore The shared CommonCore.
     * @param adapter The adapter.
     */
    public CoreNumber(CommonCore supercore, AbstractAdapter<Number> adapter) {
        super(supercore, adapter);
    }

    /**
     * Returns the average of all enclosed numbers.<br/>
     * <br/>
     * 
     * Examples:
     * <ul>
     * <li><code>$(1, 3).average()</code> - Returns the average of 1 and 3, which is 2.</li>
     * </ul>
     * 
     * Single-threaded.<br/>
     * <br/>
     * 
     * @since 1.0
     * @return The average of all enclosed numbers. If no numbers are enclosed, <code>0</code> is returned.
     */
    public double average() {
        return average(0.0);
    }


    /**
     * Returns the average of all enclosed numbers or <code>alternative</code> if there 
     * were no elements in this core.<br/>
     * <br/>
     * 
     * Examples:
     * <ul>
     * <li><code>$(null).average(Double.NaN)</code> - Returns <code>NaN</code>.</li>
     * </ul>
     * 
     * Single-threaded.<br/>
     * <br/>
     * 
     * @param alternative The alternative to return if no, or only <code>NaN</code> / 
     * <code>null</code> elements are enclosed. 
     * 
     * @since 1.0
     * @return The average of all enclosed numbers. If no numbers (or no actual numbers) are enclosed, 
     * <code>alternative</code> is returned.
     */
    public double average(double alternative) {
        int cnt = 0;
        double sum = 0.0;

        // Compute the average of all values
        for (Number number : this) {
            if (number == null) continue;
            if (Double.isNaN(number.doubleValue())) continue;

            sum += number.doubleValue();
            cnt++;
        }

        // If we haven't had any element, return 0
        if (cnt == 0) return alternative;
        return sum / cnt;
    }

    
    /**
     * Returns the number at the given position as a double, or
     * returns <code>Double.NaN</code> if the object was null.<br/>
     * <br/>
     * 
     * Examples:
     * <ul>
     * <li><code>$(100, 200).d(0)</code> - Returns the first value (100) in this core as a double value (100.0).</li>
     * </ul>
     * 
     * Single-threaded.<br/>
     * <br/>
     * 
     * @since 1.0
     * @param index The index to get the number for.
     * @return The double value of the number or <code>NaN</code> if it was null.
     */
    public double d(int index) {
        if (get(index) == null) return Double.NaN;
        return this.adapter.get(index).doubleValue();
    }

    /**
     * Returns all contained numbers as a true double array.<br/>
     * <br/>
     * 
     * Examples:
     * <ul>
     * <li><code>$(100, 200).ds</code> - Returns <code>[100.0, 200.0]</code>.</li>
     * </ul>
     * 
     * Single-threaded.<br/>
     * <br/>
     * 
     * @since 1.0
     * @return An array of doubles to return. Null objects are converted to <code>Double.NaN</code>.
     */
    public double[] ds() {
        double rval[] = new double[size()];
        for (int i = 0; i < rval.length; i++) {
            rval[i] = get(i) == null ? Double.NaN : get(i).doubleValue();
        }

        return rval;
    }

    /**
     * Returns the number at the given position as an integer, or
     * returns <code>0</code> if the object was null.<br/>
     * <br/>
     * 
     * Examples:
     * <ul>
     * <li><code>$(100.26, 200.33).i(1)</code> - Returns the second value (200.33) in this core as an int value (200).</li>
     * </ul>
     * 
     * Single-threaded.<br/>
     * <br/>
     * 
     * @since 1.0
     * @param index The index to get the number for.
     * @return The integer value of the number or <code>0</code> if it was null.
     */
    public int i(int index) {
        if (get(index) == null) return 0;
        return this.adapter.get(index).intValue();
    }

    /**
     * Returns all contained numbers as a true integer array.<br/>
     * <br/>
     * 
     * Examples:
     * <ul>
     * <li><code>$(100, 200).is</code> - Returns <code>[100, 200]</code>.</li>
     * </ul>
     * 
     * Single-threaded.<br/>
     * <br/>
     * 
     * @since 1.0
     * @return An array of integers to return. Null objects are converted to <code>0</code>.
     */
    public int[] is() {
        int rval[] = new int[size()];
        for (int i = 0; i < rval.length; i++) {
            rval[i] = get(i) == null ? 0 : get(i).intValue();
        }
        return rval;
    }

    /**
     * Returns the maximum value.<br/>
     * <br/>
     * 
     * Examples:
     * <ul>
     * <li><code>$(3, 1, 2).max()</code> - Returns 3.0.</li>
     * </ul>
     * 
     * Single-threaded.<br/>
     * <br/>
     * 
     * @since 1.0
     * @return The maximum value enclosed in this core, or <code>0</code> if no value was found.
     */
    public double max() {
        return max(0.0);
    }
    

    /**
     * Returns the maximum value or the alternative, if the result were undefined (only <code>null</code> /
     * <code>NaN</code> in the core).<br/>
     * <br/>
     * 
     * Examples:
     * <ul>
     * <li><code>$(null, Double.NaN).min(1.0)</code> - Returns 1.0</li>
     * </ul>
     * 
     * Single-threaded.<br/>
     * <br/>
     * 
     * @param alternative The alternative to return when the result would otherwise be <code>null</code> /
     * <code>NaN</code>. 
     * @return The maximum value enclosed in this core, or <code>0</code> if no value was found.
     */
    @SuppressWarnings("boxing")
    public double max(final double alternative) {
        double value = reduce(new F2ReduceObjects<Number>() {
            @Override
            public Number f(Number left, Number right) {
                return Math.max(left.doubleValue(), right.doubleValue());
            }
        }).get(Double.valueOf(alternative)).doubleValue();

        return Double.isNaN(value) ? alternative : value;
    }


    /**
     * Returns the minimum value.<br/>
     * <br/>
     * 
     * Examples:
     * <ul>
     * <li><code>$(3, 1, -2).min()</code> - Returns -2.0.</li>
     * </ul>
     * 
     * Single-threaded.<br/>
     * <br/>
     * 
     * @return The maximum value enclosed in this core, or <code>0</code> if no value was found.
     */
    public double min() {
        return min(0.0);
    }

    /**
     * Returns the minimum value or the alternative, if the result were undefined (only <code>null</code> /
     * <code>NaN</code> in the core).<br/>
     * <br/>
     * 
     * Examples:
     * <ul>
     * <li><code>$(null, Double.NaN).min(1.0)</code> - Returns 1.0</li>
     * </ul>
     * 
     * Single-threaded.<br/>
     * <br/>
     * 
     * @param alternative The alternative to return when the result would otherwise be <code>null</code> /
     * <code>NaN</code>. 
     * @return The minimum value enclosed in this core, or <code>0</code> if no value was found.
     */
    @SuppressWarnings("boxing")
    public double min(final double alternative) {
        double value = reduce(new F2ReduceObjects<Number>() {
            @Override
            public Number f(Number left, Number right) {
                return Math.min(left.doubleValue(), right.doubleValue());
            }
        }).get(Double.valueOf(alternative)).doubleValue();

        return Double.isNaN(value) ? alternative : value;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.jcores.shared.cores.CoreObject#reduce(net.jcores.shared.interfaces.functions.F2ReduceObjects,
     * net.jcores.shared.options.Option[])
     */
    @Override
    public CoreObject<Number> reduce(final F2ReduceObjects<Number> f,
                                     final Option... options) {
        // We call super.reduce, but we don't consider NaN numbers.
        return super.reduce(new F2ReduceObjects<Number>() {
            @Override
            public Number f(Number left, Number right) {
                if (Double.isNaN(left.doubleValue())) return right;
                if (Double.isNaN(right.doubleValue())) return left;
                return f.f(left, right);
            }
        }, options);
    }

    /**
     * Returns the number at the given position as a String.<br/>
     * <br/>
     * 
     * Examples:
     * <ul>
     * <li><code>$(100.26, 200.33).s(0)</code> - Returns "100.26"</li>
     * </ul>
     * 
     * Single-threaded.<br/>
     * <br/>
     * 
     * @since 1.0
     * @param index The index to get the number for.
     * @return The String value of the number or <code>"null"</code> if it was null.
     */
    public String s(int index) {
        if (get(index) == null) return "null";
        return this.adapter.get(index).toString();
    }

    /**
     * Returns the number at the given position as a String, formatted using the given {@link DecimalFormat}.<br/>
     * <br/>
     * 
     * Examples:
     * <ul>
     * <li><code>$(100.26, 200.33).s(0, ".0")</code> - Returns "100.3"</li>
     * </ul>
     * 
     * Single-threaded.<br/>
     * <br/>
     * 
     * @since 1.0
     * @param index The index to get the number for.
     * @param format The format {@link DecimalFormat} format.
     * @return The String value of the number or <code>"null"</code> if it was null.
     */
    public String s(int index, String format) {
        if (get(index) == null) return "null";
        final DecimalFormat fmt = new DecimalFormat(format);
        return fmt.format(get(index).doubleValue());
    }

    /**
     * Returns the standard deviation of all enclosed numbers.<br/>
     * <br/>
     * 
     * Examples:
     * <ul>
     * <li><code>$(2, -2, 2, -2).standarddeviation()</code> - Returns 2.0.</li>
     * </ul>
     * 
     * Single-threaded.<br/>>
     * <br/>
     * 
     * @since 1.0
     * @return The standard deviation of all enclosed numbers. If no numbers are
     * enclosed, <code>0</code> is returned.
     */
    public double standarddeviation() {
        return Math.sqrt(variance());
    }

    /**
     * Returns a {@link CoreString} with all numbers converted using the given {@link DecimalFormat}.<br/>
     * <br/>
     * 
     * Examples:
     * <ul>
     * <li><code>$(100.26, 200.33).string(".0").get(1)</code> - Returns 200.3.</li>
     * </ul>
     * 
     * Single-threaded.<br/>>
     * <br/>
     * 
     * @since 1.0
     * @param format The {@link DecimalFormat} to apply.
     * @return A {@link CoreString} with all numbers formatted.
     */
    public CoreString string(String format) {
        final DecimalFormat fmt = new DecimalFormat(format);
        return map(new F1<Number, String>() {
            public String f(Number x) {
                return fmt.format(x.doubleValue());
            }
        }).as(CoreString.class);
    }

    /**
     * Returns the sum of all enclosed numbers.<br/>
     * <br/>
     * 
     * Examples:
     * <ul>
     * <li><code>$(1, 2, 3).sum()</code> - Returns 6.0.</li>
     * </ul>
     * 
     * Single-threaded.<br/>>
     * <br/>
     * 
     * @since 1.0
     * @return The sum of all enclosed numbers. If no numbers are
     * enclosed, <code>0</code> is returned.
     */
    public double sum() {
        final int size = size();
        double sum = 0.0;

        // Compute the average of all values
        for (int i = 0; i < size; i++) {
            final Number number = get(i);
            if (number == null) continue;
            if (Double.isNaN(number.doubleValue())) continue;

            sum += number.doubleValue();
        }

        return sum;
    }

    /**
     * Returns the variance of all enclosed numbers, assuming a uniform distribution.<br/>
     * <br/>
     * 
     * Examples:
     * <ul>
     * <li><code>$(2, -2, 2, -2).variance()</code> - Returns 4.0.</li>
     * </ul>
     * 
     * Single-threaded.<br/>>
     * <br/>
     * 
     * @since 1.0
     * @return The variance of all enclosed numbers. If no numbers are enclosed, <code>0</code> is returned.
     */
    public double variance() {
        final double average = average();
        final int size = size();

        int cnt = 0;
        double rval = 0;

        // Compute the variance
        for (int i = 0; i < size; i++) {
            final Number number = get(i);
            if (number == null) continue;
            if (Double.isNaN(number.doubleValue())) continue;

            rval += (average - number.doubleValue()) * (average - number.doubleValue());
            cnt++;
        }

        // If we haven't had any element, return 0
        if (cnt == 0) return 0;

        return rval / cnt;
    }
}
