package space.kscience.kmath.tensors

import space.kscience.kmath.structures.*


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

}

/*
//todo make generator mb nextMatrixIndex?
public class InnerMatrix<T>(private val tensor: BufferedTensor<T>){
    private var offset: Int = 0
    private val n : Int = tensor.shape.size
    //stride?
    private val step = tensor.shape[n - 1] * tensor.shape[n - 2]

    public operator fun get(i: Int, j: Int): T {
        val index = tensor.strides.index(offset)
        index[n - 2] = i
        index[n - 1] = j
        return tensor[index]
    }

    public operator fun set(i: Int, j: Int, value: T): Unit {
        val index = tensor.strides.index(offset)
        index[n - 2] = i
        index[n - 1] = j
        tensor[index] = value
    }

    public fun makeStep(){
        offset += step
    }
}

public class InnerVector<T>(private val tensor: BufferedTensor<T>){
    private var offset: Int = 0
    private val n : Int = tensor.shape.size
    //stride?
    private val step = tensor.shape[n - 1]

    public operator fun get(i: Int): T {
        val index = tensor.strides.index(offset)
        index[n - 1] = i
        return tensor[index]
    }

    public operator fun set(i: Int, value: T): Unit {
        val index = tensor.strides.index(offset)
        index[n - 1] = i
        tensor[index] = value
    }

    public fun makeStep(){
        offset += step
    }
}
//todo default buffer = arrayOf(0)???
 */

public class IntTensor(
    shape: IntArray,
    buffer: IntArray,
    offset: Int = 0
) : BufferedTensor<Int>(shape, IntBuffer(buffer), offset)

public class LongTensor(
    shape: IntArray,
    buffer: LongArray,
    offset: Int = 0
) : BufferedTensor<Long>(shape, LongBuffer(buffer), offset)

public class FloatTensor(
    shape: IntArray,
    buffer: FloatArray,
    offset: Int = 0
) : BufferedTensor<Float>(shape, FloatBuffer(buffer), offset)

public class DoubleTensor(
    shape: IntArray,
    buffer: DoubleArray,
    offset: Int = 0
) : BufferedTensor<Double>(shape, RealBuffer(buffer), offset)