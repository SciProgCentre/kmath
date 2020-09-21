package kscience.kmath.nd4j

import org.nd4j.linalg.api.ndarray.INDArray
import kscience.kmath.structures.MutableNDStructure
import kscience.kmath.structures.NDStructure

/**
 * Represents a [NDStructure] wrapping an [INDArray] object.
 *
 * @param T the type of items.
 */
public sealed class INDArrayStructure<T> : MutableNDStructure<T> {
    /**
     * The wrapped [INDArray].
     */
    public abstract val ndArray: INDArray

    public override val shape: IntArray
        get() = ndArray.shape().toIntArray()

    internal abstract fun elementsIterator(): Iterator<Pair<IntArray, T>>
    internal fun indicesIterator(): Iterator<IntArray> = ndArray.indicesIterator()
    public override fun elements(): Sequence<Pair<IntArray, T>> = Sequence(::elementsIterator)
}

private data class INDArrayIntStructure(override val ndArray: INDArray) : INDArrayStructure<Int>() {
    override fun elementsIterator(): Iterator<Pair<IntArray, Int>> = ndArray.intIterator()
    override fun get(index: IntArray): Int = ndArray.getInt(*index)
    override fun set(index: IntArray, value: Int): Unit = run { ndArray.putScalar(index, value) }
}

/**
 * Wraps this [INDArray] to [INDArrayStructure].
 */
public fun INDArray.asIntStructure(): INDArrayStructure<Int> = INDArrayIntStructure(this)

private data class INDArrayLongStructure(override val ndArray: INDArray) : INDArrayStructure<Long>() {
    override fun elementsIterator(): Iterator<Pair<IntArray, Long>> = ndArray.longIterator()
    override fun get(index: IntArray): Long = ndArray.getLong(*index.toLongArray())
    override fun set(index: IntArray, value: Long): Unit = run { ndArray.putScalar(index, value.toDouble()) }
}

/**
 * Wraps this [INDArray] to [INDArrayStructure].
 */
public fun INDArray.asLongStructure(): INDArrayStructure<Long> = INDArrayLongStructure(this)

private data class INDArrayRealStructure(override val ndArray: INDArray) : INDArrayStructure<Double>() {
    override fun elementsIterator(): Iterator<Pair<IntArray, Double>> = ndArray.realIterator()
    override fun get(index: IntArray): Double = ndArray.getDouble(*index)
    override fun set(index: IntArray, value: Double): Unit = run { ndArray.putScalar(index, value) }
}

/**
 * Wraps this [INDArray] to [INDArrayStructure].
 */
public fun INDArray.asRealStructure(): INDArrayStructure<Double> = INDArrayRealStructure(this)

private data class INDArrayFloatStructure(override val ndArray: INDArray) : INDArrayStructure<Float>() {
    override fun elementsIterator(): Iterator<Pair<IntArray, Float>> = ndArray.floatIterator()
    override fun get(index: IntArray): Float = ndArray.getFloat(*index)
    override fun set(index: IntArray, value: Float): Unit = run { ndArray.putScalar(index, value) }
}

/**
 * Wraps this [INDArray] to [INDArrayStructure].
 */
public fun INDArray.asFloatStructure(): INDArrayStructure<Float> = INDArrayFloatStructure(this)
