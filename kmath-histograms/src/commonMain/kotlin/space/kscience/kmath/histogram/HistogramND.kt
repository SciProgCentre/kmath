/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.histogram

import space.kscience.kmath.domains.Domain
import space.kscience.kmath.linear.Point
import space.kscience.kmath.nd.DefaultStrides
import space.kscience.kmath.nd.FieldOpsND
import space.kscience.kmath.nd.Shape
import space.kscience.kmath.nd.StructureND
import space.kscience.kmath.operations.Group
import space.kscience.kmath.operations.ScaleOperations
import space.kscience.kmath.operations.invoke

/**
 * @param T the type of the argument space
 * @param V the type of bin value
 */
public class HistogramND<T : Comparable<T>, D : Domain<T>, V : Any>(
    public val group: HistogramGroupND<T, D, V>,
    internal val values: StructureND<V>,
) : Histogram<T, V, DomainBin<T, D, V>> {

    override fun get(point: Point<T>): DomainBin<T, D, V>? {
        val index = group.getIndexOrNull(point) ?: return null
        return group.produceBin(index, values[index])
    }

    override val dimension: Int get() = group.shape.size

    override val bins: Iterable<DomainBin<T, D, V>>
        get() = DefaultStrides(group.shape).asSequence().map {
            group.produceBin(it, values[it])
        }.asIterable()
}

/**
 * A space for producing histograms with values in a NDStructure
 */
public interface HistogramGroupND<T : Comparable<T>, D : Domain<T>, V : Any> :
    Group<HistogramND<T, D, V>>, ScaleOperations<HistogramND<T, D, V>> {
    public val shape: Shape
    public val valueAlgebraND: FieldOpsND<V, *> //= NDAlgebra.space(valueSpace, Buffer.Companion::boxing, *shape),

    /**
     * Resolve index of the bin including given [point]. Return null if point is outside histogram area
     */
    public fun getIndexOrNull(point: Point<T>): IntArray?

    /**
     * Get a bin domain represented by given index
     */
    public fun getDomain(index: IntArray): Domain<T>?

    public fun produceBin(index: IntArray, value: V): DomainBin<T, D, V>

    public fun produce(builder: HistogramBuilder<T, V>.() -> Unit): HistogramND<T, D, V>

    override fun add(left: HistogramND<T, D, V>, right: HistogramND<T, D, V>): HistogramND<T, D, V> {
        require(left.group == this && right.group == this) {
            "A histogram belonging to a different group cannot be operated."
        }
        return HistogramND(this, valueAlgebraND { left.values + right.values })
    }

    override fun scale(a: HistogramND<T, D, V>, value: Double): HistogramND<T, D, V> {
        require(a.group == this) { "A histogram belonging to a different group cannot be operated." }
        return HistogramND(this, valueAlgebraND { a.values * value })
    }

    override val zero: HistogramND<T, D, V> get() = produce { }
}

