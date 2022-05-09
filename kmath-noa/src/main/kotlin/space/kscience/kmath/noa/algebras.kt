/*
 * Copyright 2018-2021 KMath contributors.
 * Use of tensor source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.noa

import space.kscience.kmath.misc.PerformancePitfall
import space.kscience.kmath.nd.StructureND
import space.kscience.kmath.noa.memory.NoaScope
import space.kscience.kmath.operations.*
import space.kscience.kmath.tensors.api.AnalyticTensorAlgebra
import space.kscience.kmath.tensors.api.LinearOpsTensorAlgebra
import space.kscience.kmath.tensors.api.Tensor
import space.kscience.kmath.tensors.api.TensorAlgebra
import space.kscience.kmath.tensors.core.TensorLinearStructure

internal typealias Slice = Pair<Int, Int>

public sealed class NoaAlgebra<T, A : Ring<T>, PrimitiveArray, TensorType : NoaTensor<T>>
protected constructor(protected val scope: NoaScope) :
    TensorAlgebra<T, A> {

    protected abstract val StructureND<T>.tensor: TensorType

    protected abstract fun wrap(tensorHandle: TensorHandle): TensorType

    @PerformancePitfall
    public fun Tensor<T>.cast(): TensorType = tensor

    /**
     * A scalar tensor must have empty shape
     */
    override fun StructureND<T>.valueOrNull(): T? =
        try {
            tensor.item()
        } catch (e: NoaException) {
            null
        }

    override fun StructureND<T>.value(): T = tensor.item()

    public abstract fun randDiscrete(low: Long, high: Long, shape: IntArray, device: Device = Device.CPU): TensorType

    public abstract fun TensorType.copyToArray(): PrimitiveArray

    public abstract fun copyFromArray(array: PrimitiveArray, shape: IntArray, device: Device = Device.CPU): TensorType

    public abstract fun full(value: T, shape: IntArray, device: Device = Device.CPU): TensorType

    override operator fun StructureND<T>.times(arg: StructureND<T>): TensorType {
        return wrap(JNoa.timesTensor(tensor.tensorHandle, arg.tensor.tensorHandle))
    }

    override operator fun Tensor<T>.timesAssign(arg: StructureND<T>): Unit {
        JNoa.timesTensorAssign(tensor.tensorHandle, arg.tensor.tensorHandle)
    }

    override operator fun StructureND<T>.plus(arg: StructureND<T>): TensorType {
        return wrap(JNoa.plusTensor(tensor.tensorHandle, arg.tensor.tensorHandle))
    }

    override operator fun Tensor<T>.plusAssign(arg: StructureND<T>): Unit {
        JNoa.plusTensorAssign(tensor.tensorHandle, arg.tensor.tensorHandle)
    }

    override operator fun StructureND<T>.minus(arg: StructureND<T>): TensorType {
        return wrap(JNoa.minusTensor(tensor.tensorHandle, arg.tensor.tensorHandle))
    }

    override operator fun Tensor<T>.minusAssign(arg: StructureND<T>): Unit {
        JNoa.minusTensorAssign(tensor.tensorHandle, arg.tensor.tensorHandle)
    }

    override operator fun StructureND<T>.unaryMinus(): TensorType =
        wrap(JNoa.unaryMinus(tensor.tensorHandle))

    override infix fun StructureND<T>.dot(other: StructureND<T>): TensorType {
        return wrap(JNoa.matmul(tensor.tensorHandle, other.tensor.tensorHandle))
    }

    public infix fun Tensor<T>.dotAssign(arg: StructureND<T>): Unit {
        JNoa.matmulAssign(tensor.tensorHandle, arg.tensor.tensorHandle)
    }

    public infix fun StructureND<T>.dotRightAssign(arg: Tensor<T>): Unit {
        JNoa.matmulRightAssign(tensor.tensorHandle, arg.tensor.tensorHandle)
    }

    override operator fun Tensor<T>.get(i: Int): TensorType =
        wrap(JNoa.getIndex(tensor.tensorHandle, i))

    public operator fun TensorType.set(i: Int, value: Tensor<T>): Unit =
        JNoa.setTensor(tensorHandle, i, value.tensor.tensorHandle)

    public abstract operator fun TensorType.set(i: Int, array: PrimitiveArray): Unit

    public operator fun Tensor<T>.get(dim: Int, slice: Slice): TensorType =
        wrap(JNoa.getSliceTensor(tensor.tensorHandle, dim, slice.first, slice.second))

    public operator fun TensorType.set(dim: Int, slice: Slice, value: Tensor<T>): Unit =
        JNoa.setSliceTensor(tensorHandle, dim, slice.first, slice.second, value.tensor.tensorHandle)

    public abstract operator fun TensorType.set(dim: Int, slice: Slice, array: PrimitiveArray): Unit

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

    override fun Tensor<T>.viewAs(other: StructureND<T>): TensorType {
        return wrap(JNoa.viewAsTensor(tensor.tensorHandle, other.tensor.tensorHandle))
    }

    public fun StructureND<T>.abs(): TensorType = wrap(JNoa.absTensor(tensor.tensorHandle))

    public fun StructureND<T>.sumAll(): TensorType = wrap(JNoa.sumTensor(tensor.tensorHandle))
    override fun StructureND<T>.sum(): T = sumAll().item()
    override fun StructureND<T>.sum(dim: Int, keepDim: Boolean): TensorType =
        wrap(JNoa.sumDimTensor(tensor.tensorHandle, dim, keepDim))

    public fun StructureND<T>.minAll(): TensorType = wrap(JNoa.minTensor(tensor.tensorHandle))
    override fun StructureND<T>.min(): T = minAll().item()
    override fun StructureND<T>.min(dim: Int, keepDim: Boolean): TensorType =
        wrap(JNoa.minDimTensor(tensor.tensorHandle, dim, keepDim))

    public fun StructureND<T>.maxAll(): TensorType = wrap(JNoa.maxTensor(tensor.tensorHandle))
    override fun StructureND<T>.max(): T = maxAll().item()
    override fun StructureND<T>.max(dim: Int, keepDim: Boolean): TensorType =
        wrap(JNoa.maxDimTensor(tensor.tensorHandle, dim, keepDim))

    override fun StructureND<T>.argMax(dim: Int, keepDim: Boolean): NoaIntTensor =
        NoaIntTensor(scope, JNoa.argMaxTensor(tensor.tensorHandle, dim, keepDim))

    public fun Tensor<T>.flatten(startDim: Int, endDim: Int): TensorType =
        wrap(JNoa.flattenTensor(tensor.tensorHandle, startDim, endDim))

    public fun Tensor<T>.randDiscrete(low: Long, high: Long): TensorType =
        wrap(JNoa.randintLike(tensor.tensorHandle, low, high))

    public fun Tensor<T>.randDiscreteAssign(low: Long, high: Long): Unit =
        JNoa.randintLikeAssign(tensor.tensorHandle, low, high)

    public fun Tensor<T>.copy(): TensorType =
        wrap(JNoa.copyTensor(tensor.tensorHandle))

    public fun Tensor<T>.copyToDevice(device: Device = Device.CPU): TensorType =
        wrap(JNoa.copyToDevice(tensor.tensorHandle, device.toInt()))

    public abstract fun loadJitModule(path: String, device: Device = Device.CPU): NoaJitModule

    public abstract fun loadTensor(path: String, device: Device = Device.CPU): TensorType

    public fun NoaJitModule.forward(features: Tensor<T>): TensorType =
        wrap(JNoa.forwardPass(jitModuleHandle, features.tensor.tensorHandle))

    public fun NoaJitModule.forwardAssign(features: TensorType, predictions: TensorType): Unit =
        JNoa.forwardPassAssign(jitModuleHandle, features.tensorHandle, predictions.tensorHandle)

    public fun NoaJitModule.getParameter(name: String): TensorType =
        wrap(JNoa.getModuleParameter(jitModuleHandle, name))

    public fun NoaJitModule.setParameter(name: String, parameter: Tensor<T>): Unit =
        JNoa.setModuleParameter(jitModuleHandle, name, parameter.tensor.tensorHandle)

    public fun NoaJitModule.getBuffer(name: String): TensorType =
        wrap(JNoa.getModuleBuffer(jitModuleHandle, name))

    public fun NoaJitModule.setBuffer(name: String, buffer: Tensor<T>): Unit =
        JNoa.setModuleBuffer(jitModuleHandle, name, buffer.tensor.tensorHandle)

    public infix fun TensorType.swap(arg: TensorType): Unit =
        JNoa.swapTensors(tensorHandle, arg.tensorHandle)

    public abstract fun TensorType.assignFromArray(array: PrimitiveArray): Unit

}

public sealed class NoaPartialDivisionAlgebra<T, A : Field<T>, PrimitiveArray, TensorType : NoaTensor<T>>
protected constructor(scope: NoaScope) :
    NoaAlgebra<T, A, PrimitiveArray, TensorType>(scope),
    LinearOpsTensorAlgebra<T, A>,
    AnalyticTensorAlgebra<T, A> {

    override operator fun StructureND<T>.div(arg: StructureND<T>): TensorType {
        return wrap(JNoa.divTensor(tensor.tensorHandle, arg.tensor.tensorHandle))
    }

    override operator fun Tensor<T>.divAssign(arg: StructureND<T>): Unit {
        JNoa.divTensorAssign(tensor.tensorHandle, arg.tensor.tensorHandle)
    }

    public fun StructureND<T>.meanAll(): TensorType = wrap(JNoa.meanTensor(tensor.tensorHandle))
    override fun StructureND<T>.mean(): T = meanAll().item()
    override fun StructureND<T>.mean(dim: Int, keepDim: Boolean): TensorType =
        wrap(JNoa.meanDimTensor(tensor.tensorHandle, dim, keepDim))

    public fun StructureND<T>.stdAll(): TensorType = wrap(JNoa.stdTensor(tensor.tensorHandle))
    override fun StructureND<T>.std(): T = stdAll().item()
    override fun StructureND<T>.std(dim: Int, keepDim: Boolean): TensorType =
        wrap(JNoa.stdDimTensor(tensor.tensorHandle, dim, keepDim))

    public fun StructureND<T>.varAll(): TensorType = wrap(JNoa.varTensor(tensor.tensorHandle))
    override fun StructureND<T>.variance(): T = varAll().item()
    override fun StructureND<T>.variance(dim: Int, keepDim: Boolean): TensorType =
        wrap(JNoa.varDimTensor(tensor.tensorHandle, dim, keepDim))

    public abstract fun randNormal(shape: IntArray, device: Device = Device.CPU): TensorType

    public abstract fun randUniform(shape: IntArray, device: Device = Device.CPU): TensorType

    public fun StructureND<T>.randUniform(): TensorType =
        wrap(JNoa.randLike(tensor.tensorHandle))

    public fun StructureND<T>.randUniformAssign(): Unit =
        JNoa.randLikeAssign(tensor.tensorHandle)

    public fun StructureND<T>.randNormal(): TensorType =
        wrap(JNoa.randnLike(tensor.tensorHandle))

    public fun StructureND<T>.randNormalAssign(): Unit =
        JNoa.randnLikeAssign(tensor.tensorHandle)

    override fun StructureND<T>.exp(): TensorType =
        wrap(JNoa.expTensor(tensor.tensorHandle))

    override fun StructureND<T>.ln(): TensorType =
        wrap(JNoa.lnTensor(tensor.tensorHandle))

    override fun StructureND<T>.sqrt(): TensorType =
        wrap(JNoa.sqrtTensor(tensor.tensorHandle))

    override fun StructureND<T>.cos(): TensorType =
        wrap(JNoa.cosTensor(tensor.tensorHandle))

    override fun StructureND<T>.acos(): TensorType =
        wrap(JNoa.acosTensor(tensor.tensorHandle))

    override fun StructureND<T>.cosh(): TensorType =
        wrap(JNoa.coshTensor(tensor.tensorHandle))

    override fun StructureND<T>.acosh(): TensorType =
        wrap(JNoa.acoshTensor(tensor.tensorHandle))

    override fun StructureND<T>.sin(): TensorType =
        wrap(JNoa.sinTensor(tensor.tensorHandle))

    override fun StructureND<T>.asin(): TensorType =
        wrap(JNoa.asinTensor(tensor.tensorHandle))

    override fun StructureND<T>.sinh(): TensorType =
        wrap(JNoa.sinhTensor(tensor.tensorHandle))

    override fun StructureND<T>.asinh(): TensorType =
        wrap(JNoa.asinhTensor(tensor.tensorHandle))

    override fun StructureND<T>.tan(): TensorType =
        wrap(JNoa.tanTensor(tensor.tensorHandle))

    override fun StructureND<T>.atan(): TensorType =
        wrap(JNoa.atanTensor(tensor.tensorHandle))

    override fun StructureND<T>.tanh(): TensorType =
        wrap(JNoa.tanhTensor(tensor.tensorHandle))

    override fun StructureND<T>.atanh(): TensorType =
        wrap(JNoa.atanhTensor(tensor.tensorHandle))

    override fun StructureND<T>.ceil(): TensorType =
        wrap(JNoa.ceilTensor(tensor.tensorHandle))

    override fun StructureND<T>.floor(): TensorType =
        wrap(JNoa.floorTensor(tensor.tensorHandle))

    override fun StructureND<T>.det(): Tensor<T> =
        wrap(JNoa.detTensor(tensor.tensorHandle))

    override fun StructureND<T>.inv(): Tensor<T> =
        wrap(JNoa.invTensor(tensor.tensorHandle))

    override fun StructureND<T>.cholesky(): Tensor<T> =
        wrap(JNoa.choleskyTensor(tensor.tensorHandle))

    override fun StructureND<T>.qr(): Pair<TensorType, TensorType> {
        val Q = JNoa.emptyTensor()
        val R = JNoa.emptyTensor()
        JNoa.qrTensor(tensor.tensorHandle, Q, R)
        return Pair(wrap(Q), wrap(R))
    }

    /**
     * this implementation satisfies `tensor = P dot L dot U`
     */
    override fun StructureND<T>.lu(): Triple<TensorType, TensorType, TensorType> {
        val P = JNoa.emptyTensor()
        val L = JNoa.emptyTensor()
        val U = JNoa.emptyTensor()
        JNoa.luTensor(tensor.tensorHandle, P, L, U)
        return Triple(wrap(P), wrap(L), wrap(U))
    }

    override fun StructureND<T>.svd(): Triple<TensorType, TensorType, TensorType> {
        val U = JNoa.emptyTensor()
        val V = JNoa.emptyTensor()
        val S = JNoa.emptyTensor()
        JNoa.svdTensor(tensor.tensorHandle, U, S, V)
        return Triple(wrap(U), wrap(S), wrap(V))
    }

    override fun StructureND<T>.symEig(): Pair<TensorType, TensorType> {
        val V = JNoa.emptyTensor()
        val S = JNoa.emptyTensor()
        JNoa.symEigTensor(tensor.tensorHandle, S, V)
        return Pair(wrap(S), wrap(V))
    }

    public fun TensorType.autoGradient(variable: TensorType, retainGraph: Boolean = false): TensorType =
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

    /**
     * Implements RMSprop algorithm. Receive `learning rate`, `alpha` (smoothing constant),
     * `eps` (term added to the denominator to improve numerical stability), `weight_decay`,
     * `momentum` factor, `centered` (if True, compute the centered RMSProp).
     * For more information: https://pytorch.org/docs/stable/generated/torch.optim.RMSprop.html
     *
     * @receiver the `learning rate`, `alpha`, `eps`, `weight_decay`, `momentum`, `centered`.
     * @return RMSpropOptimiser.
     */
    public fun NoaJitModule.rmsOptimiser(learningRate: Double, alpha: Double, 
        eps: Double, weightDecay: Double, momentum: Double, centered: Boolean): RMSpropOptimiser =
        RMSpropOptimiser(scope, JNoa.rmsOptim(jitModuleHandle, learningRate, alpha, 
        eps, weightDecay, momentum, centered))

    /**
     * Implements AdamW algorithm. Receive `learning rate`, `beta1` and `beta2` (coefficients used
     * for computing running averages of gradient and its square), `eps` (term added to the denominator
     * to improve numerical stability), `weight_decay`, `amsgrad`.
     * For more information: https://pytorch.org/docs/stable/generated/torch.optim.AdamW.html
     *
     * @receiver the `learning rate`, `beta1`, `beta2`, `eps`, `weight_decay`, `amsgrad`.
     * @return AdamWOptimiser.
     */
    public fun NoaJitModule.adamWOptimiser(learningRate: Double, beta1: Double,
        beta2: Double, eps: Double, weightDecay: Double, amsgrad: Boolean): AdamWOptimiser =
        AdamWOptimiser(scope, JNoa.adamWOptim(jitModuleHandle, learningRate, beta1,
        beta2, eps, weightDecay, amsgrad))

    /**
     * Implements Adagrad algorithm. Receive `learning rate`, `weight_decay`,
     * `learning rate decay`, `initial accumulator value`, `eps`.
     * For more information: https://pytorch.org/docs/stable/generated/torch.optim.Adagrad.html
     *
     * @receiver the `learning rate`, `weight_decay`, `learning rate decay`, `initial accumulator value`, `eps`.
     * @return AdagradOptimiser.
     */
    public fun NoaJitModule.adagradOptimiser(learningRate: Double, weightDecay: Double,
        lrDecay: Double, initialAccumulatorValue: Double, eps: Double): AdagradOptimiser =
        AdagradOptimiser(scope, JNoa.adagradOptim(jitModuleHandle, learningRate, weightDecay,
        lrDecay, initialAccumulatorValue, eps))

    /**
     * Implements stochastic gradient descent. Receive `learning rate`, `momentum` factor,
     * `dampening` for momentum, `weight_decay`, `nesterov` (enables Nesterov momentum).
     * For more information: https://pytorch.org/docs/stable/generated/torch.optim.SGD.html
     *
     * @receiver the `learning rate`, `momentum`, `dampening`, `weight_decay`, `nesterov`.
     * @return SgdOptimiser.
     */
    public fun NoaJitModule.sgdOptimiser(learningRate: Double, momentum: Double,
        dampening: Double, weightDecay: Double, nesterov: Boolean): SgdOptimiser =
        SgdOptimiser(scope, JNoa.sgdOptim(jitModuleHandle, learningRate, momentum,
        dampening, weightDecay, nesterov))
}

public sealed class NoaDoubleAlgebra
protected constructor(scope: NoaScope) :
    NoaPartialDivisionAlgebra<Double, DoubleField, DoubleArray, NoaDoubleTensor>(scope) {

    override val elementAlgebra: DoubleField
        get() = DoubleField

    override fun structureND(shape: IntArray, initializer: DoubleField.(IntArray) -> Double): NoaDoubleTensor =
        copyFromArray(
            TensorLinearStructure(shape).asSequence().map { DoubleField.initializer(it) }.toMutableList()
                .toDoubleArray(),
            shape, Device.CPU
        )

    private fun StructureND<Double>.castHelper(): NoaDoubleTensor =
        copyFromArray(
            TensorLinearStructure(this.shape).asSequence().map(this::get).toMutableList().toDoubleArray(),
            this.shape, Device.CPU
        )

    override val StructureND<Double>.tensor: NoaDoubleTensor
        get() = when (this) {
            is NoaDoubleTensor -> this
            else -> castHelper()
        }

    override fun wrap(tensorHandle: TensorHandle): NoaDoubleTensor =
        NoaDoubleTensor(scope = scope, tensorHandle = tensorHandle)

    override fun NoaDoubleTensor.copyToArray(): DoubleArray {
        val array = DoubleArray(numElements)
        JNoa.getBlobDouble(tensorHandle, array)
        return array
    }

    override fun copyFromArray(array: DoubleArray, shape: IntArray, device: Device): NoaDoubleTensor =
        wrap(JNoa.fromBlobDouble(array, shape, device.toInt()))

    override fun randNormal(shape: IntArray, device: Device): NoaDoubleTensor =
        wrap(JNoa.randnDouble(shape, device.toInt()))

    override fun randUniform(shape: IntArray, device: Device): NoaDoubleTensor =
        wrap(JNoa.randDouble(shape, device.toInt()))

    override fun randDiscrete(low: Long, high: Long, shape: IntArray, device: Device): NoaDoubleTensor =
        wrap(JNoa.randintDouble(low, high, shape, device.toInt()))

    override operator fun Double.plus(arg: StructureND<Double>): NoaDoubleTensor =
        wrap(JNoa.plusDouble(this, arg.tensor.tensorHandle))

    override fun StructureND<Double>.plus(value: Double): NoaDoubleTensor =
        wrap(JNoa.plusDouble(value, tensor.tensorHandle))

    override fun Tensor<Double>.plusAssign(value: Double): Unit =
        JNoa.plusDoubleAssign(value, tensor.tensorHandle)

    override operator fun Double.minus(arg: StructureND<Double>): NoaDoubleTensor =
        wrap(JNoa.plusDouble(-this, arg.tensor.tensorHandle))

    override fun StructureND<Double>.minus(value: Double): NoaDoubleTensor =
        wrap(JNoa.plusDouble(-value, tensor.tensorHandle))

    override fun Tensor<Double>.minusAssign(value: Double): Unit =
        JNoa.plusDoubleAssign(-value, tensor.tensorHandle)

    override operator fun Double.times(arg: StructureND<Double>): NoaDoubleTensor =
        wrap(JNoa.timesDouble(this, arg.tensor.tensorHandle))

    override fun StructureND<Double>.times(value: Double): NoaDoubleTensor =
        wrap(JNoa.timesDouble(value, tensor.tensorHandle))

    override fun Tensor<Double>.timesAssign(value: Double): Unit =
        JNoa.timesDoubleAssign(value, tensor.tensorHandle)

    override fun Double.div(arg: StructureND<Double>): NoaDoubleTensor =
        arg.tensor * (1 / this)

    override fun StructureND<Double>.div(value: Double): NoaDoubleTensor =
        tensor * (1 / value)

    override fun Tensor<Double>.divAssign(value: Double): Unit =
        tensor.timesAssign(1 / value)

    override fun full(value: Double, shape: IntArray, device: Device): NoaDoubleTensor =
        wrap(JNoa.fullDouble(value, shape, device.toInt()))

    override fun loadJitModule(path: String, device: Device): NoaJitModule =
        NoaJitModule(scope, JNoa.loadJitModuleDouble(path, device.toInt()))

    override fun loadTensor(path: String, device: Device): NoaDoubleTensor =
        wrap(JNoa.loadTensorDouble(path, device.toInt()))

    override fun NoaDoubleTensor.assignFromArray(array: DoubleArray): Unit =
        JNoa.assignBlobDouble(tensorHandle, array)

    override fun NoaDoubleTensor.set(i: Int, array: DoubleArray): Unit =
        JNoa.setBlobDouble(tensorHandle, i, array)

    override fun NoaDoubleTensor.set(dim: Int, slice: Slice, array: DoubleArray): Unit =
        JNoa.setSliceBlobDouble(tensorHandle, dim, slice.first, slice.second, array)
}

public sealed class NoaFloatAlgebra
protected constructor(scope: NoaScope) :
    NoaPartialDivisionAlgebra<Float, FloatField, FloatArray, NoaFloatTensor>(scope) {

    override val elementAlgebra: FloatField
        get() = FloatField

    override fun structureND(shape: IntArray, initializer: FloatField.(IntArray) -> Float): NoaFloatTensor =
        copyFromArray(
            TensorLinearStructure(shape).asSequence().map { FloatField.initializer(it) }.toMutableList()
                .toFloatArray(),
            shape, Device.CPU
        )

    private fun StructureND<Float>.castHelper(): NoaFloatTensor =
        copyFromArray(
            TensorLinearStructure(this.shape).asSequence().map(this::get).toMutableList().toFloatArray(),
            this.shape, Device.CPU
        )

    override val StructureND<Float>.tensor: NoaFloatTensor
        get() = when (this) {
            is NoaFloatTensor -> this
            else -> castHelper()
        }

    override fun wrap(tensorHandle: TensorHandle): NoaFloatTensor =
        NoaFloatTensor(scope = scope, tensorHandle = tensorHandle)

    override fun NoaFloatTensor.copyToArray(): FloatArray {
        val res = FloatArray(numElements)
        JNoa.getBlobFloat(tensorHandle, res)
        return res
    }

    override fun copyFromArray(array: FloatArray, shape: IntArray, device: Device): NoaFloatTensor =
        wrap(JNoa.fromBlobFloat(array, shape, device.toInt()))

    override fun randNormal(shape: IntArray, device: Device): NoaFloatTensor =
        wrap(JNoa.randnFloat(shape, device.toInt()))

    override fun randUniform(shape: IntArray, device: Device): NoaFloatTensor =
        wrap(JNoa.randFloat(shape, device.toInt()))

    override fun randDiscrete(low: Long, high: Long, shape: IntArray, device: Device): NoaFloatTensor =
        wrap(JNoa.randintFloat(low, high, shape, device.toInt()))

    override operator fun Float.plus(arg: StructureND<Float>): NoaFloatTensor =
        wrap(JNoa.plusFloat(this, arg.tensor.tensorHandle))

    override fun StructureND<Float>.plus(value: Float): NoaFloatTensor =
        wrap(JNoa.plusFloat(value, tensor.tensorHandle))

    override fun Tensor<Float>.plusAssign(value: Float): Unit =
        JNoa.plusFloatAssign(value, tensor.tensorHandle)

    override operator fun Float.minus(arg: StructureND<Float>): NoaFloatTensor =
        wrap(JNoa.plusFloat(-this, arg.tensor.tensorHandle))

    override fun StructureND<Float>.minus(value: Float): NoaFloatTensor =
        wrap(JNoa.plusFloat(-value, tensor.tensorHandle))

    override fun Tensor<Float>.minusAssign(value: Float): Unit =
        JNoa.plusFloatAssign(-value, tensor.tensorHandle)

    override operator fun Float.times(arg: StructureND<Float>): NoaFloatTensor =
        wrap(JNoa.timesFloat(this, arg.tensor.tensorHandle))

    override fun StructureND<Float>.times(value: Float): NoaFloatTensor =
        wrap(JNoa.timesFloat(value, tensor.tensorHandle))

    override fun Tensor<Float>.timesAssign(value: Float): Unit =
        JNoa.timesFloatAssign(value, tensor.tensorHandle)

    override fun Float.div(arg: StructureND<Float>): NoaFloatTensor =
        arg.tensor * (1 / this)

    override fun StructureND<Float>.div(value: Float): NoaFloatTensor =
        tensor * (1 / value)

    override fun Tensor<Float>.divAssign(value: Float): Unit =
        tensor.timesAssign(1 / value)

    override fun full(value: Float, shape: IntArray, device: Device): NoaFloatTensor =
        wrap(JNoa.fullFloat(value, shape, device.toInt()))

    override fun loadJitModule(path: String, device: Device): NoaJitModule =
        NoaJitModule(scope, JNoa.loadJitModuleFloat(path, device.toInt()))

    override fun loadTensor(path: String, device: Device): NoaFloatTensor =
        wrap(JNoa.loadTensorFloat(path, device.toInt()))

    override fun NoaFloatTensor.assignFromArray(array: FloatArray): Unit =
        JNoa.assignBlobFloat(tensorHandle, array)

    override fun NoaFloatTensor.set(i: Int, array: FloatArray): Unit =
        JNoa.setBlobFloat(tensorHandle, i, array)

    override fun NoaFloatTensor.set(dim: Int, slice: Slice, array: FloatArray): Unit =
        JNoa.setSliceBlobFloat(tensorHandle, dim, slice.first, slice.second, array)

}

public sealed class NoaLongAlgebra
protected constructor(scope: NoaScope) :
    NoaAlgebra<Long, LongRing, LongArray, NoaLongTensor>(scope) {

    override val elementAlgebra: LongRing
        get() = LongRing

    override fun structureND(shape: IntArray, initializer: LongRing.(IntArray) -> Long): NoaLongTensor =
        copyFromArray(
            TensorLinearStructure(shape).asSequence().map { LongRing.initializer(it) }.toMutableList()
                .toLongArray(),
            shape, Device.CPU
        )

    private fun StructureND<Long>.castHelper(): NoaLongTensor =
        copyFromArray(
            TensorLinearStructure(this.shape).asSequence().map(this::get).toMutableList().toLongArray(),
            this.shape, Device.CPU
        )

    override val StructureND<Long>.tensor: NoaLongTensor
        get() = when (this) {
            is NoaLongTensor -> this
            else -> castHelper()
        }

    override fun wrap(tensorHandle: TensorHandle): NoaLongTensor =
        NoaLongTensor(scope = scope, tensorHandle = tensorHandle)

    override fun NoaLongTensor.copyToArray(): LongArray {
        val array = LongArray(numElements)
        JNoa.getBlobLong(tensorHandle, array)
        return array
    }

    override fun copyFromArray(array: LongArray, shape: IntArray, device: Device): NoaLongTensor =
        wrap(JNoa.fromBlobLong(array, shape, device.toInt()))

    override fun randDiscrete(low: Long, high: Long, shape: IntArray, device: Device): NoaLongTensor =
        wrap(JNoa.randintLong(low, high, shape, device.toInt()))

    override operator fun Long.plus(arg: StructureND<Long>): NoaLongTensor =
        wrap(JNoa.plusLong(this, arg.tensor.tensorHandle))

    override fun StructureND<Long>.plus(value: Long): NoaLongTensor =
        wrap(JNoa.plusLong(value, tensor.tensorHandle))

    override fun Tensor<Long>.plusAssign(value: Long): Unit =
        JNoa.plusLongAssign(value, tensor.tensorHandle)

    override operator fun Long.minus(arg: StructureND<Long>): NoaLongTensor =
        wrap(JNoa.plusLong(-this, arg.tensor.tensorHandle))

    override fun StructureND<Long>.minus(value: Long): NoaLongTensor =
        wrap(JNoa.plusLong(-value, tensor.tensorHandle))

    override fun Tensor<Long>.minusAssign(value: Long): Unit =
        JNoa.plusLongAssign(-value, tensor.tensorHandle)

    override operator fun Long.times(arg: StructureND<Long>): NoaLongTensor =
        wrap(JNoa.timesLong(this, arg.tensor.tensorHandle))

    override fun StructureND<Long>.times(value: Long): NoaLongTensor =
        wrap(JNoa.timesLong(value, tensor.tensorHandle))

    override fun Tensor<Long>.timesAssign(value: Long): Unit =
        JNoa.timesLongAssign(value, tensor.tensorHandle)

    override fun full(value: Long, shape: IntArray, device: Device): NoaLongTensor =
        wrap(JNoa.fullLong(value, shape, device.toInt()))

    override fun loadJitModule(path: String, device: Device): NoaJitModule =
        NoaJitModule(scope, JNoa.loadJitModuleLong(path, device.toInt()))

    override fun loadTensor(path: String, device: Device): NoaLongTensor =
        wrap(JNoa.loadTensorLong(path, device.toInt()))

    override fun NoaLongTensor.assignFromArray(array: LongArray): Unit =
        JNoa.assignBlobLong(tensorHandle, array)

    override fun NoaLongTensor.set(i: Int, array: LongArray): Unit =
        JNoa.setBlobLong(tensorHandle, i, array)

    override fun NoaLongTensor.set(dim: Int, slice: Slice, array: LongArray): Unit =
        JNoa.setSliceBlobLong(tensorHandle, dim, slice.first, slice.second, array)
}

public sealed class NoaIntAlgebra
protected constructor(scope: NoaScope) :
    NoaAlgebra<Int, IntRing, IntArray, NoaIntTensor>(scope) {

    override val elementAlgebra: IntRing
        get() = IntRing

    override fun structureND(shape: IntArray, initializer: IntRing.(IntArray) -> Int): NoaIntTensor =
        copyFromArray(
            TensorLinearStructure(shape).asSequence().map { IntRing.initializer(it) }.toMutableList()
                .toIntArray(),
            shape, Device.CPU
        )

    private fun StructureND<Int>.castHelper(): NoaIntTensor =
        copyFromArray(
            TensorLinearStructure(this.shape).asSequence().map(this::get).toMutableList().toIntArray(),
            this.shape, Device.CPU
        )

    override val StructureND<Int>.tensor: NoaIntTensor
        get() = when (this) {
            is NoaIntTensor -> this
            else -> castHelper()
        }

    override fun wrap(tensorHandle: TensorHandle): NoaIntTensor =
        NoaIntTensor(scope = scope, tensorHandle = tensorHandle)

    override fun NoaIntTensor.copyToArray(): IntArray {
        val array = IntArray(numElements)
        JNoa.getBlobInt(tensorHandle, array)
        return array
    }

    override fun copyFromArray(array: IntArray, shape: IntArray, device: Device): NoaIntTensor =
        wrap(JNoa.fromBlobInt(array, shape, device.toInt()))

    override fun randDiscrete(low: Long, high: Long, shape: IntArray, device: Device): NoaIntTensor =
        wrap(JNoa.randintInt(low, high, shape, device.toInt()))

    override operator fun Int.plus(arg: StructureND<Int>): NoaIntTensor =
        wrap(JNoa.plusInt(this, arg.tensor.tensorHandle))

    override fun StructureND<Int>.plus(value: Int): NoaIntTensor =
        wrap(JNoa.plusInt(value, tensor.tensorHandle))

    override fun Tensor<Int>.plusAssign(value: Int): Unit =
        JNoa.plusIntAssign(value, tensor.tensorHandle)

    override operator fun Int.minus(arg: StructureND<Int>): NoaIntTensor =
        wrap(JNoa.plusInt(-this, arg.tensor.tensorHandle))

    override fun StructureND<Int>.minus(value: Int): NoaIntTensor =
        wrap(JNoa.plusInt(-value, tensor.tensorHandle))

    override fun Tensor<Int>.minusAssign(value: Int): Unit =
        JNoa.plusIntAssign(-value, tensor.tensorHandle)

    override operator fun Int.times(arg: StructureND<Int>): NoaIntTensor =
        wrap(JNoa.timesInt(this, arg.tensor.tensorHandle))

    override fun StructureND<Int>.times(value: Int): NoaIntTensor =
        wrap(JNoa.timesInt(value, tensor.tensorHandle))

    override fun Tensor<Int>.timesAssign(value: Int): Unit =
        JNoa.timesIntAssign(value, tensor.tensorHandle)

    override fun full(value: Int, shape: IntArray, device: Device): NoaIntTensor =
        wrap(JNoa.fullInt(value, shape, device.toInt()))

    override fun loadJitModule(path: String, device: Device): NoaJitModule =
        NoaJitModule(scope, JNoa.loadJitModuleInt(path, device.toInt()))

    override fun loadTensor(path: String, device: Device): NoaIntTensor =
        wrap(JNoa.loadTensorInt(path, device.toInt()))

    override fun NoaIntTensor.assignFromArray(array: IntArray): Unit =
        JNoa.assignBlobInt(tensorHandle, array)

    override fun NoaIntTensor.set(i: Int, array: IntArray): Unit =
        JNoa.setBlobInt(tensorHandle, i, array)

    override fun NoaIntTensor.set(dim: Int, slice: Slice, array: IntArray): Unit =
        JNoa.setSliceBlobInt(tensorHandle, dim, slice.first, slice.second, array)
}
