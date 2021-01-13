package kscience.kmath.torch

import kotlinx.cinterop.*
import kscience.kmath.ctorch.*

public sealed class TorchTensorAlgebra<
        T,
        TVar : CPrimitiveVar,
        PrimitiveArrayType,
        TorchTensorType : TorchTensor<T>> constructor(
    internal val scope: DeferScope
) {
    internal abstract fun wrap(tensorHandle: COpaquePointer): TorchTensorType

    public abstract fun copyFromArray(
        array: PrimitiveArrayType,
        shape: IntArray,
        device: TorchDevice = TorchDevice.TorchCPU
    ): TorchTensorType

    public abstract fun TorchTensorType.copyToArray(): PrimitiveArrayType

    public abstract fun fromBlob(arrayBlob: CPointer<TVar>, shape: IntArray): TorchTensorType
    public abstract fun TorchTensorType.getData(): CPointer<TVar>

    public abstract fun full(value: T, shape: IntArray, device: TorchDevice): TorchTensorType

    public abstract operator fun T.plus(other: TorchTensorType): TorchTensorType
    public abstract operator fun TorchTensorType.plus(value: T): TorchTensorType
    public abstract operator fun TorchTensorType.plusAssign(value: T): Unit
    public abstract operator fun T.minus(other: TorchTensorType): TorchTensorType
    public abstract operator fun TorchTensorType.minus(value: T): TorchTensorType
    public abstract operator fun TorchTensorType.minusAssign(value: T): Unit
    public abstract operator fun T.times(other: TorchTensorType): TorchTensorType
    public abstract operator fun TorchTensorType.times(value: T): TorchTensorType
    public abstract operator fun TorchTensorType.timesAssign(value: T): Unit

    public operator fun TorchTensorType.times(other: TorchTensorType): TorchTensorType =
        wrap(times_tensor(this.tensorHandle, other.tensorHandle)!!)

    public operator fun TorchTensorType.timesAssign(other: TorchTensorType): Unit {
        times_tensor_assign(this.tensorHandle, other.tensorHandle)
    }

    public operator fun TorchTensorType.div(other: TorchTensorType): TorchTensorType =
        wrap(div_tensor(this.tensorHandle, other.tensorHandle)!!)

    public operator fun TorchTensorType.divAssign(other: TorchTensorType): Unit {
        div_tensor_assign(this.tensorHandle, other.tensorHandle)
    }

    public infix fun TorchTensorType.dot(other: TorchTensorType): TorchTensorType =
        wrap(matmul(this.tensorHandle, other.tensorHandle)!!)

    public infix fun TorchTensorType.dotAssign(other: TorchTensorType): Unit {
        matmul_assign(this.tensorHandle, other.tensorHandle)
    }

    public infix fun TorchTensorType.dotRightAssign(other: TorchTensorType): Unit {
        matmul_right_assign(this.tensorHandle, other.tensorHandle)
    }

    public operator fun TorchTensorType.plus(other: TorchTensorType): TorchTensorType =
        wrap(plus_tensor(this.tensorHandle, other.tensorHandle)!!)

    public operator fun TorchTensorType.plusAssign(other: TorchTensorType): Unit {
        plus_tensor_assign(this.tensorHandle, other.tensorHandle)
    }

    public operator fun TorchTensorType.minus(other: TorchTensorType): TorchTensorType =
        wrap(minus_tensor(this.tensorHandle, other.tensorHandle)!!)

    public operator fun TorchTensorType.minusAssign(other: TorchTensorType): Unit {
        minus_tensor_assign(this.tensorHandle, other.tensorHandle)
    }

    public operator fun TorchTensorType.unaryMinus(): TorchTensorType =
        wrap(unary_minus(this.tensorHandle)!!)

    public fun TorchTensorType.abs(): TorchTensorType = wrap(abs_tensor(tensorHandle)!!)
    public fun TorchTensorType.absAssign(): Unit {
        abs_tensor_assign(tensorHandle)
    }

    public fun TorchTensorType.transpose(i: Int, j: Int): TorchTensorType =
        wrap(transpose_tensor(tensorHandle, i, j)!!)

    public fun TorchTensorType.transposeAssign(i: Int, j: Int): Unit {
        transpose_tensor_assign(tensorHandle, i, j)
    }

    public fun TorchTensorType.sum(): TorchTensorType = wrap(sum_tensor(tensorHandle)!!)
    public fun TorchTensorType.sumAssign(): Unit {
        sum_tensor_assign(tensorHandle)
    }

    public fun diagEmbed(diagonalEntries: TorchTensorType,
                         offset: Int = 0, dim1: Int = -2, dim2: Int = -1): TorchTensorType =
        wrap(diag_embed(diagonalEntries.tensorHandle, offset, dim1, dim2)!!)

    public fun TorchTensorType.svd(): Triple<TorchTensorType,TorchTensorType,TorchTensorType> {
        val U = empty_tensor()!!
        val V = empty_tensor()!!
        val S = empty_tensor()!!
        svd_tensor(this.tensorHandle, U, S, V)
        return Triple(wrap(U), wrap(S), wrap(V))
    }

    public fun TorchTensorType.symEig(eigenvectors: Boolean = true): Pair<TorchTensorType, TorchTensorType> {
        val V = empty_tensor()!!
        val S = empty_tensor()!!
        symeig_tensor(this.tensorHandle, S,  V, eigenvectors)
        return Pair(wrap(S), wrap(V))
    }

    public infix fun TorchTensorType.grad(variable: TorchTensorType): TorchTensorType =
        wrap(autograd_tensor(this.tensorHandle, variable.tensorHandle)!!)

    public fun TorchTensorType.copy(): TorchTensorType =
        wrap(tensorHandle = copy_tensor(this.tensorHandle)!!)

    public fun TorchTensorType.copyToDevice(device: TorchDevice): TorchTensorType =
        wrap(tensorHandle = copy_to_device(this.tensorHandle, device.toInt())!!)

    public fun TorchTensorType.detachFromGraph(): TorchTensorType =
        wrap(tensorHandle = detach_from_graph(this.tensorHandle)!!)

}

public sealed class TorchTensorFieldAlgebra<T, TVar : CPrimitiveVar,
        PrimitiveArrayType, TorchTensorType : TorchTensor<T>>(scope: DeferScope) :
    TorchTensorAlgebra<T, TVar, PrimitiveArrayType, TorchTensorType>(scope) {
    public abstract fun randNormal(shape: IntArray, device: TorchDevice = TorchDevice.TorchCPU): TorchTensorType
    public abstract fun randUniform(shape: IntArray, device: TorchDevice = TorchDevice.TorchCPU): TorchTensorType

    public fun TorchTensorType.exp(): TorchTensorType = wrap(exp_tensor(tensorHandle)!!)
    public fun TorchTensorType.expAssign(): Unit {
        exp_tensor_assign(tensorHandle)
    }

    public fun TorchTensorType.log(): TorchTensorType = wrap(log_tensor(tensorHandle)!!)
    public fun TorchTensorType.logAssign(): Unit {
        log_tensor_assign(tensorHandle)
    }

}

public class TorchTensorRealAlgebra(scope: DeferScope) :
    TorchTensorFieldAlgebra<Double, DoubleVar, DoubleArray, TorchTensorReal>(scope) {
    override fun wrap(tensorHandle: COpaquePointer): TorchTensorReal =
        TorchTensorReal(scope = scope, tensorHandle = tensorHandle)

    override fun TorchTensorReal.copyToArray(): DoubleArray =
        this.elements().map { it.second }.toList().toDoubleArray()

    override fun copyFromArray(
        array: DoubleArray,
        shape: IntArray,
        device: TorchDevice
    ): TorchTensorReal =
        TorchTensorReal(
            scope = scope,
            tensorHandle = from_blob_double(
                array.toCValues(),
                shape.toCValues(),
                shape.size,
                device.toInt(),
                true
            )!!
        )

    override fun fromBlob(arrayBlob: CPointer<DoubleVar>, shape: IntArray): TorchTensorReal =
        TorchTensorReal(
            scope = scope,
            tensorHandle = from_blob_double(
                arrayBlob,
                shape.toCValues(),
                shape.size,
                TorchDevice.TorchCPU.toInt(),
                false
            )!!
        )

    override fun TorchTensorReal.getData(): CPointer<DoubleVar> {
        require(this.device is TorchDevice.TorchCPU) {
            "This tensor is not on available on CPU"
        }
        return get_data_double(this.tensorHandle)!!
    }

    override fun randNormal(shape: IntArray, device: TorchDevice): TorchTensorReal = TorchTensorReal(
        scope = scope,
        tensorHandle = randn_double(shape.toCValues(), shape.size, device.toInt())!!
    )

    override fun randUniform(shape: IntArray, device: TorchDevice): TorchTensorReal = TorchTensorReal(
        scope = scope,
        tensorHandle = rand_double(shape.toCValues(), shape.size, device.toInt())!!
    )

    override operator fun Double.plus(other: TorchTensorReal): TorchTensorReal = TorchTensorReal(
        scope = scope,
        tensorHandle = plus_double(this, other.tensorHandle)!!
    )

    override fun TorchTensorReal.plus(value: Double): TorchTensorReal = TorchTensorReal(
        scope = scope,
        tensorHandle = plus_double(value, this.tensorHandle)!!
    )

    override fun TorchTensorReal.plusAssign(value: Double): Unit {
        plus_double_assign(value, this.tensorHandle)
    }

    override operator fun Double.minus(other: TorchTensorReal): TorchTensorReal = TorchTensorReal(
        scope = scope,
        tensorHandle = plus_double(-this, other.tensorHandle)!!
    )

    override fun TorchTensorReal.minus(value: Double): TorchTensorReal = TorchTensorReal(
        scope = scope,
        tensorHandle = plus_double(-value, this.tensorHandle)!!
    )

    override fun TorchTensorReal.minusAssign(value: Double): Unit {
        plus_double_assign(-value, this.tensorHandle)
    }

    override operator fun Double.times(other: TorchTensorReal): TorchTensorReal = TorchTensorReal(
        scope = scope,
        tensorHandle = times_double(this, other.tensorHandle)!!
    )

    override fun TorchTensorReal.times(value: Double): TorchTensorReal = TorchTensorReal(
        scope = scope,
        tensorHandle = times_double(value, this.tensorHandle)!!
    )

    override fun TorchTensorReal.timesAssign(value: Double): Unit {
        times_double_assign(value, this.tensorHandle)
    }


    override fun full(value: Double, shape: IntArray, device: TorchDevice): TorchTensorReal = TorchTensorReal(
        scope = scope,
        tensorHandle = full_double(value, shape.toCValues(), shape.size, device.toInt())!!
    )

}

public inline fun <R> TorchTensorRealAlgebra(block: TorchTensorRealAlgebra.() -> R): R =
    memScoped { TorchTensorRealAlgebra(this).block() }