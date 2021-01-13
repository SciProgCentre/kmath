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

    public var requiresGrad: Boolean
        get() = requires_grad(tensorHandle)
        set(value) = requires_grad_(tensorHandle, value)

    public fun copyToDouble(): TorchTensorReal = TorchTensorReal(
        scope = scope,
        tensorHandle = copy_to_double(this.tensorHandle)!!
    )
    public fun copyToFloat(): TorchTensorFloat = TorchTensorFloat(
        scope = scope,
        tensorHandle = copy_to_float(this.tensorHandle)!!
    )
    public fun copyToLong(): TorchTensorLong = TorchTensorLong(
        scope = scope,
        tensorHandle = copy_to_long(this.tensorHandle)!!
    )
    public fun copyToInt(): TorchTensorInt = TorchTensorInt(
        scope = scope,
        tensorHandle = copy_to_int(this.tensorHandle)!!
    )

}

public class TorchTensorReal internal constructor(
    scope: DeferScope,
    tensorHandle: COpaquePointer
) : TorchTensor<Double>(scope, tensorHandle) {
    override fun item(): Double = get_item_double(tensorHandle)
    override fun get(index: IntArray): Double = get_double(tensorHandle, index.toCValues())
    override fun set(index: IntArray, value: Double) {
        set_double(tensorHandle, index.toCValues(), value)
    }
}

public class TorchTensorFloat internal constructor(
    scope: DeferScope,
    tensorHandle: COpaquePointer
) : TorchTensor<Float>(scope, tensorHandle) {
    override fun item(): Float = get_item_float(tensorHandle)
    override fun get(index: IntArray): Float = get_float(tensorHandle, index.toCValues())
    override fun set(index: IntArray, value: Float) {
        set_float(tensorHandle, index.toCValues(), value)
    }
}

public class TorchTensorLong internal constructor(
    scope: DeferScope,
    tensorHandle: COpaquePointer
) : TorchTensor<Long>(scope, tensorHandle) {
    override fun item(): Long = get_item_long(tensorHandle)
    override fun get(index: IntArray): Long = get_long(tensorHandle, index.toCValues())
    override fun set(index: IntArray, value: Long) {
        set_long(tensorHandle, index.toCValues(), value)
    }
}

public class TorchTensorInt internal constructor(
    scope: DeferScope,
    tensorHandle: COpaquePointer
) : TorchTensor<Int>(scope, tensorHandle) {
    override fun item(): Int = get_item_int(tensorHandle)
    override fun get(index: IntArray): Int = get_int(tensorHandle, index.toCValues())
    override fun set(index: IntArray, value: Int) {
        set_int(tensorHandle, index.toCValues(), value)
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
