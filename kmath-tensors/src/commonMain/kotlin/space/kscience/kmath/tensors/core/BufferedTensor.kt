package space.kscience.kmath.tensors.core

import space.kscience.kmath.structures.*
import space.kscience.kmath.tensors.api.Tensor
import space.kscience.kmath.tensors.core.algebras.TensorLinearStructure

/**
 * [Tensor] implementation provided with [MutableBuffer]
 */
public open class BufferedTensor<T> internal constructor(
    override val shape: IntArray,
    internal val mutableBuffer: MutableBuffer<T>,
    internal val bufferStart: Int
) : Tensor<T> {

    /**
     * [TensorLinearStructure] with the same shape
     */
    public val linearStructure: TensorLinearStructure
        get() = TensorLinearStructure(shape)

    /**
     * Number of elements in tensor
     */
    public val numElements: Int
        get() = linearStructure.linearSize

    /**
     * @param index [IntArray] with size equal to tensor dimension
     * @return the element by multidimensional index
     */
    override fun get(index: IntArray): T = mutableBuffer[bufferStart + linearStructure.offset(index)]

    /**
     * @param index the [IntArray] with size equal to tensor dimension
     * @param value the value to set
     */
    override fun set(index: IntArray, value: T) {
        mutableBuffer[bufferStart + linearStructure.offset(index)] = value
    }

    /**
     * @return the sequence of pairs multidimensional indices and values
     */
    override fun elements(): Sequence<Pair<IntArray, T>> = linearStructure.indices().map {
        it to this[it]
    }
}
