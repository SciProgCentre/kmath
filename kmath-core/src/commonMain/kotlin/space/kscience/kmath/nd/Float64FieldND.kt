/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.nd

import space.kscience.kmath.PerformancePitfall
import space.kscience.kmath.UnstableKMathAPI
import space.kscience.kmath.operations.*
import space.kscience.kmath.structures.Float64Buffer
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.math.pow
import kotlin.math.pow as kpow

/**
 * A simple mutable [StructureND] of doubles
 */
public class Float64BufferND(
    indexes: ShapeIndexer,
    override val buffer: Float64Buffer,
) : MutableBufferND<Double>(indexes, buffer), MutableStructureNDOfDouble{
    override fun getDouble(index: IntArray): Double = buffer[indices.offset(index)]

    override fun setDouble(index: IntArray, value: Double) {
        buffer[indices.offset(index)] = value
    }
}


public sealed class Floa64FieldOpsND : BufferedFieldOpsND<Double, Float64Field>(Float64Field.bufferAlgebra),
    ScaleOperations<StructureND<Double>>, ExtendedFieldOps<StructureND<Double>> {

    @OptIn(PerformancePitfall::class)
    override fun StructureND<Double>.toBufferND(): Float64BufferND = when (this) {
        is Float64BufferND -> this
        else -> {
            val indexer = indexerBuilder(shape)
            Float64BufferND(indexer, Float64Buffer(indexer.linearSize) { offset -> get(indexer.index(offset)) })
        }
    }

    protected inline fun mapInline(
        arg: Float64BufferND,
        transform: (Double) -> Double,
    ): Float64BufferND {
        val indexes = arg.indices
        val array = arg.buffer.array
        return Float64BufferND(indexes, Float64Buffer(indexes.linearSize) { transform(array[it]) })
    }

    private inline fun zipInline(
        l: Float64BufferND,
        r: Float64BufferND,
        block: (l: Double, r: Double) -> Double,
    ): Float64BufferND {
        require(l.indices == r.indices) { "Zip requires the same shapes, but found ${l.shape} on the left and ${r.shape} on the right" }
        val indexes = l.indices
        val lArray = l.buffer.array
        val rArray = r.buffer.array
        return Float64BufferND(indexes, Float64Buffer(indexes.linearSize) { block(lArray[it], rArray[it]) })
    }

    @OptIn(PerformancePitfall::class)
    override fun StructureND<Double>.map(transform: Float64Field.(Double) -> Double): BufferND<Double> =
        mapInline(toBufferND()) { Float64Field.transform(it) }


    @OptIn(PerformancePitfall::class)
    override fun zip(
        left: StructureND<Double>,
        right: StructureND<Double>,
        transform: Float64Field.(Double, Double) -> Double,
    ): BufferND<Double> = zipInline(left.toBufferND(), right.toBufferND()) { l, r -> Float64Field.transform(l, r) }

    override fun structureND(shape: ShapeND, initializer: Float64Field.(IntArray) -> Double): Float64BufferND {
        val indexer = indexerBuilder(shape)
        return Float64BufferND(
            indexer,
            Float64Buffer(indexer.linearSize) { offset ->
                elementAlgebra.initializer(indexer.index(offset))
            }
        )
    }

    override fun add(left: StructureND<Double>, right: StructureND<Double>): Float64BufferND =
        zipInline(left.toBufferND(), right.toBufferND()) { l, r -> l + r }

    override fun multiply(left: StructureND<Double>, right: StructureND<Double>): Float64BufferND =
        zipInline(left.toBufferND(), right.toBufferND()) { l, r -> l * r }

    override fun StructureND<Double>.unaryMinus(): Float64BufferND = mapInline(toBufferND()) { -it }

    override fun StructureND<Double>.div(arg: StructureND<Double>): Float64BufferND =
        zipInline(toBufferND(), arg.toBufferND()) { l, r -> l / r }

    override fun divide(left: StructureND<Double>, right: StructureND<Double>): Float64BufferND =
        zipInline(left.toBufferND(), right.toBufferND()) { l: Double, r: Double -> l / r }

    override fun StructureND<Double>.div(arg: Double): Float64BufferND =
        mapInline(toBufferND()) { it / arg }

    override fun Double.div(arg: StructureND<Double>): Float64BufferND =
        mapInline(arg.toBufferND()) { this / it }

    override fun StructureND<Double>.unaryPlus(): Float64BufferND = toBufferND()

    override fun StructureND<Double>.plus(arg: StructureND<Double>): Float64BufferND =
        zipInline(toBufferND(), arg.toBufferND()) { l: Double, r: Double -> l + r }

    override fun StructureND<Double>.minus(arg: StructureND<Double>): Float64BufferND =
        zipInline(toBufferND(), arg.toBufferND()) { l: Double, r: Double -> l - r }

    override fun StructureND<Double>.times(arg: StructureND<Double>): Float64BufferND =
        zipInline(toBufferND(), arg.toBufferND()) { l: Double, r: Double -> l * r }

    override fun StructureND<Double>.times(k: Number): Float64BufferND =
        mapInline(toBufferND()) { it * k.toDouble() }

    override fun StructureND<Double>.div(k: Number): Float64BufferND =
        mapInline(toBufferND()) { it / k.toDouble() }

    override fun Number.times(arg: StructureND<Double>): Float64BufferND = arg * this

    override fun StructureND<Double>.plus(arg: Double): Float64BufferND = mapInline(toBufferND()) { it + arg }

    override fun StructureND<Double>.minus(arg: Double): StructureND<Double> = mapInline(toBufferND()) { it - arg }

    override fun Double.plus(arg: StructureND<Double>): StructureND<Double> = arg + this

    override fun Double.minus(arg: StructureND<Double>): StructureND<Double> = mapInline(arg.toBufferND()) { this - it }

    override fun scale(a: StructureND<Double>, value: Double): Float64BufferND =
        mapInline(a.toBufferND()) { it * value }

    override fun exp(arg: StructureND<Double>): Float64BufferND =
        mapInline(arg.toBufferND()) { kotlin.math.exp(it) }

    override fun ln(arg: StructureND<Double>): Float64BufferND =
        mapInline(arg.toBufferND()) { kotlin.math.ln(it) }

    override fun sin(arg: StructureND<Double>): Float64BufferND =
        mapInline(arg.toBufferND()) { kotlin.math.sin(it) }

    override fun cos(arg: StructureND<Double>): Float64BufferND =
        mapInline(arg.toBufferND()) { kotlin.math.cos(it) }

    override fun tan(arg: StructureND<Double>): Float64BufferND =
        mapInline(arg.toBufferND()) { kotlin.math.tan(it) }

    override fun asin(arg: StructureND<Double>): Float64BufferND =
        mapInline(arg.toBufferND()) { kotlin.math.asin(it) }

    override fun acos(arg: StructureND<Double>): Float64BufferND =
        mapInline(arg.toBufferND()) { kotlin.math.acos(it) }

    override fun atan(arg: StructureND<Double>): Float64BufferND =
        mapInline(arg.toBufferND()) { kotlin.math.atan(it) }

    override fun sinh(arg: StructureND<Double>): Float64BufferND =
        mapInline(arg.toBufferND()) { kotlin.math.sinh(it) }

    override fun cosh(arg: StructureND<Double>): Float64BufferND =
        mapInline(arg.toBufferND()) { kotlin.math.cosh(it) }

    override fun tanh(arg: StructureND<Double>): Float64BufferND =
        mapInline(arg.toBufferND()) { kotlin.math.tanh(it) }

    override fun asinh(arg: StructureND<Double>): Float64BufferND =
        mapInline(arg.toBufferND()) { kotlin.math.asinh(it) }

    override fun acosh(arg: StructureND<Double>): Float64BufferND =
        mapInline(arg.toBufferND()) { kotlin.math.acosh(it) }

    override fun atanh(arg: StructureND<Double>): Float64BufferND =
        mapInline(arg.toBufferND()) { kotlin.math.atanh(it) }

    override fun power(
        arg: StructureND<Double>,
        pow: Number,
    ): StructureND<Double> = if (pow is Int) {
        mapInline(arg.toBufferND()) { it.pow(pow) }
    } else {
        mapInline(arg.toBufferND()) { it.pow(pow.toDouble()) }
    }

    public companion object : Floa64FieldOpsND()
}

@OptIn(UnstableKMathAPI::class)
public class Float64FieldND(override val shape: ShapeND) :
    Floa64FieldOpsND(), FieldND<Double, Float64Field>, NumbersAddOps<StructureND<Double>>,
    ExtendedField<StructureND<Double>> {

    override fun power(arg: StructureND<Double>, pow: UInt): Float64BufferND = mapInline(arg.toBufferND()) {
        it.kpow(pow.toInt())
    }

    override fun power(arg: StructureND<Double>, pow: Int): Float64BufferND = mapInline(arg.toBufferND()) {
        it.kpow(pow)
    }

    override fun power(arg: StructureND<Double>, pow: Number): Float64BufferND = if (pow.isInteger()) {
        power(arg, pow.toInt())
    } else {
        val dpow = pow.toDouble()
        mapInline(arg.toBufferND()) {
            if (it < 0) throw IllegalArgumentException("Can't raise negative $it to a fractional power")
            else it.kpow(dpow)
        }
    }

    override fun sinh(arg: StructureND<Double>): Float64BufferND = super<Floa64FieldOpsND>.sinh(arg)

    override fun cosh(arg: StructureND<Double>): Float64BufferND = super<Floa64FieldOpsND>.cosh(arg)

    override fun tanh(arg: StructureND<Double>): Float64BufferND = super<Floa64FieldOpsND>.tan(arg)

    override fun asinh(arg: StructureND<Double>): Float64BufferND = super<Floa64FieldOpsND>.asinh(arg)

    override fun acosh(arg: StructureND<Double>): Float64BufferND = super<Floa64FieldOpsND>.acosh(arg)

    override fun atanh(arg: StructureND<Double>): Float64BufferND = super<Floa64FieldOpsND>.atanh(arg)

    override fun number(value: Number): Float64BufferND {
        val d = value.toDouble() // minimize conversions
        return structureND(shape) { d }
    }
}

public val Float64Field.ndAlgebra: Floa64FieldOpsND get() = Floa64FieldOpsND

public fun Float64Field.ndAlgebra(vararg shape: Int): Float64FieldND = Float64FieldND(ShapeND(shape))
public fun Float64Field.ndAlgebra(shape: ShapeND): Float64FieldND = Float64FieldND(shape)

/**
 * Produce a context for n-dimensional operations inside this real field
 */
@UnstableKMathAPI
public inline fun <R> Float64Field.withNdAlgebra(vararg shape: Int, action: Float64FieldND.() -> R): R {
    contract { callsInPlace(action, InvocationKind.EXACTLY_ONCE) }
    return Float64FieldND(ShapeND(shape)).run(action)
}
