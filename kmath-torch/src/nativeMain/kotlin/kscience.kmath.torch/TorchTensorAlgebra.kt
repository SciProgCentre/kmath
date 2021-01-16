package kscience.kmath.torch

import kotlinx.cinterop.*
import kscience.kmath.ctorch.*

public sealed class TorchTensorAlgebra<
        T,
        TVar : CPrimitiveVar,
        PrimitiveArrayType,
        TorchTensorType : TorchTensor<T>> constructor(
    internal val scope: DeferScope
) :
    TensorAlgebra<T, TorchTensorType> {

    internal abstract fun wrap(tensorHandle: COpaquePointer): TorchTensorType

    public abstract fun copyFromArray(
        array: PrimitiveArrayType,
        shape: IntArray,
        device: Device = Device.CPU
    ): TorchTensorType

    public abstract fun TorchTensorType.copyToArray(): PrimitiveArrayType

    public abstract fun fromBlob(arrayBlob: CPointer<TVar>, shape: IntArray): TorchTensorType
    public abstract fun TorchTensorType.getData(): CPointer<TVar>

    public abstract fun full(value: T, shape: IntArray, device: Device): TorchTensorType

    public abstract fun randIntegral(
        low: T, high: T, shape: IntArray,
        device: Device = Device.CPU
    ): TorchTensorType

    public abstract fun TorchTensorType.randIntegral(low: T, high: T): TorchTensorType
    public abstract fun TorchTensorType.randIntegralAssign(low: T, high: T): Unit

    override val zero: TorchTensorType
        get() = number(0)
    override val one: TorchTensorType
        get() = number(1)

    protected inline fun checkDeviceCompatible(a: TorchTensorType, b: TorchTensorType) =
        check(a.device == b.device) {
            "Tensors must be on the same device"
        }

    protected inline fun checkShapeCompatible(a: TorchTensorType, b: TorchTensorType) =
        check(a.shape contentEquals b.shape) {
            "Tensors must be of identical shape"
        }

    protected inline fun checkLinearOperation(a: TorchTensorType, b: TorchTensorType) {
        if (a.isNotValue() and b.isNotValue()) {
            checkDeviceCompatible(a, b)
            checkShapeCompatible(a, b)
        }
    }

    override operator fun TorchTensorType.times(b: TorchTensorType): TorchTensorType =
        this.times(b, safe = true)

    public fun TorchTensorType.times(b: TorchTensorType, safe: Boolean): TorchTensorType {
        if (safe) checkLinearOperation(this, b)
        return wrap(times_tensor(this.tensorHandle, b.tensorHandle)!!)
    }

    override operator fun TorchTensorType.timesAssign(b: TorchTensorType): Unit =
        this.timesAssign(b, safe = true)

    public fun TorchTensorType.timesAssign(b: TorchTensorType, safe: Boolean): Unit {
        if (safe) checkLinearOperation(this, b)
        times_tensor_assign(this.tensorHandle, b.tensorHandle)
    }

    override fun multiply(a: TorchTensorType, b: TorchTensorType): TorchTensorType = a * b

    override operator fun TorchTensorType.plus(b: TorchTensorType): TorchTensorType =
        this.plus(b, safe = true)

    public fun TorchTensorType.plus(b: TorchTensorType, safe: Boolean): TorchTensorType {
        if (safe) checkLinearOperation(this, b)
        return wrap(plus_tensor(this.tensorHandle, b.tensorHandle)!!)
    }

    override operator fun TorchTensorType.plusAssign(b: TorchTensorType): Unit =
        this.plusAssign(b, false)

    public fun TorchTensorType.plusAssign(b: TorchTensorType, safe: Boolean): Unit {
        if (safe) checkLinearOperation(this, b)
        plus_tensor_assign(this.tensorHandle, b.tensorHandle)
    }

    override fun add(a: TorchTensorType, b: TorchTensorType): TorchTensorType = a + b

    override operator fun TorchTensorType.minus(b: TorchTensorType): TorchTensorType =
        this.minus(b, safe = true)

    public fun TorchTensorType.minus(b: TorchTensorType, safe: Boolean): TorchTensorType {
        if (safe) checkLinearOperation(this, b)
        return wrap(minus_tensor(this.tensorHandle, b.tensorHandle)!!)
    }

    override operator fun TorchTensorType.minusAssign(b: TorchTensorType): Unit =
        this.minusAssign(b, safe = true)

    public fun TorchTensorType.minusAssign(b: TorchTensorType, safe: Boolean): Unit {
        if (safe) checkLinearOperation(this, b)
        minus_tensor_assign(this.tensorHandle, b.tensorHandle)
    }

    override operator fun TorchTensorType.unaryMinus(): TorchTensorType =
        wrap(unary_minus(this.tensorHandle)!!)

    private inline fun checkDotOperation(a: TorchTensorType, b: TorchTensorType): Unit {
        checkDeviceCompatible(a, b)
        val sa = a.shape
        val sb = b.shape
        val na = sa.size
        val nb = sb.size
        var status: Boolean
        if (nb == 1) {
            status = sa.last() == sb[0]
        } else {
            status = sa.last() == sb[nb - 2]
            if ((na > 2) and (nb > 2)) {
                status = status and
                        (sa.take(nb - 2).toIntArray() contentEquals sb.take(nb - 2).toIntArray())
            }
        }
        check(status) { "Incompatible shapes $sa and $sb for dot product" }
    }

    override infix fun TorchTensorType.dot(b: TorchTensorType): TorchTensorType =
        this.dot(b, safe = true)

    public fun TorchTensorType.dot(b: TorchTensorType, safe: Boolean): TorchTensorType {
        if (safe) checkDotOperation(this, b)
        return wrap(matmul(this.tensorHandle, b.tensorHandle)!!)
    }

    public infix fun TorchTensorType.dotAssign(b: TorchTensorType): Unit =
        this.dotAssign(b, safe = true)

    public fun TorchTensorType.dotAssign(b: TorchTensorType, safe: Boolean): Unit {
        if (safe) checkDotOperation(this, b)
        matmul_assign(this.tensorHandle, b.tensorHandle)
    }

    public infix fun TorchTensorType.dotRightAssign(b: TorchTensorType): Unit =
        this.dotRightAssign(b, safe = true)

    public fun TorchTensorType.dotRightAssign(b: TorchTensorType, safe: Boolean): Unit {
        if (safe) checkDotOperation(this, b)
        matmul_right_assign(this.tensorHandle, b.tensorHandle)
    }

    override fun diagonalEmbedding(
        diagonalEntries: TorchTensorType, offset: Int, dim1: Int, dim2: Int
    ): TorchTensorType =
        wrap(diag_embed(diagonalEntries.tensorHandle, offset, dim1, dim2)!!)

    private inline fun checkTranspose(dim: Int, i: Int, j: Int): Unit =
        check((i < dim) and (j < dim)) {
            "Cannot transpose $i to $j for a tensor of dim $dim"
        }

    override fun TorchTensorType.transpose(i: Int, j: Int): TorchTensorType =
        this.transpose(i, j, safe = true)

    public fun TorchTensorType.transpose(i: Int, j: Int, safe: Boolean): TorchTensorType {
        if (safe) checkTranspose(this.dimension, i, j)
        return wrap(transpose_tensor(tensorHandle, i, j)!!)
    }

    public fun TorchTensorType.transposeAssign(i: Int, j: Int): Unit =
        this.transposeAssign(i, j, safe = true)

    public fun TorchTensorType.transposeAssign(i: Int, j: Int, safe: Boolean): Unit {
        if (safe) checkTranspose(this.dimension, i, j)
        transpose_tensor_assign(tensorHandle, i, j)
    }

    private inline fun checkView(a: TorchTensorType, shape: IntArray): Unit =
        check(a.shape.reduce(Int::times) == shape.reduce(Int::times))

    override fun TorchTensorType.view(shape: IntArray): TorchTensorType =
        this.view(shape, safe = true)

    public fun TorchTensorType.view(shape: IntArray, safe: Boolean): TorchTensorType {
        if (safe) checkView(this, shape)
        return wrap(view_tensor(this.tensorHandle, shape.toCValues(), shape.size)!!)
    }

    override fun TorchTensorType.abs(): TorchTensorType = wrap(abs_tensor(tensorHandle)!!)
    public fun TorchTensorType.absAssign(): Unit {
        abs_tensor_assign(tensorHandle)
    }

    override fun TorchTensorType.sum(): TorchTensorType = wrap(sum_tensor(tensorHandle)!!)
    public fun TorchTensorType.sumAssign(): Unit {
        sum_tensor_assign(tensorHandle)
    }

    public fun TorchTensorType.copy(): TorchTensorType =
        wrap(copy_tensor(this.tensorHandle)!!)

    public fun TorchTensorType.copyToDevice(device: Device): TorchTensorType =
        wrap(copy_to_device(this.tensorHandle, device.toInt())!!)

    public infix fun TorchTensorType.swap(otherTensor: TorchTensorType): Unit {
        swap_tensors(this.tensorHandle, otherTensor.tensorHandle)
    }
}

public sealed class TorchTensorFieldAlgebra<T, TVar : CPrimitiveVar,
        PrimitiveArrayType, TorchTensorType : TorchTensor<T>>(scope: DeferScope) :
    TorchTensorAlgebra<T, TVar, PrimitiveArrayType, TorchTensorType>(scope),
    TensorFieldAlgebra<T, TorchTensorType> {

    override operator fun TorchTensorType.div(b: TorchTensorType): TorchTensorType =
        this.div(b, safe = true)

    public fun TorchTensorType.div(b: TorchTensorType, safe: Boolean): TorchTensorType {
        if (safe) checkLinearOperation(this, b)
        return wrap(div_tensor(this.tensorHandle, b.tensorHandle)!!)
    }

    override operator fun TorchTensorType.divAssign(b: TorchTensorType): Unit =
        this.divAssign(b, safe = true)

    public fun TorchTensorType.divAssign(b: TorchTensorType, safe: Boolean): Unit {
        if (safe) checkLinearOperation(this, b)
        div_tensor_assign(this.tensorHandle, b.tensorHandle)
    }

    override fun divide(a: TorchTensorType, b: TorchTensorType): TorchTensorType = a / b

    public abstract fun randUniform(shape: IntArray, device: Device = Device.CPU): TorchTensorType
    public abstract fun randNormal(shape: IntArray, device: Device = Device.CPU): TorchTensorType

    public fun TorchTensorType.randUniform(): TorchTensorType =
        wrap(rand_like(this.tensorHandle)!!)

    public fun TorchTensorType.randUniformAssign(): Unit {
        rand_like_assign(this.tensorHandle)
    }

    public fun TorchTensorType.randNormal(): TorchTensorType =
        wrap(randn_like(this.tensorHandle)!!)

    public fun TorchTensorType.randNormalAssign(): Unit {
        randn_like_assign(this.tensorHandle)
    }

    override fun TorchTensorType.exp(): TorchTensorType = wrap(exp_tensor(tensorHandle)!!)
    public fun TorchTensorType.expAssign(): Unit {
        exp_tensor_assign(tensorHandle)
    }

    override fun TorchTensorType.log(): TorchTensorType = wrap(log_tensor(tensorHandle)!!)
    public fun TorchTensorType.logAssign(): Unit {
        log_tensor_assign(tensorHandle)
    }

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

    public fun TorchTensorType.grad(variable: TorchTensorType, retainGraph: Boolean = false): TorchTensorType {
        this.checkIsValue()
        return wrap(autograd_tensor(this.tensorHandle, variable.tensorHandle, retainGraph)!!)
    }

    public infix fun TorchTensorType.grad(variable: TorchTensorType): TorchTensorType =
        this.grad(variable, false)

    public infix fun TorchTensorType.hess(variable: TorchTensorType): TorchTensorType {
        this.checkIsValue()
        return wrap(autohess_tensor(this.tensorHandle, variable.tensorHandle)!!)
    }

    public fun TorchTensorType.detachFromGraph(): TorchTensorType =
        wrap(tensorHandle = detach_from_graph(this.tensorHandle)!!)
}

public class TorchTensorRealAlgebra(scope: DeferScope) :
    TorchTensorFieldAlgebra<Double, DoubleVar, DoubleArray, TorchTensorReal>(scope) {
    override fun wrap(tensorHandle: COpaquePointer): TorchTensorReal =
        TorchTensorReal(scope = scope, tensorHandle = tensorHandle)

    override fun number(value: Number): TorchTensorReal =
        full(value.toDouble(), intArrayOf(1), Device.CPU).sum()

    override fun TorchTensorReal.copyToArray(): DoubleArray =
        this.elements().map { it.second }.toList().toDoubleArray()

    override fun copyFromArray(array: DoubleArray, shape: IntArray, device: Device): TorchTensorReal =
        wrap(from_blob_double(array.toCValues(), shape.toCValues(), shape.size, device.toInt(), true)!!)

    override fun fromBlob(arrayBlob: CPointer<DoubleVar>, shape: IntArray): TorchTensorReal =
        wrap(from_blob_double(arrayBlob, shape.toCValues(), shape.size, Device.CPU.toInt(), false)!!)

    override fun TorchTensorReal.getData(): CPointer<DoubleVar> {
        require(this.device is Device.CPU) {
            "This tensor is not on available on CPU"
        }
        return get_data_double(this.tensorHandle)!!
    }

    override fun randNormal(shape: IntArray, device: Device): TorchTensorReal =
        wrap(randn_double(shape.toCValues(), shape.size, device.toInt())!!)

    override fun randUniform(shape: IntArray, device: Device): TorchTensorReal =
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

    override fun multiply(a: TorchTensorReal, k: Number): TorchTensorReal = a * k.toDouble()

    override fun full(value: Double, shape: IntArray, device: Device): TorchTensorReal =
        wrap(full_double(value, shape.toCValues(), shape.size, device.toInt())!!)

    override fun randIntegral(low: Double, high: Double, shape: IntArray, device: Device): TorchTensorReal =
        wrap(randint_double(low.toLong(), high.toLong(), shape.toCValues(), shape.size, device.toInt())!!)

    override fun TorchTensorReal.randIntegral(low: Double, high: Double): TorchTensorReal =
        wrap(randint_long_like(this.tensorHandle, low.toLong(), high.toLong())!!)

    override fun TorchTensorReal.randIntegralAssign(low: Double, high: Double): Unit {
        randint_long_like_assign(this.tensorHandle, low.toLong(), high.toLong())
    }
}

public class TorchTensorFloatAlgebra(scope: DeferScope) :
    TorchTensorFieldAlgebra<Float, FloatVar, FloatArray, TorchTensorFloat>(scope) {
    override fun wrap(tensorHandle: COpaquePointer): TorchTensorFloat =
        TorchTensorFloat(scope = scope, tensorHandle = tensorHandle)

    override fun number(value: Number): TorchTensorFloat =
        full(value.toFloat(), intArrayOf(1), Device.CPU).sum()

    override fun TorchTensorFloat.copyToArray(): FloatArray =
        this.elements().map { it.second }.toList().toFloatArray()

    override fun copyFromArray(array: FloatArray, shape: IntArray, device: Device): TorchTensorFloat =
        wrap(from_blob_float(array.toCValues(), shape.toCValues(), shape.size, device.toInt(), true)!!)

    override fun fromBlob(arrayBlob: CPointer<FloatVar>, shape: IntArray): TorchTensorFloat =
        wrap(from_blob_float(arrayBlob, shape.toCValues(), shape.size, Device.CPU.toInt(), false)!!)

    override fun TorchTensorFloat.getData(): CPointer<FloatVar> {
        require(this.device is Device.CPU) {
            "This tensor is not on available on CPU"
        }
        return get_data_float(this.tensorHandle)!!
    }

    override fun randNormal(shape: IntArray, device: Device): TorchTensorFloat =
        wrap(randn_float(shape.toCValues(), shape.size, device.toInt())!!)

    override fun randUniform(shape: IntArray, device: Device): TorchTensorFloat =
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

    override fun multiply(a: TorchTensorFloat, k: Number): TorchTensorFloat = a * k.toFloat()

    override fun full(value: Float, shape: IntArray, device: Device): TorchTensorFloat =
        wrap(full_float(value, shape.toCValues(), shape.size, device.toInt())!!)

    override fun randIntegral(low: Float, high: Float, shape: IntArray, device: Device): TorchTensorFloat =
        wrap(randint_float(low.toLong(), high.toLong(), shape.toCValues(), shape.size, device.toInt())!!)

    override fun TorchTensorFloat.randIntegral(low: Float, high: Float): TorchTensorFloat =
        wrap(randint_long_like(this.tensorHandle, low.toLong(), high.toLong())!!)

    override fun TorchTensorFloat.randIntegralAssign(low: Float, high: Float): Unit {
        randint_long_like_assign(this.tensorHandle, low.toLong(), high.toLong())
    }
}

public class TorchTensorLongAlgebra(scope: DeferScope) :
    TorchTensorAlgebra<Long, LongVar, LongArray, TorchTensorLong>(scope) {
    override fun wrap(tensorHandle: COpaquePointer): TorchTensorLong =
        TorchTensorLong(scope = scope, tensorHandle = tensorHandle)

    override fun number(value: Number): TorchTensorLong =
        full(value.toLong(), intArrayOf(1), Device.CPU).sum()

    override fun TorchTensorLong.copyToArray(): LongArray =
        this.elements().map { it.second }.toList().toLongArray()

    override fun copyFromArray(array: LongArray, shape: IntArray, device: Device): TorchTensorLong =
        wrap(from_blob_long(array.toCValues(), shape.toCValues(), shape.size, device.toInt(), true)!!)

    override fun fromBlob(arrayBlob: CPointer<LongVar>, shape: IntArray): TorchTensorLong =
        wrap(from_blob_long(arrayBlob, shape.toCValues(), shape.size, Device.CPU.toInt(), false)!!)

    override fun TorchTensorLong.getData(): CPointer<LongVar> {
        check(this.device is Device.CPU) {
            "This tensor is not on available on CPU"
        }
        return get_data_long(this.tensorHandle)!!
    }

    override fun randIntegral(low: Long, high: Long, shape: IntArray, device: Device): TorchTensorLong =
        wrap(randint_long(low, high, shape.toCValues(), shape.size, device.toInt())!!)

    override fun TorchTensorLong.randIntegral(low: Long, high: Long): TorchTensorLong =
        wrap(randint_long_like(this.tensorHandle, low, high)!!)

    override fun TorchTensorLong.randIntegralAssign(low: Long, high: Long): Unit {
        randint_long_like_assign(this.tensorHandle, low, high)
    }

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

    override fun multiply(a: TorchTensorLong, k: Number): TorchTensorLong = a * k.toLong()

    override fun full(value: Long, shape: IntArray, device: Device): TorchTensorLong =
        wrap(full_long(value, shape.toCValues(), shape.size, device.toInt())!!)
}

public class TorchTensorIntAlgebra(scope: DeferScope) :
    TorchTensorAlgebra<Int, IntVar, IntArray, TorchTensorInt>(scope) {
    override fun wrap(tensorHandle: COpaquePointer): TorchTensorInt =
        TorchTensorInt(scope = scope, tensorHandle = tensorHandle)

    override fun number(value: Number): TorchTensorInt =
        full(value.toInt(), intArrayOf(1), Device.CPU).sum()

    override fun TorchTensorInt.copyToArray(): IntArray =
        this.elements().map { it.second }.toList().toIntArray()

    override fun copyFromArray(array: IntArray, shape: IntArray, device: Device): TorchTensorInt =
        wrap(from_blob_int(array.toCValues(), shape.toCValues(), shape.size, device.toInt(), true)!!)

    override fun fromBlob(arrayBlob: CPointer<IntVar>, shape: IntArray): TorchTensorInt =
        wrap(from_blob_int(arrayBlob, shape.toCValues(), shape.size, Device.CPU.toInt(), false)!!)

    override fun TorchTensorInt.getData(): CPointer<IntVar> {
        require(this.device is Device.CPU) {
            "This tensor is not on available on CPU"
        }
        return get_data_int(this.tensorHandle)!!
    }

    override fun randIntegral(low: Int, high: Int, shape: IntArray, device: Device): TorchTensorInt =
        wrap(randint_int(low, high, shape.toCValues(), shape.size, device.toInt())!!)

    override fun TorchTensorInt.randIntegral(low: Int, high: Int): TorchTensorInt =
        wrap(randint_int_like(this.tensorHandle, low, high)!!)

    override fun TorchTensorInt.randIntegralAssign(low: Int, high: Int): Unit {
        randint_int_like_assign(this.tensorHandle, low, high)
    }

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

    override fun multiply(a: TorchTensorInt, k: Number): TorchTensorInt = a * k.toInt()

    override fun full(value: Int, shape: IntArray, device: Device): TorchTensorInt =
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