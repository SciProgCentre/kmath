package kscience.kmath.torch

import kscience.kmath.structures.MutableNDStructure

import kotlinx.cinterop.*
import kscience.kmath.ctorch.*


public sealed class TorchTensor<T> constructor(
    internal val scope: DeferScope,
    internal val tensorHandle: COpaquePointer
) : MutableNDStructure<T> {
    init {
        scope.defer(::close)
    }
    private fun close(): Unit = dispose_tensor(tensorHandle)

    protected abstract fun item(): T
    internal abstract fun wrap(outScope: DeferScope, outTensorHandle: COpaquePointer): TorchTensor<T>

    override val dimension: Int get() = get_dim(tensorHandle)
    override val shape: IntArray
        get() = (1..dimension).map{get_shape_at(tensorHandle, it-1)}.toIntArray()
    public val strides: IntArray
        get() = (1..dimension).map{get_stride_at(tensorHandle, it-1)}.toIntArray()
    public val size: Int get() = get_numel(tensorHandle)
    public val device: TorchDevice get() = TorchDevice.fromInt(get_device(tensorHandle))

    override fun equals(other: Any?): Boolean = false
    override fun hashCode(): Int = 0
    override fun toString(): String {
        val nativeStringRepresentation: CPointer<ByteVar> = tensor_to_string(tensorHandle)!!
        val stringRepresentation = nativeStringRepresentation.toKString()
        dispose_char(nativeStringRepresentation)
        return stringRepresentation
    }

    override fun elements(): Sequence<Pair<IntArray, T>> {
        if (dimension == 0) {
            return emptySequence()
        }
        val indices = (1..size).asSequence().map { indexFromOffset(it - 1, strides, dimension) }
        return indices.map { it to get(it) }
    }

    public fun value(): T {
        check(dimension == 0) {
            "This tensor has shape ${shape.toList()}"
        }
        return item()
    }

    public fun copy(): TorchTensor<T> =
        wrap(
            outScope = scope,
            outTensorHandle = copy_tensor(tensorHandle)!!
        )

    public fun copyToDevice(device: TorchDevice): TorchTensor<T> =
        wrap(
            outScope = scope,
            outTensorHandle = copy_to_device(tensorHandle, device.toInt())!!
        )

    public var requiresGrad: Boolean
        get() = requires_grad(tensorHandle)
        set(value) = requires_grad_(tensorHandle, value)

    public fun detachFromGraph(): Unit {
        detach_from_graph(tensorHandle)
    }
}

public class TorchTensorReal internal constructor(
    scope: DeferScope,
    tensorHandle: COpaquePointer
) : TorchTensor<Double>(scope, tensorHandle) {
    override fun item(): Double = get_item_double(tensorHandle)
    override fun wrap(outScope: DeferScope, outTensorHandle: COpaquePointer
    ): TorchTensorReal = TorchTensorReal(scope = outScope, tensorHandle = outTensorHandle)

    override fun get(index: IntArray): Double = get_double(tensorHandle, index.toCValues())
    override fun set(index: IntArray, value: Double) {
        set_double(tensorHandle, index.toCValues(), value)
    }
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
