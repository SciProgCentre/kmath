/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.tensors.core.algebras

import space.kscience.kmath.nd.as2D
import space.kscience.kmath.tensors.api.TensorPartialDivisionAlgebra
import space.kscience.kmath.tensors.api.Tensor
import space.kscience.kmath.tensors.core.*
import space.kscience.kmath.tensors.core.algebras.DoubleAnalyticTensorAlgebra.fold
import space.kscience.kmath.tensors.core.algebras.DoubleAnalyticTensorAlgebra.foldDim
import space.kscience.kmath.tensors.core.broadcastOuterTensors
import space.kscience.kmath.tensors.core.checkBufferShapeConsistency
import space.kscience.kmath.tensors.core.checkEmptyDoubleBuffer
import space.kscience.kmath.tensors.core.checkEmptyShape
import space.kscience.kmath.tensors.core.checkShapesCompatible
import space.kscience.kmath.tensors.core.checkTranspose
import space.kscience.kmath.tensors.core.checkView
import space.kscience.kmath.tensors.core.dotHelper
import space.kscience.kmath.tensors.core.getRandomNormals
import space.kscience.kmath.tensors.core.minusIndexFrom
import kotlin.math.abs

/**
 * Implementation of basic operations over double tensors and basic algebra operations on them.
 */
public open class DoubleTensorAlgebra : TensorPartialDivisionAlgebra<Double> {

    public companion object : DoubleTensorAlgebra()

    override fun Tensor<Double>.value(): Double {
        check(tensor.shape contentEquals intArrayOf(1)) {
            "Inconsistent value for tensor of shape ${shape.toList()}"
        }
        return tensor.mutableBuffer.array()[tensor.bufferStart]
    }

    /**
     * Constructs a tensor with the specified shape and data.
     *
     * @param shape the desired shape for the tensor.
     * @param buffer one-dimensional data array.
     * @return tensor with the [shape] shape and [buffer] data.
     */
    public fun fromArray(shape: IntArray, buffer: DoubleArray): DoubleTensor {
        checkEmptyShape(shape)
        checkEmptyDoubleBuffer(buffer)
        checkBufferShapeConsistency(shape, buffer)
        return DoubleTensor(shape, buffer, 0)
    }

    override operator fun Tensor<Double>.get(i: Int): DoubleTensor {
        val lastShape = tensor.shape.drop(1).toIntArray()
        val newShape = if (lastShape.isNotEmpty()) lastShape else intArrayOf(1)
        val newStart = newShape.reduce(Int::times) * i + tensor.bufferStart
        return DoubleTensor(newShape, tensor.mutableBuffer.array(), newStart)
    }

    /**
     * Creates a tensor of a given shape and fills all elements with a given value.
     *
     * @param value the value to fill the output tensor with.
     * @param shape array of integers defining the shape of the output tensor.
     * @return tensor with the [shape] shape and filled with [value].
     */
    public fun full(value: Double, shape: IntArray): DoubleTensor {
        checkEmptyShape(shape)
        val buffer = DoubleArray(shape.reduce(Int::times)) { value }
        return DoubleTensor(shape, buffer)
    }

    /**
     * Returns a tensor with the same shape as `input` filled with [value].
     *
     * @param value the value to fill the output tensor with.
     * @return tensor with the `input` tensor shape and filled with [value].
     */
    public fun Tensor<Double>.fullLike(value: Double): DoubleTensor {
        val shape = tensor.shape
        val buffer = DoubleArray(tensor.numElements) { value }
        return DoubleTensor(shape, buffer)
    }

    /**
     * Returns a tensor filled with the scalar value 0.0, with the shape defined by the variable argument [shape].
     *
     * @param shape array of integers defining the shape of the output tensor.
     * @return tensor filled with the scalar value 0.0, with the [shape] shape.
     */
    public fun zeros(shape: IntArray): DoubleTensor = full(0.0, shape)

    /**
     * Returns a tensor filled with the scalar value 0.0, with the same shape as a given array.
     *
     * @return tensor filled with the scalar value 0.0, with the same shape as `input` tensor.
     */
    public fun Tensor<Double>.zeroesLike(): DoubleTensor = tensor.fullLike(0.0)

    /**
     * Returns a tensor filled with the scalar value 1.0, with the shape defined by the variable argument [shape].
     *
     * @param shape array of integers defining the shape of the output tensor.
     * @return tensor filled with the scalar value 1.0, with the [shape] shape.
     */
    public fun ones(shape: IntArray): DoubleTensor = full(1.0, shape)

    /**
     * Returns a tensor filled with the scalar value 1.0, with the same shape as a given array.
     *
     * @return tensor filled with the scalar value 1.0, with the same shape as `input` tensor.
     */
    public fun Tensor<Double>.onesLike(): DoubleTensor = tensor.fullLike(1.0)

    /**
     * Returns a 2-D tensor with shape ([n], [n]), with ones on the diagonal and zeros elsewhere.
     *
     * @param n the number of rows and columns
     * @return a 2-D tensor with ones on the diagonal and zeros elsewhere.
     */
    public fun eye(n: Int): DoubleTensor {
        val shape = intArrayOf(n, n)
        val buffer = DoubleArray(n * n) { 0.0 }
        val res = DoubleTensor(shape, buffer)
        for (i in 0 until n) {
            res[intArrayOf(i, i)] = 1.0
        }
        return res
    }

    /**
     * Return a copy of the tensor.
     *
     * @return a copy of the `input` tensor with a copied buffer.
     */
    public fun Tensor<Double>.copy(): DoubleTensor {
        return DoubleTensor(tensor.shape, tensor.mutableBuffer.array().copyOf(), tensor.bufferStart)
    }

    override fun Double.plus(other: Tensor<Double>): DoubleTensor {
        val resBuffer = DoubleArray(other.tensor.numElements) { i ->
            other.tensor.mutableBuffer.array()[other.tensor.bufferStart + i] + this
        }
        return DoubleTensor(other.shape, resBuffer)
    }

    override fun Tensor<Double>.plus(value: Double): DoubleTensor = value + tensor

    override fun Tensor<Double>.plus(other: Tensor<Double>): DoubleTensor {
        checkShapesCompatible(tensor, other.tensor)
        val resBuffer = DoubleArray(tensor.numElements) { i ->
            tensor.mutableBuffer.array()[i] + other.tensor.mutableBuffer.array()[i]
        }
        return DoubleTensor(tensor.shape, resBuffer)
    }

    override fun Tensor<Double>.plusAssign(value: Double) {
        for (i in 0 until tensor.numElements) {
            tensor.mutableBuffer.array()[tensor.bufferStart + i] += value
        }
    }

    override fun Tensor<Double>.plusAssign(other: Tensor<Double>) {
        checkShapesCompatible(tensor, other.tensor)
        for (i in 0 until tensor.numElements) {
            tensor.mutableBuffer.array()[tensor.bufferStart + i] +=
                other.tensor.mutableBuffer.array()[tensor.bufferStart + i]
        }
    }

    override fun Double.minus(other: Tensor<Double>): DoubleTensor {
        val resBuffer = DoubleArray(other.tensor.numElements) { i ->
            this - other.tensor.mutableBuffer.array()[other.tensor.bufferStart + i]
        }
        return DoubleTensor(other.shape, resBuffer)
    }

    override fun Tensor<Double>.minus(value: Double): DoubleTensor {
        val resBuffer = DoubleArray(tensor.numElements) { i ->
            tensor.mutableBuffer.array()[tensor.bufferStart + i] - value
        }
        return DoubleTensor(tensor.shape, resBuffer)
    }

    override fun Tensor<Double>.minus(other: Tensor<Double>): DoubleTensor {
        checkShapesCompatible(tensor, other)
        val resBuffer = DoubleArray(tensor.numElements) { i ->
            tensor.mutableBuffer.array()[i] - other.tensor.mutableBuffer.array()[i]
        }
        return DoubleTensor(tensor.shape, resBuffer)
    }

    override fun Tensor<Double>.minusAssign(value: Double) {
        for (i in 0 until tensor.numElements) {
            tensor.mutableBuffer.array()[tensor.bufferStart + i] -= value
        }
    }

    override fun Tensor<Double>.minusAssign(other: Tensor<Double>) {
        checkShapesCompatible(tensor, other)
        for (i in 0 until tensor.numElements) {
            tensor.mutableBuffer.array()[tensor.bufferStart + i] -=
                other.tensor.mutableBuffer.array()[tensor.bufferStart + i]
        }
    }

    override fun Double.times(other: Tensor<Double>): DoubleTensor {
        val resBuffer = DoubleArray(other.tensor.numElements) { i ->
            other.tensor.mutableBuffer.array()[other.tensor.bufferStart + i] * this
        }
        return DoubleTensor(other.shape, resBuffer)
    }

    override fun Tensor<Double>.times(value: Double): DoubleTensor = value * tensor

    override fun Tensor<Double>.times(other: Tensor<Double>): DoubleTensor {
        checkShapesCompatible(tensor, other)
        val resBuffer = DoubleArray(tensor.numElements) { i ->
            tensor.mutableBuffer.array()[tensor.bufferStart + i] *
                    other.tensor.mutableBuffer.array()[other.tensor.bufferStart + i]
        }
        return DoubleTensor(tensor.shape, resBuffer)
    }

    override fun Tensor<Double>.timesAssign(value: Double) {
        for (i in 0 until tensor.numElements) {
            tensor.mutableBuffer.array()[tensor.bufferStart + i] *= value
        }
    }

    override fun Tensor<Double>.timesAssign(other: Tensor<Double>) {
        checkShapesCompatible(tensor, other)
        for (i in 0 until tensor.numElements) {
            tensor.mutableBuffer.array()[tensor.bufferStart + i] *=
                other.tensor.mutableBuffer.array()[tensor.bufferStart + i]
        }
    }

    override fun Double.div(other: Tensor<Double>): DoubleTensor {
        val resBuffer = DoubleArray(other.tensor.numElements) { i ->
            this / other.tensor.mutableBuffer.array()[other.tensor.bufferStart + i]
        }
        return DoubleTensor(other.shape, resBuffer)
    }

    override fun Tensor<Double>.div(value: Double): DoubleTensor {
        val resBuffer = DoubleArray(tensor.numElements) { i ->
            tensor.mutableBuffer.array()[tensor.bufferStart + i] / value
        }
        return DoubleTensor(shape, resBuffer)
    }

    override fun Tensor<Double>.div(other: Tensor<Double>): DoubleTensor {
        checkShapesCompatible(tensor, other)
        val resBuffer = DoubleArray(tensor.numElements) { i ->
            tensor.mutableBuffer.array()[other.tensor.bufferStart + i] /
                    other.tensor.mutableBuffer.array()[other.tensor.bufferStart + i]
        }
        return DoubleTensor(tensor.shape, resBuffer)
    }

    override fun Tensor<Double>.divAssign(value: Double) {
        for (i in 0 until tensor.numElements) {
            tensor.mutableBuffer.array()[tensor.bufferStart + i] /= value
        }
    }

    override fun Tensor<Double>.divAssign(other: Tensor<Double>) {
        checkShapesCompatible(tensor, other)
        for (i in 0 until tensor.numElements) {
            tensor.mutableBuffer.array()[tensor.bufferStart + i] /=
                other.tensor.mutableBuffer.array()[tensor.bufferStart + i]
        }
    }

    override fun Tensor<Double>.unaryMinus(): DoubleTensor {
        val resBuffer = DoubleArray(tensor.numElements) { i ->
            tensor.mutableBuffer.array()[tensor.bufferStart + i].unaryMinus()
        }
        return DoubleTensor(tensor.shape, resBuffer)
    }

    override fun Tensor<Double>.transpose(i: Int, j: Int): DoubleTensor {
        val ii = tensor.minusIndex(i)
        val jj = tensor.minusIndex(j)
        checkTranspose(tensor.dimension, ii, jj)
        val n = tensor.numElements
        val resBuffer = DoubleArray(n)

        val resShape = tensor.shape.copyOf()
        resShape[ii] = resShape[jj].also { resShape[jj] = resShape[ii] }

        val resTensor = DoubleTensor(resShape, resBuffer)

        for (offset in 0 until n) {
            val oldMultiIndex = tensor.linearStructure.index(offset)
            val newMultiIndex = oldMultiIndex.copyOf()
            newMultiIndex[ii] = newMultiIndex[jj].also { newMultiIndex[jj] = newMultiIndex[ii] }

            val linearIndex = resTensor.linearStructure.offset(newMultiIndex)
            resTensor.mutableBuffer.array()[linearIndex] =
                tensor.mutableBuffer.array()[tensor.bufferStart + offset]
        }
        return resTensor
    }


    override fun Tensor<Double>.view(shape: IntArray): DoubleTensor {
        checkView(tensor, shape)
        return DoubleTensor(shape, tensor.mutableBuffer.array(), tensor.bufferStart)
    }

    override fun Tensor<Double>.viewAs(other: Tensor<Double>): DoubleTensor {
        return tensor.view(other.shape)
    }

    override infix fun Tensor<Double>.dot(other: Tensor<Double>): DoubleTensor {
        if (tensor.shape.size == 1 && other.shape.size == 1) {
            return DoubleTensor(intArrayOf(1), doubleArrayOf(tensor.times(other).tensor.mutableBuffer.array().sum()))
        }

        var newThis = tensor.copy()
        var newOther = other.copy()

        var penultimateDim = false
        var lastDim = false
        if (tensor.shape.size == 1) {
            penultimateDim = true
            newThis = tensor.view(intArrayOf(1) + tensor.shape)
        }
        if (other.shape.size == 1) {
            lastDim = true
            newOther = other.tensor.view(other.shape + intArrayOf(1))
        }

        val broadcastTensors = broadcastOuterTensors(newThis.tensor, newOther.tensor)
        newThis = broadcastTensors[0]
        newOther = broadcastTensors[1]

        val l = newThis.shape[newThis.shape.size - 2]
        val m1 = newThis.shape[newThis.shape.size - 1]
        val m2 = newOther.shape[newOther.shape.size - 2]
        val n = newOther.shape[newOther.shape.size - 1]
        check(m1 == m2) {
            throw RuntimeException("Tensors dot operation dimension mismatch: ($l, $m1) x ($m2, $n)")
        }

        val resShape = newThis.shape.sliceArray(0..(newThis.shape.size - 2)) + intArrayOf(newOther.shape.last())
        val resSize = resShape.reduce { acc, i -> acc * i }
        val resTensor = DoubleTensor(resShape, DoubleArray(resSize))

        for ((res, ab) in resTensor.matrixSequence().zip(newThis.matrixSequence().zip(newOther.matrixSequence()))) {
            val (a, b) = ab
            dotHelper(a.as2D(), b.as2D(), res.as2D(), l, m1, n)
        }

        if (penultimateDim) {
            return resTensor.view(
                resTensor.shape.dropLast(2).toIntArray() +
                        intArrayOf(resTensor.shape.last())
            )
        }
        if (lastDim) {
            return resTensor.view(resTensor.shape.dropLast(1).toIntArray())
        }
        return resTensor
    }

    override fun diagonalEmbedding(diagonalEntries: Tensor<Double>, offset: Int, dim1: Int, dim2: Int):
            DoubleTensor {
        val n = diagonalEntries.shape.size
        val d1 = minusIndexFrom(n + 1, dim1)
        val d2 = minusIndexFrom(n + 1, dim2)

        check(d1 != d2) {
            "Diagonal dimensions cannot be identical $d1, $d2"
        }
        check(d1 <= n && d2 <= n) {
            "Dimension out of range"
        }

        var lessDim = d1
        var greaterDim = d2
        var realOffset = offset
        if (lessDim > greaterDim) {
            realOffset *= -1
            lessDim = greaterDim.also { greaterDim = lessDim }
        }

        val resShape = diagonalEntries.shape.slice(0 until lessDim).toIntArray() +
                intArrayOf(diagonalEntries.shape[n - 1] + abs(realOffset)) +
                diagonalEntries.shape.slice(lessDim until greaterDim - 1).toIntArray() +
                intArrayOf(diagonalEntries.shape[n - 1] + abs(realOffset)) +
                diagonalEntries.shape.slice(greaterDim - 1 until n - 1).toIntArray()
        val resTensor = zeros(resShape)

        for (i in 0 until diagonalEntries.tensor.numElements) {
            val multiIndex = diagonalEntries.tensor.linearStructure.index(i)

            var offset1 = 0
            var offset2 = abs(realOffset)
            if (realOffset < 0) {
                offset1 = offset2.also { offset2 = offset1 }
            }
            val diagonalMultiIndex = multiIndex.slice(0 until lessDim).toIntArray() +
                    intArrayOf(multiIndex[n - 1] + offset1) +
                    multiIndex.slice(lessDim until greaterDim - 1).toIntArray() +
                    intArrayOf(multiIndex[n - 1] + offset2) +
                    multiIndex.slice(greaterDim - 1 until n - 1).toIntArray()

            resTensor[diagonalMultiIndex] = diagonalEntries[multiIndex]
        }

        return resTensor.tensor
    }

    /**
     * Applies the [transform] function to each element of the tensor and returns the resulting modified tensor.
     *
     * @param transform the function to be applied to each element of the tensor.
     * @return the resulting tensor after applying the function.
     */
    public fun Tensor<Double>.map(transform: (Double) -> Double): DoubleTensor {
        return DoubleTensor(
            tensor.shape,
            tensor.mutableBuffer.array().map { transform(it) }.toDoubleArray(),
            tensor.bufferStart
        )
    }

    /**
     * Compares element-wise two tensors with a specified precision.
     *
     * @param other the tensor to compare with `input` tensor.
     * @param epsilon permissible error when comparing two Double values.
     * @return true if two tensors have the same shape and elements, false otherwise.
     */
    public fun Tensor<Double>.eq(other: Tensor<Double>, epsilon: Double): Boolean {
        return tensor.eq(other) { x, y -> abs(x - y) < epsilon }
    }

    /**
     * Compares element-wise two tensors.
     * Comparison of two Double values occurs with 1e-5 precision.
     *
     * @param other the tensor to compare with `input` tensor.
     * @return true if two tensors have the same shape and elements, false otherwise.
     */
    public infix fun Tensor<Double>.eq(other: Tensor<Double>): Boolean = tensor.eq(other, 1e-5)

    private fun Tensor<Double>.eq(
        other: Tensor<Double>,
        eqFunction: (Double, Double) -> Boolean
    ): Boolean {
        checkShapesCompatible(tensor, other)
        val n = tensor.numElements
        if (n != other.tensor.numElements) {
            return false
        }
        for (i in 0 until n) {
            if (!eqFunction(
                    tensor.mutableBuffer[tensor.bufferStart + i],
                    other.tensor.mutableBuffer[other.tensor.bufferStart + i]
                )
            ) {
                return false
            }
        }
        return true
    }

    /**
     * Returns a tensor of random numbers drawn from normal distributions with 0.0 mean and 1.0 standard deviation.
     *
     * @param shape the desired shape for the output tensor.
     * @param seed the random seed of the pseudo-random number generator.
     * @return tensor of a given shape filled with numbers from the normal distribution
     * with 0.0 mean and 1.0 standard deviation.
     */
    public fun randomNormal(shape: IntArray, seed: Long = 0): DoubleTensor =
        DoubleTensor(shape, getRandomNormals(shape.reduce(Int::times), seed))

    /**
     * Returns a tensor with the same shape as `input` of random numbers drawn from normal distributions
     * with 0.0 mean and 1.0 standard deviation.
     *
     * @param seed the random seed of the pseudo-random number generator.
     * @return tensor with the same shape as `input` filled with numbers from the normal distribution
     * with 0.0 mean and 1.0 standard deviation.
     */
    public fun Tensor<Double>.randomNormalLike(seed: Long = 0): DoubleTensor =
        DoubleTensor(tensor.shape, getRandomNormals(tensor.shape.reduce(Int::times), seed))

    /**
     * Concatenates a sequence of tensors with equal shapes along the first dimension.
     *
     * @param tensors the [List] of tensors with same shapes to concatenate
     * @return tensor with concatenation result
     */
    public fun stack(tensors: List<Tensor<Double>>): DoubleTensor {
        check(tensors.isNotEmpty()) { "List must have at least 1 element" }
        val shape = tensors[0].shape
        check(tensors.all { it.shape contentEquals shape }) { "Tensors must have same shapes" }
        val resShape = intArrayOf(tensors.size) + shape
        val resBuffer = tensors.flatMap {
            it.tensor.mutableBuffer.array().drop(it.tensor.bufferStart).take(it.tensor.numElements)
        }.toDoubleArray()
        return DoubleTensor(resShape, resBuffer, 0)
    }

    /**
     * Build tensor from rows of input tensor
     *
     * @param indices the [IntArray] of 1-dimensional indices
     * @return tensor with rows corresponding to rows by [indices]
     */
    public fun Tensor<Double>.rowsByIndices(indices: IntArray): DoubleTensor {
        return stack(indices.map { this[it] })
    }

    internal fun Tensor<Double>.fold(foldFunction: (DoubleArray) -> Double): Double =
        foldFunction(tensor.toDoubleArray())

    internal fun Tensor<Double>.foldDim(
        foldFunction: (DoubleArray) -> Double,
        dim: Int,
        keepDim: Boolean
    ): DoubleTensor {
        check(dim < dimension) { "Dimension $dim out of range $dimension" }
        val resShape = if (keepDim) {
            shape.take(dim).toIntArray() + intArrayOf(1) + shape.takeLast(dimension - dim - 1).toIntArray()
        } else {
            shape.take(dim).toIntArray() + shape.takeLast(dimension - dim - 1).toIntArray()
        }
        val resNumElements = resShape.reduce(Int::times)
        val resTensor = DoubleTensor(resShape, DoubleArray(resNumElements) { 0.0 }, 0)
        for (index in resTensor.linearStructure.indices()) {
            val prefix = index.take(dim).toIntArray()
            val suffix = index.takeLast(dimension - dim - 1).toIntArray()
            resTensor[index] = foldFunction(DoubleArray(shape[dim]) { i ->
                tensor[prefix + intArrayOf(i) + suffix]
            })
        }

        return resTensor
    }

    override fun Tensor<Double>.sum(): Double = tensor.fold { it.sum() }

    override fun Tensor<Double>.sum(dim: Int, keepDim: Boolean): DoubleTensor =
        foldDim({ x -> x.sum() }, dim, keepDim)

    override fun Tensor<Double>.min(): Double = this.fold { it.minOrNull()!! }

    override fun Tensor<Double>.min(dim: Int, keepDim: Boolean): DoubleTensor =
        foldDim({ x -> x.minOrNull()!! }, dim, keepDim)

    override fun Tensor<Double>.max(): Double = this.fold { it.maxOrNull()!! }

    override fun Tensor<Double>.max(dim: Int, keepDim: Boolean): DoubleTensor =
        foldDim({ x -> x.maxOrNull()!! }, dim, keepDim)

    override fun Tensor<Double>.argMax(dim: Int, keepDim: Boolean): DoubleTensor =
        foldDim({ x ->
            x.withIndex().maxByOrNull { it.value }?.index!!.toDouble()
        }, dim, keepDim)

}
