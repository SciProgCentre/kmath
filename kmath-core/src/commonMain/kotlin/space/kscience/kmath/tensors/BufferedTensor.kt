package space.kscience.kmath.tensors

import space.kscience.kmath.linear.BufferMatrix
import space.kscience.kmath.nd.MutableNDBuffer
import space.kscience.kmath.structures.*
import space.kscience.kmath.structures.BufferAccessor2D

public open class BufferedTensor<T>(
    override val shape: IntArray,
    buffer: MutableBuffer<T>
) :
    TensorStructure<T>,
    MutableNDBuffer<T>(
        TensorStrides(shape),
        buffer
    ) {

    public operator fun get(i: Int, j: Int): T{
            check(this.dimension == 2) {"Not matrix"}
            return this[intArrayOf(i, j)]
    }

    public operator fun set(i: Int, j: Int, value: T): Unit{
        check(this.dimension == 2) {"Not matrix"}
        this[intArrayOf(i, j)] = value
    }

}

public class IntTensor(
    shape: IntArray,
    buffer: IntArray
) : BufferedTensor<Int>(shape, IntBuffer(buffer))
