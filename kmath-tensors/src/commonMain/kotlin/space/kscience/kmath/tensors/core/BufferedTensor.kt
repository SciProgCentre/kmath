package space.kscience.kmath.tensors.core

import space.kscience.kmath.nd.MutableBufferND
import space.kscience.kmath.structures.*
import space.kscience.kmath.tensors.api.TensorStructure
import space.kscience.kmath.tensors.core.algebras.TensorLinearStructure


public open class BufferedTensor<T>(
    override val shape: IntArray,
    internal val buffer: MutableBuffer<T>,
    internal val bufferStart: Int
) : TensorStructure<T> {
    public val linearStructure: TensorLinearStructure
        get() = TensorLinearStructure(shape)

    public val numElements: Int
        get() = linearStructure.size

    override fun get(index: IntArray): T = buffer[bufferStart + linearStructure.offset(index)]

    override fun set(index: IntArray, value: T) {
        buffer[bufferStart + linearStructure.offset(index)] = value
    }

    override fun elements(): Sequence<Pair<IntArray, T>> = linearStructure.indices().map {
        it to this[it]
    }

    override fun equals(other: Any?): Boolean = false

    override fun hashCode(): Int = 0

}

public class IntTensor internal constructor(
    shape: IntArray,
    buffer: IntArray,
    offset: Int = 0
) : BufferedTensor<Int>(shape, IntBuffer(buffer), offset)
{
    internal constructor(bufferedTensor: BufferedTensor<Int>):
            this(bufferedTensor.shape, bufferedTensor.buffer.array(), bufferedTensor.bufferStart)
}

public class DoubleTensor internal constructor(
    shape: IntArray,
    buffer: DoubleArray,
    offset: Int = 0
) : BufferedTensor<Double>(shape, DoubleBuffer(buffer), offset)
{
    internal constructor(bufferedTensor: BufferedTensor<Double>):
            this(bufferedTensor.shape, bufferedTensor.buffer.array(), bufferedTensor.bufferStart)

    override fun toString(): String = toPrettyString()

}

internal inline fun BufferedTensor<Int>.asTensor(): IntTensor = IntTensor(this)
internal inline fun BufferedTensor<Double>.asTensor(): DoubleTensor = DoubleTensor(this)

internal inline fun <T> TensorStructure<T>.toBufferedTensor(): BufferedTensor<T> = when (this) {
    is BufferedTensor<T> -> this
    is MutableBufferND<T> -> BufferedTensor(this.shape, this.mutableBuffer, 0)
    else -> BufferedTensor(this.shape, this.elements().map{ it.second }.toMutableList().asMutableBuffer(), 0)
}

internal val TensorStructure<Double>.tensor: DoubleTensor
    get() = when (this) {
        is DoubleTensor -> this
        else -> this.toBufferedTensor().asTensor()
    }

internal val TensorStructure<Int>.tensor: IntTensor
    get() = when (this) {
        is IntTensor -> this
        else -> this.toBufferedTensor().asTensor()
    }

