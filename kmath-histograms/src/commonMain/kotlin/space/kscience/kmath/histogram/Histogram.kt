/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.histogram

import space.kscience.kmath.domains.Domain
import space.kscience.kmath.linear.Point
import space.kscience.kmath.structures.DoubleBuffer
import space.kscience.kmath.structures.asBuffer

/**
 * The binned data element. Could be a histogram bin with a number of counts or an artificial construct.
 */
public interface Bin<in T : Any, out V> : Domain<T> {
    /**
     * The value of this bin.
     */
    public val binValue: V
}

/**
 * A simple histogram bin based on domain
 */
public data class DomainBin<in T : Comparable<T>, D : Domain<T>, out V>(
    public val domain: D,
    override val binValue: V,
) : Bin<T, V>, Domain<T> by domain


public interface Histogram<in T : Any, out V, out B : Bin<T, V>> {
    /**
     * Find existing bin, corresponding to given coordinates
     */
    public operator fun get(point: Point<out T>): B?

    /**
     * Dimension of the histogram
     */
    public val dimension: Int

    public val bins: Iterable<B>

    public companion object {
        //A discoverability root
    }
}

public interface HistogramBuilder<in T : Any, V : Any> {

    /**
     * The default value increment for a bin
     */
    public val defaultValue: V

    /**
     * Increment appropriate bin with given value
     */
    public fun putValue(point: Point<out T>, value: V = defaultValue)

}

public fun <T : Any> HistogramBuilder<T, *>.put(point: Point<out T>): Unit = putValue(point)

public fun <T : Any> HistogramBuilder<T, *>.put(vararg point: T): Unit = put(point.asBuffer())

public fun HistogramBuilder<Double, *>.put(vararg point: Number): Unit =
    put(DoubleBuffer(point.map { it.toDouble() }.toDoubleArray()))

public fun HistogramBuilder<Double, *>.put(vararg point: Double): Unit = put(DoubleBuffer(point))
public fun <T : Any> HistogramBuilder<T, *>.fill(sequence: Iterable<Point<T>>): Unit = sequence.forEach { put(it) }

/**
 * Pass a sequence builder into histogram
 */
public fun <T : Any> HistogramBuilder<T, *>.fill(block: suspend SequenceScope<Point<T>>.() -> Unit): Unit =
    fill(sequence(block).asIterable())
