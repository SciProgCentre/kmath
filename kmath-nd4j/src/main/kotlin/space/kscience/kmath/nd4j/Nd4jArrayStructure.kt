/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.nd4j

import org.nd4j.linalg.api.ndarray.INDArray
import space.kscience.kmath.PerformancePitfall
import space.kscience.kmath.nd.*

/**
 * Represents a [StructureND] wrapping an [INDArray] object.
 *
 * @param T the type of items.
 */
public sealed class Nd4jArrayStructure<T> : MutableStructureND<T> {
    /**
     * The wrapped [INDArray]. Since KMath uses [Int] indexes, assuming the size of [INDArray] is less or equal to
     * [Int.MAX_VALUE].
     */
    public abstract val ndArray: INDArray

    override val shape: ShapeND get() = ShapeND(ndArray.shape().toIntArray())

    internal abstract fun elementsIterator(): Iterator<Pair<IntArray, T>>
    internal fun indicesIterator(): Iterator<IntArray> = ndArray.indicesIterator()

    @PerformancePitfall
    override fun elements(): Sequence<Pair<IntArray, T>> = Sequence(::elementsIterator)
}

public data class Nd4jArrayIntStructure(override val ndArray: INDArray) : Nd4jArrayStructure<Int>(), StructureNDOfInt {
    override fun elementsIterator(): Iterator<Pair<IntArray, Int>> = ndArray.intIterator()

    @OptIn(PerformancePitfall::class)
    override fun get(index: IntArray): Int = ndArray.getInt(*index)

    override fun getInt(index: IntArray): Int = ndArray.getInt(*index)

    @OptIn(PerformancePitfall::class)
    override fun set(index: IntArray, value: Int): Unit = run { ndArray.putScalar(index, value) }
}

/**
 * Wraps this [INDArray] to [Nd4jArrayStructure].
 */
public fun INDArray.asIntStructure(): Nd4jArrayIntStructure = Nd4jArrayIntStructure(this)

public data class Nd4jArrayDoubleStructure(override val ndArray: INDArray) : Nd4jArrayStructure<Double>(), StructureNDOfDouble {
    override fun elementsIterator(): Iterator<Pair<IntArray, Double>> = ndArray.realIterator()
    @OptIn(PerformancePitfall::class)
    override fun get(index: IntArray): Double = ndArray.getDouble(*index)

    override fun getDouble(index: IntArray): Double = ndArray.getDouble(*index)

    @OptIn(PerformancePitfall::class)
    override fun set(index: IntArray, value: Double): Unit = run { ndArray.putScalar(index, value) }
}

/**
 * Wraps this [INDArray] to [Nd4jArrayStructure].
 */
public fun INDArray.asDoubleStructure(): Nd4jArrayStructure<Double> = Nd4jArrayDoubleStructure(this)

public data class Nd4jArrayFloatStructure(override val ndArray: INDArray) : Nd4jArrayStructure<Float>() {
    override fun elementsIterator(): Iterator<Pair<IntArray, Float>> = ndArray.floatIterator()
    @PerformancePitfall
    override fun get(index: IntArray): Float = ndArray.getFloat(*index)

    @PerformancePitfall
    override fun set(index: IntArray, value: Float): Unit = run { ndArray.putScalar(index, value) }
}

/**
 * Wraps this [INDArray] to [Nd4jArrayStructure].
 */
public fun INDArray.asFloatStructure(): Nd4jArrayStructure<Float> = Nd4jArrayFloatStructure(this)
