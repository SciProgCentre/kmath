/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package space.kscience.kmath.nd

import space.kscience.kmath.misc.UnstableKMathAPI
import space.kscience.kmath.operations.*
import space.kscience.kmath.structures.DoubleBuffer
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

public sealed class DoubleFieldOpsND : BufferedFieldOpsND<Double, DoubleField>(DoubleField.bufferAlgebra),
    ScaleOperations<StructureND<Double>>, ExtendedFieldOps<StructureND<Double>> {

    override fun StructureND<Double>.toBufferND(): BufferND<Double> = when (this) {
        is BufferND -> this
        else -> {
            val indexer = indexerBuilder(shape)
            BufferND(indexer, DoubleBuffer(indexer.linearSize) { offset -> get(indexer.index(offset)) })
        }
    }

    //TODO do specialization

    override fun scale(a: StructureND<Double>, value: Double): BufferND<Double> =
        mapInline(a.toBufferND()) { it * value }

    override fun power(arg: StructureND<Double>, pow: Number): BufferND<Double> =
        mapInline(arg.toBufferND()) { power(it, pow) }

    override fun exp(arg: StructureND<Double>): BufferND<Double> = mapInline(arg.toBufferND()) { exp(it) }
    override fun ln(arg: StructureND<Double>): BufferND<Double> = mapInline(arg.toBufferND()) { ln(it) }

    override fun sin(arg: StructureND<Double>): BufferND<Double> = mapInline(arg.toBufferND()) { sin(it) }
    override fun cos(arg: StructureND<Double>): BufferND<Double> = mapInline(arg.toBufferND()) { cos(it) }
    override fun tan(arg: StructureND<Double>): BufferND<Double> = mapInline(arg.toBufferND()) { tan(it) }
    override fun asin(arg: StructureND<Double>): BufferND<Double> = mapInline(arg.toBufferND()) { asin(it) }
    override fun acos(arg: StructureND<Double>): BufferND<Double> = mapInline(arg.toBufferND()) { acos(it) }
    override fun atan(arg: StructureND<Double>): BufferND<Double> = mapInline(arg.toBufferND()) { atan(it) }

    override fun sinh(arg: StructureND<Double>): BufferND<Double> = mapInline(arg.toBufferND()) { sinh(it) }
    override fun cosh(arg: StructureND<Double>): BufferND<Double> = mapInline(arg.toBufferND()) { cosh(it) }
    override fun tanh(arg: StructureND<Double>): BufferND<Double> = mapInline(arg.toBufferND()) { tanh(it) }
    override fun asinh(arg: StructureND<Double>): BufferND<Double> = mapInline(arg.toBufferND()) { asinh(it) }
    override fun acosh(arg: StructureND<Double>): BufferND<Double> = mapInline(arg.toBufferND()) { acosh(it) }
    override fun atanh(arg: StructureND<Double>): BufferND<Double> = mapInline(arg.toBufferND()) { atanh(it) }

    public companion object : DoubleFieldOpsND()
}

@OptIn(UnstableKMathAPI::class)
public class DoubleFieldND(override val shape: Shape) :
    DoubleFieldOpsND(), FieldND<Double, DoubleField>, NumbersAddOps<StructureND<Double>> {

    override fun number(value: Number): BufferND<Double> {
        val d = value.toDouble() // minimize conversions
        return produce(shape) { d }
    }
}

public val DoubleField.ndAlgebra: DoubleFieldOpsND get() = DoubleFieldOpsND

public fun DoubleField.ndAlgebra(vararg shape: Int): DoubleFieldND = DoubleFieldND(shape)

/**
 * Produce a context for n-dimensional operations inside this real field
 */
@UnstableKMathAPI
public inline fun <R> DoubleField.withNdAlgebra(vararg shape: Int, action: DoubleFieldND.() -> R): R {
    contract { callsInPlace(action, InvocationKind.EXACTLY_ONCE) }
    return DoubleFieldND(shape).run(action)
}
