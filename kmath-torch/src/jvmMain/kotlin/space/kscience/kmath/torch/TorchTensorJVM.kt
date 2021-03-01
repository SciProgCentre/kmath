package space.kscience.kmath.torch

import space.kscience.kmath.memory.DeferScope

public sealed class TorchTensorJVM<T> constructor(
    scope: DeferScope,
    internal val tensorHandle: Long
) : TorchTensor<T>, TorchTensorMemoryHolder(scope)
{
    override fun close(): Unit = JTorch.disposeTensor(tensorHandle)

    override val dimension: Int get() = JTorch.getDim(tensorHandle)
    override val shape: IntArray
        get() = (1..dimension).map { JTorch.getShapeAt(tensorHandle, it - 1) }.toIntArray()
    override val strides: IntArray
        get() = (1..dimension).map { JTorch.getStrideAt(tensorHandle, it - 1) }.toIntArray()
    override val size: Int get() = JTorch.getNumel(tensorHandle)
    override val device: Device get() = Device.fromInt(JTorch.getDevice(tensorHandle))

    override fun toString(): String = JTorch.tensorToString(tensorHandle)

    public fun copyToDouble(): TorchTensorReal = TorchTensorReal(
        scope = scope,
        tensorHandle = JTorch.copyToDouble(this.tensorHandle)
    )

    public fun copyToFloat(): TorchTensorFloat = TorchTensorFloat(
        scope = scope,
        tensorHandle = JTorch.copyToFloat(this.tensorHandle)
    )

    public fun copyToLong(): TorchTensorLong = TorchTensorLong(
        scope = scope,
        tensorHandle = JTorch.copyToLong(this.tensorHandle)
    )

    public fun copyToInt(): TorchTensorInt = TorchTensorInt(
        scope = scope,
        tensorHandle = JTorch.copyToInt(this.tensorHandle)
    )
}

public sealed class TorchTensorOverFieldJVM<T> constructor(
    scope: DeferScope,
    tensorHandle: Long
) : TorchTensorJVM<T>(scope, tensorHandle), TorchTensorOverField<T> {
    override var requiresGrad: Boolean
        get() = JTorch.requiresGrad(tensorHandle)
        set(value) = JTorch.setRequiresGrad(tensorHandle, value)
}

public class TorchTensorReal internal constructor(
    scope: DeferScope,
    tensorHandle: Long
) : TorchTensorOverFieldJVM<Double>(scope, tensorHandle) {
    override fun item(): Double = JTorch.getItemDouble(tensorHandle)
    override fun get(index: IntArray): Double = JTorch.getDouble(tensorHandle, index)
    override fun set(index: IntArray, value: Double) {
        JTorch.setDouble(tensorHandle, index, value)
    }
}

public class TorchTensorFloat internal constructor(
    scope: DeferScope,
    tensorHandle: Long
) : TorchTensorOverFieldJVM<Float>(scope, tensorHandle) {
    override fun item(): Float = JTorch.getItemFloat(tensorHandle)
    override fun get(index: IntArray): Float = JTorch.getFloat(tensorHandle, index)
    override fun set(index: IntArray, value: Float) {
        JTorch.setFloat(tensorHandle, index, value)
    }
}

public class TorchTensorLong internal constructor(
    scope: DeferScope,
    tensorHandle: Long
) : TorchTensorOverFieldJVM<Long>(scope, tensorHandle) {
    override fun item(): Long = JTorch.getItemLong(tensorHandle)
    override fun get(index: IntArray): Long = JTorch.getLong(tensorHandle, index)
    override fun set(index: IntArray, value: Long) {
        JTorch.setLong(tensorHandle, index, value)
    }
}

public class TorchTensorInt internal constructor(
    scope: DeferScope,
    tensorHandle: Long
) : TorchTensorOverFieldJVM<Int>(scope, tensorHandle) {
    override fun item(): Int = JTorch.getItemInt(tensorHandle)
    override fun get(index: IntArray): Int = JTorch.getInt(tensorHandle, index)
    override fun set(index: IntArray, value: Int) {
        JTorch.setInt(tensorHandle, index, value)
    }
}