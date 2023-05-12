/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.nd

import space.kscience.kmath.PerformancePitfall
import space.kscience.kmath.UnstableKMathAPI
import space.kscience.kmath.operations.*
import space.kscience.kmath.structures.DoubleBuffer
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.math.pow
import kotlin.math.pow as kpow

/**
 * A simple mutable [StructureND] of doubles
 */
public class DoubleBufferND(
    indexes: ShapeIndexer,
    override val buffer: DoubleBuffer,
) : MutableBufferND<Double>(indexes, buffer), MutableStructureNDOfDouble{
    override fun getDouble(index: IntArray): Double = buffer[indices.offset(index)]

    override fun setDouble(index: IntArray, value: Double) {
        buffer[indices.offset(index)] = value
    }
}


public sealed class DoubleFieldOpsND : BufferedFieldOpsND<Double, DoubleField>(DoubleField.bufferAlgebra),
    ScaleOperations<StructureND<Double>>, ExtendedFieldOps<StructureND<Double>> {

    @OptIn(PerformancePitfall::class)
    override fun StructureND<Double>.toBufferND(): DoubleBufferND = when (this) {
        is DoubleBufferND -> this
        else -> {
            val indexer = indexerBuilder(shape)
            DoubleBufferND(indexer, DoubleBuffer(indexer.linearSize) { offset -> get(indexer.index(offset)) })
        }
    }

    protected inline fun mapInline(
        arg: DoubleBufferND,
        transform: (Double) -> Double,
    ): DoubleBufferND {
        val indexes = arg.indices
        val array = arg.buffer.array
        return DoubleBufferND(indexes, DoubleBuffer(indexes.linearSize) { transform(array[it]) })
    }

    private inline fun zipInline(
        l: DoubleBufferND,
        r: DoubleBufferND,
        block: (l: Double, r: Double) -> Double,
    ): DoubleBufferND {
        require(l.indices == r.indices) { "Zip requires the same shapes, but found ${l.shape} on the left and ${r.shape} on the right" }
        val indexes = l.indices
        val lArray = l.buffer.array
        val rArray = r.buffer.array
        return DoubleBufferND(indexes, DoubleBuffer(indexes.linearSize) { block(lArray[it], rArray[it]) })
    }

    @OptIn(PerformancePitfall::class)
    override fun StructureND<Double>.map(transform: DoubleField.(Double) -> Double): BufferND<Double> =
        mapInline(toBufferND()) { DoubleField.transform(it) }


    @OptIn(PerformancePitfall::class)
    override fun zip(
        left: StructureND<Double>,
        right: StructureND<Double>,
        transform: DoubleField.(Double, Double) -> Double,
    ): BufferND<Double> = zipInline(left.toBufferND(), right.toBufferND()) { l, r -> DoubleField.transform(l, r) }

    override fun structureND(shape: ShapeND, initializer: DoubleField.(IntArray) -> Double): DoubleBufferND {
        val indexer = indexerBuilder(shape)
        return DoubleBufferND(
            indexer,
            DoubleBuffer(indexer.linearSize) { offset ->
                elementAlgebra.initializer(indexer.index(offset))
            }
        )
    }

    override fun add(left: StructureND<Double>, right: StructureND<Double>): DoubleBufferND =
        zipInline(left.toBufferND(), right.toBufferND()) { l, r -> l + r }

    override fun multiply(left: StructureND<Double>, right: StructureND<Double>): DoubleBufferND =
        zipInline(left.toBufferND(), right.toBufferND()) { l, r -> l * r }

    override fun StructureND<Double>.unaryMinus(): DoubleBufferND = mapInline(toBufferND()) { -it }

    override fun StructureND<Double>.div(arg: StructureND<Double>): DoubleBufferND =
        zipInline(toBufferND(), arg.toBufferND()) { l, r -> l / r }

    override fun divide(left: StructureND<Double>, right: StructureND<Double>): DoubleBufferND =
        zipInline(left.toBufferND(), right.toBufferND()) { l: Double, r: Double -> l / r }

    override fun StructureND<Double>.div(arg: Double): DoubleBufferND =
        mapInline(toBufferND()) { it / arg }

    override fun Double.div(arg: StructureND<Double>): DoubleBufferND =
        mapInline(arg.toBufferND()) { this / it }

    override fun StructureND<Double>.unaryPlus(): DoubleBufferND = toBufferND()

    override fun StructureND<Double>.plus(arg: StructureND<Double>): DoubleBufferND =
        zipInline(toBufferND(), arg.toBufferND()) { l: Double, r: Double -> l + r }

    override fun StructureND<Double>.minus(arg: StructureND<Double>): DoubleBufferND =
        zipInline(toBufferND(), arg.toBufferND()) { l: Double, r: Double -> l - r }

    override fun StructureND<Double>.times(arg: StructureND<Double>): DoubleBufferND =
        zipInline(toBufferND(), arg.toBufferND()) { l: Double, r: Double -> l * r }

    override fun StructureND<Double>.times(k: Number): DoubleBufferND =
        mapInline(toBufferND()) { it * k.toDouble() }

    override fun StructureND<Double>.div(k: Number): DoubleBufferND =
        mapInline(toBufferND()) { it / k.toDouble() }

    override fun Number.times(arg: StructureND<Double>): DoubleBufferND = arg * this

    override fun StructureND<Double>.plus(arg: Double): DoubleBufferND = mapInline(toBufferND()) { it + arg }

    override fun StructureND<Double>.minus(arg: Double): StructureND<Double> = mapInline(toBufferND()) { it - arg }

    override fun Double.plus(arg: StructureND<Double>): StructureND<Double> = arg + this

    override fun Double.minus(arg: StructureND<Double>): StructureND<Double> = mapInline(arg.toBufferND()) { this - it }

    override fun scale(a: StructureND<Double>, value: Double): DoubleBufferND =
        mapInline(a.toBufferND()) { it * value }

    override fun exp(arg: StructureND<Double>): DoubleBufferND =
        mapInline(arg.toBufferND()) { kotlin.math.exp(it) }

    override fun ln(arg: StructureND<Double>): DoubleBufferND =
        mapInline(arg.toBufferND()) { kotlin.math.ln(it) }

    override fun sin(arg: StructureND<Double>): DoubleBufferND =
        mapInline(arg.toBufferND()) { kotlin.math.sin(it) }

    override fun cos(arg: StructureND<Double>): DoubleBufferND =
        mapInline(arg.toBufferND()) { kotlin.math.cos(it) }

    override fun tan(arg: StructureND<Double>): DoubleBufferND =
        mapInline(arg.toBufferND()) { kotlin.math.tan(it) }

    override fun asin(arg: StructureND<Double>): DoubleBufferND =
        mapInline(arg.toBufferND()) { kotlin.math.asin(it) }

    override fun acos(arg: StructureND<Double>): DoubleBufferND =
        mapInline(arg.toBufferND()) { kotlin.math.acos(it) }

    override fun atan(arg: StructureND<Double>): DoubleBufferND =
        mapInline(arg.toBufferND()) { kotlin.math.atan(it) }

    override fun sinh(arg: StructureND<Double>): DoubleBufferND =
        mapInline(arg.toBufferND()) { kotlin.math.sinh(it) }

    override fun cosh(arg: StructureND<Double>): DoubleBufferND =
        mapInline(arg.toBufferND()) { kotlin.math.cosh(it) }

    override fun tanh(arg: StructureND<Double>): DoubleBufferND =
        mapInline(arg.toBufferND()) { kotlin.math.tanh(it) }

    override fun asinh(arg: StructureND<Double>): DoubleBufferND =
        mapInline(arg.toBufferND()) { kotlin.math.asinh(it) }

    override fun acosh(arg: StructureND<Double>): DoubleBufferND =
        mapInline(arg.toBufferND()) { kotlin.math.acosh(it) }

    override fun atanh(arg: StructureND<Double>): DoubleBufferND =
        mapInline(arg.toBufferND()) { kotlin.math.atanh(it) }

    override fun power(
        arg: StructureND<Double>,
        pow: Number,
    ): StructureND<Double> = if (pow is Int) {
        mapInline(arg.toBufferND()) { it.pow(pow) }
    } else {
        mapInline(arg.toBufferND()) { it.pow(pow.toDouble()) }
    }

    public companion object : DoubleFieldOpsND()
}

@OptIn(UnstableKMathAPI::class)
public class DoubleFieldND(override val shape: ShapeND) :
    DoubleFieldOpsND(), FieldND<Double, DoubleField>, NumbersAddOps<StructureND<Double>>,
    ExtendedField<StructureND<Double>> {

    override fun power(arg: StructureND<Double>, pow: UInt): DoubleBufferND = mapInline(arg.toBufferND()) {
        it.kpow(pow.toInt())
    }

    override fun power(arg: StructureND<Double>, pow: Int): DoubleBufferND = mapInline(arg.toBufferND()) {
        it.kpow(pow)
    }

    override fun power(arg: StructureND<Double>, pow: Number): DoubleBufferND = if (pow.isInteger()) {
        power(arg, pow.toInt())
    } else {
        val dpow = pow.toDouble()
        mapInline(arg.toBufferND()) {
            if (it < 0) throw IllegalArgumentException("Can't raise negative $it to a fractional power")
            else it.kpow(dpow)
        }
    }

    override fun sinh(arg: StructureND<Double>): DoubleBufferND = super<DoubleFieldOpsND>.sinh(arg)

    override fun cosh(arg: StructureND<Double>): DoubleBufferND = super<DoubleFieldOpsND>.cosh(arg)

    override fun tanh(arg: StructureND<Double>): DoubleBufferND = super<DoubleFieldOpsND>.tan(arg)

    override fun asinh(arg: StructureND<Double>): DoubleBufferND = super<DoubleFieldOpsND>.asinh(arg)

    override fun acosh(arg: StructureND<Double>): DoubleBufferND = super<DoubleFieldOpsND>.acosh(arg)

    override fun atanh(arg: StructureND<Double>): DoubleBufferND = super<DoubleFieldOpsND>.atanh(arg)

    override fun number(value: Number): DoubleBufferND {
        val d = value.toDouble() // minimize conversions
        return structureND(shape) { d }
    }
}

public val DoubleField.ndAlgebra: DoubleFieldOpsND get() = DoubleFieldOpsND

public fun DoubleField.ndAlgebra(vararg shape: Int): DoubleFieldND = DoubleFieldND(ShapeND(shape))
public fun DoubleField.ndAlgebra(shape: ShapeND): DoubleFieldND = DoubleFieldND(shape)

/**
 * Produce a context for n-dimensional operations inside this real field
 */
@UnstableKMathAPI
public inline fun <R> DoubleField.withNdAlgebra(vararg shape: Int, action: DoubleFieldND.() -> R): R {
    contract { callsInPlace(action, InvocationKind.EXACTLY_ONCE) }
    return DoubleFieldND(ShapeND(shape)).run(action)
}
