/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.tensors.core.algebras

import space.kscience.kmath.nd.as2D
import space.kscience.kmath.tensors.api.TensorPartialDivisionAlgebra
import space.kscience.kmath.tensors.api.TensorStructure
import space.kscience.kmath.tensors.core.*
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

public open class DoubleTensorAlgebra : TensorPartialDivisionAlgebra<Double> {


    override fun TensorStructure<Double>.value(): Double {
        check(tensor.shape contentEquals intArrayOf(1)) {
            "Inconsistent value for tensor of shape ${shape.toList()}"
        }
        return tensor.buffer.array()[tensor.bufferStart]
    }

    public fun fromArray(shape: IntArray, buffer: DoubleArray): DoubleTensor {
        checkEmptyShape(shape)
        checkEmptyDoubleBuffer(buffer)
        checkBufferShapeConsistency(shape, buffer)
        return DoubleTensor(shape, buffer, 0)
    }

    override operator fun TensorStructure<Double>.get(i: Int): DoubleTensor {
        val lastShape = tensor.shape.drop(1).toIntArray()
        val newShape = if (lastShape.isNotEmpty()) lastShape else intArrayOf(1)
        val newStart = newShape.reduce(Int::times) * i + tensor.bufferStart
        return DoubleTensor(newShape, tensor.buffer.array(), newStart)
    }

    public fun full(value: Double, shape: IntArray): DoubleTensor {
        checkEmptyShape(shape)
        val buffer = DoubleArray(shape.reduce(Int::times)) { value }
        return DoubleTensor(shape, buffer)
    }

    public fun TensorStructure<Double>.fullLike(value: Double): DoubleTensor {
        val shape = tensor.shape
        val buffer = DoubleArray(tensor.numElements) { value }
        return DoubleTensor(shape, buffer)
    }

    public fun zeros(shape: IntArray): DoubleTensor = full(0.0, shape)

    public fun TensorStructure<Double>.zeroesLike(): DoubleTensor = tensor.fullLike(0.0)

    public fun ones(shape: IntArray): DoubleTensor = full(1.0, shape)

    public fun TensorStructure<Double>.onesLike(): DoubleTensor = tensor.fullLike(1.0)

    public fun eye(n: Int): DoubleTensor {
        val shape = intArrayOf(n, n)
        val buffer = DoubleArray(n * n) { 0.0 }
        val res = DoubleTensor(shape, buffer)
        for (i in 0 until n) {
            res[intArrayOf(i, i)] = 1.0
        }
        return res
    }

    public fun TensorStructure<Double>.copy(): DoubleTensor {
        return DoubleTensor(tensor.shape, tensor.buffer.array().copyOf(), tensor.bufferStart)
    }

    override fun Double.plus(other: TensorStructure<Double>): DoubleTensor {
        val resBuffer = DoubleArray(other.tensor.numElements) { i ->
            other.tensor.buffer.array()[other.tensor.bufferStart + i] + this
        }
        return DoubleTensor(other.shape, resBuffer)
    }

    override fun TensorStructure<Double>.plus(value: Double): DoubleTensor = value + tensor

    override fun TensorStructure<Double>.plus(other: TensorStructure<Double>): DoubleTensor {
        checkShapesCompatible(tensor, other.tensor)
        val resBuffer = DoubleArray(tensor.numElements) { i ->
            tensor.buffer.array()[i] + other.tensor.buffer.array()[i]
        }
        return DoubleTensor(tensor.shape, resBuffer)
    }

    override fun TensorStructure<Double>.plusAssign(value: Double) {
        for (i in 0 until tensor.numElements) {
            tensor.buffer.array()[tensor.bufferStart + i] += value
        }
    }

    override fun TensorStructure<Double>.plusAssign(other: TensorStructure<Double>) {
        checkShapesCompatible(tensor, other.tensor)
        for (i in 0 until tensor.numElements) {
            tensor.buffer.array()[tensor.bufferStart + i] +=
                other.tensor.buffer.array()[tensor.bufferStart + i]
        }
    }

    override fun Double.minus(other: TensorStructure<Double>): DoubleTensor {
        val resBuffer = DoubleArray(other.tensor.numElements) { i ->
            this - other.tensor.buffer.array()[other.tensor.bufferStart + i]
        }
        return DoubleTensor(other.shape, resBuffer)
    }

    override fun TensorStructure<Double>.minus(value: Double): DoubleTensor {
        val resBuffer = DoubleArray(tensor.numElements) { i ->
            tensor.buffer.array()[tensor.bufferStart + i] - value
        }
        return DoubleTensor(tensor.shape, resBuffer)
    }

    override fun TensorStructure<Double>.minus(other: TensorStructure<Double>): DoubleTensor {
        checkShapesCompatible(tensor, other)
        val resBuffer = DoubleArray(tensor.numElements) { i ->
            tensor.buffer.array()[i] - other.tensor.buffer.array()[i]
        }
        return DoubleTensor(tensor.shape, resBuffer)
    }

    override fun TensorStructure<Double>.minusAssign(value: Double) {
        for (i in 0 until tensor.numElements) {
            tensor.buffer.array()[tensor.bufferStart + i] -= value
        }
    }

    override fun TensorStructure<Double>.minusAssign(other: TensorStructure<Double>) {
        checkShapesCompatible(tensor, other)
        for (i in 0 until tensor.numElements) {
            tensor.buffer.array()[tensor.bufferStart + i] -=
                other.tensor.buffer.array()[tensor.bufferStart + i]
        }
    }

    override fun Double.times(other: TensorStructure<Double>): DoubleTensor {
        val resBuffer = DoubleArray(other.tensor.numElements) { i ->
            other.tensor.buffer.array()[other.tensor.bufferStart + i] * this
        }
        return DoubleTensor(other.shape, resBuffer)
    }

    override fun TensorStructure<Double>.times(value: Double): DoubleTensor = value * tensor

    override fun TensorStructure<Double>.times(other: TensorStructure<Double>): DoubleTensor {
        checkShapesCompatible(tensor, other)
        val resBuffer = DoubleArray(tensor.numElements) { i ->
            tensor.buffer.array()[tensor.bufferStart + i] *
                    other.tensor.buffer.array()[other.tensor.bufferStart + i]
        }
        return DoubleTensor(tensor.shape, resBuffer)
    }

    override fun TensorStructure<Double>.timesAssign(value: Double) {
        for (i in 0 until tensor.numElements) {
            tensor.buffer.array()[tensor.bufferStart + i] *= value
        }
    }

    override fun TensorStructure<Double>.timesAssign(other: TensorStructure<Double>) {
        checkShapesCompatible(tensor, other)
        for (i in 0 until tensor.numElements) {
            tensor.buffer.array()[tensor.bufferStart + i] *=
                other.tensor.buffer.array()[tensor.bufferStart + i]
        }
    }

    override fun TensorStructure<Double>.div(value: Double): DoubleTensor {
        val resBuffer = DoubleArray(tensor.numElements) { i ->
            tensor.buffer.array()[tensor.bufferStart + i] / value
        }
        return DoubleTensor(tensor.shape, resBuffer)
    }

    override fun TensorStructure<Double>.div(other: TensorStructure<Double>): DoubleTensor {
        checkShapesCompatible(tensor, other)
        val resBuffer = DoubleArray(tensor.numElements) { i ->
            tensor.buffer.array()[other.tensor.bufferStart + i] /
                    other.tensor.buffer.array()[other.tensor.bufferStart + i]
        }
        return DoubleTensor(tensor.shape, resBuffer)
    }

    override fun TensorStructure<Double>.divAssign(value: Double) {
        for (i in 0 until tensor.numElements) {
            tensor.buffer.array()[tensor.bufferStart + i] /= value
        }
    }

    override fun TensorStructure<Double>.divAssign(other: TensorStructure<Double>) {
        checkShapesCompatible(tensor, other)
        for (i in 0 until tensor.numElements) {
            tensor.buffer.array()[tensor.bufferStart + i] /=
                other.tensor.buffer.array()[tensor.bufferStart + i]
        }
    }

    override fun TensorStructure<Double>.unaryMinus(): DoubleTensor {
        val resBuffer = DoubleArray(tensor.numElements) { i ->
            tensor.buffer.array()[tensor.bufferStart + i].unaryMinus()
        }
        return DoubleTensor(tensor.shape, resBuffer)
    }

    override fun TensorStructure<Double>.transpose(i: Int, j: Int): DoubleTensor {
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
            resTensor.buffer.array()[linearIndex] =
                tensor.buffer.array()[tensor.bufferStart + offset]
        }
        return resTensor
    }


    override fun TensorStructure<Double>.view(shape: IntArray): DoubleTensor {
        checkView(tensor, shape)
        return DoubleTensor(shape, tensor.buffer.array(), tensor.bufferStart)
    }

    override fun TensorStructure<Double>.viewAs(other: TensorStructure<Double>): DoubleTensor {
        return tensor.view(other.shape)
    }

    override infix fun TensorStructure<Double>.dot(other: TensorStructure<Double>): DoubleTensor {
        if (tensor.shape.size == 1 && other.shape.size == 1) {
            return DoubleTensor(intArrayOf(1), doubleArrayOf(tensor.times(other).tensor.buffer.array().sum()))
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
        if (m1 != m2) {
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

    override fun diagonalEmbedding(diagonalEntries: TensorStructure<Double>, offset: Int, dim1: Int, dim2: Int):
            DoubleTensor {
        val n = diagonalEntries.shape.size
        val d1 = minusIndexFrom(n + 1, dim1)
        val d2 = minusIndexFrom(n + 1, dim2)

        if (d1 == d2) {
            throw RuntimeException("Diagonal dimensions cannot be identical $d1, $d2")
        }
        if (d1 > n || d2 > n) {
            throw RuntimeException("Dimension out of range")
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


    public fun TensorStructure<Double>.map(transform: (Double) -> Double): DoubleTensor {
        return DoubleTensor(
            tensor.shape,
            tensor.buffer.array().map { transform(it) }.toDoubleArray(),
            tensor.bufferStart
        )
    }

    public fun TensorStructure<Double>.eq(other: TensorStructure<Double>, delta: Double): Boolean {
        return tensor.eq(other) { x, y -> abs(x - y) < delta }
    }

    public infix fun TensorStructure<Double>.eq(other: TensorStructure<Double>): Boolean = tensor.eq(other, 1e-5)

    private fun TensorStructure<Double>.eq(
        other: TensorStructure<Double>,
        eqFunction: (Double, Double) -> Boolean
    ): Boolean {
        checkShapesCompatible(tensor, other)
        val n = tensor.numElements
        if (n != other.tensor.numElements) {
            return false
        }
        for (i in 0 until n) {
            if (!eqFunction(tensor.buffer[tensor.bufferStart + i], other.tensor.buffer[other.tensor.bufferStart + i])) {
                return false
            }
        }
        return true
    }

    public fun randNormal(shape: IntArray, seed: Long = 0): DoubleTensor =
        DoubleTensor(shape, getRandomNormals(shape.reduce(Int::times), seed))

    public fun TensorStructure<Double>.randNormalLike(seed: Long = 0): DoubleTensor =
        DoubleTensor(tensor.shape, getRandomNormals(tensor.shape.reduce(Int::times), seed))

}


public inline fun <R> DoubleTensorAlgebra(block: DoubleTensorAlgebra.() -> R): R =
    DoubleTensorAlgebra().block()
