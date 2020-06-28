package scientifik.kmath.nd4j

import org.nd4j.linalg.api.ndarray.INDArray
import scientifik.kmath.structures.MutableNDStructure
import scientifik.kmath.structures.NDStructure

interface INDArrayStructure<T> : NDStructure<T> {
    val ndArray: INDArray

    override val shape: IntArray
        get() = narrowToIntArray(ndArray.shape())

    fun elementsIterator(): Iterator<Pair<IntArray, T>>
    override fun elements(): Sequence<Pair<IntArray, T>> = Sequence(::elementsIterator)
}

inline class INDArrayIntStructure(override val ndArray: INDArray) : INDArrayStructure<Int>, MutableNDStructure<Int> {
    override fun elementsIterator(): Iterator<Pair<IntArray, Int>> = INDArrayIntIterator(ndArray)
    override fun get(index: IntArray): Int = ndArray.getInt(*index)
    override fun set(index: IntArray, value: Int): Unit = run { ndArray.putScalar(index, value) }
    override fun toString(): String = "INDArrayIntStructure(ndArray=$ndArray)"
}

inline class INDArrayLongStructure(override val ndArray: INDArray) : INDArrayStructure<Long> {
    override fun elementsIterator(): Iterator<Pair<IntArray, Long>> = INDArrayLongIterator(ndArray)
    override fun get(index: IntArray): Long = ndArray.getLong(*widenToLongArray(index))
    override fun toString(): String = "INDArrayLongStructure(ndArray=$ndArray)"

}

inline class INDArrayDoubleStructure(override val ndArray: INDArray) : INDArrayStructure<Double>, MutableNDStructure<Double> {
    override fun elementsIterator(): Iterator<Pair<IntArray, Double>> = INDArrayDoubleIterator(ndArray)
    override fun get(index: IntArray): Double = ndArray.getDouble(*index)
    override fun set(index: IntArray, value: Double): Unit = run { ndArray.putScalar(index, value) }
    override fun toString(): String = "INDArrayDoubleStructure(ndArray=$ndArray)"
}

inline class INDArrayFloatStructure(override val ndArray: INDArray) : INDArrayStructure<Float>, MutableNDStructure<Float> {
    override fun elementsIterator(): Iterator<Pair<IntArray, Float>> = INDArrayFloatIterator(ndArray)
    override fun get(index: IntArray): Float = ndArray.getFloat(*index)
    override fun set(index: IntArray, value: Float): Unit = run { ndArray.putScalar(index, value) }
    override fun toString(): String = "INDArrayFloatStructure(ndArray=$ndArray)"
}
