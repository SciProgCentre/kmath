/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.tensors.core

import space.kscience.kmath.UnstableKMathAPI
import space.kscience.kmath.nd.StructureND
import space.kscience.kmath.structures.DoubleBuffer
import space.kscience.kmath.tensors.api.Tensor
import space.kscience.kmath.tensors.core.internal.broadcastTensors
import space.kscience.kmath.tensors.core.internal.broadcastTo

/**
 * Basic linear algebra operations implemented with broadcasting.
 * For more information: https://pytorch.org/docs/stable/notes/broadcasting.html
 */
public object BroadcastDoubleTensorAlgebra : DoubleTensorAlgebra() {

    override fun StructureND<Double>.plus(arg: StructureND<Double>): DoubleTensor {
        val broadcast = broadcastTensors(asDoubleTensor(), arg.asDoubleTensor())
        val newThis = broadcast[0]
        val newOther = broadcast[1]
        val resBuffer = DoubleBuffer(newThis.indices.linearSize) { i ->
            newThis.source[i] + newOther.source[i]
        }
        return DoubleTensor(newThis.shape, resBuffer)
    }

    override fun Tensor<Double>.plusAssign(arg: StructureND<Double>) {
        val newOther = broadcastTo(arg.asDoubleTensor(), asDoubleTensor().shape)
        for (i in 0 until asDoubleTensor().indices.linearSize) {
            asDoubleTensor().source[i] += newOther.source[i]
        }
    }

    override fun StructureND<Double>.minus(arg: StructureND<Double>): DoubleTensor {
        val broadcast = broadcastTensors(asDoubleTensor(), arg.asDoubleTensor())
        val newThis = broadcast[0]
        val newOther = broadcast[1]
        val resBuffer = DoubleBuffer(newThis.indices.linearSize) { i ->
            newThis.source[i] - newOther.source[i]
        }
        return DoubleTensor(newThis.shape, resBuffer)
    }

    override fun Tensor<Double>.minusAssign(arg: StructureND<Double>) {
        val newOther = broadcastTo(arg.asDoubleTensor(), asDoubleTensor().shape)
        for (i in 0 until indices.linearSize) {
            asDoubleTensor().source[i] -= newOther.source[i]
        }
    }

    override fun StructureND<Double>.times(arg: StructureND<Double>): DoubleTensor {
        val broadcast = broadcastTensors(asDoubleTensor(), arg.asDoubleTensor())
        val newThis = broadcast[0]
        val newOther = broadcast[1]
        val resBuffer = DoubleBuffer(newThis.indices.linearSize) { i ->
            newThis.source[i] * newOther.source[i]
        }
        return DoubleTensor(newThis.shape, resBuffer)
    }

    override fun Tensor<Double>.timesAssign(arg: StructureND<Double>) {
        val newOther = broadcastTo(arg.asDoubleTensor(), asDoubleTensor().shape)
        for (i in 0 until indices.linearSize) {
            asDoubleTensor().source[+i] *= newOther.source[i]
        }
    }

    override fun StructureND<Double>.div(arg: StructureND<Double>): DoubleTensor {
        val broadcast = broadcastTensors(asDoubleTensor(), arg.asDoubleTensor())
        val newThis = broadcast[0]
        val newOther = broadcast[1]
        val resBuffer = DoubleBuffer(newThis.indices.linearSize) { i ->
            newThis.source[i] / newOther.source[i]
        }
        return DoubleTensor(newThis.shape, resBuffer)
    }

    override fun Tensor<Double>.divAssign(arg: StructureND<Double>) {
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