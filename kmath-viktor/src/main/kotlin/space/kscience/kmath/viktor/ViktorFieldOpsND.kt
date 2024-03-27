/*
 * Copyright 2018-2024 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

@file:OptIn(PerformancePitfall::class)

package space.kscience.kmath.viktor

import org.jetbrains.bio.viktor.F64Array
import space.kscience.kmath.PerformancePitfall
import space.kscience.kmath.UnsafeKMathAPI
import space.kscience.kmath.UnstableKMathAPI
import space.kscience.kmath.nd.*
import space.kscience.kmath.operations.ExtendedFieldOps
import space.kscience.kmath.operations.Float64Field
import space.kscience.kmath.operations.NumbersAddOps
import space.kscience.kmath.operations.PowerOperations

@OptIn(PerformancePitfall::class)
public open class ViktorFieldOpsND :
    FieldOpsND<Double, Float64Field>,
    ExtendedFieldOps<StructureND<Double>>,
    PowerOperations<StructureND<Double>> {

    public val StructureND<Double>.f64Buffer: F64Array
        get() = when (this) {
            is ViktorStructureND -> this.f64Buffer
            else -> mutableStructureND(shape) { this@f64Buffer[it] }.f64Buffer
        }

    override val elementAlgebra: Float64Field get() = Float64Field

    @OptIn(UnsafeKMathAPI::class)
    override fun mutableStructureND(shape: ShapeND, initializer: Float64Field.(IntArray) -> Double): ViktorStructureND =
        F64Array(*shape.asArray()).apply {
            ColumnStrides(shape).asSequence().forEach { index ->
                set(value = Float64Field.initializer(index), indices = index)
            }
        }.asStructure()

    override fun StructureND<Double>.unaryMinus(): StructureND<Double> = -1 * this

    @OptIn(UnsafeKMathAPI::class)
    @PerformancePitfall
    override fun StructureND<Double>.map(transform: Float64Field.(Double) -> Double): ViktorStructureND =
        F64Array(*shape.asArray()).apply {
            ColumnStrides(ShapeND(shape)).asSequence().forEach { index ->
                set(value = Float64Field.transform(this@map[index]), indices = index)
            }
        }.asStructure()

    @OptIn(UnsafeKMathAPI::class)
    @PerformancePitfall
    override fun StructureND<Double>.mapIndexed(
        transform: Float64Field.(index: IntArray, Double) -> Double,
    ): ViktorStructureND = F64Array(*shape.asArray()).apply {
        ColumnStrides(ShapeND(shape)).asSequence().forEach { index ->
            set(value = Float64Field.transform(index, this@mapIndexed[index]), indices = index)
        }
    }.asStructure()

    @OptIn(UnsafeKMathAPI::class)
    @PerformancePitfall
    override fun zip(
        left: StructureND<Double>,
        right: StructureND<Double>,
        transform: Float64Field.(Double, Double) -> Double,
    ): ViktorStructureND {
        require(left.shape.contentEquals(right.shape))
        return F64Array(*left.shape.asArray()).apply {
            ColumnStrides(left.shape).asSequence().forEach { index ->
                set(value = Float64Field.transform(left[index], right[index]), indices = index)
            }
        }.asStructure()
    }

    override fun add(left: StructureND<Double>, right: StructureND<Double>): ViktorStructureND =
        (left.f64Buffer + right.f64Buffer).asStructure()

    override fun scale(a: StructureND<Double>, value: Double): ViktorStructureND =
        (a.f64Buffer * value).asStructure()

    override fun StructureND<Double>.plus(arg: StructureND<Double>): ViktorStructureND =
        (f64Buffer + arg.f64Buffer).asStructure()

    override fun StructureND<Double>.minus(arg: StructureND<Double>): ViktorStructureND =
        (f64Buffer - arg.f64Buffer).asStructure()

    override fun StructureND<Double>.times(k: Number): ViktorStructureND =
        (f64Buffer * k.toDouble()).asStructure()

    override fun StructureND<Double>.plus(arg: Double): ViktorStructureND =
        (f64Buffer.plus(arg)).asStructure()

    override fun sin(arg: StructureND<Double>): ViktorStructureND = arg.map { sin(it) }
    override fun cos(arg: StructureND<Double>): ViktorStructureND = arg.map { cos(it) }
    override fun tan(arg: StructureND<Double>): ViktorStructureND = arg.map { tan(it) }
    override fun asin(arg: StructureND<Double>): ViktorStructureND = arg.map { asin(it) }
    override fun acos(arg: StructureND<Double>): ViktorStructureND = arg.map { acos(it) }
    override fun atan(arg: StructureND<Double>): ViktorStructureND = arg.map { atan(it) }

    override fun power(arg: StructureND<Double>, pow: Number): ViktorStructureND = arg.map { it.pow(pow) }

    override fun exp(arg: StructureND<Double>): ViktorStructureND = arg.f64Buffer.exp().asStructure()

    override fun ln(arg: StructureND<Double>): ViktorStructureND = arg.f64Buffer.log().asStructure()

    override fun sinh(arg: StructureND<Double>): ViktorStructureND = arg.map { sinh(it) }

    override fun cosh(arg: StructureND<Double>): ViktorStructureND = arg.map { cosh(it) }

    override fun asinh(arg: StructureND<Double>): ViktorStructureND = arg.map { asinh(it) }

    override fun acosh(arg: StructureND<Double>): ViktorStructureND = arg.map { acosh(it) }

    override fun atanh(arg: StructureND<Double>): ViktorStructureND = arg.map { atanh(it) }

    public companion object : ViktorFieldOpsND()
}

public val Float64Field.viktorAlgebra: ViktorFieldOpsND get() = ViktorFieldOpsND

@OptIn(UnstableKMathAPI::class)
public open class ViktorFieldND(
    private val shapeAsArray: IntArray,
) : ViktorFieldOpsND(), FieldND<Double, Float64Field>, NumbersAddOps<StructureND<Double>> {

    override val shape: ShapeND = ShapeND(shapeAsArray)


    override val zero: ViktorStructureND by lazy { F64Array.full(init = 0.0, shape = shapeAsArray).asStructure() }
    override val one: ViktorStructureND by lazy { F64Array.full(init = 1.0, shape = shapeAsArray).asStructure() }

    override fun number(value: Number): ViktorStructureND =
        F64Array.full(init = value.toDouble(), shape = shapeAsArray).asStructure()
}

public fun Float64Field.viktorAlgebra(vararg shape: Int): ViktorFieldND = ViktorFieldND(shape)

public fun ViktorFieldND(vararg shape: Int): ViktorFieldND = ViktorFieldND(shape)