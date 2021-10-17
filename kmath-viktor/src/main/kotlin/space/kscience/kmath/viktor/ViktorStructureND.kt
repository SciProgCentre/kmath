/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package space.kscience.kmath.viktor

import org.jetbrains.bio.viktor.F64Array
import space.kscience.kmath.misc.PerformancePitfall
import space.kscience.kmath.misc.UnstableKMathAPI
import space.kscience.kmath.nd.*
import space.kscience.kmath.operations.DoubleField
import space.kscience.kmath.operations.ExtendedField
import space.kscience.kmath.operations.NumbersAddOps
import space.kscience.kmath.operations.ScaleOperations

@Suppress("OVERRIDE_BY_INLINE", "NOTHING_TO_INLINE")
public class ViktorStructureND(public val f64Buffer: F64Array) : MutableStructureND<Double> {
    override val shape: IntArray get() = f64Buffer.shape

    override inline fun get(index: IntArray): Double = f64Buffer.get(*index)

    override inline fun set(index: IntArray, value: Double) {
        f64Buffer.set(*index, value = value)
    }

    @PerformancePitfall
    override fun elements(): Sequence<Pair<IntArray, Double>> =
        DefaultStrides(shape).indices().map { it to get(it) }
}

public fun F64Array.asStructure(): ViktorStructureND = ViktorStructureND(this)

@OptIn(UnstableKMathAPI::class)
@Suppress("OVERRIDE_BY_INLINE", "NOTHING_TO_INLINE")
public class ViktorFieldND(override val shape: IntArray) : FieldND<Double, DoubleField>,
    NumbersAddOps<StructureND<Double>>, ExtendedField<StructureND<Double>>,
    ScaleOperations<StructureND<Double>> {

    public val StructureND<Double>.f64Buffer: F64Array
        get() = when {
            !shape.contentEquals(this@ViktorFieldND.shape) -> throw ShapeMismatchException(
                this@ViktorFieldND.shape,
                shape
            )
            this is ViktorStructureND && this.f64Buffer.shape.contentEquals(this@ViktorFieldND.shape) -> this.f64Buffer
            else -> produce(shape) { this@f64Buffer[it] }.f64Buffer
        }

    override val zero: ViktorStructureND by lazy { F64Array.full(init = 0.0, shape = shape).asStructure() }
    override val one: ViktorStructureND by lazy { F64Array.full(init = 1.0, shape = shape).asStructure() }

    private val strides: Strides = DefaultStrides(shape)

    override val elementAlgebra: DoubleField get() = DoubleField

    override fun produce(shape: IntArray, initializer: DoubleField.(IntArray) -> Double): ViktorStructureND =
        F64Array(*shape).apply {
            this@ViktorFieldND.strides.indices().forEach { index ->
                set(value = DoubleField.initializer(index), indices = index)
            }
        }.asStructure()

    override fun StructureND<Double>.unaryMinus(): StructureND<Double> = -1 * this

    override fun StructureND<Double>.map(transform: DoubleField.(Double) -> Double): ViktorStructureND =
        F64Array(*this@ViktorFieldND.shape).apply {
            this@ViktorFieldND.strides.indices().forEach { index ->
                set(value = DoubleField.transform(this@map[index]), indices = index)
            }
        }.asStructure()

    override fun StructureND<Double>.mapIndexed(
        transform: DoubleField.(index: IntArray, Double) -> Double,
    ): ViktorStructureND = F64Array(*this@ViktorFieldND.shape).apply {
        this@ViktorFieldND.strides.indices().forEach { index ->
            set(value = DoubleField.transform(index, this@mapIndexed[index]), indices = index)
        }
    }.asStructure()

    override fun zip(
        left: StructureND<Double>,
        right: StructureND<Double>,
        transform: DoubleField.(Double, Double) -> Double,
    ): ViktorStructureND = F64Array(*shape).apply {
        this@ViktorFieldND.strides.indices().forEach { index ->
            set(value = DoubleField.transform(left[index], right[index]), indices = index)
        }
    }.asStructure()

    override fun add(left: StructureND<Double>, right: StructureND<Double>): ViktorStructureND =
        (left.f64Buffer + right.f64Buffer).asStructure()

    override fun scale(a: StructureND<Double>, value: Double): ViktorStructureND =
        (a.f64Buffer * value).asStructure()

    override inline fun StructureND<Double>.plus(other: StructureND<Double>): ViktorStructureND =
        (f64Buffer + other.f64Buffer).asStructure()

    override inline fun StructureND<Double>.minus(other: StructureND<Double>): ViktorStructureND =
        (f64Buffer - other.f64Buffer).asStructure()

    override inline fun StructureND<Double>.times(k: Number): ViktorStructureND =
        (f64Buffer * k.toDouble()).asStructure()

    override inline fun StructureND<Double>.plus(arg: Double): ViktorStructureND =
        (f64Buffer.plus(arg)).asStructure()

    override fun number(value: Number): ViktorStructureND =
        F64Array.full(init = value.toDouble(), shape = shape).asStructure()

    override fun sin(arg: StructureND<Double>): ViktorStructureND = arg.map { sin(it) }
    override fun cos(arg: StructureND<Double>): ViktorStructureND = arg.map { cos(it) }
    override fun tan(arg: StructureND<Double>): ViktorStructureND = arg.map { tan(it) }
    override fun asin(arg: StructureND<Double>): ViktorStructureND = arg.map { asin(it) }
    override fun acos(arg: StructureND<Double>): ViktorStructureND = arg.map { acos(it) }
    override fun atan(arg: StructureND<Double>): ViktorStructureND = arg.map { atan(it) }

    override fun power(arg: StructureND<Double>, pow: Number): ViktorStructureND = arg.map { it.pow(pow) }

    override fun exp(arg: StructureND<Double>): ViktorStructureND = arg.f64Buffer.exp().asStructure()

    override fun ln(arg: StructureND<Double>): ViktorStructureND = arg.f64Buffer.log().asStructure()
}

public fun ViktorFieldND(vararg shape: Int): ViktorFieldND = ViktorFieldND(shape)
