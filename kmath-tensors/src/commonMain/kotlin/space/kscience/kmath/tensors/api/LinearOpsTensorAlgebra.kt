/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.tensors.api


public interface LinearOpsTensorAlgebra<T> :
    TensorPartialDivisionAlgebra<T> {

    //https://pytorch.org/docs/stable/linalg.html#torch.linalg.det
    public fun TensorStructure<T>.det(): TensorStructure<T>

    //https://pytorch.org/docs/stable/linalg.html#torch.linalg.inv
    public fun TensorStructure<T>.inv(): TensorStructure<T>

    //https://pytorch.org/docs/stable/linalg.html#torch.linalg.cholesky
    public fun TensorStructure<T>.cholesky(): TensorStructure<T>

    //https://pytorch.org/docs/stable/linalg.html#torch.linalg.qr
    public fun TensorStructure<T>.qr(): Pair<TensorStructure<T>, TensorStructure<T>>

    //https://pytorch.org/docs/stable/generated/torch.lu.html
    public fun TensorStructure<T>.lu(): Pair<TensorStructure<T>, TensorStructure<Int>>

    //https://pytorch.org/docs/stable/generated/torch.lu_unpack.html
    public fun luPivot(luTensor: TensorStructure<T>, pivotsTensor: TensorStructure<Int>):
            Triple<TensorStructure<T>, TensorStructure<T>, TensorStructure<T>>

    //https://pytorch.org/docs/stable/linalg.html#torch.linalg.svd
    public fun TensorStructure<T>.svd(): Triple<TensorStructure<T>, TensorStructure<T>, TensorStructure<T>>

    //https://pytorch.org/docs/stable/generated/torch.symeig.html
    public fun TensorStructure<T>.symEig(): Pair<TensorStructure<T>, TensorStructure<T>>

}