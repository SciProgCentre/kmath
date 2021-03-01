package space.kscience.kmath.tensors

import space.kscience.kmath.nd.Strides
import space.kscience.kmath.nd.offsetFromIndex
import kotlin.math.max


inline public fun stridesFromShape(shape: IntArray): IntArray {
    val nDim = shape.size
    val res = IntArray(nDim)
    if (nDim == 0)
        return res

    var current = nDim - 1
    res[current] = 1

    while (current > 0) {
        res[current - 1] = max(1, shape[current]) * res[current]
        current--
    }
    return res

}

inline public fun indexFromOffset(offset: Int, strides: IntArray, nDim: Int): IntArray {
    val res = IntArray(nDim)
    var current = offset
    var strideIndex = 0

    while (strideIndex < nDim) {
        res[strideIndex] = (current / strides[strideIndex])
        current %= strides[strideIndex]
        strideIndex++
    }
    return res
}


public class TensorStrides(override val shape: IntArray) : Strides {
    override val strides: IntArray
        get() = stridesFromShape(shape)

    override fun offset(index: IntArray): Int = offsetFromIndex(index, shape, strides)

    override fun index(offset: Int): IntArray =
        indexFromOffset(offset, strides, shape.size)

    override val linearSize: Int
        get() = shape.fold(1) { acc, i -> acc * i }
}