package space.kscience.kmath.torch

import space.kscience.kmath.memory.DeferScope

import kotlinx.cinterop.*
import space.kscience.kmath.torch.ctorch.*


public sealed class TorchTensorNative<T> constructor(
    scope: DeferScope,
    internal val tensorHandle: COpaquePointer
) : TorchTensor<T>, TorchTensorMemoryHolder(scope) {

    override fun close(): Unit = dispose_tensor(tensorHandle)

    override val dimension: Int get() = get_dim(tensorHandle)
    override val shape: IntArray
        get() = (1..dimension).map { get_shape_at(tensorHandle, it - 1) }.toIntArray()
    override val strides: IntArray
        get() = (1..dimension).map { get_stride_at(tensorHandle, it - 1) }.toIntArray()
    override val size: Int get() = get_numel(tensorHandle)
    override val device: Device get() = Device.fromInt(get_device(tensorHandle))

    override fun toString(): String {
        val nativeStringRepresentation: CPointer<ByteVar> = tensor_to_string(tensorHandle)!!
        val stringRepresentation = nativeStringRepresentation.toKString()
        dispose_char(nativeStringRepresentation)
        return stringRepresentation
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

public sealed class TorchTensorOverFieldNative<T> constructor(
    scope: DeferScope,
    tensorHandle: COpaquePointer
) : TorchTensorNative<T>(scope, tensorHandle), TorchTensorOverField<T> {
    override var requiresGrad: Boolean
        get() = requires_grad(tensorHandle)
        set(value) = requires_grad_(tensorHandle, value)
}


public class TorchTensorReal internal constructor(
    scope: DeferScope,
    tensorHandle: COpaquePointer
) : TorchTensorOverFieldNative<Double>(scope, tensorHandle) {
    override fun item(): Double = get_item_double(tensorHandle)
    override fun get(index: IntArray): Double = get_double(tensorHandle, index.toCValues())
    override fun set(index: IntArray, value: Double) {
        set_double(tensorHandle, index.toCValues(), value)
    }
}

public class TorchTensorFloat internal constructor(
    scope: DeferScope,
    tensorHandle: COpaquePointer
) : TorchTensorOverFieldNative<Float>(scope, tensorHandle) {
    override fun item(): Float = get_item_float(tensorHandle)
    override fun get(index: IntArray): Float = get_float(tensorHandle, index.toCValues())
    override fun set(index: IntArray, value: Float) {
        set_float(tensorHandle, index.toCValues(), value)
    }
}

public class TorchTensorLong internal constructor(
    scope: DeferScope,
    tensorHandle: COpaquePointer
) : TorchTensorNative<Long>(scope, tensorHandle) {
    override fun item(): Long = get_item_long(tensorHandle)
    override fun get(index: IntArray): Long = get_long(tensorHandle, index.toCValues())
    override fun set(index: IntArray, value: Long) {
        set_long(tensorHandle, index.toCValues(), value)
    }
}

public class TorchTensorInt internal constructor(
    scope: DeferScope,
    tensorHandle: COpaquePointer
) : TorchTensorNative<Int>(scope, tensorHandle) {
    override fun item(): Int = get_item_int(tensorHandle)
    override fun get(index: IntArray): Int = get_int(tensorHandle, index.toCValues())
    override fun set(index: IntArray, value: Int) {
        set_int(tensorHandle, index.toCValues(), value)
    }
}
