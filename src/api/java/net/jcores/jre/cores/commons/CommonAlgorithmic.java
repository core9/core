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

import java.util.concurrent.atomic.AtomicLong;

import net.jcores.jre.CommonCore;

/**
 * Algorithmic utilities like permutations.
 * 
 * @author Ralf Biedert
 * @since 1.0
 * 
 */
public class CommonAlgorithmic extends CommonNamespace {
    /** Counter to deliver the next atomic long */
    final AtomicLong nextLong = new AtomicLong();

    /**
     * Creates a common file object.
     * 
     * @param commonCore
     */
    public CommonAlgorithmic(CommonCore commonCore) {
        super(commonCore);
    }

    /**
     * Ensures the value <code>x</code> is between a and b, so that <code>a <= x <= b</code>. If x is larger or smaller,
     * it will be
     * limited to the given bounds.
     * 
     * @param a The lower bound.
     * @param x The value.
     * @param b The upper bound.
     * 
     * @since 1.0
     * @return The new list.
     */
    public double limit(double a, double x, double b) {
        if (x < a) return a;
        if (x > b) return b;
        return x;
    }

    /**
     * Ensures the value <code>x</code> is between a and b, so that <code>a <= x <= b</code>. If x is larger or smaller,
     * it will be
     * limited to the given bounds.
     * 
     * @param a The lower bound.
     * @param x The value.
     * @param b The upper bound.
     * 
     * @since 1.0
     * @return The new list.
     */
    public int limit(int a, int x, int b) {
        if (x < a) return a;
        if (x > b) return b;
        return x;
    }

    /**
     * With each call the next available long is returned. Since many functions
     * might call this method you cannot assume you will receive the direct
     * successor to your previous call.
     * 
     * @since 1.0
     * @return The next long.
     */
    public long nextLong() {
        // TODO: See Issue #17 … apparently getAndIncrement() can hang 
        // on some machines, as we have seen on Lion 10.7, Java 6.something 
        // when running the Text 2.0 tracking server with the Jython APIv3 hack.  
        
        // Let's try this ugly workaround …
        synchronized (this.nextLong) {
            return this.nextLong.getAndIncrement();
        }
    }

    /**
     * Permutes the given <b>sorted</b> list of objects. With each invocation the next
     * possible permutation will be constructed. You can call this method multiple times
     * on the same array, which iteratively creates the next permutation until <code>false</code> is being returned. In
     * that case, the array was not permuted
     * and no other permutations exist.
     * 
     * @since 1.0
     * @param <T> The type of the array.
     * @param objects The array to permute.
     * @return True if the array was successfully permuted, false if not. Once this method
     * returns false, subsequent calls on the same array will always return false.
     */
    public <T extends Comparable<T>> boolean permute(T objects[]) {
        // Pseudocode from Wikipedia
        // Find the largest index k such that a[k] < a[k + 1]. If no such index exists, the permutation is the last
        // permutation.
        int kk = -1, ll = -1, n = objects.length;
        for (int k = 0; k < n - 1; k++) {
            if (objects[k].compareTo(objects[k + 1]) < 0) kk = k;
        }
        if (kk < 0) return false;

        // Find the largest index l such that a[k] < a[l]. Since k + 1 is such an index, l is well defined and satisfies
        // k < l.
        for (int l = 0; l < n; l++) {
            if (objects[kk].compareTo(objects[l]) < 0) ll = l;
        }

        // Swap a[k] with a[l].
        swap(objects, kk, ll);

        // Reverse the sequence from a[k + 1] up to and including the final element a[n].
        int c = 1;
        for (int i = kk + 1; i < n; i++) {
            if (i >= n - c) break;
            swap(objects, i, n - c++);
        }

        return true;
    }

    /**
     * Returns the relative value of x with respect to the bounds of a and b. If <code>a == x</code> then <code>0</code>
     * is being returned, if <code>b == x</code> then <code>1</code> is being
     * returned, if it is in between, the relative amount will be returned. If <code>x</code> lies
     * outside the bounds, values outside the range <code>0 ... 1</code> will be returned.
     * 
     * @since 1.0
     * 
     * @param a The lower bound.
     * @param x The value.
     * @param b The upper bound.
     * @return The relative value.
     */
    public double relative(double a, double x, double b) {
        return (x - a) / (b - a);
    }

    /**
     * Swaps two elements in an array.
     * 
     * @since 1.0
     * @param <T> The type of the object array.
     * @param objects The array to swap the elements in.
     * @param i The index i to swap with j.
     * @param j The index j to swap with i.
     */
    @SuppressWarnings("unchecked")
    public <T> void swap(T objects[], int i, int j) {
        Object tmp = objects[i];
        objects[i] = objects[j];
        objects[j] = (T) tmp;
    }

}
