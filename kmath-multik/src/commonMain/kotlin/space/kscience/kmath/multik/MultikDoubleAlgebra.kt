/*
 * Copyright 2018-2024 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.multik

import org.jetbrains.kotlinx.multik.api.Engine
import org.jetbrains.kotlinx.multik.api.Multik
import org.jetbrains.kotlinx.multik.api.ndarrayOf
import org.jetbrains.kotlinx.multik.ndarray.data.DataType
import space.kscience.kmath.PerformancePitfall
import space.kscience.kmath.nd.StructureND
import space.kscience.kmath.operations.ExponentialOperations
import space.kscience.kmath.operations.Float64Field
import space.kscience.kmath.operations.TrigonometricOperations
import space.kscience.kmath.structures.Float64

public class MultikDoubleAlgebra(
    multikEngine: Engine,
) : MultikDivisionTensorAlgebra<Double, Float64Field>(multikEngine),
    TrigonometricOperations<StructureND<Float64>>, ExponentialOperations<StructureND<Float64>> {
    override val elementAlgebra: Float64Field get() = Float64Field
    override val dataType: DataType get() = DataType.DoubleDataType

    override fun sin(arg: StructureND<Float64>): MultikTensor<Float64> =
        multikMath.mathEx.sin(arg.asMultik().array).wrap()

    override fun cos(arg: StructureND<Float64>): MultikTensor<Float64> =
        multikMath.mathEx.cos(arg.asMultik().array).wrap()

    override fun tan(arg: StructureND<Float64>): MultikTensor<Float64> = sin(arg) / cos(arg)

    @PerformancePitfall
    override fun asin(arg: StructureND<Float64>): MultikTensor<Float64> = arg.map { asin(it) }

    @PerformancePitfall
    override fun acos(arg: StructureND<Float64>): MultikTensor<Float64> = arg.map { acos(it) }

    @PerformancePitfall
    override fun atan(arg: StructureND<Float64>): MultikTensor<Float64> = arg.map { atan(it) }

    override fun exp(arg: StructureND<Float64>): MultikTensor<Float64> =
        multikMath.mathEx.exp(arg.asMultik().array).wrap()

    override fun ln(arg: StructureND<Float64>): MultikTensor<Float64> = multikMath.mathEx.log(arg.asMultik().array).wrap()

    override fun sinh(arg: StructureND<Float64>): MultikTensor<Float64> = (exp(arg) - exp(-arg)) / 2.0

    override fun cosh(arg: StructureND<Float64>): MultikTensor<Float64> = (exp(arg) + exp(-arg)) / 2.0

    override fun tanh(arg: StructureND<Float64>): MultikTensor<Float64> {
        val expPlus = exp(arg)
        val expMinus = exp(-arg)
        return (expPlus - expMinus) / (expPlus + expMinus)
    }

    @PerformancePitfall
    override fun asinh(arg: StructureND<Float64>): MultikTensor<Float64> = arg.map { asinh(it) }

    @PerformancePitfall
    override fun acosh(arg: StructureND<Float64>): MultikTensor<Float64> = arg.map { acosh(it) }

    @PerformancePitfall
    override fun atanh(arg: StructureND<Float64>): MultikTensor<Float64> = arg.map { atanh(it) }

    override fun scalar(value: Double): MultikTensor<Float64> = Multik.ndarrayOf(value).wrap()
}

//public val Double.Companion.multikAlgebra: MultikTensorAlgebra<Double, DoubleField> get() = MultikDoubleAlgebra
//public val DoubleField.multikAlgebra: MultikTensorAlgebra<Double, DoubleField> get() = MultikDoubleAlgebra

