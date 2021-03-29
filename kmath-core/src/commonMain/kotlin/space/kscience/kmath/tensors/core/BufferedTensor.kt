package space.kscience.kmath.tensors.core

import space.kscience.kmath.nd.MutableStructure1D
import space.kscience.kmath.nd.MutableStructure2D
import space.kscience.kmath.structures.*
import space.kscience.kmath.tensors.TensorStructure



public open class BufferedTensor<T>(
    override val shape: IntArray,
    public val buffer: MutableBuffer<T>,
    internal val bufferStart: Int
) : TensorStructure<T> {
    public val linearStructure: TensorLinearStructure
        get() = TensorLinearStructure(shape)

    public val numel: Int
        get() = linearStructure.size

    internal constructor(tensor: BufferedTensor<T>) :
            this(tensor.shape, tensor.buffer, tensor.bufferStart)

    override fun get(index: IntArray): T = buffer[bufferStart + linearStructure.offset(index)]

    override fun set(index: IntArray, value: T) {
        buffer[bufferStart + linearStructure.offset(index)] = value
    }

    override fun elements(): Sequence<Pair<IntArray, T>> = linearStructure.indices().map {
        it to this[it]
    }

    override fun equals(other: Any?): Boolean = false

    override fun hashCode(): Int = 0

    internal fun vectorSequence(): Sequence<BufferedTensor<T>> = sequence {
        val n = shape.size
        val vectorOffset = shape[n - 1]
        val vectorShape = intArrayOf(shape.last())
        for (offset in 0 until numel step vectorOffset) {
            val vector = BufferedTensor(vectorShape, buffer, offset)
            yield(vector)
        }
    }

    internal fun matrixSequence(): Sequence<BufferedTensor<T>> = sequence {
        check(shape.size >= 2) { "todo" }
        val n = shape.size
        val matrixOffset = shape[n - 1] * shape[n - 2]
        val matrixShape = intArrayOf(shape[n - 2], shape[n - 1])
        for (offset in 0 until numel step matrixOffset) {
            val matrix = BufferedTensor(matrixShape, buffer, offset)
            yield(matrix)
        }
    }

    internal inline fun forEachVector(vectorAction: (BufferedTensor<T>) -> Unit): Unit {
        for (vector in vectorSequence()) {
            vectorAction(vector)
        }
    }

    internal inline fun forEachMatrix(matrixAction: (BufferedTensor<T>) -> Unit): Unit {
        for (matrix in matrixSequence()) {
            matrixAction(matrix)
        }
    }

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

public class LongTensor internal constructor(
    shape: IntArray,
    buffer: LongArray,
    offset: Int = 0
) : BufferedTensor<Long>(shape, LongBuffer(buffer), offset)
{
    internal constructor(bufferedTensor: BufferedTensor<Long>):
            this(bufferedTensor.shape, bufferedTensor.buffer.array(), bufferedTensor.bufferStart)
}

public class FloatTensor internal constructor(
    shape: IntArray,
    buffer: FloatArray,
    offset: Int = 0
) : BufferedTensor<Float>(shape, FloatBuffer(buffer), offset)
{
    internal constructor(bufferedTensor: BufferedTensor<Float>):
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
}

internal fun BufferedTensor<Int>.asTensor(): IntTensor = IntTensor(this)
internal fun BufferedTensor<Long>.asTensor(): LongTensor = LongTensor(this)
internal fun BufferedTensor<Float>.asTensor(): FloatTensor = FloatTensor(this)
internal fun BufferedTensor<Double>.asTensor(): DoubleTensor = DoubleTensor(this)
