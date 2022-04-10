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
public data class DomainBin<in T : Comparable<T>, out V>(
    public val domain: Domain<T>,
    override val binValue: V,
) : Bin<T, V>, Domain<T> by domain

/**
 * @param T the type of the argument space
 * @param V the type of bin value
 */
public class IndexedHistogram<T : Comparable<T>, V : Any>(
    public val histogramGroup: IndexedHistogramGroup<T, V>,
    public val values: StructureND<V>,
) : Histogram<T, V, DomainBin<T, V>> {

    override fun get(point: Point<T>): DomainBin<T, V>? {
        val index = histogramGroup.getIndexOrNull(point) ?: return null
        return histogramGroup.produceBin(index, values[index])
    }

    override val dimension: Int get() = histogramGroup.shape.size

    override val bins: Iterable<DomainBin<T, V>>
        get() = DefaultStrides(histogramGroup.shape).asSequence().map {
            histogramGroup.produceBin(it, values[it])
        }.asIterable()
}

/**
 * A space for producing histograms with values in a NDStructure
 */
public interface IndexedHistogramGroup<T : Comparable<T>, V : Any> : Group<IndexedHistogram<T, V>>,
    ScaleOperations<IndexedHistogram<T, V>> {
    public val shape: Shape
    public val histogramValueAlgebra: FieldND<V, *> //= NDAlgebra.space(valueSpace, Buffer.Companion::boxing, *shape),

    /**
     * Resolve index of the bin including given [point]. Return null if point is outside histogram area
     */
    public fun getIndexOrNull(point: Point<T>): IntArray?

    /**
     * Get a bin domain represented by given index
     */
    public fun getDomain(index: IntArray): Domain<T>?

    public fun produceBin(index: IntArray, value: V): DomainBin<T, V>

    public fun produce(builder: HistogramBuilder<T, V>.() -> Unit): IndexedHistogram<T, V>

    override fun add(left: IndexedHistogram<T, V>, right: IndexedHistogram<T, V>): IndexedHistogram<T, V> {
        require(left.histogramGroup == this && right.histogramGroup == this) {
            "A histogram belonging to a different group cannot be operated."
        }
        return IndexedHistogram(this, histogramValueAlgebra { left.values + right.values })
    }

    override fun scale(a: IndexedHistogram<T, V>, value: Double): IndexedHistogram<T, V> {
        require(a.histogramGroup == this) { "A histogram belonging to a different group cannot be operated." }
        return IndexedHistogram(this, histogramValueAlgebra { a.values * value })
    }

    override val zero: IndexedHistogram<T, V> get() = produce { }
}

