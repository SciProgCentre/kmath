/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.tensors.core

import space.kscience.kmath.misc.PerformancePitfall
import space.kscience.kmath.misc.UnstableKMathAPI
import space.kscience.kmath.nd.StructureND
import space.kscience.kmath.tensors.api.Tensor
import space.kscience.kmath.tensors.core.internal.array
import space.kscience.kmath.tensors.core.internal.broadcastTensors
import space.kscience.kmath.tensors.core.internal.broadcastTo
import space.kscience.kmath.tensors.core.internal.tensor

/**
 * Basic linear algebra operations implemented with broadcasting.
 * For more information: https://pytorch.org/docs/stable/notes/broadcasting.html
 */

@PerformancePitfall
public object BroadcastDoubleTensorAlgebra : DoubleTensorAlgebra() {

    override fun add(left: StructureND<Double>, right: StructureND<Double>): DoubleTensor {
        val broadcast = broadcastTensors(left.tensor, right.tensor)
        val newThis = broadcast[0]
        val newOther = broadcast[1]
        val resBuffer = DoubleArray(newThis.indices.linearSize) { i ->
            newThis.mutableBuffer.array()[i] + newOther.mutableBuffer.array()[i]
        }
        return DoubleTensor(newThis.shape, resBuffer)
    }

    override fun Tensor<Double>.plusAssign(arg: StructureND<Double>) {
        val newOther = broadcastTo(arg.tensor, tensor.shape)
        for (i in 0 until tensor.indices.linearSize) {
            tensor.mutableBuffer.array()[tensor.bufferStart + i] +=
                newOther.mutableBuffer.array()[tensor.bufferStart + i]
        }
    }

    override fun subtract(left: StructureND<Double>, right: StructureND<Double>): DoubleTensor {
        val broadcast = broadcastTensors(left.tensor, right.tensor)
        val newThis = broadcast[0]
        val newOther = broadcast[1]
        val resBuffer = DoubleArray(newThis.indices.linearSize) { i ->
            newThis.mutableBuffer.array()[i] - newOther.mutableBuffer.array()[i]
        }
        return DoubleTensor(newThis.shape, resBuffer)
    }

    override fun Tensor<Double>.minusAssign(arg: StructureND<Double>) {
        val newOther = broadcastTo(arg.tensor, tensor.shape)
        for (i in 0 until tensor.indices.linearSize) {
            tensor.mutableBuffer.array()[tensor.bufferStart + i] -=
                newOther.mutableBuffer.array()[tensor.bufferStart + i]
        }
    }

    override fun multiply(left: StructureND<Double>, right: StructureND<Double>): DoubleTensor {
        val broadcast = broadcastTensors(left.tensor, right.tensor)
        val newThis = broadcast[0]
        val newOther = broadcast[1]
        val resBuffer = DoubleArray(newThis.indices.linearSize) { i ->
            newThis.mutableBuffer.array()[newThis.bufferStart + i] *
                    newOther.mutableBuffer.array()[newOther.bufferStart + i]
        }
        return DoubleTensor(newThis.shape, resBuffer)
    }

    override fun Tensor<Double>.timesAssign(arg: StructureND<Double>) {
        val newOther = broadcastTo(arg.tensor, tensor.shape)
        for (i in 0 until tensor.indices.linearSize) {
            tensor.mutableBuffer.array()[tensor.bufferStart + i] *=
                newOther.mutableBuffer.array()[tensor.bufferStart + i]
        }
    }

    override fun divide(left: StructureND<Double>, right: StructureND<Double>): DoubleTensor {
        val broadcast = broadcastTensors(left.tensor, right.tensor)
        val newThis = broadcast[0]
        val newOther = broadcast[1]
        val resBuffer = DoubleArray(newThis.indices.linearSize) { i ->
            newThis.mutableBuffer.array()[newOther.bufferStart + i] /
                    newOther.mutableBuffer.array()[newOther.bufferStart + i]
        }
        return DoubleTensor(newThis.shape, resBuffer)
    }

    override fun Tensor<Double>.divAssign(arg: StructureND<Double>) {
        val newOther = broadcastTo(arg.tensor, tensor.shape)
        for (i in 0 until tensor.indices.linearSize) {
            tensor.mutableBuffer.array()[tensor.bufferStart + i] /=
                newOther.mutableBuffer.array()[tensor.bufferStart + i]
        }
    }
}


/**
 * Compute a value using broadcast double tensor algebra
 */
@UnstableKMathAPI
@PerformancePitfall
public fun <R> DoubleTensorAlgebra.withBroadcast(block: BroadcastDoubleTensorAlgebra.() -> R): R =
    BroadcastDoubleTensorAlgebra.block()