package space.kscience.kmath.tensors.core

import space.kscience.kmath.tensors.TensorPartialDivisionAlgebra
import space.kscience.kmath.nd.as2D
import kotlin.math.abs

public open class DoubleTensorAlgebra : TensorPartialDivisionAlgebra<Double, DoubleTensor> {

    override fun DoubleTensor.value(): Double {
        check(this.shape contentEquals intArrayOf(1)) {
            "Inconsistent value for tensor of shape ${shape.toList()}"
        }
        return this.buffer.array()[this.bufferStart]
    }

    public fun fromArray(shape: IntArray, buffer: DoubleArray): DoubleTensor {
        checkEmptyShape(shape)
        checkEmptyDoubleBuffer(buffer)
        checkBufferShapeConsistency(shape, buffer)
        return DoubleTensor(shape, buffer, 0)
    }

    override operator fun DoubleTensor.get(i: Int): DoubleTensor {
        val lastShape = this.shape.drop(1).toIntArray()
        val newShape = if (lastShape.isNotEmpty()) lastShape else intArrayOf(1)
        val newStart = newShape.reduce(Int::times) * i + this.bufferStart
        return DoubleTensor(newShape, this.buffer.array(), newStart)
    }

    public fun full(value: Double, shape: IntArray): DoubleTensor {
        checkEmptyShape(shape)
        val buffer = DoubleArray(shape.reduce(Int::times)) { value }
        return DoubleTensor(shape, buffer)
    }

    public fun DoubleTensor.fullLike(value: Double): DoubleTensor {
        val shape = this.shape
        val buffer = DoubleArray(this.linearStructure.size) { value }
        return DoubleTensor(shape, buffer)
    }

    public fun zeros(shape: IntArray): DoubleTensor = full(0.0, shape)

    public fun DoubleTensor.zeroesLike(): DoubleTensor = this.fullLike(0.0)

    public fun ones(shape: IntArray): DoubleTensor = full(1.0, shape)

    public fun DoubleTensor.onesLike(): DoubleTensor = this.fullLike(1.0)

    public fun eye(n: Int): DoubleTensor {
        val shape = intArrayOf(n, n)
        val buffer = DoubleArray(n * n) { 0.0 }
        val res = DoubleTensor(shape, buffer)
        for (i in 0 until n) {
            res[intArrayOf(i, i)] = 1.0
        }
        return res
    }

    public fun DoubleTensor.copy(): DoubleTensor {
        return DoubleTensor(this.shape, this.buffer.array().copyOf(), this.bufferStart)
    }

    override fun Double.plus(other: DoubleTensor): DoubleTensor {
        val resBuffer = DoubleArray(other.linearStructure.size) { i ->
            other.buffer.array()[other.bufferStart + i] + this
        }
        return DoubleTensor(other.shape, resBuffer)
    }

    override fun DoubleTensor.plus(value: Double): DoubleTensor = value + this

    override fun DoubleTensor.plus(other: DoubleTensor): DoubleTensor {
        checkShapesCompatible(this, other)
        val resBuffer = DoubleArray(this.linearStructure.size) { i ->
            this.buffer.array()[i] + other.buffer.array()[i]
        }
        return DoubleTensor(this.shape, resBuffer)
    }

    override fun DoubleTensor.plusAssign(value: Double) {
        for (i in 0 until this.linearStructure.size) {
            this.buffer.array()[this.bufferStart + i] += value
        }
    }

    override fun DoubleTensor.plusAssign(other: DoubleTensor) {
        checkShapesCompatible(this, other)
        for (i in 0 until this.linearStructure.size) {
            this.buffer.array()[this.bufferStart + i] +=
                other.buffer.array()[this.bufferStart + i]
        }
    }

    override fun Double.minus(other: DoubleTensor): DoubleTensor {
        val resBuffer = DoubleArray(other.linearStructure.size) { i ->
            this - other.buffer.array()[other.bufferStart + i]
        }
        return DoubleTensor(other.shape, resBuffer)
    }

    override fun DoubleTensor.minus(value: Double): DoubleTensor {
        val resBuffer = DoubleArray(this.linearStructure.size) { i ->
            this.buffer.array()[this.bufferStart + i] - value
        }
        return DoubleTensor(this.shape, resBuffer)
    }

    override fun DoubleTensor.minus(other: DoubleTensor): DoubleTensor {
        checkShapesCompatible(this, other)
        val resBuffer = DoubleArray(this.linearStructure.size) { i ->
            this.buffer.array()[i] - other.buffer.array()[i]
        }
        return DoubleTensor(this.shape, resBuffer)
    }

    override fun DoubleTensor.minusAssign(value: Double) {
        for (i in 0 until this.linearStructure.size) {
            this.buffer.array()[this.bufferStart + i] -= value
        }
    }

    override fun DoubleTensor.minusAssign(other: DoubleTensor) {
        checkShapesCompatible(this, other)
        for (i in 0 until this.linearStructure.size) {
            this.buffer.array()[this.bufferStart + i] -=
                other.buffer.array()[this.bufferStart + i]
        }
    }

    override fun Double.times(other: DoubleTensor): DoubleTensor {
        val resBuffer = DoubleArray(other.linearStructure.size) { i ->
            other.buffer.array()[other.bufferStart + i] * this
        }
        return DoubleTensor(other.shape, resBuffer)
    }

    override fun DoubleTensor.times(value: Double): DoubleTensor = value * this

    override fun DoubleTensor.times(other: DoubleTensor): DoubleTensor {
        checkShapesCompatible(this, other)
        val resBuffer = DoubleArray(this.linearStructure.size) { i ->
            this.buffer.array()[this.bufferStart + i] *
                    other.buffer.array()[other.bufferStart + i]
        }
        return DoubleTensor(this.shape, resBuffer)
    }

    override fun DoubleTensor.timesAssign(value: Double) {
        for (i in 0 until this.linearStructure.size) {
            this.buffer.array()[this.bufferStart + i] *= value
        }
    }

    override fun DoubleTensor.timesAssign(other: DoubleTensor) {
        checkShapesCompatible(this, other)
        for (i in 0 until this.linearStructure.size) {
            this.buffer.array()[this.bufferStart + i] *=
                other.buffer.array()[this.bufferStart + i]
        }
    }

    override fun DoubleTensor.div(value: Double): DoubleTensor {
        val resBuffer = DoubleArray(this.linearStructure.size) { i ->
            this.buffer.array()[this.bufferStart + i] / value
        }
        return DoubleTensor(this.shape, resBuffer)
    }

    override fun DoubleTensor.div(other: DoubleTensor): DoubleTensor {
        checkShapesCompatible(this, other)
        val resBuffer = DoubleArray(this.linearStructure.size) { i ->
            this.buffer.array()[other.bufferStart + i] /
                    other.buffer.array()[other.bufferStart + i]
        }
        return DoubleTensor(this.shape, resBuffer)
    }

    override fun DoubleTensor.divAssign(value: Double) {
        for (i in 0 until this.linearStructure.size) {
            this.buffer.array()[this.bufferStart + i] /= value
        }
    }

    override fun DoubleTensor.divAssign(other: DoubleTensor) {
        checkShapesCompatible(this, other)
        for (i in 0 until this.linearStructure.size) {
            this.buffer.array()[this.bufferStart + i] /=
                other.buffer.array()[this.bufferStart + i]
        }
    }

    override fun DoubleTensor.unaryMinus(): DoubleTensor {
        val resBuffer = DoubleArray(this.linearStructure.size) { i ->
            this.buffer.array()[this.bufferStart + i].unaryMinus()
        }
        return DoubleTensor(this.shape, resBuffer)
    }

    override fun DoubleTensor.transpose(i: Int, j: Int): DoubleTensor {
        checkTranspose(this.dimension, i, j)
        val n = this.linearStructure.size
        val resBuffer = DoubleArray(n)

        val resShape = this.shape.copyOf()
        resShape[i] = resShape[j].also { resShape[j] = resShape[i] }

        val resTensor = DoubleTensor(resShape, resBuffer)

        for (offset in 0 until n) {
            val oldMultiIndex = this.linearStructure.index(offset)
            val newMultiIndex = oldMultiIndex.copyOf()
            newMultiIndex[i] = newMultiIndex[j].also { newMultiIndex[j] = newMultiIndex[i] }

            val linearIndex = resTensor.linearStructure.offset(newMultiIndex)
            resTensor.buffer.array()[linearIndex] =
                this.buffer.array()[this.bufferStart + offset]
        }
        return resTensor
    }


    override fun DoubleTensor.view(shape: IntArray): DoubleTensor {
        checkView(this, shape)
        return DoubleTensor(shape, this.buffer.array(), this.bufferStart)
    }

    override fun DoubleTensor.viewAs(other: DoubleTensor): DoubleTensor {
        return this.view(other.shape)
    }

    override infix fun DoubleTensor.dot(other: DoubleTensor): DoubleTensor {
        if (this.shape.size == 1 && other.shape.size == 1) {
            return DoubleTensor(intArrayOf(1), doubleArrayOf(this.times(other).buffer.array().sum()))
        }

        var newThis = this.copy()
        var newOther = other.copy()

        var penultimateDim = false
        var lastDim = false
        if (this.shape.size == 1) {
            penultimateDim = true
            newThis = this.view(intArrayOf(1) + this.shape)
        }
        if (other.shape.size == 1) {
            lastDim = true
            newOther = other.view(other.shape + intArrayOf(1))
        }

        val broadcastTensors = broadcastOuterTensors(newThis, newOther)
        newThis = broadcastTensors[0]
        newOther = broadcastTensors[1]

        val l = newThis.shape[newThis.shape.size - 2]
        val m1 = newThis.shape[newThis.shape.size - 1]
        val m2 = newOther.shape[newOther.shape.size - 2]
        val n = newOther.shape[newOther.shape.size - 1]
        if (m1 != m2) {
            throw RuntimeException("Tensors dot operation dimension mismatch: ($l, $m1) x ($m2, $n)")
        }
        val m = m1

        val resShape = newThis.shape.sliceArray(0..(newThis.shape.size - 2)) + intArrayOf(newOther.shape.last())
        val resSize = resShape.reduce { acc, i -> acc * i }
        val resTensor = DoubleTensor(resShape, DoubleArray(resSize))

        for ((res, ab) in resTensor.matrixSequence().zip(newThis.matrixSequence().zip(newOther.matrixSequence()))) {
            val (a, b) = ab
            dotHelper(a.as2D(), b.as2D(), res.as2D(), l, m, n)
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

    override fun diagonalEmbedding(diagonalEntries: DoubleTensor, offset: Int, dim1: Int, dim2: Int): DoubleTensor {
        val n = diagonalEntries.shape.size
        if (dim1 == dim2) {
            throw RuntimeException("Diagonal dimensions cannot be identical $dim1, $dim2")
        }
        if (dim1 > n || dim2 > n) {
            throw RuntimeException("Dimension out of range")
        }

        var lessDim = dim1
        var greaterDim = dim2
        var realOffset = offset
        if (lessDim > greaterDim) {
            realOffset *= -1
            lessDim = greaterDim.also {greaterDim = lessDim}
        }

        val resShape = diagonalEntries.shape.slice(0 until lessDim).toIntArray() +
                intArrayOf(diagonalEntries.shape[n - 1] + abs(realOffset)) +
                diagonalEntries.shape.slice(lessDim until greaterDim - 1).toIntArray() +
                intArrayOf(diagonalEntries.shape[n - 1] + abs(realOffset)) +
                diagonalEntries.shape.slice(greaterDim - 1 until n - 1).toIntArray()
        val resTensor = zeros(resShape)

        for (i in 0 until diagonalEntries.linearStructure.size) {
            val multiIndex = diagonalEntries.linearStructure.index(i)

            var offset1 = 0
            var offset2 = abs(realOffset)
            if (realOffset < 0) {
                offset1 = offset2.also {offset2 = offset1}
            }
            val diagonalMultiIndex = multiIndex.slice(0 until lessDim).toIntArray() +
                    intArrayOf(multiIndex[n - 1] + offset1) +
                    multiIndex.slice(lessDim until greaterDim - 1).toIntArray() +
                    intArrayOf(multiIndex[n - 1] + offset2) +
                    multiIndex.slice(greaterDim - 1 until n - 1).toIntArray()

            resTensor[diagonalMultiIndex] = diagonalEntries[multiIndex]
        }

        return resTensor
    }


    public fun DoubleTensor.map(transform: (Double) -> Double): DoubleTensor {
        return DoubleTensor(
            this.shape,
            this.buffer.array().map { transform(it) }.toDoubleArray(),
            this.bufferStart
        )
    }

    public fun DoubleTensor.contentEquals(other: DoubleTensor, delta: Double = 1e-5): Boolean {
        return this.contentEquals(other) { x, y -> abs(x - y) < delta }
    }

    public fun DoubleTensor.eq(other: DoubleTensor, delta: Double): Boolean {
        return this.eq(other) { x, y -> abs(x - y) < delta }
    }

    public fun DoubleTensor.eq(other: DoubleTensor): Boolean = this.eq(other, 1e-5)

    public fun DoubleTensor.contentEquals(other: DoubleTensor, eqFunction: (Double, Double) -> Boolean): Boolean =
        this.eq(other, eqFunction)

    private fun DoubleTensor.eq(other: DoubleTensor, eqFunction: (Double, Double) -> Boolean): Boolean {
        checkShapesCompatible(this, other)
        val n = this.linearStructure.size
        if (n != other.linearStructure.size) {
            return false
        }
        for (i in 0 until n) {
            if (!eqFunction(this.buffer[this.bufferStart + i], other.buffer[other.bufferStart + i])) {
                return false
            }
        }
        return true
    }

}


public inline fun <R> DoubleTensorAlgebra(block: DoubleTensorAlgebra.() -> R): R =
    DoubleTensorAlgebra().block()
