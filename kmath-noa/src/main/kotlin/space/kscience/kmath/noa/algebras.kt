/*
 * Copyright 2018-2021 KMath contributors.
 * Use of tensor source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.noa

import space.kscience.kmath.misc.PerformancePitfall
import space.kscience.kmath.noa.memory.NoaScope
import space.kscience.kmath.tensors.api.AnalyticTensorAlgebra
import space.kscience.kmath.tensors.api.LinearOpsTensorAlgebra
import space.kscience.kmath.tensors.api.Tensor
import space.kscience.kmath.tensors.api.TensorAlgebra
import space.kscience.kmath.tensors.core.TensorLinearStructure


public sealed class NoaAlgebra<T, PrimitiveArray, TensorType : NoaTensor<T>>
protected constructor(protected val scope: NoaScope) :
    TensorAlgebra<T> {

    protected abstract val Tensor<T>.tensor: TensorType

    protected abstract fun wrap(tensorHandle: TensorHandle): TensorType

    public fun Tensor<T>.cast(): TensorType = tensor

    /**
     * A scalar tensor must have empty shape
     */
    override fun Tensor<T>.valueOrNull(): T? =
        try {
            tensor.item()
        } catch (e: NoaException) {
            null
        }

    override fun Tensor<T>.value(): T = tensor.item()

    public abstract fun randDiscrete(low: Long, high: Long, shape: IntArray, device: Device): TensorType

    @PerformancePitfall
    public abstract fun Tensor<T>.copyToArray(): PrimitiveArray

    public abstract fun copyFromArray(array: PrimitiveArray, shape: IntArray, device: Device): TensorType

    public abstract fun full(value: T, shape: IntArray, device: Device): TensorType

    override operator fun Tensor<T>.times(other: Tensor<T>): TensorType {
        return wrap(JNoa.timesTensor(tensor.tensorHandle, other.tensor.tensorHandle))
    }

    override operator fun Tensor<T>.timesAssign(other: Tensor<T>): Unit {
        JNoa.timesTensorAssign(tensor.tensorHandle, other.tensor.tensorHandle)
    }

    override operator fun Tensor<T>.plus(other: Tensor<T>): TensorType {
        return wrap(JNoa.plusTensor(tensor.tensorHandle, other.tensor.tensorHandle))
    }

    override operator fun Tensor<T>.plusAssign(other: Tensor<T>): Unit {
        JNoa.plusTensorAssign(tensor.tensorHandle, other.tensor.tensorHandle)
    }

    override operator fun Tensor<T>.minus(other: Tensor<T>): TensorType {
        return wrap(JNoa.minusTensor(tensor.tensorHandle, other.tensor.tensorHandle))
    }

    override operator fun Tensor<T>.minusAssign(other: Tensor<T>): Unit {
        JNoa.minusTensorAssign(tensor.tensorHandle, other.tensor.tensorHandle)
    }

    override operator fun Tensor<T>.unaryMinus(): TensorType =
        wrap(JNoa.unaryMinus(tensor.tensorHandle))

    override infix fun Tensor<T>.dot(other: Tensor<T>): TensorType {
        return wrap(JNoa.matmul(tensor.tensorHandle, other.tensor.tensorHandle))
    }

    public infix fun Tensor<T>.dotAssign(other: Tensor<T>): Unit {
        JNoa.matmulAssign(tensor.tensorHandle, other.tensor.tensorHandle)
    }

    public infix fun Tensor<T>.dotRightAssign(other: Tensor<T>): Unit {
        JNoa.matmulRightAssign(tensor.tensorHandle, other.tensor.tensorHandle)
    }

    override operator fun Tensor<T>.get(i: Int): TensorType =
        wrap(JNoa.getIndex(tensor.tensorHandle, i))

    override fun diagonalEmbedding(
        diagonalEntries: Tensor<T>, offset: Int, dim1: Int, dim2: Int
    ): TensorType =
        wrap(JNoa.diagEmbed(diagonalEntries.tensor.tensorHandle, offset, dim1, dim2))

    override fun Tensor<T>.transpose(i: Int, j: Int): TensorType {
        return wrap(JNoa.transposeTensor(tensor.tensorHandle, i, j))
    }

    override fun Tensor<T>.view(shape: IntArray): TensorType {
        return wrap(JNoa.viewTensor(tensor.tensorHandle, shape))
    }

    override fun Tensor<T>.viewAs(other: Tensor<T>): TensorType {
        return wrap(JNoa.viewAsTensor(tensor.tensorHandle, other.tensor.tensorHandle))
    }

    public fun Tensor<T>.abs(): TensorType = wrap(JNoa.absTensor(tensor.tensorHandle))

    public fun Tensor<T>.sumAll(): TensorType = wrap(JNoa.sumTensor(tensor.tensorHandle))
    override fun Tensor<T>.sum(): T = sumAll().item()
    override fun Tensor<T>.sum(dim: Int, keepDim: Boolean): TensorType =
        wrap(JNoa.sumDimTensor(tensor.tensorHandle, dim, keepDim))

    public fun Tensor<T>.minAll(): TensorType = wrap(JNoa.minTensor(tensor.tensorHandle))
    override fun Tensor<T>.min(): T = minAll().item()
    override fun Tensor<T>.min(dim: Int, keepDim: Boolean): TensorType =
        wrap(JNoa.minDimTensor(tensor.tensorHandle, dim, keepDim))

    public fun Tensor<T>.maxAll(): TensorType = wrap(JNoa.maxTensor(tensor.tensorHandle))
    override fun Tensor<T>.max(): T = maxAll().item()
    override fun Tensor<T>.max(dim: Int, keepDim: Boolean): TensorType =
        wrap(JNoa.maxDimTensor(tensor.tensorHandle, dim, keepDim))

    override fun Tensor<T>.argMax(dim: Int, keepDim: Boolean): NoaIntTensor =
        NoaIntTensor(scope, JNoa.argMaxTensor(tensor.tensorHandle, dim, keepDim))

    public fun Tensor<T>.flatten(startDim: Int, endDim: Int): TensorType =
        wrap(JNoa.flattenTensor(tensor.tensorHandle, startDim, endDim))

    public fun Tensor<T>.randDiscrete(low: Long, high: Long): TensorType =
        wrap(JNoa.randintLike(tensor.tensorHandle, low, high))

    public fun Tensor<T>.randDiscreteAssign(low: Long, high: Long): Unit =
        JNoa.randintLikeAssign(tensor.tensorHandle, low, high)

    public fun Tensor<T>.copy(): TensorType =
        wrap(JNoa.copyTensor(tensor.tensorHandle))

    public fun Tensor<T>.copyToDevice(device: Device): TensorType =
        wrap(JNoa.copyToDevice(tensor.tensorHandle, device.toInt()))

    public abstract fun loadJitModule(path: String, device: Device): NoaJitModule

    public fun NoaJitModule.forward(parameters: Tensor<T>): TensorType =
        wrap(JNoa.forwardPass(jitModuleHandle, parameters.tensor.tensorHandle))

    public fun NoaJitModule.forwardAssign(parameters: TensorType): Unit =
        JNoa.forwardPassAssign(jitModuleHandle, parameters.tensorHandle)

    public fun NoaJitModule.getParameter(name: String): TensorType =
        wrap(JNoa.getModuleParameter(jitModuleHandle, name))

    public fun NoaJitModule.setParameter(name: String, parameter: Tensor<T>): Unit =
        JNoa.setModuleParameter(jitModuleHandle, name, parameter.tensor.tensorHandle)

    public fun NoaJitModule.getBuffer(name: String): TensorType =
        wrap(JNoa.getModuleParameter(jitModuleHandle, name))

    public fun NoaJitModule.setBuffer(name: String, buffer: Tensor<T>): Unit =
        JNoa.setModuleBuffer(jitModuleHandle, name, buffer.tensor.tensorHandle)

    public infix fun TensorType.swap(other: TensorType): Unit =
        JNoa.swapTensors(tensorHandle, other.tensorHandle)

}

public sealed class NoaPartialDivisionAlgebra<T, PrimitiveArray, TensorType : NoaTensor<T>>
protected constructor(scope: NoaScope) :
    NoaAlgebra<T, PrimitiveArray, TensorType>(scope),
    LinearOpsTensorAlgebra<T>,
    AnalyticTensorAlgebra<T> {

    override operator fun Tensor<T>.div(other: Tensor<T>): TensorType {
        return wrap(JNoa.divTensor(tensor.tensorHandle, other.tensor.tensorHandle))
    }

    override operator fun Tensor<T>.divAssign(other: Tensor<T>): Unit {
        JNoa.divTensorAssign(tensor.tensorHandle, other.tensor.tensorHandle)
    }

    public fun Tensor<T>.meanAll(): TensorType = wrap(JNoa.meanTensor(tensor.tensorHandle))
    override fun Tensor<T>.mean(): T = meanAll().item()
    override fun Tensor<T>.mean(dim: Int, keepDim: Boolean): TensorType =
        wrap(JNoa.meanDimTensor(tensor.tensorHandle, dim, keepDim))

    public fun Tensor<T>.stdAll(): TensorType = wrap(JNoa.stdTensor(tensor.tensorHandle))
    override fun Tensor<T>.std(): T = stdAll().item()
    override fun Tensor<T>.std(dim: Int, keepDim: Boolean): TensorType =
        wrap(JNoa.stdDimTensor(tensor.tensorHandle, dim, keepDim))

    public fun Tensor<T>.varAll(): TensorType = wrap(JNoa.varTensor(tensor.tensorHandle))
    override fun Tensor<T>.variance(): T = varAll().item()
    override fun Tensor<T>.variance(dim: Int, keepDim: Boolean): TensorType =
        wrap(JNoa.varDimTensor(tensor.tensorHandle, dim, keepDim))

    public abstract fun randNormal(shape: IntArray, device: Device): TensorType

    public abstract fun randUniform(shape: IntArray, device: Device): TensorType

    public fun Tensor<T>.randUniform(): TensorType =
        wrap(JNoa.randLike(tensor.tensorHandle))

    public fun Tensor<T>.randUniformAssign(): Unit =
        JNoa.randLikeAssign(tensor.tensorHandle)

    public fun Tensor<T>.randNormal(): TensorType =
        wrap(JNoa.randnLike(tensor.tensorHandle))

    public fun Tensor<T>.randNormalAssign(): Unit =
        JNoa.randnLikeAssign(tensor.tensorHandle)

    override fun Tensor<T>.exp(): TensorType =
        wrap(JNoa.expTensor(tensor.tensorHandle))

    override fun Tensor<T>.ln(): TensorType =
        wrap(JNoa.lnTensor(tensor.tensorHandle))

    override fun Tensor<T>.sqrt(): TensorType =
        wrap(JNoa.sqrtTensor(tensor.tensorHandle))

    override fun Tensor<T>.cos(): TensorType =
        wrap(JNoa.cosTensor(tensor.tensorHandle))

    override fun Tensor<T>.acos(): TensorType =
        wrap(JNoa.acosTensor(tensor.tensorHandle))

    override fun Tensor<T>.cosh(): TensorType =
        wrap(JNoa.coshTensor(tensor.tensorHandle))

    override fun Tensor<T>.acosh(): TensorType =
        wrap(JNoa.acoshTensor(tensor.tensorHandle))

    override fun Tensor<T>.sin(): TensorType =
        wrap(JNoa.sinTensor(tensor.tensorHandle))

    override fun Tensor<T>.asin(): TensorType =
        wrap(JNoa.asinTensor(tensor.tensorHandle))

    override fun Tensor<T>.sinh(): TensorType =
        wrap(JNoa.sinhTensor(tensor.tensorHandle))

    override fun Tensor<T>.asinh(): TensorType =
        wrap(JNoa.asinhTensor(tensor.tensorHandle))

    override fun Tensor<T>.tan(): TensorType =
        wrap(JNoa.tanTensor(tensor.tensorHandle))

    override fun Tensor<T>.atan(): TensorType =
        wrap(JNoa.atanTensor(tensor.tensorHandle))

    override fun Tensor<T>.tanh(): TensorType =
        wrap(JNoa.tanhTensor(tensor.tensorHandle))

    override fun Tensor<T>.atanh(): TensorType =
        wrap(JNoa.atanhTensor(tensor.tensorHandle))

    override fun Tensor<T>.ceil(): TensorType =
        wrap(JNoa.ceilTensor(tensor.tensorHandle))

    override fun Tensor<T>.floor(): TensorType =
        wrap(JNoa.floorTensor(tensor.tensorHandle))

    override fun Tensor<T>.det(): Tensor<T> =
        wrap(JNoa.detTensor(tensor.tensorHandle))

    override fun Tensor<T>.inv(): Tensor<T> =
        wrap(JNoa.invTensor(tensor.tensorHandle))

    override fun Tensor<T>.cholesky(): Tensor<T> =
        wrap(JNoa.choleskyTensor(tensor.tensorHandle))

    override fun Tensor<T>.qr(): Pair<TensorType, TensorType> {
        val Q = JNoa.emptyTensor()
        val R = JNoa.emptyTensor()
        JNoa.qrTensor(tensor.tensorHandle, Q, R)
        return Pair(wrap(Q), wrap(R))
    }
    /**
     * this implementation satisfies `tensor = P dot L dot U`
     */
    override fun Tensor<T>.lu(): Triple<TensorType, TensorType, TensorType> {
        val P = JNoa.emptyTensor()
        val L = JNoa.emptyTensor()
        val U = JNoa.emptyTensor()
        JNoa.luTensor(tensor.tensorHandle, P, L, U)
        return Triple(wrap(P), wrap(L), wrap(U))
    }

    override fun Tensor<T>.svd(): Triple<TensorType, TensorType, TensorType> {
        val U = JNoa.emptyTensor()
        val V = JNoa.emptyTensor()
        val S = JNoa.emptyTensor()
        JNoa.svdTensor(tensor.tensorHandle, U, S, V)
        return Triple(wrap(U), wrap(S), wrap(V))
    }

    override fun Tensor<T>.symEig(): Pair<TensorType, TensorType> {
        val V = JNoa.emptyTensor()
        val S = JNoa.emptyTensor()
        JNoa.symEigTensor(tensor.tensorHandle, S, V)
        return Pair(wrap(S), wrap(V))
    }

    public fun TensorType.autoGradient(variable: TensorType, retainGraph: Boolean): TensorType =
        wrap(JNoa.autoGradTensor(tensorHandle, variable.tensorHandle, retainGraph))

    public fun TensorType.autoHessian(variable: TensorType): TensorType =
        wrap(JNoa.autoHessTensor(tensorHandle, variable.tensorHandle))

    public fun TensorType.detachFromGraph(): TensorType =
        wrap(JNoa.detachFromGraph(tensorHandle))

    public fun TensorType.backward(): Unit =
        JNoa.backwardPass(tensorHandle)

    public fun TensorType.grad(): TensorType =
        wrap(JNoa.tensorGrad(tensorHandle))

    public fun NoaJitModule.train(status: Boolean): Unit =
        JNoa.trainMode(jitModuleHandle, status)

    public fun NoaJitModule.adamOptimiser(learningRate: Double): AdamOptimiser =
        AdamOptimiser(scope, JNoa.adamOptim(jitModuleHandle, learningRate))
}

public sealed class NoaDoubleAlgebra
protected constructor(scope: NoaScope) :
    NoaPartialDivisionAlgebra<Double, DoubleArray, NoaDoubleTensor>(scope) {

    private fun Tensor<Double>.castHelper(): NoaDoubleTensor =
        copyFromArray(
            TensorLinearStructure(this.shape).indices().map(this::get).toMutableList().toDoubleArray(),
            this.shape, Device.CPU
        )

    override val Tensor<Double>.tensor: NoaDoubleTensor
        get() = when (this) {
            is NoaDoubleTensor -> this
            else -> castHelper()
        }

    override fun wrap(tensorHandle: TensorHandle): NoaDoubleTensor =
        NoaDoubleTensor(scope = scope, tensorHandle = tensorHandle)

    @PerformancePitfall
    override fun Tensor<Double>.copyToArray(): DoubleArray =
        tensor.elements().map { it.second }.toList().toDoubleArray()

    override fun copyFromArray(array: DoubleArray, shape: IntArray, device: Device): NoaDoubleTensor =
        wrap(JNoa.fromBlobDouble(array, shape, device.toInt()))

    override fun randNormal(shape: IntArray, device: Device): NoaDoubleTensor =
        wrap(JNoa.randnDouble(shape, device.toInt()))

    override fun randUniform(shape: IntArray, device: Device): NoaDoubleTensor =
        wrap(JNoa.randDouble(shape, device.toInt()))

    override fun randDiscrete(low: Long, high: Long, shape: IntArray, device: Device): NoaDoubleTensor =
        wrap(JNoa.randintDouble(low, high, shape, device.toInt()))

    override operator fun Double.plus(other: Tensor<Double>): NoaDoubleTensor =
        wrap(JNoa.plusDouble(this, other.tensor.tensorHandle))

    override fun Tensor<Double>.plus(value: Double): NoaDoubleTensor =
        wrap(JNoa.plusDouble(value, tensor.tensorHandle))

    override fun Tensor<Double>.plusAssign(value: Double): Unit =
        JNoa.plusDoubleAssign(value, tensor.tensorHandle)

    override operator fun Double.minus(other: Tensor<Double>): NoaDoubleTensor =
        wrap(JNoa.plusDouble(-this, other.tensor.tensorHandle))

    override fun Tensor<Double>.minus(value: Double): NoaDoubleTensor =
        wrap(JNoa.plusDouble(-value, tensor.tensorHandle))

    override fun Tensor<Double>.minusAssign(value: Double): Unit =
        JNoa.plusDoubleAssign(-value, tensor.tensorHandle)

    override operator fun Double.times(other: Tensor<Double>): NoaDoubleTensor =
        wrap(JNoa.timesDouble(this, other.tensor.tensorHandle))

    override fun Tensor<Double>.times(value: Double): NoaDoubleTensor =
        wrap(JNoa.timesDouble(value, tensor.tensorHandle))

    override fun Tensor<Double>.timesAssign(value: Double): Unit =
        JNoa.timesDoubleAssign(value, tensor.tensorHandle)

    override fun Double.div(other: Tensor<Double>): NoaDoubleTensor =
        other.tensor * (1 / this)

    override fun Tensor<Double>.div(value: Double): NoaDoubleTensor =
        tensor * (1 / value)

    override fun Tensor<Double>.divAssign(value: Double): Unit =
        tensor.timesAssign(1 / value)

    override fun full(value: Double, shape: IntArray, device: Device): NoaDoubleTensor =
        wrap(JNoa.fullDouble(value, shape, device.toInt()))

    override fun loadJitModule(path: String, device: Device): NoaJitModule =
        NoaJitModule(scope, JNoa.loadJitModuleDouble(path, device.toInt()))
}

public sealed class NoaFloatAlgebra
protected constructor(scope: NoaScope) :
    NoaPartialDivisionAlgebra<Float, FloatArray, NoaFloatTensor>(scope) {

    private fun Tensor<Float>.castHelper(): NoaFloatTensor =
        copyFromArray(
            TensorLinearStructure(this.shape).indices().map(this::get).toMutableList().toFloatArray(),
            this.shape, Device.CPU
        )

    override val Tensor<Float>.tensor: NoaFloatTensor
        get() = when (this) {
            is NoaFloatTensor -> this
            else -> castHelper()
        }

    override fun wrap(tensorHandle: TensorHandle): NoaFloatTensor =
        NoaFloatTensor(scope = scope, tensorHandle = tensorHandle)

    @PerformancePitfall
    override fun Tensor<Float>.copyToArray(): FloatArray =
        tensor.elements().map { it.second }.toList().toFloatArray()

    override fun copyFromArray(array: FloatArray, shape: IntArray, device: Device): NoaFloatTensor =
        wrap(JNoa.fromBlobFloat(array, shape, device.toInt()))

    override fun randNormal(shape: IntArray, device: Device): NoaFloatTensor =
        wrap(JNoa.randnFloat(shape, device.toInt()))

    override fun randUniform(shape: IntArray, device: Device): NoaFloatTensor =
        wrap(JNoa.randFloat(shape, device.toInt()))

    override fun randDiscrete(low: Long, high: Long, shape: IntArray, device: Device): NoaFloatTensor =
        wrap(JNoa.randintFloat(low, high, shape, device.toInt()))

    override operator fun Float.plus(other: Tensor<Float>): NoaFloatTensor =
        wrap(JNoa.plusFloat(this, other.tensor.tensorHandle))

    override fun Tensor<Float>.plus(value: Float): NoaFloatTensor =
        wrap(JNoa.plusFloat(value, tensor.tensorHandle))

    override fun Tensor<Float>.plusAssign(value: Float): Unit =
        JNoa.plusFloatAssign(value, tensor.tensorHandle)

    override operator fun Float.minus(other: Tensor<Float>): NoaFloatTensor =
        wrap(JNoa.plusFloat(-this, other.tensor.tensorHandle))

    override fun Tensor<Float>.minus(value: Float): NoaFloatTensor =
        wrap(JNoa.plusFloat(-value, tensor.tensorHandle))

    override fun Tensor<Float>.minusAssign(value: Float): Unit =
        JNoa.plusFloatAssign(-value, tensor.tensorHandle)

    override operator fun Float.times(other: Tensor<Float>): NoaFloatTensor =
        wrap(JNoa.timesFloat(this, other.tensor.tensorHandle))

    override fun Tensor<Float>.times(value: Float): NoaFloatTensor =
        wrap(JNoa.timesFloat(value, tensor.tensorHandle))

    override fun Tensor<Float>.timesAssign(value: Float): Unit =
        JNoa.timesFloatAssign(value, tensor.tensorHandle)

    override fun Float.div(other: Tensor<Float>): NoaFloatTensor =
        other.tensor * (1 / this)

    override fun Tensor<Float>.div(value: Float): NoaFloatTensor =
        tensor * (1 / value)

    override fun Tensor<Float>.divAssign(value: Float): Unit =
        tensor.timesAssign(1 / value)

    override fun full(value: Float, shape: IntArray, device: Device): NoaFloatTensor =
        wrap(JNoa.fullFloat(value, shape, device.toInt()))

    override fun loadJitModule(path: String, device: Device): NoaJitModule =
        NoaJitModule(scope, JNoa.loadJitModuleFloat(path, device.toInt()))

}

public sealed class NoaLongAlgebra
protected constructor(scope: NoaScope) :
    NoaAlgebra<Long, LongArray, NoaLongTensor>(scope) {

    private fun Tensor<Long>.castHelper(): NoaLongTensor =
        copyFromArray(
            TensorLinearStructure(this.shape).indices().map(this::get).toMutableList().toLongArray(),
            this.shape, Device.CPU
        )

    override val Tensor<Long>.tensor: NoaLongTensor
        get() = when (this) {
            is NoaLongTensor -> this
            else -> castHelper()
        }

    override fun wrap(tensorHandle: TensorHandle): NoaLongTensor =
        NoaLongTensor(scope = scope, tensorHandle = tensorHandle)

    @PerformancePitfall
    override fun Tensor<Long>.copyToArray(): LongArray =
        tensor.elements().map { it.second }.toList().toLongArray()

    override fun copyFromArray(array: LongArray, shape: IntArray, device: Device): NoaLongTensor =
        wrap(JNoa.fromBlobLong(array, shape, device.toInt()))

    override fun randDiscrete(low: Long, high: Long, shape: IntArray, device: Device): NoaLongTensor =
        wrap(JNoa.randintLong(low, high, shape, device.toInt()))

    override operator fun Long.plus(other: Tensor<Long>): NoaLongTensor =
        wrap(JNoa.plusLong(this, other.tensor.tensorHandle))

    override fun Tensor<Long>.plus(value: Long): NoaLongTensor =
        wrap(JNoa.plusLong(value, tensor.tensorHandle))

    override fun Tensor<Long>.plusAssign(value: Long): Unit =
        JNoa.plusLongAssign(value, tensor.tensorHandle)

    override operator fun Long.minus(other: Tensor<Long>): NoaLongTensor =
        wrap(JNoa.plusLong(-this, other.tensor.tensorHandle))

    override fun Tensor<Long>.minus(value: Long): NoaLongTensor =
        wrap(JNoa.plusLong(-value, tensor.tensorHandle))

    override fun Tensor<Long>.minusAssign(value: Long): Unit =
        JNoa.plusLongAssign(-value, tensor.tensorHandle)

    override operator fun Long.times(other: Tensor<Long>): NoaLongTensor =
        wrap(JNoa.timesLong(this, other.tensor.tensorHandle))

    override fun Tensor<Long>.times(value: Long): NoaLongTensor =
        wrap(JNoa.timesLong(value, tensor.tensorHandle))

    override fun Tensor<Long>.timesAssign(value: Long): Unit =
        JNoa.timesLongAssign(value, tensor.tensorHandle)

    override fun full(value: Long, shape: IntArray, device: Device): NoaLongTensor =
        wrap(JNoa.fullLong(value, shape, device.toInt()))

    override fun loadJitModule(path: String, device: Device): NoaJitModule =
        NoaJitModule(scope, JNoa.loadJitModuleLong(path, device.toInt()))

}

public sealed class NoaIntAlgebra
protected constructor(scope: NoaScope) :
    NoaAlgebra<Int, IntArray, NoaIntTensor>(scope) {

    private fun Tensor<Int>.castHelper(): NoaIntTensor =
        copyFromArray(
            TensorLinearStructure(this.shape).indices().map(this::get).toMutableList().toIntArray(),
            this.shape, Device.CPU
        )

    override val Tensor<Int>.tensor: NoaIntTensor
        get() = when (this) {
            is NoaIntTensor -> this
            else -> castHelper()
        }

    override fun wrap(tensorHandle: TensorHandle): NoaIntTensor =
        NoaIntTensor(scope = scope, tensorHandle = tensorHandle)

    @PerformancePitfall
    override fun Tensor<Int>.copyToArray(): IntArray =
        tensor.elements().map { it.second }.toList().toIntArray()

    override fun copyFromArray(array: IntArray, shape: IntArray, device: Device): NoaIntTensor =
        wrap(JNoa.fromBlobInt(array, shape, device.toInt()))

    override fun randDiscrete(low: Long, high: Long, shape: IntArray, device: Device): NoaIntTensor =
        wrap(JNoa.randintInt(low, high, shape, device.toInt()))

    override operator fun Int.plus(other: Tensor<Int>): NoaIntTensor =
        wrap(JNoa.plusInt(this, other.tensor.tensorHandle))

    override fun Tensor<Int>.plus(value: Int): NoaIntTensor =
        wrap(JNoa.plusInt(value, tensor.tensorHandle))

    override fun Tensor<Int>.plusAssign(value: Int): Unit =
        JNoa.plusIntAssign(value, tensor.tensorHandle)

    override operator fun Int.minus(other: Tensor<Int>): NoaIntTensor =
        wrap(JNoa.plusInt(-this, other.tensor.tensorHandle))

    override fun Tensor<Int>.minus(value: Int): NoaIntTensor =
        wrap(JNoa.plusInt(-value, tensor.tensorHandle))

    override fun Tensor<Int>.minusAssign(value: Int): Unit =
        JNoa.plusIntAssign(-value, tensor.tensorHandle)

    override operator fun Int.times(other: Tensor<Int>): NoaIntTensor =
        wrap(JNoa.timesInt(this, other.tensor.tensorHandle))

    override fun Tensor<Int>.times(value: Int): NoaIntTensor =
        wrap(JNoa.timesInt(value, tensor.tensorHandle))

    override fun Tensor<Int>.timesAssign(value: Int): Unit =
        JNoa.timesIntAssign(value, tensor.tensorHandle)

    override fun full(value: Int, shape: IntArray, device: Device): NoaIntTensor =
        wrap(JNoa.fullInt(value, shape, device.toInt()))

    override fun loadJitModule(path: String, device: Device): NoaJitModule =
        NoaJitModule(scope, JNoa.loadJitModuleInt(path, device.toInt()))

}
