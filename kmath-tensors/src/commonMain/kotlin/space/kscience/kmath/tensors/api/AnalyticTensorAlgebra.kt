/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package space.kscience.kmath.tensors.api


/**
 * Analytic operations on [Tensor].
 *
 * @param T the type of items closed under analytic functions in the tensors.
 */
public interface AnalyticTensorAlgebra<T> : TensorPartialDivisionAlgebra<T> {

    /**
     * @return the mean of all elements in the input tensor.
     */
    public fun Tensor<T>.mean(): T

    /**
     * Returns the mean of each row of the input tensor in the given dimension [dim].
     *
     * If [keepDim] is true, the output tensor is of the same size as
     * input except in the dimension [dim] where it is of size 1.
     * Otherwise, [dim] is squeezed, resulting in the output tensor having 1 fewer dimension.
     *
     * @param dim the dimension to reduce.
     * @param keepDim whether the output tensor has [dim] retained or not.
     * @return the mean of each row of the input tensor in the given dimension [dim].
     */
    public fun Tensor<T>.mean(dim: Int, keepDim: Boolean): Tensor<T>

    /**
     * @return the standard deviation of all elements in the input tensor.
     */
    public fun Tensor<T>.std(): T

    /**
     * Returns the standard deviation of each row of the input tensor in the given dimension [dim].
     *
     * If [keepDim] is true, the output tensor is of the same size as
     * input except in the dimension [dim] where it is of size 1.
     * Otherwise, [dim] is squeezed, resulting in the output tensor having 1 fewer dimension.
     *
     * @param dim the dimension to reduce.
     * @param keepDim whether the output tensor has [dim] retained or not.
     * @return the standard deviation of each row of the input tensor in the given dimension [dim].
     */
    public fun Tensor<T>.std(dim: Int, keepDim: Boolean): Tensor<T>

    /**
     * @return the variance of all elements in the input tensor.
     */
    public fun Tensor<T>.variance(): T

    /**
     * Returns the variance of each row of the input tensor in the given dimension [dim].
     *
     * If [keepDim] is true, the output tensor is of the same size as
     * input except in the dimension [dim] where it is of size 1.
     * Otherwise, [dim] is squeezed, resulting in the output tensor having 1 fewer dimension.
     *
     * @param dim the dimension to reduce.
     * @param keepDim whether the output tensor has [dim] retained or not.
     * @return the variance of each row of the input tensor in the given dimension [dim].
     */
    public fun Tensor<T>.variance(dim: Int, keepDim: Boolean): Tensor<T>

    //For information: https://pytorch.org/docs/stable/generated/torch.exp.html
    public fun Tensor<T>.exp(): Tensor<T>

    //For information: https://pytorch.org/docs/stable/generated/torch.log.html
    public fun Tensor<T>.ln(): Tensor<T>

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