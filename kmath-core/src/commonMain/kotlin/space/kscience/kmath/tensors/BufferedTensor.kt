package space.kscience.kmath.tensors

import space.kscience.kmath.nd.MutableNDBuffer
import space.kscience.kmath.structures.*


public open class BufferedTensor<T>(
    override val shape: IntArray,
    buffer: MutableBuffer<T>
) :
    TensorStructure<T>,
    MutableNDBuffer<T>(
        TensorStrides(shape),
        buffer
    ) {

    public operator fun get(i: Int, j: Int): T {
        check(this.dimension == 2) { "Not matrix" }
        return this[intArrayOf(i, j)]
    }

    public operator fun set(i: Int, j: Int, value: T): Unit {
        check(this.dimension == 2) { "Not matrix" }
        this[intArrayOf(i, j)] = value
    }

}

public class IntTensor(
    shape: IntArray,
    buffer: IntArray
) : BufferedTensor<Int>(shape, IntBuffer(buffer))

public class LongTensor(
    shape: IntArray,
    buffer: LongArray
) : BufferedTensor<Long>(shape, LongBuffer(buffer))

public class FloatTensor(
    shape: IntArray,
    buffer: FloatArray
) : BufferedTensor<Float>(shape, FloatBuffer(buffer))

public class RealTensor(
    shape: IntArray,
    buffer: DoubleArray
) : BufferedTensor<Double>(shape, RealBuffer(buffer))