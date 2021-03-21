package space.kscience.kmath.tensors.core

import space.kscience.kmath.tensors.TensorPartialDivisionAlgebra
import kotlin.math.abs

public open class DoubleTensorAlgebra : TensorPartialDivisionAlgebra<Double, DoubleTensor> {

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

    override fun full(value: Double, shape: IntArray): DoubleTensor {
        checkEmptyShape(shape)
        val buffer = DoubleArray(shape.reduce(Int::times)) { value }
        return DoubleTensor(shape, buffer)
    }

    override fun DoubleTensor.fullLike(value: Double): DoubleTensor {
        val shape = this.shape
        val buffer = DoubleArray(this.linearStructure.size) { value }
        return DoubleTensor(shape, buffer)
    }

    override fun zeros(shape: IntArray): DoubleTensor = full(0.0, shape)

    override fun DoubleTensor.zeroesLike(): DoubleTensor = this.fullLike(0.0)

    override fun ones(shape: IntArray): DoubleTensor = full(1.0, shape)

    override fun DoubleTensor.onesLike(): DoubleTensor = this.fullLike(1.0)

    override fun eye(n: Int): DoubleTensor {
        val shape = intArrayOf(n, n)
        val buffer = DoubleArray(n * n) { 0.0 }
        val res = DoubleTensor(shape, buffer)
        for (i in 0 until n) {
            res[intArrayOf(i, i)] = 1.0
        }
        return res
    }

    override fun DoubleTensor.copy(): DoubleTensor {
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
            this.buffer.array()[other.bufferStart + i] *
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

    override fun DoubleTensor.abs(): DoubleTensor {
        TODO("Not yet implemented")
    }

    override fun DoubleTensor.sum(dim: Int, keepDim: Boolean): DoubleTensor {
        TODO("Not yet implemented")
    }

    override fun DoubleTensor.cumsum(dim: Int): DoubleTensor {
        TODO("Not yet implemented")
    }

    override fun DoubleTensor.prod(dim: Int, keepDim: Boolean): DoubleTensor {
        TODO("Not yet implemented")
    }

    override fun DoubleTensor.cumprod(dim: Int): DoubleTensor {
        TODO("Not yet implemented")
    }

    override fun DoubleTensor.dot(other: DoubleTensor): DoubleTensor {
        TODO("Alya")
    }

    override fun diagonalEmbedding(diagonalEntries: DoubleTensor, offset: Int, dim1: Int, dim2: Int): DoubleTensor {
        TODO("Alya")
    }

    override fun cat(tensors: List<DoubleTensor>, dim: Int): DoubleTensor {
        TODO("Not yet implemented")
    }

    override fun DoubleTensor.flatten(startDim: Int, endDim: Int): DoubleTensor {
        TODO("Not yet implemented")
    }

    override fun DoubleTensor.mean(dim: Int, keepDim: Boolean): DoubleTensor {
        TODO("Not yet implemented")
    }

    override fun DoubleTensor.det(): DoubleTensor {
        TODO("Not yet implemented")
    }

    override fun DoubleTensor.square(): DoubleTensor {
        TODO("Not yet implemented")
    }

    override fun DoubleTensor.variance(dim: Int, unbiased: Boolean, keepDim: Boolean): DoubleTensor {
        TODO("Not yet implemented")
    }

    override fun DoubleTensor.squeeze(dim: Int): DoubleTensor {
        TODO("Not yet implemented")
    }

    override fun DoubleTensor.map(transform: (Double) -> Double): DoubleTensor {
        return DoubleTensor(
            this.shape,
            this.buffer.array().map { transform(it) }.toDoubleArray(),
            this.bufferStart
        )
    }

    public fun DoubleTensor.contentEquals(other: DoubleTensor, delta: Double = 1e-5): Boolean {
        return this.contentEquals(other) { x, y -> abs(x - y) < delta }
    }

    public fun DoubleTensor.eq(other: DoubleTensor, delta: Double = 1e-5): Boolean {
        return this.eq(other) { x, y -> abs(x - y) < delta }
    }

    override fun DoubleTensor.contentEquals(other: DoubleTensor, eqFunction: (Double, Double) -> Boolean): Boolean {
        if (!(this.shape contentEquals other.shape)){
            return false
        }
        return this.eq(other, eqFunction)
    }

    override fun DoubleTensor.eq(other: DoubleTensor, eqFunction: (Double, Double) -> Boolean): Boolean {
        // todo broadcasting checking
        val n = this.strides.linearSize
        if (n != other.strides.linearSize){
            return false
        }
        for (i in 0 until n){
            if (!eqFunction(this.buffer[this.bufferStart + i], other.buffer[other.bufferStart + i])) {
                return false
            }
        }
        return true
    }

}


public inline fun <R> DoubleTensorAlgebra(block: DoubleTensorAlgebra.() -> R): R =
    DoubleTensorAlgebra().block()
