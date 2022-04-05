/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.histogram

import space.kscience.kmath.domains.DoubleDomain1D
import space.kscience.kmath.misc.UnstableKMathAPI
import space.kscience.kmath.operations.Group
import space.kscience.kmath.operations.Ring
import space.kscience.kmath.operations.ScaleOperations
import space.kscience.kmath.operations.invoke
import space.kscience.kmath.structures.Buffer
import kotlin.math.floor

@OptIn(UnstableKMathAPI::class)
public class UniformHistogram1D<V : Any>(
    public val group: UniformHistogram1DGroup<V, *>,
    public val values: Map<Int, V>,
) : Histogram1D<Double, V> {

    private val startPoint get() = group.startPoint
    private val binSize get() = group.binSize

    private fun produceBin(index: Int, value: V): Bin1D<Double, V> {
        val domain = DoubleDomain1D((startPoint + index * binSize)..(startPoint + (index + 1) * binSize))
        return Bin1D(domain, value)
    }

    override val bins: Iterable<Bin1D<Double, V>> get() = values.map { produceBin(it.key, it.value) }

    override fun get(value: Double): Bin1D<Double, V>? {
        val index: Int = group.getIndex(value)
        val v = values[index]
        return v?.let { produceBin(index, it) }
    }
}

public class UniformHistogram1DGroup<V : Any, A>(
    public val valueAlgebra: A,
    public val binSize: Double,
    public val startPoint: Double = 0.0,
) : Group<UniformHistogram1D<V>>, ScaleOperations<UniformHistogram1D<V>> where A : Ring<V>, A : ScaleOperations<V> {
    override val zero: UniformHistogram1D<V> by lazy { UniformHistogram1D(this, emptyMap()) }

    public fun getIndex(at: Double): Int = floor((at - startPoint) / binSize).toInt()

    override fun add(left: UniformHistogram1D<V>, right: UniformHistogram1D<V>): UniformHistogram1D<V> = valueAlgebra {
        require(left.group == this@UniformHistogram1DGroup)
        require(right.group == this@UniformHistogram1DGroup)
        val keys = left.values.keys + right.values.keys
        UniformHistogram1D(
            this@UniformHistogram1DGroup,
            keys.associateWith { (left.values[it] ?: valueAlgebra.zero) + (right.values[it] ?: valueAlgebra.zero) }
        )
    }

    override fun UniformHistogram1D<V>.unaryMinus(): UniformHistogram1D<V> = valueAlgebra {
        UniformHistogram1D(this@UniformHistogram1DGroup, values.mapValues { -it.value })
    }

    override fun scale(
        a: UniformHistogram1D<V>,
        value: Double,
    ): UniformHistogram1D<V> = UniformHistogram1D(
        this@UniformHistogram1DGroup,
        a.values.mapValues { valueAlgebra.scale(it.value, value) }
    )

    public inline fun produce(block: Histogram1DBuilder<Double, V>.() -> Unit): UniformHistogram1D<V> {
        val map = HashMap<Int, V>()
        val builder = object : Histogram1DBuilder<Double, V> {
            override val defaultValue: V get() = valueAlgebra.zero

            override fun putValue(at: Double, value: V) {
                val index = getIndex(at)
                map[index] = with(valueAlgebra) { (map[index] ?: zero) + one }
            }
        }
        builder.block()
        return UniformHistogram1D(this, map)
    }
}

public fun <V : Any, A> Histogram.Companion.uniform1D(
    algebra: A,
    binSize: Double,
    startPoint: Double = 0.0,
): UniformHistogram1DGroup<V, A> where A : Ring<V>, A : ScaleOperations<V> =
    UniformHistogram1DGroup(algebra, binSize, startPoint)

@UnstableKMathAPI
public fun <V : Any> UniformHistogram1DGroup<V, *>.produce(
    buffer: Buffer<Double>,
): UniformHistogram1D<V> = produce { fill(buffer) }