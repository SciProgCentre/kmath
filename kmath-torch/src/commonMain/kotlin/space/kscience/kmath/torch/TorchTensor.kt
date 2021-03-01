@file:Suppress("NOTHING_TO_INLINE")

package space.kscience.kmath.torch

import space.kscience.kmath.tensors.*

public interface TorchTensor<T> : TensorStructure<T> {

    public val strides: IntArray
    public val size: Int
    public val device: Device

    override fun elements(): Sequence<Pair<IntArray, T>> {
        if (dimension == 0) {
            return emptySequence()
        }
        val indices = (1..size).asSequence().map { indexFromOffset(it - 1, strides, dimension) }
        return indices.map { it to get(it) }
    }
}

public interface TorchTensorOverField<T>: TorchTensor<T>
{
    public var requiresGrad: Boolean
}

