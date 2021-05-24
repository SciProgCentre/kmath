/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.nd4j

import org.nd4j.linalg.api.ndarray.INDArray
import space.kscience.kmath.misc.PerformancePitfall
import space.kscience.kmath.nd.MutableStructureND
import space.kscience.kmath.nd.StructureND

/**
 * Represents a [StructureND] wrapping an [INDArray] object.
 *
 * @param T the type of items.
 */
public sealed class Nd4jArrayStructure<T> : MutableStructureND<T> {
    /**
     * The wrapped [INDArray]. Since KMath uses [Int] indices, assuming the size of [INDArray] is less or equal to
     * [Int.MAX_VALUE].
     */
    public abstract val ndArray: INDArray

    override val shape: IntArray get() = ndArray.shape().toIntArray()

    internal abstract fun elementsIterator(): Iterator<Pair<IntArray, T>>
    internal fun indicesIterator(): Iterator<IntArray> = ndArray.indicesIterator()

    @PerformancePitfall
    override fun elements(): Sequence<Pair<IntArray, T>> = Sequence(::elementsIterator)
}

private data class Nd4jArrayIntStructure(override val ndArray: INDArray) : Nd4jArrayStructure<Int>() {
    override fun elementsIterator(): Iterator<Pair<IntArray, Int>> = ndArray.intIterator()
    override fun get(index: IntArray): Int = ndArray.getInt(*index)
    override fun set(index: IntArray, value: Int): Unit = run { ndArray.putScalar(index, value) }
}

/**
 * Wraps this [INDArray] to [Nd4jArrayStructure].
 */
public fun INDArray.asIntStructure(): Nd4jArrayStructure<Int> = Nd4jArrayIntStructure(this)

private data class Nd4jArrayDoubleStructure(override val ndArray: INDArray) : Nd4jArrayStructure<Double>() {
    override fun elementsIterator(): Iterator<Pair<IntArray, Double>> = ndArray.realIterator()
    override fun get(index: IntArray): Double = ndArray.getDouble(*index)
    override fun set(index: IntArray, value: Double): Unit = run { ndArray.putScalar(index, value) }
}

/**
 * Wraps this [INDArray] to [Nd4jArrayStructure].
 */
public fun INDArray.asDoubleStructure(): Nd4jArrayStructure<Double> = Nd4jArrayDoubleStructure(this)

private data class Nd4jArrayFloatStructure(override val ndArray: INDArray) : Nd4jArrayStructure<Float>() {
    override fun elementsIterator(): Iterator<Pair<IntArray, Float>> = ndArray.floatIterator()
    override fun get(index: IntArray): Float = ndArray.getFloat(*index)
    override fun set(index: IntArray, value: Float): Unit = run { ndArray.putScalar(index, value) }
}

/**
 * Wraps this [INDArray] to [Nd4jArrayStructure].
 */
public fun INDArray.asFloatStructure(): Nd4jArrayStructure<Float> = Nd4jArrayFloatStructure(this)
