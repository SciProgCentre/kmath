/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this.cast() source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.noa

import space.kscience.kmath.noa.memory.NoaScope
import space.kscience.kmath.tensors.api.AnalyticTensorAlgebra
import space.kscience.kmath.tensors.api.LinearOpsTensorAlgebra
import space.kscience.kmath.tensors.api.Tensor
import space.kscience.kmath.tensors.api.TensorAlgebra


public sealed class NoaAlgebra<T, TensorType : NoaTensor<T>>
constructor(protected val scope: NoaScope) : TensorAlgebra<T> {

    protected abstract fun Tensor<T>.cast(): TensorType

    protected abstract fun wrap(tensorHandle: TensorHandle): TensorType

    /**
     * A scalar tensor in this.cast() implementation must have empty shape
     */
    override fun Tensor<T>.valueOrNull(): T? =
        try {
            this.cast().cast().item()
        } catch (e: NoaException) {
            null
        }

    override fun Tensor<T>.value(): T = this.cast().cast().item()

    override operator fun Tensor<T>.times(other: Tensor<T>): TensorType {
        return wrap(JNoa.timesTensor(this.cast().tensorHandle, other.cast().tensorHandle))
    }

    override operator fun Tensor<T>.timesAssign(other: Tensor<T>): Unit {
        JNoa.timesTensorAssign(this.cast().tensorHandle, other.cast().tensorHandle)
    }

    override operator fun Tensor<T>.plus(other: Tensor<T>): TensorType {
        return wrap(JNoa.plusTensor(this.cast().tensorHandle, other.cast().tensorHandle))
    }

    override operator fun Tensor<T>.plusAssign(other: Tensor<T>): Unit {
        JNoa.plusTensorAssign(this.cast().tensorHandle, other.cast().tensorHandle)
    }

    override operator fun Tensor<T>.minus(other: Tensor<T>): TensorType {
        return wrap(JNoa.minusTensor(this.cast().tensorHandle, other.cast().tensorHandle))
    }

    override operator fun Tensor<T>.minusAssign(other: Tensor<T>): Unit {
        JNoa.minusTensorAssign(this.cast().tensorHandle, other.cast().tensorHandle)
    }

    override operator fun Tensor<T>.unaryMinus(): TensorType =
        wrap(JNoa.unaryMinus(this.cast().tensorHandle))

    override infix fun Tensor<T>.dot(other: Tensor<T>): TensorType {
        return wrap(JNoa.matmul(this.cast().tensorHandle, other.cast().tensorHandle))
    }

    public infix fun Tensor<T>.dotAssign(other: Tensor<T>): Unit {
        JNoa.matmulAssign(this.cast().tensorHandle, other.cast().tensorHandle)
    }

    public infix fun Tensor<T>.dotRightAssign(other: Tensor<T>): Unit {
        JNoa.matmulRightAssign(this.cast().tensorHandle, other.cast().tensorHandle)
    }

    override operator fun Tensor<T>.get(i: Int): TensorType =
        wrap(JNoa.getIndex(this.cast().tensorHandle, i))

    public operator fun Tensor<T>.get(indexTensor: NoaLongTensor): TensorType =
        wrap(JNoa.getIndexTensor(this.cast().tensorHandle, indexTensor.tensorHandle))

    override fun diagonalEmbedding(
        diagonalEntries: Tensor<T>, offset: Int, dim1: Int, dim2: Int
    ): TensorType =
        wrap(JNoa.diagEmbed(diagonalEntries.cast().tensorHandle, offset, dim1, dim2))

    override fun Tensor<T>.transpose(i: Int, j: Int): TensorType {
        return wrap(JNoa.transposeTensor(this.cast().tensorHandle, i, j))
    }

    override fun Tensor<T>.view(shape: IntArray): TensorType {
        return wrap(JNoa.viewTensor(this.cast().tensorHandle, shape))
    }

    override fun Tensor<T>.viewAs(other: Tensor<T>): TensorType {
        return wrap(JNoa.viewAsTensor(this.cast().tensorHandle, other.cast().tensorHandle))
    }

    public fun Tensor<T>.abs(): TensorType = wrap(JNoa.absTensor(this.cast().tensorHandle))

    public fun Tensor<T>.sumAll(): TensorType = wrap(JNoa.sumTensor(this.cast().tensorHandle))
    override fun Tensor<T>.sum(): T = sumAll().item()
    override fun Tensor<T>.sum(dim: Int, keepDim: Boolean): TensorType =
        wrap(JNoa.sumDimTensor(this.cast().tensorHandle, dim, keepDim))

    public fun Tensor<T>.minAll(): TensorType = wrap(JNoa.minTensor(this.cast().tensorHandle))
    override fun Tensor<T>.min(): T = minAll().item()
    override fun Tensor<T>.min(dim: Int, keepDim: Boolean): TensorType =
        wrap(JNoa.minDimTensor(this.cast().tensorHandle, dim, keepDim))

    public fun Tensor<T>.maxAll(): TensorType = wrap(JNoa.maxTensor(this.cast().tensorHandle))
    override fun Tensor<T>.max(): T = maxAll().item()
    override fun Tensor<T>.max(dim: Int, keepDim: Boolean): TensorType =
        wrap(JNoa.maxDimTensor(this.cast().tensorHandle, dim, keepDim))

    override fun Tensor<T>.argMax(dim: Int, keepDim: Boolean): NoaIntTensor =
        NoaIntTensor(scope, JNoa.argMaxTensor(this.cast().tensorHandle, dim, keepDim))

    public fun Tensor<T>.flatten(): TensorType =
        wrap(JNoa.flattenTensor(this.cast().tensorHandle))

    public fun Tensor<T>.copy(): TensorType =
        wrap(JNoa.copyTensor(this.cast().tensorHandle))

    public fun Tensor<T>.copyToDevice(device: Device): TensorType =
        wrap(JNoa.copyToDevice(this.cast().tensorHandle, device.toInt()))

}

public abstract class NoaPartialDivisionAlgebra<T, TensorType : NoaTensor<T>>
internal constructor(scope: NoaScope) : NoaAlgebra<T, TensorType>(scope), LinearOpsTensorAlgebra<T>,
    AnalyticTensorAlgebra<T> {

    override operator fun Tensor<T>.div(other: Tensor<T>): TensorType {
        return wrap(JNoa.divTensor(this.cast().tensorHandle, other.cast().tensorHandle))
    }

    override operator fun Tensor<T>.divAssign(other: Tensor<T>): Unit {
        JNoa.divTensorAssign(this.cast().tensorHandle, other.cast().tensorHandle)
    }

}


