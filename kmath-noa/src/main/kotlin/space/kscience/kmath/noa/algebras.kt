/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
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
     * A scalar tensor in this implementation must have empty shape
     */
    override fun Tensor<T>.valueOrNull(): T? =
        try { this.cast().item() } catch (e: NoaException) { null }

    override fun Tensor<T>.value(): T = this.cast().item()
}

public abstract class NoaPartialDivisionAlgebra<T, TensorType : NoaTensor<T>>
internal constructor(scope: NoaScope) : NoaAlgebra<T, TensorType>(scope), LinearOpsTensorAlgebra<T>,
    AnalyticTensorAlgebra<T> {


}


