package space.kscience.kmath.torch


import space.kscience.kmath.memory.DeferScope
import space.kscience.kmath.memory.withDeferScope

import kotlinx.cinterop.*
import space.kscience.kmath.torch.ctorch.*

public sealed class TorchTensorAlgebraNative<
        T,
        TVar : CPrimitiveVar,
        PrimitiveArrayType,
        TorchTensorType : TorchTensorNative<T>> constructor(
    internal val scope: DeferScope
) : TorchTensorAlgebra<T, PrimitiveArrayType, TorchTensorType> {

    override fun getNumThreads(): Int {
        return get_num_threads()
    }

    override fun setNumThreads(numThreads: Int): Unit {
        set_num_threads(numThreads)
    }

    override fun cudaAvailable(): Boolean {
        return cuda_is_available()
    }

    override fun setSeed(seed: Int): Unit {
        set_seed(seed)
    }

    override var checks: Boolean = false

    internal abstract fun wrap(tensorHandle: COpaquePointer): TorchTensorType

    public abstract fun fromBlob(arrayBlob: CPointer<TVar>, shape: IntArray): TorchTensorType
    public abstract fun TorchTensorType.getData(): CPointer<TVar>

    override operator fun TorchTensorType.times(other: TorchTensorType): TorchTensorType {
        if (checks) checkLinearOperation(this, other)
        return wrap(times_tensor(this.tensorHandle, other.tensorHandle)!!)
    }

    override operator fun TorchTensorType.timesAssign(other: TorchTensorType): Unit {
        if (checks) checkLinearOperation(this, other)
        times_tensor_assign(this.tensorHandle, other.tensorHandle)
    }

    override operator fun TorchTensorType.plus(other: TorchTensorType): TorchTensorType {
        if (checks) checkLinearOperation(this, other)
        return wrap(plus_tensor(this.tensorHandle, other.tensorHandle)!!)
    }

    override operator fun TorchTensorType.plusAssign(other: TorchTensorType): Unit {
        if (checks) checkLinearOperation(this, other)
        plus_tensor_assign(this.tensorHandle, other.tensorHandle)
    }

    override operator fun TorchTensorType.minus(other: TorchTensorType): TorchTensorType {
        if (checks) checkLinearOperation(this, other)
        return wrap(minus_tensor(this.tensorHandle, other.tensorHandle)!!)
    }

    override operator fun TorchTensorType.minusAssign(other: TorchTensorType): Unit {
        if (checks) checkLinearOperation(this, other)
        minus_tensor_assign(this.tensorHandle, other.tensorHandle)
    }

    override operator fun TorchTensorType.unaryMinus(): TorchTensorType =
        wrap(unary_minus(this.tensorHandle)!!)

    override infix fun TorchTensorType.dot(other: TorchTensorType): TorchTensorType {
        if (checks) checkDotOperation(this, other)
        return wrap(matmul(this.tensorHandle, other.tensorHandle)!!)
    }

    override infix fun TorchTensorType.dotAssign(other: TorchTensorType): Unit {
        if (checks) checkDotOperation(this, other)
        matmul_assign(this.tensorHandle, other.tensorHandle)
    }

    override infix fun TorchTensorType.dotRightAssign(other: TorchTensorType): Unit {
        if (checks) checkDotOperation(this, other)
        matmul_right_assign(this.tensorHandle, other.tensorHandle)
    }

    override fun diagonalEmbedding(
        diagonalEntries: TorchTensorType, offset: Int, dim1: Int, dim2: Int
    ): TorchTensorType =
        wrap(diag_embed(diagonalEntries.tensorHandle, offset, dim1, dim2)!!)

    override fun TorchTensorType.transpose(i: Int, j: Int): TorchTensorType {
        if (checks) checkTranspose(this.dimension, i, j)
        return wrap(transpose_tensor(tensorHandle, i, j)!!)
    }

    override fun TorchTensorType.transposeAssign(i: Int, j: Int): Unit {
        if (checks) checkTranspose(this.dimension, i, j)
        transpose_tensor_assign(tensorHandle, i, j)
    }

    override fun TorchTensorType.view(shape: IntArray): TorchTensorType {
        if (checks) checkView(this, shape)
        return wrap(view_tensor(this.tensorHandle, shape.toCValues(), shape.size)!!)
    }

    override fun TorchTensorType.abs(): TorchTensorType = wrap(abs_tensor(tensorHandle)!!)
    override fun TorchTensorType.absAssign(): Unit = abs_tensor_assign(tensorHandle)

    override fun TorchTensorType.sum(): TorchTensorType = wrap(sum_tensor(tensorHandle)!!)
    override fun TorchTensorType.sumAssign(): Unit = sum_tensor_assign(tensorHandle)

    override fun TorchTensorType.randIntegral(low: Long, high: Long): TorchTensorType =
        wrap(randint_like(this.tensorHandle, low, high)!!)

    override fun TorchTensorType.randIntegralAssign(low: Long, high: Long): Unit =
        randint_like_assign(this.tensorHandle, low, high)

    override fun TorchTensorType.copy(): TorchTensorType =
        wrap(copy_tensor(this.tensorHandle)!!)

    override fun TorchTensorType.copyToDevice(device: space.kscience.kmath.torch.Device): TorchTensorType =
        wrap(copy_to_device(this.tensorHandle, device.toInt())!!)

    override infix fun TorchTensorType.swap(other: TorchTensorType): Unit =
        swap_tensors(this.tensorHandle, other.tensorHandle)
}

public sealed class TorchTensorPartialDivisionAlgebraNative<T, TVar : CPrimitiveVar,
        PrimitiveArrayType, TorchTensorType : TorchTensorOverFieldNative<T>>(scope: DeferScope) :
    TorchTensorAlgebraNative<T, TVar, PrimitiveArrayType, TorchTensorType>(scope),
    TorchTensorPartialDivisionAlgebra<T, PrimitiveArrayType, TorchTensorType> {

    override operator fun TorchTensorType.div(other: TorchTensorType): TorchTensorType {
        if (checks) checkLinearOperation(this, other)
        return wrap(div_tensor(this.tensorHandle, other.tensorHandle)!!)
    }

    override operator fun TorchTensorType.divAssign(other: TorchTensorType): Unit {
        if (checks) checkLinearOperation(this, other)
        div_tensor_assign(this.tensorHandle, other.tensorHandle)
    }

    override fun TorchTensorType.randUniform(): TorchTensorType =
        wrap(rand_like(this.tensorHandle)!!)

    override fun TorchTensorType.randUniformAssign(): Unit =
        rand_like_assign(this.tensorHandle)


    override fun TorchTensorType.randNormal(): TorchTensorType =
        wrap(randn_like(this.tensorHandle)!!)

    override fun TorchTensorType.randNormalAssign(): Unit =
        randn_like_assign(this.tensorHandle)


    override fun TorchTensorType.exp(): TorchTensorType = wrap(exp_tensor(tensorHandle)!!)
    override fun TorchTensorType.expAssign(): Unit = exp_tensor_assign(tensorHandle)
    override fun TorchTensorType.log(): TorchTensorType = wrap(log_tensor(tensorHandle)!!)
    override fun TorchTensorType.logAssign(): Unit = log_tensor_assign(tensorHandle)

    override fun TorchTensorType.svd(): Triple<TorchTensorType, TorchTensorType, TorchTensorType> {
        val U = empty_tensor()!!
        val V = empty_tensor()!!
        val S = empty_tensor()!!
        svd_tensor(this.tensorHandle, U, S, V)
        return Triple(wrap(U), wrap(S), wrap(V))
    }

    override fun TorchTensorType.symEig(eigenvectors: Boolean): Pair<TorchTensorType, TorchTensorType> {
        val V = empty_tensor()!!
        val S = empty_tensor()!!
        symeig_tensor(this.tensorHandle, S, V, eigenvectors)
        return Pair(wrap(S), wrap(V))
    }

    override fun TorchTensorType.grad(variable: TorchTensorType, retainGraph: Boolean): TorchTensorType {
        if (checks) this.checkIsValue()
        return wrap(autograd_tensor(this.tensorHandle, variable.tensorHandle, retainGraph)!!)
    }

    override infix fun TorchTensorType.hess(variable: TorchTensorType): TorchTensorType {
        if (checks) this.checkIsValue()
        return wrap(autohess_tensor(this.tensorHandle, variable.tensorHandle)!!)
    }

    override fun TorchTensorType.detachFromGraph(): TorchTensorType =
        wrap(detach_from_graph(this.tensorHandle)!!)

}

public class TorchTensorRealAlgebra(scope: DeferScope) :
    TorchTensorPartialDivisionAlgebraNative<Double, DoubleVar, DoubleArray, TorchTensorReal>(scope) {
    override fun wrap(tensorHandle: COpaquePointer): TorchTensorReal =
        TorchTensorReal(scope = scope, tensorHandle = tensorHandle)

    override fun TorchTensorReal.copyToArray(): DoubleArray =
        this.elements().map { it.second }.toList().toDoubleArray()

    override fun copyFromArray(array: DoubleArray, shape: IntArray, device: space.kscience.kmath.torch.Device): TorchTensorReal =
        wrap(from_blob_double(array.toCValues(), shape.toCValues(), shape.size, device.toInt(), true)!!)

    override fun fromBlob(arrayBlob: CPointer<DoubleVar>, shape: IntArray): TorchTensorReal =
        wrap(from_blob_double(arrayBlob, shape.toCValues(), shape.size, space.kscience.kmath.torch.Device.CPU.toInt(), false)!!)

    override fun TorchTensorReal.getData(): CPointer<DoubleVar> {
        require(this.device is space.kscience.kmath.torch.Device.CPU) {
            "This tensor is not on available on CPU"
        }
        return get_data_double(this.tensorHandle)!!
    }

    override fun randNormal(shape: IntArray, device: space.kscience.kmath.torch.Device): TorchTensorReal =
        wrap(randn_double(shape.toCValues(), shape.size, device.toInt())!!)

    override fun randUniform(shape: IntArray, device: space.kscience.kmath.torch.Device): TorchTensorReal =
        wrap(rand_double(shape.toCValues(), shape.size, device.toInt())!!)

    override fun randIntegral(low: Long, high: Long, shape: IntArray, device: space.kscience.kmath.torch.Device): TorchTensorReal =
        wrap(randint_double(low, high, shape.toCValues(), shape.size, device.toInt())!!)

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

    override fun full(value: Double, shape: IntArray, device: space.kscience.kmath.torch.Device): TorchTensorReal =
        wrap(full_double(value, shape.toCValues(), shape.size, device.toInt())!!)
}


public class TorchTensorFloatAlgebra(scope: DeferScope) :
    TorchTensorPartialDivisionAlgebraNative<Float, FloatVar, FloatArray, TorchTensorFloat>(scope) {
    override fun wrap(tensorHandle: COpaquePointer): TorchTensorFloat =
        TorchTensorFloat(scope = scope, tensorHandle = tensorHandle)

    override fun TorchTensorFloat.copyToArray(): FloatArray =
        this.elements().map { it.second }.toList().toFloatArray()

    override fun copyFromArray(array: FloatArray, shape: IntArray, device: space.kscience.kmath.torch.Device): TorchTensorFloat =
        wrap(from_blob_float(array.toCValues(), shape.toCValues(), shape.size, device.toInt(), true)!!)

    override fun fromBlob(arrayBlob: CPointer<FloatVar>, shape: IntArray): TorchTensorFloat =
        wrap(from_blob_float(arrayBlob, shape.toCValues(), shape.size, space.kscience.kmath.torch.Device.CPU.toInt(), false)!!)

    override fun TorchTensorFloat.getData(): CPointer<FloatVar> {
        require(this.device is space.kscience.kmath.torch.Device.CPU) {
            "This tensor is not on available on CPU"
        }
        return get_data_float(this.tensorHandle)!!
    }

    override fun randNormal(shape: IntArray, device: space.kscience.kmath.torch.Device): TorchTensorFloat =
        wrap(randn_float(shape.toCValues(), shape.size, device.toInt())!!)

    override fun randUniform(shape: IntArray, device: space.kscience.kmath.torch.Device): TorchTensorFloat =
        wrap(rand_float(shape.toCValues(), shape.size, device.toInt())!!)

    override fun randIntegral(low: Long, high: Long, shape: IntArray, device: space.kscience.kmath.torch.Device): TorchTensorFloat =
        wrap(randint_float(low, high, shape.toCValues(), shape.size, device.toInt())!!)

    override operator fun Float.plus(other: TorchTensorFloat): TorchTensorFloat =
        wrap(plus_float(this, other.tensorHandle)!!)

    override fun TorchTensorFloat.plus(value: Float): TorchTensorFloat =
        wrap(plus_float(value, this.tensorHandle)!!)

    override fun TorchTensorFloat.plusAssign(value: Float): Unit =
        plus_float_assign(value, this.tensorHandle)

    override operator fun Float.minus(other: TorchTensorFloat): TorchTensorFloat =
        wrap(plus_float(-this, other.tensorHandle)!!)

    override fun TorchTensorFloat.minus(value: Float): TorchTensorFloat =
        wrap(plus_float(-value, this.tensorHandle)!!)

    override fun TorchTensorFloat.minusAssign(value: Float): Unit =
        plus_float_assign(-value, this.tensorHandle)

    override operator fun Float.times(other: TorchTensorFloat): TorchTensorFloat =
        wrap(times_float(this, other.tensorHandle)!!)

    override fun TorchTensorFloat.times(value: Float): TorchTensorFloat =
        wrap(times_float(value, this.tensorHandle)!!)

    override fun TorchTensorFloat.timesAssign(value: Float): Unit =
        times_float_assign(value, this.tensorHandle)

    override fun full(value: Float, shape: IntArray, device: space.kscience.kmath.torch.Device): TorchTensorFloat =
        wrap(full_float(value, shape.toCValues(), shape.size, device.toInt())!!)

}

public class TorchTensorLongAlgebra(scope: DeferScope) :
    TorchTensorAlgebraNative<Long, LongVar, LongArray, TorchTensorLong>(scope) {
    override fun wrap(tensorHandle: COpaquePointer): TorchTensorLong =
        TorchTensorLong(scope = scope, tensorHandle = tensorHandle)

    override fun TorchTensorLong.copyToArray(): LongArray =
        this.elements().map { it.second }.toList().toLongArray()

    override fun copyFromArray(array: LongArray, shape: IntArray, device: space.kscience.kmath.torch.Device): TorchTensorLong =
        wrap(from_blob_long(array.toCValues(), shape.toCValues(), shape.size, device.toInt(), true)!!)

    override fun fromBlob(arrayBlob: CPointer<LongVar>, shape: IntArray): TorchTensorLong =
        wrap(from_blob_long(arrayBlob, shape.toCValues(), shape.size, space.kscience.kmath.torch.Device.CPU.toInt(), false)!!)

    override fun TorchTensorLong.getData(): CPointer<LongVar> {
        check(this.device is space.kscience.kmath.torch.Device.CPU) {
            "This tensor is not on available on CPU"
        }
        return get_data_long(this.tensorHandle)!!
    }

    override fun randIntegral(low: Long, high: Long, shape: IntArray, device: space.kscience.kmath.torch.Device): TorchTensorLong =
        wrap(randint_long(low, high, shape.toCValues(), shape.size, device.toInt())!!)

    override operator fun Long.plus(other: TorchTensorLong): TorchTensorLong =
        wrap(plus_long(this, other.tensorHandle)!!)

    override fun TorchTensorLong.plus(value: Long): TorchTensorLong =
        wrap(plus_long(value, this.tensorHandle)!!)

    override fun TorchTensorLong.plusAssign(value: Long): Unit =
        plus_long_assign(value, this.tensorHandle)

    override operator fun Long.minus(other: TorchTensorLong): TorchTensorLong =
        wrap(plus_long(-this, other.tensorHandle)!!)

    override fun TorchTensorLong.minus(value: Long): TorchTensorLong =
        wrap(plus_long(-value, this.tensorHandle)!!)

    override fun TorchTensorLong.minusAssign(value: Long): Unit =
        plus_long_assign(-value, this.tensorHandle)

    override operator fun Long.times(other: TorchTensorLong): TorchTensorLong =
        wrap(times_long(this, other.tensorHandle)!!)

    override fun TorchTensorLong.times(value: Long): TorchTensorLong =
        wrap(times_long(value, this.tensorHandle)!!)

    override fun TorchTensorLong.timesAssign(value: Long): Unit =
        times_long_assign(value, this.tensorHandle)

    override fun full(value: Long, shape: IntArray, device: space.kscience.kmath.torch.Device): TorchTensorLong =
        wrap(full_long(value, shape.toCValues(), shape.size, device.toInt())!!)
}

public class TorchTensorIntAlgebra(scope: DeferScope) :
    TorchTensorAlgebraNative<Int, IntVar, IntArray, TorchTensorInt>(scope) {
    override fun wrap(tensorHandle: COpaquePointer): TorchTensorInt =
        TorchTensorInt(scope = scope, tensorHandle = tensorHandle)

    override fun TorchTensorInt.copyToArray(): IntArray =
        this.elements().map { it.second }.toList().toIntArray()

    override fun copyFromArray(array: IntArray, shape: IntArray, device: space.kscience.kmath.torch.Device): TorchTensorInt =
        wrap(from_blob_int(array.toCValues(), shape.toCValues(), shape.size, device.toInt(), true)!!)

    override fun fromBlob(arrayBlob: CPointer<IntVar>, shape: IntArray): TorchTensorInt =
        wrap(from_blob_int(arrayBlob, shape.toCValues(), shape.size, space.kscience.kmath.torch.Device.CPU.toInt(), false)!!)

    override fun TorchTensorInt.getData(): CPointer<IntVar> {
        require(this.device is space.kscience.kmath.torch.Device.CPU) {
            "This tensor is not on available on CPU"
        }
        return get_data_int(this.tensorHandle)!!
    }

    override fun randIntegral(low: Long, high: Long, shape: IntArray, device: space.kscience.kmath.torch.Device): TorchTensorInt =
        wrap(randint_int(low, high, shape.toCValues(), shape.size, device.toInt())!!)

    override operator fun Int.plus(other: TorchTensorInt): TorchTensorInt =
        wrap(plus_int(this, other.tensorHandle)!!)

    override fun TorchTensorInt.plus(value: Int): TorchTensorInt =
        wrap(plus_int(value, this.tensorHandle)!!)

    override fun TorchTensorInt.plusAssign(value: Int): Unit =
        plus_int_assign(value, this.tensorHandle)

    override operator fun Int.minus(other: TorchTensorInt): TorchTensorInt =
        wrap(plus_int(-this, other.tensorHandle)!!)

    override fun TorchTensorInt.minus(value: Int): TorchTensorInt =
        wrap(plus_int(-value, this.tensorHandle)!!)

    override fun TorchTensorInt.minusAssign(value: Int): Unit =
        plus_int_assign(-value, this.tensorHandle)

    override operator fun Int.times(other: TorchTensorInt): TorchTensorInt =
        wrap(times_int(this, other.tensorHandle)!!)

    override fun TorchTensorInt.times(value: Int): TorchTensorInt =
        wrap(times_int(value, this.tensorHandle)!!)

    override fun TorchTensorInt.timesAssign(value: Int): Unit =
        times_int_assign(value, this.tensorHandle)

    override fun full(value: Int, shape: IntArray, device: space.kscience.kmath.torch.Device): TorchTensorInt =
        wrap(full_int(value, shape.toCValues(), shape.size, device.toInt())!!)
}


public inline fun <R> TorchTensorRealAlgebra(block: TorchTensorRealAlgebra.() -> R): R =
    withDeferScope { TorchTensorRealAlgebra(this).block() }

public inline fun <R> TorchTensorFloatAlgebra(block: TorchTensorFloatAlgebra.() -> R): R =
    withDeferScope { TorchTensorFloatAlgebra(this).block() }

public inline fun <R> TorchTensorLongAlgebra(block: TorchTensorLongAlgebra.() -> R): R =
    withDeferScope { TorchTensorLongAlgebra(this).block() }

public inline fun <R> TorchTensorIntAlgebra(block: TorchTensorIntAlgebra.() -> R): R =
    withDeferScope { TorchTensorIntAlgebra(this).block() }

