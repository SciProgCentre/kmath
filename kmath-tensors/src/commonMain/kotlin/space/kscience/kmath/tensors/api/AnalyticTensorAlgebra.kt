/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.tensors.api

/**
 * Element-wise analytic operations on [Tensor].
 *
 * @param T the type of items closed under analytic functions in the tensors.
 */
public interface AnalyticTensorAlgebra<T> :
    TensorPartialDivisionAlgebra<T> {

    //For information: https://pytorch.org/docs/stable/generated/torch.exp.html
    public fun Tensor<T>.exp(): Tensor<T>

    //For information: https://pytorch.org/docs/stable/generated/torch.log.html
    public fun Tensor<T>.log(): Tensor<T>

    //For information: https://pytorch.org/docs/stable/generated/torch.sqrt.html
    public fun Tensor<T>.sqrt(): Tensor<T>

    //For information: https://pytorch.org/docs/stable/generated/torch.acos.html#torch.cos
    public fun Tensor<T>.cos(): Tensor<T>

    //For information: https://pytorch.org/docs/stable/generated/torch.acos.html#torch.acos
    public fun Tensor<T>.acos(): Tensor<T>

    //For information: https://pytorch.org/docs/stable/generated/torch.acosh.html#torch.cosh
    public fun Tensor<T>.cosh(): Tensor<T>

    //For information: https://pytorch.org/docs/stable/generated/torch.acosh.html#torch.acosh
    public fun Tensor<T>.acosh(): Tensor<T>

    //For information: https://pytorch.org/docs/stable/generated/torch.asin.html#torch.sin
    public fun Tensor<T>.sin(): Tensor<T>

    //For information: https://pytorch.org/docs/stable/generated/torch.asin.html#torch.asin
    public fun Tensor<T>.asin(): Tensor<T>

    //For information: https://pytorch.org/docs/stable/generated/torch.asin.html#torch.sinh
    public fun Tensor<T>.sinh(): Tensor<T>

    //For information: https://pytorch.org/docs/stable/generated/torch.asin.html#torch.asinh
    public fun Tensor<T>.asinh(): Tensor<T>

    //For information: https://pytorch.org/docs/stable/generated/torch.atan.html#torch.tan
    public fun Tensor<T>.tan(): Tensor<T>

    //https://pytorch.org/docs/stable/generated/torch.atan.html#torch.atan
    public fun Tensor<T>.atan(): Tensor<T>

    //For information: https://pytorch.org/docs/stable/generated/torch.atanh.html#torch.tanh
    public fun Tensor<T>.tanh(): Tensor<T>

    //For information: https://pytorch.org/docs/stable/generated/torch.atanh.html#torch.atanh
    public fun Tensor<T>.atanh(): Tensor<T>

    //For information: https://pytorch.org/docs/stable/generated/torch.ceil.html#torch.ceil
    public fun Tensor<T>.ceil(): Tensor<T>

    //For information: https://pytorch.org/docs/stable/generated/torch.floor.html#torch.floor
    public fun Tensor<T>.floor(): Tensor<T>

}