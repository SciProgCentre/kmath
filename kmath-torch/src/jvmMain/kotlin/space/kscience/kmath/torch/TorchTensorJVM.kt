package space.kscience.kmath.torch

import space.kscience.kmath.memory.DeferScope

public sealed class TorchTensorJVM<T> constructor(
    scope: DeferScope,
    internal val tensorHandle: Long
) : TorchTensor<T>, TorchTensorMemoryHolder(scope)
{
    override fun close(): Unit = space.kscience.kmath.torch.JTorch.disposeTensor(tensorHandle)

    override val dimension: Int get() = space.kscience.kmath.torch.JTorch.getDim(tensorHandle)
    override val shape: IntArray
        get() = (1..dimension).map { space.kscience.kmath.torch.JTorch.getShapeAt(tensorHandle, it - 1) }.toIntArray()
    override val strides: IntArray
        get() = (1..dimension).map { space.kscience.kmath.torch.JTorch.getStrideAt(tensorHandle, it - 1) }.toIntArray()
    override val size: Int get() = space.kscience.kmath.torch.JTorch.getNumel(tensorHandle)
    override val device: space.kscience.kmath.torch.Device get() = space.kscience.kmath.torch.Device.fromInt(space.kscience.kmath.torch.JTorch.getDevice(tensorHandle))

    override fun toString(): String = space.kscience.kmath.torch.JTorch.tensorToString(tensorHandle)

    public fun copyToDouble(): TorchTensorReal = TorchTensorReal(
        scope = scope,
        tensorHandle = space.kscience.kmath.torch.JTorch.copyToDouble(this.tensorHandle)
    )

    public fun copyToFloat(): TorchTensorFloat = TorchTensorFloat(
        scope = scope,
        tensorHandle = space.kscience.kmath.torch.JTorch.copyToFloat(this.tensorHandle)
    )

    public fun copyToLong(): TorchTensorLong = TorchTensorLong(
        scope = scope,
        tensorHandle = space.kscience.kmath.torch.JTorch.copyToLong(this.tensorHandle)
    )

    public fun copyToInt(): TorchTensorInt = TorchTensorInt(
        scope = scope,
        tensorHandle = space.kscience.kmath.torch.JTorch.copyToInt(this.tensorHandle)
    )
}

public sealed class TorchTensorOverFieldJVM<T> constructor(
    scope: DeferScope,
    tensorHandle: Long
) : TorchTensorJVM<T>(scope, tensorHandle), TorchTensorOverField<T> {
    override var requiresGrad: Boolean
        get() = space.kscience.kmath.torch.JTorch.requiresGrad(tensorHandle)
        set(value) = space.kscience.kmath.torch.JTorch.setRequiresGrad(tensorHandle, value)
}

public class TorchTensorReal internal constructor(
    scope: DeferScope,
    tensorHandle: Long
) : TorchTensorOverFieldJVM<Double>(scope, tensorHandle) {
    override fun item(): Double = space.kscience.kmath.torch.JTorch.getItemDouble(tensorHandle)
    override fun get(index: IntArray): Double = space.kscience.kmath.torch.JTorch.getDouble(tensorHandle, index)
    override fun set(index: IntArray, value: Double) {
        space.kscience.kmath.torch.JTorch.setDouble(tensorHandle, index, value)
    }
}

public class TorchTensorFloat internal constructor(
    scope: DeferScope,
    tensorHandle: Long
) : TorchTensorOverFieldJVM<Float>(scope, tensorHandle) {
    override fun item(): Float = space.kscience.kmath.torch.JTorch.getItemFloat(tensorHandle)
    override fun get(index: IntArray): Float = space.kscience.kmath.torch.JTorch.getFloat(tensorHandle, index)
    override fun set(index: IntArray, value: Float) {
        space.kscience.kmath.torch.JTorch.setFloat(tensorHandle, index, value)
    }
}

public class TorchTensorLong internal constructor(
    scope: DeferScope,
    tensorHandle: Long
) : TorchTensorOverFieldJVM<Long>(scope, tensorHandle) {
    override fun item(): Long = space.kscience.kmath.torch.JTorch.getItemLong(tensorHandle)
    override fun get(index: IntArray): Long = space.kscience.kmath.torch.JTorch.getLong(tensorHandle, index)
    override fun set(index: IntArray, value: Long) {
        space.kscience.kmath.torch.JTorch.setLong(tensorHandle, index, value)
    }
}

public class TorchTensorInt internal constructor(
    scope: DeferScope,
    tensorHandle: Long
) : TorchTensorOverFieldJVM<Int>(scope, tensorHandle) {
    override fun item(): Int = space.kscience.kmath.torch.JTorch.getItemInt(tensorHandle)
    override fun get(index: IntArray): Int = space.kscience.kmath.torch.JTorch.getInt(tensorHandle, index)
    override fun set(index: IntArray, value: Int) {
        space.kscience.kmath.torch.JTorch.setInt(tensorHandle, index, value)
    }
}