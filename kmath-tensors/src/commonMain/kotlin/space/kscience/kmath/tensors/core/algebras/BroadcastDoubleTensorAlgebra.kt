/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.tensors.core.algebras

import space.kscience.kmath.tensors.api.TensorStructure
import space.kscience.kmath.tensors.core.*
import space.kscience.kmath.tensors.core.broadcastTensors
import space.kscience.kmath.tensors.core.broadcastTo

/**
 * Basic linear algebra operations implemented with broadcasting.
 * For more information: https://pytorch.org/docs/stable/notes/broadcasting.html
 */
public object BroadcastDoubleTensorAlgebra : DoubleTensorAlgebra() {

    override fun TensorStructure<Double>.plus(other: TensorStructure<Double>): DoubleTensor {
        val broadcast = broadcastTensors(tensor, other.tensor)
        val newThis = broadcast[0]
        val newOther = broadcast[1]
        val resBuffer = DoubleArray(newThis.linearStructure.linearSize) { i ->
            newThis.mutableBuffer.array()[i] + newOther.mutableBuffer.array()[i]
        }
        return DoubleTensor(newThis.shape, resBuffer)
    }

    override fun TensorStructure<Double>.plusAssign(other: TensorStructure<Double>) {
        val newOther = broadcastTo(other.tensor, tensor.shape)
        for (i in 0 until tensor.linearStructure.linearSize) {
            tensor.mutableBuffer.array()[tensor.bufferStart + i] +=
                newOther.mutableBuffer.array()[tensor.bufferStart + i]
        }
    }

    override fun TensorStructure<Double>.minus(other: TensorStructure<Double>): DoubleTensor {
        val broadcast = broadcastTensors(tensor, other.tensor)
        val newThis = broadcast[0]
        val newOther = broadcast[1]
        val resBuffer = DoubleArray(newThis.linearStructure.linearSize) { i ->
            newThis.mutableBuffer.array()[i] - newOther.mutableBuffer.array()[i]
        }
        return DoubleTensor(newThis.shape, resBuffer)
    }

    override fun TensorStructure<Double>.minusAssign(other: TensorStructure<Double>) {
        val newOther = broadcastTo(other.tensor, tensor.shape)
        for (i in 0 until tensor.linearStructure.linearSize) {
            tensor.mutableBuffer.array()[tensor.bufferStart + i] -=
                newOther.mutableBuffer.array()[tensor.bufferStart + i]
        }
    }

    override fun TensorStructure<Double>.times(other: TensorStructure<Double>): DoubleTensor {
        val broadcast = broadcastTensors(tensor, other.tensor)
        val newThis = broadcast[0]
        val newOther = broadcast[1]
        val resBuffer = DoubleArray(newThis.linearStructure.linearSize) { i ->
            newThis.mutableBuffer.array()[newThis.bufferStart + i] *
                    newOther.mutableBuffer.array()[newOther.bufferStart + i]
        }
        return DoubleTensor(newThis.shape, resBuffer)
    }

    override fun TensorStructure<Double>.timesAssign(other: TensorStructure<Double>) {
        val newOther = broadcastTo(other.tensor, tensor.shape)
        for (i in 0 until tensor.linearStructure.linearSize) {
            tensor.mutableBuffer.array()[tensor.bufferStart + i] *=
                newOther.mutableBuffer.array()[tensor.bufferStart + i]
        }
    }

    override fun TensorStructure<Double>.div(other: TensorStructure<Double>): DoubleTensor {
        val broadcast = broadcastTensors(tensor, other.tensor)
        val newThis = broadcast[0]
        val newOther = broadcast[1]
        val resBuffer = DoubleArray(newThis.linearStructure.linearSize) { i ->
            newThis.mutableBuffer.array()[newOther.bufferStart + i] /
                    newOther.mutableBuffer.array()[newOther.bufferStart + i]
        }
        return DoubleTensor(newThis.shape, resBuffer)
    }

    override fun TensorStructure<Double>.divAssign(other: TensorStructure<Double>) {
        val newOther = broadcastTo(other.tensor, tensor.shape)
        for (i in 0 until tensor.linearStructure.linearSize) {
            tensor.mutableBuffer.array()[tensor.bufferStart + i] /=
                newOther.mutableBuffer.array()[tensor.bufferStart + i]
        }
    }
}