/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.multik

import org.jetbrains.kotlinx.multik.api.Engine
import org.jetbrains.kotlinx.multik.api.Multik
import org.jetbrains.kotlinx.multik.api.ndarrayOf
import org.jetbrains.kotlinx.multik.ndarray.data.DataType
import space.kscience.kmath.PerformancePitfall
import space.kscience.kmath.nd.StructureND
import space.kscience.kmath.operations.DoubleField
import space.kscience.kmath.operations.ExponentialOperations
import space.kscience.kmath.operations.TrigonometricOperations

public class MultikDoubleAlgebra(
    multikEngine: Engine
) : MultikDivisionTensorAlgebra<Double, DoubleField>(multikEngine),
    TrigonometricOperations<StructureND<Double>>, ExponentialOperations<StructureND<Double>> {
    override val elementAlgebra: DoubleField get() = DoubleField
    override val type: DataType get() = DataType.DoubleDataType

    override fun sin(arg: StructureND<Double>): MultikTensor<Double> = multikMath.mathEx.sin(arg.asMultik().array).wrap()

    override fun cos(arg: StructureND<Double>): MultikTensor<Double> = multikMath.mathEx.cos(arg.asMultik().array).wrap()

    override fun tan(arg: StructureND<Double>): MultikTensor<Double> = sin(arg) / cos(arg)

    @PerformancePitfall
    override fun asin(arg: StructureND<Double>): MultikTensor<Double>  = arg.map { asin(it) }

    @PerformancePitfall
    override fun acos(arg: StructureND<Double>): MultikTensor<Double> = arg.map { acos(it) }

    @PerformancePitfall
    override fun atan(arg: StructureND<Double>): MultikTensor<Double> = arg.map { atan(it) }

    override fun exp(arg: StructureND<Double>): MultikTensor<Double> = multikMath.mathEx.exp(arg.asMultik().array).wrap()

    override fun ln(arg: StructureND<Double>): MultikTensor<Double> = multikMath.mathEx.log(arg.asMultik().array).wrap()

    override fun sinh(arg: StructureND<Double>): MultikTensor<Double> = (exp(arg) - exp(-arg)) / 2.0

    override fun cosh(arg: StructureND<Double>): MultikTensor<Double> = (exp(arg) + exp(-arg)) / 2.0

    override fun tanh(arg: StructureND<Double>): MultikTensor<Double> {
        val expPlus = exp(arg)
        val expMinus = exp(-arg)
        return (expPlus - expMinus) / (expPlus + expMinus)
    }

    @PerformancePitfall
    override fun asinh(arg: StructureND<Double>): MultikTensor<Double> = arg.map { asinh(it) }

    @PerformancePitfall
    override fun acosh(arg: StructureND<Double>): MultikTensor<Double> = arg.map { acosh(it) }

    @PerformancePitfall
    override fun atanh(arg: StructureND<Double>): MultikTensor<Double> = arg.map { atanh(it) }

    override fun scalar(value: Double): MultikTensor<Double>  = Multik.ndarrayOf(value).wrap()
}

//public val Double.Companion.multikAlgebra: MultikTensorAlgebra<Double, DoubleField> get() = MultikDoubleAlgebra
//public val DoubleField.multikAlgebra: MultikTensorAlgebra<Double, DoubleField> get() = MultikDoubleAlgebra

