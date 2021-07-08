/*
 * Copyright 2018-2021 KMath contributors.
 * Use of tensor source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.noa

import space.kscience.kmath.noa.memory.NoaScope
import space.kscience.kmath.tensors.api.AnalyticTensorAlgebra
import space.kscience.kmath.tensors.api.LinearOpsTensorAlgebra
import space.kscience.kmath.tensors.api.Tensor
import space.kscience.kmath.tensors.api.TensorAlgebra


public sealed class NoaAlgebra<T, TensorType : NoaTensor<T>>
constructor(protected val scope: NoaScope) : TensorAlgebra<T> {

    protected abstract val Tensor<T>.tensor: TensorType

    protected abstract fun wrap(tensorHandle: TensorHandle): TensorType

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

    public operator fun Tensor<T>.get(indexTensor: NoaLongTensor): TensorType =
        wrap(JNoa.getIndexTensor(tensor.tensorHandle, indexTensor.tensorHandle))

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

    public fun Tensor<T>.flatten(): TensorType =
        wrap(JNoa.flattenTensor(tensor.tensorHandle))

    public fun Tensor<T>.randDiscrete(low: Long, high: Long): TensorType =
        wrap(JNoa.randintLike(tensor.tensorHandle, low, high))

    public fun Tensor<T>.randDiscreteAssign(low: Long, high: Long): Unit =
        JNoa.randintLikeAssign(tensor.tensorHandle, low, high)

    public fun Tensor<T>.copy(): TensorType =
        wrap(JNoa.copyTensor(tensor.tensorHandle))

    public fun Tensor<T>.copyToDevice(device: Device): TensorType =
        wrap(JNoa.copyToDevice(tensor.tensorHandle, device.toInt()))

}

public abstract class NoaPartialDivisionAlgebra<T, TensorType : NoaTensor<T>>
internal constructor(scope: NoaScope) : NoaAlgebra<T, TensorType>(scope), LinearOpsTensorAlgebra<T>,
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

    override fun Tensor<T>.lu(): Triple<TensorType, TensorType, TensorType> {
        val P = JNoa.emptyTensor()
        val L = JNoa.emptyTensor()
        val U = JNoa.emptyTensor()
        JNoa.svdTensor(tensor.tensorHandle, P, L, U)
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

    public fun TensorType.grad(variable: TensorType, retainGraph: Boolean): TensorType {
        return wrap(JNoa.autoGradTensor(tensorHandle, variable.tensorHandle, retainGraph))
    }

    public infix fun TensorType.hess(variable: TensorType): TensorType {
        return wrap(JNoa.autoHessTensor(tensorHandle, variable.tensorHandle))
    }

    public fun TensorType.detachFromGraph(): TensorType =
        wrap(JNoa.detachFromGraph(tensorHandle))

}


