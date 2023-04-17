/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.histogram

import space.kscience.kmath.UnstableKMathAPI
import space.kscience.kmath.domains.Domain1D
import space.kscience.kmath.domains.center
import space.kscience.kmath.linear.Point
import space.kscience.kmath.operations.asSequence
import space.kscience.kmath.structures.Buffer


/**
 * A univariate bin based on a range
 *
 * @property binValue The value of histogram including weighting
 */
@UnstableKMathAPI
public data class Bin1D<T : Comparable<T>, out V>(
    public val domain: Domain1D<T>,
    override val binValue: V,
) : Bin<T, V>, ClosedRange<T> by domain.range {

    override val dimension: Int get() = 1

    override fun contains(point: Buffer<T>): Boolean = point.size == 1 && contains(point[0])
}

@OptIn(UnstableKMathAPI::class)
public interface Histogram1D<T : Comparable<T>, V> : Histogram<T, V, Bin1D<T, V>> {
    override val dimension: Int get() = 1
    public operator fun get(value: T): Bin1D<T, V>?
    override operator fun get(point: Buffer<T>): Bin1D<T, V>? = get(point[0])
}

public interface Histogram1DBuilder<in T : Any, V : Any> : HistogramBuilder<T, V> {
    /**
     * Thread safe put operation
     */
    public fun putValue(at: T, value: V = defaultValue)

    override fun putValue(point: Point<out T>, value: V) {
        require(point.size == 1) { "Only points with single value could be used in Histogram1D" }
        putValue(point[0], value)
    }
}

@UnstableKMathAPI
public fun Histogram1DBuilder<Double, *>.fill(items: Iterable<Double>): Unit =
    items.forEach(this::putValue)

@UnstableKMathAPI
public fun Histogram1DBuilder<Double, *>.fill(array: DoubleArray): Unit =
    array.forEach(this::putValue)

@UnstableKMathAPI
public fun <T : Any> Histogram1DBuilder<T, *>.fill(buffer: Buffer<T>): Unit =
    buffer.asSequence().forEach(this::putValue)

@OptIn(UnstableKMathAPI::class)
public val Bin1D<Double, *>.center: Double get() = domain.center