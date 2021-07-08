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

public abstract class NoaAlgebra<T, TensorType: NoaTensor<T>>
internal constructor(protected val scope: NoaScope)
    : TensorAlgebra<T>  {

    protected abstract fun Tensor<T>.cast(): TensorType
}

public abstract class NoaPartialDivisionAlgebra<T, TensorType: NoaTensor<T>>
internal constructor(scope: NoaScope)
    : NoaAlgebra<T, TensorType>(scope), LinearOpsTensorAlgebra<T>, AnalyticTensorAlgebra<T> {


}


