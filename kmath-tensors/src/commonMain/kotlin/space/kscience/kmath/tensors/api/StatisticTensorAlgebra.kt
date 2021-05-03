/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.tensors.api

import space.kscience.kmath.tensors.core.DoubleTensor

/**
 * Common algebra with statistics methods. Operates on [Tensor].
 */

public interface StatisticTensorAlgebra<T>: TensorAlgebra<T> {

    /**
     * Returns the minimum value of all elements in the input tensor.
     */
    public fun Tensor<T>.min(): Double

    /**
     * Returns the minimum value of each row of the input tensor in the given dimension [dim].
     *
     * If [keepDim] is true, the output tensor is of the same size as
     * input except in the dimension [dim] where it is of size 1.
     * Otherwise, [dim] is squeezed, resulting in the output tensor having 1 fewer dimension.
     *
     * @param dim the dimension to reduce.
     * @param keepDim whether the output tensor has [dim] retained or not.
     * @return the minimum value of each row of the input tensor in the given dimension [dim].
     */
    public fun Tensor<T>.min(dim: Int, keepDim: Boolean): DoubleTensor

    /**
     * Returns the maximum value of all elements in the input tensor.
     */
    public fun Tensor<T>.max(): Double

    /**
     * Returns the maximum value of each row of the input tensor in the given dimension [dim].
     *
     * If [keepDim] is true, the output tensor is of the same size as
     * input except in the dimension [dim] where it is of size 1.
     * Otherwise, [dim] is squeezed, resulting in the output tensor having 1 fewer dimension.
     *
     * @param dim the dimension to reduce.
     * @param keepDim whether the output tensor has [dim] retained or not.
     * @return the maximum value of each row of the input tensor in the given dimension [dim].
     */
    public fun Tensor<T>.max(dim: Int, keepDim: Boolean): DoubleTensor

    /**
     * Returns the sum of all elements in the input tensor.
     */
    public fun Tensor<T>.sum(): Double

    /**
     * Returns the sum of each row of the input tensor in the given dimension [dim].
     *
     * If [keepDim] is true, the output tensor is of the same size as
     * input except in the dimension [dim] where it is of size 1.
     * Otherwise, [dim] is squeezed, resulting in the output tensor having 1 fewer dimension.
     *
     * @param dim the dimension to reduce.
     * @param keepDim whether the output tensor has [dim] retained or not.
     * @return the sum of each row of the input tensor in the given dimension [dim].
     */
    public fun Tensor<T>.sum(dim: Int, keepDim: Boolean): DoubleTensor

    /**
     * Returns the mean of all elements in the input tensor.
     */
    public fun Tensor<T>.mean(): Double

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
    public fun Tensor<T>.mean(dim: Int, keepDim: Boolean): DoubleTensor

    /**
     * Returns the standard deviation of all elements in the input tensor.
     */
    public fun Tensor<T>.std(): Double

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
    public fun Tensor<T>.std(dim: Int, keepDim: Boolean): DoubleTensor

    /**
     * Returns the variance of all elements in the input tensor.
     */
    public fun Tensor<T>.variance(): Double

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
    public fun Tensor<T>.variance(dim: Int, keepDim: Boolean): DoubleTensor

}
