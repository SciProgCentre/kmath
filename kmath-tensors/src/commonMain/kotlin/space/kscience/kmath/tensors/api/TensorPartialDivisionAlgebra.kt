/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.tensors.api

import space.kscience.kmath.nd.FieldOpsND
import space.kscience.kmath.nd.StructureND
import space.kscience.kmath.operations.Field

/**
 * Algebra over a field with partial division on [Tensor].
 * For more information: https://proofwiki.org/wiki/Definition:Division_Algebra
 *
 * @param T the type of items closed under division in the tensors.
 */
public interface TensorPartialDivisionAlgebra<T, out A : Field<T>> : TensorAlgebra<T, A>, FieldOpsND<T, A> {
    /**
     * Each element of the tensor [right] is divided by each element of [left] tensor.
     * The resulting tensor is returned.
     *
     * @param left tensor to be divided by.
     * @param right tensor to be divided by.
     * @return the division of [left] tensor by [right] one.
     */
    override fun divide(left: StructureND<T>, right: StructureND<T>): Tensor<T>

    /**
     * Divides by the scalar [value] each element of this tensor.
     *
     * @param value the number to divide by each element of this tensor.
     */
    public operator fun Tensor<T>.divAssign(value: T)

    /**
     * Each element of this tensor is divided by each element of the [arg] tensor.
     *
     * @param arg tensor to be divided by.
     */
    public operator fun Tensor<T>.divAssign(arg: StructureND<T>)
}
