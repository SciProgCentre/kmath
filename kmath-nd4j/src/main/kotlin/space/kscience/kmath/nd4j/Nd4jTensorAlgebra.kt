/*
 * Copyright 2018-2021 KMath contributors.
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
import space.kscience.kmath.misc.PerformancePitfall
import space.kscience.kmath.nd.StructureND
import space.kscience.kmath.tensors.api.AnalyticTensorAlgebra
import space.kscience.kmath.tensors.api.Tensor
import space.kscience.kmath.tensors.api.TensorAlgebra
import space.kscience.kmath.tensors.core.DoubleTensorAlgebra

/**
 * ND4J based [TensorAlgebra] implementation.
 */
public sealed interface Nd4jTensorAlgebra<T : Number> : AnalyticTensorAlgebra<T> {
    /**
     * Wraps [INDArray] to [Nd4jArrayStructure].
     */
    public fun INDArray.wrap(): Nd4jArrayStructure<T>

    /**
     * Unwraps to or acquires [INDArray] from [StructureND].
     */
    public val StructureND<T>.ndArray: INDArray

    public override fun T.plus(other: Tensor<T>): Tensor<T> = other.ndArray.add(this).wrap()
    public override fun Tensor<T>.plus(value: T): Tensor<T> = ndArray.add(value).wrap()

    public override fun Tensor<T>.plus(other: Tensor<T>): Tensor<T> = ndArray.add(other.ndArray).wrap()

    public override fun Tensor<T>.plusAssign(value: T) {
        ndArray.addi(value)
    }

    public override fun Tensor<T>.plusAssign(other: Tensor<T>) {
        ndArray.addi(other.ndArray)
    }

    public override fun T.minus(other: Tensor<T>): Tensor<T> = other.ndArray.rsub(this).wrap()
    public override fun Tensor<T>.minus(value: T): Tensor<T> = ndArray.sub(value).wrap()
    public override fun Tensor<T>.minus(other: Tensor<T>): Tensor<T> = ndArray.sub(other.ndArray).wrap()

    public override fun Tensor<T>.minusAssign(value: T) {
        ndArray.rsubi(value)
    }

    public override fun Tensor<T>.minusAssign(other: Tensor<T>) {
        ndArray.subi(other.ndArray)
    }

    public override fun T.times(other: Tensor<T>): Tensor<T> = other.ndArray.mul(this).wrap()

    public override fun Tensor<T>.times(value: T): Tensor<T> =
        ndArray.mul(value).wrap()

    public override fun Tensor<T>.times(other: Tensor<T>): Tensor<T> = ndArray.mul(other.ndArray).wrap()

    public override fun Tensor<T>.timesAssign(value: T) {
        ndArray.muli(value)
    }

    public override fun Tensor<T>.timesAssign(other: Tensor<T>) {
        ndArray.mmuli(other.ndArray)
    }

    public override fun Tensor<T>.unaryMinus(): Tensor<T> = ndArray.neg().wrap()
    public override fun Tensor<T>.get(i: Int): Tensor<T> = ndArray.slice(i.toLong()).wrap()
    public override fun Tensor<T>.transpose(i: Int, j: Int): Tensor<T> = ndArray.swapAxes(i, j).wrap()
    public override fun Tensor<T>.dot(other: Tensor<T>): Tensor<T> = ndArray.mmul(other.ndArray).wrap()

    public override fun Tensor<T>.min(dim: Int, keepDim: Boolean): Tensor<T> =
        ndArray.min(keepDim, dim).wrap()

    public override fun Tensor<T>.sum(dim: Int, keepDim: Boolean): Tensor<T> =
        ndArray.sum(keepDim, dim).wrap()

    public override fun Tensor<T>.max(dim: Int, keepDim: Boolean): Tensor<T> =
        ndArray.max(keepDim, dim).wrap()

    public override fun Tensor<T>.view(shape: IntArray): Tensor<T> = ndArray.reshape(shape).wrap()
    public override fun Tensor<T>.viewAs(other: Tensor<T>): Tensor<T> = view(other.shape)

    public fun Tensor<T>.argMax(dim: Int, keepDim: Boolean): Tensor<T> =
        ndBase.get().argmax(ndArray, keepDim, dim).wrap()

    public override fun Tensor<T>.mean(dim: Int, keepDim: Boolean): Tensor<T> = ndArray.mean(keepDim, dim).wrap()

    public override fun Tensor<T>.exp(): Tensor<T> = Transforms.exp(ndArray).wrap()
    public override fun Tensor<T>.ln(): Tensor<T> = Transforms.log(ndArray).wrap()
    public override fun Tensor<T>.sqrt(): Tensor<T> = Transforms.sqrt(ndArray).wrap()
    public override fun Tensor<T>.cos(): Tensor<T> = Transforms.cos(ndArray).wrap()
    public override fun Tensor<T>.acos(): Tensor<T> = Transforms.acos(ndArray).wrap()
    public override fun Tensor<T>.cosh(): Tensor<T> = Transforms.cosh(ndArray).wrap()

    public override fun Tensor<T>.acosh(): Tensor<T> =
        Nd4j.getExecutioner().exec(ACosh(ndArray, ndArray.ulike())).wrap()

    public override fun Tensor<T>.sin(): Tensor<T> = Transforms.sin(ndArray).wrap()
    public override fun Tensor<T>.asin(): Tensor<T> = Transforms.asin(ndArray).wrap()
    public override fun Tensor<T>.sinh(): Tensor<T> = Transforms.sinh(ndArray).wrap()

    public override fun Tensor<T>.asinh(): Tensor<T> =
        Nd4j.getExecutioner().exec(ASinh(ndArray, ndArray.ulike())).wrap()

    public override fun Tensor<T>.tan(): Tensor<T> = Transforms.tan(ndArray).wrap()
    public override fun Tensor<T>.atan(): Tensor<T> = Transforms.atan(ndArray).wrap()
    public override fun Tensor<T>.tanh(): Tensor<T> = Transforms.tanh(ndArray).wrap()
    public override fun Tensor<T>.atanh(): Tensor<T> = Transforms.atanh(ndArray).wrap()
    public override fun Tensor<T>.ceil(): Tensor<T> = Transforms.ceil(ndArray).wrap()
    public override fun Tensor<T>.floor(): Tensor<T> = Transforms.floor(ndArray).wrap()
    public override fun Tensor<T>.std(dim: Int, keepDim: Boolean): Tensor<T> = ndArray.std(true, keepDim, dim).wrap()
    public override fun T.div(other: Tensor<T>): Tensor<T> = other.ndArray.rdiv(this).wrap()
    public override fun Tensor<T>.div(value: T): Tensor<T> = ndArray.div(value).wrap()
    public override fun Tensor<T>.div(other: Tensor<T>): Tensor<T> = ndArray.div(other.ndArray).wrap()

    public override fun Tensor<T>.divAssign(value: T) {
        ndArray.divi(value)
    }

    public override fun Tensor<T>.divAssign(other: Tensor<T>) {
        ndArray.divi(other.ndArray)
    }

    public override fun Tensor<T>.variance(dim: Int, keepDim: Boolean): Tensor<T> =
        Nd4j.getExecutioner().exec(Variance(ndArray, true, true, dim)).wrap()

    private companion object {
        private val ndBase: ThreadLocal<NDBase> = ThreadLocal.withInitial(::NDBase)
    }
}

/**
 * [Double] specialization of [Nd4jTensorAlgebra].
 */
public object DoubleNd4jTensorAlgebra : Nd4jTensorAlgebra<Double> {
    public override fun INDArray.wrap(): Nd4jArrayStructure<Double> = asDoubleStructure()

    @OptIn(PerformancePitfall::class)
    public override val StructureND<Double>.ndArray: INDArray
        get() = when (this) {
            is Nd4jArrayStructure<Double> -> ndArray
            else -> Nd4j.zeros(*shape).also {
                elements().forEach { (idx, value) -> it.putScalar(idx, value) }
            }
        }

    public override fun Tensor<Double>.valueOrNull(): Double? =
        if (shape contentEquals intArrayOf(1)) ndArray.getDouble(0) else null

    // TODO rewrite
    @PerformancePitfall
    public override fun diagonalEmbedding(
        diagonalEntries: Tensor<Double>,
        offset: Int,
        dim1: Int,
        dim2: Int,
    ): Tensor<Double> = DoubleTensorAlgebra.diagonalEmbedding(diagonalEntries, offset, dim1, dim2)

    public override fun Tensor<Double>.sum(): Double = ndArray.sumNumber().toDouble()
    public override fun Tensor<Double>.min(): Double = ndArray.minNumber().toDouble()
    public override fun Tensor<Double>.max(): Double = ndArray.maxNumber().toDouble()
    public override fun Tensor<Double>.mean(): Double = ndArray.meanNumber().toDouble()
    public override fun Tensor<Double>.std(): Double = ndArray.stdNumber().toDouble()
    public override fun Tensor<Double>.variance(): Double = ndArray.varNumber().toDouble()
}
