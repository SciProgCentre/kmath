package space.kscience.kmath.tensors.core

import space.kscience.kmath.linear.Matrix
import space.kscience.kmath.nd.*
import space.kscience.kmath.structures.*
import space.kscience.kmath.tensors.TensorStrides
import space.kscience.kmath.tensors.TensorStructure


public open class BufferedTensor<T>(
    override val shape: IntArray,
    public val buffer: MutableBuffer<T>,
    internal val bufferStart: Int
) : TensorStructure<T>
{
    public val strides: TensorStrides
        get() = TensorStrides(shape)

    public val numel: Int
        get() = strides.linearSize

    override fun get(index: IntArray): T = buffer[bufferStart + strides.offset(index)]

    override fun set(index: IntArray, value: T) {
        buffer[bufferStart + strides.offset(index)] = value
    }

    override fun elements(): Sequence<Pair<IntArray, T>> = strides.indices().map {
        it to this[it]
    }

    override fun equals(other: Any?): Boolean = false

    override fun hashCode(): Int = 0

    // todo rename to vector
    public inline fun forEachVector(vectorAction : (MutableStructure1D<T>) -> Unit): Unit {
        check(shape.size >= 1) {"todo"}
        val vectorOffset = strides.strides[0]
        val vectorShape = intArrayOf(shape.last())
        for (offset in 0 until numel step vectorOffset) {
            val vector = BufferedTensor<T>(vectorShape, buffer, offset).as1D()
            vectorAction(vector)
        }
    }

    public inline fun forEachMatrix(matrixAction : (MutableStructure2D<T>) -> Unit): Unit {
        check(shape.size >= 2) {"todo"}
        val matrixOffset = strides.strides[1]
        val matrixShape = intArrayOf(shape[shape.size - 2], shape.last()) //todo better way?
        for (offset in 0 until numel step matrixOffset) {
            val matrix = BufferedTensor<T>(matrixShape, buffer, offset).as2D()
            matrixAction(matrix)
        }
    }
    // todo remove code copy-pasting

    public fun vectorSequence(): Sequence<MutableStructure1D<T>> = sequence {
        check(shape.size >= 1) {"todo"}
        val vectorOffset = strides.strides[0]
        val vectorShape = intArrayOf(shape.last())
        for (offset in 0 until numel step vectorOffset) {
            val vector = BufferedTensor<T>(vectorShape, buffer, offset).as1D()
            yield(vector)
        }
    }

    public fun matrixSequence(): Sequence<MutableStructure2D<T>> = sequence {
        check(shape.size >= 2) {"todo"}
        val matrixOffset = strides.strides[1]
        val matrixShape = intArrayOf(shape[shape.size - 2], shape.last()) //todo better way?
        for (offset in 0 until numel step matrixOffset) {
            val matrix = BufferedTensor<T>(matrixShape, buffer, offset).as2D()
            yield(matrix)
        }
    }

}


public class IntTensor internal constructor(
    shape: IntArray,
    buffer: IntArray,
    offset: Int = 0
) : BufferedTensor<Int>(shape, IntBuffer(buffer), offset)

public class LongTensor internal constructor(
    shape: IntArray,
    buffer: LongArray,
    offset: Int = 0
) : BufferedTensor<Long>(shape, LongBuffer(buffer), offset)

public class FloatTensor internal constructor(
    shape: IntArray,
    buffer: FloatArray,
    offset: Int = 0
) : BufferedTensor<Float>(shape, FloatBuffer(buffer), offset)

public class DoubleTensor internal constructor(
    shape: IntArray,
    buffer: DoubleArray,
    offset: Int = 0
) : BufferedTensor<Double>(shape, RealBuffer(buffer), offset)