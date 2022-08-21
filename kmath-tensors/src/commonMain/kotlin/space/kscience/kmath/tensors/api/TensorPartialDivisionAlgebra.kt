/*
 * Copyright 2018-2022 KMath contributors.
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
public interface TensorPartialDivisionAlgebra<T, A : Field<T>> : TensorAlgebra<T, A>, FieldOpsND<T, A> {

    /**
     * Each element of the tensor [arg] is divided by this value.
     * The resulting tensor is returned.
     *
     * @param arg tensor to divide by.
     * @return the division of this value by the tensor [arg].
     */
    override operator fun T.div(arg: StructureND<T>): Tensor<T>

    /**
     * Divide by the scalar [arg] each element of this tensor returns a new resulting tensor.
     *
     * @param arg the number to divide by each element of this tensor.
     * @return the division of this tensor by the [arg].
     */
    override operator fun StructureND<T>.div(arg: T): Tensor<T>

    /**
     * Each element of the tensor [arg] is divided by each element of this tensor.
     * The resulting tensor is returned.
     *
     * @param arg tensor to be divided by.
     * @return the division of this tensor by [arg].
     */
    override operator fun StructureND<T>.div(arg: StructureND<T>): Tensor<T>

    override fun divide(left: StructureND<T>, right: StructureND<T>): StructureND<T> = left.div(right)

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
