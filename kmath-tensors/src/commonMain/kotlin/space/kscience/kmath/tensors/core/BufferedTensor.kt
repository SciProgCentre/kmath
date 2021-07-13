package space.kscience.kmath.tensors.core

import space.kscience.kmath.misc.PerformancePitfall
import space.kscience.kmath.nd.Strides
import space.kscience.kmath.structures.MutableBuffer
import space.kscience.kmath.tensors.api.Tensor
import space.kscience.kmath.tensors.core.internal.TensorLinearStructure

/**
 * Represents [Tensor] over a [MutableBuffer] intended to be used through [DoubleTensor] and [IntTensor]
 */
public open class BufferedTensor<T> internal constructor(
    override val shape: IntArray,
    @PublishedApi internal val mutableBuffer: MutableBuffer<T>,
    @PublishedApi internal val bufferStart: Int,
) : Tensor<T> {

    /**
     * Buffer strides based on [TensorLinearStructure] implementation
     */
    public val linearStructure: Strides
        get() = TensorLinearStructure(shape)

    /**
     * Number of elements in tensor
     */
    public val numElements: Int
        get() = linearStructure.linearSize

    override fun get(index: IntArray): T = mutableBuffer[bufferStart + linearStructure.offset(index)]

    override fun set(index: IntArray, value: T) {
        mutableBuffer[bufferStart + linearStructure.offset(index)] = value
    }

    @PerformancePitfall
    override fun elements(): Sequence<Pair<IntArray, T>> = linearStructure.indices().map {
        it to get(it)
    }
}
