package space.kscience.kmath.tensors.core

import space.kscience.kmath.tensors.core.algebras.BroadcastDoubleTensorAlgebra
import kotlin.math.max

public inline fun <R> BroadcastDoubleTensorAlgebra(block: BroadcastDoubleTensorAlgebra.() -> R): R =
    BroadcastDoubleTensorAlgebra().block()

internal inline fun multiIndexBroadCasting(tensor: DoubleTensor, resTensor: DoubleTensor, linearSize: Int) {
    for (linearIndex in 0 until linearSize) {
        val totalMultiIndex = resTensor.linearStructure.index(linearIndex)
        val curMultiIndex = tensor.shape.copyOf()

        val offset = totalMultiIndex.size - curMultiIndex.size

        for (i in curMultiIndex.indices) {
            if (curMultiIndex[i] != 1) {
                curMultiIndex[i] = totalMultiIndex[i + offset]
            } else {
                curMultiIndex[i] = 0
            }
        }

        val curLinearIndex = tensor.linearStructure.offset(curMultiIndex)
        resTensor.buffer.array()[linearIndex] =
            tensor.buffer.array()[tensor.bufferStart + curLinearIndex]
    }
}

internal inline fun broadcastShapes(vararg shapes: IntArray): IntArray {
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
            if (curDim != 1 && totalShape[i + offset] != curDim) {
                throw RuntimeException("Shapes are not compatible and cannot be broadcast")
            }
        }
    }

    return totalShape
}

internal inline fun broadcastTo(tensor: DoubleTensor, newShape: IntArray): DoubleTensor {
    if (tensor.shape.size > newShape.size) {
        throw RuntimeException("Tensor is not compatible with the new shape")
    }

    val n = newShape.reduce { acc, i -> acc * i }
    val resTensor = DoubleTensor(newShape, DoubleArray(n))

    for (i in tensor.shape.indices) {
        val curDim = tensor.shape[i]
        val offset = newShape.size - tensor.shape.size
        if (curDim != 1 && newShape[i + offset] != curDim) {
            throw RuntimeException("Tensor is not compatible with the new shape and cannot be broadcast")
        }
    }

    multiIndexBroadCasting(tensor, resTensor, n)
    return resTensor
}

internal inline fun broadcastTensors(vararg tensors: DoubleTensor): List<DoubleTensor> {
    val totalShape = broadcastShapes(*(tensors.map { it.shape }).toTypedArray())
    val n = totalShape.reduce { acc, i -> acc * i }

    val res = ArrayList<DoubleTensor>(0)
    for (tensor in tensors) {
        val resTensor = DoubleTensor(totalShape, DoubleArray(n))
        multiIndexBroadCasting(tensor, resTensor, n)
        res.add(resTensor)
    }

    return res
}

internal inline fun broadcastOuterTensors(vararg tensors: DoubleTensor): List<DoubleTensor> {
    val onlyTwoDims = tensors.asSequence().onEach {
        require(it.shape.size >= 2) {
            throw RuntimeException("Tensors must have at least 2 dimensions")
        }
    }.any { it.shape.size != 2 }

    if (!onlyTwoDims) {
        return tensors.asList()
    }

    val totalShape = broadcastShapes(*(tensors.map { it.shape.sliceArray(0..it.shape.size - 3) }).toTypedArray())
    val n = totalShape.reduce { acc, i -> acc * i }

    val res = ArrayList<DoubleTensor>(0)
    for (tensor in tensors) {
        val matrixShape = tensor.shape.sliceArray(tensor.shape.size - 2 until tensor.shape.size).copyOf()
        val matrixSize = matrixShape[0] * matrixShape[1]
        val matrix = DoubleTensor(matrixShape, DoubleArray(matrixSize))

        val outerTensor = DoubleTensor(totalShape, DoubleArray(n))
        val resTensor = DoubleTensor(totalShape + matrixShape, DoubleArray(n * matrixSize))

        for (linearIndex in 0 until n) {
            val totalMultiIndex = outerTensor.linearStructure.index(linearIndex)
            var curMultiIndex = tensor.shape.sliceArray(0..tensor.shape.size - 3).copyOf()
            curMultiIndex = IntArray(totalMultiIndex.size - curMultiIndex.size) { 1 } + curMultiIndex

            val newTensor = DoubleTensor(curMultiIndex + matrixShape, tensor.buffer.array())

            for (i in curMultiIndex.indices) {
                if (curMultiIndex[i] != 1) {
                    curMultiIndex[i] = totalMultiIndex[i]
                } else {
                    curMultiIndex[i] = 0
                }
            }

            for (i in 0 until matrixSize) {
                val curLinearIndex = newTensor.linearStructure.offset(
                    curMultiIndex +
                            matrix.linearStructure.index(i)
                )
                val newLinearIndex = resTensor.linearStructure.offset(
                    totalMultiIndex +
                            matrix.linearStructure.index(i)
                )

                resTensor.buffer.array()[resTensor.bufferStart + newLinearIndex] =
                    newTensor.buffer.array()[newTensor.bufferStart + curLinearIndex]
            }
        }
        res += resTensor
    }

    return res
}