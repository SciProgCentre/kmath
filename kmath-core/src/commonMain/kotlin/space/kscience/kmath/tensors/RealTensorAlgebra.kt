package space.kscience.kmath.tensors

import space.kscience.kmath.linear.LupDecompositionFeature
import space.kscience.kmath.nd.MutableNDBuffer
import space.kscience.kmath.structures.RealBuffer
import space.kscience.kmath.structures.array


public class RealTensor(
    override val shape: IntArray,
    buffer: DoubleArray
) :
    TensorStructure<Double>,
    MutableNDBuffer<Double>(
        TensorStrides(shape),
        RealBuffer(buffer)
    ) {
    override fun item(): Double {
        check(buffer.size > 0) { "The tensor is empty" }
        return buffer[0]
    }
}


public class RealTensorAlgebra : TensorPartialDivisionAlgebra<Double, RealTensor> {

    override fun Double.plus(other: RealTensor): RealTensor {
        val n = other.buffer.size
        val arr = other.buffer.array
        val res = DoubleArray(n)
        for (i in 1..n)
            res[i - 1] = arr[i - 1] + this
        return RealTensor(other.shape, res)
    }

    override fun RealTensor.plus(value: Double): RealTensor {
        TODO("Not yet implemented")
    }

    override fun RealTensor.plus(other: RealTensor): RealTensor {
        TODO("Not yet implemented")
    }

    override fun RealTensor.plusAssign(value: Double) {
        TODO("Not yet implemented")
    }

    override fun RealTensor.plusAssign(other: RealTensor) {
        TODO("Not yet implemented")
    }

    override fun Double.minus(other: RealTensor): RealTensor {
        TODO("Not yet implemented")
    }

    override fun RealTensor.minus(value: Double): RealTensor {
        TODO("Not yet implemented")
    }

    override fun RealTensor.minus(other: RealTensor): RealTensor {
        TODO("Not yet implemented")
    }

    override fun RealTensor.minusAssign(value: Double) {
        TODO("Not yet implemented")
    }

    override fun RealTensor.minusAssign(other: RealTensor) {
        TODO("Not yet implemented")
    }

    override fun Double.times(other: RealTensor): RealTensor {
        TODO("Not yet implemented")
    }

    override fun RealTensor.times(value: Double): RealTensor {
        TODO("Not yet implemented")
    }

    override fun RealTensor.times(other: RealTensor): RealTensor {
        TODO("Not yet implemented")
    }

    override fun RealTensor.timesAssign(value: Double) {
        TODO("Not yet implemented")
    }

    override fun RealTensor.timesAssign(other: RealTensor) {
        TODO("Not yet implemented")
    }

    override fun RealTensor.unaryMinus(): RealTensor {
        TODO("Not yet implemented")
    }

    override fun RealTensor.dot(other: RealTensor): RealTensor {
        TODO("Not yet implemented")
    }

    override fun RealTensor.dotAssign(other: RealTensor) {
        TODO("Not yet implemented")
    }

    override fun RealTensor.dotRightAssign(other: RealTensor) {
        TODO("Not yet implemented")
    }

    override fun diagonalEmbedding(diagonalEntries: RealTensor, offset: Int, dim1: Int, dim2: Int): RealTensor {
        TODO("Not yet implemented")
    }

    override fun RealTensor.transpose(i: Int, j: Int): RealTensor {
        TODO("Not yet implemented")
    }

    override fun RealTensor.transposeAssign(i: Int, j: Int) {
        TODO("Not yet implemented")
    }

    override fun RealTensor.view(shape: IntArray): RealTensor {
        TODO("Not yet implemented")
    }

    override fun RealTensor.view_as(other: RealTensor): RealTensor {
        TODO("Not yet implemented")
    }

    override fun RealTensor.abs(): RealTensor {
        TODO("Not yet implemented")
    }

    override fun RealTensor.absAssign() {
        TODO("Not yet implemented")
    }

    override fun RealTensor.sum(): RealTensor {
        TODO("Not yet implemented")
    }

    override fun RealTensor.sumAssign() {
        TODO("Not yet implemented")
    }

    override fun RealTensor.div(other: RealTensor): RealTensor {
        TODO("Not yet implemented")
    }

    override fun RealTensor.divAssign(other: RealTensor) {
        TODO("Not yet implemented")
    }

    override fun RealTensor.exp(): RealTensor {
        TODO("Not yet implemented")
    }

    override fun RealTensor.expAssign() {
        TODO("Not yet implemented")
    }

    override fun RealTensor.log(): RealTensor {
        TODO("Not yet implemented")
    }

    override fun RealTensor.logAssign() {
        TODO("Not yet implemented")
    }

    override fun RealTensor.lu(): Pair<RealTensor, RealTensor> {
        /**
         * Main first task for @AndreiKingsley
         * Compare with the implementation of [LupDecomposition]
         * and provide a common API
         */
        TODO("Not yet implemented")
    }

    override fun RealTensor.svd(): Triple<RealTensor, RealTensor, RealTensor> {
        /**
         * Main first task for @AlyaNovikova
         */
        TODO("Not yet implemented")
    }

    override fun RealTensor.symEig(eigenvectors: Boolean): Pair<RealTensor, RealTensor> {
        TODO("Not yet implemented")
    }

}

public inline fun <R> RealTensorAlgebra(block: RealTensorAlgebra.() -> R): R =
    RealTensorAlgebra().block()