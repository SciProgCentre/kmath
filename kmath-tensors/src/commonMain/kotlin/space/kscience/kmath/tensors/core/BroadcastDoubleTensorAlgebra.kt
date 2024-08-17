/*
 * Copyright 2018-2024 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.tensors.core

import space.kscience.kmath.UnstableKMathAPI
import space.kscience.kmath.nd.StructureND
import space.kscience.kmath.structures.Float64
import space.kscience.kmath.structures.Float64Buffer
import space.kscience.kmath.tensors.api.Tensor
import space.kscience.kmath.tensors.core.internal.broadcastTensors
import space.kscience.kmath.tensors.core.internal.broadcastTo

/**
 * Basic linear algebra operations implemented with broadcasting.
 * For more information: https://pytorch.org/docs/stable/notes/broadcasting.html
 */
public object BroadcastDoubleTensorAlgebra : DoubleTensorAlgebra() {

    override fun StructureND<Float64>.plus(arg: StructureND<Float64>): DoubleTensor {
        val broadcast = broadcastTensors(asDoubleTensor(), arg.asDoubleTensor())
        val newThis = broadcast[0]
        val newOther = broadcast[1]
        val resBuffer = Float64Buffer(newThis.indices.linearSize) { i ->
            newThis.source[i] + newOther.source[i]
        }
        return DoubleTensor(newThis.shape, resBuffer)
    }

    override fun Tensor<Float64>.plusAssign(arg: StructureND<Float64>) {
        val newOther = broadcastTo(arg.asDoubleTensor(), asDoubleTensor().shape)
        for (i in 0 until asDoubleTensor().indices.linearSize) {
            asDoubleTensor().source[i] += newOther.source[i]
        }
    }

    override fun StructureND<Float64>.minus(arg: StructureND<Float64>): DoubleTensor {
        val broadcast = broadcastTensors(asDoubleTensor(), arg.asDoubleTensor())
        val newThis = broadcast[0]
        val newOther = broadcast[1]
        val resBuffer = Float64Buffer(newThis.indices.linearSize) { i ->
            newThis.source[i] - newOther.source[i]
        }
        return DoubleTensor(newThis.shape, resBuffer)
    }

    override fun Tensor<Float64>.minusAssign(arg: StructureND<Float64>) {
        val newOther = broadcastTo(arg.asDoubleTensor(), asDoubleTensor().shape)
        for (i in 0 until indices.linearSize) {
            asDoubleTensor().source[i] -= newOther.source[i]
        }
    }

    override fun StructureND<Float64>.times(arg: StructureND<Float64>): DoubleTensor {
        val broadcast = broadcastTensors(asDoubleTensor(), arg.asDoubleTensor())
        val newThis = broadcast[0]
        val newOther = broadcast[1]
        val resBuffer = Float64Buffer(newThis.indices.linearSize) { i ->
            newThis.source[i] * newOther.source[i]
        }
        return DoubleTensor(newThis.shape, resBuffer)
    }

    override fun Tensor<Float64>.timesAssign(arg: StructureND<Float64>) {
        val newOther = broadcastTo(arg.asDoubleTensor(), asDoubleTensor().shape)
        for (i in 0 until indices.linearSize) {
            asDoubleTensor().source[+i] *= newOther.source[i]
        }
    }

    override fun StructureND<Float64>.div(arg: StructureND<Float64>): DoubleTensor {
        val broadcast = broadcastTensors(asDoubleTensor(), arg.asDoubleTensor())
        val newThis = broadcast[0]
        val newOther = broadcast[1]
        val resBuffer = Float64Buffer(newThis.indices.linearSize) { i ->
            newThis.source[i] / newOther.source[i]
        }
        return DoubleTensor(newThis.shape, resBuffer)
    }

    override fun Tensor<Float64>.divAssign(arg: StructureND<Float64>) {
        val newOther = broadcastTo(arg.asDoubleTensor(), asDoubleTensor().shape)
        for (i in 0 until indices.linearSize) {
            asDoubleTensor().source[i] /= newOther.source[i]
        }
    }
}


/**
 * Compute a value using broadcast double tensor algebra
 */
@UnstableKMathAPI
public fun <R> DoubleTensorAlgebra.withBroadcast(block: BroadcastDoubleTensorAlgebra.() -> R): R =
    BroadcastDoubleTensorAlgebra.block()