package kscience.kmath.torch


import kscience.kmath.structures.TensorStructure
import kscience.kmath.memory.DeferScope

import kotlinx.cinterop.*
import kscience.kmath.ctorch.*


public sealed class TorchTensor<T> constructor(
    public val scope: DeferScope,
    internal val tensorHandle: COpaquePointer
) : TensorStructure<T>() {
    init {
        scope.defer(::close)
    }

    private fun close(): Unit = dispose_tensor(tensorHandle)

    protected abstract fun item(): T

    override val dimension: Int get() = get_dim(tensorHandle)
    override val shape: IntArray
        get() = (1..dimension).map { get_shape_at(tensorHandle, it - 1) }.toIntArray()
    public val strides: IntArray
        get() = (1..dimension).map { get_stride_at(tensorHandle, it - 1) }.toIntArray()
    public val size: Int get() = get_numel(tensorHandle)
    public val device: Device get() = Device.fromInt(get_device(tensorHandle))

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

    public inline fun isValue(): Boolean = dimension == 0
    public inline fun isNotValue(): Boolean = !isValue()
    internal inline fun checkIsValue() = check(isValue()) {
        "This tensor has shape ${shape.toList()}"
    }

    override fun value(): T {
        checkIsValue()
        return item()
    }

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

public sealed class TorchTensorOverField<T> constructor(
    scope: DeferScope,
    tensorHandle: COpaquePointer
) : TorchTensor<T>(scope, tensorHandle) {
    public var requiresGrad: Boolean
        get() = requires_grad(tensorHandle)
        set(value) = requires_grad_(tensorHandle, value)
}


public class TorchTensorReal internal constructor(
    scope: DeferScope,
    tensorHandle: COpaquePointer
) : TorchTensorOverField<Double>(scope, tensorHandle) {
    override fun item(): Double = get_item_double(tensorHandle)
    override fun get(index: IntArray): Double = get_double(tensorHandle, index.toCValues())
    override fun set(index: IntArray, value: Double) {
        set_double(tensorHandle, index.toCValues(), value)
    }
}

public class TorchTensorFloat internal constructor(
    scope: DeferScope,
    tensorHandle: COpaquePointer
) : TorchTensorOverField<Float>(scope, tensorHandle) {
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
