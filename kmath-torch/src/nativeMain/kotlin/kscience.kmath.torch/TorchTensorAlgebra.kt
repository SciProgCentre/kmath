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

    public fun diagEmbed(
        diagonalEntries: TorchTensorType,
        offset: Int = 0, dim1: Int = -2, dim2: Int = -1
    ): TorchTensorType =
        wrap(diag_embed(diagonalEntries.tensorHandle, offset, dim1, dim2)!!)

    public fun TorchTensorType.copy(): TorchTensorType =
        wrap(copy_tensor(this.tensorHandle)!!)

    public fun TorchTensorType.copyToDevice(device: TorchDevice): TorchTensorType =
        wrap(copy_to_device(this.tensorHandle, device.toInt())!!)

    public infix fun TorchTensorType.swap(otherTensor: TorchTensorType): Unit {
        swap_tensors(this.tensorHandle, otherTensor.tensorHandle)
    }
}

public sealed class TorchTensorFieldAlgebra<T, TVar : CPrimitiveVar,
        PrimitiveArrayType, TorchTensorType : TorchTensor<T>>(scope: DeferScope) :
    TorchTensorAlgebra<T, TVar, PrimitiveArrayType, TorchTensorType>(scope) {

    public abstract fun randNormal(shape: IntArray, device: TorchDevice = TorchDevice.TorchCPU): TorchTensorType
    public abstract fun randUniform(shape: IntArray, device: TorchDevice = TorchDevice.TorchCPU): TorchTensorType

    public operator fun TorchTensorType.div(other: TorchTensorType): TorchTensorType =
        wrap(div_tensor(this.tensorHandle, other.tensorHandle)!!)

    public operator fun TorchTensorType.divAssign(other: TorchTensorType): Unit {
        div_tensor_assign(this.tensorHandle, other.tensorHandle)
    }

    public fun TorchTensorType.exp(): TorchTensorType = wrap(exp_tensor(tensorHandle)!!)
    public fun TorchTensorType.expAssign(): Unit {
        exp_tensor_assign(tensorHandle)
    }

    public fun TorchTensorType.log(): TorchTensorType = wrap(log_tensor(tensorHandle)!!)
    public fun TorchTensorType.logAssign(): Unit {
        log_tensor_assign(tensorHandle)
    }

    public fun TorchTensorType.svd(): Triple<TorchTensorType, TorchTensorType, TorchTensorType> {
        val U = empty_tensor()!!
        val V = empty_tensor()!!
        val S = empty_tensor()!!
        svd_tensor(this.tensorHandle, U, S, V)
        return Triple(wrap(U), wrap(S), wrap(V))
    }

    public fun TorchTensorType.symEig(eigenvectors: Boolean = true): Pair<TorchTensorType, TorchTensorType> {
        val V = empty_tensor()!!
        val S = empty_tensor()!!
        symeig_tensor(this.tensorHandle, S, V, eigenvectors)
        return Pair(wrap(S), wrap(V))
    }

    public infix fun TorchTensorType.grad(variable: TorchTensorType): TorchTensorType =
        wrap(autograd_tensor(this.tensorHandle, variable.tensorHandle)!!)

    public fun TorchTensorType.detachFromGraph(): TorchTensorType =
        wrap(tensorHandle = detach_from_graph(this.tensorHandle)!!)
}

public sealed class TorchTensorRingAlgebra<T, TVar : CPrimitiveVar,
        PrimitiveArrayType, TorchTensorType : TorchTensor<T>>(scope: DeferScope) :
    TorchTensorAlgebra<T, TVar, PrimitiveArrayType, TorchTensorType>(scope) {
    public abstract fun randIntegral(
        low: T, high: T, shape: IntArray,
        device: TorchDevice = TorchDevice.TorchCPU
    ): TorchTensorType
}

public class TorchTensorRealAlgebra(scope: DeferScope) :
    TorchTensorFieldAlgebra<Double, DoubleVar, DoubleArray, TorchTensorReal>(scope) {
    override fun wrap(tensorHandle: COpaquePointer): TorchTensorReal =
        TorchTensorReal(scope = scope, tensorHandle = tensorHandle)

    override fun TorchTensorReal.copyToArray(): DoubleArray =
        this.elements().map { it.second }.toList().toDoubleArray()

    override fun copyFromArray(array: DoubleArray, shape: IntArray, device: TorchDevice): TorchTensorReal =
        wrap(from_blob_double(array.toCValues(), shape.toCValues(), shape.size, device.toInt(), true)!!)

    override fun fromBlob(arrayBlob: CPointer<DoubleVar>, shape: IntArray): TorchTensorReal =
        wrap(from_blob_double(arrayBlob, shape.toCValues(), shape.size, TorchDevice.TorchCPU.toInt(), false)!!)

    override fun TorchTensorReal.getData(): CPointer<DoubleVar> {
        require(this.device is TorchDevice.TorchCPU) {
            "This tensor is not on available on CPU"
        }
        return get_data_double(this.tensorHandle)!!
    }

    override fun randNormal(shape: IntArray, device: TorchDevice): TorchTensorReal =
        wrap(randn_double(shape.toCValues(), shape.size, device.toInt())!!)

    override fun randUniform(shape: IntArray, device: TorchDevice): TorchTensorReal =
        wrap(rand_double(shape.toCValues(), shape.size, device.toInt())!!)

    override operator fun Double.plus(other: TorchTensorReal): TorchTensorReal =
        wrap(plus_double(this, other.tensorHandle)!!)

    override fun TorchTensorReal.plus(value: Double): TorchTensorReal =
        wrap(plus_double(value, this.tensorHandle)!!)

    override fun TorchTensorReal.plusAssign(value: Double): Unit {
        plus_double_assign(value, this.tensorHandle)
    }

    override operator fun Double.minus(other: TorchTensorReal): TorchTensorReal =
        wrap(plus_double(-this, other.tensorHandle)!!)

    override fun TorchTensorReal.minus(value: Double): TorchTensorReal =
        wrap(plus_double(-value, this.tensorHandle)!!)

    override fun TorchTensorReal.minusAssign(value: Double): Unit {
        plus_double_assign(-value, this.tensorHandle)
    }

    override operator fun Double.times(other: TorchTensorReal): TorchTensorReal =
        wrap(times_double(this, other.tensorHandle)!!)

    override fun TorchTensorReal.times(value: Double): TorchTensorReal =
        wrap(times_double(value, this.tensorHandle)!!)

    override fun TorchTensorReal.timesAssign(value: Double): Unit {
        times_double_assign(value, this.tensorHandle)
    }

    override fun full(value: Double, shape: IntArray, device: TorchDevice): TorchTensorReal =
        wrap(full_double(value, shape.toCValues(), shape.size, device.toInt())!!)
}

public class TorchTensorFloatAlgebra(scope: DeferScope) :
    TorchTensorFieldAlgebra<Float, FloatVar, FloatArray, TorchTensorFloat>(scope) {
    override fun wrap(tensorHandle: COpaquePointer): TorchTensorFloat =
        TorchTensorFloat(scope = scope, tensorHandle = tensorHandle)

    override fun TorchTensorFloat.copyToArray(): FloatArray =
        this.elements().map { it.second }.toList().toFloatArray()

    override fun copyFromArray(array: FloatArray, shape: IntArray, device: TorchDevice): TorchTensorFloat =
        wrap(from_blob_float(array.toCValues(), shape.toCValues(), shape.size, device.toInt(), true)!!)

    override fun fromBlob(arrayBlob: CPointer<FloatVar>, shape: IntArray): TorchTensorFloat =
        wrap(from_blob_float(arrayBlob, shape.toCValues(), shape.size, TorchDevice.TorchCPU.toInt(), false)!!)

    override fun TorchTensorFloat.getData(): CPointer<FloatVar> {
        require(this.device is TorchDevice.TorchCPU) {
            "This tensor is not on available on CPU"
        }
        return get_data_float(this.tensorHandle)!!
    }

    override fun randNormal(shape: IntArray, device: TorchDevice): TorchTensorFloat =
        wrap(randn_float(shape.toCValues(), shape.size, device.toInt())!!)

    override fun randUniform(shape: IntArray, device: TorchDevice): TorchTensorFloat =
        wrap(rand_float(shape.toCValues(), shape.size, device.toInt())!!)

    override operator fun Float.plus(other: TorchTensorFloat): TorchTensorFloat =
        wrap(plus_float(this, other.tensorHandle)!!)

    override fun TorchTensorFloat.plus(value: Float): TorchTensorFloat =
        wrap(plus_float(value, this.tensorHandle)!!)

    override fun TorchTensorFloat.plusAssign(value: Float): Unit {
        plus_float_assign(value, this.tensorHandle)
    }

    override operator fun Float.minus(other: TorchTensorFloat): TorchTensorFloat =
        wrap(plus_float(-this, other.tensorHandle)!!)

    override fun TorchTensorFloat.minus(value: Float): TorchTensorFloat =
        wrap(plus_float(-value, this.tensorHandle)!!)

    override fun TorchTensorFloat.minusAssign(value: Float): Unit {
        plus_float_assign(-value, this.tensorHandle)
    }

    override operator fun Float.times(other: TorchTensorFloat): TorchTensorFloat =
        wrap(times_float(this, other.tensorHandle)!!)

    override fun TorchTensorFloat.times(value: Float): TorchTensorFloat =
        wrap(times_float(value, this.tensorHandle)!!)

    override fun TorchTensorFloat.timesAssign(value: Float): Unit {
        times_float_assign(value, this.tensorHandle)
    }

    override fun full(value: Float, shape: IntArray, device: TorchDevice): TorchTensorFloat =
        wrap(full_float(value, shape.toCValues(), shape.size, device.toInt())!!)
}

public class TorchTensorLongAlgebra(scope: DeferScope) :
    TorchTensorRingAlgebra<Long, LongVar, LongArray, TorchTensorLong>(scope) {
    override fun wrap(tensorHandle: COpaquePointer): TorchTensorLong =
        TorchTensorLong(scope = scope, tensorHandle = tensorHandle)

    override fun TorchTensorLong.copyToArray(): LongArray =
        this.elements().map { it.second }.toList().toLongArray()

    override fun copyFromArray(array: LongArray, shape: IntArray, device: TorchDevice): TorchTensorLong =
        wrap(from_blob_long(array.toCValues(), shape.toCValues(), shape.size, device.toInt(), true)!!)

    override fun fromBlob(arrayBlob: CPointer<LongVar>, shape: IntArray): TorchTensorLong =
        wrap(from_blob_long(arrayBlob, shape.toCValues(), shape.size, TorchDevice.TorchCPU.toInt(), false)!!)

    override fun TorchTensorLong.getData(): CPointer<LongVar> {
        require(this.device is TorchDevice.TorchCPU) {
            "This tensor is not on available on CPU"
        }
        return get_data_long(this.tensorHandle)!!
    }

    override fun randIntegral(low: Long, high: Long, shape: IntArray, device: TorchDevice): TorchTensorLong =
        wrap(randint_long(low, high, shape.toCValues(), shape.size, device.toInt())!!)

    override operator fun Long.plus(other: TorchTensorLong): TorchTensorLong =
        wrap(plus_long(this, other.tensorHandle)!!)

    override fun TorchTensorLong.plus(value: Long): TorchTensorLong =
        wrap(plus_long(value, this.tensorHandle)!!)

    override fun TorchTensorLong.plusAssign(value: Long): Unit {
        plus_long_assign(value, this.tensorHandle)
    }

    override operator fun Long.minus(other: TorchTensorLong): TorchTensorLong =
        wrap(plus_long(-this, other.tensorHandle)!!)

    override fun TorchTensorLong.minus(value: Long): TorchTensorLong =
        wrap(plus_long(-value, this.tensorHandle)!!)

    override fun TorchTensorLong.minusAssign(value: Long): Unit {
        plus_long_assign(-value, this.tensorHandle)
    }

    override operator fun Long.times(other: TorchTensorLong): TorchTensorLong =
        wrap(times_long(this, other.tensorHandle)!!)

    override fun TorchTensorLong.times(value: Long): TorchTensorLong =
        wrap(times_long(value, this.tensorHandle)!!)

    override fun TorchTensorLong.timesAssign(value: Long): Unit {
        times_long_assign(value, this.tensorHandle)
    }

    override fun full(value: Long, shape: IntArray, device: TorchDevice): TorchTensorLong =
        wrap(full_long(value, shape.toCValues(), shape.size, device.toInt())!!)
}

public class TorchTensorIntAlgebra(scope: DeferScope) :
    TorchTensorRingAlgebra<Int, IntVar, IntArray, TorchTensorInt>(scope) {
    override fun wrap(tensorHandle: COpaquePointer): TorchTensorInt =
        TorchTensorInt(scope = scope, tensorHandle = tensorHandle)

    override fun TorchTensorInt.copyToArray(): IntArray =
        this.elements().map { it.second }.toList().toIntArray()

    override fun copyFromArray(array: IntArray, shape: IntArray, device: TorchDevice): TorchTensorInt =
        wrap(from_blob_int(array.toCValues(), shape.toCValues(), shape.size, device.toInt(), true)!!)

    override fun fromBlob(arrayBlob: CPointer<IntVar>, shape: IntArray): TorchTensorInt =
        wrap(from_blob_int(arrayBlob, shape.toCValues(), shape.size, TorchDevice.TorchCPU.toInt(), false)!!)

    override fun TorchTensorInt.getData(): CPointer<IntVar> {
        require(this.device is TorchDevice.TorchCPU) {
            "This tensor is not on available on CPU"
        }
        return get_data_int(this.tensorHandle)!!
    }

    override fun randIntegral(low: Int, high: Int, shape: IntArray, device: TorchDevice): TorchTensorInt =
        wrap(randint_int(low, high, shape.toCValues(), shape.size, device.toInt())!!)

    override operator fun Int.plus(other: TorchTensorInt): TorchTensorInt =
        wrap(plus_int(this, other.tensorHandle)!!)

    override fun TorchTensorInt.plus(value: Int): TorchTensorInt =
        wrap(plus_int(value, this.tensorHandle)!!)

    override fun TorchTensorInt.plusAssign(value: Int): Unit {
        plus_int_assign(value, this.tensorHandle)
    }

    override operator fun Int.minus(other: TorchTensorInt): TorchTensorInt =
        wrap(plus_int(-this, other.tensorHandle)!!)

    override fun TorchTensorInt.minus(value: Int): TorchTensorInt =
        wrap(plus_int(-value, this.tensorHandle)!!)

    override fun TorchTensorInt.minusAssign(value: Int): Unit {
        plus_int_assign(-value, this.tensorHandle)
    }

    override operator fun Int.times(other: TorchTensorInt): TorchTensorInt =
        wrap(times_int(this, other.tensorHandle)!!)

    override fun TorchTensorInt.times(value: Int): TorchTensorInt =
        wrap(times_int(value, this.tensorHandle)!!)

    override fun TorchTensorInt.timesAssign(value: Int): Unit {
        times_int_assign(value, this.tensorHandle)
    }

    override fun full(value: Int, shape: IntArray, device: TorchDevice): TorchTensorInt =
        wrap(full_int(value, shape.toCValues(), shape.size, device.toInt())!!)
}

public inline fun <R> TorchTensorRealAlgebra(block: TorchTensorRealAlgebra.() -> R): R =
    memScoped { TorchTensorRealAlgebra(this).block() }

public inline fun <R> TorchTensorFloatAlgebra(block: TorchTensorFloatAlgebra.() -> R): R =
    memScoped { TorchTensorFloatAlgebra(this).block() }

public inline fun <R> TorchTensorLongAlgebra(block: TorchTensorLongAlgebra.() -> R): R =
    memScoped { TorchTensorLongAlgebra(this).block() }

public inline fun <R> TorchTensorIntAlgebra(block: TorchTensorIntAlgebra.() -> R): R =
    memScoped { TorchTensorIntAlgebra(this).block() }