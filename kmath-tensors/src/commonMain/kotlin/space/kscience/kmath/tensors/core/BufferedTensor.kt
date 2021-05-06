package space.kscience.kmath.tensors.core

import space.kscience.kmath.nd.MutableBufferND
import space.kscience.kmath.structures.*
import space.kscience.kmath.tensors.api.Tensor
import space.kscience.kmath.tensors.core.algebras.TensorLinearStructure


public open class BufferedTensor<T>(
    override val shape: IntArray,
    internal val mutableBuffer: MutableBuffer<T>,
    internal val bufferStart: Int
) : Tensor<T> {
    public val linearStructure: TensorLinearStructure
        get() = TensorLinearStructure(shape)

    public val numElements: Int
        get() = linearStructure.linearSize

    override fun get(index: IntArray): T = mutableBuffer[bufferStart + linearStructure.offset(index)]

    override fun set(index: IntArray, value: T) {
        mutableBuffer[bufferStart + linearStructure.offset(index)] = value
    }

    override fun elements(): Sequence<Pair<IntArray, T>> = linearStructure.indices().map {
        it to this[it]
    }
}

public class IntTensor internal constructor(
    shape: IntArray,
    buffer: IntArray,
    offset: Int = 0
) : BufferedTensor<Int>(shape, IntBuffer(buffer), offset)

public class DoubleTensor internal constructor(
    shape: IntArray,
    buffer: DoubleArray,
    offset: Int = 0
) : BufferedTensor<Double>(shape, DoubleBuffer(buffer), offset) {
    override fun toString(): String = toPrettyString()
}

internal fun BufferedTensor<Int>.asTensor(): IntTensor =
    IntTensor(this.shape, this.mutableBuffer.array(), this.bufferStart)

internal fun BufferedTensor<Double>.asTensor(): DoubleTensor =
    DoubleTensor(this.shape, this.mutableBuffer.array(), this.bufferStart)

internal fun <T> Tensor<T>.copyToBufferedTensor(): BufferedTensor<T> =
    BufferedTensor(
        this.shape,
        TensorLinearStructure(this.shape).indices().map(this::get).toMutableList().asMutableBuffer(), 0
    )

internal fun <T> Tensor<T>.toBufferedTensor(): BufferedTensor<T> = when (this) {
    is BufferedTensor<T> -> this
    is MutableBufferND<T> -> if (this.strides.strides contentEquals TensorLinearStructure(this.shape).strides)
        BufferedTensor(this.shape, this.mutableBuffer, 0) else this.copyToBufferedTensor()
    else -> this.copyToBufferedTensor()
}

internal val Tensor<Double>.tensor: DoubleTensor
    get() = when (this) {
        is DoubleTensor -> this
        else -> this.toBufferedTensor().asTensor()
    }

internal val Tensor<Int>.tensor: IntTensor
    get() = when (this) {
        is IntTensor -> this
        else -> this.toBufferedTensor().asTensor()
    }

public fun Tensor<Double>.toDoubleTensor(): DoubleTensor = this.tensor
public fun Tensor<Int>.toIntTensor(): IntTensor = this.tensor

public fun Array<DoubleArray>.toDoubleTensor(): DoubleTensor {
    val n = size
    check(n > 0) { "An empty array cannot be casted to tensor" }
    val m = first().size
    check(m > 0) { "Inner arrays must have at least 1 argument" }
    check(all { size == m }) { "Inner arrays must be the same size" }

    val shape = intArrayOf(n, m)
    val buffer = this.flatMap { arr -> arr.map { it } }.toDoubleArray()

    return DoubleTensor(shape, buffer, 0)
}


public fun Array<IntArray>.toIntTensor(): IntTensor {
    val n = size
    check(n > 0) { "An empty array cannot be casted to tensor" }
    val m = first().size
    check(m > 0) { "Inner arrays must have at least 1 argument" }
    check(all { size == m }) { "Inner arrays must be the same size" }

    val shape = intArrayOf(n, m)
    val buffer = this.flatMap { arr -> arr.map { it } }.toIntArray()

    return IntTensor(shape, buffer, 0)
}

public fun DoubleTensor.toDoubleArray(): DoubleArray {
    return DoubleArray(numElements) { i ->
        mutableBuffer[bufferStart + i]
    }
}

public fun IntTensor.toIntArray(): IntArray {
    return IntArray(numElements) { i ->
        mutableBuffer[bufferStart + i]
    }
}