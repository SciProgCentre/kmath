/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.nd4j

import org.nd4j.linalg.api.ndarray.INDArray
import org.nd4j.linalg.api.ops.impl.summarystats.Variance
import org.nd4j.linalg.api.ops.impl.transforms.strict.ACosh
import org.nd4j.linalg.api.ops.impl.transforms.strict.ASinh
import org.nd4j.linalg.api.shape.Shape
import org.nd4j.linalg.factory.Nd4j
import org.nd4j.linalg.factory.ops.NDBase
import org.nd4j.linalg.ops.transforms.Transforms
import space.kscience.kmath.misc.PerformancePitfall
import space.kscience.kmath.nd.DefaultStrides
import space.kscience.kmath.nd.Shape
import space.kscience.kmath.nd.StructureND
import space.kscience.kmath.operations.DoubleField
import space.kscience.kmath.operations.Field
import space.kscience.kmath.samplers.GaussianSampler
import space.kscience.kmath.stat.RandomGenerator
import space.kscience.kmath.structures.toDoubleArray
import space.kscience.kmath.tensors.api.AnalyticTensorAlgebra
import space.kscience.kmath.tensors.api.Tensor
import space.kscience.kmath.tensors.api.TensorAlgebra
import space.kscience.kmath.tensors.core.DoubleTensor
import kotlin.math.abs

/**
 * ND4J based [TensorAlgebra] implementation.
 */
public sealed interface Nd4jTensorAlgebra<T : Number, A : Field<T>> : AnalyticTensorAlgebra<T, A> {

    /**
     * Wraps [INDArray] to [Nd4jArrayStructure].
     */
    public fun INDArray.wrap(): Nd4jArrayStructure<T>

    /**
     * Unwraps to or gets [INDArray] from [StructureND].
     */
    public val StructureND<T>.ndArray: INDArray

    override fun structureND(shape: Shape, initializer: A.(IntArray) -> T): Nd4jArrayStructure<T>

    override fun StructureND<T>.map(transform: A.(T) -> T): Nd4jArrayStructure<T> =
        structureND(shape) { index -> elementAlgebra.transform(get(index)) }

    override fun StructureND<T>.mapIndexed(transform: A.(index: IntArray, T) -> T): Nd4jArrayStructure<T> =
        structureND(shape) { index -> elementAlgebra.transform(index, get(index)) }

    override fun zip(left: StructureND<T>, right: StructureND<T>, transform: A.(T, T) -> T): Nd4jArrayStructure<T> {
        require(left.shape.contentEquals(right.shape))
        return structureND(left.shape) { index -> elementAlgebra.transform(left[index], right[index]) }
    }

    override fun T.plus(arg: StructureND<T>): Nd4jArrayStructure<T> = arg.ndArray.add(this).wrap()
    override fun StructureND<T>.plus(arg: T): Nd4jArrayStructure<T> = ndArray.add(arg).wrap()

    override fun StructureND<T>.plus(arg: StructureND<T>): Nd4jArrayStructure<T> = ndArray.add(arg.ndArray).wrap()

    override fun Tensor<T>.plusAssign(value: T) {
        ndArray.addi(value)
    }

    override fun Tensor<T>.plusAssign(arg: StructureND<T>) {
        ndArray.addi(arg.ndArray)
    }

    override fun T.minus(arg: StructureND<T>): Nd4jArrayStructure<T> = arg.ndArray.rsub(this).wrap()
    override fun StructureND<T>.minus(arg: T): Nd4jArrayStructure<T> = ndArray.sub(arg).wrap()
    override fun StructureND<T>.minus(arg: StructureND<T>): Nd4jArrayStructure<T> = ndArray.sub(arg.ndArray).wrap()

    override fun Tensor<T>.minusAssign(value: T) {
        ndArray.rsubi(value)
    }

    override fun Tensor<T>.minusAssign(arg: StructureND<T>) {
        ndArray.subi(arg.ndArray)
    }

    override fun T.times(arg: StructureND<T>): Nd4jArrayStructure<T> = arg.ndArray.mul(this).wrap()

    override fun StructureND<T>.times(arg: T): Nd4jArrayStructure<T> =
        ndArray.mul(arg).wrap()

    override fun StructureND<T>.times(arg: StructureND<T>): Nd4jArrayStructure<T> = ndArray.mul(arg.ndArray).wrap()

    override fun Tensor<T>.timesAssign(value: T) {
        ndArray.muli(value)
    }

    override fun Tensor<T>.timesAssign(arg: StructureND<T>) {
        ndArray.mmuli(arg.ndArray)
    }

    override fun StructureND<T>.unaryMinus(): Nd4jArrayStructure<T> = ndArray.neg().wrap()
    override fun Tensor<T>.get(i: Int): Nd4jArrayStructure<T> = ndArray.slice(i.toLong()).wrap()
    override fun Tensor<T>.transpose(i: Int, j: Int): Nd4jArrayStructure<T> = ndArray.swapAxes(i, j).wrap()
    override fun StructureND<T>.dot(other: StructureND<T>): Nd4jArrayStructure<T> = ndArray.mmul(other.ndArray).wrap()

    override fun StructureND<T>.min(dim: Int, keepDim: Boolean): Nd4jArrayStructure<T> =
        ndArray.min(keepDim, dim).wrap()

    override fun StructureND<T>.sum(dim: Int, keepDim: Boolean): Nd4jArrayStructure<T> =
        ndArray.sum(keepDim, dim).wrap()

    override fun StructureND<T>.max(dim: Int, keepDim: Boolean): Nd4jArrayStructure<T> =
        ndArray.max(keepDim, dim).wrap()

    override fun Tensor<T>.view(shape: IntArray): Nd4jArrayStructure<T> = ndArray.reshape(shape).wrap()
    override fun Tensor<T>.viewAs(other: StructureND<T>): Nd4jArrayStructure<T> = view(other.shape)

    override fun StructureND<T>.argMax(dim: Int, keepDim: Boolean): Tensor<Int> =
        ndBase.get().argmax(ndArray, keepDim, dim).asIntStructure()

    override fun StructureND<T>.mean(dim: Int, keepDim: Boolean): Nd4jArrayStructure<T> =
        ndArray.mean(keepDim, dim).wrap()

    override fun StructureND<T>.exp(): Nd4jArrayStructure<T> = Transforms.exp(ndArray).wrap()
    override fun StructureND<T>.ln(): Nd4jArrayStructure<T> = Transforms.log(ndArray).wrap()
    override fun StructureND<T>.sqrt(): Nd4jArrayStructure<T> = Transforms.sqrt(ndArray).wrap()
    override fun StructureND<T>.cos(): Nd4jArrayStructure<T> = Transforms.cos(ndArray).wrap()
    override fun StructureND<T>.acos(): Nd4jArrayStructure<T> = Transforms.acos(ndArray).wrap()
    override fun StructureND<T>.cosh(): Nd4jArrayStructure<T> = Transforms.cosh(ndArray).wrap()

    override fun StructureND<T>.acosh(): Nd4jArrayStructure<T> =
        Nd4j.getExecutioner().exec(ACosh(ndArray, ndArray.ulike())).wrap()

    override fun StructureND<T>.sin(): Nd4jArrayStructure<T> = Transforms.sin(ndArray).wrap()
    override fun StructureND<T>.asin(): Nd4jArrayStructure<T> = Transforms.asin(ndArray).wrap()
    override fun StructureND<T>.sinh(): Tensor<T> = Transforms.sinh(ndArray).wrap()

    override fun StructureND<T>.asinh(): Nd4jArrayStructure<T> =
        Nd4j.getExecutioner().exec(ASinh(ndArray, ndArray.ulike())).wrap()

    override fun StructureND<T>.tan(): Nd4jArrayStructure<T> = Transforms.tan(ndArray).wrap()
    override fun StructureND<T>.atan(): Nd4jArrayStructure<T> = Transforms.atan(ndArray).wrap()
    override fun StructureND<T>.tanh(): Nd4jArrayStructure<T> = Transforms.tanh(ndArray).wrap()
    override fun StructureND<T>.atanh(): Nd4jArrayStructure<T> = Transforms.atanh(ndArray).wrap()
    override fun StructureND<T>.ceil(): Nd4jArrayStructure<T> = Transforms.ceil(ndArray).wrap()
    override fun StructureND<T>.floor(): Nd4jArrayStructure<T> = Transforms.floor(ndArray).wrap()
    override fun StructureND<T>.std(dim: Int, keepDim: Boolean): Nd4jArrayStructure<T> =
        ndArray.std(true, keepDim, dim).wrap()

    override fun T.div(arg: StructureND<T>): Nd4jArrayStructure<T> = arg.ndArray.rdiv(this).wrap()
    override fun StructureND<T>.div(arg: T): Nd4jArrayStructure<T> = ndArray.div(arg).wrap()
    override fun StructureND<T>.div(arg: StructureND<T>): Nd4jArrayStructure<T> = ndArray.div(arg.ndArray).wrap()

    override fun Tensor<T>.divAssign(value: T) {
        ndArray.divi(value)
    }

    override fun Tensor<T>.divAssign(arg: StructureND<T>) {
        ndArray.divi(arg.ndArray)
    }

    override fun StructureND<T>.variance(dim: Int, keepDim: Boolean): Nd4jArrayStructure<T> =
        Nd4j.getExecutioner().exec(Variance(ndArray, true, true, dim)).wrap()

    private companion object {
        private val ndBase = NDBase()
    }


private fun minusIndexFrom(n: Int, i: Int): Int = if (i >= 0) i else {
    val ii = n + i
    check(ii >= 0) { "Out of bound index $i for tensor of dim $n" }
    ii
}

private fun getRandomNormals(n: Int, seed: Long): DoubleArray {
    val distribution = GaussianSampler(0.0, 1.0)
    val generator = RandomGenerator.default(seed)
    return distribution.sample(generator).nextBufferBlocking(n).toDoubleArray()
}


/**
 * [Double] specialization of [Nd4jTensorAlgebra].
 */
public object DoubleNd4jTensorAlgebra : Nd4jTensorAlgebra<Double, DoubleField> {

    override val elementAlgebra: DoubleField get() = DoubleField

    override fun INDArray.wrap(): Nd4jArrayStructure<Double> = asDoubleStructure()

    override fun structureND(shape: Shape, initializer: DoubleField.(IntArray) -> Double): Nd4jArrayStructure<Double> {
        val array: INDArray = Nd4j.zeros(*shape)
        val indices = DefaultStrides(shape)
        indices.asSequence().forEach { index ->
            array.putScalar(index, elementAlgebra.initializer(index))
        }
        return array.wrap()
    }


    @OptIn(PerformancePitfall::class)
    public override val StructureND<Double>.ndArray: INDArray
        get() = when (this) {
            is Nd4jArrayStructure<Double> -> ndArray
            else -> Nd4j.zeros(*shape).also {
                elements().forEach { (idx, value) -> it.putScalar(idx, value) }
            }
        }

    override fun StructureND<Double>.valueOrNull(): Double? =
        if (shape contentEquals intArrayOf(1)) ndArray.getDouble(0) else null

    // TODO rewrite
    override fun diagonalEmbedding(
        diagonalEntries: Tensor<Double>,
        offset: Int,
        dim1: Int,
        dim2: Int,
    ): Tensor<Double> {
        val diagonalEntriesNDArray = diagonalEntries.ndArray
        val n = diagonalEntries.shape.size
        val d1 = minusIndexFrom(n + 1, dim1)
        val d2 = minusIndexFrom(n + 1, dim2)
        check(d1 != d2) { "Diagonal dimensions cannot be identical $d1, $d2" }
        check(d1 <= n && d2 <= n) { "Dimension out of range" }
        var lessDim = d1
        var greaterDim = d2
        var realOffset = offset

        if (lessDim > greaterDim) {
            realOffset *= -1
            lessDim = greaterDim.also { greaterDim = lessDim }
        }

        val resShape = diagonalEntries.shape.sliceArray(0 until lessDim) +
                intArrayOf(diagonalEntries.shape[n - 1] + abs(realOffset)) +
                diagonalEntries.shape.sliceArray(lessDim until greaterDim - 1) +
                intArrayOf(diagonalEntries.shape[n - 1] + abs(realOffset)) +
                diagonalEntries.shape.sliceArray(greaterDim - 1 until n - 1)
        val resTensor = Nd4j.zeros(*resShape).wrap()

        for (i in 0 until diagonalEntriesNDArray.length()) {
            val multiIndex = (if (diagonalEntriesNDArray.ordering() == 'c')
                Shape.ind2subC(diagonalEntriesNDArray, i)
            else
                Shape.ind2sub(diagonalEntriesNDArray, i)).toIntArray()

            var offset1 = 0
            var offset2 = abs(realOffset)
            if (realOffset < 0) offset1 = offset2.also { offset2 = offset1 }

            val diagonalMultiIndex = multiIndex.sliceArray(0 until lessDim) +
                    intArrayOf(multiIndex[n - 1] + offset1) +
                    multiIndex.sliceArray(lessDim until greaterDim - 1) +
                    intArrayOf(multiIndex[n - 1] + offset2) +
                    multiIndex.sliceArray(greaterDim - 1 until n - 1)

            resTensor[diagonalMultiIndex] = diagonalEntries[multiIndex]
        }

    override fun StructureND<Double>.sum(): Double = ndArray.sumNumber().toDouble()
    override fun StructureND<Double>.min(): Double = ndArray.minNumber().toDouble()
    override fun StructureND<Double>.max(): Double = ndArray.maxNumber().toDouble()
    override fun StructureND<Double>.mean(): Double = ndArray.meanNumber().toDouble()
    override fun StructureND<Double>.std(): Double = ndArray.stdNumber().toDouble()
    override fun StructureND<Double>.variance(): Double = ndArray.varNumber().toDouble()
        return resTensor
    }

    /**
     * Compares element-wise two tensors with a specified precision.
     *
     * @param other the tensor to compare with `input` tensor.
     * @param epsilon permissible error when comparing two Double values.
     * @return true if two tensors have the same shape and elements, false otherwise.
     */
    public fun Tensor<Double>.eq(other: Tensor<Double>, epsilon: Double): Boolean =
        ndArray.equalsWithEps(other, epsilon)

    /**
     * Compares element-wise two tensors.
     * Comparison of two Double values occurs with 1e-5 precision.
     *
     * @param other the tensor to compare with `input` tensor.
     * @return true if two tensors have the same shape and elements, false otherwise.
     */
    public infix fun Tensor<Double>.eq(other: Tensor<Double>): Boolean = eq(other, 1e-5)

    public override fun Tensor<Double>.sum(): Double = ndArray.sumNumber().toDouble()
    public override fun Tensor<Double>.min(): Double = ndArray.minNumber().toDouble()
    public override fun Tensor<Double>.max(): Double = ndArray.maxNumber().toDouble()
    public override fun Tensor<Double>.mean(): Double = ndArray.meanNumber().toDouble()
    public override fun Tensor<Double>.std(): Double = ndArray.stdNumber().toDouble()
    public override fun Tensor<Double>.variance(): Double = ndArray.varNumber().toDouble()

    /**
     * Constructs a tensor with the specified shape and data.
     *
     * @param shape the desired shape for the tensor.
     * @param buffer one-dimensional data array.
     * @return tensor with the [shape] shape and [buffer] data.
     */
    public fun fromArray(shape: IntArray, buffer: DoubleArray): Nd4jArrayStructure<Double> =
        Nd4j.create(buffer, shape).wrap()

    /**
     * Constructs a tensor with the specified shape and initializer.
     *
     * @param shape the desired shape for the tensor.
     * @param initializer mapping tensor indices to values.
     * @return tensor with the [shape] shape and data generated by the [initializer].
     */
    public fun produce(shape: IntArray, initializer: (IntArray) -> Double): Nd4jArrayStructure<Double> {
        val struct = Nd4j.create(*shape)!!.wrap()
        struct.indicesIterator().forEach { struct[it] = initializer(it) }
        return struct
    }

    /**
     * Returns a tensor of random numbers drawn from normal distributions with 0.0 mean and 1.0 standard deviation.
     *
     * @param shape the desired shape for the output tensor.
     * @param seed the random seed of the pseudo-random number generator.
     * @return tensor of a given shape filled with numbers from the normal distribution
     * with 0.0 mean and 1.0 standard deviation.
     */
    public fun randomNormal(shape: IntArray, seed: Long = 0): Nd4jArrayStructure<Double> =
        fromArray(shape, getRandomNormals(shape.reduce(Int::times), seed))

    /**
     * Returns a tensor with the same shape as `input` of random numbers drawn from normal distributions
     * with 0.0 mean and 1.0 standard deviation.
     *
     * @param seed the random seed of the pseudo-random number generator.
     * @return tensor with the same shape as `input` filled with numbers from the normal distribution
     * with 0.0 mean and 1.0 standard deviation.
     */
    public fun Tensor<Double>.randomNormalLike(seed: Long = 0): Nd4jArrayStructure<Double> =
        fromArray(shape, getRandomNormals(shape.reduce(Int::times), seed))

    /**
     * Creates a tensor of a given shape and fills all elements with a given value.
     *
     * @param value the value to fill the output tensor with.
     * @param shape array of integers defining the shape of the output tensor.
     * @return tensor with the [shape] shape and filled with [value].
     */
    public fun full(value: Double, shape: IntArray): Nd4jArrayStructure<Double> = Nd4j.valueArrayOf(shape, value).wrap()

    /**
     * Returns a tensor with the same shape as `input` filled with [value].
     *
     * @param value the value to fill the output tensor with.
     * @return tensor with the `input` tensor shape and filled with [value].
     */
    public fun Tensor<Double>.fullLike(value: Double): Nd4jArrayStructure<Double> =
        Nd4j.valueArrayOf(ndArray.shape(), value).wrap()

    /**
     * Returns a tensor filled with the scalar value 0.0, with the shape defined by the variable argument [shape].
     *
     * @param shape array of integers defining the shape of the output tensor.
     * @return tensor filled with the scalar value 0.0, with the [shape] shape.
     */
    public fun zeros(shape: IntArray): Nd4jArrayStructure<Double> = full(0.0, shape)

    /**
     * Returns a tensor filled with the scalar value 0.0, with the same shape as a given array.
     *
     * @return tensor filled with the scalar value 0.0, with the same shape as `input` tensor.
     */
    public fun Tensor<Double>.zeroesLike(): Nd4jArrayStructure<Double> = Nd4j.zerosLike(ndArray).wrap()

    /**
     * Returns a tensor filled with the scalar value 1.0, with the shape defined by the variable argument [shape].
     *
     * @param shape array of integers defining the shape of the output tensor.
     * @return tensor filled with the scalar value 1.0, with the [shape] shape.
     */
    public fun ones(shape: IntArray): Nd4jArrayStructure<Double> = Nd4j.ones(*shape).wrap()

    /**
     * Returns a tensor filled with the scalar value 1.0, with the same shape as a given array.
     *
     * @return tensor filled with the scalar value 1.0, with the same shape as `input` tensor.
     */
    public fun Tensor<Double>.onesLike(): Nd4jArrayStructure<Double> = Nd4j.onesLike(ndArray).wrap()
}
