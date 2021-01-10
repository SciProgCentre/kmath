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
    public abstract fun full(value: T, shape: IntArray, device: TorchDevice): TorchTensor<T>

    public abstract operator fun T.times(other: TorchTensor<T>): TorchTensor<T>
    public abstract operator fun TorchTensor<T>.times(value: T): TorchTensor<T>
    public abstract operator fun TorchTensor<T>.timesAssign(value: T): Unit

    public infix fun TorchTensor<T>.dot(other: TorchTensor<T>): TorchTensor<T> =
        wrap(matmul(this.tensorHandle, other.tensorHandle)!!)

    public infix fun TorchTensor<T>.dotAssign(other: TorchTensor<T>): Unit {
        matmul_assign(this.tensorHandle, other.tensorHandle)
    }

    public infix fun TorchTensor<T>.dotRightAssign(other: TorchTensor<T>): Unit {
        matmul_right_assign(this.tensorHandle, other.tensorHandle)
    }

    public operator fun TorchTensor<T>.plus(other: TorchTensor<T>): TorchTensor<T> =
        wrap(plus_tensor(this.tensorHandle, other.tensorHandle)!!)

    public operator fun TorchTensor<T>.plusAssign(other: TorchTensor<T>): Unit {
        plus_tensor_assign(this.tensorHandle, other.tensorHandle)
    }

    public operator fun TorchTensor<T>.minus(other: TorchTensor<T>): TorchTensor<T> =
        wrap(minus_tensor(this.tensorHandle, other.tensorHandle)!!)

    public operator fun TorchTensor<T>.minusAssign(other: TorchTensor<T>): Unit {
        minus_tensor_assign(this.tensorHandle, other.tensorHandle)
    }

    public fun TorchTensor<T>.abs(): TorchTensor<T> = wrap(abs_tensor(tensorHandle)!!)

    public fun TorchTensor<T>.sum(): TorchTensor<T> = wrap(sum_tensor(tensorHandle)!!)

    public fun TorchTensor<T>.transpose(i: Int, j: Int): TorchTensor<T> =
        wrap(transpose_tensor(tensorHandle, i , j)!!)

    public infix fun TorchTensor<T>.grad(variable: TorchTensor<T>): TorchTensor<T> =
        wrap(autograd_tensor(this.tensorHandle, variable.tensorHandle)!!)
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

    override operator fun Double.times(other: TorchTensor<Double>): TorchTensorReal = TorchTensorReal(
        scope = scope,
        tensorHandle = times_double(this, other.tensorHandle)!!
    )

    override fun TorchTensor<Double>.times(value: Double): TorchTensorReal = TorchTensorReal(
        scope = scope,
        tensorHandle = times_double(value, this.tensorHandle)!!
    )

    override fun TorchTensor<Double>.timesAssign(value: Double): Unit {
        times_assign_double(value, this.tensorHandle)
    }


    override fun full(value: Double, shape: IntArray, device: TorchDevice):  TorchTensorReal = TorchTensorReal(
        scope = scope,
        tensorHandle = full_double(value, shape.toCValues(), shape.size, device.toInt())!!
    )

}

public fun <R> TorchTensorRealAlgebra(block: TorchTensorRealAlgebra.() -> R): R =
    memScoped { TorchTensorRealAlgebra(this).block() }