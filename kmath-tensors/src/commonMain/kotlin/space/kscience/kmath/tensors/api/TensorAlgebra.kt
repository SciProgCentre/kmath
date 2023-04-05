/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.tensors.api

import space.kscience.kmath.nd.RingOpsND
import space.kscience.kmath.nd.ShapeND
import space.kscience.kmath.nd.StructureND
import space.kscience.kmath.operations.Ring

/**
 * Algebra over a ring on [Tensor].
 * For more information: https://proofwiki.org/wiki/Definition:Algebra_over_Ring
 *
 * @param T the type of items in the tensors.
 */
public interface TensorAlgebra<T, A : Ring<T>> : RingOpsND<T, A> {
    /**
     * Returns a single tensor value of unit dimension if tensor shape equals to [1].
     *
     * @return a nullable value of a potentially scalar tensor.
     */
    public fun StructureND<T>.valueOrNull(): T?

    /**
     * Returns a single tensor value of unit dimension. The tensor shape must be equal to [1].
     *
     * @return the value of a scalar tensor.
     */
    public fun StructureND<T>.value(): T =
        valueOrNull() ?: throw IllegalArgumentException("Inconsistent value for tensor of with $shape shape")

    /**
     * Each element of the tensor [arg] is added to this value.
     * The resulting tensor is returned.
     *
     * @param arg tensor to be added.
     * @return the sum of this value and tensor [arg].
     */
    override operator fun T.plus(arg: StructureND<T>): Tensor<T>

    /**
     * Adds the scalar [arg] to each element of this tensor and returns a new resulting tensor.
     *
     * @param arg the number to be added to each element of this tensor.
     * @return the sum of this tensor and [arg].
     */
    override operator fun StructureND<T>.plus(arg: T): Tensor<T>

    /**
     * Each element of the tensor [arg] is added to each element of this tensor.
     * The resulting tensor is returned.
     *
     * @param arg tensor to be added.
     * @return the sum of this tensor and [arg].
     */
    override operator fun StructureND<T>.plus(arg: StructureND<T>): Tensor<T>

    /**
     * Adds the scalar [value] to each element of this tensor.
     *
     * @param value the number to be added to each element of this tensor.
     */
    public operator fun Tensor<T>.plusAssign(value: T)

    /**
     * Each element of the tensor [arg] is added to each element of this tensor.
     *
     * @param arg tensor to be added.
     */
    public operator fun Tensor<T>.plusAssign(arg: StructureND<T>)

    /**
     * Each element of the tensor [arg] is subtracted from this value.
     * The resulting tensor is returned.
     *
     * @param arg tensor to be subtracted.
     * @return the difference between this value and tensor [arg].
     */
    override operator fun T.minus(arg: StructureND<T>): Tensor<T>

    /**
     * Subtracts the scalar [arg] from each element of this tensor and returns a new resulting tensor.
     *
     * @param arg the number to be subtracted from each element of this tensor.
     * @return the difference between this tensor and [arg].
     */
    override operator fun StructureND<T>.minus(arg: T): Tensor<T>

    /**
     * Each element of the tensor [arg] is subtracted from each element of this tensor.
     * The resulting tensor is returned.
     *
     * @param arg tensor to be subtracted.
     * @return the difference between this tensor and [arg].
     */
    override operator fun StructureND<T>.minus(arg: StructureND<T>): Tensor<T>

    /**
     * Subtracts the scalar [value] from each element of this tensor.
     *
     * @param value the number to be subtracted from each element of this tensor.
     */
    public operator fun Tensor<T>.minusAssign(value: T)

    /**
     * Each element of the tensor [arg] is subtracted from each element of this tensor.
     *
     * @param arg tensor to be subtracted.
     */
    public operator fun Tensor<T>.minusAssign(arg: StructureND<T>)


    /**
     * Each element of the tensor [arg] is multiplied by this value.
     * The resulting tensor is returned.
     *
     * @param arg tensor to be multiplied.
     * @return the product of this value and tensor [arg].
     */
    override operator fun T.times(arg: StructureND<T>): Tensor<T>

    /**
     * Multiplies the scalar [arg] by each element of this tensor and returns a new resulting tensor.
     *
     * @param arg the number to be multiplied by each element of this tensor.
     * @return the product of this tensor and [arg].
     */
    override operator fun StructureND<T>.times(arg: T): Tensor<T>

    /**
     * Each element of the tensor [arg] is multiplied by each element of this tensor.
     * The resulting tensor is returned.
     *
     * @param arg tensor to be multiplied.
     * @return the product of this tensor and [arg].
     */
    override operator fun StructureND<T>.times(arg: StructureND<T>): Tensor<T>

    /**
     * Multiplies the scalar [value] by each element of this tensor.
     *
     * @param value the number to be multiplied by each element of this tensor.
     */
    public operator fun Tensor<T>.timesAssign(value: T)

    /**
     * Each element of the tensor [arg] is multiplied by each element of this tensor.
     *
     * @param arg tensor to be multiplied.
     */
    public operator fun Tensor<T>.timesAssign(arg: StructureND<T>)

    /**
     * Numerical negative, element-wise.
     *
     * @return tensor negation of the original tensor.
     */
    override operator fun StructureND<T>.unaryMinus(): Tensor<T>

    /**
     * Returns the tensor at index i
     * For more information: https://pytorch.org/cppdocs/notes/tensor_indexing.html
     *
     * @param i index of the extractable tensor
     * @return subtensor of the original tensor with index [i]
     */
    public fun Tensor<T>.getTensor(i: Int): Tensor<T>

    public fun Tensor<T>.getTensor(first: Int, second: Int): Tensor<T> {
        return getTensor(first).getTensor(second)
    }

    /**
     * Returns a tensor that is a transposed version of this tensor. The given dimensions [i] and [j] are swapped.
     * For more information: https://pytorch.org/docs/stable/generated/torch.transpose.html
     *
     * If axis indices are negative, they are counted from shape end.
     *
     * @param i the first dimension to be transposed
     * @param j the second dimension to be transposed
     * @return transposed tensor
     */
    public fun StructureND<T>.transposed(i: Int = shape.size - 2, j: Int = shape.size - 1): Tensor<T>

    /**
     * Returns a new tensor with the same data as the self tensor but of a different shape.
     * The returned tensor shares the same data and must have the same number of elements, but may have a different size
     * For more information: https://pytorch.org/docs/stable/tensor_view.html
     *
     * @param shape the desired size
     * @return tensor with new shape
     */
    public fun Tensor<T>.view(shape: ShapeND): Tensor<T>

    /**
     * View this tensor as the same size as [other].
     * `this.viewAs(other)` is equivalent to `this.view(other.shape)`.
     * For more information: https://pytorch.org/cppdocs/notes/tensor_indexing.html
     *
     * @param other the result tensor has the same size as other.
     * @return the result tensor with the same size as other.
     */
    public fun Tensor<T>.viewAs(other: StructureND<T>): Tensor<T>

    /**
     * Matrix product of two tensors.
     *
     * The behavior depends on the dimensionality of the tensors as follows:
     * 1. If both tensors are 1-dimensional, the dot product (scalar) is returned.
     *
     * 2. If both arguments are 2-dimensional, the matrix-matrix product is returned.
     *
     * 3. If the first argument is 1-dimensional and the second argument is 2-dimensional,
     * a 1 is prepended to its dimension for the purpose of the matrix multiply.
     * After the matrix multiply, depending on the implementation the prepended dimension might be removed.
     *
     * 4. If the first argument is 2-dimensional and the second argument is 1-dimensional,
     * the matrix-vector product is returned.
     *
     * Otherwise, throw an exception.
     *
     * @param other tensor to be multiplied.
     * @return a mathematical product of two tensors.
     */
    public infix fun StructureND<T>.dot(other: StructureND<T>): Tensor<T>

    /**
     * Creates a tensor whose diagonals of certain 2D planes (specified by [dim1] and [dim2])
     * are filled by [diagonalEntries].
     * To facilitate creating batched diagonal matrices,
     * the 2D planes formed by the last two dimensions of the returned tensor are chosen by default.
     *
     * The argument [offset] controls, which diagonal to consider:
     * 1. If [offset] = 0, it is the main diagonal.
     * 1. If [offset] > 0, it is above the main diagonal.
     * 1. If [offset] < 0, it is below the main diagonal.
     *
     * The size of the new matrix will be calculated
     * to make the specified diagonal of the size of the last input dimension.
     * For more information: https://pytorch.org/docs/stable/generated/torch.diag_embed.html
     *
     * @param diagonalEntries the input tensor. Must be at least 1-dimensional.
     * @param offset which diagonal to consider. Default: 0 (main diagonal).
     * @param dim1 first dimension with respect to which to take diagonal. Default: -2.
     * @param dim2 second dimension with respect to which to take diagonal. Default: -1.
     *
     * @return tensor whose diagonals of certain 2D planes (specified by [dim1] and [dim2])
     * are filled by [diagonalEntries]
     */
    public fun diagonalEmbedding(
        diagonalEntries: StructureND<T>,
        offset: Int = 0,
        dim1: Int = -2,
        dim2: Int = -1,
    ): Tensor<T>

    /**
     * @return the sum of all elements in the input tensor.
     */
    public fun StructureND<T>.sum(): T

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
    public fun StructureND<T>.sum(dim: Int, keepDim: Boolean): Tensor<T>

    /**
     * @return the minimum value of all elements in the input tensor or null if there are no values
     */
    public fun StructureND<T>.min(): T?

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
    public fun StructureND<T>.min(dim: Int, keepDim: Boolean): Tensor<T>

    /**
     * Returns the index of minimum value of each row of the input tensor in the given dimension [dim].
     *
     * If [keepDim] is true, the output tensor is of the same size as
     * input except in the dimension [dim] where it is of size 1.
     * Otherwise, [dim] is squeezed, resulting in the output tensor having 1 fewer dimension.
     *
     * @param dim the dimension to reduce.
     * @param keepDim whether the output tensor has [dim] retained or not.
     * @return the index of maximum value of each row of the input tensor in the given dimension [dim].
     */
    public fun StructureND<T>.argMin(dim: Int, keepDim: Boolean): Tensor<Int>

    /**
     * Returns the maximum value of all elements in the input tensor or null if there are no values
     */
    public fun StructureND<T>.max(): T?

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
    public fun StructureND<T>.max(dim: Int, keepDim: Boolean): Tensor<T>

    /**
     * Returns the index of maximum value of each row of the input tensor in the given dimension [dim].
     *
     * If [keepDim] is true, the output tensor is of the same size as
     * input except in the dimension [dim] where it is of size 1.
     * Otherwise, [dim] is squeezed, resulting in the output tensor having 1 fewer dimension.
     *
     * @param dim the dimension to reduce.
     * @param keepDim whether the output tensor has [dim] retained or not.
     * @return the index of maximum value of each row of the input tensor in the given dimension [dim].
     */
    public fun StructureND<T>.argMax(dim: Int, keepDim: Boolean): Tensor<Int>

    override fun add(left: StructureND<T>, right: StructureND<T>): Tensor<T> = left + right

    override fun multiply(left: StructureND<T>, right: StructureND<T>): Tensor<T> = left * right
}