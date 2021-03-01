@file:Suppress("NOTHING_TO_INLINE")

package space.kscience.kmath.torch

import space.kscience.kmath.tensors.TensorStructure

public interface TorchTensor<T> : TensorStructure<T> {
    public fun item(): T
    public val strides: IntArray
    public val size: Int
    public val device: space.kscience.kmath.torch.Device
    override fun value(): T {
        checkIsValue()
        return item()
    }
    override fun elements(): Sequence<Pair<IntArray, T>> {
        if (dimension == 0) {
            return emptySequence()
        }
        val indices = (1..size).asSequence().map { indexFromOffset(it - 1, strides, dimension) }
        return indices.map { it to get(it) }
    }
}

public inline fun <T> TorchTensor<T>.isValue(): Boolean {
    return (dimension == 0)
}

public inline fun <T> TorchTensor<T>.isNotValue(): Boolean = !this.isValue()

public inline fun <T> TorchTensor<T>.checkIsValue(): Unit = check(this.isValue()) {
    "This tensor has shape ${shape.toList()}"
}

public interface TorchTensorOverField<T>: TorchTensor<T>
{
    public var requiresGrad: Boolean
}

private inline fun indexFromOffset(offset: Int, strides: IntArray, nDim: Int): IntArray {
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

