/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package space.kscience.kmath.tensors.core.internal

import space.kscience.kmath.tensors.core.DoubleTensor
import kotlin.math.max

internal fun multiIndexBroadCasting(tensor: DoubleTensor, resTensor: DoubleTensor, linearSize: Int) {
    for (linearIndex in 0 until linearSize) {
        val totalMultiIndex = resTensor.shapeIndices.index(linearIndex)
        val curMultiIndex = tensor.shape.copyOf()

        val offset = totalMultiIndex.size - curMultiIndex.size

        for (i in curMultiIndex.indices) {
            if (curMultiIndex[i] != 1) {
                curMultiIndex[i] = totalMultiIndex[i + offset]
            } else {
                curMultiIndex[i] = 0
            }
        }

        val curLinearIndex = tensor.shapeIndices.offset(curMultiIndex)
        resTensor.mutableBuffer.array()[linearIndex] =
            tensor.mutableBuffer.array()[tensor.bufferStart + curLinearIndex]
    }
}

internal fun broadcastShapes(vararg shapes: IntArray): IntArray {
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

    return totalShape
}

internal fun broadcastTo(tensor: DoubleTensor, newShape: IntArray): DoubleTensor {
    require(tensor.shape.size <= newShape.size) {
        "Tensor is not compatible with the new shape"
    }

    val n = newShape.reduce { acc, i -> acc * i }
    val resTensor = DoubleTensor(newShape, DoubleArray(n))

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
    val totalShape = broadcastShapes(*(tensors.map { it.shape }).toTypedArray())
    val n = totalShape.reduce { acc, i -> acc * i }

    return tensors.map { tensor ->
        val resTensor = DoubleTensor(totalShape, DoubleArray(n))
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

    val totalShape = broadcastShapes(*(tensors.map { it.shape.sliceArray(0..it.shape.size - 3) }).toTypedArray())
    val n = totalShape.reduce { acc, i -> acc * i }

    return buildList {
        for (tensor in tensors) {
            val matrixShape = tensor.shape.sliceArray(tensor.shape.size - 2 until tensor.shape.size).copyOf()
            val matrixSize = matrixShape[0] * matrixShape[1]
            val matrix = DoubleTensor(matrixShape, DoubleArray(matrixSize))

            val outerTensor = DoubleTensor(totalShape, DoubleArray(n))
            val resTensor = DoubleTensor(totalShape + matrixShape, DoubleArray(n * matrixSize))

            for (linearIndex in 0 until n) {
                val totalMultiIndex = outerTensor.shapeIndices.index(linearIndex)
                var curMultiIndex = tensor.shape.sliceArray(0..tensor.shape.size - 3).copyOf()
                curMultiIndex = IntArray(totalMultiIndex.size - curMultiIndex.size) { 1 } + curMultiIndex

                val newTensor = DoubleTensor(curMultiIndex + matrixShape, tensor.mutableBuffer.array())

                for (i in curMultiIndex.indices) {
                    if (curMultiIndex[i] != 1) {
                        curMultiIndex[i] = totalMultiIndex[i]
                    } else {
                        curMultiIndex[i] = 0
                    }
                }

                for (i in 0 until matrixSize) {
                    val curLinearIndex = newTensor.shapeIndices.offset(
                        curMultiIndex +
                                matrix.shapeIndices.index(i)
                    )
                    val newLinearIndex = resTensor.shapeIndices.offset(
                        totalMultiIndex +
                                matrix.shapeIndices.index(i)
                    )

                    resTensor.mutableBuffer.array()[resTensor.bufferStart + newLinearIndex] =
                        newTensor.mutableBuffer.array()[newTensor.bufferStart + curLinearIndex]
                }
            }
            add(resTensor)
        }
    }
}