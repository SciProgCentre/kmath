package kscience.kmath.torch

import kotlinx.cinterop.*
import kscience.kmath.ctorch.*

public sealed class TorchTensorAlgebra<T, PrimitiveArrayType> constructor(
    internal val scope: DeferScope
) {
    internal abstract fun wrap(tensorHandle: COpaquePointer): TorchTensor<T>
    public abstract fun copyFromArray(
        array: PrimitiveArrayType,
        shape: IntArray,
        device: TorchDevice = TorchDevice.TorchCPU
    ): TorchTensor<T>

    public abstract fun TorchTensor<T>.copyToArray(): PrimitiveArrayType

    public infix fun TorchTensor<T>.dot(other: TorchTensor<T>): TorchTensor<T> =
        wrap(matmul(this.tensorHandle, other.tensorHandle)!!)

    public infix fun TorchTensor<T>.dotAssign(other: TorchTensor<T>): Unit {
        matmul_assign(this.tensorHandle, other.tensorHandle)
    }
}

public sealed class TorchTensorFieldAlgebra<T, PrimitiveArrayType>(scope: DeferScope) :
    TorchTensorAlgebra<T, PrimitiveArrayType>(scope) {
    public abstract fun randNormal(shape: IntArray, device: TorchDevice = TorchDevice.TorchCPU): TorchTensor<T>
    public abstract fun randUniform(shape: IntArray, device: TorchDevice = TorchDevice.TorchCPU): TorchTensor<T>
}

public class TorchTensorRealAlgebra(scope: DeferScope) : TorchTensorFieldAlgebra<Double, DoubleArray>(scope) {
    override fun wrap(tensorHandle: COpaquePointer): TorchTensorReal =
        TorchTensorReal(scope = scope, tensorHandle = tensorHandle)

    override fun TorchTensor<Double>.copyToArray(): DoubleArray =
        this.elements().map { it.second }.toList().toDoubleArray()

    override fun copyFromArray(
        array: DoubleArray,
        shape: IntArray,
        device: TorchDevice
    ): TorchTensorReal =
        TorchTensorReal(
            scope = scope,
            tensorHandle = copy_from_blob_double(
                array.toCValues(),
                shape.toCValues(),
                shape.size,
                device.toInt()
            )!!
        )

    override fun randNormal(shape: IntArray, device: TorchDevice): TorchTensorReal = TorchTensorReal(
        scope = scope,
        tensorHandle = randn_double(shape.toCValues(), shape.size, device.toInt())!!
    )

    override fun randUniform(shape: IntArray, device: TorchDevice): TorchTensorReal = TorchTensorReal(
        scope = scope,
        tensorHandle = rand_double(shape.toCValues(), shape.size, device.toInt())!!
    )
}

public fun <R> TorchTensorRealAlgebra(block: TorchTensorRealAlgebra.() -> R): R =
    memScoped { TorchTensorRealAlgebra(this).block() }