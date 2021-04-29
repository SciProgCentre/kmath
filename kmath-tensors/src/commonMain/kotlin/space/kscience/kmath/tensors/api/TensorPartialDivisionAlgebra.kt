/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.tensors.api

/**
 * Algebra over a field with partial division on [TensorStructure].
 * For more information: https://proofwiki.org/wiki/Definition:Division_Algebra
 *
 * @param T the type of items closed under division in the tensors.
 */
public interface TensorPartialDivisionAlgebra<T> :
    TensorAlgebra<T> {

    /**
     * Each element of the tensor [other] is divided by this value.
     * The resulting tensor is returned.
     *
     * @param other tensor to divide by.
     * @return the division of this value by the tensor [other].
     */
    public operator fun T.div(other: TensorStructure<T>): TensorStructure<T>

    /**
     * Divide by the scalar [value] each element of this tensor returns a new resulting tensor.
     *
     * @param value the number to divide by each element of this tensor.
     * @return the division of this tensor by the [value].
     */
    public operator fun TensorStructure<T>.div(value: T): TensorStructure<T>

    /**
     * Each element of the tensor [other] is divided by each element of this tensor.
     * The resulting tensor is returned.
     *
     * @param other tensor to be divided by.
     * @return the division of this tensor by [other].
     */
    public operator fun TensorStructure<T>.div(other: TensorStructure<T>): TensorStructure<T>

    /**
     * Divides by the scalar [value] each element of this tensor.
     *
     * @param value the number to divide by each element of this tensor.
     */
    public operator fun TensorStructure<T>.divAssign(value: T)

    /**
     * Each element of this tensor is divided by each element of the [other] tensor.
     *
     * @param other tensor to be divide by.
     */
    public operator fun TensorStructure<T>.divAssign(other: TensorStructure<T>)
}
