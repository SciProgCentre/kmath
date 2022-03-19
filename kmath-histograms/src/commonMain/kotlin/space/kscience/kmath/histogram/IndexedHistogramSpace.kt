/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.histogram

import space.kscience.kmath.domains.Domain
import space.kscience.kmath.linear.Point
import space.kscience.kmath.nd.DefaultStrides
import space.kscience.kmath.nd.FieldND
import space.kscience.kmath.nd.Shape
import space.kscience.kmath.nd.StructureND
import space.kscience.kmath.operations.Group
import space.kscience.kmath.operations.ScaleOperations
import space.kscience.kmath.operations.invoke

/**
 * A simple histogram bin based on domain
 */
public data class DomainBin<in T : Comparable<T>>(
    public val domain: Domain<T>,
    override val value: Number,
) : Bin<T>, Domain<T> by domain

public class IndexedHistogram<T : Comparable<T>, V : Any>(
    public val histogramSpace: IndexedHistogramSpace<T, V>,
    public val values: StructureND<V>,
) : Histogram<T, Bin<T>> {

    override fun get(point: Point<T>): Bin<T>? {
        val index = histogramSpace.getIndex(point) ?: return null
        return histogramSpace.produceBin(index, values[index])
    }

    override val dimension: Int get() = histogramSpace.shape.size

    override val bins: Iterable<Bin<T>>
        get() = DefaultStrides(histogramSpace.shape).asSequence().map {
            histogramSpace.produceBin(it, values[it])
        }.asIterable()
}

/**
 * A space for producing histograms with values in a NDStructure
 */
public interface IndexedHistogramSpace<T : Comparable<T>, V : Any>
    : Group<IndexedHistogram<T, V>>, ScaleOperations<IndexedHistogram<T, V>> {
    //public val valueSpace: Space<V>
    public val shape: Shape
    public val histogramValueSpace: FieldND<V, *> //= NDAlgebra.space(valueSpace, Buffer.Companion::boxing, *shape),

    /**
     * Resolve index of the bin including given [point]
     */
    public fun getIndex(point: Point<T>): IntArray?

    /**
     * Get a bin domain represented by given index
     */
    public fun getDomain(index: IntArray): Domain<T>?

    public fun produceBin(index: IntArray, value: V): Bin<T>

    public fun produce(builder: HistogramBuilder<T>.() -> Unit): IndexedHistogram<T, V>

    override fun add(left: IndexedHistogram<T, V>, right: IndexedHistogram<T, V>): IndexedHistogram<T, V> {
        require(left.histogramSpace == this) { "Can't operate on a histogram produced by external space" }
        require(right.histogramSpace == this) { "Can't operate on a histogram produced by external space" }
        return IndexedHistogram(this, histogramValueSpace { left.values + right.values })
    }

    override fun scale(a: IndexedHistogram<T, V>, value: Double): IndexedHistogram<T, V> {
        require(a.histogramSpace == this) { "Can't operate on a histogram produced by external space" }
        return IndexedHistogram(this, histogramValueSpace { a.values * value })
    }

    override val zero: IndexedHistogram<T, V> get() = produce { }
}

