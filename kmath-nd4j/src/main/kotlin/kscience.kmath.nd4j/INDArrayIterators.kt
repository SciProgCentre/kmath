package kscience.kmath.nd4j

import org.nd4j.linalg.api.ndarray.INDArray
import org.nd4j.linalg.api.shape.Shape

private class INDArrayIndicesIterator(private val iterateOver: INDArray) : Iterator<IntArray> {
    private var i: Int = 0

    override fun hasNext(): Boolean = i < iterateOver.length()

    override fun next(): IntArray {
        val la = if (iterateOver.ordering() == 'c')
            Shape.ind2subC(iterateOver, i++.toLong())!!
        else
            Shape.ind2sub(iterateOver, i++.toLong())!!

        return la.toIntArray()
    }
}

internal fun INDArray.indicesIterator(): Iterator<IntArray> = INDArrayIndicesIterator(this)

private sealed class INDArrayIteratorBase<T>(protected val iterateOver: INDArray) : Iterator<Pair<IntArray, T>> {
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

private class INDArrayRealIterator(iterateOver: INDArray) : INDArrayIteratorBase<Double>(iterateOver) {
    override fun getSingle(indices: LongArray): Double = iterateOver.getDouble(*indices)
}

internal fun INDArray.realIterator(): Iterator<Pair<IntArray, Double>> = INDArrayRealIterator(this)

private class INDArrayLongIterator(iterateOver: INDArray) : INDArrayIteratorBase<Long>(iterateOver) {
    override fun getSingle(indices: LongArray) = iterateOver.getLong(*indices)
}

internal fun INDArray.longIterator(): Iterator<Pair<IntArray, Long>> = INDArrayLongIterator(this)

private class INDArrayIntIterator(iterateOver: INDArray) : INDArrayIteratorBase<Int>(iterateOver) {
    override fun getSingle(indices: LongArray) = iterateOver.getInt(*indices.toIntArray())
}

internal fun INDArray.intIterator(): Iterator<Pair<IntArray, Int>> = INDArrayIntIterator(this)

private class INDArrayFloatIterator(iterateOver: INDArray) : INDArrayIteratorBase<Float>(iterateOver) {
    override fun getSingle(indices: LongArray) = iterateOver.getFloat(*indices)
}

internal fun INDArray.floatIterator(): Iterator<Pair<IntArray, Float>> = INDArrayFloatIterator(this)
