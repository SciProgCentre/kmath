/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.tensors.api

import space.kscience.kmath.nd.StructureND
import space.kscience.kmath.operations.ExtendedFieldOps
import space.kscience.kmath.operations.Field


/**
 * Analytic operations on [Tensor].
 *
 * @param T the type of items closed under analytic functions in the tensors.
 */
public interface AnalyticTensorAlgebra<T, A : Field<T>> :
    TensorPartialDivisionAlgebra<T, A>, ExtendedFieldOps<StructureND<T>> {

    /**
     * @return the mean of all elements in the input tensor.
     */
    public fun mean(structureND: StructureND<T>): T

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
    public fun mean(structureND: StructureND<T>, dim: Int, keepDim: Boolean): Tensor<T>

    /**
     * @return the standard deviation of all elements in the input tensor.
     */
    public fun std(structureND: StructureND<T>): T

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
    public fun std(structureND: StructureND<T>, dim: Int, keepDim: Boolean): Tensor<T>

    /**
     * @return the variance of all elements in the input tensor.
     */
    public fun variance(structureND: StructureND<T>): T

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
    public fun variance(structureND: StructureND<T>, dim: Int, keepDim: Boolean): Tensor<T>

    //For information: https://pytorch.org/docs/stable/generated/torch.sqrt.html
    override fun sqrt(arg: StructureND<T>): Tensor<T>

    //For information: https://pytorch.org/docs/stable/generated/torch.atan.html#torch.tan
    override fun tan(arg: StructureND<T>): Tensor<T>

    //https://pytorch.org/docs/stable/generated/torch.atan.html#torch.atan
    override fun atan(arg: StructureND<T>): Tensor<T>

    //For information: https://pytorch.org/docs/stable/generated/torch.atanh.html#torch.tanh
    override fun tanh(arg: StructureND<T>): Tensor<T>

    //For information: https://pytorch.org/docs/stable/generated/torch.ceil.html#torch.ceil
    public fun ceil(arg: StructureND<T>): Tensor<T>

    //For information: https://pytorch.org/docs/stable/generated/torch.floor.html#torch.floor
    public fun floor(structureND: StructureND<T>): Tensor<T>

    override fun sin(arg: StructureND<T>): StructureND<T> 

    override fun cos(arg: StructureND<T>): StructureND<T> 

    override fun asin(arg: StructureND<T>): StructureND<T>

    override fun acos(arg: StructureND<T>): StructureND<T>

    override fun exp(arg: StructureND<T>): StructureND<T> 

    override fun ln(arg: StructureND<T>): StructureND<T>

    override fun sinh(arg: StructureND<T>): StructureND<T>

    override fun cosh(arg: StructureND<T>): StructureND<T>

    override fun asinh(arg: StructureND<T>): StructureND<T>

    override fun acosh(arg: StructureND<T>): StructureND<T>

    override fun atanh(arg: StructureND<T>): StructureND<T>
}