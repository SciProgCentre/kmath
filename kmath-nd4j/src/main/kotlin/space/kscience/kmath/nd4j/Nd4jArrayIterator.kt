/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.nd4j

import org.nd4j.linalg.api.ndarray.INDArray
import org.nd4j.linalg.api.shape.Shape

private class Nd4jArrayIndicesIterator(private val iterateOver: INDArray) : Iterator<IntArray> {
    private var i: Int = 0

    override fun hasNext(): Boolean = i < iterateOver.length()

    override fun next(): IntArray {
        val la = if (iterateOver.ordering() == 'c')
            Shape.ind2subC(iterateOver, i++.toLong())
        else
            Shape.ind2sub(iterateOver, i++.toLong())

        return la.toIntArray()
    }
}

internal fun INDArray.indicesIterator(): Iterator<IntArray> = Nd4jArrayIndicesIterator(this)

private sealed class Nd4jArrayIteratorBase<out T>(protected val iterateOver: INDArray) : Iterator<Pair<IntArray, T>> {
    private var i: Int = 0

    final override fun hasNext(): Boolean = i < iterateOver.length()

    abstract fun getSingle(indices: LongArray): T

    final override fun next(): Pair<IntArray, T> {
        val la = if (iterateOver.ordering() == 'c')
            Shape.ind2subC(iterateOver, i++.toLong())!!
        else
            Shape.ind2sub(iterateOver, i++.toLong())!!

        return la.toIntArray() to getSingle(la)
    }
}

private class Nd4jArrayDoubleIterator(iterateOver: INDArray) : Nd4jArrayIteratorBase<Double>(iterateOver) {
    override fun getSingle(indices: LongArray): Double = iterateOver.getDouble(*indices)
}

internal fun INDArray.realIterator(): Iterator<Pair<IntArray, Double>> = Nd4jArrayDoubleIterator(this)

private class Nd4jArrayIntIterator(iterateOver: INDArray) : Nd4jArrayIteratorBase<Int>(iterateOver) {
    override fun getSingle(indices: LongArray) = iterateOver.getInt(*indices.toIntArray())
}

internal fun INDArray.intIterator(): Iterator<Pair<IntArray, Int>> = Nd4jArrayIntIterator(this)

private class Nd4jArrayFloatIterator(iterateOver: INDArray) : Nd4jArrayIteratorBase<Float>(iterateOver) {
    override fun getSingle(indices: LongArray) = iterateOver.getFloat(*indices)
}

internal fun INDArray.floatIterator(): Iterator<Pair<IntArray, Float>> = Nd4jArrayFloatIterator(this)
