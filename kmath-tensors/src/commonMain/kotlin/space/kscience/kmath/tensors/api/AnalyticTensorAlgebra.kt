/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.tensors.api

/**
 * Element-wise analytic operations on [TensorStructure].
 *
 * @param T the type of items closed under analytic functions in the tensors.
 */
public interface AnalyticTensorAlgebra<T> :
    TensorPartialDivisionAlgebra<T> {

    //For information: https://pytorch.org/docs/stable/generated/torch.exp.html
    public fun TensorStructure<T>.exp(): TensorStructure<T>

    //For information: https://pytorch.org/docs/stable/generated/torch.log.html
    public fun TensorStructure<T>.log(): TensorStructure<T>

    //For information: https://pytorch.org/docs/stable/generated/torch.sqrt.html
    public fun TensorStructure<T>.sqrt(): TensorStructure<T>

    //For information: https://pytorch.org/docs/stable/generated/torch.acos.html#torch.cos
    public fun TensorStructure<T>.cos(): TensorStructure<T>

    //For information: https://pytorch.org/docs/stable/generated/torch.acos.html#torch.acos
    public fun TensorStructure<T>.acos(): TensorStructure<T>

    //For information: https://pytorch.org/docs/stable/generated/torch.acosh.html#torch.cosh
    public fun TensorStructure<T>.cosh(): TensorStructure<T>

    //For information: https://pytorch.org/docs/stable/generated/torch.acosh.html#torch.acosh
    public fun TensorStructure<T>.acosh(): TensorStructure<T>

    //For information: https://pytorch.org/docs/stable/generated/torch.asin.html#torch.sin
    public fun TensorStructure<T>.sin(): TensorStructure<T>

    //For information: https://pytorch.org/docs/stable/generated/torch.asin.html#torch.asin
    public fun TensorStructure<T>.asin(): TensorStructure<T>

    //For information: https://pytorch.org/docs/stable/generated/torch.asin.html#torch.sinh
    public fun TensorStructure<T>.sinh(): TensorStructure<T>

    //For information: https://pytorch.org/docs/stable/generated/torch.asin.html#torch.asinh
    public fun TensorStructure<T>.asinh(): TensorStructure<T>

    //For information: https://pytorch.org/docs/stable/generated/torch.atan.html#torch.tan
    public fun TensorStructure<T>.tan(): TensorStructure<T>

    //https://pytorch.org/docs/stable/generated/torch.atan.html#torch.atan
    public fun TensorStructure<T>.atan(): TensorStructure<T>

    //For information: https://pytorch.org/docs/stable/generated/torch.atanh.html#torch.tanh
    public fun TensorStructure<T>.tanh(): TensorStructure<T>

    //For information: https://pytorch.org/docs/stable/generated/torch.atanh.html#torch.atanh
    public fun TensorStructure<T>.atanh(): TensorStructure<T>

    //For information: https://pytorch.org/docs/stable/generated/torch.ceil.html#torch.ceil
    public fun TensorStructure<T>.ceil(): TensorStructure<T>

    //For information: https://pytorch.org/docs/stable/generated/torch.floor.html#torch.floor
    public fun TensorStructure<T>.floor(): TensorStructure<T>

}