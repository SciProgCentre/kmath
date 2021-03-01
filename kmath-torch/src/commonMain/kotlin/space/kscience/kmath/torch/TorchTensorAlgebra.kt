@file:Suppress("NOTHING_TO_INLINE")

package space.kscience.kmath.torch

import space.kscience.kmath.tensors.*

public interface TorchTensorAlgebra<T, PrimitiveArrayType, TorchTensorType : TorchTensor<T>> :
    TensorAlgebra<T, TorchTensorType> {

    public fun getNumThreads(): Int
    public fun setNumThreads(numThreads: Int): Unit
    public fun cudaAvailable(): Boolean
    public fun setSeed(seed: Int): Unit

    public var checks: Boolean

    public fun copyFromArray(
        array: PrimitiveArrayType,
        shape: IntArray,
        device: Device = Device.CPU
    ): TorchTensorType

    public fun TorchTensorType.copyToArray(): PrimitiveArrayType

    public fun full(value: T, shape: IntArray, device: Device): TorchTensorType

    public fun randIntegral(
        low: Long, high: Long, shape: IntArray,
        device: Device = Device.CPU
    ): TorchTensorType
    public fun TorchTensorType.randIntegral(low: Long, high: Long): TorchTensorType
    public fun TorchTensorType.randIntegralAssign(low: Long, high: Long): Unit

    public fun TorchTensorType.copy(): TorchTensorType
    public fun TorchTensorType.copyToDevice(device: Device): TorchTensorType
    public infix fun TorchTensorType.swap(other: TorchTensorType)

}

public interface TorchTensorPartialDivisionAlgebra<T, PrimitiveArrayType, TorchTensorType : TorchTensorOverField<T>> :
    TorchTensorAlgebra<T, PrimitiveArrayType, TorchTensorType>, TensorPartialDivisionAlgebra<T, TorchTensorType> {

    public fun randUniform(shape: IntArray, device: Device = Device.CPU): TorchTensorType
    public fun randNormal(shape: IntArray, device: Device = Device.CPU): TorchTensorType
    public fun TorchTensorType.randUniform(): TorchTensorType
    public fun TorchTensorType.randUniformAssign(): Unit
    public fun TorchTensorType.randNormal(): TorchTensorType
    public fun TorchTensorType.randNormalAssign(): Unit

    public fun TorchTensorType.grad(variable: TorchTensorType, retainGraph: Boolean = false): TorchTensorType
    public infix fun TorchTensorType.grad(variable: TorchTensorType): TorchTensorType =
        this.grad(variable, false)

    public infix fun TorchTensorType.hess(variable: TorchTensorType): TorchTensorType
    public fun TorchTensorType.detachFromGraph(): TorchTensorType
}


public inline fun <T, PrimitiveArrayType, TorchTensorType : TorchTensor<T>,
        TorchTensorAlgebraType : TorchTensorAlgebra<T, PrimitiveArrayType, TorchTensorType>>
        TorchTensorAlgebraType.withChecks(block: TorchTensorAlgebraType.() -> Unit): Unit {
    val state = this.checks
    this.checks = true
    this.block()
    this.checks = state
}

public inline fun <T, PrimitiveArrayType, TorchTensorType : TorchTensor<T>,
        TorchTensorAlgebraType : TorchTensorAlgebra<T, PrimitiveArrayType, TorchTensorType>>
        TorchTensorAlgebraType.checkDeviceCompatible(
    a: TorchTensorType, b: TorchTensorType
): Unit =
    check(a.device == b.device) {
        "Tensors must be on the same device"
    }


public inline fun <T, PrimitiveArrayType, TorchTensorType : TorchTensor<T>,
        TorchTensorAlgebraType : TorchTensorAlgebra<T, PrimitiveArrayType, TorchTensorType>>
        TorchTensorAlgebraType.checkLinearOperation(
    a: TorchTensorType,
    b: TorchTensorType
) {
    if (a.isNotValue() and b.isNotValue()) {
        checkDeviceCompatible(a, b)
        checkShapeCompatible(a, b)
    }
}

public inline fun <T, PrimitiveArrayType, TorchTensorType : TorchTensor<T>,
        TorchTensorAlgebraType : TorchTensorAlgebra<T, PrimitiveArrayType, TorchTensorType>>
        TorchTensorAlgebraType.checkDotOperation(a: TorchTensorType, b: TorchTensorType): Unit {
    checkDeviceCompatible(a, b)
    checkDot(a,b)
}

public inline fun <T, PrimitiveArrayType, TorchTensorType : TorchTensorOverField<T>,
        TorchTensorDivisionAlgebraType : TorchTensorPartialDivisionAlgebra<T, PrimitiveArrayType, TorchTensorType>>
        TorchTensorDivisionAlgebraType.withGradAt(
    tensor: TorchTensorType,
    block: TorchTensorDivisionAlgebraType.(TorchTensorType) -> TorchTensorType
): TorchTensorType {
    tensor.requiresGrad = true
    return this.block(tensor)
}