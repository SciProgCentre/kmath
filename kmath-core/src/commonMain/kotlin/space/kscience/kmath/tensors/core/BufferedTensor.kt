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

    public fun vectorSequence(): Sequence<BufferedTensor1D<T>> = sequence {
        check(shape.size >= 1) { "todo" }
        val n = shape.size
        val vectorOffset = shape[n - 1]
        val vectorShape = intArrayOf(shape.last())
        for (offset in 0 until numel step vectorOffset) {
            val vector = BufferedTensor<T>(vectorShape, buffer, offset).as1D()
            yield(vector)
        }
    }

    public fun matrixSequence(): Sequence<BufferedTensor2D<T>> = sequence {
        check(shape.size >= 2) { "todo" }
        val n = shape.size
        val matrixOffset = shape[n - 1] * shape[n - 2]
        val matrixShape = intArrayOf(shape[n - 2], shape[n - 1]) //todo better way?
        for (offset in 0 until numel step matrixOffset) {
            val matrix = BufferedTensor<T>(matrixShape, buffer, offset).as2D()
            yield(matrix)
        }
    }

    public inline fun forEachVector(vectorAction: (BufferedTensor1D<T>) -> Unit): Unit {
        for (vector in vectorSequence()) {
            vectorAction(vector)
        }
    }

    public inline fun forEachMatrix(matrixAction: (BufferedTensor2D<T>) -> Unit): Unit {
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


public class BufferedTensor2D<T> internal constructor(
    private val tensor: BufferedTensor<T>,
) : BufferedTensor<T>(tensor), MutableStructure2D<T> {
    init {
        check(shape.size == 2) {
            "Shape ${shape.toList()} not compatible with DoubleTensor2D"
        }
    }

    override val shape: IntArray
        get() = tensor.shape

    override val rowNum: Int
        get() = shape[0]
    override val colNum: Int
        get() = shape[1]

    override fun get(i: Int, j: Int): T = tensor[intArrayOf(i, j)]

    override fun get(index: IntArray): T = tensor[index]

    override fun elements(): Sequence<Pair<IntArray, T>> = tensor.elements()

    override fun set(i: Int, j: Int, value: T) {
        tensor[intArrayOf(i, j)] = value
    }

    override val rows: List<BufferedTensor1D<T>>
        get() = List(rowNum) { i ->
            BufferedTensor1D(
                BufferedTensor(
                    shape = intArrayOf(colNum),
                    buffer = VirtualMutableBuffer(colNum) { j -> get(i, j) },
                    bufferStart = 0
                )
            )
        }

    override val columns: List<BufferedTensor1D<T>>
        get() = List(colNum) { j ->
            BufferedTensor1D(
                BufferedTensor(
                    shape = intArrayOf(rowNum),
                    buffer = VirtualMutableBuffer(rowNum) { i -> get(i, j) },
                    bufferStart = 0
                )
            )
        }
}

public class BufferedTensor1D<T> internal constructor(
    private val tensor: BufferedTensor<T>
) : BufferedTensor<T>(tensor), MutableStructure1D<T> {
    init {
        check(shape.size == 1) {
            "Shape ${shape.toList()} not compatible with DoubleTensor1D"
        }
    }

    override fun get(index: IntArray): T = tensor[index]

    override fun set(index: IntArray, value: T) {
        tensor[index] = value
    }

    override val size: Int
        get() = tensor.linearStructure.size

    override fun get(index: Int): T = tensor[intArrayOf(index)]

    override fun set(index: Int, value: T) {
        tensor[intArrayOf(index)] = value
    }

    override fun copy(): MutableBuffer<T> = tensor.buffer.copy()

}

internal fun BufferedTensor<Int>.asIntTensor(): IntTensor = IntTensor(this)
internal fun BufferedTensor<Long>.asLongTensor(): LongTensor = LongTensor(this)
internal fun BufferedTensor<Float>.asFloatTensor(): FloatTensor = FloatTensor(this)
internal fun BufferedTensor<Double>.asDoubleTensor(): DoubleTensor = DoubleTensor(this)


public fun <T> BufferedTensor<T>.as2D(): BufferedTensor2D<T> = BufferedTensor2D(this)
public fun <T> BufferedTensor<T>.as1D(): BufferedTensor1D<T> = BufferedTensor1D(this)