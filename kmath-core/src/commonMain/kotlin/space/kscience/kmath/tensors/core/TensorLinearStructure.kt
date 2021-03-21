package space.kscience.kmath.tensors.core

import kotlin.math.max


internal inline fun offsetFromIndex(index: IntArray, shape: IntArray, strides: IntArray): Int =
    index.mapIndexed { i, value ->
        if (value < 0 || value >= shape[i]) throw IndexOutOfBoundsException("Index $value out of shape bounds: (0,${shape[i]})")
        value * strides[i]
    }.sum()

internal inline fun stridesFromShape(shape: IntArray): IntArray {
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

internal inline fun indexFromOffset(offset: Int, strides: IntArray, nDim: Int): IntArray {
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

internal inline fun stepIndex(index: IntArray, shape: IntArray, nDim: Int): IntArray {
    val res = index.copyOf()
    var current = nDim - 1
    var carry = 0

    do {
        res[current]++
        if (res[current] >= shape[current]) {
            carry = 1
            res[current] = 0
        }
        current--
    } while (carry != 0 && current >= 0)

    return res
}


public class TensorLinearStructure(public val shape: IntArray)
{
    public val strides: IntArray
        get() = stridesFromShape(shape)

    public fun offset(index: IntArray): Int = offsetFromIndex(index, shape, strides)

    public fun index(offset: Int): IntArray =
        indexFromOffset(offset, strides, shape.size)

    public fun stepIndex(index: IntArray): IntArray =
        stepIndex(index, shape, shape.size)

    public val size: Int
        get() = shape.reduce(Int::times)

    public fun indices(): Sequence<IntArray> = (0 until size).asSequence().map {
        index(it)
    }
}