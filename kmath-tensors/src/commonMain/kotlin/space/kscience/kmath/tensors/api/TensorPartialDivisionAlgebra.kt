/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.tensors.api

// https://proofwiki.org/wiki/Definition:Division_Algebra
public interface TensorPartialDivisionAlgebra<T> :
    TensorAlgebra<T> {
    public operator fun T.div(other: TensorStructure<T>): TensorStructure<T>
    public operator fun TensorStructure<T>.div(value: T): TensorStructure<T>
    public operator fun TensorStructure<T>.div(other: TensorStructure<T>): TensorStructure<T>
    public operator fun TensorStructure<T>.divAssign(value: T)
    public operator fun TensorStructure<T>.divAssign(other: TensorStructure<T>)
}
