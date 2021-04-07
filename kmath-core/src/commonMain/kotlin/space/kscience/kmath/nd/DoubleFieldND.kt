package space.kscience.kmath.nd

import space.kscience.kmath.misc.UnstableKMathAPI
import space.kscience.kmath.operations.DoubleField
import space.kscience.kmath.operations.ExtendedField
import space.kscience.kmath.operations.NumbersAddOperations
import space.kscience.kmath.operations.ScaleOperations
import space.kscience.kmath.structures.DoubleBuffer
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@OptIn(UnstableKMathAPI::class)
public class DoubleFieldND(
    shape: IntArray,
) : BufferedFieldND<Double, DoubleField>(shape, DoubleField, ::DoubleBuffer),
    NumbersAddOperations<StructureND<Double>>,
    ScaleOperations<StructureND<Double>>,
    ExtendedField<StructureND<Double>> {

    public override val zero: BufferND<Double> by lazy { produce { zero } }
    public override val one: BufferND<Double> by lazy { produce { one } }

    public override fun number(value: Number): BufferND<Double> {
        val d = value.toDouble() // minimize conversions
        return produce { d }
    }

    public override val StructureND<Double>.buffer: DoubleBuffer
        get() = when {
            !shape.contentEquals(this@DoubleFieldND.shape) -> throw ShapeMismatchException(
                this@DoubleFieldND.shape,
                shape
            )
            this is BufferND && this.strides == this@DoubleFieldND.strides -> this.buffer as DoubleBuffer
            else -> DoubleBuffer(strides.linearSize) { offset -> get(strides.index(offset)) }
        }

    @Suppress("OVERRIDE_BY_INLINE")
    public override inline fun StructureND<Double>.map(
        transform: DoubleField.(Double) -> Double,
    ): BufferND<Double> {
        val buffer = DoubleBuffer(strides.linearSize) { offset -> DoubleField.transform(buffer.array[offset]) }
        return BufferND(strides, buffer)
    }

    @Suppress("OVERRIDE_BY_INLINE")
    public override inline fun produce(initializer: DoubleField.(IntArray) -> Double): BufferND<Double> {
        val array = DoubleArray(strides.linearSize) { offset ->
            val index = strides.index(offset)
            DoubleField.initializer(index)
        }
        return BufferND(strides, DoubleBuffer(array))
    }

    @Suppress("OVERRIDE_BY_INLINE")
    public override inline fun StructureND<Double>.mapIndexed(
        transform: DoubleField.(index: IntArray, Double) -> Double,
    ): BufferND<Double> = BufferND(
        strides,
        buffer = DoubleBuffer(strides.linearSize) { offset ->
            DoubleField.transform(
                strides.index(offset),
                buffer.array[offset]
            )
        })

    @Suppress("OVERRIDE_BY_INLINE")
    public override inline fun combine(
        a: StructureND<Double>,
        b: StructureND<Double>,
        transform: DoubleField.(Double, Double) -> Double,
    ): BufferND<Double> {
        val buffer = DoubleBuffer(strides.linearSize) { offset ->
            DoubleField.transform(a.buffer.array[offset], b.buffer.array[offset])
        }
        return BufferND(strides, buffer)
    }

    public override fun scale(a: StructureND<Double>, value: Double): StructureND<Double> = a.map { it * value }

    public override fun power(arg: StructureND<Double>, pow: Number): BufferND<Double> = arg.map { power(it, pow) }

    public override fun exp(arg: StructureND<Double>): BufferND<Double> = arg.map { exp(it) }
    public override fun ln(arg: StructureND<Double>): BufferND<Double> = arg.map { ln(it) }

    public override fun sin(arg: StructureND<Double>): BufferND<Double> = arg.map { sin(it) }
    public override fun cos(arg: StructureND<Double>): BufferND<Double> = arg.map { cos(it) }
    public override fun tan(arg: StructureND<Double>): BufferND<Double> = arg.map { tan(it) }
    public override fun asin(arg: StructureND<Double>): BufferND<Double> = arg.map { asin(it) }
    public override fun acos(arg: StructureND<Double>): BufferND<Double> = arg.map { acos(it) }
    public override fun atan(arg: StructureND<Double>): BufferND<Double> = arg.map { atan(it) }

    public override fun sinh(arg: StructureND<Double>): BufferND<Double> = arg.map { sinh(it) }
    public override fun cosh(arg: StructureND<Double>): BufferND<Double> = arg.map { cosh(it) }
    public override fun tanh(arg: StructureND<Double>): BufferND<Double> = arg.map { tanh(it) }
    public override fun asinh(arg: StructureND<Double>): BufferND<Double> = arg.map { asinh(it) }
    public override fun acosh(arg: StructureND<Double>): BufferND<Double> = arg.map { acosh(it) }
    public override fun atanh(arg: StructureND<Double>): BufferND<Double> = arg.map { atanh(it) }
}

public fun AlgebraND.Companion.real(vararg shape: Int): DoubleFieldND = DoubleFieldND(shape)

/**
 * Produce a context for n-dimensional operations inside this real field
 */
public inline fun <R> DoubleField.nd(vararg shape: Int, action: DoubleFieldND.() -> R): R {
    contract { callsInPlace(action, InvocationKind.EXACTLY_ONCE) }
    return DoubleFieldND(shape).run(action)
}
