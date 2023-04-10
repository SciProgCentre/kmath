/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.nd4j

import org.nd4j.linalg.api.ndarray.INDArray
import org.nd4j.linalg.api.ops.impl.summarystats.Variance
import org.nd4j.linalg.api.ops.impl.transforms.strict.ACosh
import org.nd4j.linalg.api.ops.impl.transforms.strict.ASinh
import org.nd4j.linalg.factory.Nd4j
import org.nd4j.linalg.factory.ops.NDBase
import org.nd4j.linalg.ops.transforms.Transforms
import space.kscience.kmath.PerformancePitfall
import space.kscience.kmath.UnsafeKMathAPI
import space.kscience.kmath.nd.*
import space.kscience.kmath.operations.DoubleField
import space.kscience.kmath.operations.Field
import space.kscience.kmath.tensors.api.AnalyticTensorAlgebra
import space.kscience.kmath.tensors.api.Tensor
import space.kscience.kmath.tensors.api.TensorAlgebra
import space.kscience.kmath.tensors.core.DoubleTensorAlgebra

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

    override fun structureND(shape: ShapeND, initializer: A.(IntArray) -> T): Nd4jArrayStructure<T>

    @OptIn(PerformancePitfall::class)
    override fun StructureND<T>.map(transform: A.(T) -> T): Nd4jArrayStructure<T> =
        structureND(shape) { index -> elementAlgebra.transform(get(index)) }

    @OptIn(PerformancePitfall::class)
    override fun StructureND<T>.mapIndexed(transform: A.(index: IntArray, T) -> T): Nd4jArrayStructure<T> =
        structureND(shape) { index -> elementAlgebra.transform(index, get(index)) }

    @OptIn(PerformancePitfall::class)
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
    override fun Tensor<T>.getTensor(i: Int): Nd4jArrayStructure<T> = ndArray.slice(i.toLong()).wrap()
    override fun StructureND<T>.transposed(i: Int, j: Int): Nd4jArrayStructure<T> = ndArray.swapAxes(i, j).wrap()
    override fun StructureND<T>.dot(other: StructureND<T>): Nd4jArrayStructure<T> = ndArray.mmul(other.ndArray).wrap()

    override fun StructureND<T>.min(dim: Int, keepDim: Boolean): Nd4jArrayStructure<T> =
        ndArray.min(keepDim, dim).wrap()

    override fun StructureND<T>.sum(dim: Int, keepDim: Boolean): Nd4jArrayStructure<T> =
        ndArray.sum(keepDim, dim).wrap()

    override fun StructureND<T>.max(dim: Int, keepDim: Boolean): Nd4jArrayStructure<T> =
        ndArray.max(keepDim, dim).wrap()

    @OptIn(UnsafeKMathAPI::class)
    override fun Tensor<T>.view(shape: ShapeND): Nd4jArrayStructure<T> = ndArray.reshape(shape.asArray()).wrap()

    override fun Tensor<T>.viewAs(other: StructureND<T>): Nd4jArrayStructure<T> = view(other.shape)

    override fun StructureND<T>.argMin(dim: Int, keepDim: Boolean): Tensor<Int> =
        ndBase.get().argmin(ndArray, keepDim, dim).asIntStructure()

    override fun StructureND<T>.argMax(dim: Int, keepDim: Boolean): Tensor<Int> =
        ndBase.get().argmax(ndArray, keepDim, dim).asIntStructure()

    override fun mean(structureND: StructureND<T>, dim: Int, keepDim: Boolean): Tensor<T> =
        structureND.ndArray.mean(keepDim, dim).wrap()

    override fun exp(arg: StructureND<T>): Nd4jArrayStructure<T> = Transforms.exp(arg.ndArray).wrap()
    override fun ln(arg: StructureND<T>): Nd4jArrayStructure<T> = Transforms.log(arg.ndArray).wrap()
    override fun sqrt(arg: StructureND<T>): Nd4jArrayStructure<T> = Transforms.sqrt(arg.ndArray).wrap()
    override fun cos(arg: StructureND<T>): Nd4jArrayStructure<T> = Transforms.cos(arg.ndArray).wrap()
    override fun acos(arg: StructureND<T>): Nd4jArrayStructure<T> = Transforms.acos(arg.ndArray).wrap()
    override fun cosh(arg: StructureND<T>): Nd4jArrayStructure<T> = Transforms.cosh(arg.ndArray).wrap()

    override fun acosh(arg: StructureND<T>): Nd4jArrayStructure<T> =
        Nd4j.getExecutioner().exec(ACosh(arg.ndArray, arg.ndArray.ulike())).wrap()

    override fun sin(arg: StructureND<T>): Nd4jArrayStructure<T> = Transforms.sin(arg.ndArray).wrap()
    override fun asin(arg: StructureND<T>): Nd4jArrayStructure<T> = Transforms.asin(arg.ndArray).wrap()
    override fun sinh(arg: StructureND<T>): Tensor<T> = Transforms.sinh(arg.ndArray).wrap()

    override fun asinh(arg: StructureND<T>): Nd4jArrayStructure<T> =
        Nd4j.getExecutioner().exec(ASinh(arg.ndArray, arg.ndArray.ulike())).wrap()

    override fun tan(arg: StructureND<T>): Nd4jArrayStructure<T> = Transforms.tan(arg.ndArray).wrap()
    override fun atan(arg: StructureND<T>): Nd4jArrayStructure<T> = Transforms.atan(arg.ndArray).wrap()
    override fun tanh(arg: StructureND<T>): Nd4jArrayStructure<T> = Transforms.tanh(arg.ndArray).wrap()
    override fun atanh(arg: StructureND<T>): Nd4jArrayStructure<T> = Transforms.atanh(arg.ndArray).wrap()
    override fun power(arg: StructureND<T>, pow: Number): StructureND<T> = Transforms.pow(arg.ndArray, pow).wrap()
    override fun ceil(arg: StructureND<T>): Nd4jArrayStructure<T> = Transforms.ceil(arg.ndArray).wrap()
    override fun floor(structureND: StructureND<T>): Nd4jArrayStructure<T> = Transforms.floor(structureND.ndArray).wrap()
    override fun std(structureND: StructureND<T>, dim: Int, keepDim: Boolean): Tensor<T> =
        structureND.ndArray.std(true, keepDim, dim).wrap()

    override fun T.div(arg: StructureND<T>): Nd4jArrayStructure<T> = arg.ndArray.rdiv(this).wrap()
    override fun StructureND<T>.div(arg: T): Nd4jArrayStructure<T> = ndArray.div(arg).wrap()
    override fun StructureND<T>.div(arg: StructureND<T>): Nd4jArrayStructure<T> = ndArray.div(arg.ndArray).wrap()

    override fun Tensor<T>.divAssign(value: T) {
        ndArray.divi(value)
    }

    override fun Tensor<T>.divAssign(arg: StructureND<T>) {
        ndArray.divi(arg.ndArray)
    }

    override fun variance(structureND: StructureND<T>, dim: Int, keepDim: Boolean): Tensor<T> =
        Nd4j.getExecutioner().exec(Variance(structureND.ndArray, true, true, dim)).wrap()

    private companion object {
        private val ndBase: ThreadLocal<NDBase> = ThreadLocal.withInitial(::NDBase)
    }
}

/**
 * [Double] specialization of [Nd4jTensorAlgebra].
 */
public object DoubleNd4jTensorAlgebra : Nd4jTensorAlgebra<Double, DoubleField> {

    override val elementAlgebra: DoubleField get() = DoubleField

    override fun INDArray.wrap(): Nd4jArrayStructure<Double> = asDoubleStructure()

    @OptIn(UnsafeKMathAPI::class)
    override fun structureND(shape: ShapeND, initializer: DoubleField.(IntArray) -> Double): Nd4jArrayStructure<Double> {
        val array: INDArray = Nd4j.zeros(*shape.asArray())
        val indices = ColumnStrides(shape)
        indices.asSequence().forEach { index ->
            array.putScalar(index, elementAlgebra.initializer(index))
        }
        return array.wrap()
    }


    @OptIn(PerformancePitfall::class, UnsafeKMathAPI::class)
    override val StructureND<Double>.ndArray: INDArray
        get() = when (this) {
            is Nd4jArrayStructure<Double> -> ndArray
            else -> Nd4j.zeros(*shape.asArray()).also {
                elements().forEach { (idx, value) -> it.putScalar(idx, value) }
            }
        }

    override fun StructureND<Double>.valueOrNull(): Double? =
        if (shape contentEquals ShapeND(1)) ndArray.getDouble(0) else null

    // TODO rewrite
    override fun diagonalEmbedding(
        diagonalEntries: StructureND<Double>,
        offset: Int,
        dim1: Int,
        dim2: Int,
    ): Tensor<Double> = DoubleTensorAlgebra.diagonalEmbedding(diagonalEntries, offset, dim1, dim2)

    override fun StructureND<Double>.sum(): Double = ndArray.sumNumber().toDouble()
    override fun StructureND<Double>.min(): Double = ndArray.minNumber().toDouble()
    override fun StructureND<Double>.max(): Double = ndArray.maxNumber().toDouble()
    override fun mean(structureND: StructureND<Double>): Double = structureND.ndArray.meanNumber().toDouble()
    override fun std(structureND: StructureND<Double>): Double = structureND.ndArray.stdNumber().toDouble()
    override fun variance(structureND: StructureND<Double>): Double = structureND.ndArray.varNumber().toDouble()
}
