/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.tensors.core.algebras

import kotlin.math.sqrt

import space.kscience.kmath.tensors.api.*
import space.kscience.kmath.tensors.core.*
import space.kscience.kmath.tensors.core.algebras.DoubleStatisticTensorAlgebra.max
import space.kscience.kmath.tensors.core.algebras.DoubleStatisticTensorAlgebra.mean
import space.kscience.kmath.tensors.core.algebras.DoubleStatisticTensorAlgebra.min
import space.kscience.kmath.tensors.core.algebras.DoubleStatisticTensorAlgebra.sum
import space.kscience.kmath.tensors.core.algebras.DoubleStatisticTensorAlgebra.variance

public object DoubleStatisticTensorAlgebra : StatisticTensorAlgebra<Double>, DoubleTensorAlgebra() {

    private fun Tensor<Double>.fold(foldFunction: (DoubleArray) -> Double): Double =
        foldFunction(this.tensor.toDoubleArray())

    private fun Tensor<Double>.foldDim(
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
                this[prefix + intArrayOf(i) + suffix]
            })
        }

        return resTensor
    }

    override fun Tensor<Double>.min(): Double = this.fold { it.minOrNull()!! }

    override fun Tensor<Double>.min(dim: Int, keepDim: Boolean): DoubleTensor =
        foldDim({ x -> x.minOrNull()!! }, dim, keepDim)

    override fun Tensor<Double>.max(): Double = this.fold { it.maxOrNull()!! }

    override fun Tensor<Double>.max(dim: Int, keepDim: Boolean): DoubleTensor =
        foldDim({ x -> x.maxOrNull()!! }, dim, keepDim)

    override fun Tensor<Double>.sum(): Double = this.fold { it.sum() }

    override fun Tensor<Double>.sum(dim: Int, keepDim: Boolean): DoubleTensor =
        foldDim({ x -> x.sum() }, dim, keepDim)

    override fun Tensor<Double>.mean(): Double = this.fold { it.sum() / tensor.numElements }

    override fun Tensor<Double>.mean(dim: Int, keepDim: Boolean): DoubleTensor =
        foldDim(
            { arr ->
                check(dim < dimension) { "Dimension $dim out of range $dimension" }
                arr.sum() / shape[dim]
            },
            dim,
            keepDim
        )

    override fun Tensor<Double>.std(): Double = this.fold { arr ->
        val mean = arr.sum() / tensor.numElements
        sqrt(arr.sumOf { (it - mean) * (it - mean) } / (tensor.numElements - 1))
    }

    override fun Tensor<Double>.std(dim: Int, keepDim: Boolean): DoubleTensor = foldDim(
        { arr ->
            check(dim < dimension) { "Dimension $dim out of range $dimension" }
            val mean = arr.sum() / shape[dim]
            sqrt(arr.sumOf { (it - mean) * (it - mean) } / (shape[dim] - 1))
        },
        dim,
        keepDim
    )

    override fun Tensor<Double>.variance(): Double = this.fold { arr ->
        val mean = arr.sum() / tensor.numElements
        arr.sumOf { (it - mean) * (it - mean) } / (tensor.numElements - 1)
    }

    override fun Tensor<Double>.variance(dim: Int, keepDim: Boolean): DoubleTensor = foldDim(
        { arr ->
            check(dim < dimension) { "Dimension $dim out of range $dimension" }
            val mean = arr.sum() / shape[dim]
            arr.sumOf { (it - mean) * (it - mean) } / (shape[dim] - 1)
        },
        dim,
        keepDim
    )

}
