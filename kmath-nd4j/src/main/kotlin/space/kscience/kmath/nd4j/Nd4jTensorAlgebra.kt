/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package space.kscience.kmath.nd4j

import org.nd4j.linalg.api.ndarray.INDArray
import org.nd4j.linalg.api.ops.impl.summarystats.Variance
import org.nd4j.linalg.api.ops.impl.transforms.strict.ACosh
import org.nd4j.linalg.api.ops.impl.transforms.strict.ASinh
import org.nd4j.linalg.factory.Nd4j
import org.nd4j.linalg.factory.ops.NDBase
import org.nd4j.linalg.ops.transforms.Transforms
import space.kscience.kmath.misc.PerformancePitfall
import space.kscience.kmath.nd.Shape
import space.kscience.kmath.nd.StructureND
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

    override fun structureND(shape: Shape, initializer: A.(IntArray) -> T): Nd4jArrayStructure<T> {
        val array =
    }

    override fun StructureND<T>.map(transform: A.(T) -> T): Nd4jArrayStructure<T> {
        TODO("Not yet implemented")
    }

    override fun StructureND<T>.mapIndexed(transform: A.(index: IntArray, T) -> T): Nd4jArrayStructure<T> {
        TODO("Not yet implemented")
    }

    override fun zip(left: StructureND<T>, right: StructureND<T>, transform: A.(T, T) -> T): Nd4jArrayStructure<T> {
        TODO("Not yet implemented")
    }

    /**
     * Wraps [INDArray] to [Nd4jArrayStructure].
     */
    public fun INDArray.wrap(): Nd4jArrayStructure<T>

    /**
     * Unwraps to or gets [INDArray] from [StructureND].
     */
    public val StructureND<T>.ndArray: INDArray

    override fun T.plus(other: StructureND<T>): Nd4jArrayStructure<T> = other.ndArray.add(this).wrap()
    override fun StructureND<T>.plus(value: T): Nd4jArrayStructure<T> = ndArray.add(value).wrap()

    override fun StructureND<T>.plus(other: StructureND<T>): Nd4jArrayStructure<T> = ndArray.add(other.ndArray).wrap()

    override fun Tensor<T>.plusAssign(value: T) {
        ndArray.addi(value)
    }

    override fun Tensor<T>.plusAssign(other: StructureND<T>) {
        ndArray.addi(other.ndArray)
    }

    override fun T.minus(other: StructureND<T>): Nd4jArrayStructure<T> = other.ndArray.rsub(this).wrap()
    override fun StructureND<T>.minus(arg: T): Nd4jArrayStructure<T> = ndArray.sub(arg).wrap()
    override fun StructureND<T>.minus(other: StructureND<T>): Nd4jArrayStructure<T> = ndArray.sub(other.ndArray).wrap()

    override fun Tensor<T>.minusAssign(value: T) {
        ndArray.rsubi(value)
    }

    override fun Tensor<T>.minusAssign(other: StructureND<T>) {
        ndArray.subi(other.ndArray)
    }

    override fun T.times(arg: StructureND<T>): Nd4jArrayStructure<T> = arg.ndArray.mul(this).wrap()

    override fun StructureND<T>.times(arg: T): Nd4jArrayStructure<T> =
        ndArray.mul(arg).wrap()

    override fun StructureND<T>.times(other: StructureND<T>): Nd4jArrayStructure<T> = ndArray.mul(other.ndArray).wrap()

    override fun Tensor<T>.timesAssign(value: T) {
        ndArray.muli(value)
    }

    override fun Tensor<T>.timesAssign(other: StructureND<T>) {
        ndArray.mmuli(other.ndArray)
    }

    override fun StructureND<T>.unaryMinus(): Nd4jArrayStructure<T> = ndArray.neg().wrap()
    override fun Tensor<T>.get(i: Int): Nd4jArrayStructure<T> = ndArray.slice(i.toLong()).wrap()
    override fun Tensor<T>.transpose(i: Int, j: Int): Nd4jArrayStructure<T> = ndArray.swapAxes(i, j).wrap()
    override fun Tensor<T>.dot(other: Tensor<T>): Nd4jArrayStructure<T> = ndArray.mmul(other.ndArray).wrap()

    override fun Tensor<T>.min(dim: Int, keepDim: Boolean): Nd4jArrayStructure<T> =
        ndArray.min(keepDim, dim).wrap()

    override fun Tensor<T>.sum(dim: Int, keepDim: Boolean): Nd4jArrayStructure<T> =
        ndArray.sum(keepDim, dim).wrap()

    override fun Tensor<T>.max(dim: Int, keepDim: Boolean): Nd4jArrayStructure<T> =
        ndArray.max(keepDim, dim).wrap()

    override fun Tensor<T>.view(shape: IntArray): Nd4jArrayStructure<T> = ndArray.reshape(shape).wrap()
    override fun Tensor<T>.viewAs(other: Tensor<T>): Nd4jArrayStructure<T> = view(other.shape)

    override fun Tensor<T>.argMax(dim: Int, keepDim: Boolean): Nd4jArrayStructure<T> =
        ndBase.get().argmax(ndArray, keepDim, dim).wrap()

    override fun Tensor<T>.mean(dim: Int, keepDim: Boolean): Nd4jArrayStructure<T> = ndArray.mean(keepDim, dim).wrap()

    override fun Tensor<T>.exp(): Nd4jArrayStructure<T> = Transforms.exp(ndArray).wrap()
    override fun Tensor<T>.ln(): Nd4jArrayStructure<T> = Transforms.log(ndArray).wrap()
    override fun Tensor<T>.sqrt(): Nd4jArrayStructure<T> = Transforms.sqrt(ndArray).wrap()
    override fun Tensor<T>.cos(): Nd4jArrayStructure<T> = Transforms.cos(ndArray).wrap()
    override fun Tensor<T>.acos(): Nd4jArrayStructure<T> = Transforms.acos(ndArray).wrap()
    override fun Tensor<T>.cosh(): Nd4jArrayStructure<T> = Transforms.cosh(ndArray).wrap()

    override fun Tensor<T>.acosh(): Nd4jArrayStructure<T> =
        Nd4j.getExecutioner().exec(ACosh(ndArray, ndArray.ulike())).wrap()

    override fun Tensor<T>.sin(): Nd4jArrayStructure<T> = Transforms.sin(ndArray).wrap()
    override fun Tensor<T>.asin(): Nd4jArrayStructure<T> = Transforms.asin(ndArray).wrap()
    override fun Tensor<T>.sinh(): Tensor<T> = Transforms.sinh(ndArray).wrap()

    override fun Tensor<T>.asinh(): Nd4jArrayStructure<T> =
        Nd4j.getExecutioner().exec(ASinh(ndArray, ndArray.ulike())).wrap()

    override fun Tensor<T>.tan(): Nd4jArrayStructure<T> = Transforms.tan(ndArray).wrap()
    override fun Tensor<T>.atan(): Nd4jArrayStructure<T> = Transforms.atan(ndArray).wrap()
    override fun Tensor<T>.tanh(): Nd4jArrayStructure<T> = Transforms.tanh(ndArray).wrap()
    override fun Tensor<T>.atanh(): Nd4jArrayStructure<T> = Transforms.atanh(ndArray).wrap()
    override fun Tensor<T>.ceil(): Nd4jArrayStructure<T> = Transforms.ceil(ndArray).wrap()
    override fun Tensor<T>.floor(): Nd4jArrayStructure<T> = Transforms.floor(ndArray).wrap()
    override fun Tensor<T>.std(dim: Int, keepDim: Boolean): Nd4jArrayStructure<T> =
        ndArray.std(true, keepDim, dim).wrap()

    override fun T.div(arg: StructureND<T>): Nd4jArrayStructure<T> = arg.ndArray.rdiv(this).wrap()
    override fun StructureND<T>.div(arg: T): Nd4jArrayStructure<T> = ndArray.div(arg).wrap()
    override fun StructureND<T>.div(other: StructureND<T>): Nd4jArrayStructure<T> = ndArray.div(other.ndArray).wrap()

    override fun Tensor<T>.divAssign(value: T) {
        ndArray.divi(value)
    }

    override fun Tensor<T>.divAssign(other: StructureND<T>) {
        ndArray.divi(other.ndArray)
    }

    override fun Tensor<T>.variance(dim: Int, keepDim: Boolean): Nd4jArrayStructure<T> =
        Nd4j.getExecutioner().exec(Variance(ndArray, true, true, dim)).wrap()

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

    @OptIn(PerformancePitfall::class)
    override val StructureND<Double>.ndArray: INDArray
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
    ): Tensor<Double> = DoubleTensorAlgebra.diagonalEmbedding(diagonalEntries, offset, dim1, dim2)

    override fun Tensor<Double>.sum(): Double = ndArray.sumNumber().toDouble()
    override fun Tensor<Double>.min(): Double = ndArray.minNumber().toDouble()
    override fun Tensor<Double>.max(): Double = ndArray.maxNumber().toDouble()
    override fun Tensor<Double>.mean(): Double = ndArray.meanNumber().toDouble()
    override fun Tensor<Double>.std(): Double = ndArray.stdNumber().toDouble()
    override fun Tensor<Double>.variance(): Double = ndArray.varNumber().toDouble()
}
