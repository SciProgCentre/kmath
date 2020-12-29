package kscience.kmath.torch

import kscience.kmath.structures.Strides

import kotlinx.cinterop.*
import ctorch.*

public class TorchTensorStrides internal constructor(
    override val shape: IntArray,
    override val strides: IntArray,
    override val linearSize: Int
) : Strides {
    override fun index(offset: Int): IntArray {
        val nDim = shape.size
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
}


private inline fun intPointerToArrayAndClean(ptr: CPointer<IntVar>, nDim: Int): IntArray {
    val res: IntArray = (1 .. nDim).map{ptr[it-1]}.toIntArray()
    dispose_int_array(ptr)
    return res
}

private inline fun getShapeFromNative(tensorHandle: COpaquePointer, nDim: Int): IntArray{
    return intPointerToArrayAndClean(get_shape(tensorHandle)!!, nDim)
}

private inline fun getStridesFromNative(tensorHandle: COpaquePointer, nDim: Int): IntArray{
    return intPointerToArrayAndClean(get_strides(tensorHandle)!!, nDim)
}

internal inline fun populateStridesFromNative(
    tensorHandle: COpaquePointer,
    rawShape: IntArray? = null,
    rawStrides: IntArray? = null,
    rawLinearSize: Int? = null
): TorchTensorStrides {
    val nDim = rawShape?.size?: rawStrides?.size?: get_dim(tensorHandle)
    return TorchTensorStrides(
        shape = rawShape?: getShapeFromNative(tensorHandle, nDim),
        strides = rawStrides?: getStridesFromNative(tensorHandle, nDim),
        linearSize = rawLinearSize?: get_numel(tensorHandle)
    )
}