package space.kscience.kmath.tensors

import space.kscience.kmath.structures.array


public open class RealTensorAlgebra : TensorPartialDivisionAlgebra<Double, RealTensor> {

    override fun RealTensor.value(): Double {
        check(this.shape contentEquals intArrayOf(1)) {
            "Inconsistent value for tensor of shape ${shape.toList()}"
        }
        return this.buffer.array[0]
    }

    override fun zeros(shape: IntArray): RealTensor {
        TODO("Not yet implemented")
    }

    override fun RealTensor.zeroesLike(): RealTensor {
        val shape = this.shape
        val buffer = DoubleArray(this.buffer.size) { 0.0 }
        return RealTensor(shape, buffer)
    }

    override fun ones(shape: IntArray): RealTensor {
        TODO("Not yet implemented")
    }

    override fun RealTensor.onesLike(): RealTensor {
        TODO("Not yet implemented")
    }

    override fun RealTensor.copy(): RealTensor {
        // should be rework as soon as copy() method for NDBuffer will be available
        return RealTensor(this.shape, this.buffer.array.copyOf())
    }


    override fun Double.plus(other: RealTensor): RealTensor {
        val resBuffer = DoubleArray(other.buffer.size) { i ->
            other.buffer.array[i] + this
        }
        return RealTensor(other.shape, resBuffer)
    }

    override fun RealTensor.plus(value: Double): RealTensor = value + this

    override fun RealTensor.plus(other: RealTensor): RealTensor {
        val broadcast = broadcastTensors(this, other)
        val newThis = broadcast[0]
        val newOther = broadcast[1]
        val resBuffer = DoubleArray(newThis.buffer.size) { i ->
            newThis.buffer.array[i] + newOther.buffer.array[i]
        }
        return RealTensor(newThis.shape, resBuffer)
    }

    override fun RealTensor.plusAssign(value: Double) {
        for (i in this.buffer.array.indices) {
            this.buffer.array[i] += value
        }
    }

    override fun RealTensor.plusAssign(other: RealTensor) {
        //todo should be change with broadcasting
        for (i in this.buffer.array.indices) {
            this.buffer.array[i] += other.buffer.array[i]
        }
    }

    override fun Double.minus(other: RealTensor): RealTensor {
        val resBuffer = DoubleArray(other.buffer.size) { i ->
            this - other.buffer.array[i]
        }
        return RealTensor(other.shape, resBuffer)
    }

    override fun RealTensor.minus(value: Double): RealTensor {
        val resBuffer = DoubleArray(this.buffer.size) { i ->
            this.buffer.array[i] - value
        }
        return RealTensor(this.shape, resBuffer)
    }

    override fun RealTensor.minus(other: RealTensor): RealTensor {
        val broadcast = broadcastTensors(this, other)
        val newThis = broadcast[0]
        val newOther = broadcast[1]
        val resBuffer = DoubleArray(newThis.buffer.size) { i ->
            newThis.buffer.array[i] - newOther.buffer.array[i]
        }
        return RealTensor(newThis.shape, resBuffer)
    }

    override fun RealTensor.minusAssign(value: Double) {
        for (i in this.buffer.array.indices) {
            this.buffer.array[i] -= value
        }
    }

    override fun RealTensor.minusAssign(other: RealTensor) {
        TODO("Alya")
    }

    override fun Double.times(other: RealTensor): RealTensor {
        //todo should be change with broadcasting
        val resBuffer = DoubleArray(other.buffer.size) { i ->
            other.buffer.array[i] * this
        }
        return RealTensor(other.shape, resBuffer)
    }

    //todo should be change with broadcasting
    override fun RealTensor.times(value: Double): RealTensor = value * this

    override fun RealTensor.times(other: RealTensor): RealTensor {
        //todo should be change with broadcasting
        val resBuffer = DoubleArray(this.buffer.size) { i ->
            this.buffer.array[i] * other.buffer.array[i]
        }
        return RealTensor(this.shape, resBuffer)
    }

    override fun RealTensor.timesAssign(value: Double) {
        //todo should be change with broadcasting
        for (i in this.buffer.array.indices) {
            this.buffer.array[i] *= value
        }
    }

    override fun RealTensor.timesAssign(other: RealTensor) {
        //todo should be change with broadcasting
        for (i in this.buffer.array.indices) {
            this.buffer.array[i] *= other.buffer.array[i]
        }
    }

    override fun RealTensor.unaryMinus(): RealTensor {
        val resBuffer = DoubleArray(this.buffer.size) { i ->
            this.buffer.array[i].unaryMinus()
        }
        return RealTensor(this.shape, resBuffer)
    }

    override fun RealTensor.transpose(i: Int, j: Int): RealTensor {
        checkTranspose(this.dimension, i, j)
        val n = this.buffer.size
        val resBuffer = DoubleArray(n)

        val resShape = this.shape.copyOf()
        resShape[i] = resShape[j].also { resShape[j] = resShape[i] }

        val resTensor = RealTensor(resShape, resBuffer)

        for (offset in 0 until n) {
            val oldMultiIndex = this.strides.index(offset)
            val newMultiIndex = oldMultiIndex.copyOf()
            newMultiIndex[i] = newMultiIndex[j].also { newMultiIndex[j] = newMultiIndex[i] }

            val linearIndex = resTensor.strides.offset(newMultiIndex)
            resTensor.buffer.array[linearIndex] = this.buffer.array[offset]
        }
        return resTensor
    }


    override fun RealTensor.view(shape: IntArray): RealTensor {
        return RealTensor(shape, this.buffer.array)
    }

    override fun RealTensor.viewAs(other: RealTensor): RealTensor {
        return this.view(other.shape)
    }

    override fun RealTensor.abs(): RealTensor {
        TODO("Not yet implemented")
    }

    override fun full(shape: IntArray, value: Double): RealTensor {
        TODO("Not yet implemented")
    }

    override fun RealTensor.fullLike(value: Double): RealTensor {
        TODO("Not yet implemented")
    }


    override fun RealTensor.sum(dim: Int, keepDim: Boolean): RealTensor {
        TODO("Not yet implemented")
    }

    override fun RealTensor.cumsum(dim: Int): RealTensor {
        TODO("Not yet implemented")
    }

    override fun RealTensor.prod(dim: Int, keepDim: Boolean): RealTensor {
        TODO("Not yet implemented")
    }

    override fun RealTensor.cumprod(dim: Int): RealTensor {
        TODO("Not yet implemented")
    }

    override fun RealTensor.max(dim: Int, keepDim: Boolean): RealTensor {
        TODO("Not yet implemented")
    }

    override fun RealTensor.cummax(dim: Int): RealTensor {
        TODO("Not yet implemented")
    }

    override fun RealTensor.min(dim: Int, keepDim: Boolean): RealTensor {
        TODO("Not yet implemented")
    }

    override fun RealTensor.cummin(dim: Int): RealTensor {
        TODO("Not yet implemented")
    }

    override fun RealTensor.median(dim: Int, keepDim: Boolean): RealTensor {
        TODO("Not yet implemented")
    }

    override fun maximum(lhs: RealTensor, rhs: RealTensor) {
        TODO("Not yet implemented")
    }

    override fun minimum(lhs: RealTensor, rhs: RealTensor) {
        TODO("Not yet implemented")
    }

    override fun RealTensor.sort(dim: Int, keepDim: Boolean, descending: Boolean): RealTensor {
        TODO("Not yet implemented")
    }

    override fun cat(tensors: List<RealTensor>, dim: Int): RealTensor {
        TODO("Not yet implemented")
    }


    override fun RealTensor.div(value: Double): RealTensor {
        TODO("Not yet implemented")
    }

    override fun RealTensor.div(other: RealTensor): RealTensor {
        TODO("Not yet implemented")
    }

    override fun RealTensor.flatten(startDim: Int, endDim: Int): RealTensor {
        TODO("Not yet implemented")
    }

    override fun RealTensor.divAssign(value: Double) {
        TODO("Not yet implemented")
    }

    override fun RealTensor.divAssign(other: RealTensor) {
        TODO("Not yet implemented")
    }

    override fun RealTensor.mean(dim: Int, keepDim: Boolean): RealTensor {
        TODO("Not yet implemented")
    }

    override fun RealTensor.quantile(q: Double, dim: Int, keepDim: Boolean): RealTensor {
        TODO("Not yet implemented")
    }

    override fun RealTensor.std(dim: Int, unbiased: Boolean, keepDim: Boolean): RealTensor {
        TODO("Not yet implemented")
    }

    override fun RealTensor.variance(dim: Int, unbiased: Boolean, keepDim: Boolean): RealTensor {
        TODO("Not yet implemented")
    }

    override fun RealTensor.histc(bins: Int, min: Double, max: Double): RealTensor {
        TODO("Not yet implemented")
    }

}

public inline fun <R> RealTensorAlgebra(block: RealTensorAlgebra.() -> R): R =
    RealTensorAlgebra().block()