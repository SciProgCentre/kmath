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


