/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.tensors.core.internal

import space.kscience.kmath.UnsafeKMathAPI
import space.kscience.kmath.nd.*
import space.kscience.kmath.structures.asBuffer
import space.kscience.kmath.tensors.core.DoubleTensor
import kotlin.math.max

internal fun multiIndexBroadCasting(tensor: DoubleTensor, resTensor: DoubleTensor, linearSize: Int) {
    for (linearIndex in 0 until linearSize) {
        val totalMultiIndex = resTensor.indices.index(linearIndex)
        val curMultiIndex = tensor.shape.toArray()

        val offset = totalMultiIndex.size - curMultiIndex.size

        for (i in curMultiIndex.indices) {
            if (curMultiIndex[i] != 1) {
                curMultiIndex[i] = totalMultiIndex[i + offset]
            } else {
                curMultiIndex[i] = 0
            }
        }

        val curLinearIndex = tensor.indices.offset(curMultiIndex)
        resTensor.source[linearIndex] =
            tensor.source[curLinearIndex]
    }
}

internal fun broadcastShapes(shapes: List<ShapeND>): ShapeND {
    var totalDim = 0
    for (shape in shapes) {
        totalDim = max(totalDim, shape.size)
    }

    val totalShape = IntArray(totalDim) { 0 }
    for (shape in shapes) {
        for (i in shape.indices) {
            val curDim = shape[i]
            val offset = totalDim - shape.size
            totalShape[i + offset] = max(totalShape[i + offset], curDim)
        }
    }

    for (shape in shapes) {
        for (i in shape.indices) {
            val curDim = shape[i]
            val offset = totalDim - shape.size
            check(curDim == 1 || totalShape[i + offset] == curDim) {
                "Shapes are not compatible and cannot be broadcast"
            }
        }
    }

    return ShapeND(totalShape)
}

internal fun broadcastTo(tensor: DoubleTensor, newShape: ShapeND): DoubleTensor {
    require(tensor.shape.size <= newShape.size) {
        "Tensor is not compatible with the new shape"
    }

    val n = newShape.linearSize
    val resTensor = DoubleTensor(newShape, DoubleArray(n).asBuffer())

    for (i in tensor.shape.indices) {
        val curDim = tensor.shape[i]
        val offset = newShape.size - tensor.shape.size
        check(curDim == 1 || newShape[i + offset] == curDim) {
            "Tensor is not compatible with the new shape and cannot be broadcast"
        }
    }

    multiIndexBroadCasting(tensor, resTensor, n)
    return resTensor
}

internal fun broadcastTensors(vararg tensors: DoubleTensor): List<DoubleTensor> {
    val totalShape = broadcastShapes(tensors.map { it.shape })
    val n = totalShape.linearSize

    return tensors.map { tensor ->
        val resTensor = DoubleTensor(totalShape, DoubleArray(n).asBuffer())
        multiIndexBroadCasting(tensor, resTensor, n)
        resTensor
    }
}

internal fun broadcastOuterTensors(vararg tensors: DoubleTensor): List<DoubleTensor> {
    val onlyTwoDims = tensors.asSequence().onEach {
        require(it.shape.size >= 2) {
            "Tensors must have at least 2 dimensions"
        }
    }.any { it.shape.size != 2 }

    if (!onlyTwoDims) {
        return tensors.asList()
    }

    val totalShape = broadcastShapes(tensors.map { it.shape.slice(0..it.shape.size - 3) })
    val n = totalShape.linearSize

    return buildList {
        for (tensor in tensors) {
            val matrixShape = tensor.shape.slice(tensor.shape.size - 2 until tensor.shape.size)
            val matrixSize = matrixShape[0] * matrixShape[1]
            val matrix = DoubleTensor(matrixShape, DoubleArray(matrixSize).asBuffer())

            val outerTensor = DoubleTensor(totalShape, DoubleArray(n).asBuffer())
            val resTensor = DoubleTensor(totalShape + matrixShape, DoubleArray(n * matrixSize).asBuffer())

            for (linearIndex in 0 until n) {
                val totalMultiIndex = outerTensor.indices.index(linearIndex)
                @OptIn(UnsafeKMathAPI::class)
                var curMultiIndex = tensor.shape.slice(0..tensor.shape.size - 3).asArray()
                curMultiIndex = IntArray(totalMultiIndex.size - curMultiIndex.size) { 1 } + curMultiIndex

                val newTensor = DoubleTensor(ShapeND(curMultiIndex) + matrixShape, tensor.source)

                for (i in curMultiIndex.indices) {
                    if (curMultiIndex[i] != 1) {
                        curMultiIndex[i] = totalMultiIndex[i]
                    } else {
                        curMultiIndex[i] = 0
                    }
                }

                for (i in 0 until matrixSize) {
                    val curLinearIndex = newTensor.indices.offset(
                        curMultiIndex +
                                matrix.indices.index(i)
                    )
                    val newLinearIndex = resTensor.indices.offset(
                        totalMultiIndex +
                                matrix.indices.index(i)
                    )

                    resTensor.source[newLinearIndex] =
                        newTensor.source[curLinearIndex]
                }
            }
            add(resTensor)
        }
    }
}