package scientifik.kmath.nd4j

import org.nd4j.linalg.api.ndarray.INDArray
import scientifik.kmath.structures.MutableNDStructure
import scientifik.kmath.structures.NDStructure

interface INDArrayStructure<T> : NDStructure<T> {
    val ndArray: INDArray

    override val shape: IntArray
        get() = ndArray.shape().toIntArray()

    fun elementsIterator(): Iterator<Pair<IntArray, T>>
    override fun elements(): Sequence<Pair<IntArray, T>> = Sequence(::elementsIterator)
}

data class INDArrayIntStructure(override val ndArray: INDArray) : INDArrayStructure<Int>, MutableNDStructure<Int> {
    override fun elementsIterator(): Iterator<Pair<IntArray, Int>> = ndArray.intIterator()
    override fun get(index: IntArray): Int = ndArray.getInt(*index)
    override fun set(index: IntArray, value: Int): Unit = run { ndArray.putScalar(index, value) }
}

fun INDArray.asIntStructure(): INDArrayIntStructure = INDArrayIntStructure(this)

data class INDArrayLongStructure(override val ndArray: INDArray) : INDArrayStructure<Long> {
    override fun elementsIterator(): Iterator<Pair<IntArray, Long>> = ndArray.longIterator()
    override fun get(index: IntArray): Long = ndArray.getLong(*index.toLongArray())
}

fun INDArray.asLongStructure(): INDArrayLongStructure = INDArrayLongStructure(this)

data class INDArrayRealStructure(override val ndArray: INDArray) : INDArrayStructure<Double>,
    MutableNDStructure<Double> {
    override fun elementsIterator(): Iterator<Pair<IntArray, Double>> = ndArray.realIterator()
    override fun get(index: IntArray): Double = ndArray.getDouble(*index)
    override fun set(index: IntArray, value: Double): Unit = run { ndArray.putScalar(index, value) }
}

fun INDArray.asRealStructure(): INDArrayRealStructure = INDArrayRealStructure(this)

data class INDArrayFloatStructure(override val ndArray: INDArray) : INDArrayStructure<Float>,
    MutableNDStructure<Float> {
    override fun elementsIterator(): Iterator<Pair<IntArray, Float>> = ndArray.floatIterator()
    override fun get(index: IntArray): Float = ndArray.getFloat(*index)
    override fun set(index: IntArray, value: Float): Unit = run { ndArray.putScalar(index, value) }
}

fun INDArray.asFloatStructure(): INDArrayFloatStructure = INDArrayFloatStructure(this)
