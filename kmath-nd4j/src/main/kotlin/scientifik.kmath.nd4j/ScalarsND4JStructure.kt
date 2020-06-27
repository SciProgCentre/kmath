package scientifik.kmath.nd4j

import org.nd4j.linalg.api.ndarray.INDArray
import scientifik.kmath.structures.NDStructure

interface INDArrayStructureBase<T> : NDStructure<T> {
    val ndArray: INDArray

    override val shape: IntArray
        get() = narrowToIntArray(ndArray.shape())

    fun elementsIterator(): Iterator<Pair<IntArray, T>>
    override fun elements(): Sequence<Pair<IntArray, T>> = Sequence(::elementsIterator)
}

data class INDArrayIntStructure(override val ndArray: INDArray) : INDArrayStructureBase<Int> {
    override fun elementsIterator(): Iterator<Pair<IntArray, Int>> = INDArrayIntIterator(ndArray)
    override fun get(index: IntArray): Int = ndArray.getInt(*index)
}

data class INDArrayLongStructure(override val ndArray: INDArray) : INDArrayStructureBase<Long> {
    override fun elementsIterator(): Iterator<Pair<IntArray, Long>> = INDArrayLongIterator(ndArray)
    override fun get(index: IntArray): Long = ndArray.getLong(*widenToLongArray(index))
}

data class INDArrayDoubleStructure(override val ndArray: INDArray) : INDArrayStructureBase<Double> {
    override fun elementsIterator(): Iterator<Pair<IntArray, Double>> = INDArrayDoubleIterator(ndArray)
    override fun get(index: IntArray): Double = ndArray.getDouble(*index)
}

data class INDArrayFloatStructure(override val ndArray: INDArray) : INDArrayStructureBase<Float> {
    override fun elementsIterator(): Iterator<Pair<IntArray, Float>> = INDArrayFloatIterator(ndArray)
    override fun get(index: IntArray): Float = ndArray.getFloat(*index)
}
