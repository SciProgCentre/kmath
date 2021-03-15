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
    /*

    public operator fun get(i: Int, j: Int): T {
        check(this.dimension == 2) { "Not matrix" }
        return this[intArrayOf(i, j)]
    }

    public operator fun set(i: Int, j: Int, value: T): Unit {
        check(this.dimension == 2) { "Not matrix" }
        this[intArrayOf(i, j)] = value
    }

     */
}

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