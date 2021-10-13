/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package space.kscience.kmath.tensors.core

import space.kscience.kmath.misc.UnstableKMathAPI
import space.kscience.kmath.tensors.api.Tensor
import space.kscience.kmath.tensors.core.internal.array
import space.kscience.kmath.tensors.core.internal.broadcastTensors
import space.kscience.kmath.tensors.core.internal.broadcastTo
import space.kscience.kmath.tensors.core.internal.tensor

/**
 * Basic linear algebra operations implemented with broadcasting.
 * For more information: https://pytorch.org/docs/stable/notes/broadcasting.html
 */
public object BroadcastDoubleTensorAlgebra : DoubleTensorAlgebra() {

    override fun Tensor<Double>.plus(other: Tensor<Double>): DoubleTensor {
        val broadcast = broadcastTensors(tensor, other.tensor)
        val newThis = broadcast[0]
        val newOther = broadcast[1]
        val resBuffer = DoubleArray(newThis.linearStructure.linearSize) { i ->
            newThis.mutableBuffer.array()[i] + newOther.mutableBuffer.array()[i]
        }
        return DoubleTensor(newThis.shape, resBuffer)
    }

    override fun Tensor<Double>.plusAssign(other: Tensor<Double>) {
        val newOther = broadcastTo(other.tensor, tensor.shape)
        for (i in 0 until tensor.linearStructure.linearSize) {
            tensor.mutableBuffer.array()[tensor.bufferStart + i] +=
                newOther.mutableBuffer.array()[tensor.bufferStart + i]
        }
    }

    override fun Tensor<Double>.minus(other: Tensor<Double>): DoubleTensor {
        val broadcast = broadcastTensors(tensor, other.tensor)
        val newThis = broadcast[0]
        val newOther = broadcast[1]
        val resBuffer = DoubleArray(newThis.linearStructure.linearSize) { i ->
            newThis.mutableBuffer.array()[i] - newOther.mutableBuffer.array()[i]
        }
        return DoubleTensor(newThis.shape, resBuffer)
    }

    override fun Tensor<Double>.minusAssign(other: Tensor<Double>) {
        val newOther = broadcastTo(other.tensor, tensor.shape)
        for (i in 0 until tensor.linearStructure.linearSize) {
            tensor.mutableBuffer.array()[tensor.bufferStart + i] -=
                newOther.mutableBuffer.array()[tensor.bufferStart + i]
        }
    }

    override fun Tensor<Double>.times(other: Tensor<Double>): DoubleTensor {
        val broadcast = broadcastTensors(tensor, other.tensor)
        val newThis = broadcast[0]
        val newOther = broadcast[1]
        val resBuffer = DoubleArray(newThis.linearStructure.linearSize) { i ->
            newThis.mutableBuffer.array()[newThis.bufferStart + i] *
                    newOther.mutableBuffer.array()[newOther.bufferStart + i]
        }
        return DoubleTensor(newThis.shape, resBuffer)
    }

    override fun Tensor<Double>.timesAssign(other: Tensor<Double>) {
        val newOther = broadcastTo(other.tensor, tensor.shape)
        for (i in 0 until tensor.linearStructure.linearSize) {
            tensor.mutableBuffer.array()[tensor.bufferStart + i] *=
                newOther.mutableBuffer.array()[tensor.bufferStart + i]
        }
    }

    override fun Tensor<Double>.div(other: Tensor<Double>): DoubleTensor {
        val broadcast = broadcastTensors(tensor, other.tensor)
        val newThis = broadcast[0]
        val newOther = broadcast[1]
        val resBuffer = DoubleArray(newThis.linearStructure.linearSize) { i ->
            newThis.mutableBuffer.array()[newOther.bufferStart + i] /
                    newOther.mutableBuffer.array()[newOther.bufferStart + i]
        }
        return DoubleTensor(newThis.shape, resBuffer)
    }

    override fun Tensor<Double>.divAssign(other: Tensor<Double>) {
        val newOther = broadcastTo(other.tensor, tensor.shape)
        for (i in 0 until tensor.linearStructure.linearSize) {
            tensor.mutableBuffer.array()[tensor.bufferStart + i] /=
                newOther.mutableBuffer.array()[tensor.bufferStart + i]
        }
    }
}


/**
 * Compute a value using broadcast double tensor algebra
 */
@UnstableKMathAPI
public fun <R> DoubleTensorAlgebra.withBroadcast(block: BroadcastDoubleTensorAlgebra.() -> R): R =
    BroadcastDoubleTensorAlgebra.block()